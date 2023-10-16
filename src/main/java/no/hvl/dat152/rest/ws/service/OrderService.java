/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.repository.OrderRepository;
import no.hvl.dat152.rest.ws.security.service.UserDetailsImpl;

/**
 * @author tdoy
 */
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	public Order saveOrder(Order order) {
		
		order = orderRepository.save(order);
		
		return order;
	}
	
	public void deleteOrder(Long id) throws OrderNotFoundException, UnauthorizedOrderActionException {

		try {
			orderRepository.deleteById(id);
		} catch (Exception e) {
			throw new OrderNotFoundException("Order with id: "+id+" not found");
		}
	}
	
	public List<Order> findAllOrders(){
		
		List<Order> orders = (List<Order>) orderRepository.findAll();
		
		return orders;
	}

	
	public Order findOrder(Long id) throws OrderNotFoundException, UnauthorizedOrderActionException {
		
		verifyPrincipalOfOrder(id);	
		Order order = orderRepository.findById(id)
				.orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));
		
		return order;
	}
	
	public Order updateOrder(Order order, Long id) throws OrderNotFoundException, UnauthorizedOrderActionException {

		try {
			order.setId(id);
			return saveOrder(order);
		} catch (Exception e) {
			throw new OrderNotFoundException("Order with id: "+id+" not found");
		}
	}
	
	public List<Order> findByExpiryDate(LocalDate expiry, Pageable page){

		Page<Order> orders = orderRepository.findByExpiry(expiry, page);
		return orders.getContent();
	}
	
	private boolean verifyPrincipalOfOrder(Long id) throws UnauthorizedOrderActionException {
		
		UserDetailsImpl userPrincipal = (UserDetailsImpl) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
		// verify if the user sending request is an ADMIN or SUPER_ADMIN
		for(GrantedAuthority authority : userPrincipal.getAuthorities()){
			if(authority.getAuthority().equals("ADMIN") || 
					authority.getAuthority().equals("SUPER_ADMIN")) {
				return true;
			}
		}
		
		// otherwise, make sure that the user is the one who initially made the order
		String email = orderRepository.findEmailByOrderId(id);
		
		if(email.equals(userPrincipal.getEmail()))
			return true;
		
		throw new UnauthorizedOrderActionException("Unauthorized order action!");

	}
}
