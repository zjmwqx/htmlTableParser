package htmlAndExcelDataParser.model;

import htmlAndExcelDataParser.dataStructure.Data;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//单元格：数据，table中相对位置，横纵跨度
public class CellData implements Cloneable{
	public Object clone()
	{
		CellData o =null;
		try{
            o = (CellData)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;
	};

	private Data dataContent;
	
	private int x,y;
	
	private int colspan,rowspan;
	public CellData()
	{
	}
	public CellData(int x, int y, int colspan, int rowspan, Data data)
	{
		dataContent = data;
		this.x = x;
		this.y = y;
		this.colspan =colspan;
		this.rowspan = rowspan;
	}
	public Data getDataContent() {
		return dataContent;
	}
	public void setDataContent(Data dataContent) {
		this.dataContent = dataContent;
	}
	public String toString()
	{
		return dataContent.toString();
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getColspan() {
		return colspan;
	}
	public int getRowspan() {
		return rowspan;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
}
