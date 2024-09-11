package com.hurontg.tiger.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class Staff {
  private String name;
  private String email;
  private String phone;
  private String password;

  public Staff(String name, String email, String phone, String password) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.password = password;
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

  public JSONObject toJsonObject() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("name", name);
    json.put("email", email);
    json.put("phone", phone);
    json.put("password", password);
    return json;
  }
}
