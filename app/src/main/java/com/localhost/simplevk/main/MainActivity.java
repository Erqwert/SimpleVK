package com.localhost.simplevk.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.localhost.simplevk.R;
import com.localhost.simplevk.authentication.AuthenticationFragment_;
import com.localhost.simplevk.feed.FeedsFragment;
import com.localhost.simplevk.feed.FeedsTasksFragment;
import com.localhost.simplevk.utils.FragmentSavedState;
import com.localhost.simplevk.vk.VKFeed;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity implements FragmentSavedState, FeedsFragment.FeedFragmentCallbacks, FeedsTasksFragment.GetFeedsActivityCallbacks{

  private static final String TAG = "MainActivity";

  @Bean
  protected AppFragmentFactory appFragmentFactory;

  int currentFragmentId = -1;

  private boolean isAfterConfigurationChanged =false;
  private static final String IS_AFTER_CONFIGURATION_CHANGED_TAG ="isAfterConfigurationChanged";
  private int fragmentBeforeConfigurationChanged = -1;
  private static final String FRAGMENT_BEFORE_CONFIGURATION_CHANGED_TAG ="fragmentBeforeConfigurationChanged";
  HashMap<String, Fragment.SavedState> savedStateMap;
  private static final String SAVED_STATE_TAG ="savedState";

  VKSdkListener vkSdkListener;
  private static final String APP_ID = "4255934";
  private static final String VK_SDK_ACCESS_TOKEN_PREF_KEY = "VK_SDK_ACCESS_TOKEN_PLEASE_DONT_TOUCH";

  /**
   * Saves current state of fragment
   * @param key fragment class name
   * @param state fragment's state
   */
  public void setFragmentSavedState(String key, Fragment.SavedState state){
    savedStateMap.put(key, state);
    Log.i(TAG, "Saved state for "+key);
  }

  /**
   * Loads saved state of fragment
   * @param key fragment class name
   * @return fragment's saved state
   */
  public Fragment.SavedState getFragmentSavedState(String key){
    Log.i(TAG, "Loaded state for "+key);
    return savedStateMap.get(key);
  }

  @AfterInject
  protected void init() {
    initVKSdkListener();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestFeatureFix();
    supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR);
    getSupportActionBar().hide();

    VKSdk.initialize(vkSdkListener, APP_ID, VKAccessToken.tokenFromSharedPreferences(this, VK_SDK_ACCESS_TOKEN_PREF_KEY));

    if (savedInstanceState != null) {
      isAfterConfigurationChanged = savedInstanceState.getBoolean(IS_AFTER_CONFIGURATION_CHANGED_TAG);
      fragmentBeforeConfigurationChanged = savedInstanceState.getInt(FRAGMENT_BEFORE_CONFIGURATION_CHANGED_TAG);
      savedStateMap = ((HashMap<String, Fragment.SavedState>) savedInstanceState.getSerializable(SAVED_STATE_TAG));
    } else {
      savedStateMap = new HashMap<String, Fragment.SavedState>();
    }

    if (isAfterConfigurationChanged) {
      if (fragmentBeforeConfigurationChanged != -1) {
        goToFragment(fragmentBeforeConfigurationChanged);
      }
    }else {
      goToStartFragment();
    }
  }

  /**
   * Saving current state to Bundle so after configuration change we load fragment we was on before.
   * @param outState Bundle
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(IS_AFTER_CONFIGURATION_CHANGED_TAG, true);
    outState.putInt(FRAGMENT_BEFORE_CONFIGURATION_CHANGED_TAG, currentFragmentId);
    outState.putSerializable(SAVED_STATE_TAG, savedStateMap);
  }

  /**
   * Loads desired fragment and sets Listeners if needed
   * @param fragmentId id of desired fragment
   */
  public void goToFragment(int fragmentId) {
    if(fragmentId != currentFragmentId){
      currentFragmentId = fragmentId;
      String fragmentTAG=appFragmentFactory.assignTAG(fragmentId);

      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment destinationFragment = fragmentManager.findFragmentByTag(fragmentTAG);

      if(destinationFragment==null){
        destinationFragment = appFragmentFactory.newFragmentById(fragmentId);
        Log.i(TAG, "destinationFragment=null, creating new fragment "+appFragmentFactory.assignTAG(currentFragmentId));
        //if(destinationFragment instanceof AuthenticationFragment_){
          //destinationFragment.setInitialSavedState(getFragmentSavedState(destinationFragment.getClass().getSimpleName()));
          //Log.i(TAG, "Set initial state for "+destinationFragment.getClass().getSimpleName());
        //}
        if (fragmentManager.getBackStackEntryCount() > 0) {
          fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentManager.beginTransaction()
          .replace(R.id.activity_main_container, destinationFragment, fragmentTAG)
          .commit();
      }else if(destinationFragment.isAdded()){
        fragmentManager.beginTransaction()
          .show(destinationFragment)
          .commit();
        Log.i(TAG, "destinationFragment is already added, showing fragment "+appFragmentFactory.assignTAG(currentFragmentId));
      }
      if(destinationFragment instanceof AuthenticationFragment_) {
        ((AuthenticationFragment_) destinationFragment).setVkSdkListener(vkSdkListener);
      }
    }
  }

  /**
   * Loads start fragment depending on if user is logged or not
   */
  public void goToStartFragment(){
    if(!VKSdk.isLoggedIn()){
      goToFragment(R.id.fragment_authentication);
    }else{
      goToFragment(R.id.fragment_feeds);
    }
  }

  /**
   * VKSdkListener initialization. On AuthOK we load FeedsFragment and save access_toke to
   * SharedPreferences
   */
  private void initVKSdkListener(){
    vkSdkListener = new VKSdkListener() {
      @Override
      public void onCaptchaError(VKError captchaError) {
        Log.i("TestAuthFragment", "onCaptchaError");
      }

      @Override
      public void onTokenExpired(VKAccessToken expiredToken) {
        Log.i("TestAuthFragment", "onTokenExpired");
      }

      @Override
      public void onAccessDenied(VKError authorizationError) {
        Log.i("TestAuthFragment", "onAccessDenied, Error: "+authorizationError.errorReason+" "+authorizationError.errorMessage );
      }

      /**
       * After successful authentication we save access_token to shared preferences
       * and open fragment_feed and show ActionBar
       * @param token used token for API requests
       */
      @Override
      public void onAcceptUserToken(VKAccessToken token) {
        super.onAcceptUserToken(token);
        getSupportActionBar().show();
        token.saveTokenToSharedPreferences(getApplicationContext(), VK_SDK_ACCESS_TOKEN_PREF_KEY);
        goToFragment(R.id.fragment_feeds);
        Log.i("MainActivity", "Auth OK! token: "+token.accessToken+" user_id: "+token.userId);
      }
    };
  }

  /**
   * On logout we hide back ActionBar and load AuthenticationFragment
   */
  @Override
  public void onLogout() {
    getSupportActionBar().hide();
    goToFragment(R.id.fragment_authentication);
  }


  /**
   * mHasActionBar reflection fix in Support Library
   * Details: http://stackoverflow.com/questions/18526144/actionbarcompat-hide-actionbar-before-activity-is-created-bug
   */
  private void requestFeatureFix() {
    try {
      Field fieldImpl = ActionBarActivity.class.getDeclaredField("mImpl");
      fieldImpl.setAccessible(true);
      Object impl = fieldImpl.get(this);

      Class<?> cls = Class.forName("android.support.v7.app.ActionBarActivityDelegate");

      Field fieldHasActionBar = cls.getDeclaredField("mHasActionBar");
      fieldHasActionBar.setAccessible(true);
      fieldHasActionBar.setBoolean(impl, true);

    } catch (NoSuchFieldException e) {
      Log.e("boom", e.getLocalizedMessage(), e);
    } catch (IllegalAccessException e) {
      Log.e("boom", e.getLocalizedMessage(), e);
    } catch (IllegalArgumentException e) {
      Log.e("boom", e.getLocalizedMessage(), e);
    } catch (ClassNotFoundException e) {
      Log.e("boom", e.getLocalizedMessage(), e);
    }
  }

  /**
   * Callback metod - after we got feeds from asyncTask - we update UI.
   * @param vkFeeds
   */
  @Override
  public void onVKResponseParsed(ArrayList<VKFeed> vkFeeds, String new_from) {
    Fragment feedsFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_container);
    if (feedsFragment instanceof FeedsFragment) {
      ((FeedsFragment)feedsFragment).processVKResponseParsedResult(vkFeeds, new_from);
    }
  }
}
