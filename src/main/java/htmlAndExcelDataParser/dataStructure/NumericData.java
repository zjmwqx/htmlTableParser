package htmlAndExcelDataParser.dataStructure;

import java.util.Date;
//可以拓展integer和Double等。现在都是统一存了Double
public class NumericData extends Data{
	private Number content;
	public NumericData()
	{
		super(Data.NUMERIC);
	}
	public NumericData(Number content)
	{
		setContent(content);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getContent().toString();
	}

	@Override
	public boolean equal(Object o) {
		// TODO Auto-generated method stub
		if(o == null)
			return false;
		if(o instanceof NumericData)
			return this.content.equals(((NumericData)o).getContent());
		return false;
	}
	public Number getContent() {
		return content;
	}
	public void setContent(Number content) {
		this.content = content;
	}
}
