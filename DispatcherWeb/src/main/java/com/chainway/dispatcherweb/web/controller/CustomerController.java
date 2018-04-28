package com.chainway.dispatcherweb.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.dto.Area;
import com.chainway.dispatcherservice.dto.CustomerInfo;
import com.chainway.dispatcherservice.dto.GoodsType;
import com.chainway.dispatcherservice.service.CustomerInfoService;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;
import com.chainway.fileservice.service.FileService;

/**
 * 客户管理
 * @author chainwayits
 * @date 2018年3月28日
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private UserService userService;
	
	@Reference(timeout=60000, check=false)
	private FileService fileService;
	
	@Reference(timeout=60000, check=false)
	private CustomerInfoService customerInfoService;
	
	/**
	 * 新增用户
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="account",checkMaxLen=true,maxLen=50),
			@ValidateFiled(index=0,notNull=true,filedName="name",checkMaxLen=true,maxLen=100),
			@ValidateFiled(index=0,notNull=true,filedName="role",dataType="int"),
			@ValidateFiled(index=0,notNull=false,filedName="registeredCapital",dataType="double",maxVal=9999999),
            @ValidateFiled(index=0,notNull=true,filedName="mileageMin",dataType="int",minVal=1,maxVal=9999999),
            @ValidateFiled(index=0,notNull=true,filedName="mileageMax",dataType="int",minVal=1,maxVal=9999999)
    })
	@Log
	@RequestMapping(value="/add.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult add(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String fileList=request.getParameter("files");
		String[]fileNames=fileList.split(",");
		String logoList=request.getParameter("logos");
		String[]logoNames=logoList.split(",");
		MultipartHttpServletRequest mulRequest=(MultipartHttpServletRequest) request;
		
		MultipartFile uploadFile=mulRequest.getFile(fileNames[0]);
		String businessLicence=null;
		if(uploadFile!=null){
			//上传文件
			String fileName=uploadFile.getOriginalFilename();
			businessLicence=fileService.uploadFile(fileName,uploadFile.getBytes(),"123456789");
		}
		
		MultipartFile logoFile=mulRequest.getFile(logoNames[0]);
		String logo=null;
		if(logoFile!=null){
			//上传文件
			String fileName=logoFile.getOriginalFilename();
			logo=fileService.uploadFile(fileName,logoFile.getBytes(),"123456789");
		}
		
		String goodsTypeIdsStr=request.getParameter("goodsTypeIds");
		String[]goodsTypeIds=goodsTypeIdsStr.split(",");
		List<GoodsType> goodsTypes=new ArrayList<GoodsType>();
		for (String string : goodsTypeIds) {
			GoodsType gt=new GoodsType();
			gt.setId(Integer.parseInt(string));
			goodsTypes.add(gt);
		}
		
		String areaIdsStr=request.getParameter("areaIds");
		JSONArray arr=JSONArray.parseArray(areaIdsStr);
		Set<Area> areaset=new HashSet<Area>();
		for (Object obj : arr) {
			JSONObject json=(JSONObject) obj;
			Area area=new Area();
			area.setProvinceId(json.getString("provinceId"));
			area.setCityId(json.getString("cityId"));
			area.setDistrictId(json.getString("districtId"));
			areaset.add(area);
		}
		List<Area> areas=new ArrayList<>();
		areas.addAll(areaset);
		String name=request.getParameter("name");
		String account=request.getParameter("account");
		Integer role=Integer.parseInt(request.getParameter("role"));
		Integer settleType=Integer.parseInt(request.getParameter("settleType"));
		if(role!=2&&role!=4) {
			settleType=0;
		}
		String contacter=request.getParameter("contacter");
		String phone=request.getParameter("phone");
		String email=request.getParameter("email");
		String remark=request.getParameter("remark");
		String registeredCapital=request.getParameter("registeredCapital");
		String scc=request.getParameter("scc");
		String mileageMin=request.getParameter("mileageMin");
		String mileageMax=request.getParameter("mileageMax");
		String gmtZone=request.getParameter("gmtZone");
		
		CustomerInfo customerInfo=new CustomerInfo();
		User user=localService.getUserInSession(request);
		customerInfo.setCreater(user.getId());
		customerInfo.setUpdater(user.getId());
		customerInfo.setBusinessLicence(businessLicence);
		customerInfo.setAccount(account);
		customerInfo.setName(name);
		customerInfo.setRole(role);
		customerInfo.setSettleType(settleType);
		customerInfo.setContacter(contacter);
		customerInfo.setPhone(phone);
		customerInfo.setEmail(email);
		customerInfo.setRemark(remark);
		customerInfo.setRegisteredCapital(registeredCapital==null||"".equals(registeredCapital)?null:Double.parseDouble(registeredCapital));
		customerInfo.setScc(scc);
		customerInfo.setMileageMax(Integer.parseInt(mileageMax));
		customerInfo.setMileageMin(Integer.parseInt(mileageMin));
		customerInfo.setBusinessLicence(businessLicence);
		customerInfo.setLogo(logo);
		customerInfo.setGoodsTypes(goodsTypes);
		customerInfo.setAreas(areas);
		customerInfo.setGmtZone(gmtZone);
		
		customerInfoService.add(customerInfo);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 删除客户
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id[]")
    })
	@Log
	@RequestMapping(value="/delete.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String[]ids=request.getParameterValues("id[]");
		User user=localService.getUserInSession(request);
		CustomerInfo customerInfo=new CustomerInfo();
		customerInfo.setUpdater(user.getId());
		for(String id:ids){
			customerInfo.setId(Integer.parseInt(id));
			customerInfoService.delete(customerInfo);
		}
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 修改用户
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="name",checkMaxLen=true,maxLen=100),
			@ValidateFiled(index=0,notNull=true,filedName="role",dataType="int"),
			@ValidateFiled(index=0,notNull=false,filedName="registeredCapital",dataType="double",maxVal=9999999),
            @ValidateFiled(index=0,notNull=true,filedName="mileageMin",dataType="int",minVal=1,maxVal=9999999),
            @ValidateFiled(index=0,notNull=true,filedName="mileageMax",dataType="int",minVal=1,maxVal=9999999)
    })
	@Log
	@RequestMapping(value="/update.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult update(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String fileList=request.getParameter("files");
		String[]fileNames=fileList.split(",");
		String logoList=request.getParameter("logos");
		String[]logoNames=logoList.split(",");
		MultipartHttpServletRequest mulRequest=(MultipartHttpServletRequest) request;
		
		MultipartFile uploadFile=mulRequest.getFile(fileNames[0]);
		String businessLicence=null;
		if(uploadFile!=null){
			//上传文件
			String fileName=uploadFile.getOriginalFilename();
			businessLicence=fileService.uploadFile(fileName,uploadFile.getBytes(),"123456789");
		}
		
		MultipartFile logoFile=mulRequest.getFile(logoNames[0]);
		String logo=null;
		if(logoFile!=null){
			//上传文件
			String fileName=logoFile.getOriginalFilename();
			logo=fileService.uploadFile(fileName,logoFile.getBytes(),"123456789");
		}
		String goodsTypeIdsStr=request.getParameter("goodsTypeIds");
		String[]goodsTypeIds=goodsTypeIdsStr.split(",");
		List<GoodsType> goodsTypes=new ArrayList<GoodsType>();
		for (String string : goodsTypeIds) {
			GoodsType gt=new GoodsType();
			gt.setId(Integer.parseInt(string));
			goodsTypes.add(gt);
		}
		
		String areaIdsStr=request.getParameter("areaIds");
		JSONArray arr=JSONArray.parseArray(areaIdsStr);
		Set<Area> areaset=new HashSet<Area>();
		for (Object obj : arr) {
			JSONObject json=(JSONObject) obj;
			Area area=new Area();
			area.setProvinceId(json.getString("provinceId"));
			area.setCityId(json.getString("cityId"));
			area.setDistrictId(json.getString("districtId"));
			areaset.add(area);
		}
		List<Area> areas=new ArrayList<>();
		areas.addAll(areaset);
		
		String id=request.getParameter("id");
		String userId=request.getParameter("userId");
		String name=request.getParameter("name");
		Integer role=Integer.parseInt(request.getParameter("role"));
		Integer settleType=Integer.parseInt(request.getParameter("settleType"));
		if(role!=2&&role!=4) {
			settleType=0;
		}
		String contacter=request.getParameter("contacter");
		String phone=request.getParameter("phone");
		String email=request.getParameter("email");
		String remark=request.getParameter("remark");
		String registeredCapital=request.getParameter("registeredCapital");
		String scc=request.getParameter("scc");
		String mileageMin=request.getParameter("mileageMin");
		String mileageMax=request.getParameter("mileageMax");
		String gmtZone=request.getParameter("gmtZone");
		
		CustomerInfo customerInfo=new CustomerInfo();
		User user=localService.getUserInSession(request);
		customerInfo.setId(Integer.parseInt(id));
		customerInfo.setUserId(Integer.parseInt(userId));
		customerInfo.setUpdater(user.getId());
		customerInfo.setBusinessLicence(businessLicence);
		customerInfo.setName(name);
		customerInfo.setRole(role);
		customerInfo.setSettleType(settleType);
		customerInfo.setContacter(contacter);
		customerInfo.setPhone(phone);
		customerInfo.setEmail(email);
		customerInfo.setRemark(remark);
		customerInfo.setRegisteredCapital(registeredCapital==null||"".equals(registeredCapital)?null:Double.parseDouble(registeredCapital));
		customerInfo.setScc(scc);
		customerInfo.setMileageMax(Integer.parseInt(mileageMax));
		customerInfo.setMileageMin(Integer.parseInt(mileageMin));
		customerInfo.setBusinessLicence(businessLicence);
		customerInfo.setGoodsTypes(goodsTypes);
		customerInfo.setAreas(areas);
		customerInfo.setGmtZone(gmtZone);
		customerInfo.setLogo(logo);
		customerInfoService.update(customerInfo);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 查询计价规则列表
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/list.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@TimeZone
	public JsonResult list(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		
		Map<String,Object>param=new HashMap<String,Object>();
		String role=request.getParameter("role");
		param.put("role", role);
		String name=request.getParameter("name");
		param.put("name", name);
		//页码
		int pageSize=CommonUtils.getPageSize(request);
		int offset=CommonUtils.getOffset(request);
		param.put("pageSize", pageSize);
		param.put("offset", offset);
		List<CustomerInfo>list=customerInfoService.getList(param);
		int total=customerInfoService.getListCount(param);
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", total);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id",dataType="int",minVal=0,maxVal=999999999)
    })
	@TimeZone
	@RequestMapping(value="/info.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult info(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String id=request.getParameter("id");
		CustomerInfo customerInfo=customerInfoService.getInfo(Integer.parseInt(id));
		result.setData(customerInfo);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
}
