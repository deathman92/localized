package io.github.deathman.localized.locale_resolver;

import org.hibernate.Session;
import org.springframework.context.i18n.LocaleContextHolder;
import io.github.deathman.localized.exception.UnresolvedLocaleException;

import java.util.Locale;

/**
 * Resolve locale for a Spring/JPA environment.
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class SpringLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(Session session) throws UnresolvedLocaleException {
        return LocaleContextHolder.getLocale();
    }
}
