package telran.ashkelon2018.forum.service.filter;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;

@Service
@Order(2)
public class ExpDateFilter implements Filter {

	@Autowired
	UserAccountRepository repository;
	
	@Autowired
	AccountConfiguration configuration;
	
	@Override
	public void doFilter(ServletRequest reqs, 
							ServletResponse resp, 
							FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) reqs;//cast
		HttpServletResponse response = (HttpServletResponse) resp;//cast
		String path = request.getServletPath();//give me road
//		boolean filter = 
//				request.getAuthType().equals(HttpServletRequest.BASIC_AUTH);
		String token = request.getHeader("Authorization");
		String method = request.getMethod();
		if(!path.startsWith("/account/password") && token != null
				&& !method.equals("POST")) {
			AccountUserCredentials userCredentials = 
					configuration.tokenDecode(token);
			System.err.println("filter2");
			UserAccount userAccount = 
							repository.findById(userCredentials.getLogin()).orElse(null);
			if(userAccount != null && userAccount.getExpdate().isBefore(LocalDateTime.now())) {
				response.sendError(403, "Password expired");
				return;
			}
		
		}
		chain.doFilter(request, response);// logika this
	}
}
