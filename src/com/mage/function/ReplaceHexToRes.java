package com.mage.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mage.util.CommonUtil;
import com.mage.util.ParseXMLUtil;
import com.mage.util.UpdateUtil;

public class ReplaceHexToRes {
	private static List<String> filelist = new ArrayList<String>();
	private static Set<String> fileSet = new HashSet<String>();
	
	public static void startReplace(String basePath) {

		final String xmlPath = basePath + "\\res\\values\\public.xml";
		final String folderPath = basePath + "\\src";

		File file = new File(xmlPath);
		if (!file.exists()) {
			System.out.println(" 请确认项目中包含public.xml文件！！！ ");
			System.out.println(" 注意：要把项目的根路径作为第一个参数进行传递才可以！！！ ");
			return;
		}
		
		//获得到需要处理的类文件
		CommonUtil.getFiles(folderPath, ".java");
		filelist = CommonUtil.filelist;
		System.out.println("要处理的文件数量为：" + filelist.size());
		System.out.println();
		
		
		//解析public.xml，获得名字和十六进制值间的关系
		final Map<String, String[]> map2 = ParseXMLUtil.parsePublicXml(xmlPath);
		System.out.println("解析public.xml获得到的条目数为：" + map2.keySet().size());
		
		//解析ManifestXml ，获得包名字。
		final String packageName = ManifestXmlUtil.getPackageName(basePath);
		System.out.println("获得到的包名为："+packageName);
		
		System.out.println("开始进行处理………………");
		CommonUtil.delayMonent(2);
		final String outFolder = basePath + "\\src_dec";
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		List<String> list3 = new ArrayList<String>();
		for (int i=0;i<filelist.size();i++) {
			if(i<filelist.size()/3){
				list1.add(filelist.get(i));
				continue;
			}else if(i<filelist.size()*2/3){
				list2.add(filelist.get(i));
			}else{
				list3.add(filelist.get(i));
			}
		}
		
		final List<String> fList1 = list1;
		final List<String> fList2 = list2;
		final List<String> fList3 = list3;
		
		Thread t1 = startThreadAndRun(outFolder, fList1, map2,packageName);
		Thread t2 = startThreadAndRun(outFolder, fList2, map2,packageName);
		Thread t3 = startThreadAndRun(outFolder, fList3, map2,packageName);
		while(true){
			if(!t1.isAlive()&&!t2.isAlive()&&!t3.isAlive()){
				break;
			}else{
				CommonUtil.delayMonent(3);
			}
		}
		System.out.println("信息保存到文件……");
		CommonUtil.delayMonent(2);
		
		//保存记录到文件
		StringBuffer sb = new StringBuffer();
		sb.append("修改过id的文件共"+fileSet.size()+"个，包括：\r\n");
		for(String str:fileSet){
			sb.append(str + "\r\n");
		}
		
		CommonUtil.saveInfo(outFolder, sb);
		
		System.out.println("圆满完成任务！！！");
	}

	private static Thread startThreadAndRun(final String outFolder,
			final List<String> fList,final Map<String, String[]> map2,
			final String packageName) {
		Thread t =new Thread(new Runnable() {
			@Override
			public void run() {
				for(String str : fList){
					testParse(outFolder, str, map2, packageName);
				}
			}
		});
		t.start();
		return t;
	}

	private static void testParse(String newPath, String file,
			Map<String, String[]> map,String packageName) {
		BufferedReader in = null;
		BufferedWriter out = null;
		String uri = file.substring(file.lastIndexOf("\\src\\") + 4);
		String outPath = newPath + uri;
		System.out.println(outPath);
		try {
			File outFile = new File(outPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			in = new BufferedReader(new FileReader(file));
			out = new BufferedWriter(new FileWriter(outPath));
			String s = null;
			Set<String> keySet = map.keySet();
			
			
		
			List<String> lineList = new ArrayList<String>();
			while ((s = in.readLine()) != null) {
				lineList.add(s);
				
			}
			StringBuffer sb = UpdateUtil.updateMain(lineList,
					file, map, packageName,keySet,fileSet);
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

	

}
