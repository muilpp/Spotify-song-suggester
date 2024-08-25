package xyz.spotifyrecommender.model.webservicedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TopTracksDTO {

  private List<Item> itemsList;

  public TopTracksDTO() {
    itemsList = new ArrayList<>();
  }

  public TopTracksDTO(List<Item> itemList) {
    this.itemsList = itemList;
  }

  @JsonProperty("items")
  public List<Item> getItemList() {
    return itemsList;
  }

  public void setItemList(List<Item> itemList) {
    this.itemsList = itemList;
  }
}