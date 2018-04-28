package com.chainway.driverappweb.common;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

import chainway.frame.exception.SecuritysException;
import chainway.frame.security.SecurityUtil;

/**
 * 授权相关的工具类
 * 
 * @author xiaolong
 * 
 */
public class AuthUtils {
	/* AES加密的key */
	public final static String AESKEY = "chainwayits";

	/** AES加密算法定义,秘钥长度为16 */
	public static final String AES = "AES";
	/** DES加密算法定义,秘钥长度为8 */
	public static final String DES = "DES";
	/** DESede加密算法定义,秘钥长度为24 */
	public static final String DESede = "DESede";
	/** MD5加密算法定义 */
	public static final String MD5 = "MD5";
	/** SHA1加密算法定义 */
	public static final String SHA1 = "SHA";
	/** SHA2_256加密算法定义 */
	public static final String SHA2_256 = "SHA-256";
	/** SHA2_384加密算法定义 */
	public static final String SHA2_384 = "SHA-384";
	/** SHA2_512加密算法定义 */
	public static final String SHA2_512 = "SHA-512";
	/** SHA1PRNG伪随机数算法定义 */
	public static final String SHA1PRNG = "SHA1PRNG";

	/**
	 * 生产密文密码
	 * 
	 * @param password
	 *            明文密码
	 * @param email
	 *            邮箱
	 * @return
	 * @throws AuthException
	 */
	public static String getPassword(String password,Integer number) {
		String pwd = DigestUtils.md5Hex(password);
		System.out.println(pwd);
		for (int i = 0; i < number; i++) {
			pwd = DigestUtils.md5Hex(pwd);
			
		}
		return pwd.toLowerCase();
	}

	/**
	 * @param str
	 * @return
	 */
	public static String MD5(String str) {
		String mx = DigestUtils.md5Hex(str);
		// String md5value="";
		for (int i = 0; i < 10; i++) {
			mx = DigestUtils.md5Hex(mx);
		}
		return mx.toLowerCase();
	}

	/**
	 * 对数据进行AES加密
	 * 
	 * @param data
	 * @return
	 */
	public static String encryptionAES(String data) {
		String passWord = null;
		try {
			Key key = SecurityUtil.getKey(SecurityUtil.SHA1PRNG, SecurityUtil.AES, AESKEY);
			passWord = SecurityUtil.encryptionAES(key, data);
		} catch (SecuritysException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return passWord;

	}

	/**
	 * 对数据进行AES解密
	 * 
	 * @param data
	 * @return
	 */
	public static String decryptionAES(String data) {
		String passWord = null;
		try {
			Key key = SecurityUtil.getKey(SecurityUtil.SHA1PRNG, SecurityUtil.AES, AESKEY);
			passWord = SecurityUtil.decryptAES(key, data);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return passWord;
	}

	/**
	 * 解密配置文件中的密文
	 * 
	 * @param password
	 * @return
	 */
	public static String decryptionPassword(String enpassword) {
		String passkey = PropertiesUtil.getString("keypass");
		String password = null;
		try {
			/* passkey本身也是密文,需要根秘钥解密 */
			Key key = SecurityUtil.getKey(null, EncodePropertyFile.DATAALGORITHM, EncodePropertyFile.ROOTKEY);
			/* 秘钥明文 */
			String dePasskey = SecurityUtil.decrypt(key, passkey, EncodePropertyFile.DATAALGORITHM);
			key = SecurityUtil.getKey(null, EncodePropertyFile.DATAALGORITHM, dePasskey);
			/* 解密mysql密码 */
			password = SecurityUtil.decrypt(key, enpassword, EncodePropertyFile.DATAALGORITHM);
		} catch (SecuritysException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return password;
	}
   
	public static void main(String[] args) {
		//System.out.println(getPassword("123456",9));
		String encryptionAES = encryptionAES("123456");
		System.out.println("加密后"+encryptionAES);
		String decryptionAES = decryptionAES(encryptionAES);
		System.out.println("解密后"+decryptionAES);
		
	}
}
