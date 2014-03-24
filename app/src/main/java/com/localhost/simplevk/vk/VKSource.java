package com.localhost.simplevk.vk;

import java.io.Serializable;

public class VKSource implements Serializable {
  private int id;
  private String name;
  private String avatar50URL;
  private String avatar100URL;

  public void setAvatar100URL(String avatar100URL) {
    this.avatar100URL = avatar100URL;
  }

  public String getAvatar100URL() {

    return avatar100URL;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAvatar50URL() {
    return avatar50URL;
  }

  public void setId(int id) {

    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAvatar50URL(String avatar50URL) {
    this.avatar50URL = avatar50URL;
  }
}
