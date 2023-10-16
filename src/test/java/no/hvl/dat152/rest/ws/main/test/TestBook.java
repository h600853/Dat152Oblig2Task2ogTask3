package no.hvl.dat152.rest.ws.main.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.service.AuthorService;
import no.hvl.dat152.rest.ws.service.BookService;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestBook {

	@Autowired
	private BookService bookService;
	
	@Autowired
	private AuthorService authorService;

	private String API_ROOT = "http://localhost:8090/elibrary/api/v1";
	
	@Value("${super.admin.token}") 
	private String SUPER_ADMIN_TOKEN;
	
	@Value("${user.token}")
	private String USER_TOKEN;
	
	@DisplayName("JUnit test for @GetMapping(/books) endpoint")
	@Test
	public void getAllBooks_thenOK() {
		Response response = RestAssured.given()
				.header("Authorization", "Bearer "+ SUPER_ADMIN_TOKEN)
				.get(API_ROOT+"/books");
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("isbn").size() > 0);
	}
	
	@DisplayName("JUnit test for @GetMapping(/books/{isbn}) endpoint")
	@Test
	public void getBookByIsbn_thenOK() throws AuthorNotFoundException {

	    Response response = RestAssured.given()
				.header("Authorization", "Bearer "+ SUPER_ADMIN_TOKEN)
	    		.get(API_ROOT+"/books/abcde1234");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals("abcde1234", response.jsonPath().get("isbn"));
	}
	
	@DisplayName("JUnit test for @PostMapping(/books) endpoint")
	@Test
	public void createBook_thenOK() throws AuthorNotFoundException {
		Book book = createRandomBook();
		Response response = RestAssured.given()
				.header("Authorization", "Bearer "+ SUPER_ADMIN_TOKEN)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(book)
				.post(API_ROOT+"/books");
	    
	    assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
	    assertEquals(book.getTitle(), response.jsonPath().get("title"));
	}
	
	@DisplayName("JUnit test for @PostMapping(/books) endpoint")
	@Test
	public void createBook_USER_ROLE_thenOK() throws AuthorNotFoundException {
		Book book = createRandomBook();
		Response response = RestAssured.given()
				.header("Authorization", "Bearer "+ USER_TOKEN)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(book)
				.post(API_ROOT+"/books");
	    
	    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
	}
	
	@DisplayName("JUnit test for @GetMapping(/books/{isbn}/authors) endpoint")
	@Test
	public void getAuthorsOfBook_thenOK() throws AuthorNotFoundException, BookNotFoundException {
		
		Response response = RestAssured.given()
				.header("Authorization", "Bearer "+ SUPER_ADMIN_TOKEN)
				.get(API_ROOT+"/books/abcde1234/authors");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertTrue(response.jsonPath().getList("authorId").size() > 0);
	}
	
	@DisplayName("JUnit test for @PutMapping(/books/{isbn}) endpoint")
	@Test
	public void updateBook_thenOK() throws AuthorNotFoundException, BookNotFoundException {
		Book b = createRandomBook2();
		b = bookService.saveBook(b);
		
		Book book = createRandomBook2();
		String newTitle = "Software Engineering_2";
		book.setId(b.getId());
		book.setTitle(newTitle);
		
		Response response = RestAssured.given()
				.header("Authorization", "Bearer "+ SUPER_ADMIN_TOKEN)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(book)
				.put(API_ROOT+"/books/{isbn}", "hello_1245");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals(book.getTitle(), response.jsonPath().get("title"));
	}

	@DisplayName("JUnit test for @DeleteMapping(/books/{isbn}) endpoint")
	@Test
	public void deleteBookByIsbn_thenOK() throws AuthorNotFoundException {

	    Response response = RestAssured.given()
				.header("Authorization", "Bearer "+ SUPER_ADMIN_TOKEN)
	    		.delete(API_ROOT+"/books/qabfde1230");
	    
	    // attempt to access the same resource again
	    Response resp = RestAssured.given()
				.header("Authorization", "Bearer "+ SUPER_ADMIN_TOKEN)
	    		.get(API_ROOT+"/books/qabfde1230");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), resp.getStatusCode());

	}
	
	private Book createRandomBook() throws AuthorNotFoundException {
		
		Author savedAuthor = authorService.findById(4L);
		
		Set<Author> authors = new HashSet<Author>();
		authors.add(savedAuthor);
		
		Book book = new Book();
		book.setIsbn("yugbsn_1245");
		book.setTitle("Book1");
		book.setAuthors(authors);
		
		return book;
	}
	
	private Book createRandomBook2() throws AuthorNotFoundException {
		
		Author savedAuthor = authorService.findById(5L);
		
		Set<Author> authors = new HashSet<Author>();
		authors.add(savedAuthor);
		
		Book book = new Book();
		book.setIsbn("hello_1245");
		book.setTitle("Hello_Book1");
		book.setAuthors(authors);
		
		return book;
	}

}
