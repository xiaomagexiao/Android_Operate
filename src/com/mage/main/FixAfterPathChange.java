package com.mage.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mage.util.CommonUtil;

/**
 * 自己项目用的时候需要修改 targetColor dir
 * 
 * @author 马彦君
 * 
 */
public class FixAfterPathChange {

	public static String orgDir = "E:\\Android\\studio\\DDB\\pen\\src\\main\\java";
	public static Map<String, String> map = new HashMap<String, String>();

	public static void main(String args[]) {

		// 寻找文件夹下的所有png图片，结果存放在 CommonUtil.filelist 里面
		CommonUtil.getFiles(orgDir, ".java");

		for (String str : CommonUtil.filelist) {
			String path = str.substring(str.indexOf("\\src\\main\\java") + 15);
			path = path.replace(".java", "").replace("\\", ".");
			map.put(path.substring(path.lastIndexOf(".") + 1), path.substring(0, path.lastIndexOf(".")));
		}

		for (String str : map.keySet()) {
			System.out.println(str + " - " + map.get(str));
		}

		for (String str : CommonUtil.filelist) {

			fixFile(str);
		}
	}

	private static void fixFile(String filePath) {

		String fileName = filePath.substring(filePath.lastIndexOf("\\")+1).replace(".java", "");
		BufferedReader in = null;
		BufferedWriter out = null;
		String outPath = filePath.replace("\\src\\main\\java", "\\src\\main\\java-bak");
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				System.out.println(filePath + "不存在");
				return;
			}
			in = new BufferedReader(new FileReader(file));

			File outFile = new File(outPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			out = new BufferedWriter(new FileWriter(outPath));

			String s = null;

			String endLab = "\r\n";
			StringBuffer sb = new StringBuffer();
			while ((s = in.readLine()) != null) {
				boolean find = false;
				// package com.whaty.penserver.bean;
				if (s.contains("package ")) {
					String newPack = "package " + map.get(fileName) + ";";
					if(!newPack.equals(s)){
						System.out.println("找到变化的package " + s + " -> " + newPack);
						sb.append(newPack + endLab);
						find = true;
					}
				} else if (s.contains("import ")) {
					String key = checkEnd(s);
					if (key != null) {
						String newImport = "import " + map.get(key) + "." + key + ";";
						if(!newImport.equals(s)){
							System.out.println("找到变化的import " + s + " -> " + newImport);
							sb.append(newImport + endLab);
							find = true;
						}
					}
				}

				if (!find) {
					sb.append(s + endLab);
				}
			}

			out.write(sb.toString());
			in.close();
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String checkEnd(String s) {
		Set<String> keySet = map.keySet();
		for (String str : keySet) {
			if (s.endsWith(str + ";")) {
				return str;
			}
		}
		return null;
	}
}
