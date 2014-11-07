package htmlAndExcelDataParser.dataStructure;

public class StringData extends Data{
	private String content;
	public void setContent(String content) {
		this.content = content;
	}
	public StringData()
	{
		super(Data.STRING);
	}
	public StringData(String content)
	{
		super(Data.STRING);
		this.content = content;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getContent();
	}

	@Override
	public boolean equal(Object o) {
		// TODO Auto-generated method stub
		if(o == null)
			return false;
		if(o instanceof StringData)
			return this.content.equals(((StringData)o).getContent());
		return false;
	}
	public String getContent() {
		return content;
	}
	public void setKind(String content) {
		this.content = content;
	}
}
