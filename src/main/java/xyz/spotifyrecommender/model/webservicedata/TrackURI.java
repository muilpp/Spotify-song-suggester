package xyz.spotifyrecommender.model.webservicedata;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackURI {
    private Set<String> uriSet;

    public TrackURI() {
        uriSet = new HashSet<>();
    }

    @JsonProperty("uris")
    public Set<String> getURISet() {
        return uriSet;
    }

    public void setURISet(Set<String> uriList) {
        this.uriSet = uriList;
    }
}