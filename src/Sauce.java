import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

public class Sauce extends Ingredient
{
	public Sauce() { super(); }

	public Sauce(ResultSet rset)
		throws SQLException,
		       SQLTimeoutException
	{
		super(rset);
	}

	public Sauce(int    id,
	             String name,
	             double small_cost,
	             double medium_cost,
	             double large_cost)
	{
		super(id, name, small_cost, medium_cost, large_cost);
	}
}
