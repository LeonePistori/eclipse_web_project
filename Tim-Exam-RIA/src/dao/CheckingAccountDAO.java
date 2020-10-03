package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.CheckingAccount;

public class CheckingAccountDAO {

	private Connection con;

	public CheckingAccountDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<CheckingAccount> findAccountsByUser(String usrn) throws SQLException {
		String query = "SELECT checkingaccount.code AS accountCode FROM user JOIN checkingaccount ON user.code=checkingaccount.userID WHERE name = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					throw new SQLException();
				else {
					List<CheckingAccount> accounts = new ArrayList<CheckingAccount>();
					while(result.next()) {
						CheckingAccount account = new CheckingAccount();
						account.setCode(result.getString("accountCode"));
						accounts.add(account);
					} 
					return accounts;
				}
			}
		}
	}
	
	public String findUsernameByAccount(String account) throws SQLException{
		String query = "SELECT user.name AS username FROM user JOIN checkingaccount ON user.code=checkingaccount.userID WHERE checkingaccount.code = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, account);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					return result.getString("username");
				}
			}
		}
	}
	
	public String findUserByCode(String code) throws SQLException{
		String query = "SELECT userID FROM checkingaccount WHERE code = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, code);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					throw new SQLException();
				else {
					result.next();
					return result.getString("userID");
				}
			}
		}
	}
	
	public float findBalanceByCode(String code) throws SQLException{
		String query = "SELECT balance FROM checkingaccount WHERE code = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, code);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					throw new SQLException();
				else {
					result.next();
					float balance = result.getFloat("balance");
					return balance;
				}
			}
		}
	}
	
	public void changeBalance(float amount, String account) throws SQLException {
		float currentbalance;
		float updatedbalance;
		
		String query = "SELECT balance FROM checkingaccount WHERE code = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, account);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					throw new SQLException();
				else {
					result.next();
					currentbalance = result.getFloat("balance");
				}
			}
		}
		
		query = "UPDATE checkingaccount SET balance = ? WHERE code = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			updatedbalance = currentbalance + amount;
			pstatement.setFloat(1, updatedbalance);
			pstatement.setString(2, account);
			pstatement.executeUpdate();
		}
	}
}
