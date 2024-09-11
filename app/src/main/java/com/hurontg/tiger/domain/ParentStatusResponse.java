package com.hurontg.tiger.domain;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ParentStatusResponse {
  private String status;
  private List<LatLng> regions;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<LatLng> getRegions() {
    return regions;
  }

  public void setRegions(List<LatLng> regions) {
    this.regions = regions;
  }

  public JSONObject getRegionsAsJson() {
    JSONObject asJson = new JSONObject();
    JSONArray arr = new JSONArray();
    try {
      for(LatLng ll: regions) {
        JSONObject json = new JSONObject();
        json.put("latitude", ll.latitude );
        json.put("longitude", ll.longitude);
        arr.put(json);
      }
      asJson.put("regions", arr);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return asJson;
  }
}
