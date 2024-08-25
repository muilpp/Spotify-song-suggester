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
import xyz.spotifyrecommender.model.webservicedata.Item;
import xyz.spotifyrecommender.model.webservicedata.RecommendationDTO;
import xyz.spotifyrecommender.model.webservicedata.Token;
import xyz.spotifyrecommender.model.webservicedata.TopTracksDTO;
import xyz.spotifyrecommender.model.webservicedata.Track;
import xyz.spotifyrecommender.model.webservicedata.TrackURI;

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

        TopTracksDTO topTracksDTO = new TopTracksDTO();
        List<Item> itemList = new ArrayList<>();
        createItemList(itemList);
        topTracksDTO.setItemList(itemList);
        doReturn(topTracksDTO).when(spotifyAPI).getTopTracks(anyString(), anyString(), anyString());

        doReturn("testPlaylist").when(spotifyAPI).getPlaylistId();
        doReturn("testUserId").when(spotifyAPI).getUserId(anyString());

        RecommendationDTO recs = new RecommendationDTO();
        Track track = new Track();
        track.setSongId("testSongId2");
        Set<Track> set = new HashSet<>();
        set.add(track);
        recs.setTrackList(set);
        doReturn(recs).when(spotifyAPI).getRecommendations(anyList(), anyString());
    }

    @Test
    public void notEnoughDataIfNoSongsAvailable() {
        doReturn(new TopTracksDTO()).when(spotifyAPI).getTopTracks(anyString(), anyString(), anyString());

        RecommendationDTO recommendations = suggest.getRecommendations(token, "false", "true");

        verify(spotifyAPI, times(1)).getTopTracks(anyString(), anyString(), anyString());
        assertThat(recommendations.isSuccess(), equalTo(false));
        assertThat(recommendations.getMessage(), equalTo(NOT_ENOUGH_DATA_FOR_RECOMMENDATIONS));
    }

    @Test
    public void noRecommendationsIfPlaylistIsNotCreated() {
        doReturn(null).when(spotifyAPI).getPlaylistId();
        doReturn(null).when(spotifyAPI).createPlaylist(anyString());

        RecommendationDTO recommendations = suggest.getRecommendations(token, "false", "true");

        verify(spotifyAPI, times(1)).getPlaylistId();
        verify(spotifyAPI, times(1)).getUserId(anyString());
        verify(spotifyAPI, times(1)).createPlaylist(anyString());

        assertThat(recommendations.isSuccess(), equalTo(false));
        assertThat(recommendations.getMessage(), equalTo(COULD_NOT_GET_RECOMMENDATIONS));
    }

    @Test
    public void songsAreReplacedIfOldSongsStillPresent() {
        doReturn(Arrays.asList(new TrackURI())).when(spotifyAPI).createUriTrackList(any(RecommendationDTO.class));
        doReturn(3).when(spotifyAPI).replaceOldSongsInPlaylist(anyString(), anyString(),
                any(TrackURI.class));

        RecommendationDTO recommendations = suggest.getRecommendations(token, "false", "true");

        assertThat(recommendations.isSuccess(), equalTo(true));
        assertThat(recommendations.getMessage(), equalTo("3"));
    }

    @Test
    public void songsAreAddedIfNewSongsAlreadyPresent() {
        doReturn(Arrays.asList(new TrackURI(), new TrackURI())).when(spotifyAPI)
                .createUriTrackList(any(RecommendationDTO.class));
        doReturn(4).when(spotifyAPI).replaceOldSongsInPlaylist(anyString(), anyString(),
                any(TrackURI.class));

        RecommendationDTO recommendations = suggest.getRecommendations(token, "false", "true");

        assertThat(recommendations.isSuccess(), equalTo(true));
        assertThat(recommendations.getMessage(), equalTo("4"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noRecommendationsIfSpotifyGivesNoRecommendations() {
        RecommendationDTO recs = new RecommendationDTO();
        recs.setTrackList(Collections.<Track> emptySet());
        doReturn(recs).when(spotifyAPI).getRecommendations(anyList(), anyString());

        RecommendationDTO recommendations = suggest.getRecommendations(token, "false", "true");

        verify(spotifyAPI, times(1)).getRecommendations(anyList(), anyString());
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

        for (int i = 0; i < 12; i++) {
            item.setSongId("testId" + i);
            item.setSongName("testName" + i);
            itemList.add(item);

            item = new Item();
        }
    }
}