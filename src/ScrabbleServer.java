import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ScrabbleServer extends UnicastRemoteObject implements ServerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7248281137326966933L;
	private static final String regex = "[A-Za-z0-9]{1,8}";
	private static final String regex1 = "[A-Za-z0-9]{1,8}\\[in game\\]";
	private ArrayList<User> users = null;
	private Games games = null;

	// construct function
	protected ScrabbleServer(ArrayList<User> users, Games games) throws RemoteException {
		super();
		this.users = users;
		this.games = games;

		// This thread is used for sending the users' these information, and check if these clients are online
		new Thread() {
			public void run() {
				int i = 0;
				while (true) {
					if (i < 4) {
						// check if the user is online
						if (users.size() > 0) {
							List<User> tmp = users;
							User[] users1 = (User[]) tmp.toArray(new User[users.size()]);
							for (User user : users1) {
								try {
									user.getClient().test();
								} catch (RemoteException e) {
									System.out.println(user.getName() + " is not online");
									try {
										gameClose(user.getClient(), user.getName());
									} catch (RemoteException e1) {
										System.out.println("Someone is offline");
									}
									synchronized (users) {
										users.remove(user);
									}
								}
							}
						}
					} else {
						// refresh the list of users
						if (users.size() > 0) {
							String allName = getAllName(users);
							List<User> tmp = users;
							User[] users1 = (User[]) tmp.toArray(new User[users.size()]);
							for (User user : users1) {
								try {
									user.getClient().sendMessage(allName);
								} catch (RemoteException e) {
									System.out.println(user.getName() + " is not online");
									try {
										gameClose(user.getClient(), user.getName());
									} catch (RemoteException e1) {
										System.out.println("Someone is offline");
									}
									synchronized (users) {
										users.remove(user);
									}
								}
							}
						}
						i = 0;
					}
					i++;

					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	// used for test whether the user is online
	@Override
	public String test(RMIInterface client) throws RemoteException {
		// client.test("abc");
		return "test success";

	}

	// used for handling the login. 1 for success, 2 for failing, 0 for illegal
	@Override
	public int checklog(RMIInterface client, String name) throws RemoteException {
		synchronized (users) {
			// if the name is duplicate
			if (isExist(name) == true) {
				System.out.println("The user is already exist.");
				return 2;
			}

			// check if the name is illegal
			if (!name.matches(regex)) {
				System.out.println("The name is illegal");
				return 0;
			}
			System.out.println(name + " login.");
			User user = new User(name, client);
			users.add(user);
			String allName = getAllName(users);
			System.out.println("All online users:" + allName);
			// tell everyone the change
			for (User user1 : users) {
				new Thread() {
					public void run() {
						try {
							user1.getClient().sendMessage(allName);
						} catch (RemoteException e) {

							System.out.println("Someone is offline");
						}
					}
				}.start();

			}
			System.out.println("Already sent userlist to every user.");
			return 1;

		}
	}

	// return all user name to the client
	@Override
	public String getUser(RMIInterface client, String name) throws RemoteException {
		String allName = getAllName(users);
		System.out.println(name + " request to get userlist.");
		System.out.println("Send userlist to " + name + ".");
		System.out.println("All online users:" + allName);
		return allName;

	}

	// return the game status of the client
	@Override
	public int getStatus(RMIInterface client, String name) throws RemoteException {
		System.out.println(name + " ask for game status.");
		Game game = games.whichGame(name);
		if (game != null)
			return 1;
		else
			return 2;
	}

	// the client invites all clients in the String name
	@Override
	public void invite(RMIInterface client, String name) throws RemoteException {

		Game game;
		synchronized (games) {
			game = games.add();
		}

		game.setInviteStatus(new InviteStatus());
		game.getInviteStatus().setIfWaiting(2);
		String[] nameList = name.split(",");

		if (nameList.length > 4) {
			System.out.println("invite too many people");
			// tell the client there are too many invited people
			client.inviteResult(3, "");
			return;
		}

		///// "" cannot catch
		Object lock = new Object();
		game.getInviteStatus().setAllnumber(nameList.length - 1);
		int userSize = users.size();

		// add them to game list
		for (String n : nameList) {
			int i = 0;
			for (i = 0; i < userSize; i++) {
				if (n.matches(regex1)) {
					game.clearUsers(users);
					client.inviteResult(0, "");
					synchronized (games) {
						games.clearGame(game);
					}
					return;
				}
				if (n.equals(users.get(i).getName())) {

					// the user is in game or in invite
					if (users.get(i).getStatus() == 1) {
						game.clearUsers(users);
						client.inviteResult(0, "");
						synchronized (games) {
							games.clearGame(game);
						}
						return;
					}
					users.get(i).setStatus(1);
					game.getPlayers().add(new Player(users.get(i)));
				}
				if (i == userSize) {
					game.clearUsers(users);
					client.inviteResult(3, "");
					synchronized (games) {
						games.clearGame(game);
					}
					return;
				}
			}
		}

		boolean isGame = true;// assume all agree to play, if one confuse, it will be false
		// game.players.clear();
		System.out.println(nameList[0] + " want to initial a new game.");
		synchronized (lock) {

			game.setStatus(1);
			System.out.println(name);

			for (Player player : game.getPlayers()) {
				if (!player.getName().equals(nameList[0])) {
					InviteThread inviteThread = new InviteThread(lock, game.getInviteStatus(), player, name);
					inviteThread.start();
				}
			}

			game.getInviteStatus().setIfWaiting(1);
			lock.notifyAll();
			try {

				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (game.getInviteStatus().getRefuse() > 0) {
				isGame = false;
			}
		}
		if (isGame == true) {
			System.out.println("Everyone accept");
			game.startGame(nameList);

			System.out.println("Start game");
			// tell everyone the game start and send the game message;
			if(games.whichGame(nameList[0])==null){
				System.out.println("The invitor is offline");
				return;
			}
			for (Player player : game.getPlayers()) {
				new Thread() {
					public void run() {
						try {
							System.out.println(game.getCurrentPlayer());
							player.getClient().input(game.getFourHundred(), game.getCurrentTurn(),
									game.getCurrentPlayerName(), game.getPlayersName(), game.getScores());
							player.getClient().inviteResult(1, "");
						} catch (RemoteException e) {

							System.out.println("Someone is offline");
						}
					}
				}.start();
			}
			
			// refresh all users' waiting list
			for (User user : users) {
				new Thread() {
					public void run() {
						try {
							user.getClient().sendMessage(getAllName(users));
						} catch (RemoteException e) {

							System.out.println("Someone is offline");
						}
					}
				}.start();
			}

		} else {
			// someone refuse
			System.out.println("Someone refuse");
			for (Player player : game.getPlayers()) {
				new Thread() {
					public void run() {
						try {
							player.getClient().inviteResult(2, game.getInviteStatus().getRefuseList());
						} catch (RemoteException e) {

							System.out.println("Someone is offline");
						}
					}
				}.start();
			}
			game.clearUsers(users);

			// clear the temporary game
			synchronized (games) {
				games.clearGame(game);
			}
		}

	}

	// someone input a char
	@Override
	public void input(RMIInterface client, String name, int x, int y, char c) throws RemoteException {

		Game game = games.whichGame(name);

		// not the person
		if (!name.equals(game.getCurrentPlayerName())) {
			System.out.println("1:" + name);
			System.out.println("2:" + game.getCurrentPlayerName());
			System.out.println("not " + name + " turn");

			new Thread() {
				public void run() {
					try {
						client.inputError();
					} catch (RemoteException e) {

						System.out.println("Someone is offline");
					}
				}
			}.start();

			return;
		}

		// not repeated input
		if (game.isIfInputed() == true) {
			System.out.println(name + " wants to input but is already inputed");
			new Thread() {
				public void run() {
					try {
						client.inputError();
					} catch (RemoteException e) {

						System.out.println("Someone is offline");
					}
				}
			}.start();

			return;
		}
		// change the Game
		game.setIfInputed(true);
		game.setSkipCount(0);
		char[][] gameTable = game.getFourHundred();
		gameTable[x][y] = c;

		ArrayList<Player> players = game.getPlayers();

		// tell everyone the change
		for (Player tmp : players) {

			new Thread() {
				public void run() {
					try {
						tmp.getClient().input(game.getFourHundred(), game.getCurrentTurn(), game.getCurrentPlayerName(),
								game.getPlayersName(), game.getScores());
					} catch (RemoteException e) {

						System.out.println("Someone is offline");
					}
				}
			}.start();

		}

		// calculate the two words
		String horizontal = java.lang.String.valueOf(c);
		String vertical = java.lang.String.valueOf(c);

		int i = x - 1;
		int j = y - 1;
		int h = x + 1;
		int g = y + 1;
		while (i >= 0 && gameTable[i][y] != '0') {
			horizontal = java.lang.String.valueOf(gameTable[i][y]) + horizontal;
			i--;
		}
		while (h < 20 && gameTable[h][y] != '0') {
			horizontal += java.lang.String.valueOf(gameTable[h][y]);
			h++;
		}
		while (j >= 0 && gameTable[x][j] != '0') {
			vertical = java.lang.String.valueOf(gameTable[x][j]) + vertical;
			j--;
		}
		while (g < 20 && gameTable[x][g] != '0') {
			vertical += java.lang.String.valueOf(gameTable[x][g]);
			g++;
		}
		System.out.println("give two choices");

		client.twoChoices(horizontal, vertical);
	}

	// someone wants to skip the highlight
	@Override
	public void skip(RMIInterface client, String name) throws RemoteException {

		Game game = games.whichGame(name);

		// not your turn
		if (!name.equals(game.getCurrentPlayerName())) {
			client.inputError();
			System.out.println("An invalid operation.");
			return;
		}

		System.out.println(name + " wants to skip the vote");
		game.setCurrentPlayer(game.getCurrentPlayer() + 1);
		// change the game
		for (int i = 0; i < game.getPlayers().size(); i++) {
			if (name.equals(game.getPlayers().get(i).getName())) {
				if (i == game.getPlayers().size() - 1) {
					game.setCurrentPlayerName(game.getPlayers().get(0).getName());
				} else {
					game.setCurrentPlayerName(game.getPlayers().get(i + 1).getName());
				}
			}
		}
		if (game.getCurrentPlayer() % game.getPlayers().size() == 1) {
			game.nextTurn();
		}
		// tell everyone the change
		for (Player player : game.getPlayers()) {
			new Thread() {
				public void run() {
					try {
						player.getClient().nextTurn(game.getFourHundred(), game.getCurrentTurn(),
								game.getCurrentPlayerName(), game.getPlayersName(), game.getScores());
					} catch (RemoteException e) {

						System.out.println("Someone is offline");
					}
				}
			}.start();

		}
		game.setIfInputed(false);
		game.setIfVoted(false);
		System.out.println(name + " end his/her turn.");
	}

	// someone wants to skip input
	@Override
	public void skipInput(RMIInterface client, String name) throws RemoteException {

		Game game = games.whichGame(name);

		// if everyone skip
		if (game.getSkipCount() == game.getPlayers().size() - 1) {
			gameClose(client, name);
			System.out.println("All players give up their turns.");
			return;
		}
		
		// check if have been inputed
		if (game.isIfInputed()) {
			client.inputError();
			System.out.println("An invalid operation.");
			return;
		}

		// not your turn
		if (!name.equals(game.getCurrentPlayerName())) {
			client.inputError();
			System.out.println("An invalid operation.");
			return;
		}

		System.out.println(name + " wants to skip the input" + game.getSkipCount());
		game.setCurrentPlayer(game.getCurrentPlayer() + 1);
		for (int i = 0; i < game.getPlayers().size(); i++) {
			if (name.equals(game.getPlayers().get(i).getName())) {
				if (i == game.getPlayers().size() - 1) {
					game.setCurrentPlayerName(game.getPlayers().get(0).getName());
				} else {
					game.setCurrentPlayerName(game.getPlayers().get(i + 1).getName());
				}
			}
		}
		if (game.getCurrentPlayer() % game.getPlayers().size() == 1) {
			game.setCurrentTurn(game.getCurrentTurn() + 1);
			game.setCurrentPlayer(1);
			game.setCurrentPlayerName(game.getPlayers().get(0).name);
		}
		game.addSkipCount();
		// tell everyone the change
		for (Player player : game.getPlayers()) {
			new Thread() {
				public void run() {
					try {
						player.getClient().nextTurn(game.getFourHundred(), game.getCurrentTurn(),
								game.getCurrentPlayerName(), game.getPlayersName(), game.getScores());
					} catch (RemoteException e) {

						System.out.println("Someone is offline");
					}
				}
			}.start();

		}
		game.setIfInputed(false);
		game.setIfVoted(false);
		System.out.println(name + "give up his/her turn.");
	}

	// someone wants to vote a word
	@Override
	public void vote(RMIInterface client, String name, String word) throws RemoteException {

		Game game = games.whichGame(name);

		game.setVoteStatus(new VoteStatus());
		
		// not your turn
		if (!name.equals(game.getCurrentPlayerName())) {
			client.inputError();
			return;
		}
		if (!game.isIfInputed()) {
			client.inputError();
			System.out.println("An invalid operation.");
			return;
		}
		
		//not repeat voting
		if (game.isIfVoted()) {
			client.inputError();
			System.out.println("An invalid operation.");
			return;
		}
		game.setIfVoted(true);
		int playerSize = game.getPlayers().size();
		boolean pass = true;
		game.getVoteStatus().setAllnumber(playerSize - 1);
		game.getVoteStatus().setIfWaiting(2);
		Object lock = new Object();
		System.out.println(name + " initial a vote.");

		// get others' responds
		synchronized (lock) {
			for (int i = 0; i < playerSize; i++) {
				if (!game.getPlayers().get(i).getName().equals(name)) {
					VoteThread voteThread = new VoteThread(lock, game.getVoteStatus(), game.getPlayers().get(i), word);
					voteThread.start();
					// int voteResult = game.players.get(i).getClient().vote(word);
				}
			}
			game.getVoteStatus().setIfWaiting(1);
			lock.notifyAll();
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (game.getVoteStatus().getRefuse() > 0) {
			System.out.println("refuse this vote");
			pass = false;
		}
		if (pass) {
			System.out.println("All players passed the vote.");
			for (int i = 0; i < playerSize; i++) {
				if (name.equals(game.getPlayers().get(i).getName())) {
					int score = game.getPlayers().get(i).getScore();
					score += word.length();
					game.getPlayers().get(i).setScore(score);
				}
			}

			// tell everyone
			for (Player player : game.getPlayers()) {

				System.out.println("Send result to:" + player.name);
				new Thread() {
					public void run() {
						try {
							player.getClient().voteResult(1);
						} catch (RemoteException e) {

							System.out.println("Someone is offline");
						}
					}
				}.start();
			}
		} else {
			System.out.println("Someone denies the vote.");
			for (Player player : game.getPlayers()) {

				new Thread() {
					public void run() {
						try {
							player.client.voteResult(2);
						} catch (RemoteException e) {

							System.out.println("Someone is offline");
						}
					}
				}.start();
			}
		}
		System.out.println("Move to next turn.");
		skip(client, name);
	}

	// close the waiting list
	@Override
	public void close(RMIInterface client, String name) throws RemoteException {
		// delete the corresponding user
		synchronized (users) {

			int userSize = users.size();
			int delete = 0;
			for (int i = 0; i < userSize; i++) {
				if (name.equals(users.get(i).getName())) {
					delete = i;
				}
			}
			if (users.get(delete).getStatus() == 1) {
				gameClose(client, name);
			}
			users.remove(delete);
			System.out.println(name + " is offline now.");
			if (users.size() > 0) {
				String allName = getAllName(users);
				System.out.println("All online users:" + allName);
				for (User user : users) {
					new Thread() {
						public void run() {
							try {
								user.getClient().sendMessage(allName);
							} catch (RemoteException e) {

								System.out.println("Someone is offline");
							}
						}
					}.start();

				}
			}

		}
	}

	// check the name if in the users
	public boolean isExist(String name) throws RemoteException {
		if (users != null) {
			int userSize = users.size();
			for (int i = 0; i < userSize; i++) {
				User tmp = users.get(i);
				if (name.equals(tmp.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	
	// close the game
	@Override
	public void gameClose(RMIInterface client, String name) throws RemoteException {

		Game game = games.whichGame(name);

		if (game == null) {
			System.out.println("Game is off, cannot close the game");
			return;
		}
		int maxScore = -1;
		game.setWinner(null);
		if (game.getPlayers().size() > 0) {
			for (Player player : game.getPlayers()) {
				if (player.getScore() > maxScore) {
					maxScore = player.getScore();
					game.setWinner("" + player.getName());
				} else if (player.getScore() == maxScore) {
					game.setWinner(game.getWinner() + "," + player.getName());
				}
			}
			
			// set the users' status to no game;
			game.clearUsers(users);

			//tell everyone the game is closed
			for (Player player : game.getPlayers()) {

				new Thread() {
					public void run() {
						try {
							player.client.gameOver(game.getWinner());
						} catch (RemoteException e) {

							System.out.println("Someone is offline");
						}
					}
				}.start();

			}
			for (User user : users) {
				new Thread() {
					public void run() {
						try {
							user.client.sendMessage(getAllName(users));
						} catch (RemoteException e) {

							System.out.println("Someone is offline");
						}
					}
				}.start();
			}
		}
		synchronized (games) {
			games.clearGame(game);
		}
		System.out.println("successful close game");
	}

	// creative used. not started
	@Override
	public void timeoutInvite(RMIInterface client) throws RemoteException {

	}

	// the method makes the users into a long string
	public String getAllName(ArrayList<User> users) {
		if (users.size() == 0)
			return "";
		String allName = users.get(0).getName();
		if (users.get(0).getStatus() == 1) {
			allName = allName + "[in game]";
		}
		for (int i = 1; i < users.size(); i++) {
			allName = allName + "," + users.get(i).getName();
			if (users.get(i).getStatus() == 1) {
				allName = allName + "[in game]";
			}
		}
		return allName;
	}
}