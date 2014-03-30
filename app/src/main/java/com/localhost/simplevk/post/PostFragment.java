package com.localhost.simplevk.post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localhost.simplevk.R;
import com.localhost.simplevk.utils.Utils;
import com.localhost.simplevk.vk.VKFeed;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_post)
public class PostFragment extends Fragment{

  VKFeed vkFeed;

  @ViewById
  protected ImageView feed_ivPostAvatar;

  @ViewById
  protected TextView feed_tvPostName;

  @ViewById
  protected TextView feed_tvPostDate;

  @ViewById
  protected TextView feed_tvPostText;

  @ViewById
  protected ImageView feed_ivPost1stPhotoAttachment;

  @ViewById
  protected LinearLayout feed_llPostPhotoAttachments;

  @ViewById
  protected TextView feed_tvPostLinkName;

  @ViewById
  protected TextView feed_tvPostLinkURL;

  @ViewById
  protected LinearLayout feed_llPostLinkAttachments;

  @ViewById
  protected TextView feed_tvPostRepostsCount;

  @ViewById
  protected TextView feed_tvPostLikesCount;

  @ViewById
  protected ImageView feed_ivPostLikeIcon;

  private static final String VKFEED_TAG = "VKFEED";


  public void setVkFeed(VKFeed vkFeed) {

    if(null == this.vkFeed) {
      this.vkFeed = vkFeed;
    }
  }

  public VKFeed getVkFeed() {
    return vkFeed;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    getActivity().setTitle(R.string.post_title);


    if(null != savedInstanceState){
      vkFeed = savedInstanceState.getParcelable(VKFEED_TAG);
    }
    fillPost(vkFeed);

//    if(null != vkFeed){
//      fillPost(vkFeed);
//    }
  }

  /**
   * Here we cache Feeds to Bundle for screen rotation etc.
   * @param outState
   */
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // Saving vkFeed into Bundle
    outState.putParcelable(VKFEED_TAG, vkFeed);
  }

  private void fillPost(VKFeed vkFeed){
    // Header

    // Set avatar from URL
    Picasso.with(getActivity()).load(vkFeed.source_avatar100URL).into(feed_ivPostAvatar);
    // Set group/user name
    feed_tvPostName.setText(vkFeed.source_name);
    // Set feed date
    Utils.setFeedDate(feed_tvPostDate, vkFeed.date);

    // Body


    // Set feed text
    Utils.setFeedText(feed_tvPostText, vkFeed.text);
    // Set attachments
    if(null != vkFeed.attachments) {
      setAttachments(vkFeed.attachments);
    }

    // Footer
    // Set feed's reposts count
    feed_tvPostRepostsCount.setText(String.valueOf(vkFeed.reposts_count));
    // Set feed's likes count
    feed_tvPostLikesCount.setText(String.valueOf(vkFeed.likes_count));
    // Set liked icon
    feed_ivPostLikeIcon.setImageResource(vkFeed.user_likes ? R.drawable.liked : R.drawable.like);

  }

  /**
   * Set and display Feed's attachments (not complete)
   * @param attachments
   */
  private void setAttachments(VKAttachments attachments){
    boolean has1stPhoto = false;
    boolean hasLink = false;
    initAttachments();

    Picasso.with(getActivity()).setDebugging(true);

    for(VKAttachments.VKApiAttachment attachment : attachments){
      switch(attachment.getType()){
        case VKAttachments.TYPE_PHOTO:
          if(!has1stPhoto){
            //Picasso.with(getActivity()).load(((VKApiPhoto)attachment).photo_604).fit().into(feed_ivPost1stPhotoAttachment);
            Picasso.with(getActivity()).load(((VKApiPhoto)attachment).photo_604).into(feed_ivPost1stPhotoAttachment);
            feed_llPostPhotoAttachments.setVisibility(View.VISIBLE);
            has1stPhoto = true;
          }
          break;
        case VKAttachments.TYPE_LINK:
          if(!hasLink) {
            if(((VKApiLink) attachment).title.equals("")) {
              feed_tvPostLinkName.setText("Ссылка");
            }else{
              feed_tvPostLinkName.setText(((VKApiLink) attachment).title);
            }
            String url = ((VKApiLink) attachment).url;
            if (url.contains("http://")) {
              url = url.replace("http://", "");
            } else if (url.contains("https://")) {
              url = url.replace("https://", "");
            }
            feed_tvPostLinkURL.setText(url);
            feed_llPostLinkAttachments.setVisibility(View.VISIBLE);
            hasLink = true;
          }
          break;
      }
    }
  }

  /**
   * We set attachments field to GONE so if view was recycled - it will be empty.
   */
  private void initAttachments(){
    feed_llPostPhotoAttachments.setVisibility(View.GONE);
    feed_llPostLinkAttachments.setVisibility(View.GONE);
  }

}
