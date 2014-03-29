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

  private static final String FEED_BUNDLE_TAG = "FEED_BUNDLE";
  private static final String FEEDS_LIST_TAG = "FEEDS_LIST";

  private Parcelable feeds_list;

  private FeedsTasksFragment feedsTasksFragment;
  private static final String FEED_TASKS_FRAGMENT_TAG = "FRAGMENT_FEED_TASKS";

  private FeedFragmentCallbacks feedFragmentCallbacks;

  ArrayList<VKFeed> vkFeeds;

  private boolean loading = false;
  private String new_from;
  private int current_position = 0;
  private int top_position = 0;

  /**
   * After user Pulls Pull-to-refresh - we launch a background task to get Feeds or
   * show toast with error if network is unavailable.
   * @param view
   */
  @Override
  public void onRefreshStarted(View view) {
    if(Utils.isNetworkAvailable(getActivity())){
      loading = true;
      current_position = 0;
      top_position = 0;
      feedsTasksFragment.getFeeds(null);
    }else{
      loading = false;
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
    //feeds_list = feed_ListView.onSaveInstanceState();
    int position = feed_ListView.getFirstVisiblePosition();
    outState.putInt(FEEDS_LIST_TAG, position);
    //outState.putParcelable(FEEDS_LIST_TAG, feeds_list);
    outState.putParcelableArrayList(FEED_BUNDLE_TAG, vkFeeds);
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
      vkFeeds = savedInstanceState.getParcelableArrayList(FEED_BUNDLE_TAG);
      feed_ListView.setAdapter(feedListAdapter);
      feedListAdapter.setVkFeeds(vkFeeds);
      feed_ListView.setSelectionFromTop(savedInstanceState.getInt(FEEDS_LIST_TAG), 0);
    }else {
      initAdapter();
    }

    feed_ListView.setOnScrollListener(new AbsListView.OnScrollListener(){

      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {}

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
                           int visibleItemCount, int totalItemCount) {

        int lastInScreen = firstVisibleItem + visibleItemCount;
        if((lastInScreen == totalItemCount) && !(loading)){
          current_position = feed_ListView.getFirstVisiblePosition();
          View v = feed_ListView.getChildAt(0);
          top_position = (v == null) ? 0 : v.getTop();
          feedsTasksFragment.getFeeds(new_from);
          loading = true;
        }
      }
    });
  }

  /**
   * Starts Background task for Feeds download and display
   */
  public void initAdapter(){
    if(Utils.isNetworkAvailable(getActivity())){
      loading = true;
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
  public void processVKResponseParsedResult(final ArrayList<VKFeed> vkFeeds, String new_from){
    // Check list for null
    if(null!=vkFeeds){
      if(vkFeeds.size()!=0){
        this.vkFeeds = vkFeeds;
        this.new_from = new_from;
      }
    }else{
      Toast.makeText(getActivity(), "Во время обновления ленты произошла ошибка.", Toast.LENGTH_SHORT).show();
    }

    feed_ListView.setAdapter(feedListAdapter);
    feedListAdapter.setVkFeeds(vkFeeds);
    if (current_position != 0){
      feed_ListView.setSelectionFromTop(current_position+1, top_position);
    }

//    if(progressBar!=null){
//      lvCalc_list.removeHeaderView(progressBar);
//    }

    loading = false;

    if(feed_pullToRefreshLayout.isRefreshing()){
      feed_pullToRefreshLayout.setRefreshComplete();
    }
  }
}
