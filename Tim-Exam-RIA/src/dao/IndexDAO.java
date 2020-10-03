package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Index;

public class IndexDAO {

	private Connection con;

	public IndexDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Index> findIndexEntriesByUsername(String username) throws SQLException{
		String query = "SELECT * FROM indexbook WHERE username = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					List<Index> entries = new ArrayList<Index>();
					while(result.next()) {
						Index entry = new Index();
						entry.setUsername(result.getString("username"));
						entry.setToUser(result.getString("tousername"));
						entry.setToAccount(result.getString("toaccount"));
						entries.add(entry);
					} 
					return entries;
				}
			}
		}
	}
	
	public void createEntry(String user, String destinationUser, String destinationAccount) throws SQLException {
		String query = "INSERT INTO indexbook (username, tousername, toaccount) VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, user);
			pstatement.setString(2, destinationUser);
			pstatement.setString(3, destinationAccount);
			pstatement.executeUpdate();
		}
	}
}
