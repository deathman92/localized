package io.github.deathman.localized.cases;

import io.github.deathman.localized.rule.SessionRule;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import io.github.deathman.localized.locale_resolver.ThreadLocalLocaleResolver;
import io.github.deathman.localized.model.Book;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class TestLoad {

    private Session session;
    private ThreadLocalLocaleResolver localeResolver;

    @Rule
    public final SessionRule sessionRule = new SessionRule();

    @Before
    public void session() {
        session = sessionRule.getSession();
    }

    @Before
    public void localizedConfiguration() {
        localeResolver = sessionRule.getLocaleResolver();
    }

    @Test
    public void testNull() throws Exception {
        localeResolver.setLocale(Locale.US);

        Book book = Book.builder()
                .author("Author")
                .build();
        session.save(book);
        session.flush();

        session.refresh(book);
        assertThat(book.getTitle()).isNull();

        localeResolver.setLocale(Locale.GERMAN);
        session.refresh(book);
        assertThat(book.getTitle()).isNull();
    }

    @Test
    public void testDefaultTranslation() throws Exception {
        localeResolver.setLocale(Locale.US);

        Book book = Book.builder()
                .author("Author")
                .title("Title")
                .build();
        session.save(book);
        session.flush();

        session.refresh(book);
        assertThat(book.getTitle()).isEqualTo("Title");

        localeResolver.setLocale(Locale.GERMAN);
        session.refresh(book);
        assertThat(book.getTitle()).isEqualTo("Title");

        localeResolver.setLocale(Locale.US);
        session.refresh(book);
        assertThat(book.getTitle()).isEqualTo("Title");
    }

    @Test
    public void testTranslated() throws Exception {
        localeResolver.setLocale(Locale.US);

        Book book = Book.builder()
                .author("Author")
                .title("Title")
                .build();
        session.save(book);
        session.flush();

        localeResolver.setLocale(Locale.GERMAN);
        book.setTitle("Titel");
        session.save(book);
        session.flush();

        session.refresh(book);
        assertThat(book.getTitle()).isEqualTo("Titel");

        localeResolver.setLocale(Locale.US);
        session.refresh(book);
        assertThat(book.getTitle()).isEqualTo("Title");

        localeResolver.setLocale(Locale.GERMAN);
        session.refresh(book);
        assertThat(book.getTitle()).isEqualTo("Titel");
    }

    @Test
    public void testFallBackToLanguage() throws Exception {
        localeResolver.setLocale(Locale.ENGLISH);

        Book book = Book.builder()
                .author("Author")
                .title("Title")
                .build();
        session.save(book);
        session.flush();

        localeResolver.setLocale(Locale.US);
        session.refresh(book);
        assertThat(book.getTitle()).isEqualTo("Title");
    }
}
