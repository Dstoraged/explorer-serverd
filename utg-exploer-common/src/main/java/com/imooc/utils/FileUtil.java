package com.imooc.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

@SuppressWarnings("restriction")
public class FileUtil {
	
	public static String encodeFileBase64(String filepath){
	//	File file = new File(filepath);
		byte[] bytes = null;
		InputStream in = null;
		try {
			in = new FileInputStream(filepath);
			bytes = new byte[in.available()];
			in.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}		
		BASE64Encoder encoder = new BASE64Encoder();
		String str = encoder.encode(bytes);
	//	System.out.println(str);
		return str;
	}
		
	public static void decodeFileBase64(String str, File file) {
		BASE64Decoder decoder = new BASE64Decoder();
		OutputStream out = null;
		try{
			byte[] bytes = decoder.decodeBuffer(str);
			for (int i = 0; i < bytes.length; i++) {
				if (bytes[i] < 0)
					bytes[i] += 256;
			}
			out = new FileOutputStream(file);
			out.write(bytes);
			out.flush();			
		}catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try { out.close(); } catch (IOException e) {}
			}	
		}		
	}
}
