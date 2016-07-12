package xyz.spotifyrecommender.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import xyz.spotifyrecommender.model.SpotifyAPI;
import xyz.spotifyrecommender.model.Suggest;
import xyz.spotifyrecommender.model.database.UserDAO;
import xyz.spotifyrecommender.model.webservice_data.RecommendationDTO;
import xyz.spotifyrecommender.model.webservice_data.Token;

@RestController
@RequestMapping("/suggest")
public class SuggestController {

    private final static Logger LOGGER = Logger.getLogger(SuggestController.class.getName());

    @Autowired
    private Suggest suggest;

    @Autowired
    private SpotifyAPI spotifyAPI;

    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "/{authorizationCode}", method = RequestMethod.GET)
    public RecommendationDTO getSuggestions(@PathVariable("authorizationCode") String authorizationCode) {

        Token authToken = spotifyAPI.requestToken(authorizationCode);
        String userName = spotifyAPI.getUserId(authToken.getAccessToken());

        if (!userDAO.userExists(userName))
            userDAO.addUser(userName, authToken.getAccessToken(), authToken.getRefreshToken());

        return suggest.getRecommendations(authToken);
    }
}