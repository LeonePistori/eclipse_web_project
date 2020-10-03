package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.User;

public class UserDAO {
	
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  user.name, user.code, checkingaccount.code FROM user LEFT JOIN checkingaccount ON user.code=checkingaccount.userID WHERE name = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					List<String> accounts = new ArrayList<String>();
					result.next();
					User user = new User();
					user.setName(result.getString("name"));
					user.setCode(result.getString("user.code"));
					if(result.getString("checkingaccount.code") == null) {
						user.setCheckingAccount(null);
					}
					else {
						accounts.add(result.getString("checkingaccount.code"));
						while(result.next()) {
							accounts.add(result.getString("checkingaccount.code"));
						}
						user.setCheckingAccount(accounts);
					}
					
					return user;
				}
			}
		}
	}
	
	public boolean checkUsernameFree(String username) throws SQLException{
		String query = "SELECT name FROM user WHERE name = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return true;
				else {
					return false;
				}
			}
		}
	}
	
	
	public void registerUser(String username, String password) throws SQLException{
		String count = String.format("%05d", numberOfUsers() + 1).replace(" ", "0");
		
		String query = "INSERT INTO user (name, code, password) VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, count);
			pstatement.setString(3, password);
			pstatement.executeUpdate();
		}
	}
	
	public int numberOfUsers() throws SQLException{
		String query = "SELECT COUNT(*) AS count FROM user";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					throw new SQLException();
				else {
					result.next();
					return result.getInt("count");
				}
			}
		}
	}
	
	public String getUsernameById(String id) throws SQLException{
		String query = "SELECT  name FROM user WHERE code = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if(!result.isBeforeFirst())
					return null;
				else {
					result.next();
					return result.getString("name");
				}
			}
		}
	}
}
