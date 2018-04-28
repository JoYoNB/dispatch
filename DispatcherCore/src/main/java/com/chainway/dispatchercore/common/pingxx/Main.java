package com.chainway.dispatchercore.common.pingxx;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.pingplusplus.Pingpp;

/**
 * Created by Afon on 16/4/26.
 */
public class Main {

	/**
	 * Pingpp 管理平台对应的 API Key，api_key 获取方式：登录
	 * [Dashboard](https://dashboard.pingxx.com)->点击管理平台右上角公司名称->开发信息-> Secret
	 * Key
	 */
	private final static String apiKey = "sk_live_e4suu2WIdMN1CgSxZInNwDzm";

	/**
	 * Pingpp 管理平台对应的应用 ID，app_id 获取方式：登录
	 * [Dashboard](https://dashboard.pingxx.com)->点击你创建的应用->应用首页->应用 ID(App ID)
	 */
	private final static String appId = "app_mLu5a5rL0OuPG44K";

	/**
	 * 设置请求签名密钥，密钥对需要你自己用 openssl
	 * 工具生成，如何生成可以参考帮助中心：https://help.pingxx.com/article/123161；
	 * 生成密钥后，需要在代码中设置请求签名的私钥(rsa_private_key.pem)； 然后登录
	 * [Dashboard](https://dashboard.pingxx.com)->点击右上角公司名称->开发信息->商户公钥（
	 * 用于商户身份验证） 将你的公钥复制粘贴进去并且保存->先启用 Test 模式进行测试->测试通过后启用 Live 模式
	 */

	// 你生成的私钥路径
	private final static String privateKeyFilePath = "res/your_rsa_private_key_pkcs8.pem";

	public static void main(String[] args) throws Exception {

		// 设置 API Key
		Pingpp.apiKey = apiKey;

		// 设置私钥路径，用于请求签名
		// Pingpp.privateKeyPath = privateKeyFilePath;

		/**
		 * 或者直接设置私钥内容 Pingpp.privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
		 * "... 私钥内容字符串 ...\n" + "-----END RSA PRIVATE KEY-----\n";
		 */
		Pingpp.privateKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnkRGCBNfgfm1B8Noz907JhS2tAQzt+um2rQmlSE9fHMkW0mBF1DU4/lg26U0KugYvMWSLNqvKsEYoxJvgxfTgcECWCbZl8CKhkVt3GYS8HavWSYJr/b0bSvl5VYXLEVfJFJ3xFjt36oq705b1Qi/EkoCVrtr7DLHI4seIL3eWuinqD7SwWAQgx+eYsMYhz1D6kjHSG6ijZdqw1dx4KLjp+mFzjSze/PJyOnXBk5JfKPAkLBldI6/NmdX5N0FQ9vTTYFqBZB9IfN5AoX72r1lLEYauwsBNMBmCI71Nk9pjRXXMNI6Z3U/a44l6Vt8mvJj7aGS3zsIX1aGwLbOBGWGFwIDAQAB-----END PUBLIC KEY-----";

		// Charge 示例
		ChargeExample.runDemos(appId);

		// Refund 示例
		// RefundExample.runDemos();

		// RedEnvelope 示例
		// RedEnvelopeExample.runDemos(appId);

		// Transfer 示例
		// TransferExample.runDemos(appId);

		// Event 示例
		// EventExample.runDemos();

		// Webhooks 验证示例
		// WebhooksVerifyExample.runDemos();

		// 微信公众号 openid 相关示例
		// WxPubOAuthExample.runDemos(appId);

		// 身份证银行卡信息认证接口
		// 请使用 live key 调用该接口
		// IdentificationExample.runDemos(appId);

		// 批量付款示例
		// BatchTransferExample.runDemos(appId);

		// 报关
		// 请使用 live key 调用该接口
		// CustomsExample.runDemos(appId);
	}

	private static SecureRandom random = new SecureRandom();

	public static String randomString(int length) {
		String str = new BigInteger(130, random).toString(32);
		return str.substring(0, length);
	}

	public static int currentTimeSeconds() {
		return (int) (System.currentTimeMillis() / 1000);
	}
}
