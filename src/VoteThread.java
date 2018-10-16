

import java.rmi.RemoteException;

public class VoteThread extends Thread{
	private Object lock;
	VoteStatus voteStatus;
	Player player;
	String word;
	
	VoteThread(Object lock, VoteStatus voteStatus, Player user, String word){
		super();
		this.lock = lock;
		this.voteStatus=voteStatus;
		this.player=user;
		this.word=word;
	}
	
	@Override
	public void run() {

		try {
				
			int result=player.getClient().vote(word);
			synchronized(lock) {
				if(voteStatus.getIfWaiting() ==2)
					lock.wait();
				
				if(result==1) {
					System.out.println(player.getName()+" accepted the vote.");
					voteStatus.addAccepted();
				}
				if(result==2) {
					voteStatus.addRefuse();;
					System.out.println(player.getName()+" refused the vote.");
					if (voteStatus.getRefuseList().equals("")) {
						voteStatus.setRefuseList(player.getName());
					}else {
						voteStatus.addRefuseList(player.getName());
					}
				}
				
				if(voteStatus.checkBound()) {
					lock.notify();
				}
			}
		} catch (InterruptedException | RemoteException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}