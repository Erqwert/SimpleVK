package com.localhost.simplevk.vk;

public class VKLike {
  // Likes count, taken from items
  private int likes_count;
  // Liked flag, taken from items
  private boolean liked;
  // Can like flag, taken from items
  private boolean can_like;
  // Can repost flag, taken from items
  private boolean can_repost;

  public void setLikes_count(int likes_count) {
    this.likes_count = likes_count;
  }

  public void setLiked(boolean liked) {
    this.liked = liked;
  }

  public void setCan_like(boolean can_like) {
    this.can_like = can_like;
  }

  public void setCan_repost(boolean can_repost) {
    this.can_repost = can_repost;
  }

  public int getLikes_count() {

    return likes_count;
  }

  public boolean isLiked() {
    return liked;
  }

  public boolean isCan_like() {
    return can_like;
  }

  public boolean isCan_repost() {
    return can_repost;
  }
}
