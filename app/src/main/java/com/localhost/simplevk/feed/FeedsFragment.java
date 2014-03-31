package com.localhost.simplevk.feed;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.localhost.simplevk.R;
import com.localhost.simplevk.utils.Utils;
import com.localhost.simplevk.vk.VKFeed;
import com.vk.sdk.VKSdk;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

@EFragment(R.layout.fragment_feed)
public class FeedsFragment extends Fragment implements OnRefreshListener{

  @ViewById
  protected PullToRefreshLayout feed_pullToRefreshLayout;

  @ViewById
  protected ListView feed_ListView;

  @Bean
  protected FeedListAdapter feedListAdapter;

  private static final String FEEDS_LIST_CONTENT_TAG = "FEEDS_LIST_CONTENT";
  private static final String FEEDS_LIST_CURRENT_POSITION_TAG = "FEEDS_LIST_CURRENT_POSITION";
  private static final String FEEDS_LIST_TOP_POSITION_TAG = "FEEDS_LIST_TOP_POSITION";
  private static final String FEEDS_LIST_NEW_FROM_TAG = "FEEDS_LIST_NEW_FROM";

  private Parcelable feeds_list;

  private FeedsTasksFragment feedsTasksFragment;
  private static final String FEED_TASKS_FRAGMENT_TAG = "FRAGMENT_FEED_TASKS";

  private FeedFragmentCallbacks feedFragmentCallbacks;

  ArrayList<VKFeed> vkFeeds;

  private boolean feeds_loading = false;
  private String new_from;
  private String new_offset;
  private int current_position = 0;
  private int top_position = 0;

  private boolean post_loading = false;

  /**
   * After user Pulls Pull-to-refresh - we launch a background task to get Feeds or
   * show toast with error if network is unavailable.
   * @param view
   */
  @Override
  public void onRefreshStarted(View view) {
    if(Utils.isNetworkAvailable(getActivity())){
      feeds_loading = true;
      current_position = 0;
      top_position = 0;
      feedListAdapter.resetVkFeeds();
      feedsTasksFragment.getFeeds(null);
    }else{
      feeds_loading = false;
      feed_pullToRefreshLayout.setRefreshComplete();
      Toast.makeText(getActivity(), "Сеть недоступна", Toast.LENGTH_SHORT).show();
    }
  }

  public static interface FeedFragmentCallbacks {
    void onLogout();
  }

  /**
   * Setting callbacks to activity
   * @param activity
   */
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    feedFragmentCallbacks = (FeedFragmentCallbacks) activity;
  }

  /**
   * Removing activity callbacks onDetach()
   */
  @Override
  public void onDetach() {
    super.onDetach();
    feedFragmentCallbacks = null;
  }

  /**
   * Here we set Logout Action Item on
   */
  @AfterViews
  protected void init(){
    setHasOptionsMenu(true);
  }

  /**
   * Here we cache Feeds to Bundle for screen rotation etc.
   * @param outState
   */
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if(null != vkFeeds) {
      // Saving state of current position and top position to Bundle
      outState.putInt(FEEDS_LIST_CURRENT_POSITION_TAG, feed_ListView.getFirstVisiblePosition());
      outState.putInt(FEEDS_LIST_TOP_POSITION_TAG, getTopPosition());

      // Saving vkFeeds list to Bundle
      vkFeeds = feedListAdapter.getFeeds();
      outState.putParcelableArrayList(FEEDS_LIST_CONTENT_TAG, vkFeeds);

      // Saving vkResponse new_from string
      outState.putString(FEEDS_LIST_NEW_FROM_TAG, new_from);
    }
  }

  /**
   * Calculates top position of the feed_ListView
   * @return feed_ListView top position
   */
  private int getTopPosition(){
    View v = feed_ListView.getChildAt(0);
    return (v == null) ? 0 : v.getTop();
  }

  /**
   * Setting retain fragment for Background tasks, initializing Pull-to-refresh and
   * load cached Feeds if available (for example after screen rotation) or initiating
   * fresh Feeds load
   * @param savedInstanceState
   */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle("Новости");

    FragmentManager fm = getActivity().getSupportFragmentManager();
    feedsTasksFragment = (FeedsTasksFragment) fm.findFragmentByTag(FEED_TASKS_FRAGMENT_TAG);

    // If the Fragment is non-null, then it is currently being
    // retained across a configuration change.
    if (feedsTasksFragment == null) {
      feedsTasksFragment = new FeedsTasksFragment_();
      fm.beginTransaction()
        .add(feedsTasksFragment, FEED_TASKS_FRAGMENT_TAG)
        .commit();

    }

    ActionBarPullToRefresh.from(getActivity())
      .allChildrenArePullable()
      .listener(this)
      .setup(feed_pullToRefreshLayout);

    if(null != savedInstanceState){
      vkFeeds = savedInstanceState.getParcelableArrayList(FEEDS_LIST_CONTENT_TAG);
      new_from = savedInstanceState.getString(FEEDS_LIST_NEW_FROM_TAG);
      current_position = savedInstanceState.getInt(FEEDS_LIST_CURRENT_POSITION_TAG);
      top_position = savedInstanceState.getInt(FEEDS_LIST_TOP_POSITION_TAG);
      feed_ListView.setAdapter(feedListAdapter);
      feedListAdapter.setVkFeeds(vkFeeds);
      feed_ListView.setSelectionFromTop(current_position, top_position);

    }else {
      initAdapter();
    }

    feed_ListView.setOnScrollListener(new AbsListView.OnScrollListener(){
      boolean isScrolling=false;

      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState != SCROLL_STATE_IDLE){
          isScrolling = true;
        }else{
          isScrolling = false;
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
                           int visibleItemCount, int totalItemCount) {

        if(isScrolling) {
          int lastInScreen = firstVisibleItem + visibleItemCount;
          if ((lastInScreen >= totalItemCount) && !(feeds_loading)) {
            feeds_loading = true;
            current_position = feed_ListView.getFirstVisiblePosition();
            top_position = getTopPosition();
            feedsTasksFragment.getFeeds(new_from);
          }
        }
      }
    });
  }

  /**
   * Starts Background task for Feeds download and display
   */
  public void initAdapter(){
    if(Utils.isNetworkAvailable(getActivity())){
      feeds_loading = true;
      feedsTasksFragment.getFeeds(new_from);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.feed_fragment_context_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  /**
   * Logout implementation. Calling MainActivity's onLogout as long as VKSdk.logout (this
   * removes access_token from SharedPreferences so we can log as another user).
   * @param item
   * @return
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.actionFeed_fragment_logout:
        if(null != feedFragmentCallbacks){
          VKSdk.logout(getActivity());
          feedFragmentCallbacks.onLogout();
        }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Updating UI by setting adapter and stopping refreshing animation.
   * @param vkFeeds
   */
  @UiThread
  public void processVKResponseParsedResult(final ArrayList<VKFeed> vkFeeds, String new_from, String new_offset){
    // Check list for null
    if(null!=vkFeeds){
      if(vkFeeds.size()!=0){
        this.vkFeeds = vkFeeds;
        this.new_from = new_from;
        this.new_offset = new_offset;
      }
    }else{
      Toast.makeText(getActivity(), "Во время обновления ленты произошла ошибка.", Toast.LENGTH_SHORT).show();
    }

    feed_ListView.setAdapter(feedListAdapter);
    feedListAdapter.setVkFeeds(vkFeeds);
    if (current_position != 0){
      feed_ListView.setSelectionFromTop(current_position, top_position);
    }

//    if(progressBar!=null){
//      lvCalc_list.removeHeaderView(progressBar);
//    }

    feeds_loading = false;

    if(feed_pullToRefreshLayout.isRefreshing()){
      feed_pullToRefreshLayout.setRefreshComplete();
    }
  }

  @ItemClick(R.id.feed_ListView)
  public void onItemClick(VKFeed vkFeed) {
//    post_loading = true;
//    String id;
//    if(vkFeed.isUserFeed){
//      id=String.valueOf(vkFeed.source_id) + "_" + String.valueOf(vkFeed.post_id);
//    }else{
//      id="-" + String.valueOf(vkFeed.source_id) + "_" + String.valueOf(vkFeed.post_id);
//    }
    current_position =  feed_ListView.getFirstVisiblePosition();
    top_position = getTopPosition();

    feedsTasksFragment.getPost(vkFeed);
  }

  public void setPosition(){
    feed_ListView.setSelectionFromTop(current_position, top_position);
  }
}
