/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.service.AuthorService;

/**
 * 
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class AuthorController {

	@Autowired
	private AuthorService authorService;
	
	@GetMapping("/authors/{id}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Author> getAuthor(@PathVariable("id") Long id) throws AuthorNotFoundException {
		
		Author author = authorService.findById(id);
		
		return new ResponseEntity<>(author, HttpStatus.OK);		
	}
	@GetMapping("/authors")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<List<Author>> getAuthors()
	{

		List<Author> authors = authorService.findAll();
		if(authors.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		return new ResponseEntity<>(authors, HttpStatus.OK);

	}
	@GetMapping("/authors/{id}/books")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Object> getAuthorBooks(@PathVariable String id) {
		Author author;
		Set<Book> books;
		try {
			author = authorService.findById(Long.parseLong(id));
			books = author.getBooks();
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(books, HttpStatus.OK);
	}
	@PostMapping("/authors")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Author> createAuthor(@RequestBody Author author)
	{
		Author nauthor = authorService.saveAuthor(author);
		return new ResponseEntity<>(nauthor, HttpStatus.CREATED);
	}
	@PutMapping("/authors/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Author> updateAuthor(@PathVariable("id") String id, @RequestBody Author author) {
		try {
			authorService.findById(Long.parseLong(id));
			author.setAuthorId(Integer.parseInt(id));
			authorService.saveAuthor(author);
			return new ResponseEntity<>(author, HttpStatus.OK);
		} catch (NoSuchElementException e) {

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}


}
