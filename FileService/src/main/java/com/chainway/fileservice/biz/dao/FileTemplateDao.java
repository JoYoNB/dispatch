package com.chainway.fileservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.FileTemplate;

public interface FileTemplateDao {

	public int addFileTemplate(FileTemplate tpl);

	public int deleteFileTemplate(FileTemplate tpl);

	public int updateFileTemplate(FileTemplate tpl);

	public FileTemplate getFileTemplate(FileTemplate tpl);

	public List<FileTemplate> getFileTemplateList(Map<String, Object> param);

	public int getFileTemplateListCount(Map<String, Object> param);
}
