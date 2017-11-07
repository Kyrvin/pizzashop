public class InvalidLoginException
	extends Exception
{
	private String username;
	private String password;

	public InvalidLoginException(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	public String toString()
	{
		return "Invalid username or password.";
	}

	public String getUsername() { return username; }
	public String getPassword() { return password; }
}
