package com.mage.main;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mage.util.CommonUtil;

/**
 * 自己项目用的时候需要修改   targetColor dir
 * @author 马彦君
 *
 */
public class UpdateMain {
	public static int count = 0;
	public static int grayDeep = 0;
	
	//资源文件路径  要处理依赖库项目的和播放器项目的。
	//public static String  orgDir = "E:\\Android\\workspace\\whatymedia\\res";
	public static String orgDir = "E:\\Android\\workspace\\mooc-plugin\\res";
	
	
	public static String targetDir = "C:\\Users\\whaty\\Desktop\\mooc-main-color\\res";

	public static void main(String args[]) {

		//要替换的颜色值
		int[] orgColor = new int[3];
		orgColor[0] = 0x4B;
		orgColor[1] = 0xB2;
		orgColor[2] = 0x50;

		// 要替换成的颜色值
		int targetColor = 0x333333;

		grayDeep = (orgColor[0] * 77 + orgColor[1] * 151 + orgColor[2] * 28) >> 8;
		System.out.println("原图灰度：" + grayDeep);
		
	
		//寻找文件夹下的所有png图片，结果存放在 CommonUtil.filelist 里面
		CommonUtil.getFiles(orgDir, ".png");

		for (String str : CommonUtil.filelist) {
			//处理后的资源文件路径
			 String targetFilePath = str.replace(orgDir, targetDir);
			updateColor(str, targetFilePath, orgColor, targetColor);
		}

	}

	private static void updateColor(String inputFile, String outputFile, int[] line, int targetColor) {
		int[] rgb = new int[3];
		File file = new File(inputFile);
		String writeImageFormat = "png";
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int width = bi.getWidth();
		int height = bi.getHeight();
		int minx = bi.getMinX();
		int miny = bi.getMinY();

		//输出灰度图，  调试的时候用
		if(false){
			String name = outputFile.substring(outputFile.lastIndexOf("."));
			String newPath = outputFile.replace(name, "");
			name = "_bak" + name;
	
			File file2 = new File(newPath + name);
			try {
				file2.createNewFile();
				ImageIO.write(getGrayPicture(bi), writeImageFormat, file2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int countPxl = 0;
		for (int i = minx; i < width; i++) {
			for (int j = miny; j < height; j++) {
				int pixel = bi.getRGB(i, j);
				rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = (pixel & 0xff);
				int deep = (pixel & 0xff000000) >> 24;

				if (checkBetween(rgb, line)) {
					int result = (deep << 24) + targetColor;
					 bi.setRGB(i, j, result);
				} else {
					countPxl++;
				}
			}
		}
		System.out.println(outputFile + "非目标区域占据的百分比为： " + countPxl * 1.00 / (width * height));
		File file1 = new File(outputFile);
		file1.getParentFile().mkdirs();
		try {
			file1.createNewFile();
			ImageIO.write(bi, writeImageFormat, file1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("处理完成第" + ++count + "张！");
	}

	/**
	 * 按每个颜色值进行比对
	 * @param rgb
	 * @param line
	 * @return
	 */
	private static boolean checkBetween(int[] rgb, int[] line) {
		int max = 0x38;//多次尝试， 取这个值最好。
		if (rgb[0] - line[0] > max || rgb[0] - line[0] < -max) {
			return false;
		}
		if (rgb[1] - line[1] > max || rgb[1] - line[1] < -max) {
			return false;
		}
		if (rgb[2] - line[2] > max || rgb[2] - line[2] < -max) {
			return false;
		}

		return true;
	}

	/**
	 * 按照灰度进行比较
	 * @param rgb
	 * @param line
	 * @return
	 */
	private static boolean checkBetween2(int[] rgb, int[] line) {
		int max = 0x20;

		int temp = (rgb[0] * 77 + rgb[1] * 151 + rgb[2] * 28) >> 8;
		if (temp - grayDeep > max || temp - grayDeep < -max) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 彩色图片转换为灰度图片
	 * 
	 * @param originalImage
	 * @return
	 */
	public static BufferedImage getGrayPicture(BufferedImage originalImage) {
		BufferedImage grayPicture;
		int imageWidth = originalImage.getWidth();
		int imageHeight = originalImage.getHeight();
		grayPicture = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
		ColorConvertOp cco = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		cco.filter(originalImage, grayPicture);
		return grayPicture;
	}
}
