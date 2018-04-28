package com.chainway.dispatchercore.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };
	protected static MessageDigest messagedigest = null;
	static{
	   try{
	    messagedigest = MessageDigest.getInstance("MD5");
	   }catch(NoSuchAlgorithmException nsaex){
	    System.err.println(MD5Util.class.getName()+"初始化失败，MessageDigest不支持MD5Util。");
	    nsaex.printStackTrace();
	   }
	}
	public static void main(String[] args) throws IOException {
	   long begin = System.currentTimeMillis();
	   //2EA3E66AC37DF7610F5BD322EC4FFE48 670M 11s kuri双核1.66G 2G内存
	   File big = new File("D:/CW601-3Giot-N1.20.apk");
	   String md5=getFileMD5String(big);
	   
	  // File big2 = new File("D:/CW601-3Giot-N1.20.md5");MultipartFile
	   String mynn=getFileString("D:/CW601-3Giot-N1.20.md5");
	   long end = System.currentTimeMillis();
	   System.out.println(mynn);
	   System.out.println("md5:"+md5+" time:"+((end-begin)/1000)+"s");
	   System.out.println(md5.equalsIgnoreCase(mynn));
	}

	
	public static String getFileMD5String(File file) throws IOException {
		   FileInputStream in = new FileInputStream(file);
		   FileChannel ch = in.getChannel();
		   MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		   messagedigest.update(byteBuffer);
		   return bufferToHex(messagedigest.digest());
		}
	
	
	public static String getFileMD5String(String filename) throws IOException {
		File file=new File(filename);
		   FileInputStream in = new FileInputStream(file);
		   FileChannel ch = in.getChannel();
		   MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		   messagedigest.update(byteBuffer);
		   return bufferToHex(messagedigest.digest());
		}
	public static String getFileString(String file) throws IOException {
//		   FileInputStream in = new FileInputStream(file);
//		
//		   byte[] blob =null;
//		   try {
//			
//			  blob = new byte[in.available()];
//			   in.read(blob);
//			 
//			   } catch (Exception e) {
//			   e.printStackTrace();
//			   }
//		   finally {
//			in.close();
//		}
//		
	String name=readFileByLines(file);  
		   return name;
		   
		}
	public static String getFileString(File file) throws IOException {
		String name=readFileByLines(file);  
		   return name;
	}
	public static String getMD5String(String s) {
	   return getMD5String(s.getBytes());
	}
	public static String getMD5String(byte[] bytes) {
	   messagedigest.update(bytes);
	   return bufferToHex(messagedigest.digest());
	}
	private static String bufferToHex(byte bytes[]) {
	   return bufferToHex(bytes, 0, bytes.length);
	}
	private static String bufferToHex(byte bytes[], int m, int n) {
	   StringBuffer stringbuffer = new StringBuffer(2 * n);
	   int k = m + n;
	   for (int l = m; l < k; l++) {
	    appendHexPair(bytes[l], stringbuffer);
	   }
	   return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
	   char c0 = hexDigits[(bt & 0xf0) >> 4];
	   char c1 = hexDigits[bt & 0xf];
	   stringbuffer.append(c0);
	   stringbuffer.append(c1);
	}
	public static boolean checkPassword(String password, String md5PwdStr) {
	   String s = getMD5String(password);
	   return s.equalsIgnoreCase(md5PwdStr);
	}
	 /** 
     * 以行为单位读取文件，常用于读面向行的格式化文件 InputStream
     */  
    public static String readFileByLines(String fileName) {  
        File file = new File(fileName);  
        BufferedReader reader = null;  
        String md5s="";
        try {  
            System.out.println("以行为单位读取文件内容，一次读一整行：");  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
           
            int line = 1;  
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {  
                // 显示行号  
            	md5s+=tempString;
                System.out.println("line " + line + ": " + tempString);  
                line++;  
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
        return md5s;
    }  
    /** 
     * 以行为单位读取文件，常用于读面向行的格式化文件 InputStream
     */  
    public static String readFileByLines(BufferedReader reader) {  
       // File file = new File(fileName);  
        //BufferedReader reader = null;  
        String md5s="";
        try {  
            System.out.println("以行为单位读取文件内容，一次读一整行：");  
           // reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
           
            int line = 1;  
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {  
                // 显示行号  
            	md5s+=tempString;
              //  System.out.println("line " + line + ": " + tempString);  
                line++;  
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
        return md5s;
    }  
  
    /** 
     * 以行为单位读取文件，常用于读面向行的格式化文件 InputStream
     */  
    public static String readFileByLines(File file) {  
     
        BufferedReader reader = null;  
        String md5s="";
        try {  
            System.out.println("以行为单位读取文件内容，一次读一整行：");  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
           
            int line = 1;  
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {  
                // 显示行号  
            	md5s+=tempString;
                System.out.println("line " + line + ": " + tempString);  
                line++;  
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
        return md5s;
    }  
  
  
  
	}