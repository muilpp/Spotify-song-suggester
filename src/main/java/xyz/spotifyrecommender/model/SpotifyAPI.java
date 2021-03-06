package xyz.spotifyrecommender.model;

import java.util.List;

import xyz.spotifyrecommender.model.webservicedata.PlaylistDTO;
import xyz.spotifyrecommender.model.webservicedata.RecommendationDTO;
import xyz.spotifyrecommender.model.webservicedata.Token;
import xyz.spotifyrecommender.model.webservicedata.TopTracksDTO;
import xyz.spotifyrecommender.model.webservicedata.TrackURI;

public interface SpotifyAPI {
    public TopTracksDTO getTopTracks(String bearer, String avoidSpanishMusic, String isShortTerm);

    public String getPlaylistId();

    public PlaylistDTO getUserPlaylists();

    public String getUserId(String bearer);

    public String createPlaylist(final String userId);

    public int replaceOldSongsInPlaylist(String userId, String playlistId,
            final TrackURI trackURI);

    public int addNewSongsToPlaylist(String userId, String playlistId, TrackURI trackURI);

    public List<TrackURI> createUriTrackList(RecommendationDTO recs);

    /**
     * Requests an access token and a refresh token for the user
     * 
     * @param authorizationCode
     * @return a Token with the access, refresh and expiration time if authCode
     *         works, empty Token otherwise
     */
    public Token requestToken(String authorizationCode);

    /**
     * Request a new access token when the current one is expired
     * 
     * @param userName
     * @param refreshToken
     * @return a new Token with the access if refreshToken works, empty Token
     *         otherwise
     */
    public Token refreshToken(String userName, String refreshToken);

    /**
     * Request the profile user name
     * 
     * @return the user name if found
     */
    public String getSpotifyUserName();

    /**
     * Returns a bunch of songs recommended from Spotify
     * 
     * @param songIdList
     * @return the list of songs wrapped in the RecommendationDTO
     */
    public RecommendationDTO getRecommendations(List<String> songIdList, String avoidSpanishMusic);
}