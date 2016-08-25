package io.github.deathman.localized.cases;

import io.github.deathman.localized.LocalizedUtil;
import io.github.deathman.localized.localeResolver.ThreadLocalLocaleResolver;
import io.github.deathman.localized.model.Book;
import io.github.deathman.localized.rule.SessionRule;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class TestUtil {

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
    public void testDeleteLocale() throws Exception {
        localeResolver.setLocale(Locale.ENGLISH);
        Book book = Book.builder()
                .author("Author")
                .build();
        session.save(book);
        session.flush();

        LocalizedUtil.deleteLocale(session, book, Locale.GERMAN, SessionRule.TABLE_NAME);
        Collection<Locale> locales = LocalizedUtil.getLocales(session, book, SessionRule.TABLE_NAME);
        assertThat(locales).contains(Locale.ENGLISH);

        localeResolver.setLocale(Locale.GERMAN);
        book.setTitle("Titel");
        session.save(book);
        session.flush();

        LocalizedUtil.deleteLocale(session, book, Locale.GERMAN, SessionRule.TABLE_NAME);
        locales = LocalizedUtil.getLocales(session, book, SessionRule.TABLE_NAME);
        assertThat(locales).contains(Locale.ENGLISH);

        LocalizedUtil.deleteLocale(session, book, Locale.ENGLISH, SessionRule.TABLE_NAME);
        locales = LocalizedUtil.getLocales(session, book, SessionRule.TABLE_NAME);
        assertThat(locales).isEmpty();
    }

    @Test
    public void testGetLocales() throws Exception {
        localeResolver.setLocale(Locale.ENGLISH);
        Book book = Book.builder()
                .author("Author")
                .build();
        session.save(book);
        session.flush();
        session.refresh(book);

        Collection<Locale> locales = LocalizedUtil.getLocales(session, book, SessionRule.TABLE_NAME);
        assertThat(locales).contains(Locale.ENGLISH);

        book.setTitle("Title");
        session.save(book);
        session.flush();

        locales = LocalizedUtil.getLocales(session, book, SessionRule.TABLE_NAME);
        assertThat(locales).contains(Locale.ENGLISH);

        localeResolver.setLocale(Locale.GERMAN);
        book.setTitle("Titel");
        session.save(book);
        session.flush();

        locales = LocalizedUtil.getLocales(session, book, SessionRule.TABLE_NAME);
        assertThat(locales).contains(Locale.ENGLISH, Locale.GERMAN);
    }
}
