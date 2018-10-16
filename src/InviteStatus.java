
public class InviteStatus {
	private int allnumber = 0;
	private int accepted = 0;
	private int refuse = 0;
	private int ifWaiting = 0;
	private String refuseList = "";

	public int getAllnumber() {
		return allnumber;
	}

	public void setAllnumber(int allnumber) {
		this.allnumber = allnumber;
	}

	public int getAccepted() {
		return accepted;
	}

	public void setAccepted(int accepted) {
		this.accepted = accepted;
	}

	public int getRefuse() {
		return refuse;
	}

	public void setRefuse(int refuse) {
		this.refuse = refuse;
	}

	public int getIfWaiting() {
		return ifWaiting;
	}

	public void setIfWaiting(int ifWaiting) {
		this.ifWaiting = ifWaiting;
	}

	public String getRefuseList() {
		return refuseList;
	}

	public void setRefuseList(String refuseList) {
		this.refuseList = refuseList;
	}
	public void addAccepted() {
		accepted++;
	}
	public void addRefuse() {
		refuse++;
	}
	public void addRefuseList(String s) {
		refuseList = refuseList + "," + s;
	}
	public boolean checkBound() {
		if(allnumber==(accepted+refuse))
			return true;
		else
			return false;
	}
}