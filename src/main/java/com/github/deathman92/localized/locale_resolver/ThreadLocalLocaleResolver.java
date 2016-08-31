package com.github.deathman92.localized.locale_resolver;

import com.github.deathman92.localized.exception.UnresolvedLocaleException;
import org.hibernate.Session;

import java.util.Locale;

/**
 * Resolves the locale from the current thread.
 *
 * Before calling {@link #resolveLocale(Session)}, the thread must have
 * set the {@link Locale} by calling {@link #setLocale(Locale)}.
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class ThreadLocalLocaleResolver implements LocaleResolver {

    private ThreadLocal<Locale> locales = new ThreadLocal<>();

    public void setLocale(Locale locale) {
        locales.set(locale);
    }

    @Override
    public Locale resolveLocale(Session session) throws UnresolvedLocaleException {
        Locale locale = locales.get();
        if (locale == null) {
            throw new UnresolvedLocaleException("he current thread didn't call setLocale() before.");
        }
        return locale;
    }
}
