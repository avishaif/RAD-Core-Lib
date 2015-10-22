package cache;


/**
 * Cache class represents a record in a cache which held while the application is running.
 * Cache saves process/thread data such as ID, name, affinity, priority and policy.
 */
public class Cache
{
	private int id;
	private String name;
	private int[] affinity;
	private int policy;
	private int priority;

	public Cache(int id, String name, int[] affinity, int priority, int policy)
	{
		this.id = id;
		this.name = (name == null) ? "" : name;
		this.affinity = affinity;
		this.priority = priority;
		this.policy = policy;

	}

	public void setId(int id)
	{
		this.id = id;
	}

	public void setName(String name)
	{
		this.name = (name == null) ? "" : name;
	}

	public void setAffinity(int[] affinity)
	{
		this.affinity = affinity;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public void setPolicy(int policy)
	{
		this.policy = policy;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public int[] getAffinity()
	{
		return affinity;
	}

	public int getPriority()
	{
		return priority;
	}
	
	public int getPolicy()
	{
		return policy;
	}
}










