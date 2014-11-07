package htmlAndExcelDataParser;

import htmlAndExcelDataParser.dataStructure.Data;
import htmlAndExcelDataParser.dataStructure.NumericData;
import htmlAndExcelDataParser.dataStructure.StringData;
import htmlAndExcelDataParser.model.CellData;
import htmlAndExcelDataParser.model.Table;

import java.util.Iterator;
import java.util.List;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetTableCellFromHtml {
	/**
	 * 获得页面字符
	 */
	public static String matchCharset(String content) {
		String chs = "gb2312";
		Pattern p = Pattern.compile("(?<=charset=)(.+)(?=\")");
		Matcher m = p.matcher(content);
		if (m.find())
			return m.group();
		return chs;
	}

	/**
	 * 获得字符集
	 */
	public static String getCharset(File input) throws Exception {
		Document doc = Jsoup.parse(input, "utf-8");
		Elements eles = doc.select("meta[http-equiv=Content-Type]");
		Iterator<Element> itor = eles.iterator();
		while (itor.hasNext())
			return matchCharset(itor.next().toString());
		return "gb2312";
	}

	public static List<Table> readHtml(File input) throws Exception {
		List<Table> tableList = new ArrayList<Table>();
		String charset = getCharset(input);
		Document doc = Jsoup.parse(input, charset);
		// 选择table元素
		Elements tableElements = doc.select("table");
		// 选择行元素
		int totalConsideredTable = 0;
		for (int tab = 0; tab < tableElements.size(); tab++) {
			// 是最小table，排除递归包含
			Elements tableJudgeElements = tableElements.get(tab)
					.select("table");
			if (tableJudgeElements.size() > 1)
				continue;
			// 包含2行以上才能称之为表结构，行结构一般都是mso-yfti-irow到目前为止是这样的。
			tableJudgeElements = tableElements.get(tab).select("tr");
			if (tableJudgeElements.size() < 2)
				continue;
			tableJudgeElements = tableElements.get(tab).select("td");
			if (tableJudgeElements.size() < 2)
				continue;
			// tableJudgeElements =
			Table table = new Table();
			Elements tableRowElements = tableElements.get(tab).select("tr");
			/**
			 * 以下算法独立：把html的tab中的单元格对应到table中相对坐标（x,y），并保存单元格的横纵跨度。
			 * 处理了rowspan,colspan属性。对任何table都适用
			 */
			int[] colBeTaken = new int[1000];// 请充分理解这个数组内的数据，记录当前可以填充的最小行号，小于此行号的行的当前列已经被span占据
			for (int i = 0; i < tableRowElements.size(); i++) {
				Element row = tableRowElements.get(i);
				//System.out.println("row");
				Elements rowItems = row.select("td");
				int nextCol = 0;
				for (int j = 0; j < rowItems.size(); j++) {
					String content = rowItems.get(j).text();
					// 去除&nbsp
					// content = new
					// String(content.getBytes()).replace("?","").replace("　","");
					content = content.replaceAll("\\s*", "");
					content = content.replaceAll("　", "");
					// content = content.replace("\u3000","");
					content = content.replace(Jsoup.parse("&nbsp").text(), "");
					content = content.trim();
//					if (content.length() > 2) {
//						char c = content.charAt(1);
//						System.out.println((int) c);
//					}
					
					// 得到rowspan 和 cowspan
					int rowspan = 1;
					Element block = rowItems.get(j);
					if (!block.attr("rowspan").equals(""))
						rowspan = Integer.valueOf(block.attr("rowspan"));
					int colspan = 1;
					if (!block.attr("colspan").equals(""))
						colspan = Integer.valueOf(block.attr("colspan"));
					// 当前单元格大小

					//System.out.println(rowspan + " " + colspan);
					// 当前的起始纵坐标
					int rowidx = i;
					int colidx = nextCol;
					// 正则表达式判断是否是number，还是String，还是Data
					Pattern pattern = Pattern
							.compile("^[-+]?(([1-9]\\d*(\\.[0-9]+)?)|([1-9]\\d*(,\\d{3})*)|(0\\.\\d+))");
					Data data;
					if (pattern.matcher(content).matches()) {
						content = content.replace(",", "");
						data = new NumericData(Double.valueOf(content));
						//System.out
						//		.println("numeric: " + rowidx + ", " + colidx);
					} else
					{
						data = new StringData(content);
						System.out.print("table " + totalConsideredTable + ": "
								+ content + ",");
					}
					while (colBeTaken[colidx] > i) {
						colidx++;
					}
					table.getCells()
							.put(new Point(rowidx, colidx),
									new CellData(rowidx, colidx, colspan,
											rowspan, data));

					System.out.println(rowidx + " " + colidx);
					colBeTaken[colidx] = i;
					// 利用colspan,rowspan 跟新colBeTaken
					int k;
					for (k = colidx; k < colidx + colspan; ++k) {
						colBeTaken[k] = i + rowspan;
					}
					nextCol = k;
				}
			}
			totalConsideredTable++;
			tableList.add(table);
		}
		//System.out.println(" html parse over");
		return tableList;
	}

}
