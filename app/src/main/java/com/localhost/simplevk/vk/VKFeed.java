package com.localhost.simplevk.vk;

import java.io.Serializable;

public class VKFeed implements Serializable{

  public void setPost_id(int post_id) {
    this.post_id = post_id;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getPost_id() {

    return post_id;
  }

  public long getDate() {
    return date;
  }

  public String getText() {
    return text;
  }

  // post_id on user/group wall
  private int post_id;
  // Date of post, taken from items
  private long date;
  // Text body, taken from items
  private String text;

  private VKSource vkSource;
  private VKLike vkLike;
  private VKRepost vkRepost;
  private VKComment vkComment;
  //private VKFeed vkFeed_repost;

  public void setVkComment(VKComment vkComment) {
    this.vkComment = vkComment;
  }

  public VKComment getVkComment() {

    return vkComment;
  }

  public void setVkRepost(VKRepost vkRepost) {
    this.vkRepost = vkRepost;
  }

  public VKRepost getVkRepost() {

    return vkRepost;
  }

  public VKLike getVkLike() {
    return vkLike;
  }

  public void setVkLike(VKLike vkLike) {

    this.vkLike = vkLike;
  }

  public void setVkSource(VKSource vkSource) {
    this.vkSource = vkSource;
  }

  public VKSource getVkSource() {

    return vkSource;
  }
}
