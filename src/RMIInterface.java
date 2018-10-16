/*import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote { 
	public String getUserName() throws RemoteException;
	public void sendMessage(String message) throws RemoteException;
	public int invite() throws RemoteException;
	public void inviteResult(int result);
	public void input(Game game);
	public void nextTurn(Game game);
	public void twoChoices(String string, String string2);
	public int vote(String word);
	public void test(String string);
}
*/

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote { 

 //public String getUserName() throws RemoteException;
	
 public void sendMessage(String message) throws RemoteException;
 public int invite(String player) throws RemoteException;
 public void inviteResult(int result,String name)throws RemoteException;
 public void input(char[][] fourHundred, int round, String name, String names, String scores) throws RemoteException;
 public void nextTurn(char[][] fourHundred, int round, String name, String names, String scores)throws RemoteException;
 
 public void twoChoices(String string, String string2) throws RemoteException;
 public int vote(String word) throws RemoteException;
 public void voteResult(int result) throws RemoteException;
 
 // gameover to tell players who is the winner
 public void gameOver(String winner) throws RemoteException;
 
 public void inputError() throws RemoteException;
 
 public void test() throws RemoteException;
 
 //public void test(String string);
}