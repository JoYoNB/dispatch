package com.chainway.dispatcherservice.biz.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.DemoDao;
import com.chainway.dispatcherservice.biz.dao.UserDao;
import com.chainway.dispatcherservice.biz.service.DemoService;

@Service
//@Transactional(rollbackFor=Exception.class)
@Transactional
public class DemoServiceImpl implements DemoService {

	@Autowired
	private DemoDao demoDao;
	
	@Autowired
	private UserDao userDao;
	
	@Override
	@WriteDataSource
	public Map<String, Object> test(Map<String, Object> param) {
		Map<String,Object>ret=demoDao.test(param);
		return ret;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void testTransactional() {
		try{
			User user=new User();
			user.setId(1);
			user.setUpdater(1);
			userDao.updateUser(user);
			
			int a=0;
			int b=1;
			int d=b/a;
		}catch(Exception e){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		
	}

	@Override
	public String printAuthSql(Integer roleId,Integer authId) {
		StringBuffer sb=new StringBuffer();
		sb.append("insert into t_role_auth_ref(role_id,auth_id,create_time,update_time)value("+roleId+","+authId+",NOW(),NOW());").append("\n");
		List<Map<String,Object>>children=demoDao.getAuthList(authId);
		if(children!=null){
			for(Map<String,Object>item:children){
				Integer id=(Integer) item.get("id");
				Long childrenCount=(Long) item.get("childrenCount");
				if(childrenCount!=null && childrenCount.intValue()>0){
					//递归获取
					sb.append(printAuthSql(roleId,id));
				}else{
					sb.append("insert into t_role_auth_ref(role_id,auth_id,create_time,update_time)value("+roleId+","+id+",NOW(),NOW());").append("\n");
				}
			}
		}
		
		return sb.toString();
	}

}
