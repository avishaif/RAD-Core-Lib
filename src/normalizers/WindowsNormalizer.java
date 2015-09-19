package normalizers;

import os_api.windows.Constants;

public class WindowsNormalizer extends Normalize
{
	private final int PRIORITY_CLASSES = 6;
	private final int PRIORITY_LEVELS = 7;
	private int min;
	private int max;
	private int priorityRange;
	private int processClassRange;
	private int threadLevelRange;
	
	public WindowsNormalizer()
	{
		this.min = normalizers.Constants.MIN_PRIORITY;
		this.max = normalizers.Constants.MAX_PRIORITY;
		this.priorityRange = max - min + 1;
		this.processClassRange = priorityRange / PRIORITY_CLASSES;
		this.threadLevelRange = priorityRange / PRIORITY_LEVELS;
	}
	
	public int[] normalize(int priority, boolean isProcess)
	{
		int[] normalized = new int[2];
		normalized[0] = 0;
		if(priority < this.min || priority > this.max)
		{
			normalized[1] = 0;
			return normalized;
		}
		if(isProcess)
		{
			if(priority >= this.min && priority <= processClassRange)
			{
				normalized[1] = Constants.IDLE_PRIORITY_CLASS;
			}
			else if(priority > processClassRange && priority <= 2*processClassRange)
			{
				normalized[1] = Constants.BELOW_NORMAL_PRIORITY_CLASS;
			}
			else if(priority > 2*processClassRange && priority <= 3*processClassRange)
			{
				normalized[1] = Constants.NORMAL_PRIORITY_CLASS;
			}
			else if(priority > 3*processClassRange && priority <= 4*processClassRange)
			{
				normalized[1] = Constants.ABOVE_NORMAL_PRIORITY_CLASS;
			}
			else if(priority > 4*processClassRange && priority <= 5*processClassRange)
			{
				normalized[1] = Constants.HIGH_PRIORITY_CLASS;
			}
			else if(priority > 5*processClassRange && priority <= this.max)
			{
				normalized[1] = Constants.REALTIME_PRIORITY_CLASS;
			}			
		}
		else // isThread
		{
			if(priority >= this.min && priority <= threadLevelRange)
			{
				normalized[1] = Constants.THREAD_PRIORITY_IDLE;
			}
			else if(priority > threadLevelRange && priority <= 2*threadLevelRange)
			{
				normalized[1] = Constants.THREAD_PRIORITY_LOWEST;
			}
			else if(priority > 2*threadLevelRange && priority <= 3*threadLevelRange)
			{
				normalized[1] = Constants.THREAD_PRIORITY_BELOW_NORMAL;
			}
			else if(priority > 3*threadLevelRange && priority <= 4*threadLevelRange)
			{
				normalized[1] = Constants.THREAD_PRIORITY_NORMAL;
			}
			else if(priority > 4*threadLevelRange && priority <= 5*threadLevelRange)
			{
				normalized[1] = Constants.THREAD_PRIORITY_ABOVE_NORMAL;
			}
			else if(priority > 5*threadLevelRange && priority <= 6*threadLevelRange)
			{
				normalized[1] = Constants.THREAD_PRIORITY_HIGHEST;
			}
			else if(priority > 6*threadLevelRange && priority <= this.max)
			{
				normalized[1] = Constants.THREAD_PRIORITY_TIME_CRITICAL;
			}
		}
		return normalized;
	}

}











