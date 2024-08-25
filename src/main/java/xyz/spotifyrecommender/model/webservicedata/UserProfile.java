package xyz.spotifyrecommender.model.webservicedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

  private String href;
  private String id;
  private String uri;

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}