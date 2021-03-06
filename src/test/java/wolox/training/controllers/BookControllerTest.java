package wolox.training.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.models.Book;
import wolox.training.models.dto.BookDTO;
import wolox.training.repositories.BookRepository;
import wolox.training.security.CustomAuthenticationProvider;
import wolox.training.service.OpenLibraryService;
import wolox.training.util.TestEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRepository mockBookRepository;

    @MockBean
    private CustomAuthenticationProvider customAuthenticationProvider;

    @MockBean
    private OpenLibraryService openLibraryService;

    private static Book testBook;
    private static List<Book> testBooks;
    private static BookDTO testBookDTO;
    private static final String USER_PATH = "/api/books";

    @BeforeAll
    static void setUp() {
        testBooks = TestEntities.mockManyBooks();
        testBook = TestEntities.mockBook();
        testBookDTO = TestEntities.mockBookDTO();
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test find all book ,return status OK")
    void whenFindBookByIdThenReturnStatusOK() throws Exception {
        given(mockBookRepository.findById(1L)).willReturn(Optional.of(testBook));
        String url = (USER_PATH + "/1");
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test, When a book is searched for its id,it return status not found")
    void whenBookThatNotExistsThenReturnNotFound() throws Exception {
        given(mockBookRepository.findById(1L)).willReturn(Optional.empty());
        String url = (USER_PATH + "/1");
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test , When a book is created , it return status Created")
    void whenCreateBookThenReturnStatusCreated() throws Exception {
        String json = new ObjectMapper().writeValueAsString(testBook);
        String url = USER_PATH;
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test, When a book is updated , it return status OK")
    void whenUpdateBookThenReturnStatusCreated() throws Exception {
        given(mockBookRepository.findById(1L)).willReturn(Optional.of(testBook));
        String json = new ObjectMapper().writeValueAsString(testBook);
        String url = (USER_PATH + "/1");
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test, When a book is updated , it return status No Found")
    void whenUpdateBookThenReturnStatusNoFound() throws Exception {
        given(mockBookRepository.findById(1L)).willReturn(Optional.empty());
        String json = new ObjectMapper().writeValueAsString(testBook);
        String url = (USER_PATH + "/1");
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test, When a book is deleted , it return status No Content")
    void whenDeleteBookThenReturnStatusNoContent() throws Exception {
        given(mockBookRepository.findById(1L)).willReturn(Optional.of(testBook));
        String url = (USER_PATH + "/1");
        mvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test, When a book is deleted , it return status No Found")
    void whenDeleteBookThenReturnStatusNoFound() throws Exception {
        given(mockBookRepository.findById(1L)).willReturn(Optional.empty());
        String url = (USER_PATH + "/1");
        mvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test, When find a book by isbn , it retunr status OK")
    void whenFindBookByIsbnThenRetunrStatusOK() throws Exception {
        given(mockBookRepository.findByIsbn(anyString())).willReturn(Optional.of(testBook));
        String url = (USER_PATH + "/find-by-isbn?isbn=22");
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test, When find a book by isbn , it retunr status Created")
    void whenFindBookByIsbnThenRetunrStatusCreated() throws Exception {
        given(mockBookRepository.findByIsbn(anyString())).willReturn(Optional.empty());
        given(openLibraryService.findInfoBook(anyString())).willReturn((testBookDTO));
        String url = (USER_PATH + "/find-by-isbn?isbn=22");
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test , When a book is seached by publisher , genre and year ,it return status OK")
    void whenFindByPublisherGenreAndYearThenReturnStatusOK() throws Exception {
        Pageable pageable = PageRequest.of(1, 4);
        Page<Book> books = new PageImpl<>(testBooks);
        given(mockBookRepository.findAllByPublisherAndGenreAndYearQuery(testBook.getPublisher(), testBook.getGenre(), testBook.getYear(), pageable)).willReturn(books);
        String url = (USER_PATH + "/findby?publisher=publisher&genre=genre&year=22");
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "miguel")
    @Test
    @DisplayName("Test , When a book is seached by many parameters ,it return status OK")
    void whenFindByAllParametersThenReturnStatusOK() throws Exception {
        Pageable pageable = PageRequest.of(1, 4);
        List<Book> books = new ArrayList<>();
        books.add(testBook);
        Page<Book> bookPage = new PageImpl<>(books);
        given(mockBookRepository.findByAllParameters("genre", "author", "image", "title", "subtitle", "publisher", "startYear", "endYear", "pages", "22", pageable)).willReturn(bookPage);
        String url = (USER_PATH + "?genre=genre&author=author&image=image&title=title&subtitle=subtitle&publisher=publisher&startYear=10&endYear=2019&pages=22&isbn=22&page=1&size=4");
        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
