package com.chainway.dispatcherservice.service;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.Role;
import com.chainway.dispatchercore.dto.Tree;
import com.chainway.dispatchercore.dto.User;

public interface UserService {

	public int addUser(User user)throws Exception;
	public int deleteUser(User user)throws Exception;
	public int updateUser(User user)throws Exception;
	public List<User>getUserList(Map<String,Object>param);
	public int getUserListCount(Map<String,Object>param);
	
	public User getUser(User user);
	/**
	 * 查询用户部门管辖所有部门Id
	 * @param dept
	 * @return
	 */
	public List<String>getDeptIdList(Dept dept);
	/**
	 * 查询部门管辖下的所有角色Id
	 * @param dept
	 * @return
	 */
	public List<String>getRoleIdList(Dept dept);
	/**
	 * 查询部门管辖下所有用户Id
	 * @param dept
	 * @return
	 */
	public List<String>getUserIdList(Dept dept);
	
	public Dept checkDeptDataAuth(Integer deptId,String userDeptDNA);
	public boolean checkRoleDataAuth(Integer roleId,String userDeptDNA);
	public boolean checkUserDataAuth(Integer userId,String userDeptDNA);
	public boolean checkVehicleDataAuth(Integer vehicleId,String userDeptDNA);
	public boolean checkDriverDataAuth(Integer driverId,String userDeptDNA);
	
	public int addDept(Dept dept)throws Exception;
	public int updateDept(Dept dept)throws Exception;
	public int deleteDept(Dept dept)throws Exception;
	public Dept getDept(Dept dept);
	public List<Dept>getDeptList(Map<String,Object>param);
	public int getDeptListCount(Map<String,Object>param);
	
	public int addRole(Role role)throws Exception;
	public int deleteRole(Role role)throws Exception;
	public int updateRole(Role role)throws Exception;
	public Role getRole(Role role);
	public List<Role>getRoleList(Map<String,Object>param);
	public int getRoleListCount(Map<String,Object>param);
	public Tree getUserRoleAuthTree(Role role);
}
