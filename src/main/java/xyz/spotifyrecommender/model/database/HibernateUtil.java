package xyz.spotifyrecommender.model.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

  private static SessionFactory sessionFactory;

  private HibernateUtil() {
  }

  static {
    Configuration configuration = new Configuration().configure();
    configuration.addAnnotatedClass(User.class);

    StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
        .applySettings(configuration.getProperties());
    sessionFactory = configuration.buildSessionFactory(builder.build());
  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }
}