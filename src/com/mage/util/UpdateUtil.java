package com.mage.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdateUtil {

	/**
	 * 更新 return 0 这种， 改成return true;
	 * 
	 * @param content
	 */
	public static String updateReturn(String str) {
		if (str.contains("return 0;")) {
			str = str.replace("return 0;", "return false;");
		} else if (str.contains("return 1;")) {
			str = str.replace("return 1;", "return true;");
		}
		return str;
	}

	/**
	 * 更新 this.mLoadingView.setVisibility(View.GONE);
	 * 
	 * @param str
	 */
	public static String updateVisable(String str) {
		if (str.contains(".setVisibility(")) {
			if (str.endsWith("(0);")) {
				str = str.replace("(0);", "(View.VISIBLE);");
			} else if (str.endsWith("(4);")) {
				str = str.replace("(4);", "(View.INVISIBLE);");
			} else if (str.endsWith("(8);")) {
				str = str.replace("(8);", "(View.GONE);");
			}
		}
		return str;
	}

	/**
	 * this.mEndTime = (TextView)
	 * v.findViewById(R.id.mediacontroller_time_total)
	 * 
	 * @param map
	 * @param content
	 */
	public static String updateFindViewById(String str, Map<String, String> map) {
		String coreStr = "findViewById(R.";
		// 有需要处理的才处理，buffer 只负责查找
		if (str.indexOf(coreStr) > 0) {
			Set<String> set = map.keySet();
			if (str.contains(coreStr)) {
				String key = getProperties(str);
				if (set.contains(key) && map.get(key) != null) {
					str = str.replace("= ", "= (" + map.get(key) + ")");// 添加强转
				}
			}
		}
		return str;
	}

	private static String getProperties(String str) {
		int first = str.indexOf(".");
		int second = str.indexOf("= ");
		if(first>=0&&second>=0&&second>first){
			String ret = str.substring(first + 1, second);
			return ret.trim();
		}else{
			return null;
		}
	}

	private static Map<String, String> createProperties(List<String> content) {
		// private RelativeLayout duration_layout;
		Map<String, String> map = new HashMap<String, String>();
		for (String str : content) {
			str = str.trim();
			if (str.contains("private ") && str.endsWith(";")) {
				String temp = str.substring(str.indexOf("private ") + 8,
						str.indexOf(";"));
				String arr[] = temp.split(" ");
				map.put(arr[1].trim(), arr[0].trim());
			}
		}
		return map;
	}

	/**
	 * 更新 
	 * 
	 * @param content
	 */
	public static void updateBase(List<String> content, StringBuffer buffer) {

	}

	/**
	 * 更新 this.mLoadingView.setVisibility(View.GONE);
	 * 
	 * @param content
	 */
	public static StringBuffer updateMain(List<String> content, String file,
			Map<String, String[]> map, String packageName, Set<String> keySet,
			Set<String> fileSet) {

		Map<String, String> mapProperties = createProperties(content);
		// 获取package那行
		String packageDef = "";
		String endLab = "\r\n";
		StringBuffer sb = new StringBuffer();
		boolean haveR = false;
		for (String s : content) {
			if (s.startsWith("package ")) {
				packageDef = s;
				continue;
			}
			
			// import cn.com.open.mymooc.R;
			if (s.contains("import ") && s.contains(".R;")) {
				continue;
			}
			
			// 以 /* 开头的不处理
			if (s.startsWith("/*") || s.startsWith("//") ) {
				continue;
			}
			
			// 对反编译出来的内部类的处理。
			if (s.contains("$")) {
				s = s.replace("$", ".");
				s = s.replace("access.", "access$");
			}

			//处理id情况
			String[] arr;
			for (String str : keySet) {
				if (s.contains(str)) {
					arr = map.get(str);
					s = s.replace(str, "R." + arr[0] + "." + arr[1]);
					fileSet.add(file);
					haveR = true;
				}
			}

			//if(s.contains("public class MyMoocFragment extends Fragment")){
				//System.out.println("111");
			//}
			// 返回true false的处理
			s = updateReturn(s);

			// 设置视图可见形式的处理
			s = updateVisable(s);

			// FindViewByid的强转处理
			s = updateFindViewById(s, mapProperties);

			sb.append(s + endLab);
		}

		if(haveR){
			sb.insert(0, "import " + packageName + ".R;" + endLab);
		}
		sb.insert(0, packageDef + endLab + endLab);
		return sb;
	}
}
