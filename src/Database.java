import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.sqlite.SQLiteErrorCode;

class Schema
{
	public static final String foreign_keys = "PRAGMA foreign_keys = 1;";
	public static final String ingredient_table =
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

	public static final String crust_table =
		"CREATE TABLE IF NOT EXISTS" +
		"    crust (crust_id INTEGER PRIMARY KEY" +
		"               REFERENCES ingredient(ingredient_id)" +
		"    );";

	public static final String crust_view =
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

	public static final String sauce_table =
		"CREATE TABLE IF NOT EXISTS" +
		"    sauce (sauce_id INTEGER PRIMARY KEY" +
		"               REFERENCES ingredient(ingredient_id)" +
		"    );";

	public static final String sauce_view =
		"CREATE VIEW IF NOT EXISTS sauce_view (sauce_id, sauce_name, sauce_small_cost, sauce_medium_cost, sauce_large_cost) AS" +
		"	SELECT sauce_id, ingredient_name, ingredient_small_cost, ingredient_medium_cost, ingredient_large_cost" +
		"		FROM sauce LEFT JOIN ingredient ON sauce_id = ingredient_id;";

	public static final String cheese_table =
		"CREATE TABLE IF NOT EXISTS cheese (" +
		"	cheese_id INTEGER PRIMARY KEY REFERENCES ingredient(ingredient_id)" +
		");";

	public static final String cheese_view =
		"CREATE VIEW IF NOT EXISTS cheese_view (cheese_id, cheese_name, cheese_small_cost, cheese_medium_cost, cheese_large_cost) AS" +
		"	SELECT cheese_id, ingredient_name, ingredient_small_cost, ingredient_medium_cost, ingredient_large_cost" +
		"		FROM cheese LEFT JOIN ingredient ON cheese_id = ingredient_id;";

	public static final String topping_table =
		"CREATE TABLE IF NOT EXISTS topping (" +
		"	topping_id INTEGER PRIMARY KEY REFERENCES ingredient(ingredient_id)" +
		");";

	public static final String topping_view =
		"CREATE VIEW IF NOT EXISTS topping_view (topping_id, topping_name, topping_small_cost, topping_medium_cost, topping_large_cost) AS" +
		"	SELECT topping_id, ingredient_name, ingredient_small_cost, ingredient_medium_cost, ingredient_large_cost" +
		"		FROM topping LEFT JOIN ingredient ON topping_id = ingredient_id;";

	public static final String pizza_table =
		"CREATE TABLE IF NOT EXISTS pizza (" +
		"	pizza_id    INTEGER PRIMARY KEY," +
		"	pizza_name  TEXT," +
		"	pizza_crust INTEGER NOT NULL REFERENCES crust(crust_id)," +
		"	pizza_sauce INTEGER NOT NULL REFERENCES sauce(sauce_id)" +
		");";

	public static final String pizza_cheese_table =
		"CREATE TABLE IF NOT EXISTS pizza_cheese (" +
		"	pizza_id  INTEGER NOT NULL REFERENCES pizza(pizza_id)," +
		"	cheese_id INTEGER NOT NULL REFERENCES cheese(cheese_id)," +
		"	PRIMARY KEY (pizza_id, cheese_id)" +
		");";

	public static final String pizza_topping_table =
		"CREATE TABLE IF NOT EXISTS pizza_topping (" +
		"	pizza_id   INTEGER NOT NULL REFERENCES pizza(pizza_id)," +
		"	topping_id INTEGER NOT NULL REFERENCES topping(topping_id)," +
		"	PRIMARY KEY (pizza_id, topping_id)" +
		");";

	public static final String address_table =
		"CREATE TABLE IF NOT EXISTS address (" +
		"	address_id    INTEGER PRIMARY KEY," +
		"	address_line1 TEXT NOT NULL," +
		"	address_line2 TEXT," +
		"	address_city  TEXT NOT NULL," +
		"	address_state TEXT NOT NULL CHECK (address_state LIKE '__')," +
		"	address_zip   TEXT NOT NULL CHECK (address_zip LIKE '_____')," +
		"	CONSTRAINT address_unique UNIQUE (address_line1, address_line2, address_city, address_state, address_zip)" +
		");";

	public static final String card_table =
		"CREATE TABLE IF NOT EXISTS card (" +
		"	card_id              INTEGER  PRIMARY KEY," +
		"	card_number          TEXT     UNIQUE NOT NULL CHECK (card_number LIKE '____-____-____-____')," +
		"	card_name            TEXT            NOT NULL," +
		"	card_type            TEXT            NOT NULL CHECK (card_type IN ('debit', 'credit'))," +
		"	card_expiration_date TEXT            NOT NULL CHECK (card_expiration_date LIKE '__/__')," +
		"	card_address         INTEGER         NOT NULL REFERENCES address(address_id)" +
		");";

	public static final String customer_table =
		"CREATE TABLE IF NOT EXISTS customer (" +
		"	customer_id INTEGER PRIMARY KEY," +
		"	customer_name        TEXT           NOT NULL," +
		"	customer_email       TEXT    UNIQUE NOT NULL CHECK (customer_email LIKE '%@%.%')," +
		"	customer_password    TEXT           NOT NULL," +
		"	customer_phone       TEXT           NOT NULL CHECK (customer_phone LIKE '(___) ___-____')," +
		"	customer_notes       TEXT           NOT NULL," +
		"	customer_address     INTEGER        NOT NULL REFERENCES address(address_id)," +
		"	customer_card        INTEGER                 REFERENCES card(card_id)" +
		");";

	public static final String orders_table =
		"CREATE TABLE IF NOT EXISTS orders (" +
		"	order_id       INTEGER PRIMARY KEY," +
		"	order_customer INTEGER NOT NULL REFERENCES customer(customer_id)," +
		"	order_address  INTEGER NOT NULL REFERENCES address(address_id)," +
		"	order_card     INTEGER NOT NULL REFERENCES card(card_id)," +
		"	order_datetime TEXT    NOT NULL CHECK (order_datetime LIKE '____-__-__T__-__-__%')," +
		");";

	public static final String orders_pizza_table =
		"CREATE TABLE IF NOT EXISTS orders_pizza (" +
		"	order_id   INTEGER NOT NULL," +
		"	pizza_id   INTEGER NOT NULL," +
		"       pizza_size TEXT    NOT NULL CHECK (pizza_size IN ('small', 'medium', 'large'))," +
		"       pizza_qty  INTEGER NOT NULL," +
		"       pizza_cost REAL    NOT NULL," +
		"	PRIMARY KEY (order_id, pizza_id)" +
		");";

	public static final String pizza_update_deletes =
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

	private static final String[] schema_queries = { foreign_keys,
	                                                 ingredient_table,
	                                                 crust_table,
	                                                 crust_view,
	                                                 sauce_table,
	                                                 sauce_view,
	                                                 cheese_table,
	                                                 cheese_view,
	                                                 topping_table,
	                                                 topping_view,
							 pizza_table,
	                                                 pizza_cheese_table,
	                                                 pizza_topping_table,
	                                                 address_table,
	                                                 card_table,
	                                                 customer_table,
	                                                 orders_table,
	                                                 orders_pizza_table,
							 pizza_update_deletes
	                                               };

	/*
	 * Executes the schema on the given connection.
	 */
	public static void execute(Connection conn)
	{
		try {
			Statement stmt = conn.createStatement();
			//for (int i = 0; i < queries.length; i++) {
			for (String query : schema_queries) {
				stmt.execute(query);
			}
		} catch (SQLException e) {
			System.err.println(e);
			System.err.println("Tell Patrick to fix his code.");
			System.exit(100);
		}
	}

	/*
	 * Last Insert Row ID Statements
	 */
	public static final String last_insert_rowid =
		"SELECT last_insert_rowid();";

	/*
	 * Ingredient Statements
	 */
	public static final String insert_ingredient =
		"INSERT INTO" +
		"    ingredient (ingredient_name," +
		"                ingredient_small_cost," +
		"                ingredient_medium_cost," +
		"                ingredient_large_cost)" +
		"    VALUES (?, ?, ?, ?);";

	public static final String update_ingredient =
		"UPDATE ingredient" +
		"    SET ingredient_name = ?," +
		"        ingredient_small_cost = ?," +
		"        ingredient_medium_cost = ?," +
		"        ingredient_large_cost = ?" +
		"    WHERE ingredient_id = ?;";

	/*
	 * Crust Statements
	 */
	public static final String insert_crust =
		"INSERT INTO" +
		"    crust (crust_id)" +
		"    SELECT ingredient_id" +
		"        FROM ingredient" +
		"        WHERE ingredient_name = ?;";

	public static final String query_crust =
		"SELECT crust_id," +
		"       crust_name," +
		"       crust_small_cost," +
		"       crust_medium_cost," +
		"       crust_large_cost" +
		"    FROM crust_view" +
		"    WHERE crust_id = ?;";

	public static final String query_all_crusts =
		"SELECT crust_id," +
		"       crust_name," +
		"       crust_small_cost," +
		"       crust_medium_cost," +
		"       crust_large_cost" +
		"    FROM crust_view;";

	/*
	 * Sauce Statements
	 */
	public static final String insert_sauce =
		"INSERT INTO" +
		"    sauce (sauce_id)" +
		"    SELECT ingredient_id" +
		"        FROM ingredient" +
		"        WHERE ingredient_name = ?;";

	public static final String query_sauce =
		"SELECT sauce_id," +
		"       sauce_name," +
		"       sauce_small_cost," +
		"       sauce_medium_cost," +
		"       sauce_large_cost" +
		"    FROM sauce_view" +
		"    WHERE sauce_id = ?;";

	public static final String query_all_sauces =
		"SELECT sauce_id," +
		"       sauce_name," +
		"       sauce_small_cost," +
		"       sauce_medium_cost," +
		"       sauce_large_cost" +
		"    FROM sauce_view;";

	/*
	 * Cheese Statements
	 */
	public static final String insert_cheese =
		"INSERT INTO" +
		"    cheese (cheese_id)" +
		"    SELECT ingredient_id" +
		"        FROM ingredient" +
		"        WHERE ingredient_name = ?;";

	public static final String query_all_cheeses =
		"SELECT cheese_id," +
		"       cheese_name," +
		"       cheese_small_cost," +
		"       cheese_medium_cost," +
		"       cheese_large_cost" +
		"    FROM cheese_view;";

	public static final String query_cheeses_by_pizza =
		"SELECT cheese_view.cheese_id," +
		"       cheese_name," +
		"       cheese_small_cost," +
		"       cheese_medium_cost," +
		"       cheese_large_cost" +
		"    FROM pizza_cheese LEFT JOIN cheese_view" +
		"             ON pizza_cheese.cheese_id = cheese_view.cheese_id" +
		"    WHERE pizza_id = ?;";

	/*
	 * Topping Statements
	 */
	public static final String insert_topping =
		"INSERT INTO" +
		"    topping (topping_id)" +
		"    SELECT ingredient_id" +
		"        FROM ingredient" +
		"        WHERE ingredient_name = ?;";

	public static final String query_all_toppings =
		"SELECT topping_id," +
		"       topping_name," +
		"       topping_small_cost," +
		"       topping_medium_cost," +
		"       topping_large_cost" +
		"    FROM topping_view;";

	public static final String query_toppings_by_pizza =
		"SELECT topping_view.topping_id," +
		"       topping_name," +
		"       topping_small_cost," +
		"       topping_medium_cost," +
		"       topping_large_cost" +
		"    FROM pizza_topping LEFT JOIN topping_view" +
		"             ON pizza_topping.topping_id = topping_view.topping_id" +
		"    WHERE pizza_id = ?;";

	/*
	 * Pizza Statements
	 */
	public static final String insert_pizza =
		"INSERT INTO" +
		"    pizza (pizza_name," +
		"           pizza_crust," +
		"           pizza_sauce)" +
		"    VALUES (?, ?, ?);";

	public static final String update_pizza =
		"UPDATE pizza" +
		"    SET pizza_name = ?," +
		"        pizza_crust = ?," +
		"        pizza_sauce = ?" +
		"    WHERE pizza_id = ?;";

	public static final String query_pizza =
		"SELECT pizza_id," +
		"       pizza_name," +
		"       pizza_crust," +
		"       pizza_sauce" +
		"    FROM pizza" +
		"    WHERE pizza_id = ?;";

	public static final String query_all_pizzas =
		"SELECT pizza_id," +
		"       pizza_name," +
		"       pizza_crust," +
		"       pizza_sauce" +
		"    FROM pizza;";

	/*
	 * Pizza Cheese Statements
	 */
	public static final String insert_pizza_cheese =
		"INSERT INTO" +
		"    pizza_cheese (pizza_id," +
		"                  cheese_id)" +
		"    VALUES (?, ?);";

	/*
	 * Pizza Topping Statements
	 */
	public static final String insert_pizza_topping =
		"INSERT INTO" +
		"    pizza_topping (pizza_id," +
		"                   topping_id)" +
		"    VALUES (?, ?);";

	/*
	 * Address Statements
	 */
	public static final String insert_address =
		"INSERT INTO" +
		"    address (address_line1," +
		"             address_line2," +
		"             address_city," +
		"             address_state," +
		"             address_zip)" +
		"    VALUES (?, ?, ?, ?, ?);";

	public static final String query_address =
		"SELECT address_id," +
		"       address_line1," +
		"       address_line2," +
		"       address_city," +
		"       address_state," +
		"       address_zip" +
		"    FROM address" +
		"    WHERE address_id = ?";

	/*
	 * Card Statements
	 */
	public static final String insert_card =
		"INSERT INTO" +
		"    card (card_number," +
		"          card_name," +
		"          card_type," +
		"          card_expiration_date," +
		"          card_address)" +
		"    VALUES (?, ?, ?, ?, ?);";

	public static final String query_card =
		"SELECT card_id," +
		"       card_number," +
		"       card_name," +
		"       card_type," +
		"       card_expiration_date," +
		"       card_address" +
		"    FROM card" +
		"    WHERE card_id = ?;";

	/*
	 * Customer Statements
	 */
	public static final String insert_customer =
		"INSERT INTO" +
		"    customer (customer_name," +
		"              customer_email," +
		"              customer_password," +
		"              customer_phone," +
		"              customer_notes," +
		"              customer_address," +
		"              customer_card)" +
		"    VALUES (?, ?, ?, ?, ?, ?, ?);";

	public static final String update_customer =
		"UPDATE customer" +
		"    SET customer_name = ?," +
		"        customer_email = ?," +
		"        customer_password = ?," +
		"        customer_phone = ?," +
		"        customer_notes = ?," +
		"        customer_address = ?," +
		"        customer_card = ?" +
		"    WHERE customer_id = ?;";


	public static final String query_customer =
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

	public static final String query_customer_email =
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

	/*
	 * Order Pizza Statements
	 */
	public static final String insert_order_pizza =
		"INSERT INTO" +
		"    order_pizza (order_id," +
		"                 pizza_id," +
		"                 pizza_size," +
		"                 pizza_qty," +
		"                 pizza_cost)" +
		"    VALUES (?, ?, ?, ?, ?);";

	public static final String query_order_pizzas_by_order =
		"SELECT pizza_id," +
		"       pizza_size," +
		"       pizza_qty," +
		"       pizza_cost" +
		"    FROM pizza_order" +
		"    WHERE order_id = ?;";

	/*
	 * Orders Statements
	 */
	public static final String insert_order =
		"INSERT INTO" +
		"    orders (order_customer," +
		"            order_address," +
		"            order_card," +
		"            order_datetime)" +
		"    VALUES (?, ?, ?, ?);";

	public static final String query_orders_by_customer =
		"SELECT order_id," +
		"       order_customer," +
		"       order_address," +
		"       order_card," +
		"       order_datetime" +
		"    FROM orders" +
		"    WHERE order_id = ?;";
}

public final class Database
{
	private String     path;
	private Connection conn;

	/* Last Insert Row ID Statement */
	private PreparedStatement last_insert_rowid_stmt;

	/* Ingredient Prepared Statements */
	private PreparedStatement insert_ingredient_stmt;
	private PreparedStatement update_ingredient_stmt;

	/* Crust Prepared Statements */
	private PreparedStatement insert_crust_stmt;
	private PreparedStatement query_crust_stmt;
	private PreparedStatement query_all_crusts_stmt;

	/* Sauce Prepared Statements */
	private PreparedStatement insert_sauce_stmt;
	private PreparedStatement query_sauce_stmt;
	private PreparedStatement query_all_sauces_stmt;

	/* Cheese Prepared Statements */
	private PreparedStatement insert_cheese_stmt;
	private PreparedStatement query_all_cheeses_stmt;
	private PreparedStatement query_cheeses_by_pizza_stmt;

	/* Topping Prepared Statements */
	private PreparedStatement insert_topping_stmt;
	private PreparedStatement query_all_toppings_stmt;
	private PreparedStatement query_toppings_by_pizza_stmt;

	/* Pizza Prepared Statements */
	private PreparedStatement insert_pizza_stmt;
	private PreparedStatement update_pizza_stmt;
	private PreparedStatement query_pizza_stmt;
	private PreparedStatement query_all_pizzas_stmt;

	/* Pizza Cheese Prepared Statements */
	private PreparedStatement insert_pizza_cheese_stmt;

	/* Pizza Topping Prepared Statements */
	private PreparedStatement insert_pizza_topping_stmt;

	/* Address Prepared Statements */
	private PreparedStatement insert_address_stmt;
	private PreparedStatement query_address_stmt;

	/* Card Prepared Statement */
	private PreparedStatement insert_card_stmt;
	private PreparedStatement query_card_stmt;

	/* Customer Prepared Statements */
	private PreparedStatement insert_customer_stmt;
	private PreparedStatement update_customer_stmt;
	private PreparedStatement query_customer_stmt;
	private PreparedStatement query_customer_email_stmt;

	/* Order Pizza Prepared Statements */
	private PreparedStatement insert_order_pizza_stmt;
	private PreparedStatement query_order_pizzas_by_order_stmt;

	/* Orders Prepared Statements */
	private PreparedStatement insert_order_stmt;
	private PreparedStatement query_orders_by_customer_stmt;

	private static SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	public Database(String path)
	{
		this.path = path;
	}

	public void open()
		throws SQLException
	{
		conn = DriverManager.getConnection("jdbc:sqlite:" + path);

		Schema.execute(conn);

		last_insert_rowid_stmt = conn.prepareStatement(Schema.last_insert_rowid);

		insert_ingredient_stmt = conn.prepareStatement(Schema.insert_ingredient);
		update_ingredient_stmt = conn.prepareStatement(Schema.update_ingredient);

		insert_crust_stmt = conn.prepareStatement(Schema.insert_crust);
		query_crust_stmt = conn.prepareStatement(Schema.query_crust);
		query_all_crusts_stmt = conn.prepareStatement(Schema.query_all_crusts);

		insert_sauce_stmt = conn.prepareStatement(Schema.insert_sauce);
		query_sauce_stmt = conn.prepareStatement(Schema.query_sauce);
		query_all_sauces_stmt = conn.prepareStatement(Schema.query_all_sauces);

		insert_cheese_stmt = conn.prepareStatement(Schema.insert_cheese);
		query_all_cheeses_stmt = conn.prepareStatement(Schema.query_all_cheeses);
		query_cheeses_by_pizza_stmt = conn.prepareStatement(Schema.query_cheeses_by_pizza);

		insert_topping_stmt = conn.prepareStatement(Schema.insert_topping);
		query_all_toppings_stmt = conn.prepareStatement(Schema.query_all_toppings);
		query_toppings_by_pizza_stmt = conn.prepareStatement(Schema.query_toppings_by_pizza);

		insert_pizza_stmt = conn.prepareStatement(Schema.insert_pizza);
		update_pizza_stmt = conn.prepareStatement(Schema.update_pizza);
		query_pizza_stmt = conn.prepareStatement(Schema.query_pizza);
		query_all_pizzas_stmt = conn.prepareStatement(Schema.query_all_pizzas);

		insert_pizza_cheese_stmt = conn.prepareStatement(Schema.insert_pizza_cheese);

		insert_pizza_topping_stmt = conn.prepareStatement(Schema.insert_pizza_topping);

		insert_address_stmt = conn.prepareStatement(Schema.insert_address);
		query_address_stmt = conn.prepareStatement(Schema.query_address);

		insert_card_stmt = conn.prepareStatement(Schema.insert_card);
		query_card_stmt = conn.prepareStatement(Schema.query_card);

		insert_customer_stmt = conn.prepareStatement(Schema.insert_customer);
		update_customer_stmt = conn.prepareStatement(Schema.update_customer);
		query_customer_stmt = conn.prepareStatement(Schema.query_customer);
		query_customer_email_stmt = conn.prepareStatement(Schema.query_customer_email);

		insert_order_pizza_stmt = conn.prepareStatement(Schema.insert_order_pizza);
		query_order_pizzas_by_order_stmt = conn.prepareStatement(Schema.query_order_pizzas_by_order);

		insert_order_stmt = conn.prepareStatement(Schema.insert_order);
		query_orders_by_customer_stmt = conn.prepareStatement(Schema.query_orders_by_customer);

	}

	public void close()
		throws SQLException
	{
		conn.close();
	}

	private int lastInsertRowID()
		throws SQLException,
		       SQLTimeoutException
	{
		return last_insert_rowid_stmt.executeQuery().getInt(1);
	}

	/*
	 * Ingredient Methods
	 */
	private void insertIngredient(Ingredient ingredient)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_ingredient_stmt.setString(1, ingredient.getName());
		insert_ingredient_stmt.setDouble(2, ingredient.getSmallCost());
		insert_ingredient_stmt.setDouble(3, ingredient.getMediumCost());
		insert_ingredient_stmt.setDouble(4, ingredient.getLargeCost());

		insert_ingredient_stmt.executeUpdate();
		insert_ingredient_stmt.clearParameters();

		ingredient.setID(lastInsertRowID());
	}

	private void updateIngredient(Ingredient ingredient)
		throws SQLException,
		       SQLTimeoutException
	{
		update_ingredient_stmt.setString(1, ingredient.getName());
		update_ingredient_stmt.setDouble(2, ingredient.getSmallCost());
		update_ingredient_stmt.setDouble(3, ingredient.getMediumCost());
		update_ingredient_stmt.setDouble(4, ingredient.getLargeCost());
		update_ingredient_stmt.setInt(5, ingredient.getID());

		update_ingredient_stmt.executeUpdate();
		update_ingredient_stmt.clearParameters();
	}

	private void readIngredient(ResultSet rset, Ingredient ingredient)
		throws SQLException
	{
		ingredient.setID(rset.getInt(1));
		ingredient.setName(rset.getString(2));
		ingredient.setSmallCost(rset.getDouble(3));
		ingredient.setMediumCost(rset.getDouble(4));
		ingredient.setLargeCost(rset.getDouble(5));
	}

	/*
	 * Crust Methods
	 */
	public void insertCrust(Crust crust)
		throws SQLException,
		       SQLTimeoutException
	{
		insertIngredient(crust);

		insert_crust_stmt.setString(1, crust.getName());

		insert_crust_stmt.executeUpdate();
		insert_crust_stmt.clearParameters();
	}

	public void updateCrust(Crust crust)
		throws SQLException,
		       SQLTimeoutException
	{
		updateIngredient(crust);
	}

	private Crust readCrust(ResultSet rset)
		throws SQLException
	{
		Crust crust = new Crust();

		readIngredient(rset, crust);

		return crust;
	}

	private Crust lookupCrust(int crust_id)
		throws SQLException,
		       SQLTimeoutException
	{
		query_crust_stmt.setInt(1, crust_id);

		ResultSet rset = query_crust_stmt.executeQuery();

		Crust crust = readCrust(rset);

		query_crust_stmt.clearParameters();

		return crust;
	}

	public ArrayList<Crust> lookupAllCrusts()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Crust> list = new ArrayList<Crust>();

		ResultSet rset = query_all_crusts_stmt.executeQuery();

		while (rset.next()) {
			list.add(readCrust(rset));
		}

		return list;
	}

	/*
	 * Sauce Methods
	 */
	public void insertSauce(Sauce sauce)
		throws SQLException,
		       SQLTimeoutException
	{
		insertIngredient(sauce);

		insert_sauce_stmt.setString(1, sauce.getName());

		insert_sauce_stmt.executeUpdate();
		insert_sauce_stmt.clearParameters();
	}

	public void updateSauce(Sauce sauce)
		throws SQLException,
		       SQLTimeoutException
	{
		updateIngredient(sauce);
	}

	private Sauce readSauce(ResultSet rset)
		throws SQLException
	{
		Sauce sauce = new Sauce();

		readIngredient(rset, sauce);

		return sauce;
	}

	private Sauce lookupSauce(int sauce_id)
		throws SQLException,
		       SQLTimeoutException
	{
		query_sauce_stmt.setInt(1, sauce_id);

		ResultSet rset = query_sauce_stmt.executeQuery();

		Sauce sauce = readSauce(rset);

		query_sauce_stmt.clearParameters();

		return sauce;
	}

	public ArrayList<Sauce> lookupAllSauces()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Sauce> list = new ArrayList<Sauce>();

		ResultSet rset = query_all_sauces_stmt.executeQuery();

		while (rset.next()) {
			list.add(readSauce(rset));
		}

		return list;
	}

	/*
	 * Cheese Methods
	 */
	public void insertCheese(Cheese cheese)
		throws SQLException,
		       SQLTimeoutException
	{
		insertIngredient(cheese);

		insert_cheese_stmt.setString(1, cheese.getName());

		insert_cheese_stmt.executeUpdate();
		insert_cheese_stmt.clearParameters();
	}

	public void updateCheese(Cheese cheese)
		throws SQLException,
		       SQLTimeoutException
	{
		updateIngredient(cheese);
	}

	private Cheese readCheese(ResultSet rset)
		throws SQLException
	{
		Cheese cheese = new Cheese();

		readIngredient(rset, cheese);

		return cheese;
	}

	public ArrayList<Cheese> lookupAllCheeses()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Cheese> list = new ArrayList<Cheese>();

		ResultSet rset = query_all_cheeses_stmt.executeQuery();

		while (rset.next()) {
			list.add(readCheese(rset));
		}


		return list;
	}

	private ArrayList<Cheese> lookupCheesesByPizza(int pizza_id)
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Cheese> list = new ArrayList<Cheese>();

		query_cheeses_by_pizza_stmt.setInt(1, pizza_id);

		ResultSet rset = query_cheeses_by_pizza_stmt.executeQuery();

		while (rset.next()) {
			list.add(readCheese(rset));
		}

		query_cheeses_by_pizza_stmt.clearParameters();

		return list;
	}

	/*
	 * Topping Methods
	 */
	public void insertTopping(Topping topping)
		throws SQLException,
		       SQLTimeoutException
	{
		insertIngredient(topping);

		insert_topping_stmt.setString(1, topping.getName());

		insert_topping_stmt.executeUpdate();
		insert_topping_stmt.clearParameters();
	}

	public void updateTopping(Topping topping)
		throws SQLException,
		       SQLTimeoutException
	{
		updateIngredient(topping);
	}

	private Topping readTopping(ResultSet rset)
		throws SQLException
	{
		Topping topping = new Topping();

		readIngredient(rset, topping);

		return topping;
	}

	public ArrayList<Topping> lookupAllToppings()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Topping> list = new ArrayList<Topping>();

		ResultSet rset = query_all_toppings_stmt.executeQuery();

		while (rset.next()) {
			list.add(readTopping(rset));
		}

		return list;
	}

	private ArrayList<Topping> lookupToppingsByPizza(int pizza_id)
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Topping> list = new ArrayList<Topping>();

		query_toppings_by_pizza_stmt.setInt(1, pizza_id);

		ResultSet rset = query_toppings_by_pizza_stmt.executeQuery();

		while (rset.next()) {
			list.add(readTopping(rset));
		}

		query_toppings_by_pizza_stmt.clearParameters();

		return list;

	}

	/*
	 * Pizza Methods
	 */
	public void insertPizza(Pizza pizza)
		throws SQLException,
		       SQLTimeoutException
	{
		if (pizza.getName() == null || pizza.getName().compareTo("") == 0) {
			insert_pizza_stmt.setNull(1, JDBCType.VARCHAR.getVendorTypeNumber());
		} else {
			insert_pizza_stmt.setString(1, pizza.getName());
		}

		insert_pizza_stmt.setInt(2, pizza.getCrust().getID());
		insert_pizza_stmt.setInt(3, pizza.getSauce().getID());

		insert_pizza_stmt.execute();
		insert_pizza_stmt.clearParameters();

		pizza.setID(lastInsertRowID());

		for (Cheese cheese: pizza.getCheeses()) {
			insertPizzaCheese(pizza.getID(), cheese.getID());
		}

		for (Topping topping: pizza.getToppings()) {
			insertPizzaTopping(pizza.getID(), topping.getID());
		}
	}

	public void updatePizza(Pizza pizza)
		throws SQLException,
		       SQLTimeoutException
	{
		if (pizza.getName().compareTo("") == 0) {
			update_pizza_stmt.setNull(1, JDBCType.VARCHAR.getVendorTypeNumber());
		} else {
			update_pizza_stmt.setString(1, pizza.getName());
		}

		update_pizza_stmt.setInt(2, pizza.getCrust().getID());
		update_pizza_stmt.setInt(3, pizza.getSauce().getID());
		update_pizza_stmt.setInt(4, pizza.getID());

		update_pizza_stmt.execute();
		update_pizza_stmt.clearParameters();

		/* Reinsert cheeses and toppings */
		for (Cheese cheese: pizza.getCheeses()) {
			insertPizzaCheese(pizza.getID(), cheese.getID());
		}

		for (Topping topping: pizza.getToppings()) {
			insertPizzaTopping(pizza.getID(), topping.getID());
		}
	}

	private Pizza readPizza(ResultSet rset)
		throws SQLException
	{
		Pizza pizza = new Pizza();

		pizza.setID(rset.getInt(1));
		pizza.setName(rset.getString(2));
		pizza.setCrust(lookupCrust(rset.getInt(3)));
		pizza.setSauce(lookupSauce(rset.getInt(4)));
		pizza.setCheeses(lookupCheesesByPizza(pizza.getID()));
		pizza.setToppings(lookupToppingsByPizza(pizza.getID()));

		return pizza;
	}

	public Pizza lookupPizza(int pizza_id)
		throws SQLException,
		       SQLTimeoutException
	{

		query_pizza_stmt.setInt(1, pizza_id);

		ResultSet rset = query_pizza_stmt.executeQuery();

		Pizza pizza = readPizza(rset);

		query_pizza_stmt.clearParameters();

		return pizza;
	}

	public ArrayList<Pizza> lookupAllPizzas()
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Pizza> list = new ArrayList<Pizza>();

		ResultSet rset = query_all_pizzas_stmt.executeQuery();

		while (rset.next()) {
			list.add(readPizza(rset));
		}

		return list;
	}

	/*
	 * Pizza Cheese Methods
	 */
	private void insertPizzaCheese(int pizza_id, int cheese_id)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_pizza_cheese_stmt.setInt(1, pizza_id);
		insert_pizza_cheese_stmt.setInt(2, cheese_id);

		insert_pizza_cheese_stmt.executeUpdate();

		insert_pizza_cheese_stmt.clearParameters();
	}

	/*
	 * Pizza Topping Methods
	 */
	private void insertPizzaTopping(int pizza_id, int topping_id)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_pizza_topping_stmt.setInt(1, pizza_id);
		insert_pizza_topping_stmt.setInt(2, topping_id);

		insert_pizza_topping_stmt.executeUpdate();

		insert_pizza_topping_stmt.clearParameters();
	}

	/*
	 * Address Methods
	 */
	public void insertAddress(Address address)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_address_stmt.setString(1, address.getLine1());
		insert_address_stmt.setString(2, address.getLine2());
		insert_address_stmt.setString(3, address.getCity());
		insert_address_stmt.setString(4, address.getState());
		insert_address_stmt.setString(5, address.getZip());

		insert_address_stmt.executeUpdate();
		insert_address_stmt.clearParameters();

		address.setID(lastInsertRowID());
	}

	private Address readAddress(ResultSet rset)
		throws SQLException,
		       SQLTimeoutException
	{
		Address address = new Address();

		address.setID(rset.getInt(1));
		address.setLine1(rset.getString(2));
		address.setLine2(rset.getString(3));
		address.setCity(rset.getString(4));
		address.setState(rset.getString(5));
		address.setZip(rset.getString(6));

		return address;
	}

	private Address lookupAddress(int address_id)
		throws SQLException,
		       SQLTimeoutException
	{
		query_address_stmt.setInt(1, address_id);

		ResultSet rset = query_address_stmt.executeQuery();

		Address address = readAddress(rset);

		query_address_stmt.clearParameters();

		return address;
	}

	/*
	 * Card Methods
	 */
	public void insertCard(Card card)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_card_stmt.setString(1, card.getNumber());
		insert_card_stmt.setString(2, card.getName());

		switch (card.getType()) {
		case CREDIT:
			insert_card_stmt.setString(3, "credit");
			break;

		case DEBIT:
			insert_card_stmt.setString(3, "debit");
			break;

		case UNKNOWN:
			/* Let Sqlite throw the exception */
			insert_card_stmt.setString(3, "unknown");
			break;
		}

		insert_card_stmt.setString(4, card.getExpirationDate());
		insert_card_stmt.setInt(5, card.getAddress().getID());

		insert_card_stmt.executeUpdate();
		insert_card_stmt.clearParameters();

		card.setID(lastInsertRowID());
	}

	private Card readCard(ResultSet rset)
		throws SQLException,
		       SQLTimeoutException
	{
		Card card = new Card();

		card.setID(rset.getInt(1));
		card.setNumber(rset.getString(2));
		card.setName(rset.getString(3));

		String type = rset.getString(4);
		if (type.compareTo("debit") == 0) {
			card.setType(Card.Type.DEBIT);

		} else if (type.compareTo("credit") == 0) {
			card.setType(Card.Type.CREDIT);

		} else {
			/* unreachable */
			System.err.println("invalid card type returned from database: " + type);
			System.exit(1);
		}

		card.setExpirationDate(rset.getString(5));

		card.setAddress(lookupAddress(rset.getInt(6)));

		return card;
	}

	private Card lookupCard(int card_id)
		throws SQLException,
		       SQLTimeoutException
	{
		query_card_stmt.setInt(1, card_id);

		ResultSet rset = query_card_stmt.executeQuery();

		Card card = readCard(rset);

		query_card_stmt.clearParameters();

		return card;
	}

	/*
	 * Customer Methods
	 */
	public void insertCustomer(Customer customer)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_customer_stmt.setString(1, customer.getName());
		insert_customer_stmt.setString(2, customer.getEmail());
		insert_customer_stmt.setString(3, customer.getPassword());
		insert_customer_stmt.setString(4, customer.getPhone());
		insert_customer_stmt.setString(5, customer.getNotes());
		insert_customer_stmt.setInt(6, customer.getAddress().getID());
		insert_customer_stmt.setInt(7, customer.getActiveCard().getID());

		insert_customer_stmt.executeUpdate();
		insert_customer_stmt.clearParameters();

		customer.setID(lastInsertRowID());
	}

	public void updateCustomer(Customer customer)
		throws SQLException,
		       SQLTimeoutException
	{
		update_customer_stmt.setString(1, customer.getName());
		update_customer_stmt.setString(2, customer.getEmail());
		update_customer_stmt.setString(3, customer.getPassword());
		update_customer_stmt.setString(4, customer.getPhone());
		update_customer_stmt.setString(5, customer.getNotes());
		update_customer_stmt.setInt(6, customer.getAddress().getID());
		update_customer_stmt.setInt(7, customer.getActiveCard().getID());
		update_customer_stmt.setInt(8, customer.getID());

		update_customer_stmt.executeUpdate();
		update_customer_stmt.clearParameters();
	}

	private Customer readCustomer(ResultSet rset)
		throws SQLException
	{
		Customer customer = new Customer();

		customer.setID(rset.getInt(1));
		customer.setName(rset.getString(2));
		customer.setEmail(rset.getString(3));
		customer.setPassword(rset.getString(4));
		customer.setPhone(rset.getString(5));
		customer.setNotes(rset.getString(6));
		customer.setAddress(lookupAddress(rset.getInt(7)));
		customer.setActiveCard(lookupCard(rset.getInt(8)));

		return customer;
	}

	private Customer lookupCustomer(int customer_id)
		throws SQLException,
		       SQLTimeoutException
	{
		query_customer_stmt.setInt(1, customer_id);

		ResultSet rset = query_customer_stmt.executeQuery();

		Customer customer = readCustomer(rset);

		query_customer_stmt.clearParameters();

		return customer;
	}

	public Customer loginCustomer(String email, String password)
		throws InvalidLoginException,
		       SQLException,
		       SQLTimeoutException
	{
		query_customer_email_stmt.setString(1, email);

		ResultSet rset = query_customer_email_stmt.executeQuery();

		if (!rset.next()) {
			query_customer_email_stmt.clearParameters();
			throw new InvalidLoginException(email, password);
		}

		Customer customer = readCustomer(rset);

		query_customer_email_stmt.clearParameters();

		if (password.compareTo(customer.getPassword()) != 0) {
			throw new InvalidLoginException(email, password);
		}

		return customer;
	}

	/*
	 * Order Pizza Methods
	 */
	private void insertOrderLines(Order order)
		throws SQLException,
		       SQLTimeoutException
	{
		for (OrderLine line: order.getLines()) {
			Pizza pizza = line.getPizza();

			/* size, qty, cost */
			insert_order_pizza_stmt.setInt(1, order.getID());
			insert_order_pizza_stmt.setInt(2, line.getPizza().getID());
			insert_order_pizza_stmt.setString(3, line.getSizeString());
			insert_order_pizza_stmt.setInt(4, line.getQuantity());
			insert_order_pizza_stmt.setDouble(5, line.getUnitCost());

			insert_order_pizza_stmt.executeUpdate();
			insert_order_pizza_stmt.clearParameters();
		}
	}

	private OrderLine readOrderLine(ResultSet rset)
		throws SQLException,
		       SQLTimeoutException
	{
		OrderLine line = new OrderLine();

		line.setPizza(lookupPizza(rset.getInt(1)));
		line.setSize(rset.getString(2));
		line.setQuantity(rset.getInt(3));
		line.setUnitCost(rset.getDouble(4));

		return line;
	}

	private ArrayList<OrderLine> lookupOrderLines(int order_id)
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<OrderLine> lines = new ArrayList<OrderLine>();

		query_order_pizzas_by_order_stmt.setInt(1, order_id);

		ResultSet rset = query_order_pizzas_by_order_stmt.executeQuery();

		while (rset.next()) {
			lines.add(readOrderLine(rset));
		}

		query_order_pizzas_by_order_stmt.clearParameters();

		return lines;
	}

	/*
	 * Order Methods
	 */
	public void insertOrder(Order order)
		throws SQLException,
		       SQLTimeoutException
	{
		insert_order_stmt.setInt(1, order.getCustomer().getID());
		insert_order_stmt.setInt(2, order.getAddress().getID());
		insert_order_stmt.setInt(3, order.getCard().getID());
		insert_order_stmt.setString(4, date_formatter.format(order.getDate()));

		insert_order_stmt.executeUpdate();
		insert_order_stmt.clearParameters();

		insertOrderLines(order);
	}

	private Order readOrder(ResultSet rset)
		throws SQLException,
		       SQLTimeoutException
	{
		Order order = new Order();

		order.setID(rset.getInt(1));
		order.setCustomer(lookupCustomer(rset.getInt(2)));
		order.setAddress(lookupAddress(rset.getInt(3)));
		order.setCard(lookupCard(rset.getInt(4)));

		try {
			order.setDate(date_formatter.parse(rset.getString(5)));
		} catch (ParseException e) {
			System.err.println("unable to parse iso8601 string: " + rset.getString(5));
			System.err.println("Tell Patrick to fix his code.");
			System.exit(1);
		}

		order.setLines(lookupOrderLines(order.getID()));

		return order;
	}

	public ArrayList<Order> lookupOrdersByCustomer(Customer customer)
		throws SQLException,
		       SQLTimeoutException
	{
		ArrayList<Order> orders = new ArrayList<Order>();

		query_orders_by_customer_stmt.setInt(1, customer.getID());

		ResultSet rset = query_orders_by_customer_stmt.executeQuery();

		while (rset.next()) {
			orders.add(readOrder(rset));
		}

		query_orders_by_customer_stmt.clearParameters();

		return orders;
	}

	/*
	 * Main
	 */
	public static void main(String[] args)
		throws SQLException,
		       SQLTimeoutException
	{
		Database db = new Database("/home/kyrvin/School/swe3313/pizzashop/src/test.db");

		db.open();

		Address address = new Address(0, "658 Bartow Drive", null, "Dacula", "Ga", "30019");
		Card card = new Card(0, "1111-1111-1111-1111", "Patrick Keating", Card.Type.DEBIT, "06/19", address);

		db.insertAddress(address);
		db.insertCard(card);

		Customer kyrvin = new Customer(0, "Kyrvin", "kyrvin3@gmail.com", "password", "(678) 549-4409", "", address, card);
		db.insertCustomer(kyrvin);

		try {
			Customer customer = db.loginCustomer("kyrvin3@gmail.com", "password");

			System.out.println("ID: " + customer.getID());
			System.out.println("Name: " + customer.getName());
			System.out.println("Email: " + customer.getEmail());
			System.out.println("Password: " + customer.getPassword());
			System.out.println("Phone: " + customer.getPhone());
			System.out.println("Notes: " + customer.getNotes());
			Address addr = customer.getAddress();
			System.out.println("Address: " + addr.getLine1() + " " + addr.getCity() + ", " + addr.getState() + " " + addr.getZip());
			System.out.println("Card: " + customer.getActiveCard().getNumber());

		} catch (InvalidLoginException e) {
			System.out.println(e);

		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		db.close();
	}

	public static void killMeNow()
	{
		System.out.println("Killing you now!");
		System.exit(99);
	}
}
