package com.chainway.dispatchercore.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1710829071715863162L;
	
	private List<Node>leaf=new ArrayList<Node>();
	private List<Map<String,Object>>source;
	//节点索引库
	private Map<Integer,Node>nodesIndex=new HashMap<Integer,Node>();
	//已经添加的节点
	private Map<Integer,Node>addedNodes=new HashMap<Integer,Node>();
	
	/**
	 * 施肥
	 * 注入资源
	 * @param source
	 */
	public void apply(List<Map<String,Object>>source){
		this.source=source;
		if(this.source!=null){
			//list转map，建立索引库
			for(Map<String,Object>map:source){
				Integer id=(Integer) map.get("id");
				String code=(String) map.get("code");
				Integer parentId=(Integer) map.get("parentId");
				String name=(String) map.get("name");
				Node node=new Node();
				node.setId(id);
				node.setCode(code);
				node.setParentId(parentId);
				node.setName(name);
				
				nodesIndex.put(id, node);
			}
		}
		
	}
	
	public Tree grow(){
		if(this.source==null){
			return null;
		}
		for(Map<String,Object>map:source){
			Integer id=(Integer) map.get("id");
			if(addedNodes.get(id)!=null){
				continue;
			}
			String code=(String) map.get("code");
			Integer parentId=(Integer) map.get("parentId");
			String name=(String) map.get("name");
			
			Node node=new Node();
			node.setId(id);
			node.setCode(code);
			node.setParentId(parentId);
			node.setName(name);
			
			Node parentNode=new Node();
			parentNode.setId(parentId);
			
			this.addNode(node, parentNode);
		}
		//销毁缓存数据，释放空间
		destroy();
		return this;
		
	}
	
	private void destroy(){
		this.source=null;
		this.nodesIndex=null;
		this.addedNodes=null;
	}
	
	public Node cloneNode(Node node){
		Node _node=new Node();
		_node.setId(node.getId());
		_node.setCode(node.getCode());
		_node.setName(node.getName());
		_node.setParentId(node.getParentId());
		
		return _node;
	}
	
	/**
	 * 给某个节点添加孩子节点
	 * @param node
	 * @param parentNode
	 */
	public void addNode(Node node,Node parentNode){
		if(parentNode.getId()==null){
			//该节点没有父节点
			this.leaf.add(node);
			addedNodes.put(node.getId(), node);
		}else{
			//先要查找该父节点
			
			//先从当前的树找
			Node pNode=findNode(parentNode.getId());
			if(pNode!=null){
				//父节点已经存在树上
				pNode.children.add(node);
				addedNodes.put(node.getId(), node);
			}else{
				//在树上没发现父节点,则从索引库查找
				Node indexNode=this.nodesIndex.get(parentNode.getId());
				if(indexNode==null){
					//索引库找不到，则返回
					return;
				}
				Node _node=cloneNode(indexNode);
				//克隆一份索引库的数据
				_node.children.add(node);
				addedNodes.put(node.getId(), node);
				//把父级节点一并加到树上
				Node _pNode=new Node();
				_pNode.setId(_node.getParentId());
				addNode(_node, _pNode);
			}
		}
		
	}
	
	public Node findNode(Integer id){
		for(Node l:leaf){
			Node n=find(id, l);
			if(n!=null){
				return n;
			}
			
		}
		
		return null;
	}
	public Node find(Integer id,Node node){
		if(id.intValue()==node.getId().intValue()){
			return node;
		}
		List<Node>children=node.getChildren();
		for(Node n:children){
			Node nn=find(id, n);
			if(nn!=null){
				return nn;
			}
		}
		return null;
	}
	
	
	
	
	public List<Node> getLeaf() {
		return leaf;
	}

	public void setLeaf(List<Node> leaf) {
		this.leaf = leaf;
	}

	class Node implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1365714397309998363L;
		
		private Integer id;
		private String code;
		private String name;
		private Integer parentId;
		private Integer level;
		
		private List<Node>children=new ArrayList<Node>();

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getParentId() {
			return parentId;
		}

		public void setParentId(Integer parentId) {
			this.parentId = parentId;
		}

		public Integer getLevel() {
			return level;
		}

		public void setLevel(Integer level) {
			this.level = level;
		}

		public List<Node> getChildren() {
			return children;
		}

		public void setChildren(List<Node> children) {
			this.children = children;
		}
		
	}
	
	
	public static void main(String[]args){
		Tree t=new Tree();
		List<Map<String,Object>>list=new ArrayList<Map<String,Object>>();
		Map<String,Object>m1=new HashMap<String,Object>();
		m1.put("id", 1);
		m1.put("name", "1111");
		m1.put("parentId", null);
		m1.put("code", "1111");
		list.add(m1);
		
		Map<String,Object>m2=new HashMap<String,Object>();
		m2.put("id", 2);
		m2.put("name", "222222");
		m2.put("parentId", null);
		m2.put("code", "222222");
		list.add(m2);
		
		Map<String,Object>m3=new HashMap<String,Object>();
		m3.put("id", 3);
		m3.put("name", "333");
		m3.put("parentId", 1);
		m3.put("code", "33333");
		list.add(m3);
		
		Map<String,Object>m4=new HashMap<String,Object>();
		m4.put("id", 4);
		m4.put("name", "4444");
		m4.put("parentId", 5);
		m4.put("code", "44444");
		list.add(m4);
		
		
		Map<String,Object>m5=new HashMap<String,Object>();
		m5.put("id", 5);
		m5.put("name", "5555");
		m5.put("parentId", null);
		m5.put("code", "55555");
		list.add(m5);
		
		t.apply(list);
		t.grow();
		System.out.println(t.getLeaf());
	}

}
