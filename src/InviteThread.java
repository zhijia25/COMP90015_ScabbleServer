import java.rmi.RemoteException;

public class InviteThread extends Thread{
	private Object lock;
	InviteStatus inviteStatus;
	User user;
	String name;
	
	InviteThread(Object lock, InviteStatus inviteStatus, User user, String name){
		super();
		this.lock = lock;
		this.inviteStatus=inviteStatus;
		this.user=user;
		this.name=name;
	}
	
	// get the result of invite 
	@Override
	public void run() {

		try {
				
			int result=user.getClient().invite(name);
			synchronized(lock) {
				if(inviteStatus.getIfWaiting() ==2)
					lock.wait();
				
				if(result==1) {
					inviteStatus.addAccepted();
				}
				if(result==2) {
					inviteStatus.addRefuse();
					if (inviteStatus.getRefuseList().equals("")) {
						inviteStatus.setRefuseList(user.getName());
					}else {
						inviteStatus.addRefuseList(user.getName());
					}
				}
				
				if(inviteStatus.checkBound()) {
					lock.notify();
				}
			}
		} catch (InterruptedException | RemoteException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}