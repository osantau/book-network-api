package oct.soft.handler;

import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;
import lombok.Getter;

public enum BussinessErrorCodes {

	NO_CODE(0, NOT_IMPLEMENTED, "No Code"),
	INCORRECT_CURRENT_PASSWORD(300,BAD_REQUEST,"Current password is incorrect"),
	NEW_PASSWORD_DOES_NOT_MATCH(301,BAD_REQUEST,"New password does not match"),
	ACCOUNT_LOCKED(302, FORBIDDEN,"User account locked"),
	ACCOUNT_DISABLED(303, FORBIDDEN,"User account is disabled"),
	BAD_CREDENTIALS(304, FORBIDDEN,"Login and / or password is incorrect"),
	;

	@Getter
	private final int code;
	@Getter
	private final String description;
	@Getter
	private final HttpStatus httpStatus;

	private BussinessErrorCodes(int code, HttpStatus httpStatus, String description) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.description = description;

	}

}
