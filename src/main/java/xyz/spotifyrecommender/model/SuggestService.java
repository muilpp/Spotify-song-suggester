package xyz.spotifyrecommender.model;

import static xyz.spotifyrecommender.model.Constant.COULD_NOT_GET_RECOMMENDATIONS;
import static xyz.spotifyrecommender.model.Constant.NOT_ENOUGH_DATA_FOR_RECOMMENDATIONS;
import static xyz.spotifyrecommender.model.Constant.STEP_SIZE_FOR_RECS;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.spotifyrecommender.model.webservicedata.Item;
import xyz.spotifyrecommender.model.webservicedata.RecommendationDTO;
import xyz.spotifyrecommender.model.webservicedata.Token;
import xyz.spotifyrecommender.model.webservicedata.TopTracksDTO;
import xyz.spotifyrecommender.model.webservicedata.TrackURI;

@Service
@RequiredArgsConstructor
public class SuggestService {

  private static final Logger LOGGER = Logger.getLogger(SuggestService.class.getName());

  private final SpotifyAPI spotifyApi;

  public RecommendationDTO getRecommendations(Token authToken, String avoidSpanishMusic,
      String isShortTerm) {
    TopTracksDTO shortTermTracks = spotifyApi.getTopTracks(authToken.getAccessToken(),
        avoidSpanishMusic, isShortTerm);

    List<Item> itemList = shortTermTracks.getItemList();

    if (itemList == null || itemList.isEmpty()) {
      LOGGER.info(NOT_ENOUGH_DATA_FOR_RECOMMENDATIONS);
      return new RecommendationDTO(false, NOT_ENOUGH_DATA_FOR_RECOMMENDATIONS);
    }

    List<String> songIdList = getListByStepSize(itemList);

    String playlistId = spotifyApi.getPlaylistId();
    String userId = spotifyApi.getUserId(authToken.getAccessToken());

    if (Strings.isNullOrEmpty(playlistId)) {
      // Create playlist for the first time
      playlistId = spotifyApi.createPlaylist(userId);

      if (Strings.isNullOrEmpty(playlistId)) {
        LOGGER.info(COULD_NOT_GET_RECOMMENDATIONS);
        return new RecommendationDTO(false, COULD_NOT_GET_RECOMMENDATIONS);
      }
    }

    RecommendationDTO recs = spotifyApi.getRecommendations(songIdList, avoidSpanishMusic);

    if (recs.getTrackSet().isEmpty()) {
      LOGGER.info(COULD_NOT_GET_RECOMMENDATIONS);
      return new RecommendationDTO(false, COULD_NOT_GET_RECOMMENDATIONS);
    } else {
      List<TrackURI> trackURIList = spotifyApi.createUriTrackList(recs);
      int addedSongsCount = 0;

      // First chunk replaces the old songs
      if (!trackURIList.isEmpty()) {
        addedSongsCount += spotifyApi.replaceOldSongsInPlaylist(userId, playlistId,
            trackURIList.get(0));
      }
      // If there're still songs to be added, add them normally
      if (trackURIList.size() > 1) {
        for (int i = 1; i < trackURIList.size(); i++) {
          addedSongsCount += spotifyApi.addNewSongsToPlaylist(userId, playlistId,
              trackURIList.get(i));
        }
      }

      return new RecommendationDTO(true, Integer.toString(addedSongsCount));
    }
  }

  List<String> getListByStepSize(List<Item> list) {
    List<String> result = new ArrayList<>();
    List<List<Item>> itemSubList = Lists.partition(list, STEP_SIZE_FOR_RECS);

    for (List<Item> items : itemSubList) {
      List<String> songSet = new ArrayList<>();

      for (Item item : items) {
        songSet.add(item.getSongId());
      }

      String songName = songSet.toString().replace("[", "").replace("]", "").replaceAll(" ", "");
      result.add(songName);
    }

    return result;
  }
}