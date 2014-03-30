package com.localhost.simplevk.vk;

import android.os.Parcel;
import android.os.Parcelable;

import com.vk.sdk.api.model.ParseUtils;
import com.vk.sdk.api.model.VKApiPlace;
import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VKFeed implements Parcelable{

  public VKFeed(){

  }

  /**
   * Flag: if feed from user - flag is true
   */
  public boolean isUserFeed;

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
  public int post_id;

  /**
   * Date (in Unix time) the post was added.
   */
  public long date;

  /**
   * Text of the post.
   */
  public String text;

  /**
   * Number of comments.
   */
  public int comments_count;

  /**
   * Whether the current user can leave comments to the post (false — cannot, true — can)
   */
  public boolean can_post_comment;

  /**
   * Number of users who liked the post.
   */
  public int likes_count;

  /**
   * Whether the user liked the post (false — not liked, true — liked)
   */
  public boolean user_likes;

  /**
   * Whether the user can like the post (false — cannot, true — can).
   */
  public boolean can_like;

  /**
   * Whether the user can reposts (false — cannot, true — can).
   */
  public boolean can_publish;

  /**
   * Number of users who copied the post.
   */
  public int reposts_count;

  /**
   * Whether the user reposted the post (false — not reposted, true — reposted).
   */
  public boolean user_reposted;

  /**
   * Type of the post, can be: post, copy, reply, postpone, suggest.
   */
  public String post_type;

  /**
   * Information about attachments to the post (photos, links, etc.), if any;
   */
  public VKAttachments attachments = new VKAttachments();

  /**
   * Information about location.
   */
  public VKApiPlace geo;

  /**
   * List of history of the reposts.
   */
  public ArrayList<VKRepost> copy_history;

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
  public VKFeed parse(JSONObject source) throws JSONException {
    source_id = source.optInt("source_id");
    post_id = source.optInt("post_id");
    date = source.optLong("date");
    text = source.optString("text");
    JSONObject comments = source.optJSONObject("comments");
    if(null != comments) {
      comments_count = comments.optInt("count");
      can_post_comment = ParseUtils.parseBoolean(comments, "can_post");
    }
    JSONObject likes = source.optJSONObject("likes");
    if(null != likes) {
      likes_count = likes.optInt("count");
      user_likes = ParseUtils.parseBoolean(likes, "user_likes");
      can_like = ParseUtils.parseBoolean(likes, "can_like");
      can_publish = ParseUtils.parseBoolean(likes, "can_publish");
    }
    JSONObject reposts = source.optJSONObject("reposts");
    if(null != reposts) {
      reposts_count = reposts.optInt("count");
      user_reposted = ParseUtils.parseBoolean(reposts, "user_reposted");
    }
    post_type = source.optString("post_type");

    attachments.fill(source.optJSONArray("attachments"));

    JSONObject geo = source.optJSONObject("geo");
    if(null != geo) {
      this.geo = new VKApiPlace().parse(geo);
    }

    JSONArray history = source.optJSONArray("copy_history");
    if (null != history){
      copy_history = new ArrayList<>();
      for (int i = 0; i < history.length(); i++) {
        VKRepost vkRepost = new VKRepost();
        vkRepost.parse(history.getJSONObject(i));
        copy_history.add(vkRepost);
      }
    }

    return this;
  }

  /**
   * Creates a Post instance from Parcel.
   */
  public VKFeed(Parcel in) {
    this.isUserFeed = in.readByte() != 0;
    this.source_id = in.readInt();
    this.source_name = in.readString();
    this.source_avatar50URL = in.readString();
    this.source_avatar100URL = in.readString();
    this.post_id = in.readInt();
    this.date = in.readLong();
    this.text = in.readString();
    this.comments_count = in.readInt();
    this.can_post_comment = in.readByte() != 0;
    this.likes_count = in.readInt();
    this.user_likes = in.readByte() != 0;
    this.can_like = in.readByte() != 0;
    this.can_publish = in.readByte() != 0;
    this.reposts_count = in.readInt();
    this.user_reposted = in.readByte() != 0;
    this.post_type = in.readString();
    this.attachments = in.readParcelable(VKAttachments.class.getClassLoader());
    this.geo = in.readParcelable(VKApiPlace.class.getClassLoader());
    this.copy_history = in.readArrayList(VKRepost.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(this.isUserFeed ? (byte) 1 : (byte) 0);
    dest.writeInt(this.source_id);
    dest.writeString(this.source_name);
    dest.writeString(source_avatar50URL);
    dest.writeString(source_avatar100URL);
    dest.writeInt(this.post_id);
    dest.writeLong(this.date);
    dest.writeString(this.text);
    dest.writeInt(this.comments_count);
    dest.writeByte(can_post_comment ? (byte) 1 : (byte) 0);
    dest.writeInt(this.likes_count);
    dest.writeByte(user_likes ? (byte) 1 : (byte) 0);
    dest.writeByte(can_like ? (byte) 1 : (byte) 0);
    dest.writeByte(can_publish ? (byte) 1 : (byte) 0);
    dest.writeInt(this.reposts_count);
    dest.writeByte(user_reposted ? (byte) 1 : (byte) 0);
    dest.writeString(this.post_type);
    dest.writeParcelable(attachments, flags);
    dest.writeParcelable(this.geo, flags);
    dest.writeTypedList(this.copy_history);
  }

  public static Parcelable.Creator<VKFeed> CREATOR = new Parcelable.Creator<VKFeed>() {
    public VKFeed createFromParcel(Parcel source) {
      return new VKFeed(source);
    }

    public VKFeed[] newArray(int size) {
      return new VKFeed[size];
    }
  };
}
