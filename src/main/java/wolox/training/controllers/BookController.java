package wolox.training.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exception.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.dto.BookDTO;
import wolox.training.repositories.BookRepository;
import wolox.training.service.OpenLibraryService;

/**
 * Book controller containing the operations of update , find , delete , find by id and create
 *
 * @author luismiguelrodriguez
 */
@RestController
@RequestMapping("/api/books")
@Api
public class BookController {

    /**
     * Repository of books
     */
    @Autowired
    private BookRepository bookRepository;

    /**
     * Service External Api
     */
    @Autowired
    private OpenLibraryService openLibraryService;

    /**
     * Method for search elements
     *
     * @param id variable used to identify the element to search
     * @return method that returns an object according to the id parameter
     */
    @ApiOperation(value = "Method to find a book", response = Book.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfuly retrieved book"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Book findById(@PathVariable Long id) {
        return bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
    }

    /**
     * Method for create elements
     *
     * @param book Object required to save a book
     * @return return a view of the saved object
     */
    @ApiOperation(value = "Method to create a book", response = Book.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfuly created book")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    /**
     * Method for update element
     *
     * @param book Object required to update a book
     * @param id   variable used to identify the element to update
     * @return return a view of the updated object
     */
    @ApiOperation(value = "Method to update a book", response = Book.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfuly updated book"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Book update(@RequestBody Book book, @PathVariable Long id) {
        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        book.setId(id);
        return bookRepository.save(book);
    }

    /**
     * Method for delete element
     *
     * @param id variable used to identify the element to delete
     */
    @ApiOperation(value = "Method to delete a book", response = Book.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfuly deleted book"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        bookRepository.deleteById(id);
    }

    /**
     * Method to search a book by isbn
     *
     * @param isbn param to search book in external api or internal repository
     * @return
     */
    @ApiOperation(value = "Method to search a book by isbn", response = Book.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Book found successfully"),
            @ApiResponse(code = 201, message = "Book created"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    @GetMapping("/find-by-isbn")
    public ResponseEntity<Book> findByIsbn(@RequestParam String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElseGet(() -> {
                    BookDTO bookDTO = openLibraryService.findInfoBook(isbn);
                    Book book = bookRepository.save(bookDTO.setBook());
                    return new ResponseEntity<>(book, HttpStatus.CREATED);
                });
    }

    /**
     * Method to search a book by the following variables
     *
     * @param publisher variable used to create the filter
     * @param genre     variable used to create the filter
     * @param year      variable used to create the filter
     * @return return a books as the specified parameters
     */
    @ApiOperation(value = "Method to search a book by (publisher,genre and year)", response = Book.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Book found successfully"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    @GetMapping("/findby")
    public ResponseEntity<Page<Book>> findByPublisherGenreYear(
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String year,
            Pageable pageable) {
        Page<Book> bookList = bookRepository.findAllByPublisherAndGenreAndYearQuery(publisher, genre, year, pageable);
        return new ResponseEntity<>(bookList, HttpStatus.OK);
    }

    /**
     * Method to search a book by all parameters
     *
     * @param genre     parameter to search by genre
     * @param author    parameter to search by author
     * @param image     parameter to search by image
     * @param title     parameter to search by title
     * @param subtitle  parameter to search by subtitle
     * @param publisher parameter to search by publisher
     * @param startYear parameter to filter by year (initial)
     * @param endYear   parameter to filter by year  (final)
     * @param pages     parameter to search by pages
     * @param isbn      parameter to search by isbn
     * @return book depending on the parameters
     */
    @ApiOperation(value = "Method to search a book by all parameters", response = Book.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Book found successfully"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    @GetMapping
    public ResponseEntity<Page<Book>> findByParameters(
            @RequestParam(required = false, defaultValue = "") String genre,
            @RequestParam(required = false, defaultValue = "") String author,
            @RequestParam(required = false, defaultValue = "") String image,
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") String subtitle,
            @RequestParam(required = false, defaultValue = "") String publisher,
            @RequestParam(required = false, defaultValue = "") String startYear,
            @RequestParam(required = false, defaultValue = "") String endYear,
            @RequestParam(required = false, defaultValue = "") String pages,
            @RequestParam(required = false, defaultValue = "") String isbn,
            Pageable pageable) {
        Page<Book> books = bookRepository
                .findByAllParameters(genre, author, image, title, subtitle, publisher, startYear, endYear, pages, isbn, pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}
