package xyz.spotifyrecommender.model;

import java.util.List;
import xyz.spotifyrecommender.model.webservicedata.PlaylistDTO;
import xyz.spotifyrecommender.model.webservicedata.RecommendationDTO;
import xyz.spotifyrecommender.model.webservicedata.Token;
import xyz.spotifyrecommender.model.webservicedata.TopTracksDTO;
import xyz.spotifyrecommender.model.webservicedata.TrackURI;

public interface SpotifyAPI {

  TopTracksDTO getTopTracks(String bearer, String avoidSpanishMusic, String isShortTerm);

  String getPlaylistId();

  PlaylistDTO getUserPlaylists();

  String getUserId(String bearer);

  String createPlaylist(final String userId);

  int replaceOldSongsInPlaylist(String userId, String playlistId,
      final TrackURI trackURI);

  int addNewSongsToPlaylist(String userId, String playlistId, TrackURI trackURI);

  List<TrackURI> createUriTrackList(RecommendationDTO recs);

  /**
   * Requests an access token and a refresh token for the user
   *
   * @return a Token with the access, refresh and expiration time if authCode works, empty Token
   * otherwise
   */
  Token requestToken(String authorizationCode);

  /**
   * Request a new access token when the current one is expired
   *
   * @return a new Token with the access if refreshToken works, empty Token otherwise
   */
  Token refreshToken(String userName, String refreshToken);

  /**
   * Request the profile user name
   *
   * @return the user name if found
   */
  String getSpotifyUserName();

  /**
   * Returns a bunch of songs recommended from Spotify
   *
   * @return the list of songs wrapped in the RecommendationDTO
   */
  RecommendationDTO getRecommendations(List<String> songIdList, String avoidSpanishMusic);
}