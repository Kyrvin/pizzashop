import java.util.ArrayList;

public class Pizza
{
	private int id;
	private String name;
	private Size size; 
	private Crust crust;
	private Sauce sauce;
	private ArrayList<Cheese> cheeses;
	private ArrayList<Topping> toppings;

	public enum Size {
		SMALL,
		MEDIUM,
		LARGE
	}

	public Pizza()
	{
		this(0, "", Size.SMALL, null, null, null, null);
	}

	public Pizza(int                id,
	             String             name,
	             Size               size,
	             Crust              crust,
	             Sauce              sauce,
	             ArrayList<Cheese>  cheeses,
	             ArrayList<Topping> toppings)
	{
		this.id = id;
		this.name = name == null ? "" : name;
		this.size = size;
		this.crust = crust;
		this.sauce = sauce;

		this.cheeses = cheeses == null ? new ArrayList<Cheese>() : cheeses;
		this.toppings = toppings == null ? new ArrayList<Topping>() : toppings;
	}

	public int getID() { return id; }
	public String getName() { return name; }
	public Size getSize() { return size; }
	public Crust getCrust() { return crust; }
	public Sauce getSauce() { return sauce; }
	public ArrayList<Cheese> getCheeses() { return cheeses; }
	public ArrayList<Topping> getToppings() { return toppings; }

	public void setID(int id) { this.id = id; }
	public void setName(String name) { this.name = name; }
	public void setSize(Size size) { this.size = size; }
	public void setCrust(Crust crust) { this.crust = crust; }
	public void setSauce(Sauce sauce) { this.sauce = sauce; }
	public void setCheeses(ArrayList<Cheese> cheeses) { this.cheeses = cheeses; }
	public void setToppings(ArrayList<Topping> toppings) { this.toppings = toppings; }

	public double getCost()
	{
		double cost = 0.0;

		cost += crust.getCost(size);
		cost += sauce.getCost(size);

		for (Cheese ch: cheeses) {
			cost += ch.getCost(size);
		}

		for (Topping tp: toppings) {
			cost += tp.getCost(size);
		}

		return cost;
	}
}
