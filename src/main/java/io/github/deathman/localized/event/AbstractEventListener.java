package io.github.deathman.localized.event;

import io.github.deathman.localized.Localized;
import io.github.deathman.localized.LocalizedIntegrator;
import io.github.deathman.localized.LocalizedUtil;
import io.github.deathman.localized.exception.LocalizedException;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.spi.AbstractEvent;
import io.github.deathman.localized.LocalizedProperty;
import io.github.deathman.localized.repository.LocalizedStatelessSessionRepository;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.internal.Helper;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.walking.internal.EntityIdentifierDefinitionHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Locale;

/**
 * Base class for event handling.
 *
 * This class dispatches the event to the @{@link Localized} fields of the
 * event's entity.
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
@AllArgsConstructor
abstract class AbstractEventListener implements Serializable {
    private static final long serialVersionUID = -3445063058420230091L;

    private LocalizedIntegrator integrator;

    protected SessionFactoryImplementor sessionFactory;

    /**
     * Handles the event for each @{@link Localized} field.
     *
     * This method has a separate {@link Session} as Hibernate's event system
     * seems to break when using the same Session. The session runs on the same
     * {@link Connection}, i.e. in the same transaction.
     */
    protected abstract void handleField(StatelessSession session, Field field, Object entity, LocalizedProperty property) throws LocalizedException;

    /**
     * Dispatches an event for each @{@link Localized} field.
     *
     * The Hibernate event callback should be forwarded to this method.
     *
     * @see #handleField(StatelessSession, Field, Object, LocalizedProperty)
     */
    <T extends AbstractEvent> void handleFields(T event, Object entity, Serializable id) {
        if (entity instanceof LocalizedProperty) {
            return;
        }
        try (StatelessSession session = sessionFactory.openStatelessSession(event.getSession().connection())) {
            Locale locale = integrator.getLocaleResolver().resolveLocale(event.getSession());
            LocalizedStatelessSessionRepository repository = new LocalizedStatelessSessionRepository(session);

            for (Field field : LocalizedUtil.getLocalizedFields(entity.getClass())) {
                LocalizedProperty property = repository.find(getTableName(entity), field.getName(), locale, id);

                if (property == null) {
                    property = LocalizedProperty.builder()
                            .field(field.getName())
                            .locale(locale)
                            .tableName(getTableName(entity))
                            .instance(id.toString())
                            .build();
                }
                handleField(session, field, entity, property);
            }
        } catch (LocalizedException e) {
            throw new IllegalStateException();
        }
    }

    private String getTableName(Object entity) throws LocalizedException {
        ClassMetadata metadata = (ClassMetadata) sessionFactory.getMetamodel().entityPersister(entity.getClass());
        if (metadata != null && metadata instanceof AbstractEntityPersister) {
            AbstractEntityPersister persister = (AbstractEntityPersister) metadata;
            return persister.getTableName();
        }
        throw new LocalizedException("Entity does not exist in persistence context");
    }
}
