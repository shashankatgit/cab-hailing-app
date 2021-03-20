package cab_hailing.cab_service;

public class Logger {
	static boolean logEnabled = true;
	
	public static void log(String logString) {
		if(logEnabled) {
			System.out.println("LOG : "+logString);
		}
	}
}
