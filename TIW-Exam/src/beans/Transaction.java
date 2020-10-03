package beans;

import java.sql.Date;

public class Transaction {
	
	private Date date;
	private float amount;
	private String originAccount;
	private String destinationAccount;
	
	public Date getDate() {
		return this.date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public float getAmount() {
		return this.amount;
	}
	
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	public String getOriginAccount() {
		return this.originAccount;
	}
	
	public void setOriginAccount(String originAccount) {
		this.originAccount = originAccount;
	}
	
	public String getDestinationAccount() {
		return this.destinationAccount;
	}
	
	public void setDestinationAccount(String destinationAccount) {
		this.destinationAccount = destinationAccount;
	}
}
