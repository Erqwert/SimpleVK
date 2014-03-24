package com.localhost.simplevk.utils;

import android.support.v4.app.Fragment;

public interface FragmentSavedState {
  void setFragmentSavedState(String key, Fragment.SavedState state);
  Fragment.SavedState getFragmentSavedState(String key);
}
