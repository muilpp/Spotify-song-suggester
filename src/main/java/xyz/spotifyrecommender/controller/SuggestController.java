package xyz.spotifyrecommender.controller;

import static xyz.spotifyrecommender.model.Constant.AUTHENTICATION_PROBLEM;
import static xyz.spotifyrecommender.model.Constant.DEFAULT_ACCESS_REVOKED;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;

import xyz.spotifyrecommender.model.SpotifyAPI;
import xyz.spotifyrecommender.model.Suggest;
import xyz.spotifyrecommender.model.database.UserDAO;
import xyz.spotifyrecommender.model.webservicedata.RecommendationDTO;
import xyz.spotifyrecommender.model.webservicedata.Token;

@RestController
@RequestMapping("/suggest")
public class SuggestController {

    private static final Logger LOGGER = Logger.getLogger(SuggestController.class.getName());

    @Autowired
    private Suggest suggest;

    @Autowired
    private SpotifyAPI spotifyAPI;

    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "/{authorizationCode}", method = RequestMethod.GET)
    public RecommendationDTO getSuggestions(@PathVariable("authorizationCode") String authorizationCode) {
        Token authToken = spotifyAPI.requestToken(authorizationCode);

        if (Strings.isNullOrEmpty(authToken.getAccessToken()))
            return new RecommendationDTO(false, AUTHENTICATION_PROBLEM);

        String userName = spotifyAPI.getUserId(authToken.getAccessToken());

        if (Strings.isNullOrEmpty(userName))
            return new RecommendationDTO(false, AUTHENTICATION_PROBLEM);

        if (!userDAO.userExists(userName))
            userDAO.addUser(userName, authToken.getAccessToken(), authToken.getRefreshToken());
        else
            userDAO.updateUserAccess(userName, DEFAULT_ACCESS_REVOKED, authToken.getAccessToken(),
                    authToken.getRefreshToken());

        LOGGER.log(Level.INFO, "User [{0}] genera una llista nova", userName);

        return suggest.getRecommendations(authToken);
    }
}