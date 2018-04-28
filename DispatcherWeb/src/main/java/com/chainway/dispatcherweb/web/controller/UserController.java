package com.chainway.dispatcherweb.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.dto.Auth;
import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.Role;
import com.chainway.dispatchercore.dto.Tree;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;

@Controller
@RequestMapping("/user")
public class UserController {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private UserService userService;
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="name",checkMaxLen=true,maxLen=100),
            @ValidateFiled(index=0,notNull=true,filedName="contacter",checkMaxLen=true,maxLen=100),
            @ValidateFiled(index=0,notNull=true,filedName="phone",checkMaxLen=true,maxLen=20,checkRegex=true,regStr="phone"),
            @ValidateFiled(index=0,notNull=true,filedName="email",checkMaxLen=true,maxLen=100,checkRegex=true,regStr="email")
    })
	@Log
	@RequestMapping(value="/addDept.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult addDept(Dept dept,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//判断父级部门权限
		if(dept.getParentId()!=null){
			//根据部门Id和用户dna去查询数据，判断是否越权
			Dept d=userService.checkDeptDataAuth(dept.getParentId(), user.getDeptDNA());
			if(d==null){
				throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
			}
		}else{
			//上级部门每次传，则默认是自己的部门
			dept.setParentId(user.getDeptId());
		}
		
		dept.setCreater(user.getId());
		dept.setUpdater(user.getId());
		userService.addDept(dept);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id")
    })
	@Log
	@RequestMapping(value="/deleteDept.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult deleteDept(Dept dept,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//判断部门权限
		Dept d=userService.checkDeptDataAuth(dept.getId(), user.getDeptDNA());
		if(d==null){
			throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
		}
		dept.setUpdater(user.getId());
		userService.deleteDept(dept);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id[]")
    })
	@Log
	@RequestMapping(value="/deleteDeptList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult deleteDeptList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		String[]ids=request.getParameterValues("id[]");
		for(String id:ids){
			Dept dept=new Dept();
			dept.setId(Integer.parseInt(id));
			//判断部门权限
			Dept d=userService.checkDeptDataAuth(dept.getId(), user.getDeptDNA());
			if(d==null){
				throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
			}
			dept.setUpdater(user.getId());
			userService.deleteDept(dept);
		}
		
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id"),
            @ValidateFiled(index=0,filedName="name",checkMaxLen=true,maxLen=100),
            @ValidateFiled(index=0,filedName="contacter",checkMaxLen=true,maxLen=100),
            @ValidateFiled(index=0,filedName="phone",checkMaxLen=true,maxLen=20,checkRegex=true,regStr="phone"),
            @ValidateFiled(index=0,filedName="email",checkMaxLen=true,maxLen=100,checkRegex=true,regStr="email")
    })
	@Log
	@RequestMapping(value="/updateDept.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult updateDept(Dept dept,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//判断部门权限
		Dept d=userService.checkDeptDataAuth(dept.getId(), user.getDeptDNA());
		if(d==null){
			throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
		}
		dept.setUpdater(user.getId());
		userService.updateDept(dept);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id")
    })
	@RequestMapping(value="/getDept.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getDept(Dept dept,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		
		//判断部门权限
		Dept d=userService.checkDeptDataAuth(dept.getId(), user.getDeptDNA());
		if(d==null){
			throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
		}
		Dept deptInDB=userService.getDept(dept);
		result.setData(deptInDB);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@RequestMapping(value="/getDeptList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getDeptList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		
		Map<String,Object>param=new HashMap<String,Object>();
		String name=request.getParameter("name");
		param.put("likeName", name);
		String contacter=request.getParameter("contacter");
		param.put("contacter", contacter);
		String phone=request.getParameter("phone");
		param.put("phone", phone);
		String email=request.getParameter("email");
		param.put("email", email);
		String levelStr=request.getParameter("level");
		if(StringUtils.isNotEmpty(levelStr)){
			Integer level=Integer.parseInt(levelStr);
			param.put("level", level);
		}
		
		//页码
		int pageSize=CommonUtils.getPageSize(request);
		int offset=CommonUtils.getOffset(request);
		param.put("pageSize", pageSize);
		param.put("offset", offset);
		
		String deptIdStr=request.getParameter("deptId");
		if(StringUtils.isNotEmpty(deptIdStr)){
			Integer deptId=Integer.parseInt(deptIdStr);
			//根据部门Id和用户dna去查询数据，判断是否越权
			Dept d=userService.checkDeptDataAuth(deptId, user.getDeptDNA());
			if(d==null){
				throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
			}
			param.put("dna", d.getDna());
		}else{
			//没有传部门ID过来，则默认是用户本身部门
			param.put("dna", user.getDeptDNA());
		}
		
		//管理员以及管理创建的子后台用户，不能部门管理哪里看到承运商和货主的部门，他们只能在客户信息那里看到一级的承运商部门(承运商的子部门看不到)
		/*if(!user.isCarrier()&&!user.isConsignor()&&!user.isDual()){
			
		}*/
		
		List<Dept>list=userService.getDeptList(param);
		int total=userService.getDeptListCount(param);
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", total);
		String userDeptDNA=user.getDeptDNA();
		int userDeptLevel=1;
		if(StringUtils.isNotEmpty(userDeptDNA)){
			String[]items=userDeptDNA.split("-");
			if(items!=null&&items.length>0){
				userDeptLevel=items.length;
			}
		}
		ret.put("userDeptLevel", userDeptLevel);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	//**********************角色管理	开始*******************************
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="deptId")
    })
	@RequestMapping(value="/getRoleListForSelector.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getRoleListForSelector(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		Map<String,Object>param=new HashMap<String,Object>();
		//模糊角色名称
		String name=request.getParameter("name");
		param.put("likeName", name);
		//加上部门DNA防止越权
		String deptIdStr=request.getParameter("deptId");
		Integer deptId=Integer.parseInt(deptIdStr);
		//根据部门Id和用户dna去查询数据，判断是否越权
		Dept d=userService.checkDeptDataAuth(deptId, user.getDeptDNA());
		if(d==null){
			throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
		}
		param.put("deptDNA", d.getDna());
		//过滤掉system级别的角色
		param.put("filterSystem", 1);
		
		List<Role>list=userService.getRoleList(param);
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", list.size());
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	@RequestMapping(value="/getRoleList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getRoleList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		Map<String,Object>param=new HashMap<String,Object>();
		/*if(user.isCarrier()||user.isConsignor()||user.isDual()){
			//只能查询 custom-自定义 类型的角色
			param.put("type", Role.TYPE_CUSTOM);
		}else{
			//其他的系统用户只能查询 system-系统角色,common-普通角色
			param.put("filterCustom", true);
		}*/
		//过滤掉system级别的角色
		param.put("filterSystem", 1);
		
		//页码
		int pageSize=CommonUtils.getPageSize(request);
		int offset=CommonUtils.getOffset(request);
		param.put("pageSize", pageSize);
		param.put("offset", offset);
		//模糊角色名称
		String name=request.getParameter("name");
		param.put("likeName", name);
		//加上部门DNA防止越权
		String deptIdStr=request.getParameter("deptId");
		if(StringUtils.isNotEmpty(deptIdStr)){
			Integer deptId=Integer.parseInt(deptIdStr);
			//根据部门Id和用户dna去查询数据，判断是否越权
			Dept d=userService.checkDeptDataAuth(deptId, user.getDeptDNA());
			if(d==null){
				throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
			}
			param.put("deptDNA", d.getDna());
		}else{
			//没有传部门ID过来，则默认是用户本身部门
			param.put("deptDNA", user.getDeptDNA());
		}
		List<Role>list=userService.getRoleList(param);
		int total=userService.getRoleListCount(param);
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", total);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id")
    })
	@RequestMapping(value="/getRole.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getRole(Role role,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//校验角色权限
		boolean ok=userService.checkRoleDataAuth(role.getId(), user.getDeptDNA());
		if(!ok){
			throw new ServiceException(ExceptionCode.ERROR_NO_ROLE_AUTH,"没有该角色的操作权限");
		}
		Role roleInDB=userService.getRole(role);
		result.setData(roleInDB);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="name",checkMaxLen=true,maxLen=100),
            @ValidateFiled(index=0,filedName="remark",checkMaxLen=true,maxLen=500)
    })
	@Log
	@RequestMapping(value="/addRole.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult addRole(@RequestBody Role role,HttpServletRequest request, HttpServletResponse response) throws Exception{
		JsonResult result=new JsonResult();
		User user=localService.getUserInSession(request);
		/*
		 * 
		 * 6.	系统用户只能创建系统角色，货主/承运商只能创建自定义角色
7.	写入数据
8.	角色状态默认是有效，角色编码根据角色Id自动生成（保证角色编码唯一）

		 * */
		if(user.isCarrier()||user.isConsignor()||user.isDual()){
			//承运商/货主只能新建 custom-自定义 类型的角色
			role.setType(Role.TYPE_CUSTOM);
			if(user.isCarrier()){
				role.setCode("carrier");
			}else if(user.isConsignor()){
				role.setCode("consignor");
			}else{
				role.setCode("dual");
			}
		}else{
			//其他的系统用户只能新建 common-普通角色
			role.setType(Role.TYPE_COMMON);
			role.setCode("common");
		}
		//部门Id
		if(role.getDeptId()!=null){
			//判断越权
			//根据部门Id和用户dna去查询数据，判断是否越权
			Dept d=userService.checkDeptDataAuth(role.getDeptId(), user.getDeptDNA());
			if(d==null){
				throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
			}
		}else{
			//默认是用户本身部门Id
			role.setDeptId(user.getDeptId());
		}
		//校验角色权限
		List<Auth>authList=role.getAuthList();
		if(authList==null||authList.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_ROLE_AUTH_REQUIRED,"角色权限不能为空");
		}
		//校验角色权限有没有是超出用户本身的角色之外的
		List<String>auths=new ArrayList<String>();
		for(Auth a:user.getRole().getAuthList()){
			auths.add(a.getCode());
		}
		for(Auth aa:authList){
			if(!auths.contains(aa.getCode())){
				throw new ServiceException(ExceptionCode.ERROR_ROLE_AUTH_ERROR,"角色权限错误");
			}
		}
		
		role.setCreater(user.getId());
		role.setUpdater(user.getId());
		userService.addRole(role);
		
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id"),
            @ValidateFiled(index=0,filedName="name",checkMaxLen=true,maxLen=100),
            @ValidateFiled(index=0,filedName="remark",checkMaxLen=true,maxLen=500)
    })
	@Log
	@RequestMapping(value="/updateRole.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult updateRole(@RequestBody Role role,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//校验角色权限
		boolean ok=userService.checkRoleDataAuth(role.getId(), user.getDeptDNA());
		if(!ok){
			throw new ServiceException(ExceptionCode.ERROR_NO_ROLE_AUTH,"没有该角色的操作权限");
		}
		//部门Id
		if(role.getDeptId()!=null){
			//判断越权
			//根据部门Id和用户dna去查询数据，判断是否越权
			Dept d=userService.checkDeptDataAuth(role.getDeptId(), user.getDeptDNA());
			if(d==null){
				throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
			}
		}
		
		role.setUpdater(user.getId());
		userService.updateRole(role);
		
		//校验角色权限
		List<Auth>authList=role.getAuthList();
		if(authList!=null&&!authList.isEmpty()){
			//校验角色权限有没有是超出用户本身的角色之外的
			List<String>auths=new ArrayList<String>();
			for(Auth a:user.getRole().getAuthList()){
				auths.add(a.getCode());
			}
			for(Auth aa:authList){
				if(!auths.contains(aa.getCode())){
					throw new ServiceException(ExceptionCode.ERROR_ROLE_AUTH_ERROR,"角色权限错误");
				}
			}
		}
		
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id")
    })
	@Log
	@RequestMapping(value="/deleteRole.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult deleteRole(Role role,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//校验角色权限
		boolean ok=userService.checkRoleDataAuth(role.getId(), user.getDeptDNA());
		if(!ok){
			throw new ServiceException(ExceptionCode.ERROR_NO_ROLE_AUTH,"没有该角色的操作权限");
		}
		role.setUpdater(user.getId());
		userService.deleteRole(role);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id[]")
    })
	@Log
	@RequestMapping(value="/deleteRoleList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult deleteRoleList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		String[]ids=request.getParameterValues("id[]");
		for(String id:ids){
			Role role=new Role();
			role.setId(Integer.parseInt(id));
			boolean ok=userService.checkRoleDataAuth(role.getId(), user.getDeptDNA());
			if(!ok){
				throw new ServiceException(ExceptionCode.ERROR_NO_ROLE_AUTH,"没有该角色的操作权限");
			}
			role.setUpdater(user.getId());
			userService.deleteRole(role);
		}
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@RequestMapping(value="/getUserRoleAuthTree.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getUserRoleAuthTree(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		Role role=user.getRole();
		Tree authTree=userService.getUserRoleAuthTree(role);
		result.setData(authTree);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	//**********************角色管理	结束*******************************
	
	//**********************用户管理	结束*******************************
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="account",checkMaxLen=true,maxLen=50),
            @ValidateFiled(index=0,notNull=true,filedName="name",checkMaxLen=true,maxLen=100),
            @ValidateFiled(index=0,notNull=true,filedName="deptId"),
            @ValidateFiled(index=0,notNull=true,filedName="roleId"),
            @ValidateFiled(index=0,notNull=true,filedName="email",checkMaxLen=true,maxLen=100,checkRegex=true,regStr="email"),
            @ValidateFiled(index=0,notNull=true,filedName="phone",checkMaxLen=true,maxLen=20,checkRegex=true,regStr="phone"),
            @ValidateFiled(index=0,notNull=true,filedName="gmtZone",checkMaxLen=true,maxLen=20)
    })
	@Log
	@RequestMapping(value="/addUser.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult addUser(User u,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//校验部门
		Dept d=userService.checkDeptDataAuth(u.getDeptId(), user.getDeptDNA());
		if(d==null){
			throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
		}
		//校验角色
		boolean ok=userService.checkRoleDataAuth(u.getRoleId(), user.getDeptDNA());
		if(!ok){
			throw new ServiceException(ExceptionCode.ERROR_NO_ROLE_AUTH,"没有该角色的操作权限");
		}
		u.setCreater(user.getId());
		u.setUpdater(user.getId());
		
		userService.addUser(u);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id")
    })
	@Log
	@RequestMapping(value="/deleteUser.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult deleteUser(User u,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		u.setUpdater(user.getId());
		//校验用户有效性
		boolean ok=userService.checkUserDataAuth(u.getId(), user.getDeptDNA());
		if(!ok){
			throw new ServiceException(ExceptionCode.ERROR_NO_USER_AUTH,"没有该用户的操作权限");
		}
		userService.deleteUser(u);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id[]")
    })
	@Log
	@RequestMapping(value="/deleteUserList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult deleteUserList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		String[]ids=request.getParameterValues("id[]");
		for(String id:ids){
			User u=new User();
			u.setId(Integer.parseInt(id));
			boolean ok=userService.checkUserDataAuth(u.getId(), user.getDeptDNA());
			if(!ok){
				throw new ServiceException(ExceptionCode.ERROR_NO_USER_AUTH,"没有该用户的操作权限");
			}
			u.setUpdater(user.getId());
			userService.deleteUser(u);
		}
		
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="id"),
            @ValidateFiled(index=0,filedName="account",checkMaxLen=true,maxLen=50),
            @ValidateFiled(index=0,filedName="name",checkMaxLen=true,maxLen=100),
            @ValidateFiled(index=0,filedName="deptId"),
            @ValidateFiled(index=0,filedName="roleId"),
            @ValidateFiled(index=0,filedName="email",checkMaxLen=true,maxLen=100,checkRegex=true,regStr="email"),
            @ValidateFiled(index=0,filedName="phone",checkMaxLen=true,maxLen=20,checkRegex=true,regStr="phone"),
            @ValidateFiled(index=0,filedName="gmtZone",checkMaxLen=true,maxLen=20)
    })
	@Log
	@RequestMapping(value="/updateUser.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult updateUser(User u,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//校验用户
		boolean ok=userService.checkUserDataAuth(u.getId(), user.getDeptDNA());
		if(!ok){
			throw new ServiceException(ExceptionCode.ERROR_NO_USER_AUTH,"没有该用户的操作权限");
		}
		//校验部门
		if(u.getDeptId()!=null){
			Dept d=userService.checkDeptDataAuth(u.getDeptId(), user.getDeptDNA());
			if(d==null){
				throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
			}
		}
		//校验角色
		if(u.getRoleId()!=null){
			boolean ok1=userService.checkRoleDataAuth(u.getRoleId(), user.getDeptDNA());
			if(!ok1){
				throw new ServiceException(ExceptionCode.ERROR_NO_ROLE_AUTH,"没有该角色的操作权限");
			}
		}
		u.setUpdater(user.getId());
		if(u.getStatus()!=null){
			//保证状态位正确
			if(u.getStatus().intValue()!=0&&u.getStatus().intValue()!=1){
				u.setStatus(null);
			}
		}
		
		userService.updateUser(u);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="id")
    })
	@Log
	@RequestMapping(value="/disabledUser.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult disabledUser(User u,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//校验用户
		boolean ok=userService.checkUserDataAuth(u.getId(), user.getDeptDNA());
		if(!ok){
			throw new ServiceException(ExceptionCode.ERROR_NO_USER_AUTH,"没有该用户的操作权限");
		}
		User userInDB=userService.getUser(u);
		if(userInDB.getStatus().intValue()==1){
			u.setStatus(0);
		}else if(userInDB.getStatus().intValue()==0){
			u.setStatus(1);
		}
		u.setUpdater(user.getId());
		
		userService.updateUser(u);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="id")
    })
	@RequestMapping(value="/getUser.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getUser(User u,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//校验用户
		boolean ok=userService.checkUserDataAuth(u.getId(), user.getDeptDNA());
		if(!ok){
			throw new ServiceException(ExceptionCode.ERROR_NO_USER_AUTH,"没有该用户的操作权限");
		}
		
		User userInDB=userService.getUser(u);
		//清除明感信息
		userInDB.setPassword(null);
		result.setData(userInDB);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@RequestMapping(value="/getUserList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getUserList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		Map<String,Object>param=new HashMap<String,Object>();
		//页码
		int pageSize=CommonUtils.getPageSize(request);
		int offset=CommonUtils.getOffset(request);
		param.put("pageSize", pageSize);
		param.put("offset", offset);
		//模糊角色名称
		String name=request.getParameter("name");
		param.put("likeName", name);
		//加上部门DNA防止越权
		String deptIdStr=request.getParameter("deptId");
		if(StringUtils.isNotEmpty(deptIdStr)){
			Integer deptId=Integer.parseInt(deptIdStr);
			//根据部门Id和用户dna去查询数据，判断是否越权
			Dept d=userService.checkDeptDataAuth(deptId, user.getDeptDNA());
			if(d==null){
				throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH,"没有该部门的操作权限");
			}
			param.put("deptDNA", d.getDna());
		}else{
			//没有传部门ID过来，则默认是用户本身部门
			param.put("deptDNA", user.getDeptDNA());
		}
		
		String roleIdStr=request.getParameter("roleId");
		if(StringUtils.isNotEmpty(roleIdStr)){
			Integer roleId=Integer.parseInt(roleIdStr);
			boolean ok1=userService.checkRoleDataAuth(roleId, user.getDeptDNA());
			if(!ok1){
				throw new ServiceException(ExceptionCode.ERROR_NO_ROLE_AUTH,"没有该角色的操作权限");
			}
			
			param.put("roleId", roleId);
		}
		
		String phone=request.getParameter("phone");
		param.put("phone", phone);
		String email=request.getParameter("email");
		param.put("email", email);
		String statusStr=request.getParameter("status");
		if(StringUtils.isNotEmpty(statusStr)){
			int status=Integer.parseInt(statusStr);
			if(status==0||status==1){
				param.put("status", status);
			}
		}
		
		List<User>list=userService.getUserList(param);
		int total=userService.getUserListCount(param);
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", total);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	//**********************用户管理	结束*******************************
	
}
