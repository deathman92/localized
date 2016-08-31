package com.github.deathman92.localized.event;

import com.github.deathman92.localized.Localized;
import com.github.deathman92.localized.LocalizedIntegrator;
import com.github.deathman92.localized.LocalizedProperty;
import com.github.deathman92.localized.exception.LocalizedException;
import org.hibernate.StatelessSession;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;

import java.lang.reflect.Field;

/**
 * Remove the entity's @{@link Localized} fields.
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class DeleteEventListener extends AbstractEventListener implements PostDeleteEventListener {
    private static final long serialVersionUID = -2802542537561146062L;

    public DeleteEventListener(LocalizedIntegrator integrator, SessionFactoryImplementor sessionFactory) {
        super(integrator, sessionFactory);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        handleFields(event, event.getEntity(), event.getId());
    }

    @Override
    protected void handleField(StatelessSession session, Field field, Object entity, LocalizedProperty property) throws LocalizedException {
        if (property.getId() == null) {
            return;
        }
        session.delete(property);
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }
}
