package xyz.spotifyrecommender.model;

import static xyz.spotifyrecommender.model.Constant.DEFAULT_RECS_LIMIT;
import static xyz.spotifyrecommender.model.Constant.DEFAULT_TOP_TRACKS_LIMIT;
import static xyz.spotifyrecommender.model.Constant.LIMIT;
import static xyz.spotifyrecommender.model.Constant.MARKET;

public final class Endpoints {
    public static final String BASE_URL = "https://api.spotify.com/v1";
    public static final String TOP_TRACKS_EP = "/me/top/tracks";
    public static final String RECOMMENDATIONS_EP = "/recommendations";
    public static final String PLAYLISTS_EP = "/me/playlists";
    public static final String USER_PROFILE_EP = "/me";
    public static final String CREATE_PLAYLIST_EP = "/users/{userId}/playlists";
    public static final String REPLACE_SONGS_IN_PLAYLIST_EP = "/users/{userId}/playlists/{playlistId}/tracks";
    public static final String ADD_SONGS_IN_PLAYLIST_EP = "/users/{userId}/playlists/{playlistId}/tracks";
    public static final String REQUEST_TOKEN_EP = "https://accounts.spotify.com/api/token";
    public static final String REQUEST_USER_PROFILE = "https://api.spotify.com/v1/me/";

    private static final String USER_ID_PLACEHOLDER = "{userId}";

    private Endpoints() {}

    public static String buildURIForTopTracks(String shortTerm) {
    	final String TracksTimeRange = shortTerm.equalsIgnoreCase("1") ? TimeRange.SHORT_TERM.getTimeRange() : TimeRange.MEDIUM_TERM.getTimeRange(); 

    	return new StringBuilder()
    			.append(BASE_URL)
    			.append(TOP_TRACKS_EP)
    			.append("?")
    			.append(Constant.TIME_RANGE)
    			.append("=")
    			.append(TracksTimeRange)
    			.append("&")
    			.append(LIMIT)
    			.append("=")
    			.append(Integer.toString(DEFAULT_TOP_TRACKS_LIMIT))
    			.toString();
    }

    public static String buildURIForRecommendations(String songIds) {
    	return new StringBuilder()
    			.append(BASE_URL)
    			.append(RECOMMENDATIONS_EP)
    			.append("?")
    			.append(Constant.SEED_TRACKS)
    			.append("=")
    			.append(songIds)
    			.append("&")
    			.append(MARKET)
    			.append("&")
    			.append(LIMIT)
    			.append("=")
    			.append(DEFAULT_RECS_LIMIT)
    			.toString();
    }

    public static String buildURIToGetUserPlaylists() {
    	return new StringBuilder()
    			.append(BASE_URL)
    			.append(PLAYLISTS_EP)
    			.toString();
    }

    public static String buildURIToGetUserProfile() {
    	return new StringBuilder()
    			.append(BASE_URL)
    			.append(USER_PROFILE_EP)
    			.toString();
    }

    public static String buildURIToCreatePlaylist(String userId) {
    	return new StringBuilder()
    			.append(BASE_URL)
    			.append(CREATE_PLAYLIST_EP.replace(USER_ID_PLACEHOLDER, userId))
    			.toString();
    }

    public static String buildURIToReplaceOldSongs(String userId, String playlistId) {
    	return new StringBuilder()
    			.append(BASE_URL)
    			.append(REPLACE_SONGS_IN_PLAYLIST_EP.replace(USER_ID_PLACEHOLDER, userId).replace("{playlistId}", playlistId))
    			.toString();
    }

    public static String buildURIToAddNewSongs(String userId, String playlistId) {
    	return new StringBuilder()
    			.append(BASE_URL)
    			.append(ADD_SONGS_IN_PLAYLIST_EP.replace(USER_ID_PLACEHOLDER, userId).replace("{playlistId}", playlistId))
    			.toString();
    }

    public static String buildURIToRequestToken() {
        return REQUEST_TOKEN_EP;
    }

    public static String buildURIToRequestUserProfileName() {
        return REQUEST_USER_PROFILE;
    }
}