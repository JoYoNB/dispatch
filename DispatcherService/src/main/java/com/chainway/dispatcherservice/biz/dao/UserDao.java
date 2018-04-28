package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.Role;
import com.chainway.dispatchercore.dto.User;

public interface UserDao {

	public int addUser(User user);
	public int deleteUser(User user);
	public int updateUser(User user);
	public List<User>getUserList(Map<String,Object>param);
	public int getUserListCount(Map<String,Object>param);
	public User getUser(User user);
	public Role getRole(Role role);
	public Role getAdminRole(Role role);
	/**
	 * 查询部门管辖下的所有用户
	 * @param dept
	 * @return
	 */
	public List<String>getUserIdList(Dept dept);
	/**
	 * 个人信息-APP
	 * @param user
	 * @return
	 */
	public Map<String, Object>myInfo(User user);
}
