package com.chainway.fileservice.web.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.chainway.fileservice.common.file.FileServerFactory;

@Controller
@RequestMapping("/file")
public class FileController {

	protected final Logger log = Logger.getLogger(this.getClass());

	@RequestMapping(value = "/common/{fileName:.+}", method = { RequestMethod.GET, RequestMethod.POST })
	public void commonFile(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("fileName") String fileName) throws Exception {
		log.debug("查询的文件:" + fileName);

		byte[] data = FileServerFactory.getInstance().getFileServer().getFileData(fileName,false);
		if (data != null) {
			// 写入response
			OutputStream outputStream = null;
			try {
				fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
				response.setHeader("Content-disposition", "attachment;filename=" + fileName);
				outputStream = response.getOutputStream();
				outputStream.write(data);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
