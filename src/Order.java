import java.util.ArrayList;
import java.util.Date;

public class Order
{
	private int id;
	private Customer customer;
	private Address address;
	private Card card;
	private Date date;
	private ArrayList<OrderLine> lines;

	public Order() { this(0, null, null, null, null, new ArrayList<OrderLine>()); }

	public Order(int             id,
	             Customer        customer,
	             Address         address,
	             Card            card,
	             Date            date,
	             ArrayList<OrderLine> lines)
	{
		this.id = id;
		this.customer = customer;
		this.address = address;
		this.card = card;
		this.date = date;
		this.lines = lines;
	}

	public int getID() { return id; }
	public Customer getCustomer() { return customer; }
	public Address getAddress() { return address; }
	public Card getCard() { return card; }
	public Date getDate() { return date; }
	public ArrayList<OrderLine> getLines() { return lines; }

	public double getTotalCost()
	{
		double cost = 0.0;

		for (OrderLine line: lines) {
			cost += line.getUnitCost() * line.getQuantity();
		}

		return cost;
	}

	public void setID(int id) { this.id = id; }
	public void setCustomer(Customer customer) { this.customer = customer; }
	public void setAddress(Address address) { this.address = address; }
	public void setCard(Card card) { this.card = card; }
	public void setDate(Date date) { this.date = date; }
	public void setLines(ArrayList<OrderLine> lines) { this.lines = lines; }
}
