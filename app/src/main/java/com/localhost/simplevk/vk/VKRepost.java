package com.localhost.simplevk.vk;


public class VKRepost {
  // Reposts count, taken from items
  private int reposts_count;
  // Reposted flag, taken from items
  private boolean reposted;

  public void setReposts_count(int reposts_count) {
    this.reposts_count = reposts_count;
  }

  public void setReposted(boolean reposted) {
    this.reposted = reposted;
  }

  public int getReposts_count() {

    return reposts_count;
  }

  public boolean isReposted() {
    return reposted;
  }
}
