package htmlAndExcelDataParser;

import htmlAndExcelDataParser.log.LoggerUtil;
import htmlAndExcelDataParser.model.Table;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetCellDemo {
	static int fileNo = 0;
	static Logger logger;
	public static void directory(String URL) throws Exception {
		File file = new File(URL);// 读取文件地址
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {// 判断是否有子目录，如果有，就调用自己，没有就直接输出文件

				directory(files[i].toString());
			} else {
				File f = files[i];
				System.out.println(f.getName()); // 文件名
				if (f.getName().endsWith(".html")
						|| f.getName().endsWith(".htm")) {
					++fileNo;
					logger.log(Level.INFO, "No." + fileNo + ": " + f.getName()
							+ " is Processing...");
					List<Table> tbList = GetTableCellFromHtml.readHtml(f);
				} else if (f.getName().endsWith(".xls")
						|| f.getName().endsWith(".xlsx")) {
					++fileNo;
					logger.log(Level.INFO, "No." + fileNo + ": " + f.getName()
							+ " is Processing...");
					List<Table> tbList = GetTableCellFromExcel.readExcel(f);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		logger = Logger.getLogger("parser");
		try {
			LoggerUtil.setLogingProperties(logger);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		directory("Data");
		//directory(args[0]);
		System.out.println(fileNo);
	}
}
