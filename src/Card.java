import java.util.Calendar;

public class Card
{
	private int id;
	private String number;
	private String name;
	private Type type;
	private String expiration_date;
	private Address address;

	public enum Type
	{
		UNKNOWN,
		CREDIT,
		DEBIT
	}

	public Card() { this(0, "", "", Type.UNKNOWN, "", null); }

	public Card(int     id,
	            String  number,
	            String  name,
	            Type    type,
	            String  expiration_date,
	            Address address)
	{
		this.id = id;
		this.number = number;
		this.name = name;
		this.type = type;
		this.expiration_date = expiration_date;
		this.address = address;
	}

	public int getID() { return id; }
	public String getNumber() { return number; }
	public String getName() { return name; }
	public Type getType() { return type; }

	public String getTypeStr()
	{
		switch (type) {
		case CREDIT:
			return "credit";
		case DEBIT:
			return "debit";
		default:
			return null;
		}
	}

	public String getExpirationDate() { return expiration_date; }
	public Address getAddress() { return address; }

	public void setID(int id) { this.id = id; }
	public void setNumber(String number) { this.number = number; }
	public void setName(String name) { this.name = name; }
	public void setType(Type type) { this.type = type; }

	public void setType(String type)
	{
		if (type.compareTo("credit") == 0) {
			this.type = Type.CREDIT;

		} else if (type.compareTo("debit") == 0) {
			this.type = Type.DEBIT;

		} else {
			this.type = Type.UNKNOWN;
		}
	}

	public void setExpirationDate(String expiration_date) { this.expiration_date = expiration_date; }
	public void setAddress(Address address) { this.address = address; }
}
