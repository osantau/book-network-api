package oct.soft.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
	@NotEmpty(message = "Email is mandatory !")
	@NotBlank(message = "Email is mandatory !")
	@Email(message = "Email is not well formatted !")
	private String email;
	@Size(min = 8, message = "Password shold be 8 characters long minimum !")
	@NotEmpty(message = "Password is mandatory !")
	@NotBlank(message = "Password is mandatory !")
	private String password;
}
