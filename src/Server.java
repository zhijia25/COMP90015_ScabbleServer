import java.awt.EventQueue;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


import java.util.ArrayList;


public class Server {
	private Games games = null;
	private ArrayList<User> users = null;

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					Server window = new Server(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @param args
	 */
	public Server(String[] args) {
		initialize(args);
	}


	/**
	 * @param args 
	 */

	private void initialize(String[] args) {

		


		// used for first time initialization
		// dict = new ConcurrentHashMap<String, String>();
		users = new ArrayList<User>();
		games = new Games();

//		System.setSecurityManager(new RMISecurityManager()); cannot used
	    try
	    {  
	    	
	    	
	    	// create the RMI server and Registry
	    	ServerInterface server = new ScrabbleServer(users,games);
	    	System.out.println("Service Started2");
	    	Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
	    	System.out.println("Service Started1");
	    	registry.bind("Scrabble",server); 
	    	System.out.println("Service Started");
	    	

      
        }catch(NumberFormatException e1) {
        	System.exit(0);
        }
	    catch(Exception e) {
        	System.out.println("gogogo");
        }

	}

}
