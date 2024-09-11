package com.hurontg.tiger.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RealtimeLocation {
  private String parentUuid;
  private String parentName;
  private LatLng latLng;
  private List<Child> children;
  private float distanceToCampus;

  public String getParentUuid() {
    return parentUuid;
  }

  public void setParentUuid(String parentUuid) {
    this.parentUuid = parentUuid;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public LatLng getLatLng() {
    return latLng;
  }

  public void setLatLng(LatLng latLng) {
    this.latLng = latLng;
  }

  public List<Child> getChildren() {
    return children;
  }

  public void setChildren(List<Child> children) {
    this.children = children;
  }

  public float getDistanceToCampus() {
    return this.distanceToCampus;
  }

  public void setDistanceToCampus(float distance) {
    this.distanceToCampus = distance;
  }

  public String getTitle() {
    String unit = "m";
    float distance = distanceToCampus;
    if (distance >= 1000) {
      unit = "km";
      distance /= 1000;
    }

    StringBuilder sb = new StringBuilder();
    for (Child c: children) {
      if (sb.length() > 0) sb.append(", ");
      sb.append(c.getName())
          .append("(")
          .append(c.getGrade())
          .append(c.getSection().toUpperCase())
          .append(")");
    }

    String s = String.format("%s %4.2f %s", sb.toString(), distance, unit);
    return s;
  }

  public boolean isCampusMatch(int campus) {
    if (children == null || children.isEmpty()) return false;

    for(Child child: children) {
      if (child.isCampusMatch(campus)) {
        return true;
      }
    }
    return false;
  }
}
