public class Ingredient
{
	private int id;
	private String name;
	private double small_cost;
	private double medium_cost;
	private double large_cost;

	protected Ingredient()
	{
		this(0, "", 0, 0, 0);
	}

	protected Ingredient(int    id,
	                     String name,
	                     double small_cost,
	                     double medium_cost,
	                     double large_cost)
	{
		this.id = id;
		this.name = name;
		this.small_cost = small_cost;
		this.medium_cost = medium_cost;
		this.large_cost = large_cost;
	}

	void setID(int id) { this.id = id; }
	void setName(String name) { this.name = name; }
	void setSmallCost(double small_cost) { this.small_cost = small_cost; }
	void setMediumCost(double medium_cost) { this.medium_cost = medium_cost; }
	void setLargeCost(double large_cost) { this.large_cost = large_cost; }

	int getID() { return id; }
	String getName() { return name; }
	double getSmallCost() { return small_cost; }
	double getMediumCost() { return medium_cost; }
	double getLargeCost() { return large_cost; }

	double getCost(Pizza.Size size)
	{
		switch (size) {
		case SMALL:
			return small_cost;

		case MEDIUM:
			return medium_cost;

		case LARGE:
			return large_cost;

		default:
			System.err.println("Ingredient.getCost: Invalid pizza size");
		}

		return 0;
	}

}
