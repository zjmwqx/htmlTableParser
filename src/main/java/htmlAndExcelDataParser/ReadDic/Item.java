package htmlAndExcelDataParser.ReadDic;

import java.util.Iterator;
import java.util.Map;

public class Item {

private	String exchange;
private	String exchangeLanguage;
private	String englishName;
private	String localName;
private	String type;  //table or attribute in table
private	String encoding;
private	String description;
private	Map<String,Integer> localSynonyms;
private Item table;
private String belongedTableId;
private String belongedTableName;
private String dictionaryVersion;


public Item(String exchange, String exchangeLanguage, String englishName, String localName,
		String type, String encoding, String description, String[] localSynonyms, Item table,String tableId)
{
	
}
public Item(String encoding, String localName, String englishName, String tableId, String belongedTableName)
{
	this.encoding = encoding;
	this.localName = localName;
	this.belongedTableId = tableId;
	this.belongedTableName = belongedTableName;
	this.englishName = englishName;
}
public Item() {
	// TODO Auto-generated constructor stub
}
public void setBelongedTableName(String tableName)
{
	this.belongedTableName = tableName;
}
public String getBelongedTableName()
{
	return belongedTableName;
}
public void setBelongedTableId(String id)
{
	this.belongedTableId = id;
	return;
}
public String getBelongedTableId()
{
	return this.belongedTableId;
}
public boolean isSynonymsExist(String synonyms)
{
	return localSynonyms.containsKey(synonyms);
}
public void setSynonyms(Map synonymsMap)
{
	this.localSynonyms = synonymsMap;
}
public Map<String,Integer> getSynonyms()
{
	return this.localSynonyms;
}
public void addSynonyms(String synonyms)
{
	int status = 0; //default value 0 means the synonyms is valid
	localSynonyms.put(synonyms,status);
}

public void deleteSynonyms(String synonyms)
{
	localSynonyms.remove(synonyms);
}

public Map getLocalSynonyms()
{
	return this.localSynonyms;
}

public Item getTableItem(Item table)
{
	return this.table;
}

public void setTableItem(Item table)
{
	this.table = table;
}

public void setExchange(String exchange)
{
	this.exchange = exchange;
}

public String getExchange()
{
	return this.exchange;
}

public void setExchangeLanguage(String exchangeLanguage)
{
	this.exchangeLanguage = exchangeLanguage;
}

public String getExchangeLanguage()
{
	return this.exchangeLanguage;
}

public void setDescription(String description)
{
	this.description = description;
}

public String getDescription()
{
	return this.description;
}

public void setEncoding(String encoding)
{
	this.encoding = encoding;
}
public String getEncoding()
{
	return this.encoding;
}
public void setEnglishName(String englishName)
{
	this.englishName = englishName;
}
public String getEnglishName()
{
	return this.englishName;
}

public void setLocalName(String localName)
{
	this.localName = localName;
}
public String getLocalName()
{
	return this.localName;
}
public String toString()
{
	String temp = "encoding:"+this.encoding
			      +" englishName:"+this.englishName
			      +" localName:"+this.localName
			      +" tableName:"+this.belongedTableName
			      +" synonyms:"+this.localSynonyms.toString();

	return temp;
}
public String getDictionaryVersion() {
	return dictionaryVersion;
}
public void setDictionaryVersion(String dictionaryVersion) {
	this.dictionaryVersion = dictionaryVersion;
}

}
