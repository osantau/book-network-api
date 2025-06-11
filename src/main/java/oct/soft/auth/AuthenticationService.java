package oct.soft.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oct.soft.email.EmailService;
import oct.soft.email.EmailTemplateName;
import oct.soft.role.RoleRepository;
import oct.soft.security.JwtService;
import oct.soft.user.Token;
import oct.soft.user.TokenRepository;
import oct.soft.user.User;
import oct.soft.user.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final RoleRepository roleRepo;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepo;
	private final TokenRepository tokenRepo;
	private final EmailService emailService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Value("${application.mailing.frontend.activation-url}")
	private String activationUrl;

	public void register(@Valid RegistrationRequest regReq) throws MessagingException {
		var userRole = roleRepo.findByName("USER")
				.orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized !"));
		var user = User.builder().firstName(regReq.getFirstName()).lastName(regReq.getLastName())
				.email(regReq.getEmail()).password(passwordEncoder.encode(regReq.getPassword())).accountLocked(false)
				.enabled(false).roles(List.of(userRole)).build();
		userRepo.save(user);
		sendValidatonEmail(user);
	}

	public void sendValidatonEmail(User user) throws MessagingException {
		var newToken = generateAndSaveActivationToken(user);

		emailService.sendEmail(user.getEmail(), user.fullName(), EmailTemplateName.ACTIVATE_ACCOUNT, activationUrl,
				newToken, "Account Activation");

	}

	public String generateAndSaveActivationToken(User user) {
		// generate token
		String generatedToken = generateActivationToken(6);
		var token = Token.builder().token(generatedToken).createdAt(LocalDateTime.now())
				.expiredAt(LocalDateTime.now().plusMinutes(15)).user(user).build();
		tokenRepo.save(token);

		return generatedToken;
	}

	public String generateActivationToken(int length) {
		String characters = "0123456789";
		StringBuilder codeBuilder = new StringBuilder();
		SecureRandom secureRandom = new SecureRandom();
		for (int i = 0; i < length; i++) {
			int randomIndex = secureRandom.nextInt(characters.length());
			codeBuilder.append(characters.charAt(randomIndex));
		}
		return codeBuilder.toString();
	}

	public AuthenticationResponse authenticate(@Valid AuthenticationRequest req) {
		var auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
		var claims = new HashMap<String, Object>();
		var user = ((User) auth.getPrincipal());
		claims.put("fullName", user.fullName());
		var jwtToken = jwtService.generateToken(claims, user);
		return AuthenticationResponse.builder().token(jwtToken).build();
	}

//	@Transactional
	public void activateAccount(String token) throws MessagingException {
				Token savedToken = tokenRepo.findByToken(token).orElseThrow(()->new RuntimeException("Invalid Token"));
				if(LocalDateTime.now().isAfter(savedToken.getExpiredAt()))
				{
					sendValidatonEmail(savedToken.getUser());
					throw new RuntimeException("Activation token expired. A new token has been sent to the same email address !");
				}
				var user = userRepo.findById(savedToken.getUser().getId()).orElseThrow(()->new UsernameNotFoundException("User nor found"));
				user.setEnabled(true);
				userRepo.save(user);
				savedToken.setValidatedAt(LocalDateTime.now());
				tokenRepo.save(savedToken);
	}

}
