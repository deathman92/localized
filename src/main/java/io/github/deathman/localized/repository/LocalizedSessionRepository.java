package io.github.deathman.localized.repository;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Repository backed by a {@link Session}
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class LocalizedSessionRepository extends LocalizedRepository<Session> {

    public LocalizedSessionRepository(Session session) {
        super(session);
    }

    public Set<Locale> getLocales(Object entity, String table) {
        Query<Locale> query = session
                .createQuery("SELECT DISTINCT locale FROM LocalizedProperty" +
                        " WHERE instance = :instance AND tableName = :tableName", Locale.class);
        query.setParameter("instance", session.getIdentifier(entity).toString());
        query.setParameter("tableName", table);

        List<Locale> locales = query.list();
        return new HashSet<>(locales);
    }

    public void deleteLocale(Object entity, Locale locale, String table) {
        Query query = session
                .createQuery("DELETE FROM LocalizedProperty" +
                        " WHERE instance = :instance" +
                        " AND tableName = :tableName" +
                        " AND locale = :locale");
        query.setParameter("instance", session.getIdentifier(entity).toString());
        query.setParameter("tableName", table);
        query.setParameter("locale", locale);
        query.executeUpdate();
    }
}
