package htmlAndExcelDataParser.dataStructure;

public abstract class Data {
	static final int STRING = 1;
	static final int DATE = 2;
	static final int NUMERIC = 3;
	static final int AUTO = 0;
	private int kind = 0;
	public Data()
	{
		
	}
	public Data(int kind)
	{
		this.setKind(kind);
	}
	
	public int getKind() {
		return kind;
	}
	public void setKind(int kind) {
		this.kind = kind;
	}
	public abstract String toString();
	public abstract boolean equal(Object o);
}
