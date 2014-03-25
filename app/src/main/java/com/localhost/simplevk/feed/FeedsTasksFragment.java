package com.localhost.simplevk.feed;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.localhost.simplevk.vk.VKFeed;
import com.localhost.simplevk.vk.VKSource;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@EFragment
public class FeedsTasksFragment extends Fragment{

  VKRequest newsFeedRequest;
  private static final int VKREQUEST_FEEDS_COUNT = 50;

  private static final String TAG = "FeedsTasksFragment";

  private GetFeedsActivityCallbacks getFeedsActivityCallbacks;

  public static interface GetFeedsActivityCallbacks {
    void onVKResponseParsed(ArrayList<VKFeed> VKFeeds);
  }

  @AfterInject
  protected void init(){

  }

  /**
   * Set callbacks to activity
   * @param activity
   */
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    getFeedsActivityCallbacks = (GetFeedsActivityCallbacks) activity;
  }

  /**
   * We retain this fragment so it won't get destroyed on Activity's onDestroy()
   * @param savedInstanceState
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  /**
   * Remove callbacks onDetach()
   */
  @Override
  public void onDetach() {
    super.onDetach();
    getFeedsActivityCallbacks = null;
  }

  /**
   * Background task to getFeeds. Starts parsing method onComplete
   */
  @Background
  public void getFeeds(){
    newsFeedRequest = new VKRequest("newsfeed.get", VKParameters.from("filters", "post", VKApiConst.COUNT, VKREQUEST_FEEDS_COUNT, "return_banned", 0));
    newsFeedRequest.executeWithListener(new VKRequest.VKRequestListener() {
      @Override
      public void onComplete(VKResponse response) {
        super.onComplete(response);
        Log.i(TAG, "VKResponse: " + response.json.toString());
        parseVKResponse(response);
      }

      @Override
      public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
        super.attemptFailed(request, attemptNumber, totalAttempts);
      }

      @Override
      public void onError(VKError error) {
        super.onError(error);
        Log.i("Error", error.errorMessage);
      }

      @Override
      public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
        super.onProgress(progressType, bytesLoaded, bytesTotal);
      }
    });
    Log.i("newsFeedRequest", "newsFeedRequest id=" + newsFeedRequest);
  }


  /**
   * Here we parse response and send result to MainActivity
   * @param response
   */
  @Background
  public void parseVKResponse(VKResponse response){
    ArrayList<VKFeed> vkFeeds = new ArrayList<>();
    ArrayList<VKSource> vkSources = new ArrayList<>();

    try {
      JSONObject jsonResponse = response.json.getJSONObject("response");

      // Fill VKSources by profiles
      JSONArray jsonArrayOfProfiles = jsonResponse.getJSONArray("profiles");
      for (int i = 0; i < jsonArrayOfProfiles.length(); i++) {
        VKSource vkSource = new VKSource();
        // Retrieve JSON Objects
        vkSource.parse(jsonArrayOfProfiles.getJSONObject(i));
        // Add to VKSources
        vkSources.add(vkSource);
      }

      // Fill VKSources by groups
      JSONArray jsonArrayOfGroups = jsonResponse.getJSONArray("groups");
      for (int i = 0; i < jsonArrayOfGroups.length(); i++) {
        VKSource vkSource = new VKSource();
        // Retrieve JSON Objects
        vkSource.parse(jsonArrayOfGroups.getJSONObject(i));
        // Add to VKSources
        vkSources.add(vkSource);
      }


      JSONArray jsonArrayOfItems = jsonResponse.getJSONArray("items");
      for (int i = 0; i < jsonArrayOfItems.length(); i++) {
        VKFeed vkFeed = new VKFeed();
        Log.i("array", jsonArrayOfItems.getJSONObject(i).toString());
        // Retrieve JSON Objects
        vkFeed.parse(jsonArrayOfItems.getJSONObject(i));

        for(VKSource vkSource : vkSources){
          if(vkSource.getId() == Math.abs(vkFeed.source_id)){
            // need to test negative group id
            vkFeed.setSourceData(vkSource);
            break;
          }
        }

        // Set the JSON Objects into the array
        vkFeeds.add(vkFeed);
      }

    } catch (JSONException e) {
      Log.e(TAG, String.valueOf(e.getMessage()));
    }


    //When parsing is done - notify MainActivity
    if(null!=getFeedsActivityCallbacks) {
      getFeedsActivityCallbacks.onVKResponseParsed(vkFeeds);
    }
  }
}
