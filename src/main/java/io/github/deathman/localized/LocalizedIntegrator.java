package io.github.deathman.localized;

import io.github.deathman.localized.event.DeleteEventListener;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.beanvalidation.DuplicationStrategyImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.internal.util.config.ConfigurationException;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import io.github.deathman.localized.event.ReadEventListener;
import io.github.deathman.localized.event.WriteEventListener;
import io.github.deathman.localized.locale_resolver.DefaultLocaleResolver;
import io.github.deathman.localized.locale_resolver.LocaleResolver;

import java.util.ServiceLoader;

/**
 * Automatic integration into Hibernate.
 *
 * This is the entry point into Hibernate. Hibernate discovers this
 * service with {@link ServiceLoader}.
 *
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class LocalizedIntegrator implements Integrator {

    /**
     * The name of a configuration setting that registers a locale resolver.
     * Default is io.deathman.github.localized.locale_resolver.DefaultLocaleResolver
     */
    public static final String LOCALE_RESOLVER = "hibernate.listeners.localized.locale_resolver";

    private static LocaleResolver localeResolver;

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        String localeResolverClassName = ConfigurationHelper.getString(LOCALE_RESOLVER, sessionFactory.getProperties());
        if (localeResolverClassName != null) {
            try {
                setLocaleResolver((LocaleResolver) Class.forName(localeResolverClassName).newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new ConfigurationException(String.format(
                        "could not instantiate LocaleResolver %s from hibernate option %s",
                        localeResolverClassName, LOCALE_RESOLVER), e
                );
            }
        } else {
            DefaultLocaleResolver localeResolver = new DefaultLocaleResolver();
            localeResolver.setWarnOnce(true);
            setLocaleResolver(localeResolver);
        }

        final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        eventListenerRegistry.addDuplicationStrategy(DuplicationStrategyImpl.INSTANCE);

        eventListenerRegistry.appendListeners(EventType.POST_LOAD, new ReadEventListener(this, sessionFactory));
        eventListenerRegistry.appendListeners(EventType.POST_UPDATE, new WriteEventListener(this, sessionFactory));
        eventListenerRegistry.appendListeners(EventType.POST_INSERT, new WriteEventListener(this, sessionFactory));
        eventListenerRegistry.appendListeners(EventType.POST_DELETE, new DeleteEventListener(this, sessionFactory));
    }

    /**
     * Registers a {@link LocaleResolver}.
     *
     * You can also configure this by the hibernate property {@value #LOCALE_RESOLVER}.
     */
    public static void setLocaleResolver(LocaleResolver localeResolver) {
        LocalizedIntegrator.localeResolver = localeResolver;
    }

    public LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    }
}
