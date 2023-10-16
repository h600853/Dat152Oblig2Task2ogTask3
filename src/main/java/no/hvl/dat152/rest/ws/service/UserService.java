/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public User saveUser(User user) {
		
		user = userRepository.save(user);
		
		return user;
	}
	
	public void deleteUser(Long id) throws UserNotFoundException {


		try  {
			User user = findUser(id);
			userRepository.delete(user);
		}
		catch (Exception e) {
			throw new UserNotFoundException("User with id: "+id+" not found");
		}
	}
	
	public User updateUser(User user, Long id) throws UserNotFoundException {

		try  {
			user.setUserid(id);
			return saveUser(user);
		} catch (Exception e) {
			throw new UserNotFoundException("User with id: "+id+" not found");
		}
		
	}
	
	public List<User> findAllUsers(){
		
		List<User> allUsers = (List<User>) userRepository.findAll();
		
		return allUsers;
	}
	
	public User findUser(Long id) throws UserNotFoundException {
		
		User user = userRepository.findById(id)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+id+" not found"));
		
		return user;
	}
	
	public Set<Order> findOrdersForUser(Long id) throws UserNotFoundException{
		
		User user = findUser(id);
		
		return user.getOrders();
	}
	
	public User createOrdersForUser(Long id, Order order) throws UserNotFoundException {

		try {
			User user = findUser(id);
			user.addOrder(order);

			return saveUser(user);

		} catch (Exception e) {
			throw new UserNotFoundException("User with id: " + id + " not found");
		}
	}
}
