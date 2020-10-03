package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import beans.User;
import dao.CheckingAccountDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import utils.ConnectionHandler;

/**
 * Servlet implementation class CreateTransaction
 */
@WebServlet("/CreateTransaction")
@MultipartConfig
public class CreateTransaction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateTransaction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		float amount = 0;
		String originAccount;
		String destinationAccount;
		String toUser;
		
		HttpSession session = request.getSession();
		
		String currentaccount = (String)session.getAttribute("currentaccount");
		try {
			amount = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("amount")));
		} catch(Exception ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Amount has to be a number");
			return;
		}
		originAccount = currentaccount;
		destinationAccount = StringEscapeUtils.escapeJava(request.getParameter("toaccount"));
		toUser = StringEscapeUtils.escapeJava(request.getParameter("touser"));
		
		CheckingAccountDAO accountDAO = new CheckingAccountDAO(connection);
		TransactionDAO tranDAO = new TransactionDAO(connection);
		UserDAO userDAO = new UserDAO(connection);
		
		float currentbalance = 0;
		String destinationUser = "";
		
		try {
			Integer.parseInt(destinationAccount);
			if(destinationAccount.length() != 5) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Account of wrong length");
				return;
			}
		} catch(Exception ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Account code not a number");
			return;
		}
		
		try {
			currentbalance = accountDAO.findBalanceByCode(originAccount);
			destinationUser = userDAO.getUsernameById(accountDAO.findUserByCode(destinationAccount));
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
			e.printStackTrace();
			return;
		}
		
		if(amount < 0) amount = -amount;
		
		if(currentbalance < amount) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Not enough money");
			return;
		}
		
		if(toUser.compareTo(destinationUser)!=0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Account doesn't belong to that user");
			return;
		}
		
		try {
			tranDAO.createTransaction(amount, originAccount, destinationAccount);
			accountDAO.changeBalance(-amount, originAccount);
			accountDAO.changeBalance(amount, destinationAccount);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
			e.printStackTrace();
			return;
		}
		
		String fromUser = ((User)request.getSession().getAttribute("user")).getName();
		float destinationaccountbalance = 0;
		try {
			currentbalance = accountDAO.findBalanceByCode(originAccount);
			destinationaccountbalance = accountDAO.findBalanceByCode(destinationAccount);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
			e.printStackTrace();
			return;
		}
		

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{" + '"' + "result" + '"' + ": " + '"' + "confirm" + '"' + 
				", " + '"' + "fromuser" + '"' + ": " + '"' + fromUser + '"'
				+ ", " + '"' + "fromaccount" + '"' + ": " + '"' + currentaccount + '"'
				+ ", " + '"' + "frombalance" + '"' + ": " + '"' + String.valueOf(currentbalance) + '"'
				+ ", " + '"' + "touser" + '"' + ": " + '"' + toUser + '"'
				+ ", " + '"' + "toaccount" + '"' + ": " + '"' + destinationAccount + '"'
				+ ", " + '"' + "tobalance" + '"' + ": " + '"' + String.valueOf(destinationaccountbalance) + '"'
				+ "}");
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
