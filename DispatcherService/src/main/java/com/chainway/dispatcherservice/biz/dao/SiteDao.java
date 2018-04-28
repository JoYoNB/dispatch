package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatcherservice.dto.SiteParam;

public interface SiteDao {
	
	/**
	 * 删除站点
	 * @param siteId
	 */
	void deleteSiteById(Integer siteId);
	
	/**
	 * 通过id获取站点详情
	 * @param siteId
	 * @return
	 */
	Map<String, Object> getSiteById(Map<String, Object> param);
	
	/**
	 * 创建站点
	 * @param site
	 */
	void createSite(SiteParam site);
	
	/**
	 * 根据站点id查找绑定的订单数量，判断站点是否有和订单关联
	 * @param siteId
	 * @return
	 */
	Integer getOrderNumBySiteId(Integer siteId);
	
	/**
	 * 修改站点
	 * @param param
	 */
	void modifySite(SiteParam param);
	
	/**
	 * 获取站点信息列表
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> getSiteList(Map<String, Object> param);
	
	/**
	 * 创建站点联系人，并返回主键id到site实体对象中
	 * @param site
	 * @return
	 */
	void createLinkMan(SiteParam site);
	
	/**
	 * 根据手机号或者姓名查询常用联系人列表
	 * @param name
	 * @param phone
	 * @param deptId
	 * @return
	 */
	List<Map<String, Object>> getLinkMan(Map<String, Object> param);
	
	/**
	 * 查询符合条件的总站点数
	 * @param param
	 * @return
	 */
	Integer totalSiite(Map<String, Object> param);
	
	/**
	 * 查询有权限的站点信息供下拉选择使用
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> getSiteForSelect(Map<String, Object> param);
}
