package oct.soft.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import oct.soft.role.RoleRepository;
import oct.soft.user.Token;
import oct.soft.user.TokenRepository;
import oct.soft.user.User;
import oct.soft.user.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;

	public void register(RegistrationRequest request) {
		var userRole = roleRepository.findByName("USER")
				.orElseThrow(() -> new IllegalStateException("Role USER was not initalized"));
		var user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
				.email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).accountLocked(false)
				.enabled(false).roles(List.of(userRole)).build();
		userRepository.save(user);
		sendValidationEmail(user);		
	}

	private void sendValidationEmail(User user) {
		var newToken = generateAndSaveActivationToken(user);
		//send email
		
	}

	private String generateAndSaveActivationToken(User user) {
		String generatedToken = generateActivationCode(6);
		var token = Token.builder().token(generatedToken).createdAt(LocalDateTime.now()).expiresAt(LocalDateTime.now().plusMinutes(15))
				.user(user).build();
		tokenRepository.save(token);
		return generatedToken;
	}

	private String generateActivationCode(int length) {
		String characters = "0123456789";
		StringBuilder codeBuilder = new StringBuilder();
		SecureRandom secureRandom = new SecureRandom();
		for(int i=0;i<length;i++)
		{
			int randomIndex = secureRandom.nextInt(characters.length()); // 0..9
			codeBuilder.append(characters.charAt(randomIndex));
		}
		return codeBuilder.toString();
	}

}
