package com.localhost.simplevk.feed;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localhost.simplevk.R;
import com.localhost.simplevk.vk.VKFeed;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;

@EViewGroup(R.layout.feeds_item)
public class FeedsItemView extends RelativeLayout {

  Context context;

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

  public FeedsItemView(Context context) {
    super(context);
    this.context=context;
  }

  public FeedsItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context=context;
  }

  public FeedsItemView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.context=context;
  }

  /**
   * We set UI of each Feed here. Setting icons, names, texts, counts, photos etc.
   * @param vkFeed
   */
  public void bind(VKFeed vkFeed) {
    // Header

    // Set avatar from URL
    Picasso.with(context).load(vkFeed.source_avatar100URL).into(feed_ivAvatar);
    // Set group/user name
    feed_tvName.setText(vkFeed.source_name);


    // Body

    // Set feed date
    setFeedDate(feed_tvDate, vkFeed.date);
    // Set feed text
    setFeedText(feed_tvText, vkFeed.text);
    // Set attachments
    if(null != vkFeed.attachments) {
      setAttachments(vkFeed.attachments);
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

  /**
   * Prints date in VKFeed or VKPost
   * @param textView
   * @param unixTime
   */
  private void setFeedDate(TextView textView, long unixTime){
    long dateTimeInMillis = unixTime*1000;

    Date date = new Date(dateTimeInMillis);
    if(DateUtils.isToday(dateTimeInMillis)){
      textView.setText("Сегодня в "+new SimpleDateFormat("HH:mm").format(date));
    }else{
      textView.setText(new SimpleDateFormat("dd MMMM").format(date) + " в " + new SimpleDateFormat("HH:mm").format(date));
    }
  }

  /**
   * Sets Feed's body text and make it VISIBLE if needed
   * @param textView
   * @param text
   */
  private void setFeedText(TextView textView, String text){
    // Todo shorten text if it's too large
    textView.setVisibility(text.equals("") ? GONE : VISIBLE);
    textView.setText(text);
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
            feed_tvLinkName.setText(((VKApiLink) attachment).title);
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
}
