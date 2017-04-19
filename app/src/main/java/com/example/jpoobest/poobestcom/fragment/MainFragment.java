package com.example.jpoobest.poobestcom.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jpoobest.poobestcom.R;
import com.example.jpoobest.poobestcom.activity.MainActivity;
import com.example.jpoobest.poobestcom.adapter.PhotoListAdapter;
import com.example.jpoobest.poobestcom.datatype.MutableInteger;
import com.example.jpoobest.poobestcom.manager.HttpManager;
import com.example.jpoobest.poobestcom.manager.PhotoListManager;
import com.example.jpoobest.poobestcom.view.PhotoListItem;

import java.io.File;
import java.io.IOException;

import dao.PhotoItemCollectionDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {


    //Variable
    ListView listView;
    PhotoListAdapter listAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PhotoListManager photoListManager;
    Button btnNewPhoto;
    MutableInteger lastPositionInteger;

    /**************
     * Functions
     * *************/

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize Fragment level Variables
        //model
        init(savedInstanceState);


        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState); //Restore Instances State
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        photoListManager = new PhotoListManager();
        lastPositionInteger = new MutableInteger(-1);



//        //save Log
//        SharedPreferences prefs = getContext().getSharedPreferences("dummy",
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//
//        //get
//        String value = prefs.getString("hello", null);
//
//        //add
//        editor.putString("hello", "world");
//        editor.apply();
    }


    //Variable ระดับ view
    private void initInstances(View rootView, Bundle savedInstanceState) {
        //Button new Photo
        btnNewPhoto = (Button) rootView.findViewById(R.id.btnNewPhoto);
        btnNewPhoto.setOnClickListener(buttonClickListener);
        // Init 'View' instance(s) with rootView.findViewById here
        listView = (ListView) rootView.findViewById(R.id.listView);
        //listView
        listAdapter = new PhotoListAdapter(lastPositionInteger);
        listAdapter.setDao(photoListManager.getDao());
        listView.setAdapter(listAdapter);
        //pull ro refresh
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(pullToRefreshListener);
        //config lust view ให้ pull to refresh เมื่ออยู่บนสุด
        listView.setOnScrollListener(listViewScoreListener);
        if (savedInstanceState == null)
        refreshData();

    }

    private void refreshData() {
        if (photoListManager.getCount() == 0)
            reloadData();
        else reloadDataNewer();
    }


    private void reloadDataNewer() {
        int maxId = photoListManager.getMaximumId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadPhotoListAfterId(maxId);
        call.enqueue(new PhotoListLoadCallBack(PhotoListLoadCallBack.MODE_RELOAD_NEWER));
    }

    private void reloadData() {
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoList();
        call.enqueue(new PhotoListLoadCallBack(PhotoListLoadCallBack.MODE_RELOAD));
    }

    boolean isLoadingMore = false;

    private void loadMoreData() {
        if (isLoadingMore)
            return;
        isLoadingMore = true;
        int minId = photoListManager.getMinimumId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadPhotoListBeforeId(minId);
        call.enqueue(new PhotoListLoadCallBack(PhotoListLoadCallBack.MODE_LOAD_MORE));
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        outState.putBundle("photoListManager"
                ,photoListManager.onSaveInstanceState());
        outState.putBundle("lastPositionInteger",
                lastPositionInteger.onSaveInstanceState());


    }

    private void onRestoreInstanceState(Bundle saveInstancesState){
        //Restore Instance state here
        photoListManager.onRestoreInstanceState(
                saveInstancesState.getBundle("photoListManager"));
        lastPositionInteger.onRestoreInstanceState(
                saveInstancesState.getBundle("lastPositionInteger"));
    }

    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void shoeButtonNewPhoto() {
        btnNewPhoto.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(
                getContext(),
                R.anim.zoom_fade_in
        );
        btnNewPhoto.startAnimation(anim);
    }

    private void hideButtonNewPhoto() {
        btnNewPhoto.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(
                getContext(),
                R.anim.zoom_fade_out
        );
        btnNewPhoto.startAnimation(anim);
    }

    /**********************
     * Listener Zone
     **********************/

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnNewPhoto) {
                listView.smoothScrollToPosition(0);
                hideButtonNewPhoto();
            }
        }
    };

    SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };

    AbsListView.OnScrollListener listViewScoreListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view,
                             int firstVisibleItem,
                             int visibleItemCount,
                             int totalItemCount) {
            if (view == listView) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (photoListManager.getCount() > 0) {
                        // Load more
                        loadMoreData();
                    }
                }
            }
        }
    };

    private void showToast(String text) {
        Toast.makeText(getActivity(),
                text,
                Toast.LENGTH_SHORT)
                .show();

    }

    /****************
     * Inner Class
     */

    class PhotoListLoadCallBack implements Callback<PhotoItemCollectionDao> {
        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWER = 2;
        public static final int MODE_LOAD_MORE = 3;

        int mode;

        public PhotoListLoadCallBack(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {
            swipeRefreshLayout.setRefreshing(false);
            if (response.isSuccessful()) {
                PhotoItemCollectionDao dao = response.body();

                int firstVisiblePosition = listView.getFirstVisiblePosition();
                //ตัวเลขที่เรามองเห็น
                View c = listView.getChildAt(0);
                int top = c == null ? 0 : c.getTop();

                if (mode == MODE_RELOAD_NEWER) {
                    photoListManager.insertDaoAtTopPosition(dao);
                } else if (mode == MODE_LOAD_MORE) {
                    photoListManager.appendDaoAtBottomPosition(dao);
                } else {
                    photoListManager.setDao(dao);
                }
                clearLoadingMoreFlagIfCapable(mode);
                listAdapter.setDao(photoListManager.getDao());
                listAdapter.notifyDataSetChanged();
                if (mode == MODE_RELOAD_NEWER) {
                    //Maintain Scroll Position
                    int additionalSize = (dao != null && dao.getData() != null) ? dao.getData().size() : 0;
                    listAdapter.increaseLastPosition(additionalSize);
                    listView.setSelectionFromTop(firstVisiblePosition + additionalSize,
                            top);
                    if (additionalSize > 0)
                        shoeButtonNewPhoto();
                } else {

                }
                showToast("Load Completed");
            } else {
                //Handle
                clearLoadingMoreFlagIfCapable(mode);
                try {
                    showToast(response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {
            // handle
            clearLoadingMoreFlagIfCapable(mode);
            swipeRefreshLayout.setRefreshing(false);
            showToast(t.toString());

        }

        private void clearLoadingMoreFlagIfCapable(int mode) {
            if (mode == MODE_LOAD_MORE)
                isLoadingMore = false;
        }
    }

}
