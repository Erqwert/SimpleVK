package com.localhost.simplevk.feed;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localhost.simplevk.R;
import com.localhost.simplevk.utils.Utils;
import com.localhost.simplevk.vk.VKRepost;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.repost_item)
public class RepostView extends RelativeLayout {

  Context context;

  public RepostView(Context context) {
    super(context);
    this.context=context;
  }

  @ViewById
  protected ImageView feed_ivAvatar;

  @ViewById
  protected TextView feed_tvName;

  @ViewById
  protected TextView feed_tvDate;

  @ViewById
  protected TextView feed_tvText;

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

  /**
   * We set UI of each repost here. Setting icons, names, texts, counts, photos etc.
   * @param vkRepost
   */
  public void bind(VKRepost vkRepost) {
    // Header

    // Set avatar from URL
    Picasso.with(context).load(vkRepost.source_avatar100URL).resize(90, 90).into(feed_ivAvatar);
    // Set group/user name
    feed_tvName.setText(vkRepost.source_name);
    // Set feed date
    Utils.setFeedDate(feed_tvDate, vkRepost.date);

    // Body

    // Set feed text
    Utils.setFeedText(feed_tvText, vkRepost.text);
    // Set attachments
    if(null != vkRepost.attachments) {
      setAttachments(vkRepost.attachments);
    }


  }

  /**
   * Set and display Reposts's attachments
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
                int targetWidth = feed_llPhotoAttachments.getWidth();

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
            Picasso.with(context).load(((VKApiPhoto)attachment).photo_604).transform(transformation).into(feed_iv1stPhotoAttachment);
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
}
