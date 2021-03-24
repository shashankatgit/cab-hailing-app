package cab_hailing.ride_service;

public class Logger {
	static boolean logEnabled = true;
	
	public static void log(String logString) {
		if(logEnabled) {
			System.out.println("LOG : "+logString);
		}
	}
	
	public static void logReset(String logString) {
		System.out.println("\n\n\n-----------------RESET CALLED--------------------\n");
		log(logString);
	}
}
