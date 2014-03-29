package com.localhost.simplevk.feed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.localhost.simplevk.vk.VKFeed;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;

@EBean
public class FeedListAdapter extends BaseAdapter{

  @RootContext
  protected Context context;

  private ArrayList<VKFeed> vkFeeds;

  @AfterInject
  protected void init() {
    vkFeeds = new ArrayList<>();
  }

  @Override
  public int getCount() {
    return vkFeeds.size();
  }

  @Override
  public VKFeed getItem(int position) {
    return vkFeeds.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    //Commented due to recycle bug, will fix later.
    // ViewHolder pattern
    FeedsItemView feedsItemView = convertView != null ?
      (FeedsItemView) convertView :
      FeedsItemView_.build(context);

    //FeedsItemView feedsItemView = FeedsItemView_.build(context);

    feedsItemView.bind(getItem(position));
    return feedsItemView;
  }

  public void setVkFeeds(ArrayList<VKFeed> vkFeeds){
    if(null != this.vkFeeds){
      this.vkFeeds.addAll(vkFeeds);
    }else{
      this.vkFeeds = vkFeeds;
    }
    notifyDataSetChanged();
  }

  /**
   * Easy hack to make list items unselectable.
   * @return
   */
  public boolean areAllItemsEnabled() {
    return false;
  }

  /**
   * Easy hack to make list items unselectable.
   * @return
   */
  public boolean isEnabled(int position) {
    return false;
  }
}
