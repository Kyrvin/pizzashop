import java.util.ArrayList;

public class Pizza
{
	private int id;
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
		this(0, Size.SMALL, null, null, null, null);
	}

	public Pizza(int                id,
	             Size               size,
	             Crust              crust,
	             Sauce              sauce,
	             ArrayList<Cheese>  cheeses,
	             ArrayList<Topping> toppings)
	{
		this.id = id;
		this.size = size;
		this.crust = crust;
		this.sauce = sauce;

		this.cheeses = cheeses == null ? new ArrayList<Cheese>() : cheeses;
		this.toppings = toppings == null ? new ArrayList<Topping>() : toppings;

		/*
		if (cheeses == null) {
			this.cheeses = ArrayList<Cheese>();
		} else {
			this.cheeses = cheeses;
		}

		if (toppings == null) {
			this.toppings = ArrayList<Topping>();
		} else {
			this.toppings = toppings;
		}
		*/
	}

	int getID() { return id; }
	Size getSize() { return size; }
	Crust getCrust() { return crust; }
	Sauce getSauce() { return sauce; }
	ArrayList<Cheese> getCheeses() { return cheeses; }
	ArrayList<Topping> getToppings() { return toppings; }

	void setID(int id) { this.id = id; }
	void setSize(Size size) { this.size = size; }
	void setCrust(Crust crust) { this.crust = crust; }
	void setSauce(Sauce sauce) { this.sauce = sauce; }
	void setCheeses(ArrayList<Cheese> cheeses) { this.cheeses = cheeses; }
	void setToppings(ArrayList<Topping> toppings) { this.toppings = toppings; }

	double getTotalCost()
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
