public class Customer
{
	private int id;
	private String name;
	private String email;
	private String password;
	private String phone;
	private String notes;
	private Address address;
	private Card active_card;

	public Customer() { this(0, "", "", "", "", "", new Address(), new Card()); }

	public Customer(String email, String password)
	{
		this(0, "", email, password, "", "", new Address(), new Card());
	}

	public Customer(int     id,
	                String  name,
	                String  email,
		        String  password,
	                String  phone,
	                String  notes,
	                Address address,
	                Card    active_card)
	{
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.notes = notes;
		this.address = address;
		this.active_card = active_card;
	}

	public int getID() { return id; }
	public String getName() { return name; }
	public String getEmail() { return email; }
	public String getPassword() { return password; }
	public String getPhone() { return phone; }
	public String getNotes() { return notes; }
	public Address getAddress() { return address; }
	public Card getActiveCard() { return active_card; }

	public void setID(int id) { this.id = id; }
	public void setName(String name) { this.name = name; }
	public void setEmail(String email) { this.email = email; }
	public void setPassword(String password) { this.password = password; }
	public void setPhone(String phone) { this.phone = phone; }
	public void setNotes(String notes) { this.notes = notes; }
	public void setAddress(Address address) { this.address = address; }
	public void setActiveCard(Card active_card) { this.active_card = active_card; }
}
