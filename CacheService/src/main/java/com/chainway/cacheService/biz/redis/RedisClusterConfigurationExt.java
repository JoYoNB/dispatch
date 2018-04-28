package com.chainway.cacheService.biz.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;

public class RedisClusterConfigurationExt extends RedisClusterConfiguration{
	
	private String nodes;
	
	public String getNodes() {
		return nodes;
	}

	public void setNodes(String nodes) {
		this.nodes = nodes;
		setClusterNodesByStr(nodes);
	}

	public RedisClusterConfigurationExt(){
		super();
	}
	
	public RedisClusterConfigurationExt(int maxRedirects,String nodes){
		this.setMaxRedirects(maxRedirects);
		setClusterNodesByStr(nodes);
	}
	
	private void setClusterNodesByStr(String nodes){
		String[] nodeArray=nodes.split(",");
		if(nodeArray!=null){
			List<RedisNode> nodeList=new ArrayList<RedisNode>();
			for(String url:nodeArray){
				String[]items=url.split(":");
				String ip=items[0];
				String port=items[1];
				
				RedisNode node=new RedisNode(ip,Integer.parseInt(port));
				nodeList.add(node);
			}
			
			this.setClusterNodes(nodeList);//Iterable<RedisNode>
		}
	}
}
