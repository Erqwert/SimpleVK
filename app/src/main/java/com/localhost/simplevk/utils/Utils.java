package com.localhost.simplevk.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utils {

  /**
   * Static method to hide Software Keyboard
   * @param activity
   */
  public static void hideSoftKeyboard(Activity activity) {
    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    if(null!=activity.getCurrentFocus().getWindowToken()) {
      inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
  }

  /**
   * Static method to remove focus from any view in @param group and hide keyboard
   * @param activity
   * @param group in which focus should be removed
   */
  public static void removeFocusFromEditTexts(Activity activity, int group){
    hideSoftKeyboard(activity);
    ViewGroup mygroup = (ViewGroup)activity.findViewById(group);
    int count=0;
    if(mygroup.getChildCount()>0) {
      count = mygroup.getChildCount();
      for (int i = 0; i < count; ++i) {
        View view = mygroup.getChildAt(i);
        if (view instanceof EditText) {
          //((EditText) view).setText("");
          ((EditText) view).setFocusable(false);
          ((EditText) view).setFocusableInTouchMode(true);
        }
      }
    }
  }

  /**
   * Static method to check if Network is available
   * @param context
   * @return true if enabled, false otherwise
   */
  public static boolean isNetworkAvailable(Context context)
  {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }
}
