package com.chainway.dispatcherservice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.common.IVCApiUtils;
import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.CustomerInfoDao;
import com.chainway.dispatcherservice.biz.dao.DeptDao;
import com.chainway.dispatcherservice.biz.dao.UserDao;
import com.chainway.dispatcherservice.dto.Area;
import com.chainway.dispatcherservice.dto.CustomerInfo;
import com.chainway.dispatcherservice.dto.GoodsType;
import com.chainway.dispatcherservice.service.CustomerInfoService;

import chainway.frame.util.StringUtil;

@Component  
@Service
public class CustomerInfoServiceImpl implements CustomerInfoService {


	@Autowired
	private CustomerInfoDao customerInfoDao;
	@Autowired
	private DeptDao deptDao;
	@Autowired
	private UserDao userDao;
	
	@Override
	@Transactional
	@WriteDataSource
	public void add(CustomerInfo customerInfo) throws ServiceException {
		/**********************验证重复性***************************/
		//同级部门，不能存在相同名称的部门
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("level", 1);
		param.put("name", customerInfo.getName());
		List<Dept>list1=deptDao.getDeptList(param);
		if(list1!=null&&!list1.isEmpty()){
			//同级存在同名部门
			throw new ServiceException(ExceptionCode.ERROR_SAME_PARENT_HAD_DEPT,"同级部门存在同名部门");
		}
		//校验手机号
		param.clear();
		param.put("phone", customerInfo.getPhone());
		List<Dept>list2=deptDao.getDeptList(param);
		if(list2!=null&&!list2.isEmpty()){
			//同级存在同名部门
			throw new ServiceException(ExceptionCode.ERROR_DEPT_PHONE_HAD_EXIST,"部门联系人手机号已经存在");
		}
		//校验邮箱
		param.clear();
		param.put("email", customerInfo.getEmail());
		List<Dept>list3=deptDao.getDeptList(param);
		if(list3!=null&&!list3.isEmpty()){
			//同级存在同名部门
			throw new ServiceException(ExceptionCode.ERROR_DEPT_EMAIL_HAD_EXIST,"部门联系人邮箱已经存在");
		}
		param.put("account", customerInfo.getAccount());
		List<User>list4=userDao.getUserList(param);
		if(list4!=null&&!list4.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_USER_ACCOUNT_EXIST,"已经存在用户账号");
		}
		//手机号
		param.clear();
		param.put("phone", customerInfo.getPhone());
		List<User>list5=userDao.getUserList(param);
		if(list5!=null&&!list5.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_USER_PHONE_EXIST,"已经存在用户手机号");
		}
		//邮箱
		param.clear();
		param.put("email", customerInfo.getEmail());
		List<User>list6=userDao.getUserList(param);
		if(list6!=null&&!list6.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_USER_EMAIL_EXIST,"已经存在用户邮箱");
		}
		
		/**********************新建部门***************************/
		Dept dept = new Dept();
		dept.setContacter(customerInfo.getContacter());
		dept.setCreater(customerInfo.getCreater());
		dept.setEmail(customerInfo.getEmail());
		dept.setLevel(1);
		dept.setName(customerInfo.getName());
		dept.setPhone(customerInfo.getPhone());
		dept.setRole(customerInfo.getRole());
		dept.setStatus(1);
		dept.setUpdater(customerInfo.getUpdater());
		
		//新建2.0部门
		Integer thirdPartyDept=IVCApiUtils.getInstance().addDept(dept);
		if(thirdPartyDept==null){
			throw new ServiceException(ExceptionCode.ERROR_ADD_THIRD_PARTY_DEPT_FAIL,"新建2.0部门失败");
		}
		dept.setThirdPartyDept(thirdPartyDept);
		
		//开始新增部门
		int d=deptDao.addDept(dept);
		Integer deptId=null;
		if(d>0){
			deptId=dept.getId();
			if(deptId!=null){
				String dna=genDNA("", deptId);
				
				Dept updateDept=new Dept();
				updateDept.setId(deptId);
				updateDept.setDna(dna);
				deptDao.setDeptDNA(updateDept);
			}
		}
		
		/**********************新建用户***************************/
		User user=new User();
		user.setPassword("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");//123456
		user.setAccount(customerInfo.getAccount());
		user.setCreater(customerInfo.getCreater());
		user.setName(customerInfo.getName()+"-admin");
		user.setDeptId(deptId);
		user.setEmail(customerInfo.getEmail());
		user.setPhone(customerInfo.getPhone());
		user.setRoleId(customerInfo.getRole());
		user.setUpdater(customerInfo.getUpdater());
		user.setGmtZone(customerInfo.getGmtZone());
		userDao.addUser(user);
		
		/**********************新建客户***************************/
		//判断认证状态，注册资金，社会信用代码，营业执照都不为空时，设为已认证
		int authStatus=0;
		if(customerInfo.getBusinessLicence()!=null&&!"".equals(customerInfo.getBusinessLicence())&&customerInfo.getRegisteredCapital()!=null&&customerInfo.getScc()!=null&&!"".equals(customerInfo.getScc())) {
			authStatus=1;
		}
		customerInfo.setAuthStatus(authStatus);
		customerInfo.setId(deptId);
		customerInfo.setUserId(user.getId());
		customerInfoDao.addCustomerInfo(customerInfo);
		List<GoodsType> goodsTypes = customerInfo.getGoodsTypes();
		if(goodsTypes!=null&&goodsTypes.size()>0) {
			customerInfoDao.addCustomerGoodsType(customerInfo);
		}
		List<Area> areas = customerInfo.getAreas();
		if(areas!=null&&areas.size()>0) {
			customerInfoDao.addCustomerArea(customerInfo);
		}
	}
	
	@Override
	@Transactional
	@WriteDataSource
	public void update(CustomerInfo customerInfo) throws ServiceException {
		/**********************验证重复性***************************/
		//同级部门，不能存在相同名称的部门
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("level", 1);
		param.put("name", customerInfo.getName());
		List<Dept>list1=deptDao.getDeptList(param);
		if(list1!=null&&!list1.isEmpty()){
			Dept d=list1.get(0);
			if(d.getId().intValue()!=customerInfo.getId().intValue()){
				//同级存在同名部门
				throw new ServiceException(ExceptionCode.ERROR_SAME_PARENT_HAD_DEPT,"同级部门存在同名部门");
			}
		}
		//校验部门联系人手机号
		param.clear();
		param.put("phone", customerInfo.getPhone());
		List<Dept>list2=deptDao.getDeptList(param);
		if(list2!=null&&!list2.isEmpty()){
			Dept d=list2.get(0);
			if(d.getId().intValue()!=customerInfo.getId().intValue()){
				//同级存在同名部门
				throw new ServiceException(ExceptionCode.ERROR_DEPT_PHONE_HAD_EXIST,"部门联系人手机号已经存在");
			}
		}
		//校验部门联系人邮箱
		param.clear();
		param.put("email", customerInfo.getEmail());
		List<Dept>list3=deptDao.getDeptList(param);
		if(list3!=null&&!list3.isEmpty()){
			Dept d=list3.get(0);
			if(d.getId().intValue()!=customerInfo.getId().intValue()){
				//同级存在同名部门
				throw new ServiceException(ExceptionCode.ERROR_DEPT_EMAIL_HAD_EXIST,"部门联系人邮箱已经存在");
			}
		}
		
		//用户手机号
		param.clear();
		param.put("phone", customerInfo.getPhone());
		List<User>list5=userDao.getUserList(param);
		if(list5!=null&&!list5.isEmpty()){
			User u=list5.get(0);
			if(u.getId().intValue()!=customerInfo.getUserId().intValue()){
				throw new ServiceException(ExceptionCode.ERROR_USER_PHONE_EXIST,"已经存在用户手机号");
			}
		}
		//用户邮箱
		param.clear();
		param.put("email", customerInfo.getEmail());
		List<User>list6=userDao.getUserList(param);
		if(list6!=null&&!list6.isEmpty()){
			User u=list6.get(0);
			if(u.getId().intValue()!=customerInfo.getUserId().intValue()){
				throw new ServiceException(ExceptionCode.ERROR_USER_EMAIL_EXIST,"已经存在用户邮箱");
			}
		}
		
		/**********************修改部门***************************/
		Dept dept = new Dept();
		dept.setId(customerInfo.getId());
		dept.setContacter(customerInfo.getContacter());
		dept.setEmail(customerInfo.getEmail());
		dept.setName(customerInfo.getName());
		dept.setPhone(customerInfo.getPhone());
		dept.setRole(customerInfo.getRole());
		dept.setUpdater(customerInfo.getUpdater());
		
		deptDao.updateDept(dept);
		
		/**********************修改用户***************************/
		User user=new User();
		user.setId(customerInfo.getUserId());
		user.setUpdater(customerInfo.getUpdater());
		String userName=customerInfo.getName();
		if(userName!=null&&!"".equals(userName)) {
			userName+="-admin";
		}
		user.setName(userName);
		user.setEmail(customerInfo.getEmail());
		user.setPhone(customerInfo.getPhone());
		user.setRoleId(customerInfo.getRole());
		user.setGmtZone(customerInfo.getGmtZone());
		userDao.updateUser(user);
		
		/**********************修改客户***************************/
		//判断认证状态，注册资金，社会信用代码，营业执照都不为空时，设为已认证
		CustomerInfo customerInfoDB=getInfo(customerInfo.getId());
		if(customerInfoDB==null)return;
		String businessLicencBD=customerInfoDB.getBusinessLicence();
		String businessLicenc=customerInfo.getBusinessLicence();
		int authStatus=0;
		if(customerInfo.getRegisteredCapital()!=null&&customerInfo.getScc()!=null&&!"".equals(customerInfo.getScc())) {
			if(businessLicenc!=null&&!"".equals(businessLicenc)) {
				authStatus=1;
			}else if(businessLicencBD!=null&&!"".equals(businessLicencBD)) {
				authStatus=1;
			}
		}
		customerInfo.setAuthStatus(authStatus);
		customerInfoDao.updateCustomerInfo(customerInfo);
		List<GoodsType> goodsTypes = customerInfo.getGoodsTypes();
		if(goodsTypes!=null&&goodsTypes.size()>0) {
			customerInfoDao.deleteCustomerGoodsType(customerInfo);
			customerInfoDao.addCustomerGoodsType(customerInfo);
		}
		List<Area> vehicleTypes = customerInfo.getAreas();
		if(vehicleTypes!=null&&vehicleTypes.size()>0) {
			customerInfoDao.deleteCustomerArea(customerInfo);
			customerInfoDao.addCustomerArea(customerInfo);
		}
	}

	@Override
	public List<CustomerInfo> getList(Map<String, Object> param) {
		return customerInfoDao.getList(param);
	}

	@Override
	public int getListCount(Map<String, Object> param) {
		return customerInfoDao.getListCount(param);
	}

	@Override
	public CustomerInfo getInfo(int userId) {
		return customerInfoDao.getInfo(userId);
	}
	
	@Override
	@Transactional
	@WriteDataSource
	public void delete(CustomerInfo customerInfo) throws ServiceException {
		User user=new User();
		user.setUpdater(customerInfo.getUpdater());
		Dept dept=new Dept();
		dept.setId(customerInfo.getId());
		dept.setUpdater(customerInfo.getUpdater());
		customerInfo=customerInfoDao.getInfo(customerInfo.getId());
		int userId=customerInfo.getUserId();
		user.setId(userId);
		
		//删用户
		userDao.deleteUser(user);
		
		//子部门
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("parentId", dept.getId());
		List<Dept>list1=deptDao.getDeptList(param);
		if(list1!=null&&!list1.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_DEPT_REF_SUB_DEPT,"还存在子部门");
		}
		//角色
		int c2=deptDao.getRoleCountRefDept(dept);
		if(c2>0){
			throw new ServiceException(ExceptionCode.ERROR_DEPT_REF_ROLE,"还存在关联角色");
		}
		//用户
		int c3=deptDao.getUserCountRefDept(dept);
		if(c3>0){
			throw new ServiceException(ExceptionCode.ERROR_DEPT_REF_USER,"还存在关联用户");
		}
		//车辆
		int c4=deptDao.getVehicleCountRefDept(dept);
		if(c4>0){
			throw new ServiceException(ExceptionCode.ERROR_DEPT_REF_VEHICLE,"还存在关联车辆");
		}
		//司机
		int c5=deptDao.getDriverCountRefDept(dept);
		if(c5>0){
			throw new ServiceException(ExceptionCode.ERROR_DEPT_REF_DRIVER,"还存在关联司机");
		}
		deptDao.deleteDept(dept);
	}
	/**
	 * 构造部门DNA
	 * @param parentDNA
	 * @param deptId
	 * @return
	 */
	private String genDNA(String parentDNA,Integer deptId){
		String dna="";
		if(StringUtils.isEmpty(parentDNA)){
			dna=deptId+"-";
		}else{
			dna=parentDNA+deptId+"-";
		}
		
		return dna;
	}
}
