package htmlAndExcelDataParser;

import htmlAndExcelDataParser.dataStructure.Data;
import htmlAndExcelDataParser.dataStructure.DateData;
import htmlAndExcelDataParser.dataStructure.NumericData;
import htmlAndExcelDataParser.dataStructure.StringData;
import htmlAndExcelDataParser.model.Table;
import htmlAndExcelDataParser.model.CellData;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GetTableCellFromExcel {
	public static List<Table> readExcel(File file) throws IOException {
		String fileName = file.getName();
		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName
				.substring(fileName.lastIndexOf(".") + 1);
		List<Table> tableList = new ArrayList<Table>();
		if ("xls".equals(extension)) {
			tableList.add(read2003Excel(file));
		} else if ("xlsx".equals(extension)) {
			tableList.add(read2007Excel(file));
		} else {
			throw new IOException("不支持的文件类型");
		}
		return tableList;
	}

	/**
	 * 读取 office 2003 excel
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static Table read2003Excel(File file)
			throws IOException {
		List<List<Object>> list = new LinkedList<List<Object>>();
		HSSFWorkbook hwb = new HSSFWorkbook(new FileInputStream(file));
		HSSFSheet sheet = hwb.getSheetAt(0);
		Object value = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		Table table = new Table();
		Set<HSSFCell> cellsInRegion = new HashSet<HSSFCell>();
		Data data;
		for (int i = 0; i < sheet.getNumMergedRegions(); ++i)
		{
			CellRangeAddress range = sheet.getMergedRegion(i);
			int rowst = range.getFirstRow();
			int colst = range.getFirstColumn();
			int rowed = range.getLastRow();
			int coled = range.getLastColumn();
			StringBuilder content = new StringBuilder();
			for(int j = rowst; j<= rowed; ++j)
			{
				for(int k = colst; k<= coled; ++k)
				{
					cell = sheet.getRow(j).getCell(k);
					cellsInRegion.add(cell);
					if (cell == null) {
						continue;
					}
					if(cell.getCellType() == Cell.CELL_TYPE_STRING)
					{
						content.append(cell.getRichStringCellValue()
								.getString().trim().replaceAll("　|(//s*)", ""));
					}
				}	
			}
			if(content.length() > 0)
			{
				data = new StringData(content.toString());
				table.getCells().put(new Point(rowst,colst), 
						new CellData(rowst, colst, coled-colst+1, rowed-rowst+1, data));
//				System.out.println(content.toString() + rowst+" "+colst
//						+" "+(coled-colst+1)+""+(rowed-rowst+1));
			}
		}
		for (int i = sheet.getFirstRowNum(); i <= sheet
				.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			List<Object> linked = new LinkedList<Object>();
			for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
				cell = row.getCell(j);
				if (cell == null) {
					continue;
				}
				if(cellsInRegion.contains(cell))
				{
					continue;
				}
				
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					String sdata =  cell.getRichStringCellValue().getString().trim().replaceAll("　|(\\s*)", "");
					data = new StringData(sdata);
					table.getCells().put(new Point(i,j), new CellData(i, j, 1, 1, data));
					//System.out.println(sdata);
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						data = new DateData(cell.getDateCellValue());
						table.getCells().put(new Point(i,j), new CellData(i, j, 1, 1, data));
						//System.out.println(cell.getDateCellValue());
					} else {
						data = new NumericData(cell.getNumericCellValue());
						table.getCells().put(new Point(i,j), new CellData(i, j, 1, 1, data));
						//System.out.println(cell.getNumericCellValue());
					}
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					data = new NumericData(cell.getNumericCellValue());
					table.getCells().put(new Point(i,j), new CellData(i, j, 1, 1, data));
					//System.out.println(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					
					//System.out.println(cell.getCellFormula());
					break;
				default:
					//System.out.println();
				}
			}
		}
		return table;
		//return list;
	}

	/**
	 * 读取Office 2007 excel
	 * */
	private static Table read2007Excel(File file)
			throws IOException {
		List<List<Object>> list = new LinkedList<List<Object>>();
		// 构造 XSSFWorkbook 对象，strPath 传入文件路径
		XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file));
		// 读取第一章表格内容
		XSSFSheet sheet = xwb.getSheetAt(0);
		Object value = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		Table table = new Table();
		Set<XSSFCell> cellsInRegion = new HashSet<XSSFCell>();
		Data data;
		for (int i = 0; i < sheet.getNumMergedRegions(); ++i)
		{
			CellRangeAddress range = sheet.getMergedRegion(i);
			int rowst = range.getFirstRow();
			int colst = range.getFirstColumn();
			int rowed = range.getLastRow();
			int coled = range.getLastColumn();
			StringBuilder content = new StringBuilder();
			for(int j = rowst; j<= rowed; ++j)
			{
				for(int k = colst; k<= coled; ++k)
				{
					cell = sheet.getRow(j).getCell(k);
					cellsInRegion.add(cell);
					if (cell == null) {
						continue;
					}
					if(cell.getCellType() == Cell.CELL_TYPE_STRING)
					{
						content.append(cell.getRichStringCellValue()
								.getString().trim().replaceAll("　|(\\s*)", ""));
					}
				}	
			}
			if(content.length() > 0)
			{
				data = new StringData(content.toString());
				table.getCells().put(new Point(rowst,colst), 
						new CellData(rowst, colst, coled-colst+1, rowed-rowst+1, data));
				//System.out.println(content.toString() + rowst+" "+colst
				//		+" "+(coled-colst+1)+""+(rowed-rowst+1));
			}
		}
		for (int i = sheet.getFirstRowNum(); i <= sheet
				.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			//List<Object> linked = new LinkedList<Object>();
			for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
				
				cell = row.getCell(j);
				if (cell == null) {
					continue;
				}
				if(cellsInRegion.contains(cell))
				{
					continue;
				}
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					String sdata = cell.getRichStringCellValue().getString().trim().replaceAll("　|\\s*", "");
					data = new StringData(sdata);
					table.getCells().put(new Point(i,j), new CellData(i, j, 1, 1, data));
					//System.out.println(sdata);
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						data = new DateData(cell.getDateCellValue());
						table.getCells().put(new Point(i,j), new CellData(i, j, 1, 1, data));
						//System.out.println(cell.getDateCellValue());
					} else {
						data = new NumericData(cell.getNumericCellValue());
						table.getCells().put(new Point(i,j), new CellData(i, j, 1, 1, data));
						//System.out.println(cell.getNumericCellValue());
					}
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					data = new NumericData(cell.getNumericCellValue());
					table.getCells().put(new Point(i,j), new CellData(i, j, 1, 1, data));
					//System.out.println(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					//System.out.println(cell.getCellFormula());
					break;
				default:
					//System.out.println();
				}
			}
		}
		return table;
	}
}