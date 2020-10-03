package beans;

import java.sql.Date;

public class CheckingAccount {

	private String code;
	private int balance;
	private Date[] transactionIn;
	private Date[] transactionOut;
	
	public String getCode() {
		return this.code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public int getBalance() {
		return this.balance;
	}
	
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public Date[] getTransactionIn() {
		return this.transactionIn;
	}
	
	public void setTransactionIn(Date[] transactionIn) {
		this.transactionIn = transactionIn;
	}
	
	public Date[] getTransactionOut() {
		return this.transactionOut;
	}
	
	public void setTransactionOut(Date[] transactionOut) {
		this.transactionOut = transactionOut;
	}
}
