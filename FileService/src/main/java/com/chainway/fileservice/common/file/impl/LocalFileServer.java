package com.chainway.fileservice.common.file.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import com.chainway.fileservice.common.PropertiesUtil;
import com.chainway.fileservice.common.file.FileServer;


public class LocalFileServer implements FileServer {

	private String webUrl;
	private String root;
	
	public LocalFileServer(String root,String webUrl){
		super();
		this.webUrl=webUrl;
		this.root=root;
	}
	
	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	@Override
	public String uploadFile(String originalFileName, byte[] data, boolean isTempFile) throws Exception{
		String separator = File.separator;
		String basePath=root;
		if(isTempFile){
			//如果是临时文件，则放入临时文件夹（临时文件夹会按时清理）
			basePath+="temp"+separator;
		}
		//重新生成随机的名字
		String[]items=originalFileName.split("\\.");
		String name=items[0];
		name+=UUID.randomUUID().toString().replace("-", "");
		String suffix=items[items.length-1];
		
		String fileName=name+"."+suffix;
		String fullPath=basePath+fileName;
		File baseDir = new File(basePath);
		if(!baseDir.exists()){
			baseDir.mkdirs();
		}
		//异步生成文件
		GenFileTask task=new GenFileTask(fullPath,data);
		task.start();
		
        String url=webUrl+fileName;
		return url;
	}

	@Override
	public byte[] getFileData(String fileName, boolean isTemplateFile) throws Exception {
		String root=PropertiesUtil.getString("file.local.root");
		File file=new File(root+fileName);
		if(file==null){
			return null;
		}
		InputStream input=new FileInputStream(file);
		byte[] byt=new byte[input.available()];
		input.read(byt);
		
		input.close();
		
		return byt;
	}

	/*
	 * 生成文件做成异步的，先让文件路径先返回
	 * */
	private class GenFileTask extends Thread{
		
		private String fullPath;
		private byte[]data;
		
		public GenFileTask(String fullPath,byte[]data){
			this.fullPath=fullPath;
			this.data=data;
		}
		
		public void run(){
			File file=new File(fullPath);
			FileOutputStream fos=null;
			BufferedOutputStream bos=null;
			try{
				fos=new FileOutputStream(file);
				bos=new BufferedOutputStream(fos);
				bos.write(data);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				//关闭流
				if(bos!=null) {
		            try{
		                bos.close();
		            }catch(IOException e1){
		                e1.printStackTrace();
		            }
		        }
		        if(fos!=null){
		            try{
		                fos.close();
		            }catch(IOException e1){
		                e1.printStackTrace();
		            }
		        }
			}
			
			
		}
	}
	
	public static void main(String[]args) throws Exception{
		byte[] data = "1111111111".getBytes();
		String separator = File.separator;
		LocalFileServer fileServer = new LocalFileServer("E:\\temp\\", "http://127.0.0.1:8081/FileService/file/common/");
		fileServer.uploadFile("test.txt", data, false);
	}
	
}
