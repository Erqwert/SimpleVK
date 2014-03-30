package com.localhost.simplevk.feed;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localhost.simplevk.R;
import com.localhost.simplevk.utils.Utils;
import com.localhost.simplevk.vk.VKFeed;
import com.localhost.simplevk.vk.VKRepost;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.feeds_item)
public class FeedsItemView extends RelativeLayout {

  Context context;

  @ViewById
  protected RelativeLayout feed_rlFeedContainer;

  @ViewById
  protected ImageView feed_ivAvatar;

  @ViewById
  protected TextView feed_tvName;

  @ViewById
  protected TextView feed_tvDate;

  @ViewById
  protected TextView feed_tvText;

  @ViewById
  protected TextView feed_tvCommentsCount;

  @ViewById
  protected TextView feed_tvRepostsCount;

  @ViewById
  protected TextView feed_tvLikesCount;

  @ViewById
  protected ImageView feed_ivLikeIcon;

  @ViewById
  protected ImageView feed_iv1stPhotoAttachment;

  @ViewById
  protected LinearLayout feed_llPhotoAttachments;

  @ViewById
  protected GridLayout feed_glPhotoAttachments;

  @ViewById
  protected LinearLayout feed_llLinkAttachments;

  @ViewById
  protected TextView feed_tvLinkName;

  @ViewById
  protected TextView feed_tvLinkURL;

  @ViewById
  protected LinearLayout feed_llFooter;

  @ViewById
  protected LinearLayout feed_llRepost;

  public FeedsItemView(Context context) {
    super(context);
    this.context=context;
  }

  /**
   * We set UI of each Feed here. Setting icons, names, texts, counts, photos, reposts etc.
   * @param vkFeed
   */
  public void bind(VKFeed vkFeed) {
    // Header

    // Set avatar from URL
    Picasso.with(context).load(vkFeed.source_avatar100URL).into(feed_ivAvatar);
    // Set group/user name
    feed_tvName.setText(vkFeed.source_name);
    // Set feed date
    Utils.setFeedDate(feed_tvDate, vkFeed.date);


    // Body


    // Set feed text
    Utils.setFeedText(feed_tvText, vkFeed.text);
    // Set attachments
    if(null != vkFeed.attachments) {
      setAttachments(vkFeed.attachments);
    }

    // Repost
    if(null != vkFeed.copy_history){
      feed_llRepost.addView(getRepostView(vkFeed.copy_history.get(0)));
      //Log.i("copies: ", vkFeed.copy_history.size()+"");
      feed_llRepost.setVisibility(VISIBLE);
    }else{
      initRepost();
    }


    // Footer

    // Set feed's comments count
    feed_tvCommentsCount.setText(String.valueOf(vkFeed.comments_count));
    // Set feed's reposts count
    feed_tvRepostsCount.setText(String.valueOf(vkFeed.reposts_count));
    // Set feed's likes count
    feed_tvLikesCount.setText(String.valueOf(vkFeed.likes_count));
    // Set liked icon
    feed_ivLikeIcon.setImageResource(vkFeed.user_likes ? R.drawable.liked : R.drawable.like);
  }

  private RepostView getRepostView(VKRepost vkRepost){
    RepostView repostView = RepostView_.build(context);

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

    Picasso.with(context).setDebugging(true);

    for(VKAttachments.VKApiAttachment attachment : attachments){
      switch(attachment.getType()){
        case VKAttachments.TYPE_PHOTO:
          if(!has1stPhoto){
            Picasso.with(context).load(((VKApiPhoto)attachment).photo_604).into(feed_iv1stPhotoAttachment);
            feed_llPhotoAttachments.setVisibility(VISIBLE);
            has1stPhoto = true;
          }
          break;
        case VKAttachments.TYPE_LINK:
          if(!hasLink) {
            if(((VKApiLink) attachment).title.equals("")) {
              feed_tvLinkName.setText("Ссылка");
            }else{
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
  private void initAttachments(){
    feed_llPhotoAttachments.setVisibility(GONE);
    feed_llLinkAttachments.setVisibility(GONE);
  }

  private void initRepost(){
    feed_llRepost.setVisibility(GONE);
  }

  private void setFeedOnClickListener(ViewGroup viewGroup){
    viewGroup.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        return;
      }
    });
  }
}
