package com.chainway.dispatcherservice.biz.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

  /**
   * 
   * override determineCurrentLookupKey
   * <p>
   * Title: determineCurrentLookupKey
   * </p>
   * <p>
   * Description: 自动查找datasource
   * </p>
   * 
   * @return
   */
  @Override
  protected Object determineCurrentLookupKey() {
      return DBContextHolder.getDbType();
  }

}
