package com.imooc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommadUtil {
	
	public static String execute(String... command) {
		ProcessBuilder processBuilder = new ProcessBuilder(command);
	//	processBuilder.directory(new File("/user"));
		processBuilder.redirectErrorStream(true);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		Process ps = null;
		try {
			ps = processBuilder.start();
			br = new BufferedReader(new InputStreamReader(ps.getInputStream(),"gb2312"));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			try {
				ps.waitFor();
			} catch (InterruptedException e) {
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (ps != null) {
				ps.destroy();
			}
		}
		return sb.toString();
	}
	
	public static String exec(String... command) {
		Process ps = null;
		BufferedReader out = null;
		BufferedReader err = null;
		StringBuffer sb = new StringBuffer();
		try {
			ps = Runtime.getRuntime().exec(command);

			err = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
			String errline;
			while ((errline = err.readLine()) != null) {
				sb.append(errline).append("\n");
			}
			if (sb.length() > 0) {
				throw new RuntimeException(sb.toString());
			}
			
			out = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			String line;
			while ((line = out.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (err != null) {
				try {
					err.close();
				} catch (IOException e) {
				}
			}
			if (ps != null) {
				ps.destroy();
			}
		}
	}
	
	
	
	public static void main(String[] args){
	//	String result = execute("java","-version");
		String result = exec("D:\\nodejs\\npm.cmd","-version");
		System.out.print(result);
	}
}
