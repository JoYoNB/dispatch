package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.Dept;

public interface DeptDao {

	public List<String>getDeptIdList(Dept dept);
	public int addDept(Dept dept);
	public int updateDept(Dept dept);
	public int setDeptDNA(Dept dept);
	public int deleteDept(Dept dept);
	public Dept getDept(Dept dept);
	public List<Dept>getDeptList(Map<String,Object>param);
	public int getDeptListCount(Map<String,Object>param);
	
	public Dept checkDeptDataAuth(Map<String,Object>param);
	public int checkRoleDataAuth(Map<String,Object>param);
	public int checkUserDataAuth(Map<String,Object>param);
	
	public int getRoleCountRefDept(Dept dept);
	public int getUserCountRefDept(Dept dept);
	public int getVehicleCountRefDept(Dept dept);
	public int getDriverCountRefDept(Dept dept);
}
