package com.chainway.dispatcherappweb.common;

import java.io.*;
import java.util.Date;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.*;
/**
 * 图片压缩处理
 */
public class  ImageUtil{
	private Image img;
	private int width;
	private int height;
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		System.out.println("开始：" + new Date().toLocaleString());
		ImageUtil imgCom = new ImageUtil("C:\\temp\\pic123.jpg");
		imgCom.resizeFix(400, 400);
		System.out.println("结束：" + new Date().toLocaleString());
	}
	/**
	 * 构造函数
	 */
	public ImageUtil(String fileName) throws IOException {
		File file = new File(fileName);// 读入文件
		img = ImageIO.read(file);      // 构造Image对象
		width = img.getWidth(null);    // 得到源图宽
		height = img.getHeight(null);  // 得到源图长
	}
	
	public ImageUtil(InputStream in) throws IOException {
		img = ImageIO.read(in);
		width = img.getWidth(null);
		height = img.getHeight(null);
	}
	
	
	/**
	 * 按照宽度还是高度进行压缩
	 * @param w int 最大宽度
	 * @param h int 最大高度
	 */
	public byte[] resizeFix(int w, int h) throws IOException {
		if (width / height > w / h) {
			return resizeByWidth(w);
		} else {
			return resizeByHeight(h);
		}
	}
	/**
	 * 以宽度为基准，等比例放缩图片
	 * @param w int 新宽度
	 */
	public byte[] resizeByWidth(int w) throws IOException {
		int h = (int) (height * w / width);
		return resizeToBytes(w, h);
	}
	/**
	 * 以高度为基准，等比例缩放图片
	 * @param h int 新高度
	 */
	public byte[] resizeByHeight(int h) throws IOException {
		int w = (int) (width * h / height);
		return resizeToBytes(w, h);
	}
	/**
	 * 强制压缩/放大图片到固定的大小
	 * @param w int 新宽度
	 * @param h int 新高度
	 */
	public void resize(int w, int h) throws IOException {
		// SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
		BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB ); 
		image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图
		
		File destFile = new File("C:\\temp\\456.jpg");
		FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流
		// 可以正常实现bmp、png、gif转jpg
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		encoder.encode(image); // JPEG编码
		out.close();
	}
	
	public byte[] resizeToBytes(int w, int h) throws IOException {
		BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB ); 
		image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图
		Graphics bg= image.getGraphics();
		bg.drawImage(image,0,0,null);
		bg.dispose(); 
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		try{ 
			ImageIO.write(image,"jpeg",out);
		} catch(IOException e){
			System.err.println("com.chainway.pis.information.util.ImageUtil:draw image error");
		} 
		return out.toByteArray();
	}
	
	
	
}