package com.hurontg.tiger.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Parent {
  private String name;
  private String email;
  private String phone;
  private String password;
  private String uuid;
  private List<Child> children;

  public Parent(String name, String email, String phone, String password, List<Child> children) {
    this(name, email, phone, password, null, children);
  }

  public Parent(String name, String email, String phone, String password, String uuid, List<Child> children) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.password = password;
    this.uuid = uuid;
    this.children = children;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public List<Child> getChildren() {
    return children;
  }

  public void setChildren(List<Child> children) {
    this.children = children;
  }

  public String getUuid() {
    return uuid;
  }

  public JSONObject toJsonObject() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("name", name);
    json.put("email", email);
    json.put("phone", phone);
    json.put("password", password);
    JSONArray children = new JSONArray();
    for (Child child : this.children) {
      JSONObject c = new JSONObject();
      c.put("name", child.getName());
      c.put("grade", child.getGrade());
      c.put("section", child.getSection());
      children.put(c);
    }
    json.put("children", children);

    return json;
  }

  public String childrenAsOneLine() {
    StringBuilder sb = new StringBuilder();
    for(Child child: children) {
      sb.append(child.toString())
        .append(", ");
    }
    sb.deleteCharAt(sb.length()-1);
    return sb.toString();
  }
}
