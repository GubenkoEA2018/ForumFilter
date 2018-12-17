package telran.ashkelon2018.forum.service.filter;

import java.io.IOException;

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
@Order(3)
public class RolesFilter implements Filter {
		
		@Autowired
		UserAccountRepository repository;
		
		@Autowired
		AccountConfiguration configuration;
		
		@Override
		public void doFilter(ServletRequest reqs, 
								ServletResponse resp, 
								FilterChain chain)
				throws IOException, ServletException {
			HttpServletRequest request = 
					(HttpServletRequest) reqs;//cast
			HttpServletResponse response = 
					(HttpServletResponse) resp;//cast
			String path = request.getServletPath();//give me road
			System.out.println(path);
			String method = request.getMethod();
			System.out.println(method);
			String token = request.getHeader("Authorization");
			if(!path.startsWith("/account/role")) {
				AccountUserCredentials userCredentials = 
						configuration.tokenDecode(token);
				System.err.println("filter3");
				UserAccount userAccount = 
								repository.findById(userCredentials.getLogin()).orElse(null);
				if(!userAccount.getRoles().contains("Admin")) {
					response.sendError(403, "Access denied");
					return;
				}
			
			}
			chain.doFilter(request, response);// logika this
		}
}
