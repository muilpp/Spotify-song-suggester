package xyz.spotifyrecommender.model.webservicedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistDTO {

  private List<PlaylistItem> playlistItemList;

  @JsonProperty("items")
  public List<PlaylistItem> getPlaylistItemList() {
    return playlistItemList;
  }

  public void setPlaylistItemList(List<PlaylistItem> playlistItemList) {
    this.playlistItemList = playlistItemList;
  }
}