package com.localhost.simplevk.vk;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class VKSource implements Parcelable {

  public VKSource() {

  }

  /**
   * ID of user or group
   */
  private int id;
  /**
   * name of user or group
   */
  private String name;
  /**
   * 50x50 avatar URL
   */
  private String avatar50URL;
  /**
   * 100x100 avatar URL
   */
  private String avatar100URL;

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

  /**
   * Parse JSON method
   *
   * @param source
   * @return
   * @throws JSONException
   */
  public VKSource parse(JSONObject source) throws JSONException {
    id = source.optInt("id");

    String jsonName = source.optString("name", "");
    if (!jsonName.equals("")) {
      name = source.optString("name");
    } else {
      name = source.optString("first_name") + " " + source.optString("last_name");
    }

    avatar50URL = source.optString("photo_50");
    avatar100URL = source.optString("photo_100");
    return this;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.name);
    dest.writeString(avatar50URL);
    dest.writeString(avatar100URL);
  }

  public VKSource(Parcel in) {
    this.id = in.readInt();
    this.name = in.readString();
    this.avatar50URL = in.readString();
    this.avatar100URL = in.readString();
  }
}
