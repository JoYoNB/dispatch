package com.chainway.dispatchercore.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.chainway.dispatchercore.dto.Dept;
import com.chainway.util.rsa.RSAUtils;

public class IVCApiUtils {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	private String host;
	private String merchantId;
	private String user;
	private String rsaPrivateKey;
	
	private DefaultHttpClient httpclient;
	
	private static IVCApiUtils instance;
	
	private IVCApiUtils(){
		httpclient=new DefaultHttpClient();
	}
	
	public void init(String host,String merchantId,String user,String rsaPrivateKey){
		this.host=host;
		this.merchantId=merchantId;
		this.user=user;
		this.rsaPrivateKey=rsaPrivateKey;
		log.info("接口工具初始化完成");
	}
	
	public static IVCApiUtils getInstance(){
		if(instance==null){
			instance=new IVCApiUtils();
		}
		return instance;
	}
	
	private JSONObject genCommonParam(){
		String privateKey=this.rsaPrivateKey;
		String userName=this.user;
		SimpleDateFormat fmt=new SimpleDateFormat ("yyyyMMddHHmmss");
    	String timestamp=fmt.format(new Date());
    	String ip="127.0.0.1";
    	//sign
    	String antiFake="";
    	try {
			antiFake=RSAUtils.sign(timestamp.getBytes(), privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	JSONObject json=new JSONObject();
    	json.put("userName", userName);
    	json.put("antiFake", antiFake);
    	json.put("timestamp", timestamp);
    	json.put("serverIP", ip);
    	json.put("isEncryptResult", 0);//结果不加密
    	
    	return json;
	}
	
	private String encryptContent(String content){
		try {
			String encryptData=RSAUtils.encryptByPrivateKey(content, this.rsaPrivateKey);
			return encryptData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private void printRequestBody(InputStream ins){
		ByteArrayOutputStream baos=new ByteArrayOutputStream(); 
        int i=-1;
        try{
        	while((i=ins.read())!=-1){
            	baos.write(i);
            }
        	log.info("请求接口报文="+baos.toString());
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	private String postApi(String url,String content){
		HttpPost httpPost=new HttpPost(url);
		// prepare the request parameters  
        List<NameValuePair> params=new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("merchantId",merchantId));
        params.add(new BasicNameValuePair("content",content));
        // set the request entity  
        try {
			httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			//打印请求报文
			printRequestBody(httpPost.getEntity().getContent());
	        
		    HttpResponse response =httpclient.execute(httpPost);
		    HttpEntity entity = response.getEntity();
		    BufferedReader is=new BufferedReader(new InputStreamReader(entity.getContent()));
		    StringBuffer sb=new StringBuffer();
	        String line=null;
	        while((line=is.readLine())!=null){  
	        	String ss=new String(line.getBytes(),"UTF8");
	        	sb.append(ss);
	        }
	        log.info("接口返回报文="+sb.toString());
	        String responseBody=sb.toString();
	        
	        return responseBody;
		} catch (Exception e) {
			e.printStackTrace();
		}
        log.info("接口调用失败");
        return null;
	}
	
	public Integer addDept(Dept dept){
		String url=this.host+"/ivci/api/dept/addDeptInfo";
		
		JSONObject json=this.genCommonParam();
    	//String s=",\"addDept\":{\"deptName\":\"测试全量授权-4\",\"depfsId\":99103}";
    	
    	JSONObject addDept=new JSONObject();
    	addDept.put("deptName", dept.getName());
    	json.put("addDept", addDept);
		
    	String content=json.toJSONString();
    	log.info("api 明文参数="+content);
    	//加密
    	String encryptContent=this.encryptContent(content);
    	log.info("api 密文参数="+encryptContent);
    	
    	String responseBody=postApi(url, encryptContent);
    	if(StringUtils.isNotEmpty(responseBody)){
    		//解析出json
    		JSONObject ret=JSONObject.parseObject(responseBody);
    		if(ret!=null){
    			String code=ret.getString("code");
    			if("0".equals(code)){
    				JSONObject data=ret.getJSONObject("data");
    				if(data!=null){
    					Integer deptId=data.getInteger("deptId");
    					if(deptId!=null){
    						//新增成功
    						return deptId;
    					}
    				}
    			}
    		}
    	}
		return null;
	}
	
	/**
	 * 添加车辆
	 * @param vehicle
	 * @return
	 */
	public Integer addVehicle(Map<String, Object> param){
		String url=this.host+"/ivci/api/vehicle/addVehicle";
		JSONObject json=this.genCommonParam();
		JSONObject addVehicle=new JSONObject();
		addVehicle.put("userName", param.get("userName"));
		addVehicle.put("carEngine",param.get("carEngine"));
		addVehicle.put("carVim", param.get("carVim"));
		addVehicle.put("deptId", param.get("deptId"));
		addVehicle.put("gmtZone", param.get("gmtZone"));
		addVehicle.put("plateNo", param.get("plateNo"));
		json.put("addVehicle", addVehicle);
		
		String content=json.toJSONString();
    	log.info("api 明文参数="+content);
    	
    	//加密
    	String encryptContent=this.encryptContent(content);
    	log.info("api 密文参数="+encryptContent);
    	

    	String responseBody=postApi(url, encryptContent);
    	if(StringUtils.isNotEmpty(responseBody)){
    		//解析出json
    		JSONObject ret=JSONObject.parseObject(responseBody);
    		if(ret!=null){
    			String code=ret.getString("code");
    			if("0".equals(code)){
    				JSONObject data=ret.getJSONObject("data");
    				if(data!=null){
    					Integer vehicleId=data.getInteger("vehicleId");
    					if(vehicleId!=null){
    						//新增成功
    						return vehicleId;
    					}
    				}
    			}
    		}
    	}
		return null;
	}
	
	
	/**
	 * 查询车辆最新位置接口
	 * @param param
	 */
	public List<Map<String, Object>> getVehicleLastLocation(String vehicleIds) {
		String url=this.host+"/ivci/api/vehicle/getVehicleLastLocation";
		JSONObject json=this.genCommonParam();
		JSONObject data=new JSONObject();
		data.put("vehicleIds", vehicleIds);//vehicleIds为字符串，用逗号隔开
		json.put("data", data);
		
		String content=json.toJSONString();
    	log.info("api 明文参数="+content);
    	
    	//加密
    	String encryptContent=this.encryptContent(content);
    	log.info("api 密文参数="+encryptContent);
    	

    	String responseBody=postApi(url, encryptContent);
    	if(StringUtils.isNotEmpty(responseBody)){
    		//解析出json
    		JSONObject ret=JSONObject.parseObject(responseBody);
    		if(ret!=null){
    			String code=ret.getString("code");
    			if("0".equals(code)){
    				List<Map<String, Object>> dataResult=(List<Map<String, Object>>) ret.get("data");
    				if(dataResult!=null && dataResult.size()>0){
    				   return dataResult;
    				}
    			}
    		}
    	}
		return null;
	}
	
	
	/**
	 * 查询车辆轨迹接口
	 * @param vehicleId
	 * @param startTime
	 * @param endTime
	 * @param pageNum
	 * @return
	 */
	public Map<String, Object> getVehicleTrajectory(String vehicleId, String startTime, String endTime, int pageNum) {
		String url=this.host+"/ivci/api/vehicle/getVehicleTrajectory";
		JSONObject json=this.genCommonParam();
		JSONObject data=new JSONObject();
		data.put("vehicleId", vehicleId);
		data.put("startTime", startTime);
		data.put("endTime", endTime);
		data.put("pageNum", pageNum);
		json.put("data", data);
		
		String content=json.toJSONString();
    	log.info("api 明文参数="+content);
    	
    	//加密
    	String encryptContent=this.encryptContent(content);
    	log.info("api 密文参数="+encryptContent);
    	

    	String responseBody=postApi(url, encryptContent);
    	if(StringUtils.isNotEmpty(responseBody)){
    		//解析出json
    		JSONObject ret=JSONObject.parseObject(responseBody);
    		if(ret!=null){
    			String code=ret.getString("code");
    			if("0".equals(code)){
    				Map<String, Object> dataResult=(Map<String, Object>) ret.getJSONObject("data");
    				if(dataResult!=null){
    				   return dataResult;
    				}
    			}
    		}
    	}
		return null;
	}
}
