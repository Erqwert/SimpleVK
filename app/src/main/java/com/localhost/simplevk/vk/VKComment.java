package com.localhost.simplevk.vk;

public class VKComment {
  // Comments count, taken from items
  private int comments_count;
  // Commented flag, taken from items
  private boolean can_comment;

  public void setComments_count(int comments_count) {
    this.comments_count = comments_count;
  }

  public void setCan_comment(boolean can_comment) {
    this.can_comment = can_comment;
  }

  public boolean isCan_comment() {

    return can_comment;
  }

  public int getComments_count() {

    return comments_count;
  }
}
