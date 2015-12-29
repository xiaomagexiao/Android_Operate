package com.mage.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class CommonUtil {
	

	public static List<String> filelist = new ArrayList<String>();
	
	/**
	 * 通过递归得到某一路径下所有的目录及其文件
	 * @param filePath
	 * @param typeName
	 */
	public static void getFiles(String filePath, String typeName) {
		File root = new File(filePath);
		File[] files = root.listFiles();
		String path = "";
		if (files == null) {
			return;
		}
		for (File file : files) {
			path = file.getAbsolutePath();
			if (file.isDirectory()) {
				getFiles(path,typeName);
			} else {
				if (path.endsWith(typeName)) {
					filelist.add(path);
				} else {
					System.out.println("不在处理范围内的文件：" + path);
				}
			}
		}
	}

	/**
	 * 将信息保存到文件中
	 * @param outFolder
	 * @param buffer
	 */
	public static void saveInfo(String outFolder, StringBuffer buffer) {
		String infoFile = outFolder + "\\记录.txt";
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(infoFile));
			out.write(buffer.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(out!=null){
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 延迟操作
	 * @param time
	 */
	public static void delayMonent(int time) {
		try {
			Thread.sleep(1000*time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
