package io.github.deathman.localized.repository;

import io.github.deathman.localized.LocalizedProperty;
import lombok.AllArgsConstructor;
import org.hibernate.SharedSessionContract;
import org.hibernate.query.Query;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Abstract Repository.
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
@AllArgsConstructor
public abstract class LocalizedRepository<T extends SharedSessionContract> {

    protected T session;

    public LocalizedProperty find(String table, String field, Locale locale, Serializable id) {
        String queryString = "FROM LocalizedProperty WHERE instance = :instance" +
                " AND field = :field" +
                " AND tableName = :tableName";

        Locale baseLocale = getBaseLocale(locale);
        if (baseLocale != null) {
            queryString = queryString + " AND (locale = :locale OR locale = :baseLocale)";
        } else {
            queryString = queryString + " AND locale = :locale";
        }

        Query<LocalizedProperty> query = session.createQuery(queryString, LocalizedProperty.class);

        query.setParameter("instance", id.toString());
        query.setParameter("field", field);
        query.setParameter("tableName", table);
        query.setParameter("locale", locale);

        if (baseLocale != null) {
            query.setParameter("baseLocale", baseLocale);
        }

        List<LocalizedProperty> results = query.list();
        switch (results.size()) {
            case 0:
                return null;
            case 1:
                return results.get(0);
            case 2:
                for (LocalizedProperty property : results) {
                    if (property.getLocale().equals(locale)) {
                        return property;
                    }
                }
            default:
                throw new IllegalStateException("This query should not return more than 1 result.");
        }
    }

    private Locale getBaseLocale(Locale locale) {
        if (!StringUtils.isEmpty(locale.getCountry())) {
            return Locale.forLanguageTag(locale.getLanguage());
        }
        return null;
    }
}
