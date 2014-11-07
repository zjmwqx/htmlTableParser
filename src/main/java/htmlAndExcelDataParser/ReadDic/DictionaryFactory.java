package htmlAndExcelDataParser.ReadDic;

public class DictionaryFactory {
	private static Dictionary dictionary;
	
	public static Dictionary getDictionary()
	{
		if (dictionary == null) {
			dictionary = new Dictionary();
		}
		return dictionary;
	}

}
