package com.localhost.simplevk.post;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localhost.simplevk.R;
import com.localhost.simplevk.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;

@EViewGroup(R.layout.comment_item)
public class CommentView extends RelativeLayout {
  Context context;

  VKRequest getUserRequest;

  public CommentView(Context context) {
    super(context);
  }

  @ViewById
  protected ImageView post_ivCommenterAvatar;

  @ViewById
  protected TextView post_tvCommenterName;

  @ViewById
  protected TextView post_tvCommentDate;

  @ViewById
  protected TextView post_tvCommentText;

  @ViewById
  protected TextView post_tvCommentLikesCount;

  @ViewById
  protected ImageView feed_ivPostLikeIcon;

  @ViewById
  protected LinearLayout feed_llPhotoAttachments;

  @ViewById
  protected LinearLayout feed_llLinkAttachments;

  @ViewById
  protected ImageView feed_iv1stPhotoAttachment;

  @ViewById
  protected TextView feed_tvLinkName;

  @ViewById
  protected TextView feed_tvLinkURL;

  /**
   * We set UI of each VKApiComment. Setting icons, names, texts, counts, photos etc.
   *
   * @param vkComment
   */
  public void bind(VKApiComment vkComment) {

    // Header
    getAvatarAndName(vkComment.from_id);

    // Body

    // Set feed text
    Utils.setFeedText(post_tvCommentText, vkComment.text);
    // Set attachments
    if (null != vkComment.attachments) {
      setAttachments(vkComment.attachments);
    }


    // Footer
    // Set feed date
    Utils.setFeedDate(post_tvCommentDate, vkComment.date);
    // Set feed's likes count
    post_tvCommentLikesCount.setText(String.valueOf(vkComment.likes));
    // Set liked icon
    feed_ivPostLikeIcon.setImageResource(vkComment.user_likes ? R.drawable.liked : R.drawable.like);
  }

  /**
   * Set and display Comments's attachments
   *
   * @param attachments
   */
  private void setAttachments(VKAttachments attachments) {
    boolean has1stPhoto = false;
    boolean hasLink = false;
    initAttachments();

    for (VKAttachments.VKApiAttachment attachment : attachments) {
      switch (attachment.getType()) {
        case VKAttachments.TYPE_PHOTO:
          if (!has1stPhoto) {
            Transformation transformation = new Transformation() {

              @Override
              public Bitmap transform(Bitmap source) {
                int targetWidth = feed_llPhotoAttachments.getWidth();

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                if (source.getHeight() >= source.getWidth()) {
                  return source;
                }
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                  // Same bitmap is returned if sizes are the same
                  source.recycle();
                }
                return result;
              }

              @Override
              public String key() {
                return "transformation" + " desiredWidth";
              }
            };
            Picasso.with(context).load(((VKApiPhoto) attachment).photo_604).transform(transformation).into(feed_iv1stPhotoAttachment);
            feed_llPhotoAttachments.setVisibility(VISIBLE);
            has1stPhoto = true;
          }
          break;
        case VKAttachments.TYPE_LINK:
          if (!hasLink) {
            if (((VKApiLink) attachment).title.equals("")) {
              feed_tvLinkName.setText("Ссылка");
            } else {
              feed_tvLinkName.setText(((VKApiLink) attachment).title);
            }
            String url = ((VKApiLink) attachment).url;
            if (url.contains("http://")) {
              url = url.replace("http://", "");
            } else if (url.contains("https://")) {
              url = url.replace("https://", "");
            }
            feed_tvLinkURL.setText(url);
            feed_llLinkAttachments.setVisibility(VISIBLE);
            hasLink = true;
          }
          break;
      }
    }
  }

  /**
   * We set attachments field to GONE so if view was recycled - it will be empty.
   */
  private void initAttachments() {
    feed_llPhotoAttachments.setVisibility(GONE);
    feed_llLinkAttachments.setVisibility(GONE);
  }


  /**
   * Background method which loads user data and parse it onComplete
   *
   * @param id
   */
  @Background
  public void getAvatarAndName(int id) {
    getUserRequest = new VKRequest("users.get", VKParameters.from("user_ids", String.valueOf(id), "fields", "photo_100"));
    getUserRequest.executeWithListener(new VKRequest.VKRequestListener() {
      @Override
      public void onComplete(VKResponse response) {
        super.onComplete(response);
        Log.i("getAvatarAndName", "VKResponse: " + response.json.toString());
        parseGetUserResponse(response);
      }

      @Override
      public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
        super.attemptFailed(request, attemptNumber, totalAttempts);
      }

      @Override
      public void onError(VKError error) {
        super.onError(error);
        //Log.i("Error", error.errorReason);
        //Log.i("Error", error.errorMessage);
      }

      @Override
      public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
        super.onProgress(progressType, bytesLoaded, bytesTotal);
      }
    });
  }

  /**
   * Method parses VKResponse from get user data and updates Comments UI
   * (avatars and names)
   *
   * @param response
   */
  public void parseGetUserResponse(VKResponse response) {
    try {
      JSONArray jsonArrayOfUsers = response.json.getJSONArray("response");

      String last_name = jsonArrayOfUsers.getJSONObject(0).optString("last_name");
      String first_name = jsonArrayOfUsers.getJSONObject(0).optString("first_name");
      post_tvCommenterName.setText(first_name + " " + last_name);

      String URL = jsonArrayOfUsers.getJSONObject(0).optString("photo_100");
      Picasso.with(context).load(URL).into(post_ivCommenterAvatar);

    } catch (JSONException e) {
      Log.e("getAvatarAndName", String.valueOf(e.getMessage()));
    }
  }
}
