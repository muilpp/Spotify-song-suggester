package xyz.spotifyrecommender.model;

public final class Constant {
    public static final String CLIENT_ID = "";
    public static final String CLIENT_SECRET = "";
    public static final String TIME_RANGE = "time_range";
    public static final String SEED_TRACKS = "seed_tracks";
    public static final String MARKET = "market=US";
    public static final String LIMIT = "limit";
    public static final int MAX_SONGS_TO_ADD_PER_REQUEST = 100;
    public static final int DEFAULT_TOP_TRACKS_LIMIT = 30;
    public static final String DEFAULT_RECS_LIMIT = "25";
    public static final int STEP_SIZE_FOR_RECS = 3;
    public static final String PLAYLIST_NAME = "Weekly Suggestions";
    public static final String SPOTIFY_TRACK = "spotify:track:";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String REDIRECT_URI = "http://www.spotifyrecommender.xyz";
    public static final int MAX_TIME_TO_WAIT_IN_SECS = 5;
    public static final String DEFAULT_ACCESS_REVOKED = "0";
    public static final String USER_ACCESS_REVOKED = "1";

    // Webservice params keys
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    public static final String GRANT_TYPE_KEY = "grant_type";
    public static final String CODE_KEY = "code";
    public static final String REDIRECT_URI_KEY = "redirect_uri";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_SECRET_KEY = "client_secret";

    // Quartz
    public static final String EXECUTE_USER_JOB = "executeUserJob";
    public static final String JOB_MANAGER_GROUP = "jobManagerGroup";
    public static final String EXECUTE_USER_TRIGGER = "executeUserTrigger";
    public static final String USER_SCHEDULE = "userSchedule";

    // Error Messages
    public static final String AUTHENTICATION_PROBLEM = "There was a problem during the login, please try again later";
    public static final String NOT_ENOUGH_DATA_FOR_RECOMMENDATIONS = "Not enough data to fetch a list of recommendations, listen to some music first!";
    public static final String COULD_NOT_GET_RECOMMENDATIONS = "Could not get recommendations this time, please try again later";
    
    private Constant() {}
}