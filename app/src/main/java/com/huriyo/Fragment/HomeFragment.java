package com.huriyo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.huriyo.Adapter.FeedAdapter;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.Feed;
import com.huriyo.Model.PostUpdate;
import com.huriyo.R;
import com.huriyo.Ui.BussinessUsersActivity;
import com.huriyo.Ui.HomeActivity;
import com.huriyo.Ui.MainActivity;
import com.huriyo.Ui.SplashActivity;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.FragmentBase;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.LoginHandler;
import com.huriyo.Utility.Screen;
import com.huriyo.Utility.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends FragmentBase<HomeActivity> {

    RecyclerView recyclerView;
    View view;
    Feed feeds;
    FeedAdapter adapter;
    Constants.APIS currApi = Constants.APIS.FEED;
    PostUpdate postUpdate;
    SwipeRefreshLayout swipe_refresh_layout;

    Globals globals;
    String userId;
    TextView noData;
    LinearLayout llBussiness;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public Screen getCode() {
        return Screen.HOME;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        if (getArguments() != null && getArguments().containsKey(Constants.MM_user_id)) {
            userId = getArguments().getString(Constants.MM_user_id);
        }

        if(userId != null && !userId.isEmpty())
            view.findViewById(R.id.my_toolbar).setVisibility(View.GONE);

        postUpdate = new PostUpdate();
        llBussiness = (LinearLayout) view.findViewById(R.id.llBussiness);
        llBussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BussinessUsersActivity.class);
                startActivity(i);
            }
        });
        noData = (TextView) view.findViewById(R.id.noData);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        assert swipe_refresh_layout != null;

        swipe_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFeeds();
            }
        });

        feeds = new Feed();
        adapter = new FeedAdapter(getActivity(), new FeedAdapter.OnLikeUnlikePost() {
            @Override
            public void onLike(int position, Feed.Posts post, int status) {
                postUpdate.setPosition(position);
                postUpdate.setPosts(post);
                postUpdate.setPostStatus(status);
                likePost(post._id, status, post.user_id);
            }

            @Override
            public void onUnLike(int position, Feed.Posts post, int status) {
                postUpdate.setPosition(position);
                postUpdate.setPosts(post);
                postUpdate.setPostStatus(status);
                unLikePost(post._id, status);
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (globals == null) {
            globals = ((Globals) getActivity().getApplicationContext());
        }

//        if (globals == null) {
//            globals = ((Globals) getApplicationContext());
//        }
        getFeeds();
    }

    private void getFeeds() {
        if (feeds == null || feeds.posts == null || feeds.posts.size() <= 0)
            Utils.showProgressDialog(getActivity());

        JsonObject object = new JsonObject();
        try {
            if (userId != null && !userId.isEmpty())
                object.addProperty(Constants.MM_other_user_id, userId);
            else {
                object.addProperty(Constants.MM_latitude, globals.getLatitude());
                object.addProperty(Constants.MM_longitude, globals.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Globals.initRetrofit(getActivity()).getFeeds(object).enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                swipe_refresh_layout.setRefreshing(false);
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {
                    feeds = response.body();
                    if (feeds.posts == null || feeds.posts.size() <= 0) {
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        adapter.doRefresh(feeds.posts, feeds.media_base_url, feeds.user_media_base_url);
                    }
                } else if (response.body().response_code == 999) {
                    currApi = Constants.APIS.FEED;
                    refreshToken();
                } else {

                }
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                swipe_refresh_layout.setRefreshing(false);
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    private void likePost(final String postId, final int status, final String userId) {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_post_id, postId);
            object.addProperty(Constants.MM_status, status);
            object.addProperty(Constants.MM_user_id, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.canNotLike();
        Globals.initRetrofit(getActivity()).likePost(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {

                    postUpdate.getPosts().is_like = postUpdate.getPostStatus() == 1;
                    if (postUpdate.getPosts().is_like && postUpdate.getPosts().is_unlike) {
                        postUpdate.getPosts().is_unlike = false;
                        postUpdate.getPosts().total_unlikes += -1;
                    }

                    postUpdate.getPosts().total_likes += postUpdate.getPosts().is_like ? 1 : -1;

                    feeds.posts.set(postUpdate.getPosition(), postUpdate.getPosts());
                    adapter.doRefresh(feeds.posts, feeds.media_base_url, feeds.user_media_base_url);
                    postUpdate = new PostUpdate();
                } else if (response.body().response_code == 999) {
                    currApi = Constants.APIS.LIKE;
                    refreshToken();
                }
                adapter.canLike();
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                adapter.canLike();
            }
        });
    }

    private void unLikePost(final String postId, final int status) {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_post_id, postId);
            object.addProperty(Constants.MM_status, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.canNotLike();
        Globals.initRetrofit(getActivity()).unlikePost(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {

                    postUpdate.getPosts().is_unlike = postUpdate.getPostStatus() == 1;
                    if (postUpdate.getPosts().is_unlike && postUpdate.getPosts().is_like) {
                        postUpdate.getPosts().is_like = false;
                        postUpdate.getPosts().total_likes += -1;
                    }

                    postUpdate.getPosts().total_unlikes += postUpdate.getPosts().is_unlike ? 1 : -1;

                    feeds.posts.set(postUpdate.getPosition(), postUpdate.getPosts());
                    adapter.doRefresh(feeds.posts, feeds.media_base_url, feeds.user_media_base_url);
                    postUpdate = new PostUpdate();
                } else if (response.body().response_code == 999) {
                    currApi = Constants.APIS.UNLIKE;
                    refreshToken();
                }
                adapter.canLike();
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                adapter.canLike();
            }
        });
    }

    private void refreshToken() {
        new LoginHandler(getActivity(), Globals.login, new LoginHandler.LoginResponseHandler() {
            @Override
            public void onSuccess(BasicResponse response) {
                if (currApi == Constants.APIS.FEED) {
                    getFeeds();
                } else if (currApi == Constants.APIS.LIKE) {
                    likePost(postUpdate.getPosts()._id, postUpdate.getPostStatus(), postUpdate.getPosts().user_id);
                } else if (currApi == Constants.APIS.UNLIKE) {
                    unLikePost(postUpdate.getPosts()._id, postUpdate.getPostStatus());
                }
            }

            @Override
            public void onFail(String msg) {
                Utils.setPreference(getActivity(), Constants.MM_UserDate, "");
                Utils.setPreference(getActivity(), Constants.MM_token, "");
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().finish();
            }
        });
    }
}
