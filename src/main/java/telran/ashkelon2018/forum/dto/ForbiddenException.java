package telran.ashkelon2018.forum.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
