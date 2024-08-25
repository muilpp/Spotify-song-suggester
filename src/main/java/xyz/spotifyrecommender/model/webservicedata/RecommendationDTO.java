package xyz.spotifyrecommender.model.webservicedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationDTO {

  private Set<Track> trackSet;
  private boolean success;
  private String message;

  public RecommendationDTO() {
    trackSet = new HashSet<>();
  }

  public RecommendationDTO(boolean succes, String message) {
    this.setSuccess(succes);
    this.message = message;
  }

  @JsonProperty("tracks")
  public Set<Track> getTrackSet() {
    return trackSet;
  }

  public void setTrackList(Set<Track> trackSet) {
    this.trackSet = trackSet;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}