package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Transaction;
import dao.CheckingAccountDAO;
import dao.TransactionDAO;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GoToAccountInfo
 */
@WebServlet("/GoToAccountInfo")
public class GoToAccountInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToAccountInfo() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String selectedAccountID = request.getParameter("accountid");
		CheckingAccountDAO accountDAO = new CheckingAccountDAO(connection);
		TransactionDAO transactionDAO = new TransactionDAO(connection);
		List<Transaction> transactions = new ArrayList<Transaction>();
		float balance;
		
		try {
			transactions = transactionDAO.findTransactionByCode(selectedAccountID);
			balance = accountDAO.findBalanceByCode(selectedAccountID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to find transactions or balance");
			return;
		}
		
		String path = "/WEB-INF/AccountInfo.html";
		ServletContext servletContext = getServletContext();
		request.getSession().setAttribute("currentAccount", selectedAccountID);
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("accountcode", selectedAccountID);
		ctx.setVariable("accountbalance", balance);
		ctx.setVariable("transactions", transactions);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
