package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;
/**
 * 载货类型
 * @author chainwayits
 * @date 2018年4月10日
 */
public interface CarryTypeDao {
  public List<Map<String, Object>> getCarryTypeList();
}
