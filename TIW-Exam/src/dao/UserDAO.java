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
		String query = "SELECT  name, user.code, checkingaccount.code FROM user JOIN checkingaccount ON user.code=checkingaccount.userID WHERE name = ? AND password =?";
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
					accounts.add(result.getString("checkingaccount.code"));
					while(result.next()) {
						accounts.add(result.getString("checkingaccount.code"));
					}
					user.setCheckingAccount(accounts);
					return user;
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
