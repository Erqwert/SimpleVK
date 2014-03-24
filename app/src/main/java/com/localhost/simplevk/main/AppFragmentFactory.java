package com.localhost.simplevk.main;

import android.support.v4.app.Fragment;

import com.localhost.simplevk.R;
import com.localhost.simplevk.authentication.AuthenticationFragment_;
import com.localhost.simplevk.feed.FeedsFragment_;

import org.androidannotations.annotations.EBean;

@EBean
public class AppFragmentFactory {
  public Fragment newFragmentById(int fragmentId) {
    switch (fragmentId) {
      case R.id.fragment_feeds:
        return new FeedsFragment_();
      case R.id.fragment_authentication:
        return new AuthenticationFragment_();
      default:
        throw new IllegalArgumentException();
    }
  }

  public String assignTAG(int fragmentId){
    switch(fragmentId){
      case R.id.fragment_feeds:
        return "FRAGMENT_FEEDS";
      case R.id.fragment_authentication:
        return "FRAGMENT_AUTHENTICATION";
      default:
        return "FRAGMENT_AUTHENTICATION";
    }
  }

}
