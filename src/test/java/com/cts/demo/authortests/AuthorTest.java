package com.cts.demo.authortests;

import com.cts.demo.dao.AuthorDao;
import com.cts.demo.exception.AuthorNotFoundException;
import com.cts.demo.model.Author;
import com.cts.demo.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthorServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AuthorTest {

    @Mock
    private AuthorDao authorDao;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;

    /**
     * Sets up a sample author before each test.
     */
    @BeforeEach
    void setUp() {
        author = new Author(1, "John");
    }

    /* ===================== addAuthor ===================== */

    /**
     * Tests that addAuthor returns the author when found after insert.
     */
    @Test
    void addAuthor_shouldReturnAuthor_whenAuthorExistsAfterInsert() {

        when(authorDao.addAuthor("John")).thenReturn(1);
        when(authorDao.findByAuthorName("John"))
                .thenReturn(Optional.of(author));

        Author result = authorService.addAuthor("John");

        assertNotNull(result);
        assertEquals("John", result.getAuthorName());
        verify(authorDao).addAuthor("John");
        verify(authorDao).findByAuthorName("John");
    }

    /**
     * Tests that addAuthor throws AuthorNotFoundException when author not found after insert.
     */
    @Test
    void addAuthor_shouldThrowException_whenAuthorNotFoundAfterInsert() {

        when(authorDao.addAuthor("Unknown")).thenReturn(1);
        when(authorDao.findByAuthorName("Unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                AuthorNotFoundException.class,
                () -> authorService.addAuthor("Unknown")
        );

        verify(authorDao).addAuthor("Unknown");
    }

    /* ===================== findByAuthorId ===================== */

    /**
     * Tests that findByAuthorId returns the author when ID exists.
     */
    @Test
    void findByAuthorId_shouldReturnAuthor_whenExists() {

        when(authorDao.findByAuthorId(1))
                .thenReturn(Optional.of(author));

        Optional<Author> result = authorService.findByAuthorId(1);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getAuthorName());
    }

    /**
     * Tests that findByAuthorId throws AuthorNotFoundException when ID not found.
     */
    @Test
    void findByAuthorId_shouldThrowException_whenNotFound() {

        when(authorDao.findByAuthorId(99))
                .thenReturn(Optional.empty());

        assertThrows(
                AuthorNotFoundException.class,
                () -> authorService.findByAuthorId(99)
        );
    }

    /* ===================== findByAuthorName ===================== */

    /**
     * Tests that findByAuthorName returns the author when name exists.
     */
    @Test
    void findByAuthorName_shouldReturnAuthor_whenExists() {

        when(authorDao.findByAuthorName("John"))
                .thenReturn(Optional.of(author));

        Optional<Author> result = authorService.findByAuthorName("John");

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getAuthorName());
    }

    /**
     * Tests that findByAuthorName throws AuthorNotFoundException when name not found.
     */
    @Test
    void findByAuthorName_shouldThrowException_whenNotFound() {

        when(authorDao.findByAuthorName("Sam"))
                .thenReturn(Optional.empty());

        assertThrows(
                AuthorNotFoundException.class,
                () -> authorService.findByAuthorName("Sam")
        );
    }

    /* ===================== findAll ===================== */

    /**
     * Tests that findAll returns list of authors when authors exist.
     */
    @Test
    void findAll_shouldReturnAuthorList_whenAuthorsExist() {

        when(authorDao.findAll())
                .thenReturn(List.of(author));

        List<Author> result = authorService.findAll();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getAuthorName());
    }

    /**
     * Tests that findAll throws AuthorNotFoundException when no authors exist.
     */
    @Test
    void findAll_shouldThrowException_whenNoAuthorsExist() {

        when(authorDao.findAll())
                .thenReturn(List.of());

        assertThrows(
                AuthorNotFoundException.class,
                () -> authorService.findAll()
        );
    }
}