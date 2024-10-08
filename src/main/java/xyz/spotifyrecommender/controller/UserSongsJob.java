package xyz.spotifyrecommender.controller;

import com.google.common.base.Strings;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import xyz.spotifyrecommender.model.SpotifyAPI;
import xyz.spotifyrecommender.model.SuggestService;
import xyz.spotifyrecommender.model.database.User;
import xyz.spotifyrecommender.model.database.UserStore;
import xyz.spotifyrecommender.model.webservicedata.Token;

@RequiredArgsConstructor
public class UserSongsJob {

  private static final Logger LOGGER = Logger.getLogger(UserSongsJob.class.getName());

  private final SpotifyAPI spotifyAPI;
  private final UserStore userStore;
  private final SuggestService suggestService;

  @Scheduled(cron = "0 0 5 * * SUN")
  public void execute() {
    List<User> userList = userStore.getUsers();
    LOGGER.log(Level.INFO, "execute job, user list size -> [{0}]", userList.size());

    for (User user : userList) {
      LOGGER.log(Level.INFO, "automatic update for user -> [{0}]", user.getUserName());
      Token userToken = spotifyAPI.refreshToken(user.getUserName(), user.getRefreshToken());

			if (!Strings.isNullOrEmpty(userToken.getAccessToken())) {
				suggestService.getRecommendations(userToken, user.getAvoidSpanishMusic(),
						user.getShortTermTracks());
			}

      try {
        // wait 10s between each user
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        LOGGER.log(Level.SEVERE, e.getMessage(), e);
        Thread.currentThread().interrupt();
      }
    }
  }

  @Scheduled(cron = "0 0 */3 * * *")
  public void avoidConnectionDrop() {
    LOGGER.info("Execute sql select to avoid connection drop");
    userStore.getUsers();
  }
}