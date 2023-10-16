/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UpdateBookFailedException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.BookRepository;

/**
 * @author tdoy
 */
@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
	
	public Book saveBook(Book book) {
		
		return bookRepository.save(book);
		
	}
	
	public Book updateBook(Book book, String isbn) 
			throws BookNotFoundException, UpdateBookFailedException {

		try {
			book.setIsbn(isbn);
			return saveBook(book);
		} catch (Exception e) {
			throw new UpdateBookFailedException("Update book with isbn: "+isbn+" failed!");
		}
	}
	
	public List<Book> findAll(){
		
		return (List<Book>) bookRepository.findAll();
		
	}
	
	public List<Book> findAllPaginate(Pageable page){
		
		Page<Book> books = bookRepository.findAll(page);
		
		return books.getContent();
	}
	
	public Book findById(long id) throws BookNotFoundException{
		
		Book book = findBookById(id);
		
		return book;
	}
	
	public Book findByISBN(String isbn) throws BookNotFoundException {
		
		Book book = bookRepository.findByIsbn(isbn)
				.orElseThrow(() -> new BookNotFoundException("Book with isbn = "+isbn+" not found!"));
		
		return book;
	}

	public Set<Author> findAuthorsByBookISBN(String isbn) throws BookNotFoundException{

		try {
			Book bookByISBN = bookRepository.findBookByISBN(isbn);
			return bookByISBN.getAuthors();
		} catch (Exception e) {
			throw new BookNotFoundException("Book with isbn: "+isbn+" not found");
		}

	}

	public void deleteByISBN(String isbn) throws BookNotFoundException {

		try {
			Book bookByISBN = bookRepository.findBookByISBN(isbn);
			bookRepository.delete(bookByISBN);

		} catch (Exception e) {
			throw new BookNotFoundException("Book with isbn: "+isbn+" not found");
		}

	}
	
	private Book findBookById(long id) throws BookNotFoundException {
		
		Book book = bookRepository.findById(id)
				.orElseThrow(() -> new BookNotFoundException("Book with id = "+id+" not found!"));
		
		return book;
	}
}
