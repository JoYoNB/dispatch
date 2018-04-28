package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.Role;

public interface RoleDao {

	public List<String>getRoleIdList(Dept dept);
	
	public int addRole(Role role);
	public int deleteRole(Role role);
	public int updateRole(Role role);
	public Role getRole(Role role);
	public List<Role>getRoleList(Map<String,Object>param);
	public int getRoleListCount(Map<String,Object>param);
	
	public int addRoleAuthRef(Map<String,Object>param);
	public int deleteAllRoleAuthRef(Role role);
	public List<Map<String,Object>>getUserRoleAuthList(Role role);
	public List<Map<String,Object>>getAdminUserRoleAuthList(Role role);
	
}
