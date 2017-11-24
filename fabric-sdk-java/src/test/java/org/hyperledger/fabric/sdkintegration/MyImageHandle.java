package org.hyperledger.fabric.sdkintegration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * use Base 64 handle image
 * 
 * @author root
 *
 */
public class MyImageHandle {

	// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	public static String getImageStr(String path) {
		String imgFile = path;// 待处理的图片
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		// 返回Base64编码过的字节数组字符串
		return encoder.encode(data);

	}

	// base64字符串转化成图片
	public static boolean generateImage(String imgStr, String path) { // 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) {
			// 图像数据为空
			return false;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		OutputStream out = null;
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			String imgFilePath = path;// 新生成的图片
			out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
