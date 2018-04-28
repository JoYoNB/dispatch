package com.chainway.dispatcherservice.service;


import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.dto.OrderParam;
import com.chainway.dispatcherservice.dto.SiteParam;

/**
 * 货主服务
 * @author xubao
 *
 */
public interface ConsignorService {
	
	/**
	 * 订单统计
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> orderStatistics(Map<String, Object> param);
	
	/**
	 * 货物统计
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> cargoStatistics(Map<String, Object> param);
	
	/**
	 * 订单排行
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> orderRank(Map<String, Object> param);
	
	/**
	 * 送达货物排行
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> deliveryCargoRank(Map<String, Object> param);
	
	/**
	 * 累计数据统计
	 * @param param
	 * @return
	 */
	Map<String, Object> totalStatistics(Map<String, Object> param);
	
	
	/**
	 * 地图订单列表查询
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> mapOrderList(Map<String, Object> param);
	
	/**
	 * 获取订单详情
	 * @param param
	 * @return
	 */
	Map<String, Object> getOrderDetails(Map<String, Object> param);
	
	/**
	 * 创建订单
	 * @param param
	 * @throws ServiceException 
	 */
	void createOrder(OrderParam param) throws ServiceException;
	
	/**
	 * 修改订单
	 * @param param
	 * @throws ServiceException
	 */
	void modifyOrder(OrderParam param)throws ServiceException;
	
	/**
	 * 取消订单
	 * @param order
	 * @throws Exception
	 */
	public void cancelOrder(OrderParam param)throws Exception;
	/**
	 * 订单列表
	 * @param param
	 * @return
	 */
	Map<String, Object> orderList(Map<String, Object> param);
	
	Map<String, Object> orderListForApp(Map<String, Object> param);
	/**
	 * 创建站点
	 * @param site
	 * @return 
	 * @throws ServiceException 
	 */
	Map<String, Object> createSite(SiteParam site) throws ServiceException;
	
	/**
	 * 删除站点
	 * @param siteId
	 * @throws ServiceException 
	 */
	void deleteSite(Integer siteId) throws ServiceException;
	
	/**
	 * 修改站点
	 * @param site
	 * @throws ServiceException 
	 */
	void modifySite(SiteParam site) throws ServiceException;
	
	/**
	 * 通过站点id获取站点详细信息
	 * @param param
	 * @return
	 */
	Map<String, Object> getSiteById(Map<String, Object> param);
	
	/**
	 * 按条件查询站点列表信息
	 * @param param
	 * @return
	 */
	Map<String, Object> getSiteList(Map<String, Object> param);
	
	/**
	 * 根据手机号或者姓名查询常用联系人列表
	 * @param name
	 * @param phone
	 * @param deptId
	 * @return
	 */
	List<Map<String, Object>> getLinkMan(Map<String, Object> param);
	
	/**
	 * 删除订单
	 * @param orderNo
	 * @throws ServiceException
	 */
	void deleteOrder(OrderParam param) throws ServiceException;
	
	/**
	 * 导出订单
	 * @param param
	 * @return 
	 * @throws ServiceException
	 * @throws Exception 
	 */
	Map<String, Object> exportOrder(Map<String, Object> param)throws Exception;
	
	/**
	 * 查询有权限的站点信息供下拉选择使用
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> getSiteForSelect(Map<String, Object> param);
	
	/**
	 * 根据车辆类型、城市和距离估算费用
	 * @param param
	 * @return
	 */
	Double calculatePrice(Map<String, Object> param);
}
