/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UpdateBookFailedException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.service.BookService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class BookController {

	@Autowired
	private BookService bookService;

	
	@GetMapping("/books")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Object> getAllBooksPaginate(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size){

		List<Book> books = new ArrayList<>();
		Pageable paging = PageRequest.of(page, size);
		books = bookService.findAllPaginate(paging);
		
		if(books.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
		return new ResponseEntity<>(books, HttpStatus.OK);		
	}
	
	@GetMapping("/books/{isbn}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Object> getBook(@PathVariable("isbn") String isbn) throws BookNotFoundException{
		
		Book book;
		try {
			book = bookService.findByISBN(isbn);
			
			return new ResponseEntity<>(book, HttpStatus.OK);
			
		} catch (Exception e) {
			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
	
	@GetMapping("/books/{isbn}/authors")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Object> getAuthorsByBookISBN(@PathVariable("isbn") String isbn) throws BookNotFoundException{
		
		Set<Author> authors = bookService.findAuthorsByBookISBN(isbn);
		
		if(authors.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
		return new ResponseEntity<>(authors, HttpStatus.OK);		
	}
	
	@PostMapping("/books")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Book> createBook(@RequestBody Book book){
		
		Book nbook = bookService.saveBook(book);
		
		return new ResponseEntity<>(nbook, HttpStatus.CREATED);
	}
	
	@PutMapping("/books/{isbn}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Book> updateBook(@RequestBody Book book, @PathVariable("isbn") String isbn){
		
		Book nbook;
		try {
			nbook = bookService.updateBook(book, isbn);
		}catch(BookNotFoundException | UpdateBookFailedException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(nbook, HttpStatus.OK);
	}
	
	@DeleteMapping("/books/{isbn}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<String> deleteBook(@PathVariable("isbn") String isbn) throws BookNotFoundException{
		
		bookService.deleteByISBN(isbn);
		
		return new ResponseEntity<>(HttpStatus.OK);		
	}

}
