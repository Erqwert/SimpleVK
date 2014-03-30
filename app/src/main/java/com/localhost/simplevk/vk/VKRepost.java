package com.localhost.simplevk.vk;

import android.os.Parcel;
import android.os.Parcelable;

import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONException;
import org.json.JSONObject;

public class VKRepost implements Parcelable{

  public VKRepost(){

  }

  /**
   * Name of source (user or group)
   */
  public String source_name;

  /**
   * Link to avatar 50x50 of source (user or group)
   */
  public String source_avatar50URL;

  /**
   * Link to avatar 100x100 of source (user or group)
   */
  public String source_avatar100URL;

  /**
   * ID of user or group
   */
  public int source_id;

  /**
   * ID of post from user or group wall
   */
  public int id;

  /**
   * Date (in Unix time) the post was added.
   */
  public long date;

  /**
   * Text of the post.
   */
  public String text;

  /**
   * ID of the user who posted.
   */
  public int from_id;

  /**
   * Owner ID.
   */
  public int owner_id;

  /**
   * Type of the post, can be: post, copy, reply, postpone, suggest.
   */
  public String post_type;

  /**
   * Information about attachments to the post (photos, links, etc.), if any;
   */
  public VKAttachments attachments = new VKAttachments();

  /**
   * Setter for vkSourse
   * @param vkSource
   */
  public void setSourceData(VKSource vkSource){
    source_id = vkSource.getId();
    source_name = vkSource.getName();
    source_avatar50URL = vkSource.getAvatar50URL();
    source_avatar100URL = vkSource.getAvatar100URL();
  }

  /**
   * Fills a Post instance from JSONObject.
   */
  public VKRepost parse(JSONObject source) throws JSONException {

    id = source.optInt("id");
    date = source.optLong("date");
    text = source.optString("text");
    owner_id = source.optInt("owner_id");
    from_id = source.optInt("from_id");

    post_type = source.optString("post_type");

    attachments.fill(source.optJSONArray("attachments"));

    return this;
  }

  /**
   * Creates a Post instance from Parcel.
   */
  public VKRepost(Parcel in) {
    this.source_id = in.readInt();
    this.source_name = in.readString();
    this.source_avatar50URL = in.readString();
    this.source_avatar100URL = in.readString();
    this.id = in.readInt();
    this.date = in.readLong();
    this.text = in.readString();
    this.owner_id = in.readInt();
    this.from_id = in.readInt();
    this.post_type = in.readString();
    this.attachments = in.readParcelable(VKAttachments.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.source_id);
    dest.writeString(this.source_name);
    dest.writeString(source_avatar50URL);
    dest.writeString(source_avatar100URL);
    dest.writeInt(this.id);
    dest.writeLong(this.date);
    dest.writeString(this.text);
    dest.writeInt(this.owner_id);
    dest.writeInt(this.from_id);
    dest.writeString(this.post_type);
    dest.writeParcelable(attachments, flags);
  }

  public static Parcelable.Creator<VKRepost> CREATOR = new Parcelable.Creator<VKRepost>() {
    public VKRepost createFromParcel(Parcel source) {
      return new VKRepost(source);
    }

    public VKRepost[] newArray(int size) {
      return new VKRepost[size];
    }
  };







}
