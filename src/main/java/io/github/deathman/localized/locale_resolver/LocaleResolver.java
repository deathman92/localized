package io.github.deathman.localized.locale_resolver;

import io.github.deathman.localized.LocalizedIntegrator;
import org.hibernate.Session;
import io.github.deathman.localized.exception.UnresolvedLocaleException;

import java.util.Locale;

/**
 * Resolves the locale based on the {@link Session}.
 *
 * Register a resolver at {@link LocalizedIntegrator#setLocaleResolver(LocaleResolver)}
 * or by Hibernate's property {@link LocalizedIntegrator#LOCALE_RESOLVER}.
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
public interface LocaleResolver {

    Locale resolveLocale(Session session) throws UnresolvedLocaleException;
}
