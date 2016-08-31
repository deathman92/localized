package io.github.deathman.localized.exception;

import io.github.deathman.localized.locale_resolver.LocaleResolver;
import lombok.NoArgsConstructor;

/**
 * Exception during locale resolution.
 *
 * @see LocaleResolver#resolveLocale(org.hibernate.Session)
 * @author Victor Zhivotikov
 * @since 0.1
 */
@NoArgsConstructor
public class UnresolvedLocaleException extends LocalizedException {
    private static final long serialVersionUID = 7436361288749125603L;

    public UnresolvedLocaleException(String message) {
        super(message);
    }

    public UnresolvedLocaleException(Throwable cause) {
        super(cause);
    }

    public UnresolvedLocaleException(String message, Throwable cause) {
        super(message, cause);
    }
}
