package com.huriyo.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.huriyo.Adapter.SearchPeopleAdapter;
import com.huriyo.Adapter.FeedAdapter;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.Feed;
import com.huriyo.Model.FriendRequest;
import com.huriyo.Model.PostUpdate;
import com.huriyo.Model.SearchPost;
import com.huriyo.Model.SearchUser;
import com.huriyo.Model.Suggestions;
import com.huriyo.Model.UpdatePicture;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.LoginHandler;
import com.huriyo.Utility.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout llBack;
    RadioButton btnPeople, btnPost;
    RecyclerView recyclerView;
    FeedAdapter adapter;
    SearchPeopleAdapter adapter1;
    String searchText = "";
    int skip = 0, limit = 10;
    TextView noData;

    PostUpdate postUpdate;
    SearchUser.Results results;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        searchText = getIntent().getExtras().getString(Constants.MM_search);
        if (searchText == null || searchText.isEmpty())
            finish();

        postUpdate = new PostUpdate();

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(this);

        btnPeople = (RadioButton) findViewById(R.id.btnPeople);
        btnPeople.setOnClickListener(this);
        btnPost = (RadioButton) findViewById(R.id.btnPost);
        btnPost.setOnClickListener(this);

        noData = (TextView) findViewById(R.id.noData);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchResultActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter1 = new SearchPeopleAdapter(SearchResultActivity.this, new SearchPeopleAdapter.OnFriendRequestListener() {
            @Override
            public void onSendRequest(String userId, SearchUser.Results res, int pos) {
                results = res;
                position = pos;
                sendFriendRequest(userId);
            }

            @Override
            public void onAcceptRejectRequest(String userId, int status, int pos) {

            }
        });


        adapter = new FeedAdapter(SearchResultActivity.this, new FeedAdapter.OnLikeUnlikePost() {
            @Override
            public void onLike(int position, Feed.Posts post, int status) {
                postUpdate.setPosition(position);
                postUpdate.setPosts(post);
                postUpdate.setPostStatus(status);
                likePost(post._id, status, post.userInfo._id);
            }

            @Override
            public void onUnLike(int position, Feed.Posts post, int status) {
                postUpdate.setPosition(position);
                postUpdate.setPosts(post);
                postUpdate.setPostStatus(status);
                unLikePost(post._id, status);
            }
        });

        recyclerView.setAdapter(adapter1);

        searchUser();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.btnPeople:
                skip = 0;
                limit = 10;
                adapter1.doEmpty();
                recyclerView.setAdapter(adapter1);
                searchUser();
                break;
            case R.id.btnPost:
                skip = 0;
                limit = 10;
                adapter.doEmpty();
                recyclerView.setAdapter(adapter);
                searchPost();
                break;
        }
    }

    private void searchUser() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_keyword, searchText);
            object.addProperty(Constants.MM_search_type, 1);
            object.addProperty(Constants.MM_skip, skip);
            object.addProperty(Constants.MM_limit, limit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.showProgressDialog(SearchResultActivity.this);
        Globals.initRetrofit(SearchResultActivity.this).doSearchUser(object).enqueue(new Callback<SearchUser>() {
            @Override
            public void onResponse(Call<SearchUser> call, Response<SearchUser> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {
                    if (response.body().getResults() != null && response.body().getResults().size() > 0) {
                        adapter1.doRefresh(response.body().getResults(), response.body().user_media_base_url);
                        skip += 10;
                        limit += 10;
                        noData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }

                if (adapter1.getUsers() == null || adapter1.getUsers().size() <= 0) {
                    noData.setText(getString(R.string.no_user));
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<SearchUser> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
            }
        });
    }

    private void searchPost() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_keyword, searchText);
            object.addProperty(Constants.MM_search_type, 2);
            object.addProperty(Constants.MM_skip, skip);
            object.addProperty(Constants.MM_limit, limit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.showProgressDialog(SearchResultActivity.this);

        Globals.initRetrofit(SearchResultActivity.this).doSearchPost(object).enqueue(new Callback<SearchPost>() {
            @Override
            public void onResponse(Call<SearchPost> call, Response<SearchPost> response) {
                Utils.closeProgressDialog();

                if (response.body().response_code == 200) {
                    if (response.body().getResults() != null && response.body().getResults().size() > 0) {
                        adapter.doRefreshAll(response.body().getResults(), response.body().media_base_url, response.body().user_media_base_url);
                        skip += 10;
                        limit += 10;
                        noData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                if (adapter.getPosts() == null || adapter.getPosts().size() <= 0) {
                    noData.setText(getString(R.string.no_post));
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<SearchPost> call, Throwable t) {
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
        Globals.initRetrofit(SearchResultActivity.this).likePost(object).enqueue(new Callback<BasicResponse>() {
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

                    adapter.getPosts().set(postUpdate.getPosition(), postUpdate.getPosts());
                    adapter.doRefresh(adapter.getPosts(), adapter.getImageUrl(), adapter.getProfileUrl());
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
        Globals.initRetrofit(SearchResultActivity.this).unlikePost(object).enqueue(new Callback<BasicResponse>() {
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

                    adapter.getPosts().set(postUpdate.getPosition(), postUpdate.getPosts());
                    adapter.doRefresh(adapter.getPosts(), adapter.getImageUrl(), adapter.getProfileUrl());
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

    Constants.APIS currApi;

    private void refreshToken() {
        new LoginHandler(SearchResultActivity.this, Globals.login, new LoginHandler.LoginResponseHandler() {
            @Override
            public void onSuccess(BasicResponse response) {
                if (currApi == Constants.APIS.LIKE) {
                    likePost(postUpdate.getPosts()._id, postUpdate.getPostStatus(), postUpdate.getPosts().user_id);
                } else if (currApi == Constants.APIS.UNLIKE) {
                    unLikePost(postUpdate.getPosts()._id, postUpdate.getPostStatus());
                }
            }

            @Override
            public void onFail(String msg) {
                Utils.setPreference(SearchResultActivity.this, Constants.MM_UserDate, "");
                Utils.setPreference(SearchResultActivity.this, Constants.MM_token, "");
                Intent i = new Intent(SearchResultActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }


    private void sendFriendRequest(final String userId) {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_request_user_id, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(SearchResultActivity.this);
        Globals.initRetrofit(SearchResultActivity.this).sendFriendRequest(object).enqueue(new Callback<FriendRequest>() {
            @Override
            public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    results.setRequest_status(2);
                    adapter1.doRefreshItem(results, position);
                    Toast.makeText(SearchResultActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchResultActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FriendRequest> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(SearchResultActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
