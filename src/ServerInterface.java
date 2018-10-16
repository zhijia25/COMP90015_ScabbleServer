import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote { 

	// used for test
	public String test(RMIInterface client) throws RemoteException;
    
	// login
	public int checklog(RMIInterface client, String name) throws RemoteException ;
	
	// get all users' name through the return String
	public String getUser(RMIInterface client,String name) throws RemoteException ;
	
	// get the player's game status 1 for in game 2 for off game
	public int getStatus(RMIInterface client,String name) throws RemoteException ;
	
	// invite the players in the names
	public void invite(RMIInterface client,String names) throws RemoteException ;
	
	// input the char to change the table
	public void input(RMIInterface client,String name, int i, int j, char c) throws RemoteException ;
	
	// skip name's turn
	public void skip(RMIInterface client,String name) throws RemoteException ;
	
	//the player let server to vote the word
	public void vote(RMIInterface client,String name, String word) throws RemoteException ;
	
	//close the waiting list remove the user
	public void close(RMIInterface client,String name) throws RemoteException ;
	
	//close the game tell the game winner to all clients
	public void gameClose(RMIInterface client,String name) throws RemoteException ;
	
	//the player do not want to input
	public void skipInput(RMIInterface client, String name) throws RemoteException;
	
	//when long time no respond
	public void timeoutInvite(RMIInterface client) throws RemoteException;
}