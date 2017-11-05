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

	public void setID(int id) { this.id = id; }
	public void setName(String name) { this.name = name; }
	public void setSmallCost(double small_cost) { this.small_cost = small_cost; }
	public void setMediumCost(double medium_cost) { this.medium_cost = medium_cost; }
	public void setLargeCost(double large_cost) { this.large_cost = large_cost; }

	public int getID() { return id; }
	public String getName() { return name; }
	public double getSmallCost() { return small_cost; }
	public double getMediumCost() { return medium_cost; }
	public double getLargeCost() { return large_cost; }

	public double getCost(Pizza.Size size)
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
