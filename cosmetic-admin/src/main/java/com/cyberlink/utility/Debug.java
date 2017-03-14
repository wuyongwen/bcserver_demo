package com.cyberlink.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Debug {
	static boolean debug = true;
	public Debug(){
		
	} 
	
	public static void setDebug(boolean debug) {
		Debug.debug = debug;
	}

	public static void dprintf(String name, Object ... fmt) 
	{	
		try {
			if (debug) {
				DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				String funcName = new Exception().getStackTrace()[1].getMethodName();
				String msg = String.format("%s [%s] ", dateFormat.format(cal.getTime()), funcName);
				String ds = String.format(name, fmt);
				msg += ds;		
				System.out.println(msg);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void printInfoDiffTimeAndWriteToFile(String message,Date startTime ,Date endTime ,String strFilePath){
		long diffSpentTime = (long)Math.floor((endTime.getTime() - startTime.getTime())/1000);
		int dd = (int)Math.floor(diffSpentTime/86400);
		int hh = (int)Math.floor((diffSpentTime%86400)/3600);
		int mm = (int)Math.floor((diffSpentTime%3600)/60);
		int ss = (int)Math.floor(diffSpentTime%60);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String funcName = new Exception().getStackTrace()[1].getMethodName();
		String dateMsg = String.format("%s [%s] ", dateFormat.format(cal.getTime()), funcName);
		String data = String.format("%s %s:%d days %d hours %d minutes %d seconds \r\n",dateMsg,message,dd,hh,mm,ss);
		Tool.writeStringToFile(strFilePath, data, true);
	}	
}
