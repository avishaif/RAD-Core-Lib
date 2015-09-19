package debug_test;


import java.util.Scanner;

import facade.Facade;



/**
 * THIS CLASS CHECKS IF AFFINITY AND PRIORITY WERE SET CORRECTLY.
 * THIS IS A TEST CLASS FOR DEBUGGING.
 * DELETE THIS PACKAGE BEFORE RELEASE. 
 * 
 * @author Michael
 *
 */
public class DebugTest
{	
	static Scanner input = new Scanner(System.in);

	public static void main(String[] args)
	{
		Facade.init();
		
		
		
		
		
		


/************ INVALID PROCESS AFFINITY	************************/		
		
//		System.out.println("Setting affinity: Test.exe, {0,1,2} - Too many values");
//		Facade.setProcessAffinity("Test.exe", new int[] {0,1,2});
		
//		System.out.println("Setting affinity: Test.exe, {4} - invalid cpu");
//		Facade.setProcessAffinity("Test.exe", new int[] {4});

//		System.out.println("Setting affinity: Test, {0} - invalid name");
//		Facade.setProcessAffinity("Test", new int[] {0});
		
		
//		System.out.println("Press any button to continue");
//		input.nextLine();
		

/************	PROCESS AFFINITY	************************/		
		
//		System.out.println("Setting affinity: 5104, {1}");
//		Facade.setProcessAffinity(5104, new int[] {1});		
		
		System.out.println("Setting affinity: Test.exe, {1}");
		Facade.setProcessAffinity("Test.exe", new int[] {});
//		
//		System.out.println("Setting affinity: eclipse.exe, {1}");
//		Facade.setProcessAffinity("eclipse.exe", new int[] {1});
//		
//		System.out.println("Setting affinity: java.exe, {0}");
//		Facade.setProcessAffinity("java.exe", new int[] {0});
//		
//		System.out.println("Press any button to continue");
//		input.nextLine();

/************	PROCESS PRIORITY	************************/

		
//		System.out.println("Setting priority: 5104, 60 - ABOVE_NORMAL_PRIORITY_CLASS");
//		Facade.setProcessPriority( , 60);		

//		System.out.println("Setting priority: Test.exe, 8");
//		Facade.setProcessPriority("Test.exe", -3);
		

//		System.out.println("Press any button to continue");
//		input.nextLine();
		
/************	THREAD AFFINITY	************************/		

//		System.out.println("Setting  java thread affinity: Second Thread - print B to CPU 0");
//		Facade.setThreadAffinity("Second Thread - print B", new int[] {0}, true);
//		
//		
//		
//		
//		System.out.println("Press any button to continue");
//		input.nextLine();
		
/************	THREAD PRIORITY	************************/		
		
//		System.out.println("Setting  java thread priority: Second Thread - print B, to priority THREAD_PRIORITY_TIME_CRITICAL");
//		Facade.setThreadPriority("Second Thread - print B", -3, true);
		
		
//		System.out.println("Test.exe threads prioirities before setting process priority:");
//		Facade.getThreadsProirities("Test.exe");
//		
//		System.out.println();	
		
//		Facade.setThreadsPriority(20);
//		
//		System.out.println();
//		
//		System.out.println("Test.exe threads prioirities after setting process priority:");
//		Facade.getThreadsProirities("Test.exe");
		
		
		
	}
	
}














