package xyz.spotifyrecommender.model.database;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDAO {

  /**
   * Method to CREATE a new user in the database
   *
   * @return true if user persisted, false otherwise
   */
  boolean addUser(String userName, String accessToken, String refreshToken);

  /**
   * Method to GET a user from the database
   *
   * @return true if the user exists, false otherwise
   */
  boolean userExists(String userName);

  /**
   * Method to UPDATE user token
   *
   * @return true if user updated, false otherwise
   */
  boolean updateUserAccessToken(String userName, String oldToken, String newToken);

  /**
   * Method to UPDATE the user status to revoked
   *
   * @return true if user updated, false otherwise
   */
  boolean updateUserAccess(String userName, String accesRevoked, String newAccessToken,
      String newRefreshToken);

  /**
   * Method to UPDATE the last time the user was updated
   *
   * @return true if user updated, false otherwise
   */
  boolean updateUserLastRefresh(String userName, LocalDateTime lastRefreshTime);

  /**
   * Method to DELETE an employee from the records
   *
   * @return true if user is deleted, false otherwise
   */
  boolean deleteUser(String userName);

  /**
   * Method to READ all users
   *
   * @return list of users in database
   */
  List<User> getUsers();

  /**
   * Method to READ a specific users
   *
   * @return list of users in database matching the userName parameter
   */
  User getUser(String userName);
}