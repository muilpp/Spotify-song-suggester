package xyz.spotifyrecommender.model.webservicedata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistItem {

  private String playlistName;
  private String playlistId;
  private boolean isPublic;
  private PlaylistTrack playListTracks;

  public PlaylistItem() {
    //A no-arg constructor is needed to instantiate the object
  }

  public PlaylistItem(String name, boolean isPublic) {
    playlistName = name;
    this.isPublic = isPublic;
  }

  @JsonProperty("name")
  public String getPlaylistName() {
    return playlistName;
  }

  public void setPlaylistName(String playlistName) {
    this.playlistName = playlistName;
  }

  // This is to prevent serialization of playlistId, since it will only be
  // deserialized, always.
  @JsonIgnore
  public String getPlaylistId() {
    return playlistId;
  }

  @JsonProperty("id")
  public void setPlaylistId(String playlistId) {
    this.playlistId = playlistId;
  }

  @JsonProperty("public")
  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  @JsonIgnore
  public PlaylistTrack getPlayListTracks() {
    return playListTracks;
  }

  @JsonProperty("tracks")
  public void setPlayListTracks(PlaylistTrack playListTracks) {
    this.playListTracks = playListTracks;
  }
}