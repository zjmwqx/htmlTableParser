package htmlAndExcelDataParser.dataStructure;

public class FileDesDate {
	private int year;
	private int month;
	private String dateString;
	public FileDesDate() {
		// TODO Auto-generated constructor stub
		year = 1;
		month = 1;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	public String getDateString() {
		return dateString;
	}
	public FileDesDate(int year, int month, String dateString)
	{
		this.year = year;
		this.month = month;
		this.dateString = dateString;
	}
	public int getYear() {
		return year;
	}
	public int getMonth() {
		return month;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public void setMonth(int month) {
		this.month = month;
	}
}
