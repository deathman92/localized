package io.github.deathman.localized.rule;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import io.github.deathman.localized.LocalizedIntegrator;
import io.github.deathman.localized.LocalizedProperty;
import io.github.deathman.localized.localeResolver.ThreadLocalLocaleResolver;
import io.github.deathman.localized.model.Book;
import io.github.deathman.localized.repository.LocalizedSessionRepository;

/**
 * @author Victor Zhivotikov
 * @since 0.1
 */
public class SessionRule implements MethodRule {

    public static final String TABLE_NAME = "Book";

    @Getter
    private Session session;
    @Getter
    private SessionFactory sessionFactory;
    @Getter
    private LocalizedSessionRepository repository;
    @Getter
    private ThreadLocalLocaleResolver localeResolver;
    private boolean initResolver;

    public SessionRule() {
        this(true);
    }

    public SessionRule(boolean initResolver) {
        this.initResolver = initResolver;
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                sessionFactory = createSessionFactory();
                session = sessionFactory.openSession();
                session.beginTransaction();
                try {
                    repository = new LocalizedSessionRepository(session);
                    localeResolver = new ThreadLocalLocaleResolver();
                    if (initResolver) {
                        LocalizedIntegrator.setLocaleResolver(localeResolver);
                    }
                    base.evaluate();
                } finally {
                    session.close();
                    sessionFactory.close();
                }
            }
        };
    }

    private SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure();
        configuration.addAnnotatedClass(Book.class);
        configuration.addAnnotatedClass(LocalizedProperty.class);
        return configuration.buildSessionFactory();
    }
}
