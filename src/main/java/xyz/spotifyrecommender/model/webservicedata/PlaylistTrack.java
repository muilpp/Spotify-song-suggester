package xyz.spotifyrecommender.model.webservicedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistTrack {

  private String totalSongs;

  @JsonProperty("total")
  public String getTotalSongs() {
    return totalSongs;
  }

  public void setTotalSongs(String totalSongs) {
    this.totalSongs = totalSongs;
  }
}