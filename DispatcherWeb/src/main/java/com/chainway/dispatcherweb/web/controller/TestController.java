package com.chainway.dispatcherweb.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatchercore.dto.FileTemplate;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.service.CommonService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.fileservice.dto.Parser;
import com.chainway.fileservice.dto.Render;
import com.chainway.fileservice.service.FileService;


@Controller
@RequestMapping("/test")
public class TestController {
	
	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Reference(timeout=60000, check=false)
	private CommonService commonService;
	@Reference(timeout=60000, check=false)
	private FileService fileService;
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="p1"),
            @ValidateFiled(index=0,filedName="p2",maxLen=10)
    })
	@Log
	@RequestMapping(value="/test.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object>test(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object>result=new HashMap<String,Object>();
        result.put("a", "a");
        result.put("b", 1);
        result.put("c", false);
        result.put("d", new Date());
        //远程调用dubbo服务
        Map<String,Object>param=new HashMap<String,Object>();
        Map<String,Object>ret=commonService.test(param);
        log.info(ret);
        result.putAll(ret);
        
		return result;
	}
	
	
	@RequestMapping(value="/uploadFile.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult uploadFile(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		//先获取上次的文件名
		//String[]fileNames=request.getParameterValues("file[]");
		String fileList=request.getParameter("files");
		String[]fileNames=fileList.split(",");
		
		List<String>retList=new ArrayList<String>();
		for(String name:fileNames){
			MultipartHttpServletRequest mulRequest=(MultipartHttpServletRequest) request;
			MultipartFile uploadFile=mulRequest.getFile(name);
			if(uploadFile!=null){
				//上传文件
				String fileName=uploadFile.getOriginalFilename();
				String webUrl=fileService.uploadFile(fileName,uploadFile.getBytes(),"123456789");
				retList.add(webUrl);
			}
		}
		
		result.setData(retList);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	
	@RequestMapping(value="/importFile.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult importFile(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String param1=request.getParameter("param1");
		String param2=request.getParameter("param2");
		String param3=request.getParameter("param3");
		
		log.info("param1="+param1);
		log.info("param2="+param2);
		log.info("param3="+param3);
		
		MultipartHttpServletRequest mulRequest=(MultipartHttpServletRequest) request;
		MultipartFile importExcel=mulRequest.getFile("importExcel");
		if(importExcel!=null){
			byte[]data=importExcel.getBytes();
			//把解析对象从远程生成返回web层
			Parser<User>parser=new Parser<User>();
			
			FileTemplate tpl=new FileTemplate();
			String rule="{startIndex:1,fileTypes:['xls','xlsx'],cels:[{'name':'name'},{'name':'phone'}]}";
			tpl.setVerifyRule(rule);
			List<User>list=parser.parse(data, tpl, new Render<User>(){

				/**
				 * 
				 */
				private static final long serialVersionUID = -6765478887990118655L;

				@Override
				public void fill(User user, String key, Object value) {
					if("name".equals(key)){
						user.setName((String) value);
					}else if("phone".equals(key)){
						user.setPhone((String) value);
					}
				}

				@Override
				public User genObject() {
					return new User();
				}
			});
			
			if(list!=null){
				for(User u:list){
					log.info(u.getName()+"   "+u.getPhone());
				}
			}
		}
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
}
