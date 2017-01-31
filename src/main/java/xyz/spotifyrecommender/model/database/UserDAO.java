package xyz.spotifyrecommender.model.database;

import java.util.List;

public interface UserDAO {

    /**
     * Method to CREATE a new user in the database
     * 
     * @param userName
     * @param accessToken
     * @param refreshToken
     * @return true if user persisted, false otherwise
     */
    public boolean addUser(String userName, String accessToken, String refreshToken);

    /**
     * Method to GET a user from the database
     * 
     * @param userName
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(String userName);

    /**
     * Method to UPDATE user token
     * 
     * @param userName
     * @param oldToken
     * @param newToken
     * @return true if user updated, false otherwise
     */
    public boolean updateUserAccessToken(String userName, String oldToken, String newToken);

    /**
     * Method to UPDATE the user status to revoked
     * 
     * @param userName
     * @return true if user updated, false otherwise
     */
    public boolean updateUserAccess(String userName, String accesRevoked, String newAccessToken,
            String newRefreshToken);

    /**
     * Method to DELETE an employee from the records
     * 
     * @param userName
     * @return true if user is deleted, false otherwise
     */
    public boolean deleteUser(String userName);

    /**
     * Method to READ all users
     * 
     * @return list of users in database
     */
    public List<User> getUsers();

    /**
     * Method to READ a specific users
     * 
     * @return list of users in database matching the userName parameter
     */
    public User getUser(String userName);
}