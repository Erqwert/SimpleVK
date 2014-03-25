package com.localhost.simplevk.profile;

import android.content.Context;
import android.content.SharedPreferences;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class Account {
  private static final Account INSTANCE = new Account( );
  public static Account getInstance() {
    return INSTANCE;
  }

  private static final String TAG = "Account";

  private SharedPreferences user;

  public static SharedPreferences getUserDataSharedPreferences(Context context)
  {
    return context.getSharedPreferences("account", Context.MODE_PRIVATE);
  }

}
