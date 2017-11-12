import java.lang.IllegalArgumentException;

public class OrderLine
{
	private Pizza pizza;
	private Pizza.Size size;
	private int quantity;
	private double unit_cost;

	public OrderLine() { this(new Pizza(), Pizza.Size.LARGE, 0, 0.0); }

	public OrderLine(Pizza pizza, Pizza.Size size, int quantity, double unit_cost)
	{
		this.pizza = pizza;
		this.size = size;
		this.quantity = quantity;
		this.unit_cost = 0.0;
	}

	public Pizza getPizza() { return pizza; }
	public Pizza.Size getSize() { return size; }
	public int getQuantity() { return quantity; }

	public double getUnitCost()
	{
		if (unit_cost == 0.0) {
			return pizza.getCost(size);
		} else {
			return unit_cost;
		}
	}

	public String getSizeString()
	{
		switch (size) {
		case SMALL:
			return "small";

		case MEDIUM:
			return "medium";

		case LARGE:
			return "large";

		default:
			return null;
		}
	}

	public void setPizza(Pizza pizza) { this.pizza = pizza; }
	public void setSize(Pizza.Size size) { this.size = size; }
	public void setQuantity(int quantity) { this.quantity = quantity; }
	public void setUnitCost(double unit_cost) { this.unit_cost = unit_cost; }

	public void setSize(String size)
	{
		if (size.compareTo("small") == 0) {
			this.size = Pizza.Size.SMALL;

		} else if (size.compareTo("medium") == 0) {
			this.size = Pizza.Size.MEDIUM;

		} else if (size.compareTo("large") == 0) {
			this.size = Pizza.Size.LARGE;

		} else {
			throw new IllegalArgumentException("Invalid size: " + size);
		}
	}
}
