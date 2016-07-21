package xyz.spotifyrecommender.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static xyz.spotifyrecommender.model.Constant.COULD_NOT_GET_RECOMMENDATIONS;
import static xyz.spotifyrecommender.model.Constant.NOT_ENOUGH_DATA_FOR_RECOMMENDATIONS;
import static xyz.spotifyrecommender.model.Constant.STEP_SIZE_FOR_RECS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import xyz.spotifyrecommender.model.webservice_data.Item;
import xyz.spotifyrecommender.model.webservice_data.RecommendationDTO;
import xyz.spotifyrecommender.model.webservice_data.Token;
import xyz.spotifyrecommender.model.webservice_data.TopShortTermTracksDTO;
import xyz.spotifyrecommender.model.webservice_data.Track;
import xyz.spotifyrecommender.model.webservice_data.TrackURI;

@RunWith(MockitoJUnitRunner.class)
public class SuggestTest {

    @Mock
    SpotifyAPI spotifyAPI;

    @InjectMocks
    Suggest suggest = new Suggest();

    Token token;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        token = new Token();
        token.setAccessToken("testToken");

        TopShortTermTracksDTO topTracksDTO = new TopShortTermTracksDTO();
        List<Item> itemList = new ArrayList<>();
        createItemList(itemList);
        topTracksDTO.setItemList(itemList);
        doReturn(topTracksDTO).when(spotifyAPI).getTopTracks(anyString());

        doReturn("testPlaylist").when(spotifyAPI).getPlaylistId(anyString());
        doReturn("testUserId").when(spotifyAPI).getUserId(anyString());

        RecommendationDTO recs = new RecommendationDTO();
        Track track = new Track();
        track.setSongId("testSongId2");
        Set<Track> set = new HashSet<>();
        set.add(track);
        recs.setTrackList(set);
        doReturn(recs).when(spotifyAPI).getRecommendations(anyString(), anyList());
    }

    @Test
    public void notEnoughDataIfNoSongsAvailable() {
        doReturn(new TopShortTermTracksDTO()).when(spotifyAPI).getTopTracks(anyString());

        RecommendationDTO recommendations = suggest.getRecommendations(token);

        verify(spotifyAPI, times(1)).getTopTracks(anyString());
        assertThat(recommendations.isSuccess(), equalTo(false));
        assertThat(recommendations.getMessage(), equalTo(NOT_ENOUGH_DATA_FOR_RECOMMENDATIONS));
    }

    @Test
    public void noRecommendationsIfPlaylistIsNotCreated() {
        doReturn(null).when(spotifyAPI).getPlaylistId(anyString());
        doReturn(null).when(spotifyAPI).createPlaylist(anyString(), anyString());

        RecommendationDTO recommendations = suggest.getRecommendations(token);

        verify(spotifyAPI, times(1)).getPlaylistId(anyString());
        verify(spotifyAPI, times(1)).getUserId(anyString());
        verify(spotifyAPI, times(1)).createPlaylist(anyString(), anyString());

        assertThat(recommendations.isSuccess(), equalTo(false));
        assertThat(recommendations.getMessage(), equalTo(COULD_NOT_GET_RECOMMENDATIONS));
    }

    @Test
    public void songsAreReplacedIfOldSongsStillPresent() {
        doReturn(Arrays.asList(new TrackURI())).when(spotifyAPI).createUriTrackList(any(RecommendationDTO.class));
        doReturn(3).when(spotifyAPI).replaceOldSongsInPlaylist(anyString(), anyString(), anyString(), any(TrackURI.class));

        RecommendationDTO recommendations = suggest.getRecommendations(token);

        assertThat(recommendations.isSuccess(), equalTo(true));
        assertThat(recommendations.getMessage(), equalTo("3"));
    }

    @Test
    public void songsAreAddedIfNewSongsAlreadyPresent() {
        doReturn(Arrays.asList(new TrackURI(), new TrackURI())).when(spotifyAPI).createUriTrackList(any(RecommendationDTO.class));
        doReturn(4).when(spotifyAPI).replaceOldSongsInPlaylist(anyString(), anyString(), anyString(), any(TrackURI.class));

        RecommendationDTO recommendations = suggest.getRecommendations(token);

        assertThat(recommendations.isSuccess(), equalTo(true));
        assertThat(recommendations.getMessage(), equalTo("4"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noRecommendationsIfSpotifyGivesNoRecommendations() {
        RecommendationDTO recs = new RecommendationDTO();
        recs.setTrackList(Collections.<Track>emptySet());
        doReturn(recs).when(spotifyAPI).getRecommendations(anyString(), anyList());

        RecommendationDTO recommendations = suggest.getRecommendations(token);

        verify(spotifyAPI, times(1)).getRecommendations(anyString(), anyList());
        assertThat(recommendations.isSuccess(), equalTo(false));
        assertThat(recommendations.getMessage(), equalTo(COULD_NOT_GET_RECOMMENDATIONS));        
    }

    @Test
    public void listIsSteppedBySpecifiedSize() {

        List<Item> itemList = new ArrayList<>();
        createItemList(itemList);

        List<String> resultList = suggest.getListByStepSize(itemList);

        for (String result : resultList) {
            assertThat(result, CoreMatchers.containsString(","));

            String[] results = result.split(",");
            assertThat(results.length, equalTo(STEP_SIZE_FOR_RECS));
        }
    }

    private void createItemList(List<Item> itemList) {
        Item item = new Item();

        for (int i = 0; i<12; i++) {
            item.setSongId("testId"+i);
            item.setSongName("testName"+i);
            itemList.add(item);

            item = new Item();
        }
    }
}