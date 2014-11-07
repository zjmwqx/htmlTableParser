package htmlAndExcelDataParser.dataStructure;

import java.util.Arrays;
import java.util.HashSet;

public class UnitData extends StringData{
	private static final String[] UnitNameList = {"亿吨","亿吨公里","亿人","亿人公里","亿吨","万人公里","万人","万吨公里",
													"万吨","小时/日","%","万TEU","万元"};
	
	public static final HashSet<String> UnitNameSet = new HashSet(Arrays.asList(UnitNameList));
}
