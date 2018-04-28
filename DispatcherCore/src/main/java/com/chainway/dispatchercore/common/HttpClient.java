package com.chainway.dispatchercore.common;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;




public class HttpClient {
	private final static int timeout = 5000;//超时时间为2秒
	//发送一个GET请求
	 public static String get(String path){
	  HttpURLConnection httpConn=null;
	  BufferedReader in=null;
	
	  try {
	   URL url=new URL(path);
	   httpConn=(HttpURLConnection)url.openConnection();
	   httpConn.setDoInput(true);
	   httpConn.setDoOutput(true);
	   httpConn.setReadTimeout(timeout);
	   httpConn.setConnectTimeout(timeout);
	   //读取响应
	   if(httpConn.getResponseCode()==HttpURLConnection.HTTP_OK){
	    StringBuffer content=new StringBuffer();
	    String tempStr="";
	    in=new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
	    while((tempStr=in.readLine())!=null){
	     content.append(tempStr);
	    }
	   
	    return content.toString();
	   }
	  } catch (IOException e) {
		  e.printStackTrace();
	  }finally{
		  if(in != null){
			  try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
	   httpConn.disconnect();
	  }
	  return null;
	 }
	 //发送一个GET请求,参数形式key1=value1&key2=value2...
	 public static String post(String path,String params){
	  HttpURLConnection httpConn=null;
	  BufferedReader in=null;
	  PrintWriter out=null;
	  try {
	   URL url=new URL(path);
	   httpConn=(HttpURLConnection)url.openConnection();
	   httpConn.setRequestMethod("POST");
	   httpConn.setDoInput(true);
	   httpConn.setDoOutput(true);
	   httpConn.setReadTimeout(timeout);
	   httpConn.setConnectTimeout(timeout);
	   //发送post请求参数
	   out=new PrintWriter(httpConn.getOutputStream());
	   out.println(params);
	   out.flush();

	   //读取响应
	   if(httpConn.getResponseCode()==HttpURLConnection.HTTP_OK){
	    StringBuffer content=new StringBuffer();
	    String tempStr="";
	    in=new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
	    while((tempStr=in.readLine())!=null){
	     content.append(tempStr);
	    }
	    return content.toString();
	   }
	  } catch (IOException e) {
		  e.printStackTrace();
	  }finally{
	   try {
		   if(in!=null){
			   in.close();
		   }
		} catch (IOException e) {
			e.printStackTrace();
		}
	   if(out!=null){
		   out.close();
	   }
	   httpConn.disconnect();
	  }
	  return null;
	 }


		/**
	     * 向指定URL发送GET方法的请求
	     * 
	     * @param url
	     *            发送请求的URL
	     * @param param
	     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	     * @return URL 所代表远程资源的响应结果
	     */
	    public static String sendGet(String url, String param) {
	        String result = "";
	        BufferedReader in = null;
	        try {
	            String urlNameString = url + "?" + param;
	            URL realUrl = new URL(urlNameString);
	            // 打开和URL之间的连接
	            URLConnection connection = realUrl.openConnection();
	            // 设置通用的请求属性
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // 建立实际的连接
	            connection.connect();
	            // 获取所有响应头字段
	            Map<String, List<String>> map = connection.getHeaderFields();
	            // 遍历所有的响应头字段
	            for (String key : map.keySet()) {
	                System.out.println(key + "--->" + map.get(key));
	            }
	            // 定义 BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream(), "UTF-8"));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("发送GET请求出现异常！" + e);
	            e.printStackTrace();
	        }
	        // 使用finally块来关闭输入流
	        finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	        }
	        return result;
	    }

	    /**
	     * 向指定 URL 发送POST方法的请求
	     * 
	     * @param url
	     *            发送请求的 URL
	     * @param param
	     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	     * @return 所代表远程资源的响应结果
	     */
	    public static String sendPost(String url, String param) {
	        PrintWriter out = null;
	        BufferedReader in = null;
	        String result = "";
	        try {
	            URL realUrl = new URL(url);
	            // 打开和URL之间的连接
	            URLConnection conn = realUrl.openConnection();
	            // 设置通用的请求属性
	            conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // 发送POST请求必须设置如下两行
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            // 获取URLConnection对象对应的输出流
	            out = new PrintWriter(conn.getOutputStream());
	            // 发送请求参数
	            out.print(param);
	            // flush输出流的缓冲
	            out.flush();
	            // 定义BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("发送 POST 请求出现异常！"+e);
	            e.printStackTrace();
	        }
	        //使用finally块来关闭输出流、输入流
	        finally{
	            try{
	                if(out!=null){
	                    out.close();
	                }
	                if(in!=null){
	                    in.close();
	                }
	            }
	            catch(IOException ex){
	                ex.printStackTrace();
	            }
	        }
	        return result;
	    }    
	
	 public static void main(String[] args) throws Exception {
		 String urls="http://data.chainwayits.cn:88/service/lbs/info/pos";
		 String params="key=C79BFF3740328C4ABF700C6F6137EB2E&mcc=460&mnc=00&lac=13154&ci=53161";
		String resl= post(urls,params);
		System.out.println("resl:::"+resl);
		if(resl!=null){
			JSONObject jsonObj =JSON.parseObject(resl);
			System.out.println("ss::"+jsonObj.getString("lon")+"::::"+jsonObj.getString("lat"));
			JSONObject json = new JSONObject();
			json.put("aa", "11");
			json.put("bb", "22");
			json.put("cc", "33");
			String jsonStr = json.toString();
			System.out.println(jsonStr);
		}
	 }
}
