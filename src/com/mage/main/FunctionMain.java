package com.mage.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.mage.function.DeleteUnUsefulFile;
import com.mage.function.ReplaceHexToRes;

public class FunctionMain {
	private final static Options allOptions;
	private static final String VERSION = "1.0.0";
	static {
		allOptions = new Options();
	}

	public static void main(String[] args) {
		args = new String[] { "d", "-b","E:\\Android\\studio\\DDB\\mooc-plugin" };
		// 对输入参数的处理
		if (args == null || args.length == 0) {
			System.out.println(" 请传递项目根路径再试！ ");
			return;
		}

		// cli parser
		CommandLineParser parser = new PosixParser();
		CommandLine commandLine = null;

		// load options
		_Options();

		try {
			commandLine = parser.parse(allOptions, args, false);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			usage(commandLine);
			return;
		}

		boolean cmdFound = false;
		for (String opt : commandLine.getArgs()) {
			if (opt.equalsIgnoreCase("r") || opt.equalsIgnoreCase("replace")) {
				cmdReplace(commandLine);
				cmdFound = true;
			} else if (opt.equalsIgnoreCase("d") || opt.equalsIgnoreCase("delete")) {
				cmdDelete(commandLine);
				cmdFound = true;
			}
		}

		// if no commands ran, run the version / usage check.
		if (cmdFound == false) {
			if (commandLine.hasOption("version") || commandLine.hasOption("version")) {
				_version();
			} else {
				usage(commandLine);
			}
		}
	}

	private static void cmdReplace(CommandLine cli) {
		String basePath = getOptBasePath(cli);
		ReplaceHexToRes.startReplace(basePath);
	}

	private static void cmdDelete(CommandLine cli) {
		String basePath = getOptBasePath(cli);
		DeleteUnUsefulFile.startDlete(basePath);
	}

	private static String getOptBasePath(CommandLine cli) {
		int paraCount = cli.getArgList().size();
		// String apkName = (String) cli.getArgList().get(paraCount - 1);
		String basePath = "";
		if (cli.hasOption("b") || cli.hasOption("base")) {
			basePath = cli.getOptionValue("b");
		}
		return basePath;
	}

	private static void _version() {
		System.out.println(VERSION);
	}

	@SuppressWarnings("static-access")
	private static void _Options() {

		Option replaceOption = OptionBuilder.withLongOpt("替换类中的十六进制为resId")
				.withDescription("Decode in debug mode. Check project page for more info.").create("r");

		Option deleteOption = OptionBuilder.withLongOpt("删除无用资源")
				.withDescription("Keeps files to closest to original as possible. Prevents rebuild.").create("d");

        Option basePathDecOption = OptionBuilder.withLongOpt("要处理的文件夹")
                .withDescription("The name of folder that gets written. Default is apk.out")
                .hasArg(true)
                .withArgName("dir")
                .create("b");

		allOptions.addOption(replaceOption);
		allOptions.addOption(deleteOption);
		allOptions.addOption(basePathDecOption);
	}

	private static void usage(CommandLine commandLine) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(120);
		System.out.println("输入内容不正确，请仔细检查！");
	}
}
