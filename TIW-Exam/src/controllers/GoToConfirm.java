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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.User;
import dao.CheckingAccountDAO;
import utils.ConnectionHandler;

/**
 * Servlet implementation class ToConfirm
 */
@WebServlet("/GoToConfirm")
public class GoToConfirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToConfirm() {
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
		HttpSession session = request.getSession();
		
		String originAccount = (String) request.getAttribute("originAccount");
		String destinationAccount = (String) request.getAttribute("destinationAccount");
		
		CheckingAccountDAO accountDAO = new CheckingAccountDAO(connection);
		
		String user1;
		String account1;
		float balance1;
		
		String user2;
		String account2;
		float balance2;
		
		try {
			user1 = ((User) session.getAttribute("user")).getCode();
			account1 = originAccount;
			balance1 = accountDAO.findBalanceByCode(originAccount);
			
			user2 = accountDAO.findUserByCode(destinationAccount);
			account2 = (String) request.getAttribute("destinationAccount");
			balance2 = accountDAO.findBalanceByCode(destinationAccount);
		} catch (SQLException e){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
			return;
		}
		
		String path = "/WEB-INF/Confirm.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("username1", user1);
		ctx.setVariable("accountcode1", account1);
		ctx.setVariable("balance1", balance1);
		ctx.setVariable("username2", user2);
		ctx.setVariable("accountcode2", account2);
		ctx.setVariable("balance2", balance2);
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
