package com.github.deathman92.localized;

import org.hibernate.Session;
import com.github.deathman92.localized.repository.LocalizedSessionRepository;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Helper to get meta information about entities with @{@link Localized} fields.
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class LocalizedUtil {

    /**
     * Returns the locales of an entity.
     *
     * Even if the translation consists only of NULL values it is
     * considered a translation.
     *
     * @see #deleteLocale(Session, Object, Locale, String)
     */
    public static Set<Locale> getLocales(Session session, Object entity, String table) {
        LocalizedSessionRepository repository = new LocalizedSessionRepository(session);
        return repository.getLocales(entity, table);
    }

    /**
     * Deletes a locale from an entity.
     */
    public static void deleteLocale(Session session, Object entity, Locale locale, String table) {
        LocalizedSessionRepository repository = new LocalizedSessionRepository(session);
        repository.deleteLocale(entity, locale, table);
    }

    /**
     * Returns the entity's @{@link Localized} fields.
     *
     * These fields are made accessible.
     */
    public static Collection<Field> getLocalizedFields(Class<?> clazz) {
        Collection<Field> fields = getAllDeclaredFields(clazz);
        List<Field> localizedFields = new ArrayList<>();
        fields.stream().filter(field -> field.getAnnotation(Localized.class) != null).forEach(field -> {
            field.setAccessible(true);
            localizedFields.add(field);
        });
        return localizedFields;
    }

    private static Collection<Field> getAllDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        if (clazz.getSuperclass() != null) {
            fields.addAll(getAllDeclaredFields(clazz.getSuperclass()));
        }
        return fields;
    }
}
