package com.localhost.simplevk.main;

import android.support.v4.app.Fragment;

import com.localhost.simplevk.R;
import com.localhost.simplevk.authentication.AuthenticationFragment_;
import com.localhost.simplevk.feed.FeedsFragment_;
import com.localhost.simplevk.post.PostFragment_;

import org.androidannotations.annotations.EBean;

@EBean
public class AppFragmentFactory {

  /**
   * Creates new Fragment by ID
   *
   * @param fragmentId
   * @return
   */
  public Fragment newFragmentById(int fragmentId) {
    switch (fragmentId) {
      case R.id.fragment_feeds:
        return new FeedsFragment_();
      case R.id.fragment_authentication:
        return new AuthenticationFragment_();
      case R.id.fragment_post:
        return new PostFragment_();
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * Assigns TAG to Fragment by ID
   *
   * @param fragmentId
   * @return
   */
  public String assignTAG(int fragmentId) {
    switch (fragmentId) {
      case R.id.fragment_feeds:
        return "FRAGMENT_FEEDS";
      case R.id.fragment_authentication:
        return "FRAGMENT_AUTHENTICATION";
      case R.id.fragment_post:
        return "FRAGMENT_POST";
      default:
        return "FRAGMENT_AUTHENTICATION";
    }
  }

}
