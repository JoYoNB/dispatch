package com.chainway.fileservice.service;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.FileTemplate;
import com.chainway.fileservice.dto.Parser;

public interface FileService {

	public Map<String,Object>test(Map<String,Object>param);
	 
	
	/**
	 * 导出
	 * @param <E>
	 * @param tpl 导出文件模板对象,用于查询
	 * @param data 导出数据集合
	 * @param token 文件服务器校验token
	 * @return
	 * @throws Exception 
	 */
	<E> String export(FileTemplate tpl,List<E> data,String token) throws Exception;
	
	/**
	 * 导出
	 * @param tpl 导出文件模板对象,用于查询
	 * @param data 导出数据集合
	 * @param param 其他参数，文件服务器校验token、用户时区timeZone
	 * @return
	 * @throws Exception 
	 */
	<E>String export(FileTemplate tpl,List<E> data,Map<String, Object> param) throws Exception;
	/**
	 * 上传文件
	 * @param fileName 	上传的文件，如：1234.jpg
	 * @param data 		文件二进制数据
	 * @param token		服务校验令牌
	 * @return			上传文件后的网络路径，如：http://127.0.0.1:8082/FileService/file/123kj213h213h21321.jpg
	 * @throws Exception
	 */
	public String uploadFile(String fileName,byte[] data,String token)throws Exception;
	
	public <T> Parser<T> genExcelParser();
}
