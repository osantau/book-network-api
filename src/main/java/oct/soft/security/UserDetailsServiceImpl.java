package oct.soft.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import oct.soft.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
private final UserRepository userRepo;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		
		return userRepo.findByEmail(userEmail).orElseThrow(()-> new UsernameNotFoundException("User not found !"));
	}

}
