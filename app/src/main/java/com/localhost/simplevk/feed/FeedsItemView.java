package com.localhost.simplevk.feed;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localhost.simplevk.R;
import com.localhost.simplevk.vk.VKFeed;
import com.localhost.simplevk.vk.VKLike;
import com.localhost.simplevk.vk.VKSource;
import com.squareup.picasso.Picasso;

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

  public void bind(VKFeed vkFeed) {
    VKSource vkSource = vkFeed.getVkSource();
    // Set avatar from URL
    Picasso.with(context).load(vkSource.getAvatar100URL()).into(feed_ivAvatar);
    // Set group/user name
    feed_tvName.setText(vkSource.getName());

    // Set feed date
    setFeedDate(feed_tvDate, vkFeed.getDate());
    // Set feed text
    setFeedText(feed_tvText, vkFeed.getText());
    // Set feed's comments count
    feed_tvCommentsCount.setText(String.valueOf(vkFeed.getVkComment().getComments_count()));
    // Set feed's reposts count
    feed_tvRepostsCount.setText(String.valueOf(vkFeed.getVkRepost().getReposts_count()));

    // Set feed's likes count
    VKLike vkLike = vkFeed.getVkLike();
    feed_tvLikesCount.setText(String.valueOf(vkLike.getLikes_count()));
    // Set liked icon
    feed_ivLikeIcon.setImageResource(vkLike.isLiked() ? R.drawable.liked : R.drawable.like);

  }

  private void setFeedDate(TextView textView, long unixTime){
    // Todo show smart date compared to time passed
    // Сегодня в хх хх, Вчера в хх хх, 22 марта в хх хх итд

    long dateTimeInMillis = unixTime*1000;
    int DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    // If date is today
    if(DateUtils.isToday(dateTimeInMillis)){
      Date date = new Date(dateTimeInMillis);
      textView.setText("Сегодня в "+new SimpleDateFormat("HH:mm").format(date));
    }

//    // If date is yerterday
//    Calendar c1 = Calendar.getInstance(); // today
//    c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday
//
//    Calendar c2 = Calendar.getInstance();
//    c2.setTimeInMillis(dateTimeInMillis); // date to check
//
//    if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
//      && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
//      Date date = new Date(dateTimeInMillis);
//      textView.setText("Вчера в "+new SimpleDateFormat("hh:mm").format(date));
//    }

//    long dv = unixTime*1000;
//    Date df = new Date(dv);
//    textView.setText(new SimpleDateFormat("MM dd, yyyy hh:mma").format(df));
  }

  private void setFeedText(TextView textView, String text){
    // Todo shorten text if it's too large + parse URLs
    textView.setVisibility(text.equals("") ? GONE : VISIBLE);
    textView.setText(text);
  }

}
