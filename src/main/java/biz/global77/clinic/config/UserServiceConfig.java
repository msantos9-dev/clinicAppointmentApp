package biz.global77.clinic.config;



	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.security.core.userdetails.UserDetails;
	import org.springframework.security.core.userdetails.UserDetailsService;
	import org.springframework.security.core.userdetails.UsernameNotFoundException;
	import org.springframework.stereotype.Service;

import biz.global77.clinic.model.User;
import biz.global77.clinic.repository.UserRepository;


	@Service
	public class UserServiceConfig implements UserDetailsService {

		@Autowired
		private UserRepository userRepo;

		@Override
		public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

			User user = userRepo.findByEmail(email);

			if (user != null) {
				return new UserConfig(user);
			}

			throw new UsernameNotFoundException("user not available");
		}

	}