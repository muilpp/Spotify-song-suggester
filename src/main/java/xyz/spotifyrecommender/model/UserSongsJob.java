package xyz.spotifyrecommender.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import xyz.spotifyrecommender.model.database.User;
import xyz.spotifyrecommender.model.database.UserDAO;
import xyz.spotifyrecommender.model.webservice_data.Token;

public class UserSongsJob {
    private final static Logger LOGGER = Logger.getLogger(UserSongsJob.class.getName());

    @Autowired
    SpotifyAPI spotifyAPI;

    @Autowired
    UserDAO userDAO;

    @Autowired
    Suggest suggest;

    @Scheduled(cron="0 0 0 * * MON")
    public void execute() {
        List<User> userList = userDAO.getUsers();
        LOGGER.info("execute job, user list size -> " + userList.size());

        for (User user : userList) {
            LOGGER.info("update user -> " + user.getUserName());
            Token userToken = spotifyAPI.refreshToken(user.getRefreshToken());
            suggest.getRecommendations(userToken);

            try {
                // wait 10s between each user
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}