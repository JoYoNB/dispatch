package com.chainway.fileservice.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.chainway.dispatchercore.common.ExcelUtils;
import com.chainway.dispatchercore.dto.FileTemplate;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.fileservice.biz.dao.FileTemplateDao;
import com.chainway.fileservice.biz.service.DemoService;
import com.chainway.fileservice.common.file.FileServer;
import com.chainway.fileservice.common.file.FileServerFactory;
import com.chainway.fileservice.common.file.impl.BaseFileServer;
import com.chainway.fileservice.dto.Parser;
import com.chainway.fileservice.service.FileService;

@Component  
@Service
public class FileServiceImpl extends BaseFileServer implements FileService {
 
	@Autowired
	private DemoService demoService;
	@Autowired
	private FileTemplateDao fileTemplateDao;
	
	@Override
	public Map<String, Object> test(Map<String, Object> param) {
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("a", 1);
		ret.put("b", false);
		ret.put("c", "c");
		ret.put("d", new Date());
		
		ret.putAll(demoService.test(param));
		
		return ret;
	}

	@Override
	public <E>String export(FileTemplate tpl, List<E> data,String token) throws Exception {
		if(!checkToken(token)){
			throw new ServiceException(ExceptionCode.ERROR_FILE_SERVER_TOKEN_ERROR,"文件服务token校验失败");
		}
		FileTemplate template = fileTemplateDao.getFileTemplate(tpl);
		if (tpl == null) {
			return null;
		}
		JSONObject tempJson = JSONObject.parseObject(template.getVerifyRule());
		byte[] byt = ExcelUtils.exportFile(tempJson, data,null);
		if(byt==null){
			return null;
		}
		String fileName = tempJson.getString("name");
		FileServer fileService = FileServerFactory.getInstance().getFileServer(FileServerFactory.TYPE_LOCAL);
		String url = fileService.uploadFile(fileName,byt,false);
		return url;
	}
	
	@Override
	public <E>String export(FileTemplate tpl, List<E> data,Map<String, Object> param) throws Exception {
		if(!checkToken((String)param.get("token"))){
			throw new ServiceException(ExceptionCode.ERROR_FILE_SERVER_TOKEN_ERROR,"文件服务token校验失败");
		}
		if (tpl == null) {
			return null;
		}
		FileTemplate template = null;
		if(tpl.getVerifyRule() == null || tpl.getVerifyRule().trim().endsWith("")){
			template = fileTemplateDao.getFileTemplate(tpl);
		}
		JSONObject tempJson = JSONObject.parseObject(template.getVerifyRule());
		byte[] byt = ExcelUtils.exportFile(tempJson, data,(String)param.get("timeZone"));
		if(byt==null){
			return null;
		}
		String fileName = tempJson.getString("name");
		FileServer fileService = FileServerFactory.getInstance().getFileServer(FileServerFactory.TYPE_LOCAL);
		String url = fileService.uploadFile(fileName,byt,false);
		return url;
	}

	@Override
	public String uploadFile(String fileName,byte[] data, String token)throws Exception {
		if(!checkToken(token)){
			throw new ServiceException(ExceptionCode.ERROR_FILE_SERVER_TOKEN_ERROR,"文件服务token校验失败");
		}
		FileServerFactory factory=FileServerFactory.getInstance();
		FileServer fileServer=factory.getFileServer();
		String url=fileServer.uploadFile(fileName, data, false);
		return url;
	}

	@Override
	public <T> Parser<T> genExcelParser() {
		Parser<T>parser=new Parser<T>();
		return parser;
	}

	
}
