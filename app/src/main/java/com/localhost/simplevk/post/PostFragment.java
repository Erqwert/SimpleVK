package com.localhost.simplevk.post;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localhost.simplevk.R;
import com.localhost.simplevk.feed.FeedsTasksFragment;
import com.localhost.simplevk.feed.RepostView;
import com.localhost.simplevk.feed.RepostView_;
import com.localhost.simplevk.utils.Utils;
import com.localhost.simplevk.vk.VKFeed;
import com.localhost.simplevk.vk.VKRepost;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(R.layout.fragment_post)
public class PostFragment extends Fragment{

  VKFeed vkFeed;
  ArrayList<VKApiComment> vkComments;

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

  @ViewById
  protected LinearLayout feed_llPostRepost;

  @ViewById
  protected LinearLayout post_llCommentsContainer;

  private static final String VKFEED_TAG = "VKFEED";
  private static final String VKCOMMENTS_TAG = "VKCOMMENTS";

  private FeedsTasksFragment feedsTasksFragment;
  private static final String FEED_TASKS_FRAGMENT_TAG = "FRAGMENT_FEED_TASKS";


  public void setVkFeed(VKFeed vkFeed) {

    if(null == this.vkFeed) {
      this.vkFeed = vkFeed;
    }
  }

  /**
   * Restoring savedInstanceState and start fillPost method.
   * @param savedInstanceState
   */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    getActivity().setTitle(R.string.post_title);

    FragmentManager fm = getActivity().getSupportFragmentManager();
    feedsTasksFragment = (FeedsTasksFragment) fm.findFragmentByTag(FEED_TASKS_FRAGMENT_TAG);

    if(null != savedInstanceState){
      vkFeed = savedInstanceState.getParcelable(VKFEED_TAG);
      vkComments = savedInstanceState.getParcelableArrayList(VKCOMMENTS_TAG);
    }

    if(null != vkFeed){
      fillPost(vkFeed);
    }
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
    outState.putParcelableArrayList(VKCOMMENTS_TAG, vkComments);
  }

  /**
   * Method updates Post UI (pictures, text, icons, counts etc.
   * @param vkFeed
   */
  @UiThread
  protected void fillPost(VKFeed vkFeed){
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

    // Repost
    if(null != vkFeed.copy_history){
      feed_llPostRepost.addView(getRepostView(vkFeed.copy_history.get(0)));
      //Log.i("copies: ", vkFeed.copy_history.size()+"");
      feed_llPostRepost.setVisibility(View.VISIBLE);
    }else{
      initRepost();
    }

    // Footer
    // Set feed's reposts count
    feed_tvPostRepostsCount.setText(String.valueOf(vkFeed.reposts_count));
    // Set feed's likes count
    feed_tvPostLikesCount.setText(String.valueOf(vkFeed.likes_count));
    // Set liked icon
    feed_ivPostLikeIcon.setImageResource(vkFeed.user_likes ? R.drawable.liked : R.drawable.like);

    if(null != vkComments) {
      setComments(vkComments);
    }else {
      feedsTasksFragment.getComments(vkFeed);
    }

  }

  /**
   * Updating UI by setting adapter and stopping refreshing animation.
   * @param vkComments
   */
  @UiThread
  public void processCommentsParsedResult(final ArrayList<VKApiComment> vkComments){
    if(null != vkComments){
      this.vkComments = vkComments;
    }
    setComments(vkComments);
  }

  /**
   * Adding new comment View for each comment
   * @param vkComments
   */
  private void setComments(ArrayList<VKApiComment> vkComments){
    if(null != vkComments && vkComments.size()>0){
      post_llCommentsContainer.setVisibility(View.VISIBLE);
      int i = 0;
      for(VKApiComment comment : vkComments){
        post_llCommentsContainer.addView(getCommentView(comment));
        i++;
        if(i>7){
          break;
        }
      }
//      for (int i = 0; i < 7; i++) {
//        post_llCommentsContainer.addView(getCommentView(vkComments.get(i)));
//      }
    }
  }

  /**
   * Constructor for CommentView.
   * @param vkComment
   * @return CommentView filled by data.
   */
  private CommentView getCommentView(VKApiComment vkComment){
    CommentView commentView = CommentView_.build(getActivity());

    commentView.bind(vkComment);
    return commentView;
  }

  /**
   * Constructor for RepostView.
   * @param vkRepost
   * @return RepostView filled by data.
   */
  private RepostView getRepostView(VKRepost vkRepost){
    RepostView repostView = RepostView_.build(getActivity());

    repostView.bind(vkRepost);
    return repostView;
  }


  /**
   * Set and display Feed's attachments (not complete)
   * @param attachments
   */
  private void setAttachments(VKAttachments attachments){
    boolean has1stPhoto = false;
    boolean hasLink = false;
    initAttachments();

    for(VKAttachments.VKApiAttachment attachment : attachments){
      switch(attachment.getType()){
        case VKAttachments.TYPE_PHOTO:
          if(!has1stPhoto){

            Transformation transformation = new Transformation() {

              @Override public Bitmap transform(Bitmap source) {
                int targetWidth = feed_llPostPhotoAttachments.getWidth();

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                if(source.getHeight() >= source.getWidth()){
                  return source;
                }
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                  // Same bitmap is returned if sizes are the same
                  source.recycle();
                }
                return result;
              }

              @Override public String key() {
                return "transformation" + " desiredWidth";
              }
            };

            Picasso.with(getActivity()).load(((VKApiPhoto)attachment).photo_604).transform(transformation).into(feed_ivPost1stPhotoAttachment);

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

  /**
   * We set repost field to GONE so if view was recycled - it will be empty.
   */
  private void initRepost(){
    feed_llPostRepost.setVisibility(View.GONE);
  }


}
