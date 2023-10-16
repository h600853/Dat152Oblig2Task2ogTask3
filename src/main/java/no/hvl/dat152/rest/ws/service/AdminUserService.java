/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Role;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.RoleRepository;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class AdminUserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	public User saveUser(User user) {
		
		user = userRepository.save(user);
		
		return user;
	}

	public User deleteUserRole(Long id, String role) throws UserNotFoundException {

		Optional<User> user = userRepository.findById(id);
		User user1 = null;
		Role role1 = roleRepository.findByName(role);
		if (user.isPresent()) {
			user1 = user.get();

			Set<Role> roles = user1.getRoles();

			Set<Role> newRoles = new HashSet<>();
			for (Role r : roles) {
				if (!r.getName().equalsIgnoreCase(role)) {
					newRoles.add(r);
				}
			}
			user1.setRoles(newRoles);
			userRepository.save(user1);


			return user1;
		} else {
			throw new UserNotFoundException("User with id: " + id + " could not be found");
		}
	}

	public User updateUserRole(Long id, String role) throws UserNotFoundException {

		User user;
		try {

			user = findUser(id);
			Role r = roleRepository.findByName(role);
			Set<Role> roles = user.getRoles();

				roles.add(r);
				user.setRoles(roles);
				user = userRepository.save(user);
				return user;

		} catch (UserNotFoundException e) {
			throw new UserNotFoundException("User with id: " + id + " not found");
		}
	}
	
	public User findUser(Long id) throws UserNotFoundException {
		
		User user = userRepository.findById(id)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+id+" not found"));
		
		return user;
	}
}
