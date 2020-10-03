package beans;

import java.util.List;

public class User {
	
	private String name;
	private String code;
	private List<String> checkingAccount;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public List<String> getCheckingAccount() {
		return this.checkingAccount;
	}
	
	public void setCheckingAccount(List<String> checkingAccouont) {
		this.checkingAccount = checkingAccouont;
	}
}
