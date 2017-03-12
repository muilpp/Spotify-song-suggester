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
    
    private Endpoints() {
    }

    public static String buildURIForTopTracks() {
        return BASE_URL + TOP_TRACKS_EP + "?" + Constant.TIME_RANGE + "=" + TimeRange.SHORT_TERM.getTimeRange() + "&" + LIMIT + "="
                + Integer.toString(DEFAULT_TOP_TRACKS_LIMIT);
    }

    public static String buildURIForRecommendations(String songIds) {
        return BASE_URL + RECOMMENDATIONS_EP + "?" + Constant.SEED_TRACKS + "=" + songIds + "&" + MARKET + "&" + LIMIT
                + "=" + DEFAULT_RECS_LIMIT;
    }

    public static String buildURIToGetUserPlaylists() {
        return BASE_URL + PLAYLISTS_EP;
    }

    public static String buildURIToGetUserProfile() {
        return BASE_URL + USER_PROFILE_EP;
    }

    public static String buildURIToCreatePlaylist(String userId) {
        return BASE_URL + CREATE_PLAYLIST_EP.replace(USER_ID_PLACEHOLDER, userId);
    }

    public static String buildURIToReplaceOldSongs(String userId, String playlistId) {
        return BASE_URL + REPLACE_SONGS_IN_PLAYLIST_EP.replace(USER_ID_PLACEHOLDER, userId).replace("{playlistId}", playlistId);
    }

    public static String buildURIToAddNewSongs(String userId, String playlistId) {
        return BASE_URL + ADD_SONGS_IN_PLAYLIST_EP.replace(USER_ID_PLACEHOLDER, userId).replace("{playlistId}", playlistId);
    }

    public static String buildURIToRequestToken() {
        return REQUEST_TOKEN_EP;
    }

    public static String buildURIToRequestUserProfileName() {
        return REQUEST_USER_PROFILE;
    }
}