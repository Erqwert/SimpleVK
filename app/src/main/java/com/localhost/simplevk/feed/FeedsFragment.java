package com.localhost.simplevk.feed;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

  private FeedsTasksFragment feedsTasksFragment;
  private static final String FEED_TASKS_FRAGMENT_TAG = "FRAGMENT_FEED_TASKS";

  private FeedFragmentCallbacks feedFragmentCallbacks;

  ArrayList<VKFeed> vkFeeds;

  @Override
  public void onRefreshStarted(View view) {
    if(Utils.isNetworkAvailable(getActivity())){
      feedsTasksFragment.getFeeds();
    }else{
      feed_pullToRefreshLayout.setRefreshComplete();
      Toast.makeText(getActivity(), "Сеть недоступна", Toast.LENGTH_SHORT).show();
    }
  }

  public static interface FeedFragmentCallbacks {
    void onLogout();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    feedFragmentCallbacks = (FeedFragmentCallbacks) activity;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    feedFragmentCallbacks = null;
  }

  @AfterViews
  protected void init(){
    setHasOptionsMenu(true);
    //todo init list view (load cached data if exists)
  }



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
      Log.i("boom", "PoliciesTask fragment created.");
    }else{
      Log.i("boom", "PoliciesTask fragment retained.");
    }

    ActionBarPullToRefresh.from(getActivity())
      .allChildrenArePullable()
      .listener(this)
      .setup(feed_pullToRefreshLayout);

    initAdapter();
  }

  public void initAdapter(){
    if(Utils.isNetworkAvailable(getActivity()) /* todo add cache check !cacheNotAvailable */){
      feedsTasksFragment.getFeeds();
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.feed_fragment_context_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

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

  @UiThread
  public void processVKResponseParsedResult(ArrayList<VKFeed> vkFeeds){
    // Check list for null
    if(null!=vkFeeds){
      if(vkFeeds.size()!=0){
        this.vkFeeds = vkFeeds;
      }
    }else{
      Toast.makeText(getActivity(), "Во время обновления ленты произошла ошибка.", Toast.LENGTH_SHORT).show();
    }

    feed_ListView.setAdapter(feedListAdapter);
    feedListAdapter.setVkFeeds(vkFeeds);

//    // todo Cache list to userdata
//    if(null!=getActivity() && null!=items){
//      userdata.saveCalculationsList(getActivity(), items);
//    }

//    if(progressBar!=null){
//      lvCalc_list.removeHeaderView(progressBar);
//    }

    if(feed_pullToRefreshLayout.isRefreshing()){
      feed_pullToRefreshLayout.setRefreshComplete();
    }
  }
}
