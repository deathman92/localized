package com.github.deathman92.localized.locale_resolver;

import com.github.deathman92.localized.LocalizedIntegrator;
import com.github.deathman92.localized.exception.UnresolvedLocaleException;
import org.hibernate.Session;

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
