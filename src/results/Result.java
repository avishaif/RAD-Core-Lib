package results;

public class Result
{
	private String name;
	private int id;
	private boolean result;


	/**
	 * Constructor initiates class by process/thread id.
	 * @param id Process or thread id.
	 * @param result True if operation was successful, false otherwise.
	 */
	public Result(int id, boolean result) 
	{
		this.id = id;
		this.result = result;
	}
	

	/**
	 * Constructor initiates class by process/thread name.
	 * @param name Process or a thread name
	 * @param result True if operation was successful, false otherwise.
	 */
	public Result(String name, boolean result) 
	{
		this.setName(name);
		this.result = result;
	}
	
	
	public void setResult(boolean result) 
	{
		this.result = result;
	}
	

	public boolean getResult() 
	{
		return result;
	}

	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	
	public int getId() 
	{
		return id;
	}

	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	
	public String getName() 
	{
		return name;
	}
}














