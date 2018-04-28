package com.chainway.fileservice.common.file;

import com.chainway.fileservice.common.PropertiesUtil;
import com.chainway.fileservice.common.file.impl.LocalFileServer;

public class FileServerFactory {
	
	public static String TYPE_LOCAL="local";
	public static String TYPE_ALICLOUD="alicloud";
	
	private static FileServerFactory factory;
	
	private FileServerFactory(){
		
	}
	
	public static FileServerFactory getInstance(){
		if(factory==null){
			factory=new FileServerFactory();
		}
		return factory;
	}
	
	public FileServer getFileServer(String fileServerType){
		if(TYPE_LOCAL.equals(fileServerType)){
			String webUrl=PropertiesUtil.getString("file.local.weburl");
			String root=PropertiesUtil.getString("file.local.root");
			FileServer server=new LocalFileServer(root,webUrl);
			return server;
		}else{
			//默认是本地服务
			String webUrl=PropertiesUtil.getString("file.local.weburl");
			String root=PropertiesUtil.getString("file.local.root");
			FileServer server=new LocalFileServer(root,webUrl);
			return server;
		}
	}
	
	public FileServer getFileServer(){
		String currentFileServer=PropertiesUtil.getString("file.select");
		return getFileServer(currentFileServer);
	}
}
