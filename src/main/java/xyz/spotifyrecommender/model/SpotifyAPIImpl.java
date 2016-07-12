package xyz.spotifyrecommender.model;

import static xyz.spotifyrecommender.model.Constant.AUTHORIZATION_CODE;
import static xyz.spotifyrecommender.model.Constant.CLIENT_ID;
import static xyz.spotifyrecommender.model.Constant.CLIENT_ID_KEY;
import static xyz.spotifyrecommender.model.Constant.CLIENT_SECRET;
import static xyz.spotifyrecommender.model.Constant.CLIENT_SECRET_KEY;
import static xyz.spotifyrecommender.model.Constant.CODE_KEY;
import static xyz.spotifyrecommender.model.Constant.DEFAULT_TOP_TRACKS_LIMIT;
import static xyz.spotifyrecommender.model.Constant.GRANT_TYPE_KEY;
import static xyz.spotifyrecommender.model.Constant.MAX_SONGS_TO_ADD_PER_REQUEST;
import static xyz.spotifyrecommender.model.Constant.MAX_TIME_TO_WAIT_IN_SECS;
import static xyz.spotifyrecommender.model.Constant.PLAYLIST_NAME;
import static xyz.spotifyrecommender.model.Constant.REDIRECT_URI;
import static xyz.spotifyrecommender.model.Constant.REDIRECT_URI_KEY;
import static xyz.spotifyrecommender.model.Constant.REFRESH_TOKEN_KEY;
import static xyz.spotifyrecommender.model.Constant.SPOTIFY_TRACK;
import static xyz.spotifyrecommender.model.Constant.STEP_SIZE_FOR_RECS;
import static xyz.spotifyrecommender.model.Endpoints.buildURIForRecommendations;
import static xyz.spotifyrecommender.model.Endpoints.buildURIForTopTracks;
import static xyz.spotifyrecommender.model.Endpoints.buildURIToAddNewSongs;
import static xyz.spotifyrecommender.model.Endpoints.buildURIToCreatePlaylist;
import static xyz.spotifyrecommender.model.Endpoints.buildURIToGetUserPlaylists;
import static xyz.spotifyrecommender.model.Endpoints.buildURIToGetUserProfile;
import static xyz.spotifyrecommender.model.Endpoints.buildURIToReplaceOldSongs;
import static xyz.spotifyrecommender.model.Endpoints.buildURIToRequestToken;
import static xyz.spotifyrecommender.model.Endpoints.buildURIToRequestUserProfileName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;

import xyz.spotifyrecommender.model.webservice_data.PlaylistDTO;
import xyz.spotifyrecommender.model.webservice_data.PlaylistItem;
import xyz.spotifyrecommender.model.webservice_data.RecommendationDTO;
import xyz.spotifyrecommender.model.webservice_data.Token;
import xyz.spotifyrecommender.model.webservice_data.TopShortTermTracksDTO;
import xyz.spotifyrecommender.model.webservice_data.Track;
import xyz.spotifyrecommender.model.webservice_data.TrackURI;
import xyz.spotifyrecommender.model.webservice_data.UserProfile;
import xyz.spotifyrecommender.model.webservice_data.UserProfileDTO;

@Service
public class SpotifyAPIImpl implements SpotifyAPI {

    private final static Logger LOGGER = Logger.getLogger(SpotifyAPIImpl.class.getName());

    @Override
    public TopShortTermTracksDTO getTopTracks(String bearer) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BearerHeaderInterceptor(bearer));

        ResponseEntity<TopShortTermTracksDTO> response = restTemplate.getForEntity(buildURIForTopTracks(), TopShortTermTracksDTO.class);

        if (response.getStatusCode() == HttpStatus.OK)
            return response.getBody();
        else {
            LOGGER.info("Failed : HTTP error code -> " + response.getStatusCodeValue());
            LOGGER.info(response.getStatusCode().getReasonPhrase());
        }

        return new TopShortTermTracksDTO();
    }

    @Override
    public String getPlaylistId(String bearer) {
        PlaylistDTO playlistDTO = getUserPlaylists(bearer);

        for (PlaylistItem playlistItem : playlistDTO.getPlaylistItemList()) {
            if (playlistItem.getPlaylistName().equalsIgnoreCase(PLAYLIST_NAME)) {
                return playlistItem.getPlaylistId();
            }
        }

        return null;
    }

    @Override
    public PlaylistDTO getUserPlaylists(String bearer) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BearerHeaderInterceptor(bearer));

        ResponseEntity<PlaylistDTO> response = restTemplate.getForEntity(buildURIToGetUserPlaylists(), PlaylistDTO.class);

        if (response.getStatusCode() == HttpStatus.OK)
            return response.getBody();
        else {
            LOGGER.info("Failed : HTTP error code -> " + response.getStatusCodeValue());
            LOGGER.info(response.getStatusCode().getReasonPhrase());
        }

        return new PlaylistDTO();
    }

    @Override
    public String getUserId(String bearer) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BearerHeaderInterceptor(bearer));

        ResponseEntity<UserProfileDTO> response = restTemplate.getForEntity(buildURIToGetUserProfile(), UserProfileDTO.class);

        if (response.getStatusCode() == HttpStatus.OK)
            return response.getBody().getUserId();
        else {
            LOGGER.info("Failed : HTTP error code -> " + response.getStatusCodeValue());
            LOGGER.info(response.getStatusCode().getReasonPhrase());
        }

        return null;
    }

    @Override
    public String createPlaylist(String bearer, String userId) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BearerHeaderInterceptor(bearer));

        PlaylistItem playlist = new PlaylistItem(PLAYLIST_NAME, true);
        ResponseEntity<String> response = restTemplate.postForEntity(buildURIToCreatePlaylist(userId), playlist, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED)
            return response.getBody();
        else {
            LOGGER.info("Failed : HTTP error code -> " + response.getStatusCodeValue());
            LOGGER.info(response.getStatusCode().getReasonPhrase());
        }

        return null;
    }

    @Override
    public int replaceOldSongsInPlaylist(String bearer, String userId, String playlistId, TrackURI trackURI) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BearerHeaderInterceptor(bearer));

        HttpEntity<TrackURI> trackEntity = new HttpEntity<>(trackURI);
        ResponseEntity<Integer> response = restTemplate.exchange(buildURIToReplaceOldSongs(userId, playlistId), HttpMethod.PUT, trackEntity, Integer.class);

        if (response.getStatusCode() == HttpStatus.CREATED)
            return trackURI.getURISet().size();
        else {
            LOGGER.info( "Failed : HTTP error code : " + response.getStatusCodeValue());
            LOGGER.info(response.getStatusCode().getReasonPhrase());
        }

        return 0;
    }

    @Override
    public int addNewSongsToPlaylist(String bearer, String userId, String playlistId, TrackURI trackURI) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BearerHeaderInterceptor(bearer));

        ResponseEntity<String> response = restTemplate.postForEntity(buildURIToAddNewSongs(userId, playlistId), trackURI, String.class);
        if (response.getStatusCode() == HttpStatus.CREATED) {
            return trackURI.getURISet().size();
        } else {
            LOGGER.info("Failed : HTTP error code -> " + response.getStatusCodeValue());
            LOGGER.info(response.getStatusCode().getReasonPhrase());
        }

        return 0;
    }

    @Override
    public List<TrackURI> createUriTrackList(RecommendationDTO recs) {
        if (recs.getTrackSet().isEmpty())
            return Collections.emptyList();

        List<TrackURI> trackURIList = new ArrayList<>();
        if (recs.getTrackSet().size() > MAX_SONGS_TO_ADD_PER_REQUEST) {
            List<Track> allTracksList = new ArrayList<>();
            allTracksList.addAll(recs.getTrackSet());
            List<List<Track>> trackSublist = Lists.partition(allTracksList, MAX_SONGS_TO_ADD_PER_REQUEST);

            for (int i=0; i<trackSublist.size(); i++) {
                TrackURI trackURI = new TrackURI();
                for (Track track : trackSublist.get(i)) {
                    trackURI.getURISet().add(SPOTIFY_TRACK + track.getSongId());
                }
                trackURIList.add(trackURI);
            }
        } else {
            TrackURI trackURI = new TrackURI();
            for (Track track : recs.getTrackSet()) {
                trackURI.getURISet().add(SPOTIFY_TRACK + track.getSongId());
            }

            trackURIList.add(trackURI);
        }

        return trackURIList;
    }

    @Override
    public Token requestToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> authData = new LinkedMultiValueMap<>();
        authData.add(GRANT_TYPE_KEY, AUTHORIZATION_CODE);
        authData.add(CODE_KEY, authorizationCode);
        authData.add(REDIRECT_URI_KEY, REDIRECT_URI);
        authData.add(CLIENT_ID_KEY, CLIENT_ID);
        authData.add(CLIENT_SECRET_KEY, CLIENT_SECRET);
        
        ResponseEntity<Token> response = restTemplate.postForEntity(buildURIToRequestToken(), authData, Token.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            LOGGER.info("Failed : HTTP error code -> " + response.getStatusCodeValue());
            LOGGER.info(response.getStatusCode().getReasonPhrase());
        }

        return new Token();
    }

    @Override
    public Token refreshToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        
        MultiValueMap<String, String> authData = new LinkedMultiValueMap<>();
        authData.add(GRANT_TYPE_KEY, REFRESH_TOKEN_KEY);
        authData.add(REFRESH_TOKEN_KEY, refreshToken);
        authData.add(CLIENT_ID_KEY, CLIENT_ID);
        authData.add(CLIENT_SECRET_KEY, CLIENT_SECRET);

        ResponseEntity<Token> response = restTemplate.postForEntity(buildURIToRequestToken(), authData, Token.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            LOGGER.info("Failed : HTTP error code -> " + response.getStatusCodeValue());
            LOGGER.info(response.getStatusCode().getReasonPhrase());
        }

        return new Token();
    }

    @Override
    public String getSpotifyUserName(String bearer) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<UserProfile> response = restTemplate.getForEntity(buildURIToRequestUserProfileName(), UserProfile.class);

        if (response.getStatusCode() == HttpStatus.CREATED)
            return response.getBody().getId();
        else {
            LOGGER.info("Failed : HTTP error code -> " + response.getStatusCodeValue());
            LOGGER.info(response.getStatusCode().getReasonPhrase());
        }

        return null;
    }

    @Override
    public RecommendationDTO getRecommendations(String accessToken, List<String> songIdList) {
        final int MAX_THREADS = (int)(Math.ceil((double)DEFAULT_TOP_TRACKS_LIMIT/STEP_SIZE_FOR_RECS));        
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        RecommendationDTO recs = new RecommendationDTO();

        List<Future<Set<Track>>> workerRecommendations = new ArrayList<>();
        for (String songId : songIdList) {
            Callable<Set<Track>> worker = new Recommendations(accessToken, songId);
            Future<Set<Track>> submit = executor.submit(worker);
            workerRecommendations.add(submit);
        }
        
        executor.shutdown();

        try {
            executor.awaitTermination(MAX_TIME_TO_WAIT_IN_SECS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        for (Future<Set<Track>> future : workerRecommendations) {
            try {
                if (future.get() != null) {
                    recs.getTrackSet().addAll(future.get());
                    LOGGER.info("List size -> " + recs.getTrackSet().size());
                } else
                    LOGGER.log(Level.SEVERE, "Future is null");
            } catch (InterruptedException | ExecutionException | NullPointerException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return recs;
    }

    private class Recommendations implements Callable<Set<Track>> {
        private final Logger LOGGER = Logger.getLogger(RecommendationDTO.class.getName());
        private String accessToken;
        private String songId;

        public Recommendations(String accesToken, String songId) {
            this.accessToken = accesToken;
            this.songId = songId;
        }

        @Override
        public Set<Track> call() throws Exception {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new BearerHeaderInterceptor(accessToken));

            LOGGER.info("URI -> " + buildURIForRecommendations(songId.replace("[", "").replace("]", "")));
            ResponseEntity<RecommendationDTO> response = restTemplate.getForEntity(buildURIForRecommendations(songId.replace("[", "").replace("]", "")), RecommendationDTO.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                RecommendationDTO recDTO = response.getBody();
                return recDTO.getTrackSet();
            } else {
                LOGGER.info("Failed : HTTP error code : " + response.getStatusCodeValue());
                LOGGER.info(response.getStatusCode().getReasonPhrase());
                return Collections.emptySet();
            }
        }
    }
}