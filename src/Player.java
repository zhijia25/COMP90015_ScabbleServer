



public class Player extends User{
	int score = 0;
	Player(User user){
		super();
		super.name=user.name;
		super.client=user.client;
		super.status=user.status;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
}
