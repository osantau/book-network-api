package oct.soft.handler;

import static oct.soft.handler.BussinessErrorCodes.ACCOUNT_DISABLED;
import static oct.soft.handler.BussinessErrorCodes.ACCOUNT_LOCKED;
import static oct.soft.handler.BussinessErrorCodes.BAD_CREDENTIALS;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.mail.MessagingException;
import oct.soft.exception.OperationNotPermittedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(LockedException.class)
	public ResponseEntity<ExceptionResponse> handleException(LockedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ExceptionResponse.builder().businessErrorCode(ACCOUNT_LOCKED.getCode())
						.businessErrorDescription(ACCOUNT_LOCKED.getDescription()).error(ex.getMessage()).build());

	}

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ExceptionResponse> handleException(DisabledException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ExceptionResponse.builder().businessErrorCode(ACCOUNT_DISABLED.getCode())
						.businessErrorDescription(ACCOUNT_DISABLED.getDescription()).error(ex.getMessage()).build());

	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ExceptionResponse.builder().businessErrorCode(BAD_CREDENTIALS.getCode())
						.businessErrorDescription(BAD_CREDENTIALS.getDescription()).error(ex.getMessage()).build());

	}

	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<ExceptionResponse> handleException(MessagingException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ExceptionResponse.builder().error(ex.getMessage()).build());

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException ex) {
		Set<String> errors = new HashSet<>();
		ex.getBindingResult().getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ExceptionResponse.builder().validationErrors(errors).build());

	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
		// log the exception in console
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExceptionResponse.builder()
				.businessErrorDescription("Internal error, contact admin").error(ex.getMessage()).build());

	}

	@ExceptionHandler(OperationNotPermittedException.class)
	public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ExceptionResponse.builder().error(ex.getMessage()).build());

	}
}
