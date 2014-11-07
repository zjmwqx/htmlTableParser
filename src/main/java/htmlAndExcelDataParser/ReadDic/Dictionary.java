package htmlAndExcelDataParser.ReadDic;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;





/*dictionary data model
 * @author xiaoling.qin
 * */
public class Dictionary {
private	String version;
private	Map<String,Item> englishDictionary;
private	Map<String,Item> localDictionary;
private	Map<String,Item> encodingDictionary;
private Map<String, Map> reverseLocalDictionary;
private int timestamp;
private String[] metaData;
private Map<String,List<Item> > tableListMap;


public Dictionary()
{
	this.englishDictionary = new HashMap<String, Item>();
	this.localDictionary = new HashMap<String, Item>();
	this.encodingDictionary = new HashMap<String, Item>();
	this.reverseLocalDictionary = new HashMap<String, Map>();
	this.tableListMap = new HashMap<String,List<Item>>();
	init("/dbDic.txt");
	
}
public List<String> searchLocaNameByEndWithKey(String tableName, String sourceString){
	List<String> stringList = new ArrayList<String>();
	if(tableListMap.containsKey(tableName)){
		List<Item> itemList = tableListMap.get(tableName);
		for(int i=0; i<itemList.size(); i++){
			Item keyItem = itemList.get(i);
			Map<String, Integer> synonymMap = keyItem.getSynonyms();
			for(Entry<String, Integer> entry : synonymMap.entrySet()){
				if(sourceString.endsWith(entry.getKey())){
					stringList.add(keyItem.getLocalName());
					break;
				}
			}
		}
	}
	return stringList;
}
public void setTimestamp(int timestamp)
{
	this.timestamp = timestamp;
}
public int getTimestamp()
{
	return this.timestamp;
}
public void setVersion(String version)
{
	this.version = version;
}
public String getVersion()
{
	return this.version;
}

/**
 * @param file is the filename including the directory
 * @return 0 means success
 * @return 1 means fail
 * @author xiaoling.qin
  */
private int init(String file) 
{
	//System.out.println();
	//System.out.println("start load file..");
	try
	{
		 //System.out.println("start load file..");
		 //System.out.println(file);
		 DataInputStream in = new DataInputStream(Dictionary.class.getResourceAsStream(file));
		 //System.out.println(in.toString());
		 BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
		 //System.out.println(br.toString());
		 //get the version info
		 //String versionLine = br.readLine();
//		 String version = parseVersion(versionLine);
//		 if(version == null){
//			 System.out.println("first line should be the version"); 
//			 return -1;
//		 }else{
//			 setVersion(version);
//		 }
//		 System.out.println("version:"+this.getVersion());
	
		 //get the meta data

		 String metaDataLine = br.readLine();
		 //System.out.println("here dic read:");
		 //System.out.println(metaDataLine);
		 //char c = metaDataLine.charAt(0);
		 //System.out.println(Integer.valueOf(c));
		 String metaDataLine2 = metaDataLine.trim();
		 //System.out.println(metaDataLine.length()+"    "+metaDataLine2.length());
		 String[] metaData = parseMetadata(metaDataLine);
		 if(metaData == null){
			 System.out.println("second line should be meta data");
			 return -1;
		 }else{
			 //System.out.println(metaData.length);
			 setMetaData(metaData);
		 }
		 //parse and add the items of the dictionary
		 int correctCount = 0;
		 int errorCount = 0;
		 int stringCount = 0;
		 int line = 2;
		 int continueLine = 0;
		 String itemLine;
		 while((itemLine = br.readLine()) != null && (line++>0)) {
			 Item item = new Item();
			 stringCount = parseItem(itemLine,item);
				
			 if (stringCount == this.metaData.length){
				 addItem(item);
				 //System.out.println(line+":"+item.getEncoding()+":"+item.toString());
				 correctCount++;
			}else if (stringCount > 3){
				line++;
				itemLine=itemLine+"\t"+br.readLine();
				System.out.print("continueline:"+itemLine);
				continueLine++;
				if (parseItem(itemLine,item) == this.metaData.length){
					addItem(item);
					correctCount++;
				}else {
					System.out.println("continue line error:"+itemLine);
					errorCount++;
				}
			}else { //a item is divided to be two line
				System.out.println("invalid item:"+itemLine);
				errorCount++;
			}
			 
		 }
		 //System.out.println("correct item:" + correctCount+"\n wrong item:"+errorCount+"\n totalline:"
		//		 +line+"\n continueLine:"+continueLine);	
	}catch(Exception e) 
	{
		System.out.println("exception:"+e.toString());
	}	 
	return 0;
}

private String parseVersion(String versionLine)
{
	int versionBegin = versionLine.indexOf(":");
	if(versionBegin <0){
		System.out.println("version line is error:"+versionLine+" "+versionBegin);
		return null;
	}else{
		return versionLine.substring(versionBegin+1);
	}
}

/**parse metadata, first line is version, secondline is metadata;
 * @author xiaoling.qin
 * @param itemLine is the data need to be parsed
 * */
private String[] parseMetadata(String itemLine)
{
	if(itemLine == null){
		return null;
	}
	return getStringItem(itemLine);
}
/**
 * 
 * */
private int parseItem(String itemLine, Item item)
{
	if(itemLine == null){
		item = null;
		return 0;
	}
	String[] itemString = getStringItem(itemLine); 
	if(itemString == null)
	{
		System.out.println("itemline should more than 6 fields:"+itemLine);
		item = null;
		return 0;
	}
	
	if (!fillInItemAccordingMetaData(item,itemString)){
		System.out.println("can't fill into item,"+itemLine);
		item = null;
		return itemString.length;
	}
	 //System.out.println("finish one item:"+item.getEncoding());
	 return this.metaData.length;
}
/**
 * 
 * */
private boolean fillInItemAccordingMetaData(Item item, String[] itemString) {
	if(itemString == null) {
		System.out.println("itemString is empty");
		return false;
	}
	if(metaData.length > itemString.length){
		System.out.println("itemString length "+itemString.length);
		return false;
	}
	for(int i=0; i<metaData.length;i++){
		if(metaData[i].equals("codeId")){
			item.setEncoding(itemString[i]);
		}else if(metaData[i].equals("tcode")){
			item.setBelongedTableId(itemString[i]);
		}else if(metaData[i].equals("tablename")){
			item.setBelongedTableName(itemString[i]);
		}else if(metaData[i].equals("中文")){
			item.setLocalName(itemString[i]);
		}else if(metaData[i].equals("英文")){
			item.setEnglishName(itemString[i]);
		}else if(metaData[i].equals("同义词")) {
			item.setSynonyms(parseSynonyms(itemString[i]));
		}else{
			System.out.println("error item in metaData:"+metaData[i]+",length:"+metaData[i].length());
			return false;
		}
	}
	return true;
}
private Map parseSynonyms(String synonms)
{
	Map<String,Integer> map = new HashMap();
	String[]  list = synonms.split(",|，");
	if(list != null){		 
		for(int i=0; i<list.length;i++){
			map.put(list[i], 0);
		}
		return map;
	}else{
		return null;
	}
}


private String[] getStringItem(String src)
{
	if(src == null){
		return null;
	}
	String[] itemString= src.split("\\t+|\\n");
	if(itemString == null || itemString.length == 0){
		System.out.println("error line:"+src);
		return null;
	}
	
	int index = 0;
	boolean notFinish = false;
	for(int i = 0; i<itemString.length;i++){
		itemString[i] = itemString[i].replaceAll("\t|\r|\n|\\s*", "");
		itemString[i] = itemString[i].trim();
		if(itemString[i].equals(',') || itemString[i].equals('，') ){
			itemString[index] += itemString[i];
			notFinish = true;
			System.out.println("add, "+itemString[index]);
		}else if (itemString[i].length() != 0 && notFinish){
			itemString[index] += itemString[i];
			System.out.println("add appendx +"+itemString[index]);
		}else if (itemString[i].length() != 0 && (notFinish == false)){
			itemString[index++] = itemString[i];
			//System.out.println("read single item:"+i+":"+itemString[index-1]);
		}
	}

	
	return itemString;
}
public void reload(String file)
{
	init(file);
}

public Item searchItemByEnglishName(String englishName)
{
	return englishDictionary.get(englishName);
}

public Item searchItemByLocalName(String localName, String tableName)
{
	return localDictionary.get(localName+tableName);
}

public Item searchItemByCoding(String coding)
{
	return encodingDictionary.get(coding);
}

public Map<String,String> searchKeyByLocalSynonyms(String key)
{
	return reverseLocalDictionary.get(key);
}
public String searchLocalNameBySynonymsAndTableName(String synonyms, String tableName)
{
	Map<String,String> dicMap = searchKeyByLocalSynonyms(synonyms);
	if(dicMap == null) {
		return null;
	}
	if(dicMap.containsKey(tableName)){
		return dicMap.get(tableName);
	}else{
		return null;
	}
}

public List<String> searchSimilarBySubstringAndTableName(String subString, String tableName)
{
	if(subString == null) return null;
	List<String> list = new ArrayList<String>();
	Iterator it = this.reverseLocalDictionary.entrySet().iterator();
	while(it!= null && it.hasNext()){
		Map.Entry<String, Map> entry = (Map.Entry<String, Map>)it.next();
		if(entry.getKey().indexOf(subString) > -1 && entry.getValue().containsKey(tableName)){
			list.add(entry.getKey());
		}
	} 
	return list;
	
}
public boolean isSynonymsExistInTable(String synonsyms, String tableName)
{
	Map<String,String> dicMap = searchKeyByLocalSynonyms(synonsyms);
	if(dicMap == null){
		return false;
	}
	return dicMap.containsKey(tableName);
}
public void addItem(Item item)
{
	englishDictionary.put(item.getEnglishName(), item);
	localDictionary.put(item.getLocalName()+item.getBelongedTableName(), item);
	encodingDictionary.put(item.getEncoding(), item);
	addItemToTableList(item);
	Iterator it = item.getLocalSynonyms().entrySet().iterator();
	while(it != null &&it.hasNext())
	{
		Map.Entry<String,String> entry = (Map.Entry<String,String>)it.next();
		if(reverseLocalDictionary.containsKey(entry.getKey().toString()))
		{
			reverseLocalDictionary.get(
					entry.getKey().toString()).put(
							item.getBelongedTableName(), 
							item.getLocalName());
		}else{
				HashMap<String,String> map = new HashMap<String,String>();
				map.put(item.getBelongedTableName(),item.getLocalName());
				reverseLocalDictionary.put(entry.getKey().toString(), map);
					
		}
	}
	
}
 
public void updateItem(Item item)
{
	addItem(item);
}

public void deleteItem(Item item)
{
	englishDictionary.remove(item.getEnglishName());
	localDictionary.remove(item.getLocalName());
	encodingDictionary.remove(item.getEncoding());
	Iterator it = item.getLocalSynonyms().entrySet().iterator();
	while(it.hasNext())
	{
		Map.Entry entry = (Map.Entry)it.next();
		if(reverseLocalDictionary.containsKey(entry.getKey().toString()))
		{
			reverseLocalDictionary.get(entry.getKey().toString()).remove(item.getBelongedTableName());
		}
	}
	return;
}

public void dump()
{
	Iterator it = encodingDictionary.entrySet().iterator();
	while(it.hasNext()){
		Map.Entry entry = (Map.Entry)it.next();
		System.out.println(entry.getValue().toString());
	}
	it = this.reverseLocalDictionary.entrySet().iterator();
	System.out.println("start to output the reverse dictionary");
	while(it.hasNext()){
		Map.Entry entry = (Map.Entry)it.next();
		System.out.println(entry.getKey().toString());
		System.out.println(entry.getValue().toString());
	}
	it = this.tableListMap.entrySet().iterator();
	while(it.hasNext()){
		Map.Entry  entry = (Map.Entry)it.next();
		System.out.println(entry.getValue().toString());
	}
}
public String[] getMetaData() {
	return metaData;
}

public void setMetaData(String[] metaData) {
	this.metaData = metaData;
}

/**@param  @checkList is the item need to be checked
 * @param @tableName is the target table need to be checked
 * @author xiaoling.qin
 * @return the count how many @items exist in the @table
 * */
public int itemExistsCountInTable(String[] checkList, String tableName)
{
	int count = 0;
	for(int i = 0; i<checkList.length; i++)
	{
		if(this.isSynonymsExistInTable(checkList[i], tableName)){
			count++;
		}
	}
	return count;
}

public List<Item> getTableItemListByTableName(String tableName)
{
	//System.out.println(":"+tableName);
	//System.out.println(tableListMap.get(tableName) == null);
	//System.out.println(tableListMap.size());
	return tableListMap.get(tableName);
}

private void addItemToTableList(Item item)
{
	if(tableListMap.containsKey(item.getBelongedTableName())){
		tableListMap.get(item.getBelongedTableName()).add(item);
	}else{ 
		List<Item> list = new ArrayList<Item>();
		list.add(item);
		tableListMap.put(item.getBelongedTableName(), list);
	}
}
public List<String> getSynonymsListBySynonmsAndTableName(String synon, String tableName)
{
	String localName = searchLocalNameBySynonymsAndTableName(synon, tableName);
	if (localName != null) {
		Item item = searchItemByLocalName(localName, tableName);
		if ( item != null) { 
			return new ArrayList<String>(item.getSynonyms().keySet());
		}
	}
	return null;
}

}




