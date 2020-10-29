package wolox.training.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exception.BookNotFoundException;
import wolox.training.exception.UsersNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.Users;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UsersRepository;

/**
 * Users controller containing the operations of update , find , delete , find by id and create
 *
 * @author luismiguelrodriguez
 */
@RestController
@RequestMapping("/api/users")
@Api
public class UsersController {

    /**
     * Repository of Users
     */
    @Autowired
    private UsersRepository usersRepository;

    /**
     * Repository of Books
     */
    @Autowired
    private BookRepository bookRepository;

    /**
     * Method for find all elements
     *
     * @return returns all elements in BD
     */
    @ApiOperation(value = "Method to find all users", response = Users.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfuly retrieved users"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Iterable findAll() {
        return usersRepository.findAll();
    }

    /**
     * Method for search elements
     *
     * @param id variable used to identify the element to search
     * @return method that returns an object according to the id parameter
     */
    @ApiOperation(value = "Method to find a user", response = Users.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfuly retrieved user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Users findById(@PathVariable Long id) {
        return usersRepository.findById(id).orElseThrow(UsersNotFoundException::new);
    }

    /**
     * Method for create elements
     *
     * @param user Object required to save a user
     * @return return a view of the saved object
     */
    @ApiOperation(value = "Method to create a user", response = Users.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfuly created user")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Users create(@RequestBody Users user) {
        return usersRepository.save(user);
    }

    /**
     * Method for update element
     *
     * @param user Object required to update a user
     * @param id   variable used to identify the element to update
     * @return return a view of the updated object
     */
    @ApiOperation(value = "Method to update a user", response = Users.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfuly updated user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Users update(@RequestBody Users user, @PathVariable Long id) {
        usersRepository.findById(id).orElseThrow(UsersNotFoundException::new);
        return usersRepository.save(user);
    }

    /**
     * Method for delete element
     *
     * @param id variable used to identify the element to delete
     */
    @ApiOperation(value = "Method to delete a user", response = Users.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfuly deleted user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        usersRepository.findById(id).orElseThrow(UsersNotFoundException::new);
        usersRepository.deleteById(id);
    }

    /**
     * Method to add a book to a user
     *
     * @param id      User identifier to add a book
     * @param book_id Book identifier to add
     */
    @ApiOperation(value = "Method to add a book to a user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Book Add"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    @PatchMapping("/{id}/add_books/{book_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBook(@PathVariable Long id, @PathVariable Long book_id) {
        Users user = usersRepository.findById(id).orElseThrow(UsersNotFoundException::new);

        if (bookRepository.existsById(book_id)) {
            Book book = bookRepository.getOne(book_id);
            user.addBook(book);
            usersRepository.save(user);
        } else {
            throw new BookNotFoundException();
        }
    }

    /**
     * Method to delete a book to a user
     *
     * @param id      User identifier to delete a book
     * @param book_id Book identifier to delete
     */
    @ApiOperation(value = "Method to delete a book to a user")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Book deleted"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    @PatchMapping("/{id}/remove_books/{book_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBook(@PathVariable Long id, @PathVariable Long book_id) {
        Users user = usersRepository.findById(id).orElseThrow(UsersNotFoundException::new);

        if (bookRepository.existsById(book_id)) {
            Book book = bookRepository.getOne(book_id);
            user.removeBook(book);
            usersRepository.save(user);
        } else {
            throw new BookNotFoundException();
        }
    }

}
