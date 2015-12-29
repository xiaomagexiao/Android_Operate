package com.mage.function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mage.util.CommonUtil;

/**
 * 删除没用到的资源文件，主要是layout、png图片。
 * 
 * @author 马彦君
 * 
 */
public class DeleteUnUsefulFile {
	
	//有用的layout集合
	private static List<String> layoutList = new ArrayList<String>();

	//有用的png集合
	private static List<String> picList = new ArrayList<String>();

	public static void startDlete(String basePath) {
		// 解析类文件
		parseJavaRes(basePath + "\\src");

		//layout中include的情况
		List<String> fileList =  getFileListByType(basePath + "\\res\\layout\\", ".xml");
		for (String str : fileList) {
			testParse(str, "@layout/", layoutList);
		}
		
		// 删除layout， 这个放在前面，方便后面删除drawable
		deleteUnUseRes(basePath + "\\res\\layout\\", ".xml", layoutList);

		// 解析xml文件
		parseXmlRes(basePath + "\\res\\");

		// 删除png
		String[] imgTypes = new String[] { "drawable", "drawable-hdpi", "drawable-mdpi", "drawable-xhdpi", "drawable-xxhdpi" };
		for (String str : imgTypes) {
			deleteUnUseRes(basePath + "\\res\\" + str + "\\", ".png", picList);
		}

		System.out.println("圆满完成任务！！！");
	}

	private static List<String> getFileListByType(String dirPath, String type) {
		CommonUtil.filelist.clear();
		CommonUtil.getFiles(dirPath, type);
		return CommonUtil.filelist;
	}

	/**
	 * 解析类文件里面的layout和drawable
	 * 
	 * @param path
	 */
	private static void parseJavaRes(String path) {
		// 获得到需要处理的类文件
		List<String> fileList =  getFileListByType(path, ".java");

		for (String str : fileList) {
			testParse(str, "R.layout.", layoutList);
			testParse(str, "R.drawable.", picList);
		}
	}

	/**
	 * 解析xml文件里面的 drawable
	 * 
	 * @param path
	 */
	private static void parseXmlRes(String folderPath) {
		
		List<String> fileList =  getFileListByType(folderPath + "drawable", ".xml");
		for (String str : fileList) {
			testParse(str, "@drawable/", picList);
		}

		fileList =  getFileListByType(folderPath + "layout", ".xml");
		for (String str : fileList) {
			testParse(str, "@drawable/", picList);
		}

		fileList =  getFileListByType(folderPath + "values", ".xml");
		for (String str : fileList) {
			testParse(str, "@drawable/", picList);
		}

	}

	/**
	 * 删除无用资源
	 * @param path
	 * @param type
	 * @param orgList
	 */
	private static void deleteUnUseRes(String path, String type, List<String> orgList) {

		//某个目录下所有的指定类型文件
		List<String> fileList = getFileListByType(path, type);
		for (String str : orgList) {
			String fileName = path + str + type;
			if (fileList.contains(fileName)) {
				fileList.remove(fileName);
			}
		}

		for (String str : fileList) {
			System.out.println("没有用到的资源文件：" + str);
			File file = new File(str);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * 解析指定文件， 将符合条件的资源名字放到对应的集合中
	 * @param file
	 * @param condition
	 * @param addList
	 */
	private static void testParse(String file, String condition, List<String> addList) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			String s = null;

			List<String> lineList = new ArrayList<String>();
			while ((s = in.readLine()) != null) {
				if (s.contains(condition)) {
					lineList.add(s);

					int start = s.indexOf(condition);
					//一行里面有2个资源的话会有一个取不到。
					String str = splitSrcName(start, s, condition);// 截取资源名字
					System.out.println(condition + str);
					addList.add(str);
					if (condition.contains("drawable")) {
						addList.add(str + ".9");
					}
				}

			}
			in.close();
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
		}
	}

	/**
	 * 截取资源名字
	 * @param start
	 * @param s
	 * @param condition
	 * @return
	 */
	private static String splitSrcName(int start, String s, String condition) {
		start = start + condition.length();
		int end = start;
		String ret = "";
		try {
			while (true) {
				char temp = s.charAt(end);
				if (temp == ',' || temp == ')' || temp == ' ' || temp == ';' || temp == '"' || temp == '<' || temp == '}') {
					break;
				}
				end++;
			}
			ret = s.substring(start, end);
		} catch (Exception e) {
			System.out.println(s + "截取资源名字的时候报错了");
		}
		return ret;
	}

}
