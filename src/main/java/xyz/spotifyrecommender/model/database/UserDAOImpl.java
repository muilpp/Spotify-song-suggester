package xyz.spotifyrecommender.model.database;

import static xyz.spotifyrecommender.model.Constant.DEFAULT_ACCESS_REVOKED;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO {
	private final static Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

	@Override
	public boolean addUser(String userName, String accessToken, String refreshToken) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		Integer userID = 0;
		try {
			tx = session.beginTransaction();
			User user = new User(userName, accessToken, refreshToken, DEFAULT_ACCESS_REVOKED);
			userID = (Integer) session.save(user);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			session.close();
		}
		return userID > 0;
	}

	@Override
	public boolean userExists(String userName) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Query userQuery = session.createQuery("from User where user_name = :userName");
		userQuery.setParameter("userName", userName);

		User user = (User) userQuery.uniqueResult();

		return user != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsers() {
		Session session = HibernateUtil.getSessionFactory().openSession();

		return (List<User>) session.createQuery("from User where access_revoked != '1'").list();
	}

	@Override
	public boolean updateUserAccessToken(String userName, String oldToken, String newToken) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.getTransaction().begin();

		Query query = session.createQuery("update User set access_token = :newToken where access_token = :oldToken");
		query.setParameter("oldToken", oldToken);
		query.setParameter("newToken", newToken);

		int rowCount = query.executeUpdate();
		LOGGER.info("Updated -> " + rowCount);
		session.getTransaction().commit();

		return rowCount > 0;
	}

	@Override
	public boolean updateUserAccess(String userName, String accesRevoked) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.getTransaction().begin();

		Query query = session.createQuery("update User set access_revoked = :accesRevoked where user_name = :userName");
		query.setParameter("accesRevoked", accesRevoked);
		query.setParameter("userName", userName);

		int rowCount = query.executeUpdate();
		LOGGER.info("Updated -> " + rowCount);
		session.getTransaction().commit();

		return rowCount > 0;
	}

	@Override
	public boolean deleteUser(String userName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.getTransaction().begin();

		Query query = session.createQuery("delete User where user_name = :userName");
		query.setParameter("userName", userName);
		int rowCount = query.executeUpdate();

		LOGGER.info("Deleted -> " + rowCount);
		session.getTransaction().commit();

		return rowCount > 0;
	}
}