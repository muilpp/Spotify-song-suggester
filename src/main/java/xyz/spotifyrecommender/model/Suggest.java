package xyz.spotifyrecommender.model;

import static xyz.spotifyrecommender.model.Constant.COULD_NOT_GET_RECOMMENDATIONS;
import static xyz.spotifyrecommender.model.Constant.NOT_ENOUGH_DATA_FOR_RECOMMENDATIONS;
import static xyz.spotifyrecommender.model.Constant.STEP_SIZE_FOR_RECS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import xyz.spotifyrecommender.model.webservice_data.Item;
import xyz.spotifyrecommender.model.webservice_data.RecommendationDTO;
import xyz.spotifyrecommender.model.webservice_data.Token;
import xyz.spotifyrecommender.model.webservice_data.TopShortTermTracksDTO;
import xyz.spotifyrecommender.model.webservice_data.TrackURI;

@Service
public class Suggest {
    private static final Logger LOGGER = Logger.getLogger(Suggest.class.getName());

    @Autowired
    private SpotifyAPI spotifyApi;

    public RecommendationDTO getRecommendations(Token authToken) {

        TopShortTermTracksDTO shortTermTracks = spotifyApi.getTopTracks(authToken.getAccessToken());

        List<Item> itemList = shortTermTracks.getItemList();

        if (itemList == null || itemList.isEmpty())
            return new RecommendationDTO(false, NOT_ENOUGH_DATA_FOR_RECOMMENDATIONS);

        List<String> songIdList = getListByStepSize(itemList);

        String playlistId = spotifyApi.getPlaylistId(authToken.getAccessToken());
        String userId = spotifyApi.getUserId(authToken.getAccessToken());

        if (Strings.isNullOrEmpty(playlistId)) {
            // Create playlist for the first time
            playlistId = spotifyApi.createPlaylist(authToken.getAccessToken(), userId);

            if (Strings.isNullOrEmpty(playlistId))
                return new RecommendationDTO(false, COULD_NOT_GET_RECOMMENDATIONS);
        }

        RecommendationDTO recs = spotifyApi.getRecommendations(authToken.getAccessToken(), songIdList);

        if (recs.getTrackSet().isEmpty())
            return new RecommendationDTO(false, COULD_NOT_GET_RECOMMENDATIONS);
        else {
            List<TrackURI> trackURIList = spotifyApi.createUriTrackList(recs);

            int addedSongsCount = 0;

            // First chunk replaces the old songs
            if (trackURIList.size() > 0)
                addedSongsCount += spotifyApi.replaceOldSongsInPlaylist(authToken.getAccessToken(), userId, playlistId,
                        trackURIList.get(0));

            // If there're still songs to be added, add them normally
            if (trackURIList.size() > 1) {
                for (int i = 1; i < trackURIList.size(); i++) {
                    addedSongsCount += spotifyApi.addNewSongsToPlaylist(authToken.getAccessToken(), userId, playlistId,
                            trackURIList.get(i));
                }
            }

            return new RecommendationDTO(true, Integer.toString(addedSongsCount));
        }
    }

    List<String> getListByStepSize(List<Item> list) {
        List<String> result = new ArrayList<>();
        List<List<Item>> itemSubList = Lists.partition(list, STEP_SIZE_FOR_RECS);

        for (int i = 0; i < itemSubList.size(); i++) {
            List<String> songSet = new ArrayList<>();

            for (Item item : itemSubList.get(i)) {
                songSet.add(item.getSongId());
            }

            String songName = songSet.toString().replace("[", "").replace("]", "").replaceAll(" ", "");
            result.add(songName);
        }

        return result;
    }
}