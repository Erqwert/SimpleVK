package com.localhost.simplevk.feed;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.localhost.simplevk.profile.Account;
import com.localhost.simplevk.vk.VKComment;
import com.localhost.simplevk.vk.VKFeed;
import com.localhost.simplevk.vk.VKLike;
import com.localhost.simplevk.vk.VKRepost;
import com.localhost.simplevk.vk.VKSource;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@EFragment
public class FeedsTasksFragment extends Fragment{

  @Bean
  protected Account user;

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

  @Override
  public void onDetach() {
    super.onDetach();
    getFeedsActivityCallbacks = null;
  }

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

  @Background
  public void parseVKResponse(VKResponse response){
    ArrayList<VKFeed> vkFeeds = new ArrayList<>();
    ArrayList<VKSource> vkSources = new ArrayList<>();

    try {

//                    /*myImage.setImageUrl(jArray.getJSONObject(PHOTO_POSITION).getString("photo_604"));
//                    myText.setText(jArray.getJSONObject(PHOTO_POSITION).getString("text"));*/

      JSONObject jsonResponse = response.json.getJSONObject("response");

      // Fill VKSources by profiles
      JSONArray jsonArrayOfProfiles = jsonResponse.getJSONArray("profiles");
      for (int i = 0; i < jsonArrayOfProfiles.length(); i++) {
        VKSource vkSource = new VKSource();
        // Retrieve JSON Objects
        vkSource.setId(jsonArrayOfProfiles.getJSONObject(i).getInt("id"));
        Log.i("id: ", vkSource.getId()+"");
        vkSource.setName(jsonArrayOfProfiles.getJSONObject(i).getString("first_name") + " " + jsonArrayOfProfiles.getJSONObject(i).getString("last_name"));
        Log.i("name: ", vkSource.getName());
        vkSource.setAvatar50URL(jsonArrayOfProfiles.getJSONObject(i).getString("photo_50"));
        Log.i("photo_50: ", vkSource.getAvatar50URL());
        vkSource.setAvatar100URL(jsonArrayOfProfiles.getJSONObject(i).getString("photo_100"));
        Log.i("photo_100: ", vkSource.getAvatar100URL());
        // Add to VKSources
        vkSources.add(vkSource);
      }

      // Fill VKSources by groups
      JSONArray jsonArrayOfGroups = jsonResponse.getJSONArray("groups");
      for (int i = 0; i < jsonArrayOfGroups.length(); i++) {
        VKSource vkSource = new VKSource();
        // Retrieve JSON Objects
        vkSource.setId(jsonArrayOfGroups.getJSONObject(i).getInt("id"));
        Log.i("id: ", vkSource.getId()+"");
        vkSource.setName(jsonArrayOfGroups.getJSONObject(i).getString("name"));
        Log.i("name: ", vkSource.getName());
        vkSource.setAvatar50URL(jsonArrayOfGroups.getJSONObject(i).getString("photo_50"));
        Log.i("photo_50: ", vkSource.getAvatar50URL());
        vkSource.setAvatar100URL(jsonArrayOfGroups.getJSONObject(i).getString("photo_100"));
        Log.i("photo_100: ", vkSource.getAvatar100URL());
        // Add to VKSources
        vkSources.add(vkSource);
      }

      JSONArray jsonArrayOfItems = jsonResponse.getJSONArray("items");
      for (int i = 0; i < jsonArrayOfItems.length(); i++) {
        VKFeed VKFeed = new VKFeed();
        Log.i("array", jsonArrayOfItems.toString());
        // Retrieve JSON Objects

        // идентификатор источника новости (положительный — новость пользователя, отрицательный — новость группы);
        int source_id = jsonArrayOfItems.getJSONObject(i).getInt("source_id");
        for(VKSource vkSource : vkSources){
          if(vkSource.getId() == Math.abs(source_id)){
            // need to test negative group id
            VKFeed.setVkSource(vkSource);
            break;
          }
        }

        // время публикации новости в формате unixtime;
        // "date":1395529898
        VKFeed.setDate(jsonArrayOfItems.getJSONObject(i).getLong("date"));
        Log.i("date: ", VKFeed.getDate()+"");

        // находится в записях со стен и содержит идентификатор записи на стене владельца;
        //"post_id":112,
        VKFeed.setPost_id(jsonArrayOfItems.getJSONObject(i).getInt("post_id"));
        Log.i("post_id: ", VKFeed.getPost_id()+"");

        // находится в записях со стен и содержит текст записи;
        VKFeed.setText(jsonArrayOfItems.getJSONObject(i).getString("text"));
        Log.i("text: ", VKFeed.getText());

        //  находится в записях со стен и содержит информацию о комментариях к записи
        // "comments":{"count":0,"can_post":1}}
        VKComment vkComment = new VKComment();
        vkComment.setComments_count(jsonArrayOfItems.getJSONObject(i).getJSONObject("comments").getInt("count"));
        vkComment.setCan_comment(jsonArrayOfItems.getJSONObject(i).getJSONObject("comments").getInt("can_post") == 1);
        VKFeed.setVkComment(vkComment);
        Log.i("comments", "comments_count: "+vkComment.getComments_count()+", can_comment: "+vkComment.isCan_comment());

        // "likes":{"can_publish":1,"can_like":1,"user_likes":0,"count":0}
        VKLike vkLike = new VKLike();
        vkLike.setLikes_count(jsonArrayOfItems.getJSONObject(i).getJSONObject("likes").getInt("count"));
        vkLike.setLiked(jsonArrayOfItems.getJSONObject(i).getJSONObject("likes").getInt("user_likes")==1);
        vkLike.setCan_like(jsonArrayOfItems.getJSONObject(i).getJSONObject("likes").getInt("can_like")==1);
        vkLike.setCan_repost(jsonArrayOfItems.getJSONObject(i).getJSONObject("likes").getInt("can_publish")==1);
        VKFeed.setVkLike(vkLike);
        Log.i("likes ", "likes_count: " + vkLike.getLikes_count() + " liked: " + vkLike.isLiked());

        // "reposts":{"count":0,"user_reposted":0}
        VKRepost vkRepost = new VKRepost();
        vkRepost.setReposts_count(jsonArrayOfItems.getJSONObject(i).getJSONObject("reposts").getInt("count"));
        vkRepost.setReposted(jsonArrayOfItems.getJSONObject(i).getJSONObject("reposts").getInt("user_reposted") == 1);
        VKFeed.setVkRepost(vkRepost);
        Log.i("reposts", "count: "+vkRepost.getReposts_count()+" , reposted: "+vkRepost.isReposted());

        // copy_history - ARRAY!
        if(jsonArrayOfItems.getJSONObject(i).has("copy_history")) {
          Log.i("copy_history: ", jsonArrayOfItems.getJSONObject(i).getString("copy_history"));
        }

        //Log.i("attachments: ", jsonArrayOfItems.getJSONObject(i).getString("attachments"));

        // todo copy wtf?
        // находится в записях со стен, содержит тип новости (post или copy);
        // "post_type":"post"
        //Log.i("post_type: ", jsonArrayOfItems.getJSONObject(i).getString("post_type"));

//        Log.i("copy_owner_id: ", jsonArrayOfItems.getJSONObject(i).getString("copy_owner_id"));
//        Log.i("copy_post_id: ", jsonArrayOfItems.getJSONObject(i).getString("copy_post_id"));
//        Log.i("copy_post_date: ", jsonArrayOfItems.getJSONObject(i).getString("copy_post_date"));

        // Set the JSON Objects into the array
        vkFeeds.add(VKFeed);
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
