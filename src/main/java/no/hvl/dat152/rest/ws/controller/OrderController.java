/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	/**
	 * This method can take query params (e.g.
	 * orders?expiry=2023-10-19)should give list of orders that have expiry date less than the specified date
	 * (orders?expiry=2023-10-19&page=1&size=5) 
	 * 
	 * @param expiry
	 * @param page (page number)
	 * @param size (# of record to fetch)
	 * @return ResponseEntity<Object>
	 */
	@GetMapping("/orders")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> getAllBorrowOrders(
			@RequestParam(required = false) LocalDate expiry, 
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size){
		 if (expiry == null) {
			 List<Order> allOrders = orderService.findAllOrders();
			 return new ResponseEntity<>(allOrders, HttpStatus.OK);
		 }
		List<Order> byExpiryDate = orderService.findByExpiryDate(expiry, PageRequest.of(page, size));
		if (byExpiryDate.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(byExpiryDate, HttpStatus.OK);
	}
	
	@GetMapping("/orders/{id}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Object> getBorrowOrder(@PathVariable("id") Long id) 
			throws OrderNotFoundException, UnauthorizedOrderActionException{
		
		try {
			Order order = orderService.findOrder(id);
			return new ResponseEntity<>(order, HttpStatus.OK);
			
		}catch(OrderNotFoundException | UnauthorizedOrderActionException e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/orders/{id}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Object> updateOrder(@PathVariable("id") Long id, @RequestBody Order order) 
			throws OrderNotFoundException, UserNotFoundException, UnauthorizedOrderActionException{

		order = orderService.updateOrder(order, id);
		Link rordersLink = linkTo(methodOn(OrderController.class).returnBookOrder(id)).withRel("Update_Return_or_Cancel");
		order.add(rordersLink);
		
		return new ResponseEntity<>(order, HttpStatus.OK);
	}
	
	@DeleteMapping("/orders/{id}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Object> returnBookOrder(@PathVariable("id") Long id) 
			throws OrderNotFoundException, UnauthorizedOrderActionException{

		try {
			orderService.deleteOrder(id);

		} catch (OrderNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (UnauthorizedOrderActionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<>("Order with id: '"+id+"' deleted!", HttpStatus.OK);
	}

	
}
