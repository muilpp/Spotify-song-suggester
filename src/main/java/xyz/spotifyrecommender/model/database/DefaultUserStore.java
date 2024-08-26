package xyz.spotifyrecommender.model.database;

import static xyz.spotifyrecommender.model.Constant.DEFAULT_ACCESS_REVOKED;
import static xyz.spotifyrecommender.model.Constant.DEFAULT_AVOID_SPANISH_MUSIC;
import static xyz.spotifyrecommender.model.Constant.DEFAULT_SHORT_TERM_TRACKS;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultUserStore implements UserStore {

  private static final Logger LOGGER = Logger.getLogger(UserStore.class.getName());
  private static final String USER_NAME_PLACEHOLDER = "userName";

  @Override
  public boolean addUser(String userName, String accessToken, String refreshToken) {

    Transaction tx = null;
    Integer userID = 0;

    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      tx = session.beginTransaction();
      User user = new User(userName, accessToken, refreshToken, DEFAULT_ACCESS_REVOKED,
          DEFAULT_AVOID_SPANISH_MUSIC, DEFAULT_SHORT_TERM_TRACKS, LocalDateTime.now());
      userID = (Integer) session.save(user);
      tx.commit();
    } catch (HibernateException e) {
      if (tx != null) {
        tx.rollback();
      }
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }

    return userID > 0;
  }

  @Override
  public boolean userExists(String userName) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Query userQuery = session.createQuery("from User where user_name = :userName");
      userQuery.setParameter(USER_NAME_PLACEHOLDER, userName);
      User user = (User) userQuery.uniqueResult();

      return user != null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<User> getUsers() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      return (List<User>) session.createQuery("from User where access_revoked != '1'").list();
    }
  }

  @Override
  public User getUser(String userName) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {

      Query userQuery = session.createQuery(
          "from User where access_revoked != '1' and user_name = :userName");
      userQuery.setParameter(USER_NAME_PLACEHOLDER, userName);

      return (User) userQuery.uniqueResult();
    }
  }

  @Override
  public boolean updateUserAccessToken(String userName, String oldToken, String newToken) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      session.getTransaction().begin();

      Query query = session
          .createQuery("update User set access_token = :newToken where access_token = :oldToken");
      query.setParameter("oldToken", oldToken);
      query.setParameter("newToken", newToken);

      int rowCount = query.executeUpdate();
      LOGGER.log(Level.INFO, "Updated -> [{0}]", rowCount);
      session.getTransaction().commit();

      return rowCount > 0;
    }
  }

  @Override
  public boolean updateUserAccess(String userName, String accesRevoked, String newAccessToken,
      String newRefreshToken) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      session.getTransaction().begin();

      Query query;
      LocalDateTime currentTime = LocalDateTime.now();

      if (!accesRevoked.equalsIgnoreCase("1")) {
        query = session.createQuery(
            "update User set access_revoked = :accesRevoked, access_token = :newAccessToken, refresh_token = :newRefreshToken, updated_at = :currentTime  where user_name = :userName");
        query.setParameter("newAccessToken", newAccessToken);
        query.setParameter("newRefreshToken", newRefreshToken);
      } else {
        // do not update the credentials if a user revokes the access
        query = session
            .createQuery(
                "update User set access_revoked = :accesRevoked, updated_at = :currentTime where user_name = :userName");

      }

      query.setParameter("currentTime", currentTime);
      query.setParameter("accesRevoked", accesRevoked);
      query.setParameter(USER_NAME_PLACEHOLDER, userName);

      int rowCount = query.executeUpdate();
      LOGGER.log(Level.INFO, "Updated -> [{0}]", rowCount);
      session.getTransaction().commit();

      return rowCount > 0;
    }
  }

  @Override
  public boolean updateUserLastRefresh(String userName, LocalDateTime lastRefreshTime) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      session.getTransaction().begin();

      Query query;

      query = session.createQuery(
          "update User set updated_at = :lastRefreshTime where user_name = :userName");

      query.setParameter("lastRefreshTime", lastRefreshTime);
      query.setParameter(USER_NAME_PLACEHOLDER, userName);

      int rowCount = query.executeUpdate();
      LOGGER.log(Level.INFO, "Updated -> [{0}]", rowCount);
      session.getTransaction().commit();

      return rowCount > 0;
    }
  }

  @Override
  public boolean deleteUser(String userName) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      session.getTransaction().begin();

      Query query = session.createQuery("delete User where user_name = :userName");
      query.setParameter(USER_NAME_PLACEHOLDER, userName);
      int rowCount = query.executeUpdate();

      LOGGER.log(Level.INFO, "Deleted -> [{0}]", rowCount);
      session.getTransaction().commit();

      return rowCount > 0;
    }
  }
}