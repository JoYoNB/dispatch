package com.chainway.fileservice.common.file;

public interface FileServer {

	/**
	 * @param originalFileName 带后缀名，例如：11111.xlsx
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(String originalFileName,byte[] data,boolean isTempFile)throws Exception;
	
	public byte[] getFileData(String fileName,boolean isTemplateFile)throws Exception;
}
