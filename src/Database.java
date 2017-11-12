import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.sqlite.SQLiteErrorCode;

class IngredientTable
{
	private static final String table_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    ingredient (ingredient_id          INTEGER PRIMARY KEY," +
		"                ingredient_name        TEXT    UNIQUE NOT NULL," +
		"                ingredient_small_cost  REAL           NOT NULL," +
		"                ingredient_medium_cost REAL           NOT NULL," +
		"                ingredient_large_cost  REAL           NOT NULL," +
		"                CONSTRAINT ingredient_check_cost" +
		"                    CHECK (ingredient_small_cost <= ingredient_medium_cost" +
		"                    AND    ingredient_medium_cost <= ingredient_large_cost)" +
		"    );";

	private static final String insert_str =
		"INSERT INTO" +
		"    ingredient (ingredient_name," +
		"                ingredient_small_cost," +
		"                ingredient_medium_cost," +
		"                ingredient_large_cost)" +
		"    VALUES (?, ?, ?, ?);";

	private static final String update_str =
		"UPDATE ingredient" +
		"    SET ingredient_name = ?," +
		"        ingredient_small_cost = ?," +
		"        ingredient_medium_cost = ?," +
		"        ingredient_large_cost = ?" +
		"    WHERE ingredient_id = ?;";

	private Database db;
	private PreparedStatement insert_stmt;
	private PreparedStatement update_stmt;

	public IngredientTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(table_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		update_stmt = conn.prepareStatement(update_str);
	}

	public void insert(Ingredient ingredient)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setString(1, ingredient.getName());
		insert_stmt.setDouble(2, ingredient.getSmallCost());
		insert_stmt.setDouble(3, ingredient.getMediumCost());
		insert_stmt.setDouble(4, ingredient.getLargeCost());

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();

		ingredient.setID(db.lastInsertRowID());
	}

	public void update(Ingredient ingredient)
		throws SQLException,
		       SQLTimeoutException
	{
		update_stmt.setInt(1, ingredient.getID());
		update_stmt.setString(2, ingredient.getName());
		update_stmt.setDouble(3, ingredient.getSmallCost());
		update_stmt.setDouble(4, ingredient.getMediumCost());
		update_stmt.setDouble(5, ingredient.getLargeCost());

		update_stmt.executeUpdate();
		update_stmt.clearParameters();
	}
}

class CrustTable
{
	private static final String table_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    crust (crust_id INTEGER PRIMARY KEY" +
		"               REFERENCES ingredient(ingredient_id)" +
		"    );";

	private static final String view_schema =
		"CREATE VIEW IF NOT EXISTS" +
		"    crust_view (crust_id," +
		"                crust_name," +
		"                crust_small_cost," +
		"                crust_medium_cost," +
		"                crust_large_cost)" +
		"    AS SELECT crust_id," +
		"              ingredient_name," +
		"              ingredient_small_cost," +
		"              ingredient_medium_cost," +
		"              ingredient_large_cost" +
		"           FROM crust LEFT JOIN ingredient" +
		"                    ON crust_id = ingredient_id;";

	private static final String insert_str =
		"INSERT INTO" +
		"    crust (crust_id)" +
		"    SELECT ingredient_id" +
		"        FROM ingredient" +
		"        WHERE ingredient_name = ?;";

	private static final String query_str =
		"SELECT crust_id," +
		"       crust_name," +
		"       crust_small_cost," +
		"       crust_medium_cost," +
		"       crust_large_cost" +
		"    FROM crust_view" +
		"    WHERE crust_id = ?;";

	private static final String query_all_str =
		"SELECT crust_id," +
		"       crust_name," +
		"       crust_small_cost," +
		"       crust_medium_cost," +
		"       crust_large_cost" +
		"    FROM crust_view;";

	private Database db;
	private PreparedStatement insert_stmt;
	private PreparedStatement query_stmt;
	private PreparedStatement query_all_stmt;

	public CrustTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(table_schema);
		stmt.execute(view_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		query_stmt = conn.prepareStatement(query_str);
		query_all_stmt = conn.prepareStatement(query_all_str);
	}

	public void insert(Crust crust)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setString(1, crust.getName());

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();
	}

	public Crust query(int crust_id)
		throws SQLException,
		       SQLTimeoutException
	{
		query_stmt.setInt(1, crust_id);

		ResultSet rset = query_stmt.executeQuery();

		Crust crust = new Crust(rset);

		query_stmt.clearParameters();

		return crust;
	}

	public ArrayList<Crust> queryAll()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Crust> list = new ArrayList<Crust>();

		ResultSet rset = query_all_stmt.executeQuery();

		while (rset.next()) {
			list.add(new Crust(rset));
		}

		return list;
	}
}

class SauceTable
{
	private static final String table_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    sauce (sauce_id INTEGER PRIMARY KEY" +
		"               REFERENCES ingredient(ingredient_id)" +
		"    );";

	private static final String view_schema =
		"CREATE VIEW IF NOT EXISTS" +
		"    sauce_view (sauce_id," +
		"                sauce_name," +
		"                sauce_small_cost," +
		"                sauce_medium_cost," +
		"                sauce_large_cost)" +
		"    AS SELECT sauce_id," +
		"              ingredient_name," +
		"              ingredient_small_cost," +
		"              ingredient_medium_cost," +
		"              ingredient_large_cost" +
		"           FROM sauce LEFT JOIN ingredient ON sauce_id = ingredient_id;";

	private static final String insert_str =
		"INSERT INTO" +
		"    sauce (sauce_id)" +
		"    SELECT ingredient_id" +
		"        FROM ingredient" +
		"        WHERE ingredient_name = ?;";

	private static final String query_str =
		"SELECT sauce_id," +
		"       sauce_name," +
		"       sauce_small_cost," +
		"       sauce_medium_cost," +
		"       sauce_large_cost" +
		"    FROM sauce_view" +
		"    WHERE sauce_id = ?;";

	private static final String query_all_str =
		"SELECT sauce_id," +
		"       sauce_name," +
		"       sauce_small_cost," +
		"       sauce_medium_cost," +
		"       sauce_large_cost" +
		"    FROM sauce_view;";

	private Database db;
	private PreparedStatement insert_stmt;
	private PreparedStatement query_stmt;
	private PreparedStatement query_all_stmt;

	public SauceTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(table_schema);
		stmt.execute(view_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		query_stmt = conn.prepareStatement(query_str);
		query_all_stmt = conn.prepareStatement(query_all_str);
	}

	public void insert(Sauce sauce)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setString(1, sauce.getName());

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();
	}

	public Sauce query(int sauce_id)
		throws SQLException,
		       SQLTimeoutException
	{
		query_stmt.setInt(1, sauce_id);

		ResultSet rset = query_stmt.executeQuery();

		Sauce sauce = new Sauce(rset);

		query_stmt.clearParameters();

		return sauce;
	}

	public ArrayList<Sauce> queryAll()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Sauce> list = new ArrayList<Sauce>();

		ResultSet rset = query_all_stmt.executeQuery();

		while (rset.next()) {
			list.add(new Sauce(rset));
		}

		return list;
	}
}

class CheeseTable
{
	private static final String table_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    cheese (cheese_id INTEGER PRIMARY KEY" +
		"                REFERENCES ingredient(ingredient_id)" +
		"    );";

	private static final String view_schema =
		"CREATE VIEW IF NOT EXISTS" +
		"    cheese_view (cheese_id," +
		"                 cheese_name," +
		"                 cheese_small_cost," +
		"                 cheese_medium_cost," +
		"                 cheese_large_cost)" +
		"    AS SELECT cheese_id," +
		"              ingredient_name," +
		"              ingredient_small_cost," +
		"              ingredient_medium_cost," +
		"              ingredient_large_cost" +
		"           FROM cheese LEFT JOIN ingredient ON cheese_id = ingredient_id;";

	private static final String insert_str =
		"INSERT INTO" +
		"    cheese (cheese_id)" +
		"    SELECT ingredient_id" +
		"        FROM ingredient" +
		"        WHERE ingredient_name = ?;";

	private static final String query_all_str =
		"SELECT cheese_id," +
		"       cheese_name," +
		"       cheese_small_cost," +
		"       cheese_medium_cost," +
		"       cheese_large_cost" +
		"    FROM cheese_view;";

	private Database db;
	private PreparedStatement insert_stmt;
	private PreparedStatement query_all_stmt;

	public CheeseTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(table_schema);
		stmt.execute(view_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		query_all_stmt = conn.prepareStatement(query_all_str);
	}

	public void insert(Cheese cheese)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setString(1, cheese.getName());

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();
	}

	public ArrayList<Cheese> queryAll()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Cheese> list = new ArrayList<Cheese>();

		ResultSet rset = query_all_stmt.executeQuery();

		while (rset.next()) {
			list.add(new Cheese(rset));
		}

		return list;
	}
}

class ToppingTable
{
	private static final String table_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    topping (topping_id INTEGER PRIMARY KEY" +
		"                 REFERENCES ingredient(ingredient_id)" +
		"    );";

	private static final String view_schema =
		"CREATE VIEW IF NOT EXISTS" +
		"    topping_view (topping_id," +
		"                  topping_name," +
		"                  topping_small_cost," +
		"                  topping_medium_cost," +
		"                  topping_large_cost)" +
		"    AS SELECT topping_id," +
		"              ingredient_name," +
		"              ingredient_small_cost," +
		"              ingredient_medium_cost," +
		"              ingredient_large_cost" +
		"           FROM topping LEFT JOIN ingredient ON topping_id = ingredient_id;";

	private static final String insert_str =
		"INSERT INTO" +
		"    topping (topping_id)" +
		"    SELECT ingredient_id" +
		"        FROM ingredient" +
		"        WHERE ingredient_name = ?;";

	private static final String query_all_str =
		"SELECT topping_id," +
		"       topping_name," +
		"       topping_small_cost," +
		"       topping_medium_cost," +
		"       topping_large_cost" +
		"    FROM topping_view;";

	private Database db;
	private PreparedStatement insert_stmt;
	private PreparedStatement query_all_stmt;

	public ToppingTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(table_schema);
		stmt.execute(view_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		query_all_stmt = conn.prepareStatement(query_all_str);
	}

	public void insert(Topping topping)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setString(1, topping.getName());

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();
	}

	public ArrayList<Topping> queryAll()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Topping> list = new ArrayList<Topping>();

		ResultSet rset = query_all_stmt.executeQuery();

		while (rset.next()) {
			list.add(new Topping(rset));
		}

		return list;
	}
}

class PizzaTable
{
	private static final String table_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    pizza (pizza_id    INTEGER PRIMARY KEY," +
		"           pizza_name  TEXT," +
		"           pizza_crust INTEGER NOT NULL REFERENCES crust(crust_id)," +
		"           pizza_sauce INTEGER NOT NULL REFERENCES sauce(sauce_id)" +
		"    );";

	private static final String pizza_cheese_schema =
		"CREATE TABLE IF NOT EXISTS pizza_cheese (" +
		"	pizza_id  INTEGER NOT NULL REFERENCES pizza(pizza_id)," +
		"	cheese_id INTEGER NOT NULL REFERENCES cheese(cheese_id)," +
		"	PRIMARY KEY (pizza_id, cheese_id)" +
		");";

	private static final String pizza_topping_schema =
		"CREATE TABLE IF NOT EXISTS pizza_topping (" +
		"	pizza_id   INTEGER NOT NULL REFERENCES pizza(pizza_id)," +
		"	topping_id INTEGER NOT NULL REFERENCES topping(topping_id)," +
		"	PRIMARY KEY (pizza_id, topping_id)" +
		");";

	private static final String trigger_schema =
		"CREATE TRIGGER IF NOT EXISTS pizza_update_deletes" +
		"    AFTER" +
		"        UPDATE OF pizza_name," +
		"                  pizza_crust," +
		"                  pizza_sauce" +
		"               ON pizza" +
		"    BEGIN" +
		"        DELETE FROM pizza_cheese" +
		"               WHERE pizza_cheese.pizza_id = OLD.pizza_id;" +
		"" +
		"        DELETE FROM pizza_topping" +
		"               WHERE pizza_topping.pizza_id = OLD.pizza_id;" +
		"    END;";

	private static final String insert_str =
		"INSERT INTO" +
		"    pizza (pizza_name," +
		"           pizza_crust," +
		"           pizza_sauce)" +
		"    VALUES (?, ?, ?);";

	private static final String update_str =
		"UPDATE pizza" +
		"    SET pizza_name = ?," +
		"        pizza_crust = ?," +
		"        pizza_sauce = ?" +
		"    WHERE pizza_id = ?;";

	private static final String query_str =
		"SELECT pizza_id," +
		"       pizza_name," +
		"       pizza_crust," +
		"       pizza_sauce" +
		"    FROM pizza" +
		"    WHERE pizza_id = ?;";

	private static final String query_all_str =
		"SELECT pizza_id," +
		"       pizza_name," +
		"       pizza_crust," +
		"       pizza_sauce" +
		"    FROM pizza;";

	private static final String insert_pizza_cheese_str =
		"INSERT INTO" +
		"    pizza_cheese (pizza_id," +
		"                  cheese_id)" +
		"    VALUES (?, ?);";

	private static final String insert_pizza_topping_str =
		"INSERT INTO" +
		"    pizza_topping (pizza_id," +
		"                   topping_id)" +
		"    VALUES (?, ?);";

	private static final String query_pizza_cheese_str =
		"SELECT pizza_cheese.cheese_id," +
		"       cheese_name," +
		"       cheese_small_cost," +
		"       cheese_medium_cost," +
		"       cheese_large_cost" +
		"    FROM pizza_cheese LEFT JOIN cheese_view" +
		"             ON pizza_cheese.cheese_id = cheese_view.cheese_id" +
		"    WHERE pizza_id = ?;";

	private static final String query_pizza_topping_str =
		"SELECT pizza_topping.topping_id," +
		"       topping_name," +
		"       topping_small_cost," +
		"       topping_medium_cost," +
		"       topping_large_cost" +
		"    FROM pizza_topping LEFT JOIN topping_view" +
		"             ON pizza_topping.topping_id = topping_view.topping_id" +
		"    WHERE pizza_id = ?;";

	private Database db;
	private CrustTable crust_table;
	private SauceTable sauce_table;
	private PreparedStatement insert_stmt;
	private PreparedStatement update_stmt;
	private PreparedStatement query_stmt;
	private PreparedStatement query_all_stmt;
	private PreparedStatement insert_pizza_cheese_stmt;
	private PreparedStatement insert_pizza_topping_stmt;
	private PreparedStatement query_pizza_cheese_stmt;
	private PreparedStatement query_pizza_topping_stmt;

	public PizzaTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;
		crust_table = db.getCrustTable();
		sauce_table = db.getSauceTable();

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(table_schema);
		stmt.execute(pizza_cheese_schema);
		stmt.execute(pizza_topping_schema);
		stmt.execute(trigger_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		update_stmt = conn.prepareStatement(update_str);
		query_stmt = conn.prepareStatement(query_str);
		query_all_stmt = conn.prepareStatement(query_all_str);

		insert_pizza_cheese_stmt = conn.prepareStatement(insert_pizza_cheese_str);
		insert_pizza_topping_stmt = conn.prepareStatement(insert_pizza_topping_str);
		query_pizza_cheese_stmt = conn.prepareStatement(query_pizza_cheese_str);
		query_pizza_topping_stmt = conn.prepareStatement(query_pizza_topping_str);
	}

	public void insert(Pizza pizza)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setString(1, pizza.getName());
		insert_stmt.setInt(2, pizza.getCrust().getID());
		insert_stmt.setInt(3, pizza.getSauce().getID());

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();

		pizza.setID(db.lastInsertRowID());

		insertCheeses(pizza);
		insertToppings(pizza);
	}

	public void update(Pizza pizza)
		throws SQLException,
		       SQLTimeoutException
	{
		update_stmt.setString(1, pizza.getName());
		update_stmt.setInt(2, pizza.getCrust().getID());
		update_stmt.setInt(3, pizza.getSauce().getID());
		update_stmt.setInt(4, pizza.getID());

		update_stmt.executeUpdate();
		update_stmt.clearParameters();

		insertCheeses(pizza);
		insertToppings(pizza);
	}

	public Pizza query(int pizza_id)
		throws SQLException,
		       SQLTimeoutException
	{
		Pizza pizza = new Pizza();

		query_stmt.setInt(1, pizza_id);

		ResultSet rset = query_stmt.executeQuery();

		pizza.setID(rset.getInt(1));
		pizza.setName(rset.getString(2));
		pizza.setCrust(crust_table.query(rset.getInt(3)));
		pizza.setSauce(sauce_table.query(rset.getInt(4)));
		pizza.setCheeses(queryCheeses(pizza.getID()));
		pizza.setToppings(queryToppings(pizza.getID()));

		return pizza;
	}

	public ArrayList<Pizza> queryAll()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Pizza> list = new ArrayList<Pizza>();

		ResultSet rset = query_all_stmt.executeQuery();

		while (rset.next()) {
			Pizza pizza = new Pizza();

			pizza.setID(rset.getInt(1));
			pizza.setName(rset.getString(2));
			pizza.setCrust(crust_table.query(rset.getInt(3)));
			pizza.setSauce(sauce_table.query(rset.getInt(4)));
			pizza.setCheeses(queryCheeses(pizza.getID()));
			pizza.setToppings(queryToppings(pizza.getID()));

			list.add(pizza);
		}

		return list;
	}

	private void insertCheeses(Pizza pizza)
		throws SQLException,
		       SQLTimeoutException
	{
		for (Cheese cheese: pizza.getCheeses()) {
			insert_pizza_cheese_stmt.setInt(1, pizza.getID());
			insert_pizza_cheese_stmt.setInt(2, cheese.getID());

			insert_pizza_cheese_stmt.executeUpdate();
			insert_pizza_cheese_stmt.clearParameters();
		}
	}

	private void insertToppings(Pizza pizza)
		throws SQLException,
		       SQLTimeoutException
	{
		for (Topping topping: pizza.getToppings()) {
			insert_pizza_topping_stmt.setInt(1, pizza.getID());
			insert_pizza_topping_stmt.setInt(2, topping.getID());

			insert_pizza_topping_stmt.executeUpdate();
			insert_pizza_topping_stmt.clearParameters();
		}
	}

	private ArrayList<Cheese> queryCheeses(int pizza_id)
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Cheese> list = new ArrayList<Cheese>();

		query_pizza_cheese_stmt.setInt(1, pizza_id);

		ResultSet rset = query_pizza_cheese_stmt.executeQuery();

		while (rset.next()) {
			list.add(new Cheese(rset));
		}

		query_pizza_cheese_stmt.clearParameters();

		return list;
	}

	private ArrayList<Topping> queryToppings(int pizza_id)
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Topping> list = new ArrayList<Topping>();

		query_pizza_topping_stmt.setInt(1, pizza_id);

		ResultSet rset = query_pizza_topping_stmt.executeQuery();

		while (rset.next()) {
			list.add(new Topping(rset));
		}

		query_pizza_topping_stmt.clearParameters();

		return list;
	}
}

class AddressTable
{
	private static final String table_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    address (address_id    INTEGER PRIMARY KEY," +
		"             address_line1 TEXT NOT NULL," +
		"             address_line2 TEXT," +
		"             address_city  TEXT NOT NULL," +
		"             address_state TEXT NOT NULL CHECK (address_state LIKE '__')," +
		"             address_zip   TEXT NOT NULL CHECK (address_zip LIKE '_____')" +
		"    );";

	private static final String insert_str =
		"INSERT INTO" +
		"    address (address_line1," +
		"             address_line2," +
		"             address_city," +
		"             address_state," +
		"             address_zip)" +
		"    VALUES (?, ?, ?, ?, ?);";

	public static final String query_str =
		"SELECT address_id," +
		"       address_line1," +
		"       address_line2," +
		"       address_city," +
		"       address_state," +
		"       address_zip" +
		"    FROM address" +
		"    WHERE address_id = ?";

	private Database db;
	private PreparedStatement insert_stmt;
	private PreparedStatement query_stmt;

	public AddressTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(table_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		query_stmt = conn.prepareStatement(query_str);
	}

	public void insert(Address address)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setString(1, address.getLine1());
		insert_stmt.setString(2, address.getLine2());
		insert_stmt.setString(3, address.getCity());
		insert_stmt.setString(4, address.getState());
		insert_stmt.setString(5, address.getZip());

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();

		address.setID(db.lastInsertRowID());
	}

	public Address query(int address_id)
		throws SQLException,
		       SQLTimeoutException
	{
		query_stmt.setInt(1, address_id);

		ResultSet rset = query_stmt.executeQuery();

		Address address = new Address(rset);

		query_stmt.clearParameters();

		return address;
	}
}

class CardTable
{
	public static final String table_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    card (card_id              INTEGER  PRIMARY KEY," +
		"          card_number          TEXT     NOT NULL CHECK (card_number LIKE '____-____-____-____')," +
		"          card_name            TEXT     NOT NULL," +
		"          card_type            TEXT     NOT NULL CHECK (card_type IN ('debit', 'credit'))," +
		"          card_expiration_date TEXT     NOT NULL CHECK (card_expiration_date LIKE '__/__')," +
		"          card_address         INTEGER  NOT NULL REFERENCES address(address_id)" +
		"    );";

	private static final String insert_str =
		"INSERT INTO" +
		"    card (card_number," +
		"          card_name," +
		"          card_type," +
		"          card_expiration_date," +
		"          card_address)" +
		"    VALUES (?, ?, ?, ?, ?);";

	private static final String query_str =
		"SELECT card_id," +
		"       card_number," +
		"       card_name," +
		"       card_type," +
		"       card_expiration_date," +
		"       card_address" +
		"    FROM card" +
		"    WHERE card_id = ?;";

	private Database db;
	private AddressTable address_table;
	private PreparedStatement insert_stmt;
	private PreparedStatement query_stmt;

	public CardTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;
		address_table = db.getAddressTable();

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(table_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		query_stmt = conn.prepareStatement(query_str);
	}

	public void insert(Card card)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setString(1, card.getNumber());
		insert_stmt.setString(2, card.getName());
		insert_stmt.setString(3, card.getTypeStr());
		insert_stmt.setString(4, card.getExpirationDate());
		insert_stmt.setInt(5, card.getAddress().getID());

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();

		card.setID(db.lastInsertRowID());
	}

	public Card query(int card_id)
		throws SQLException,
		       SQLTimeoutException
	{
		Card card = new Card();

		query_stmt.setInt(1, card_id);

		ResultSet rset = query_stmt.executeQuery();

		card.setID(rset.getInt(1));
		card.setNumber(rset.getString(2));
		card.setName(rset.getString(3));
		card.setType(rset.getString(4));
		card.setExpirationDate(rset.getString(5));
		card.setAddress(address_table.query(rset.getInt(6)));

		query_stmt.clearParameters();

		return card;
	}
}

class CustomerTable
{
	private static final String table_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    customer (customer_id INTEGER PRIMARY KEY," +
		"              customer_name        TEXT           NOT NULL," +
		"              customer_email       TEXT    UNIQUE NOT NULL CHECK (customer_email LIKE '%@%.%')," +
		"              customer_password    TEXT           NOT NULL," +
		"              customer_phone       TEXT           NOT NULL CHECK (customer_phone LIKE '(___) ___ - ____')," +
		"              customer_notes       TEXT," +
		"              customer_address     INTEGER        NOT NULL REFERENCES address(address_id)," +
		"              customer_card        INTEGER                 REFERENCES card(card_id)" +
		"    );";

	private static final String insert_str =
		"INSERT INTO" +
		"    customer (customer_name," +
		"              customer_email," +
		"              customer_password," +
		"              customer_phone," +
		"              customer_notes," +
		"              customer_address," +
		"              customer_card)" +
		"    VALUES (?, ?, ?, ?, ?, ?, ?);";

	public static final String update_str =
		"UPDATE customer" +
		"    SET customer_name = ?," +
		"        customer_email = ?," +
		"        customer_password = ?," +
		"        customer_phone = ?," +
		"        customer_notes = ?," +
		"        customer_address = ?," +
		"        customer_card = ?" +
		"    WHERE customer_id = ?;";

	public static final String query_str =
		"SELECT customer_id," +
		"       customer_name," +
		"       customer_email," +
		"       customer_password," +
		"       customer_phone," +
		"       customer_notes," +
		"       customer_address," +
		"       customer_card" +
		"    FROM customer" +
		"    WHERE customer_id = ?;";

	public static final String query_email_str =
		"SELECT customer_id," +
		"       customer_name," +
		"       customer_email," +
		"       customer_password," +
		"       customer_phone," +
		"       customer_notes," +
		"       customer_address," +
		"       customer_card" +
		"    FROM customer" +
		"    WHERE customer_email = ?;";

	private Database db;
	private AddressTable address_table;
	private CardTable card_table;
	private PreparedStatement insert_stmt;
	private PreparedStatement update_stmt;
	private PreparedStatement query_stmt;
	private PreparedStatement query_email_stmt;

	public CustomerTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;
		address_table = db.getAddressTable();
		card_table = db.getCardTable();

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(table_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		update_stmt = conn.prepareStatement(update_str);
		query_stmt = conn.prepareStatement(query_str);
		query_email_stmt = conn.prepareStatement(query_email_str);
	}

	public void insert(Customer customer)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setString(1, customer.getName());
		insert_stmt.setString(2, customer.getEmail());
		insert_stmt.setString(3, customer.getPassword());
		insert_stmt.setString(4, customer.getPhone());
		insert_stmt.setString(5, customer.getNotes());
		insert_stmt.setInt(6, customer.getAddress().getID());
		insert_stmt.setInt(7, customer.getActiveCard().getID());

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();

		customer.setID(db.lastInsertRowID());
	}

	public void update(Customer customer)
		throws SQLException,
		       SQLTimeoutException
	{
		update_stmt.setString(1, customer.getName());
		update_stmt.setString(2, customer.getEmail());
		update_stmt.setString(3, customer.getPassword());
		update_stmt.setString(4, customer.getPhone());
		update_stmt.setString(5, customer.getNotes());
		update_stmt.setInt(6, customer.getAddress().getID());
		update_stmt.setInt(7, customer.getActiveCard().getID());
		update_stmt.setInt(8, customer.getID());

		update_stmt.executeUpdate();
		update_stmt.clearParameters();
	}

	public Customer query(int customer_id)
		throws SQLException,
		       SQLTimeoutException
	{
		Customer customer = new Customer();

		query_stmt.setInt(1, customer_id);

		ResultSet rset = query_stmt.executeQuery();

		customer.setID(rset.getInt(1));
		customer.setName(rset.getString(2));
		customer.setEmail(rset.getString(3));
		customer.setPassword(rset.getString(4));
		customer.setPhone(rset.getString(5));
		customer.setNotes(rset.getString(6));
		customer.setAddress(address_table.query(rset.getInt(7)));
		customer.setActiveCard(card_table.query(rset.getInt(8)));

		query_stmt.clearParameters();

		return customer;
	}

	public Customer queryEmail(String customer_email)
		throws SQLException,
		       SQLTimeoutException
	{
		Customer customer = new Customer();

		query_email_stmt.setString(1, customer_email);

		ResultSet rset = query_email_stmt.executeQuery();

		customer.setID(rset.getInt(1));
		customer.setName(rset.getString(2));
		customer.setEmail(rset.getString(3));
		customer.setPassword(rset.getString(4));
		customer.setPhone(rset.getString(5));
		customer.setNotes(rset.getString(6));
		customer.setAddress(address_table.query(rset.getInt(7)));
		customer.setActiveCard(card_table.query(rset.getInt(8)));

		query_email_stmt.clearParameters();

		return customer;
	}
}

class OrdersTable
{
	private static final String orders_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    orders (order_id       INTEGER PRIMARY KEY," +
		"            order_customer INTEGER NOT NULL REFERENCES customer(customer_id)," +
		"            order_address  INTEGER NOT NULL REFERENCES address(address_id)," +
		"            order_card     INTEGER NOT NULL REFERENCES card(card_id)," +
		"            order_datetime TEXT    NOT NULL CHECK (order_datetime LIKE '____-__-__T__:__:__%')" +
		"    );";

	private static final String orders_pizza_schema =
		"CREATE TABLE IF NOT EXISTS" +
		"    order_line (order_id   INTEGER NOT NULL," +
		"                pizza_id   INTEGER NOT NULL," +
		"                pizza_size TEXT    NOT NULL CHECK (pizza_size IN ('small', 'medium', 'large'))," +
		"                pizza_qty  INTEGER NOT NULL," +
		"                pizza_cost REAL    NOT NULL," +
		"                PRIMARY KEY (order_id, pizza_id, pizza_size)" +
		"    );";

	private static final String insert_str =
		"INSERT INTO" +
		"    orders (order_customer," +
		"            order_address," +
		"            order_card," +
		"            order_datetime)" +
		"    VALUES (?, ?, ?, ?);";

	private static final String query_by_customer_str =
		"SELECT order_id," +
		"       order_customer," +
		"       order_address," +
		"       order_card," +
		"       order_datetime" +
		"    FROM orders" +
		"    WHERE order_id = ?;";

	private static final String insert_order_line_str =
		"INSERT INTO" +
		"    order_line (order_id," +
		"                pizza_id," +
		"                pizza_size," +
		"                pizza_qty," +
		"                pizza_cost)" +
		"    VALUES (?, ?, ?, ?, ?);";

	private static final String query_order_line_by_order_str =
		"SELECT pizza_id," +
		"       pizza_size," +
		"       pizza_qty," +
		"       pizza_cost" +
		"    FROM order_line" +
		"    WHERE order_id = ?;";

	private Database db;
	private PizzaTable pizza_table;
	private AddressTable address_table;
	private CardTable card_table;
	private CustomerTable customer_table;

	private PreparedStatement insert_stmt;
	private PreparedStatement query_by_customer_stmt;
	private PreparedStatement insert_order_line_stmt;
	private PreparedStatement query_order_line_by_order_stmt;

	private static SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	public OrdersTable(Database db)
		throws SQLException,
		       SQLTimeoutException
	{
		this.db = db;
		pizza_table = db.getPizzaTable();
		address_table = db.getAddressTable();
		card_table = db.getCardTable();
		customer_table = db.getCustomerTable();

		Connection conn = db.getConnection();

		Statement stmt = conn.createStatement();
		stmt.execute(orders_schema);
		stmt.execute(orders_pizza_schema);

		insert_stmt = conn.prepareStatement(insert_str);
		query_by_customer_stmt = conn.prepareStatement(query_by_customer_str);
		insert_order_line_stmt = conn.prepareStatement(insert_order_line_str);
		query_order_line_by_order_stmt = conn.prepareStatement(query_order_line_by_order_str);
	}

	public void insert(Order order)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_stmt.setInt(1, order.getCustomer().getID());
		insert_stmt.setInt(2, order.getAddress().getID());
		insert_stmt.setInt(3, order.getCard().getID());
		insert_stmt.setString(4, date_formatter.format(order.getDate()));

		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();

		order.setID(db.lastInsertRowID());

		for (OrderLine line: order.getLines()) {
			insertOrderLine(order.getID(), line);
		}
	}

	public ArrayList<Order> query(int customer_id)
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Order> list = new ArrayList<Order>();

		query_by_customer_stmt.setInt(1, customer_id);

		ResultSet rset = query_by_customer_stmt.executeQuery();

		while (rset.next()) {
			Order order = new Order();

			order.setID(rset.getInt(1));
			order.setCustomer(customer_table.query(rset.getInt(2)));
			order.setAddress(address_table.query(rset.getInt(3)));
			order.setCard(card_table.query(rset.getInt(4)));

			try {
				order.setDate(date_formatter.parse(rset.getString(5)));
			} catch (ParseException e) {
				System.err.println("unable to parse date for order " + order.getID());
				order.setDate(Date.from(Instant.EPOCH));
			}

			order.setLines(queryOrderLines(order.getID()));

			list.add(order);
		}

		query_by_customer_stmt.clearParameters();

		return list;
	}

	private void insertOrderLine(int order_id, OrderLine line)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_order_line_stmt.setInt(1, order_id);
		insert_order_line_stmt.setInt(2, line.getPizza().getID());
		insert_order_line_stmt.setString(3, line.getSizeString());
		insert_order_line_stmt.setInt(4, line.getQuantity());
		insert_order_line_stmt.setDouble(5, line.getUnitCost());

		insert_order_line_stmt.executeUpdate();
		insert_order_line_stmt.clearParameters();
	}

	private ArrayList<OrderLine> queryOrderLines(int order_id)
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<OrderLine> list = new ArrayList<OrderLine>();

		query_order_line_by_order_stmt.setInt(1, order_id);

		ResultSet rset = query_order_line_by_order_stmt.executeQuery();

		while (rset.next()) {
			OrderLine line = new OrderLine();

			line.setPizza(pizza_table.query(rset.getInt(1)));
			line.setSize(rset.getString(2));
			line.setQuantity(rset.getInt(3));
			line.setUnitCost(rset.getDouble(4));

			list.add(line);
		}

		query_order_line_by_order_stmt.clearParameters();

		return list;
	}
}

public class Database
{
	private String path;
	private Connection conn;
	private PreparedStatement last_insert_rowid_stmt;

	private IngredientTable ingredient_table;
	private CrustTable crust_table;
	private SauceTable sauce_table;
	private CheeseTable cheese_table;
	private ToppingTable topping_table;
	private PizzaTable pizza_table;
	private AddressTable address_table;
	private CardTable card_table;
	private CustomerTable customer_table;
	private OrdersTable orders_table;

	public Database(String path)
	{
		this.path = path;
	}

	public Connection getConnection() { return conn; }
	public IngredientTable getIngredientTable() { return ingredient_table; }
	public CrustTable getCrustTable() { return crust_table; }
	public SauceTable getSauceTable() { return sauce_table; }
	public CheeseTable getCheeseTable() { return cheese_table; }
	public ToppingTable getToppingTable() { return topping_table; }
	public PizzaTable getPizzaTable() { return pizza_table; }
	public AddressTable getAddressTable() { return address_table; }
	public CardTable getCardTable() { return card_table; }
	public CustomerTable getCustomerTable() { return customer_table; }
	public OrdersTable getOrdersTable() { return orders_table; }

	public void open()
	{
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + path);

			conn.createStatement().execute("PRAGMA foreign_keys = 1;");

			last_insert_rowid_stmt = conn.prepareStatement("SELECT last_insert_rowid();");

			ingredient_table = new IngredientTable(this);
			crust_table = new CrustTable(this);
			sauce_table = new SauceTable(this);
			cheese_table = new CheeseTable(this);
			topping_table = new ToppingTable(this);
			pizza_table = new PizzaTable(this);
			address_table = new AddressTable(this);
			card_table = new CardTable(this);
			customer_table = new CustomerTable(this);
			orders_table = new OrdersTable(this);

		} catch (SQLException e) {
			System.err.println("Failed while initializing the database.");
			System.err.println(e);
			System.err.println("Tell Patrick to fix his code.");
			System.exit(1);
		}
	}

	public void close()
	{
		orders_table = null;
		customer_table = null;
		card_table = null;
		address_table = null;
		pizza_table = null;
		topping_table = null;
		cheese_table = null;
		sauce_table = null;
		crust_table = null;
		ingredient_table = null;
		last_insert_rowid_stmt = null;

		try {
			conn.close();

		} catch (SQLException e) {
			System.err.println("Failed while closing the database.");
			System.err.println(e);
		}

		conn = null;
	}

	public boolean isInitted()
	{
		int version = 0;

		try {
			PreparedStatement stmt = conn.prepareStatement("PRAGMA user_version;");
			ResultSet rset = stmt.executeQuery();

			version = rset.getInt(1);

		} catch (SQLException e) {
			System.err.println("Failed while getting user version.");
			System.err.println(e);
			System.err.println("Tell Patrick to fix his code.");
			System.exit(1);
		}

		return version != 0;
	}

	public void setInitted()
	{
		try {
			PreparedStatement stmt = conn.prepareStatement("PRAGMA user_version = 1;");
			stmt.executeUpdate();

		} catch (SQLException e) {
			System.err.println("Failed while setting user version.");
			System.err.println(e);
			System.err.println("Tell Patrick to fix his code.");
			System.exit(1);
		}
	}

	public int lastInsertRowID()
		throws SQLException,
		       SQLTimeoutException
	{
		return last_insert_rowid_stmt.executeQuery().getInt(1);
	}

	public void insertCrust(Crust crust)
	{
		if (crust.getID() != 0) {
			return;
		}

		try {
			ingredient_table.insert(crust);
			crust_table.insert(crust);

		} catch (SQLException e) {
			System.err.println("Failed to insert crust into database.");
			System.err.println(e);
		}
	}

	public ArrayList<Crust> lookupAllCrusts()
	{
		try {
			return crust_table.queryAll();

		} catch (SQLException e) {
			System.err.println("Failed to lookup all crusts.");
			System.err.println(e);

			return new ArrayList<Crust>();
		}
	}

	public void insertSauce(Sauce sauce)
	{
		if (sauce.getID() != 0) {
			return;
		}

		try {
			ingredient_table.insert(sauce);
			sauce_table.insert(sauce);

		} catch (SQLException e) {
			System.err.println("Failed to insert sauce into database.");
			System.err.println(e);
		}
	}

	public ArrayList<Sauce> lookupAllSauces()
	{
		try {
			return sauce_table.queryAll();

		} catch (SQLException e) {
			System.err.println("Failed to lookup all sauces.");
			System.err.println(e);

			return new ArrayList<Sauce>();
		}
	}

	public void insertCheese(Cheese cheese)
	{
		if (cheese.getID() != 0) {
			return;
		}

		try {
			ingredient_table.insert(cheese);
			cheese_table.insert(cheese);

		} catch (SQLException e) {
			System.err.println("Failed to insert cheese into database.");
			System.err.println(e);
		}
	}

	public ArrayList<Cheese> lookupAllCheeses()
	{
		try {
			return cheese_table.queryAll();

		} catch (SQLException e) {
			System.err.println("Failed to lookup all cheeses.");
			System.err.println(e);

			return new ArrayList<Cheese>();
		}
	}

	public void insertTopping(Topping topping)
	{
		if (topping.getID() != 0) {
			return;
		}

		try {
			ingredient_table.insert(topping);
			topping_table.insert(topping);

		} catch (SQLException e) {
			System.err.println("Failed to insert topping into database.");
			System.err.println(e);
		}
	}

	public ArrayList<Topping> lookupAllToppings()
	{
		try {
			return topping_table.queryAll();

		} catch (SQLException e) {
			System.err.println("Failed to lookup all toppings.");
			System.err.println(e);

			return new ArrayList<Topping>();
		}
	}

	public void insertPizza(Pizza pizza)
	{
		if (pizza.getID() != 0) {
			return;
		}

		try {
			insertCrust(pizza.getCrust());
			insertSauce(pizza.getSauce());

			for (Cheese cheese: pizza.getCheeses()) {
				insertCheese(cheese);
			}

			for (Topping topping: pizza.getToppings()) {
				insertTopping(topping);
			}

			pizza_table.insert(pizza);

		} catch (SQLException e) {
			System.err.println("Failed to insert pizza into database.");
			System.err.println(e);
		}
	}

	public ArrayList<Pizza> lookupAllPizzas()
	{
		try {
			return pizza_table.queryAll();

		} catch (SQLException e) {
			System.err.println("Failed while querying all pizzas.");
			System.err.println(e);

			return new ArrayList<Pizza>();
		}
	}

	public void insertAddress(Address address)
	{
		if (address.getID() != 0) {
			return;
		}

		try {
			address_table.insert(address);

		} catch (SQLException e) {
			System.err.println("Failed while inserting address.");
			System.err.println(e);
		}
	}

	public void insertCard(Card card)
	{
		if (card.getID() != 0) {
			return;
		}

		try {
			insertAddress(card.getAddress());

			card_table.insert(card);

		} catch (SQLException e) {
			System.err.println("Failed while inserting address.");
			System.err.println(e);
		}
	}

	public void insertCustomer(Customer customer)
	{
		if (customer.getID() != 0) {
			return;
		}

		try {
			insertAddress(customer.getAddress());
			insertCard(customer.getActiveCard());

			customer_table.insert(customer);

		} catch (SQLException e) {
			System.err.println("Failed while inserting customer.");
			System.err.println(e);
		}
	}

	public void updateCustomer(Customer customer)
	{
		if (customer.getID() == 0) {
			System.err.println("updateCustomer may not be called on a new customer.");
			return;
		}

		try {
			customer_table.update(customer);

		} catch (SQLException e) {
			System.err.println("Failed while updating customer.");
			System.err.println(e);
		}
	}

	public Customer loginCustomer(String email, String password)
		throws InvalidLoginException
	{
		Customer customer = null;

		try {
			customer = customer_table.queryEmail(email);
			if (customer == null ||
			    customer.getPassword().compareTo(password) != 0) {
				throw new InvalidLoginException(email, password);
			}

		} catch (SQLException e) {
			System.err.println("Failed while logging in customer.");
			System.err.println(e);

			throw new InvalidLoginException(email, password);
		}

		return customer;
	}

	public void insertOrder(Order order)
	{
		if (order.getID() != 0) {
			return;
		}

		try {
			insertAddress(order.getAddress());
			insertCard(order.getCard());
			insertCustomer(order.getCustomer());

			for (OrderLine line: order.getLines()) {
				insertPizza(line.getPizza());
			}

			/* Set the date of the order to the current time. */
			order.setDate(new Date());

			orders_table.insert(order);

		} catch (SQLException e) {
			System.err.println("Failed while inserting order.");
			System.err.println(e);
		}
	}

	public ArrayList<Order> lookupOrdersByCustomer(Customer customer)
	{
		if (customer.getID() == 0) {
			System.err.println("Invalid customer in lookupOrdersByCustomer");
			return new ArrayList<Order>();
		}

		try {
			return orders_table.query(customer.getID());

		} catch (SQLException e) {
			System.err.println("Failed while inserting order.");
			System.err.println(e);
			return new ArrayList<Order>();
		}
	}

	public static void main(String[] args)
	{
		Crust thin_crust = new Crust(0, "Thin Crust", 0.0, 0.0, 1.0);
		Crust reg_crust = new Crust(0, "Regular Crust", 0.0, 0.0, 1.0);

		Sauce marinara = new Sauce(0, "Marinara", 0.0, 0.0, 1.0);
		Sauce pesto = new Sauce(0, "Pesto", 0.0, 0.0, 1.0);

		Cheese mozzarella = new Cheese(0, "Mozzarella", 0.0, 0.0, 1.0);
		Cheese vegan = new Cheese(0, "Vegan", 0.0, 0.0, 1.0);

		Topping spinach = new Topping(0, "Spinach", 0.0, 0.0, 1.0);
		Topping tomatoes = new Topping(0, "Tomatoes", 0.0, 0.0, 1.0);
		Topping onions = new Topping(0, "Onions", 0.0, 0.0, 1.0);
		Topping mushrooms = new Topping(0, "Mushrooms", 0.0, 0.0, 1.0);

		Pizza pizza = new Pizza();
		pizza.setCrust(thin_crust);
		pizza.setSauce(pesto);
		pizza.getCheeses().add(vegan);
		pizza.getToppings().add(spinach);
		pizza.getToppings().add(tomatoes);
		pizza.getToppings().add(onions);
		pizza.getToppings().add(mushrooms);

		Address address = new Address(0, "658 Bartow Drive", null, "Dacula", "Ga", "30019");
		Card card = new Card(0, "1111-1111-1111-1111", "Kyrvin", Card.Type.DEBIT, "19/01", address);
		Customer kyrvin = new Customer(0, "Kyrvin", "kyrvin3@gmail.com", "password", "(678) 123 - 1234", null, address, card);

		ArrayList<OrderLine> order_lines = new ArrayList<OrderLine>();
		order_lines.add(new OrderLine(pizza, Pizza.Size.LARGE, 1, 0.0));

		Order order = new Order(0, kyrvin, address, card, new Date(), order_lines);

		Database db = new Database("/home/kyrvin/School/swe3313/pizzashop/src/test.db");
		db.open();

		db.insertAddress(new Address(0, "658 Bartow Drive", null, "Dacula", "Ga", "30019"));
		db.insertOrder(order);

		db.close();
	}
}
