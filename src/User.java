

public class User{
	protected String name = null;
	protected int status = 2;
	RMIInterface client;
	
	User(){
		
	}
	User(String name, RMIInterface client){
		this.name=name;
		this.client=client;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public RMIInterface getClient() {
		return client;
	}
	public void setClient(RMIInterface client) {
		this.client = client;
	}
}