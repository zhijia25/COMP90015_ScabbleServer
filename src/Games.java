import java.util.ArrayList;

public class Games{
	private ArrayList<Game> games = null;
	
	Games(){
		games = new ArrayList<Game>();
	}
	
	// find which game the player belongs to
	public Game whichGame(String name) {
		for(Game game: games) {
			for(Player player: game.getPlayers()) {
				if(name.equals(player.getName())) {
					return game;
				}
			}
		}
		return null;
	}
	
	public Game add() {
		Game game = new Game();
		games.add(game);
		return game;
	}
	public void clearGame(Game game) {
		games.remove(game);
	}
}