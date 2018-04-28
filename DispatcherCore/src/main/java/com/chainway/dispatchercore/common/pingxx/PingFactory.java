package com.chainway.dispatchercore.common.pingxx;

import com.chainway.dispatchercore.common.PropertiesUtil;
import com.pingplusplus.Pingpp;

public class PingFactory {
	private static PingFactory pingFactory;
	private PingFactory(){
		Pingpp.apiKey = PropertiesUtil.getString("ping_apiKey");
		Pingpp.privateKey = PropertiesUtil.getString("ping_privateKey");
		Pingpp.appId = PropertiesUtil.getString("ping_appId");
	}
	public static PingFactory getInstance(){
		if(pingFactory==null){
			pingFactory = new PingFactory();
		}
		return pingFactory;
	}
	
	public ChargeExample getChargeExample(){
		return new ChargeExample(PropertiesUtil.getString("ping_appId"));
	}
}
