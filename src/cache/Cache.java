package cache;

public class Cache {

	private int id;
	private String name;
	private int[] affinity;
	private int policy;

	public Cache(int id, String name, int[] affinity, int priority, int policy) {
		super();
		this.id = id;
		this.name = name;
		this.affinity = affinity;
		this.priority = priority;
		this.policy = policy;
		
	}
	
	public int getPolicy() {
		return policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int[] getAffinity() {
		return affinity;
	}
	public void setAffinity(int[] affinity) {
		this.affinity = affinity;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	private int priority;
}
