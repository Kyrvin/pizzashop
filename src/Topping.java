import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.ResultSet;

public class Topping extends Ingredient
{
	public Topping() { super(); }

	public Topping(ResultSet rset)
		throws SQLException,
		       SQLTimeoutException
	{
		super(rset);
	}

	public Topping(int    id,
	               String name,
	               double small_cost,
	               double medium_cost,
	               double large_cost)
	{
		super(id, name, small_cost, medium_cost, large_cost);
	}
}
