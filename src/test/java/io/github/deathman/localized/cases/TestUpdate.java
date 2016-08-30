package io.github.deathman.localized.cases;

import io.github.deathman.localized.locale_resolver.ThreadLocalLocaleResolver;
import io.github.deathman.localized.rule.SessionRule;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import io.github.deathman.localized.model.Book;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Victor Zhivotikov
 * @since 0.1
 */
@RunWith(Parameterized.class)
public class TestUpdate {

    private Session session;
    private ThreadLocalLocaleResolver localeResolver;
    private Book book;
    private Locale locale;

    @Rule
    public final SessionRule sessionRule = new SessionRule();

    public TestUpdate(Locale locale) {
        this.locale = locale;
    }

    @Parameterized.Parameters
    public static Collection<Locale[]> locales() {
        Locale[][] locales = new Locale[][] {{Locale.ENGLISH}, {Locale.GERMAN}};
        return Arrays.asList(locales);
    }

    @Before
    public void before() {
        session = sessionRule.getSession();
        localeResolver = sessionRule.getLocaleResolver();
        localeResolver.setLocale(Locale.ENGLISH);

        book = Book.builder()
                .author("Author")
                .title("Title")
                .build();
        session.save(book);
        session.flush();

        localeResolver.setLocale(locale);
    }

    @Test
    public void testUpdate() throws Exception {
        book.setTitle("Title 1");
        session.save(book);
        session.flush();

        session.refresh(book);
        assertThat(book.getTitle()).isEqualTo("Title 1");
    }

    @Test
    public void testSetNull() throws Exception {
        book.setTitle(null);
        session.save(book);
        session.flush();

        assertThat(book.getTitle()).isNull();
    }
}
