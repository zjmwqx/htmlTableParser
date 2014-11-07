package htmlAndExcelDataParser.dataStructure;

import java.util.Date;


public class DateData extends Data{
	private Date content;
	public DateData()
	{
		super(Data.DATE);
	}
	public DateData(Date data)
	{
		super(Data.DATE);
		content = data;
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
		if(o instanceof DateData)
			return this.content.equals(((DateData)o).getContent());
		return false;
	}
	public Date getContent() {
		return content;
	}
	public void setKind(Date content) {
		this.content = content;
	}
}
