import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.ResultSet;

public class Crust
	extends Ingredient
{
	public Crust() { super(); }

	public Crust(ResultSet rset)
		throws SQLException,
		       SQLTimeoutException
	{
		super(rset);
	}

	public Crust(int    id,
	             String name,
	             double small_cost,
	             double medium_cost,
	             double large_cost)
	{
		super(id, name, small_cost, medium_cost, large_cost);
	}
}
