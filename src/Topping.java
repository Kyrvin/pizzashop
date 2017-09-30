public class Topping extends Ingredient
{
	public Topping() { super(); }

	public Topping(int    id,
	               String name,
	               double small_cost,
	               double medium_cost,
	               double large_cost)
	{
		super(id, name, small_cost, medium_cost, large_cost);
	}
}
