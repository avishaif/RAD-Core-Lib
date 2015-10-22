package validate;

public class Validator
{
	private static int numOfCpus;
	
	/**
	 * 
	 * @param int cpus number of cpus/core in the system
	 */
	public static void setNumberOfCpus(int cpus)
	{
		numOfCpus = cpus;
	}
	
	/**
	 * verifies whether the parameters received by the setAffinity method are valid.
	 * @param id int process/thread id.
	 * @param affinity int array cpu number to set affinity to.
	 * @return boolean true if verified, false otherwise.
	 */
	public static boolean checkAffinityParams(int id, int[] affinity)
	{
		if(null == affinity)
			return false;
		else if(id > -1 && affinity.length > 0 && affinity.length <= numOfCpus)
			return true;
		else
			return false;
	}
	
	/**
	 * verifies whether the parameters received by the setAffinity method are valid.
	 * @param name String process/thread name.
	 * @param affinity int array cpu number to set affinity to.
	 * @return boolean true if verified, false otherwise.
	 */
	public static boolean checkAffinityParams(String name, int[] affinity)
	{
		if(null == affinity)
			return false;
		else if(name != null && !name.equals("") && affinity.length > 0 && affinity.length <= numOfCpus)
			return true;
		else
			return false;
	}
	
	/**
	 * verifies whether the parameters received by the setAffinity method are valid.
	 * @param pName String process name.
	 * @param tName String thread name.
	 * @param affinity int array cpu number to set affinity to.
	 * @return boolean true if verified, false otherwise.
	 */
	public static boolean checkAffinityParams(String pName, String tName, int[] affinity)
	{
		if(null == affinity)
			return false;
		else if((pName != null && !pName.equals("")) && (tName != null && !tName.equals("")) && affinity.length > 0 && affinity.length <= numOfCpus)
			return true;
		else
			return false;
	}
	
	/**
	 * verifies whether the parameters received by the setAffinity method are valid.
	 * @param pid int process id.
	 * @param tName String thread name.
	 * @param affinity int array cpu number to set affinity to.
	 * @return boolean true if verified, false otherwise.
	 */
	public static boolean checkAffinityParams(int pid, String tName, int[] affinity)
	{
		if(null == affinity)
			return false;
		else if(pid > 0 && (tName != null && !tName.equals("")) && affinity.length > 0 && affinity.length <= numOfCpus)
			return true;
		else
			return false;
	}
	
//
// priority
//	
	
	/**
	 * verifies whether the parameters received by the setPriority method are valid.
	 * @param id int process/thread id.
	 * @param priority int value must be > 0.
	 * @return boolean true if verified, false otherwise.
	 */
	public static boolean checkPriorityParams(int id, int priority)
	{
		if(id > -1 && priority > -1)
			return true;
		else
			return false;
	}
	
	/**
	 * verifies whether the parameters received by the setPriority method are valid.
	 * @param name String process/thread name.
	 * @param priority int value must be > 0.
	 * @return boolean true if verified, false otherwise.
	 */
	public static boolean checkPriorityParams(String name, int priority)
	{
		if(name != null && !name.equals("") && priority > -1)
			return true;
		else
			return false;
	}
	
	/**
	 * verifies whether the parameters received by the setPriority method are valid.
	 * @param pName String process name.
	 * @param tName String thread name.
	 * @param priority int value must be > 0.
	 * @return boolean true if verified, false otherwise.
	 */
	public static boolean checkPriorityParams(String pName, String tName, int priority)
	{
		if((pName != null && !pName.equals("")) && (tName != null && !tName.equals("")) && priority > -1)
			return true;
		else
			return false;
	}
	
	/**
	 * verifies whether the parameters received by the setPriority method are valid.
	 * @param pid int process id.
	 * @param tName String thread name.
	 * @param priority int value must be > 0.
	 * @return boolean true if verified, false otherwise.
	 */
	public static boolean checkPriorityParams(int pid, String tName, int priority)
	{
		if(pid > -1 && (tName != null && !tName.equals("")) && priority > -1)
			return true;
		else
			return false;
	}

}









