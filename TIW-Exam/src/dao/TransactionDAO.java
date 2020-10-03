package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Transaction;

public class TransactionDAO {

	private Connection connection;

	public TransactionDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Transaction> findTransactionByCode(String code) throws SQLException{
		String query = "SELECT  * FROM transaction WHERE fromaccount = ? OR toaccount = ? ORDER BY date DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, code);
			pstatement.setString(2, code);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					List<Transaction> transactions = new ArrayList<Transaction>();
					while(result.next()) {
						Transaction transaction = new Transaction();
						transaction.setDate(result.getDate("date"));
						transaction.setAmount(result.getInt("amount"));
						transaction.setOriginAccount(result.getString("fromaccount"));
						transaction.setDestinationAccount(result.getString("toaccount"));
						transactions.add(transaction);
					}
					return transactions;
				}
			}
		}
	}
	
	public int numberOfTransactions() throws SQLException{
		String query = "SELECT COUNT(*) AS count FROM transaction";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
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
	
	public void createTransaction(float amount, String originAccount, String destinationAccount) throws SQLException {
		
		Date date = new Date(System.currentTimeMillis());
		String count = String.format("%020d", numberOfTransactions()).replace(" ", "0");
		
		String query = "INSERT INTO transaction (code, date, amount, fromaccount, toaccount) VALUES(?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, count);
			pstatement.setDate(2, date);
			pstatement.setFloat(3, amount);
			pstatement.setString(4, originAccount);
			pstatement.setString(5, destinationAccount);
			pstatement.executeUpdate();
		}
	}
}
