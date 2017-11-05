public class Address
{
	private int id;
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String zip;

	public Address() { this(0, "", "", "", "", ""); }

	public Address(int id,
	        String line1,
	        String line2,
	        String city,
	        String state,
	        String zip)
	{
		this.id = id;
		this.line1 = line1;
		this.line2 = line2;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	public int getID() { return id; }
	public String getLine1() { return line1; }
	public String getLine2() { return line2; }
	public String getCity() { return city; }
	public String getState() { return state; }
	public String getZip() { return zip; }

	public void setID(int id) { this.id = id; }
	public void setLine1(String line1) { this.line1 = line1; }
	public void setLine2(String line2) { this.line2 = line2; }
	public void setCity(String city) { this.city = city; }
	public void setState(String state) { this.state = state; }
	public void setZip(String zip) { this.zip= zip; }
}
