package xyz.spotifyrecommender.model.webservicedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

  private List<Artist> artistList;
  private String songId;
  private String songName;

  @JsonProperty("artists")
  public List<Artist> artistList() {
    return artistList;
  }

  public void setArtistList(List<Artist> artistList) {
    this.artistList = artistList;
  }

  @JsonProperty("id")
  public String getSongId() {
    return songId;
  }

  public void setSongId(String id) {
    this.songId = id;
  }

  @JsonProperty("name")
  public String getSongName() {
    return songName;
  }

  public void setSongName(String songName) {
    this.songName = songName;
  }
}