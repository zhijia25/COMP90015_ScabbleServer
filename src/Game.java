import java.util.ArrayList;

//This class is used to save the information of the game
public class Game{
	
	private int status = 2; 
	private int currentPlayer;

	private String currentPlayerName;
	private String winner;
	private int currentTurn;
	private ArrayList<Player> players;
	private char[][] fourHundred = null;
	private boolean ifInputed = false;
	private boolean ifVoted = false;
	
	// used to the communication between threads
	private VoteStatus voteStatus = null;
	private InviteStatus inviteStatus = null;
	
	
	private int skipCount = 0;
	
	// set get functions
	public int getSkipCount() {
		return skipCount;
	}

	
	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}

	public VoteStatus getVoteStatus() {
		return voteStatus;
	}

	public void setVoteStatus(VoteStatus voteStatus) {
		this.voteStatus = voteStatus;
	}

	public InviteStatus getInviteStatus() {
		return inviteStatus;
	}

	public void setInviteStatus(InviteStatus inviteStatus) {
		this.inviteStatus = inviteStatus;
	}

	// Constructor
	Game(){
		status = 1;
		currentPlayer=-1;
		currentPlayerName="";
		currentPlayerName="";
		skipCount=0;
		fourHundred= new char[20][20];
		ifInputed = false;
		ifVoted = false;
		players = new ArrayList<Player>();
	}
	
	public boolean isIfVoted() {
		return ifVoted;
	}
	
	public void setIfVoted(boolean ifVoted) {
		this.ifVoted = ifVoted;
	}
	
	public boolean isIfInputed() {
		return ifInputed;
	}
	public void setIfInputed(boolean ifInputed) {
		this.ifInputed = ifInputed;
	}
	
	public char[][] getFourHundred() {
		return fourHundred;
	}
	public void setFourHundred(char[][] fourHundred) {
		
		for(int i=0; i<20 ; i++){
			for(int j=0; j<20 ; j++)
				this.fourHundred[i][j]= fourHundred[i][j];
		}
	} 
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public String getPlayersName() {
		if(players.size()==0)
			return "";
		String tmp=players.get(0).getName();
		for(int i =1;i<players.size();i++) {
			tmp=tmp+","+players.get(i).getName();
		}
		return tmp;
	}
	
	public String getScores() {
		if(players.size()==0)
			return "";
		String tmp=String.valueOf(players.get(0).getScore());
		for(int i =1;i<players.size();i++) {
			tmp=tmp+","+String.valueOf(players.get(i).getScore());
		}
		return tmp;
	}
	
	public String getFH() {
		// TODO Auto-generated method stub
		String tmp="";
		for(int i=0;i<20;i++)
			for(int j=0;j<20;j++)
				tmp=tmp+String.valueOf(fourHundred[i][j]);
		System.out.println(tmp);
		return tmp;
	}
	
	public String getWinner() {
		return winner;
	}
	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	public String getCurrentPlayerName() {
		return currentPlayerName;
	}
	public void setCurrentPlayerName(String currentPlayerName) {
		this.currentPlayerName = currentPlayerName;
	}
	
	public int getCurrentTurn() {
		return currentTurn;
	}
	public void setCurrentTurn(int currentTurn) {
		this.currentTurn = currentTurn;
	}
	
	// close the game
	public void endGame() {
		this.setStatus(2);
		this.currentPlayer = 0;
		this.currentPlayerName = null;
		this.ifInputed = false;
		this.ifVoted = false;
		this.currentTurn = 0;
		this.players.clear();
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				fourHundred[i][j] = '0';
			}
		}
		System.out.println("game close");
	}
	
	// nextTurn
	public void nextTurn() {
		this.currentTurn++;
		this.currentPlayer = 1;
		this.currentPlayerName = this.players.get(0).name;
	}

	// initial the game
	public void startGame(String[] nameList) {
		this.status = 1;

		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				fourHundred[i][j] = '0';
			}
		}

		setCurrentPlayer(1);
		setCurrentTurn(1);
		setCurrentPlayerName(nameList[0]);
		
	}

	public void clearUsers(ArrayList<User> users) {
		for (Player player : players) {
			for(User user : users) {
				if(user.getName().equals(player.getName())){
					user.setStatus(2);
				}
			}
		}
		
	}

	public void addSkipCount() {
		skipCount++;
		
	}
}