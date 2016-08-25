package io.github.deathman.localized.event;

import io.github.deathman.localized.Localized;
import io.github.deathman.localized.LocalizedIntegrator;
import io.github.deathman.localized.exception.LocalizedException;
import org.hibernate.StatelessSession;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import io.github.deathman.localized.LocalizedProperty;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Persists the entity's @{@link Localized} fields.
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class WriteEventListener extends AbstractEventListener implements
        PostUpdateEventListener, PostInsertEventListener{
    private static final long serialVersionUID = 2112739723515106510L;

    public WriteEventListener(LocalizedIntegrator integrator, SessionFactoryImplementor sessionFactory) {
        super(integrator, sessionFactory);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        handleFields(event, event.getEntity(), event.getId());
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        handleFields(event, event.getEntity(), event.getId());
    }

    @Override
    protected void handleField(StatelessSession session, Field field, Object entity, LocalizedProperty property) throws LocalizedException {
        try {
            property.setValue(field.get(entity) != null ? String.valueOf(field.get(entity)) : null);
            if (property.getId() == null) {
                session.insert(property);
            } else {
                session.update(property);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new LocalizedException(e);
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }
}
