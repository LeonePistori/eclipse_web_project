package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import dao.CheckingAccountDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import utils.ConnectionHandler;

/**
 * Servlet implementation class DoTransaction
 */
@WebServlet("/DoTransaction")
public class DoTransaction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoTransaction() {
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
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		float amount = 0;
		String originAccount;
		String destinationAccount;
		String toUser;
		
		try {
			amount = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("amount")));
		} catch(Exception ex) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Amount not a number");
			return;
		}
		
		amount = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("amount")));
		originAccount = StringEscapeUtils.escapeJava((String)session.getAttribute("currentAccount"));
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
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account");
				return;
			}
		} catch(Exception ex) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account");
			return;
		}
		
		try {
			currentbalance = accountDAO.findBalanceByCode(originAccount);
			destinationUser = userDAO.getUsernameById(accountDAO.findUserByCode(destinationAccount));
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
			return;
		}
		
		if(amount < 0) amount = -amount;
		
		if(currentbalance < amount) {
			String path = "/WEB-INF/Error.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("reason", "You poor");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		if(toUser.compareTo(destinationUser)!=0) {
			String path = "/WEB-INF/Error.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("reason", "Mismatch between username and account code");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		try {
			tranDAO.createTransaction(amount, originAccount, destinationAccount);
			accountDAO.changeBalance(-amount, originAccount);
			accountDAO.changeBalance(amount, destinationAccount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		request.setAttribute("originAccount", originAccount);
		request.setAttribute("destinationAccount", destinationAccount);
		getServletContext().getRequestDispatcher("/GoToConfirm").forward(request,response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
