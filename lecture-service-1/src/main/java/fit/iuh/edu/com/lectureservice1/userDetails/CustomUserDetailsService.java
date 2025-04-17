//package com.lms.lectureservice.userDetails;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//	private final RestTemplate restTemplate;
//	private final String USERS_SERVICE_URL = "http://users-service/api/users"; // Adjust to your Users service URL
//
//	public CustomUserDetailsService(RestTemplate restTemplate) {
//		this.restTemplate = restTemplate;
//	}
//
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		String url = USERS_SERVICE_URL + "/" + username;
//
//		Map<String, Object> userResponse;
//		try {
//			userResponse = restTemplate.getForObject(url, Map.class);
//		} catch (Exception e) {
//			throw new UsernameNotFoundException("User not found: " + username);
//		}
//
//		if (userResponse == null || !userResponse.containsKey("username") || !userResponse.containsKey("password")) {
//			throw new UsernameNotFoundException("Invalid user response from Users service.");
//		}
//
//		String fetchedUsername = (String) userResponse.get("username");
//		String password = (String) userResponse.get("password");
//		List<String> roles = (List<String>) userResponse.get("roles");
//
//		return User.builder().username(fetchedUsername).password(password) // Assume it's already hashed
//				.authorities(mapRolesToAuthorities(roles)).build();
//	}
//
//	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(List<String> roles) {
//		return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Prefixing with ROLE_
//				.collect(Collectors.toList());
//	}
//}
