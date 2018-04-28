package com.chainway.dispatcherservice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.Role;
import com.chainway.dispatchercore.dto.Tree;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.CustomerInfoDao;
import com.chainway.dispatcherservice.biz.dao.DeptDao;
import com.chainway.dispatcherservice.biz.dao.RoleDao;
import com.chainway.dispatcherservice.biz.dao.UserDao;
import com.chainway.dispatcherservice.biz.service.DemoService;
import com.chainway.dispatcherservice.service.UserService;

@Component  
@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private DeptDao deptDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private CustomerInfoDao customerInfoDao;
	
	@Override
	@WriteDataSource
	public int addUser(User user) throws Exception {
		//用户账号
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("account", user.getAccount());
		List<User>list1=userDao.getUserList(param);
		if(list1!=null&&!list1.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_USER_ACCOUNT_EXIST,"已经存在用户账号");
		}
		//手机号
		param.clear();
		param.put("phone", user.getPhone());
		List<User>list2=userDao.getUserList(param);
		if(list2!=null&&!list2.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_USER_PHONE_EXIST,"已经存在用户手机号");
		}
		//邮箱
		param.clear();
		param.put("email", user.getEmail());
		List<User>list3=userDao.getUserList(param);
		if(list3!=null&&!list3.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_USER_EMAIL_EXIST,"已经存在用户邮箱");
		}
		//设置用户默认密码
		user.setPassword("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");//123456
		int c=userDao.addUser(user);
		return c;
	}

	@Override
	@WriteDataSource
	public int deleteUser(User user) throws Exception{
		//不能删除管理员
		User userInDB=userDao.getUser(user);
		if(userInDB.getRoleCode()=="admin"){
			throw new ServiceException(ExceptionCode.ERROR_ADMIN_USER_CAN_NOT_DELETE,"管理员不能删除");
		}
		return userDao.deleteUser(user);
	}

	@Override
	@WriteDataSource
	@Transactional(propagation=Propagation.REQUIRED)
	public int updateUser(User user) throws Exception {
		//手机号
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("phone", user.getPhone());
		List<User>list2=userDao.getUserList(param);
		if(list2!=null&&!list2.isEmpty()){
			User u=list2.get(0);
			if(u.getId().intValue()!=user.getId().intValue()){
				throw new ServiceException(ExceptionCode.ERROR_USER_PHONE_EXIST,"已经存在用户手机号");
			}
		}
		//邮箱
		param.clear();
		param.put("email", user.getEmail());
		List<User>list3=userDao.getUserList(param);
		if(list3!=null&&!list3.isEmpty()){
			User u=list3.get(0);
			if(u.getId().intValue()!=user.getId().intValue()){
				throw new ServiceException(ExceptionCode.ERROR_USER_EMAIL_EXIST,"已经存在用户邮箱");
			}
		}
		if(StringUtils.isNotEmpty(user.getPassword())){
			//有修改密码
			//拿到原来的密码比较，不能和原来的密码一样
			User userInDB=userDao.getUser(user);
			if(userInDB.getPassword().equals(user.getPassword())){
				throw new ServiceException(ExceptionCode.ERROR_NEW_PW_SAME_WIDTH_OLD_PW,"密码不能和原来的密码一样");
			}
		}
		
		int c=userDao.updateUser(user);
		return c;
	}

	@Override
	public List<User> getUserList(Map<String, Object> param) {
		return userDao.getUserList(param);
	}

	@Override
	public int getUserListCount(Map<String, Object> param) {
		return userDao.getUserListCount(param);
	}
	
	@Override
	public User getUser(User user) {
		User userInDB=userDao.getUser(user);
		if(userInDB!=null){
			//加载角色信息
			Role role=new Role();
			role.setId(userInDB.getRoleId());
			role=userDao.getRole(role);
			userInDB.setRole(role);
		}
		
		return userInDB;
	}

	@Override
	public List<String> getDeptIdList(Dept dept) {
		return deptDao.getDeptIdList(dept);
	}

	@Override
	public List<String> getRoleIdList(Dept dept) {
		return roleDao.getRoleIdList(dept);
	}

	@Override
	public List<String> getUserIdList(Dept dept) {
		return userDao.getUserIdList(dept);
	}

	@Override
	public List<Dept> getDeptList(Map<String, Object> param) {
		return deptDao.getDeptList(param);
	}
	
	@Override
	public int getDeptListCount(Map<String, Object> param) {
		return deptDao.getDeptListCount(param);
	}

	private String genDNA(String parentDNA,Integer deptId){
		String dna="";
		if(StringUtils.isEmpty(parentDNA)){
			dna=deptId+"-";
		}else{
			dna=parentDNA+deptId+"-";
		}
		
		return dna;
	}
	
	@Override
	@WriteDataSource
	public int addDept(Dept dept)throws Exception {
		/*
		4.	校验数据唯一性（从主库的数据校验，防止数据库同步有差异）
		5.	如果勾选的是承运商,或者是承运商+货主,则调接口在2.0新增一个部门,并关联到成配的部门
		6.	如果承运商/货主的部门，必须是挂在第二级
		 * 
		 * */
		//先查询父级部门的DNA
		Dept parent=new Dept();
		parent.setId(dept.getParentId());
		parent=deptDao.getDept(parent);
		
		//设置部门级别
		dept.setLevel(parent.getLevel()+1);//父级的部门级别+1
		//设置部门类别（跟父级部门一样）
		dept.setRole(parent.getRole());
		
		//同级部门，不能存在相同名称的部门
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("parentId", dept.getParentId());
		param.put("name", dept.getName());
		List<Dept>list1=deptDao.getDeptList(param);
		if(list1!=null&&!list1.isEmpty()){
			//同级存在同名部门
			throw new ServiceException(ExceptionCode.ERROR_SAME_PARENT_HAD_DEPT,"同级部门存在同名部门");
		}
		//校验手机号
		param.clear();
		param.put("phone", dept.getPhone());
		List<Dept>list2=deptDao.getDeptList(param);
		if(list2!=null&&!list2.isEmpty()){
			//同级存在同名部门
			throw new ServiceException(ExceptionCode.ERROR_DEPT_PHONE_HAD_EXIST,"部门联系人手机号已经存在");
		}
		//校验邮箱
		param.clear();
		param.put("email", dept.getEmail());
		List<Dept>list3=deptDao.getDeptList(param);
		if(list3!=null&&!list3.isEmpty()){
			//同级存在同名部门
			throw new ServiceException(ExceptionCode.ERROR_DEPT_EMAIL_HAD_EXIST,"部门联系人邮箱已经存在");
		}
		/*//如果是新增承运商/货主部门，则调接口
		放到新增客户信息那里，创建客户信息时，同时创建一个部门和部门管理员
		if((dept.getIsCarrier()!=null&&dept.getIsCarrier())||
				(dept.getIsConsignor()!=null&&dept.getIsConsignor())||(dept.getIsDual()!=null&&dept.getIsDual())){
			dept.setParentId(1);//必须是挂在第二级
			if(dept.getIsCarrier()!=null&&dept.getIsCarrier()){
				dept.setRole(Dept.ROLE_CARRIER);
			}else if(dept.getIsConsignor()!=null&&dept.getIsConsignor()){
				dept.setRole(Dept.ROLE_CONSIGNOR);
			}else if(dept.getIsDual()!=null&&dept.getIsDual()){
				dept.setRole(Dept.ROLE_DUAL);
			}
			
			//调接口
			Integer deptId=IVCApiUtils.getInstance().addDept(dept);
			if(deptId==null){
				throw new ServiceException(ExceptionCode.ERROR_ADD_THIRD_PARTY_DEPT_FAIL,"新建2.0部门失败");
			}
			//返回2.0部门Id
			Integer thirdPartyDeptId=deptId;
			dept.setThirdPartyDept(thirdPartyDeptId);
		}else{
			//部门角色，1普通部门，2货主，3承运商，4承运商&货主
			dept.setRole(Dept.ROLE_SYSTEM);
		}*/
		
		//新增成配的部门
		int c=deptDao.addDept(dept);
		if(c>0){
			Integer id=dept.getId();
			if(id!=null){
				//更新部门的dna
				String parentDNA=null;
				if(parent!=null){
					parentDNA=parent.getDna();
				}
				String dna=genDNA(parentDNA, id);
				
				Dept updateDept=new Dept();
				updateDept.setId(id);
				updateDept.setDna(dna);
				deptDao.setDeptDNA(updateDept);
			}
		}
		
		return c;
	}

	@Override
	@WriteDataSource
	public int updateDept(Dept dept) throws Exception{
		//同级部门，不能存在相同名称的部门
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("parentId", dept.getParentId());
		param.put("name", dept.getName());
		List<Dept>list1=deptDao.getDeptList(param);
		if(list1!=null&&!list1.isEmpty()){
			Dept d=list1.get(0);
			if(d.getId().intValue()!=dept.getId().intValue()){
				//同级存在同名部门
				throw new ServiceException(ExceptionCode.ERROR_SAME_PARENT_HAD_DEPT,"同级部门存在同名部门");
			}
		}
		
		//校验手机号
		param.clear();
		param.put("phone", dept.getPhone());
		List<Dept>list2=deptDao.getDeptList(param);
		if(list2!=null&&!list2.isEmpty()){
			Dept d=list2.get(0);
			if(d.getId().intValue()!=dept.getId().intValue()){
				//同级存在同名部门
				throw new ServiceException(ExceptionCode.ERROR_DEPT_PHONE_HAD_EXIST,"部门联系人手机号已经存在");
			}
		}
				
		//校验邮箱
		param.clear();
		param.put("email", dept.getEmail());
		List<Dept>list3=deptDao.getDeptList(param);
		if(list3!=null&&!list3.isEmpty()){
			Dept d=list3.get(0);
			if(d.getId().intValue()!=dept.getId().intValue()){
				//同级存在同名部门
				throw new ServiceException(ExceptionCode.ERROR_DEPT_EMAIL_HAD_EXIST,"部门联系人邮箱已经存在");
			}
		}
		
		return deptDao.updateDept(dept);
	}

	@Override
	@WriteDataSource
	public int deleteDept(Dept dept) throws Exception{
		/*
		 * 3.	校验部门底下是否还有数据关联，如果还有数据关联，不能删除
		4.	如果是承运商/货主 是否考虑删除2.0的部门
		5.	判断是否还关联用户，子部门，司机
		 * 
		 * */
		
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
		return deptDao.deleteDept(dept);
	}

	@Override
	public Dept getDept(Dept dept) {
		return deptDao.getDept(dept);
	}

	@Override
	public Dept checkDeptDataAuth(Integer deptId, String userDeptDNA) {
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("deptId", deptId);
		param.put("userDeptDNA", userDeptDNA);
		Dept dept=deptDao.checkDeptDataAuth(param);
		return dept;
	}

	@Override
	public boolean checkRoleDataAuth(Integer roleId, String userDeptDNA) {
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("roleId", roleId);
		param.put("userDeptDNA", userDeptDNA);
		int c=deptDao.checkRoleDataAuth(param);
		if(c>0){
			return true;
		}
		return false;
	}

	@Override
	public boolean checkUserDataAuth(Integer userId, String userDeptDNA) {
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("userId", userId);
		param.put("userDeptDNA", userDeptDNA);
		int c=deptDao.checkUserDataAuth(param);
		if(c>0){
			return true;
		}
		return false;
	}

	@Override
	public boolean checkVehicleDataAuth(Integer vehicleId, String userDeptDNA) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkDriverDataAuth(Integer driverId, String userDeptDNA) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@WriteDataSource
	public int addRole(Role role) throws Exception {
		//相同部门下，角色名称唯一
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("name", role.getName());
		param.put("deptId", role.getDeptId());
		List<Role>list=roleDao.getRoleList(param);
		if(list!=null&&!list.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_DEPT_ROLE_NAME_EXIST,"同一部门下已经存在相同角色");
		}
		int c=roleDao.addRole(role);
		//加入角色权限
		Map<String,Object>para=new HashMap<String,Object>();
		para.put("roleId", role.getId());
		para.put("authList", role.getAuthList());
		roleDao.addRoleAuthRef(para);
		
		return c;
	}

	@Override
	@WriteDataSource
	public int deleteRole(Role role) throws Exception {
		/*
		 * 3.	系统用户只能删除系统角色（但是3种主角色不能删除），货主/承运商只能删除自定义角色
4.	查询角色是否还有用户关联，如果有用户关联，不能删除

		 * */
		return roleDao.deleteRole(role);
	}

	@Override
	@WriteDataSource
	public int updateRole(Role role) throws Exception {
		/*
		 * 3.	系统用户只能删除系统角色（但是3种主角色不能删除），货主/承运商只能删除自定义角色
		 * 
		 * */
		//相同部门下，角色名称唯一
		Role roleInDB=roleDao.getRole(role);
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("name", role.getName());
		param.put("deptId", roleInDB.getDeptId());
		List<Role>list=roleDao.getRoleList(param);
		if(list!=null&&!list.isEmpty()){
			Role r=list.get(0);
			if(r.getId().intValue()!=role.getId()){
				throw new ServiceException(ExceptionCode.ERROR_DEPT_ROLE_NAME_EXIST,"同一部门下已经存在相同角色");
			}
		}
		if(role.getAuthList()!=null&&!role.getAuthList().isEmpty()){
			//先删除所有的权限
			roleDao.deleteAllRoleAuthRef(role);
			//再插入新的
			Map<String,Object>para=new HashMap<String,Object>();
			para.put("roleId", role.getId());
			para.put("authList", role.getAuthList());
			roleDao.addRoleAuthRef(para);
		}
		
		return roleDao.updateRole(role);
	}

	@Override
	public Role getRole(Role role) {
		Role roleInDB=roleDao.getRole(role);
		//加载权限
		Role commonRole=userDao.getRole(role);
		commonRole.setDeptName(roleInDB.getDeptName());
		commonRole.setRemark(roleInDB.getRemark());
		return commonRole;
	}

	@Override
	public List<Role> getRoleList(Map<String, Object> param) {
		return roleDao.getRoleList(param);
	}

	@Override
	public int getRoleListCount(Map<String, Object> param) {
		return roleDao.getRoleListCount(param);
	}

	@Override
	public Tree getUserRoleAuthTree(Role role) {
		List<Map<String,Object>>list=null;
		list=roleDao.getUserRoleAuthList(role);
		if(list==null){
			return null;
		}
		Tree tree=new Tree();
		tree.apply(list);
		tree.grow();
		return tree;
	}

	
	

}
