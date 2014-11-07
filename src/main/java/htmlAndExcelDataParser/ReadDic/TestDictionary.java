package htmlAndExcelDataParser.ReadDic;

import java.util.List;


public class TestDictionary {
	
	 public static void main(String[] args) {
		 try{
			 Dictionary dic = DictionaryFactory.getDictionary();
			 //String file = "D:\\download\\dictionary0.3.txt";
			// dic.init(file);
	//		 String dicFile = args[0];
//			 dic.init(dicFile);
		//	 dic.dump();
			 List<Item> DBItemList = dic.getTableItemListByTableName("公路旅客运输量");
			 for (int i = 0; i < DBItemList.size(); i++) {
				System.out.println(DBItemList.get(i));
			}
			 

		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
}
