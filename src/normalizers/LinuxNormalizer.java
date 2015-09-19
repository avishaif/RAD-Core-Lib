package normalizers;

public class LinuxNormalizer extends Normalize{

	private int min;
	private int max;
	
	public LinuxNormalizer() 
	{
		this.min = Constants.MIN_PRIORITY;
		this.max = Constants.MAX_PRIORITY;
	}
	
	public int[] normalize(int priority, boolean isProcess) {
	
		int normalized[] = new int[2];
		if (priority >= min && priority <= (int)max/3)
		{
			normalized[0] = 0;
			normalized[1] = (int)(min + ((priority - 0) * (-39/(max-min))));
		}
		
		if (priority >= (int)max/3 && priority <= ((max/3)) * 2)
		{
			normalized[0] = 1;
			normalized[1] = (int)(min + ((priority - 33) * (98/(max-min))));
		}
		
		if (priority >= ((max/3) * 2) && priority <= max)
		{
			normalized[0] = 2;
			normalized[1] = (int)(min + ((priority - 33) * (98/(max-min))));
		}
		
		return normalized;
	}
}
