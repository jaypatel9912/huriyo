package com.huriyo.Ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Adapter.CommentAdapter;
import com.huriyo.Model.CommentRes;
import com.huriyo.Model.CommentUpdate;
import com.huriyo.Model.PostWithComments;
import com.huriyo.Model.User;
import com.huriyo.R;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.LoginHandler;
import com.huriyo.Utility.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import cn.jzvd.JZVideoPlayerStandard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FeedDescriptionActivity extends BaseActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    EditText edComment;
    LinearLayout llBack, llAddComment;
    CommentAdapter adapter;
    NestedScrollView scrollView;
    TextView text, name, llComment, time, likes, unlikes, username, tvComments;
    PostWithComments.Post post;
    ImageView img;
    JZVideoPlayerStandard jzVideoPlayerStandard;
    ProgressBar pb;
    String imageUrl;
    ArrayList<PostWithComments.Comments> commentsArrayList;
    User user;
    ImageView ivUnlike, ivLike;
    CircleImageView ivProfile;
    Constants.APIS currApi = Constants.APIS.FEED_DESCRIPTION;
    CommentUpdate commentUpdate;
    String postId = null, profileUrl;
    boolean canLikePost = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        user = new Gson().fromJson(Utils.getPreference(FeedDescriptionActivity.this, Constants.MM_UserDate), User.class);

        commentsArrayList = new ArrayList<>();

        if (getIntent() == null || getIntent().getExtras() == null || !getIntent().getExtras().containsKey(Constants.MM_Feed))
            finish();

        postId = getIntent().getExtras().getString(Constants.MM_Feed);
        imageUrl = getIntent().getExtras().getString(Constants.MM_image_url);
        if (imageUrl == null || imageUrl.isEmpty())
            imageUrl = "http://www.huriyo.com:7777/uploads/post_media";
        if (postId == null)
            finish();

        Typeface tfRegular = Utils.getTypefaceNormal(FeedDescriptionActivity.this);
        Typeface tfBold = Utils.getTypefaceBold(FeedDescriptionActivity.this);

        img = (ImageView) findViewById(R.id.img);
        text = (TextView) findViewById(R.id.text);
        text.setTypeface(tfRegular);
        jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        username = (TextView) findViewById(R.id.username);
        username.setTypeface(tfBold);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedDescriptionActivity.this, ProfileActivity.class);
                intent.putExtra(Constants.MM_user_id, post.userInfo._id);
                startActivity(intent);
            }
        });
        time = (TextView) findViewById(R.id.time);
        time.setTypeface(tfBold);
        pb = (ProgressBar) findViewById(R.id.pb);
        likes = (TextView) findViewById(R.id.likes);
        likes.setTypeface(tfRegular);
        unlikes = (TextView) findViewById(R.id.unlikes);
        unlikes.setTypeface(tfRegular);
        tvComments = (TextView) findViewById(R.id.comments);
        tvComments.setTypeface(tfRegular);

        ivUnlike = (ImageView) findViewById(R.id.ivUnlike);
        ivLike = (ImageView) findViewById(R.id.ivLike);
        ivProfile = (CircleImageView) findViewById(R.id.ivProfile);

        llComment = (TextView) findViewById(R.id.llComment);
        llComment.setTypeface(tfBold);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);

        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        llAddComment = (LinearLayout) findViewById(R.id.llAddComment);
        llAddComment.setOnClickListener(this);
        edComment = (EditText) findViewById(R.id.edComment);
        commentUpdate = new CommentUpdate();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FeedDescriptionActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedDescriptionActivity.this, ImageCropActivity.class);
                intent.putExtra(Constants.MM_Data, new Gson().toJson(post.post_media));
                intent.putExtra(Constants.MM_view, true);
                intent.putExtra(Constants.MM_imagepath, imageUrl);
                startActivity(intent);

            }
        });

        adapter = new CommentAdapter(FeedDescriptionActivity.this, new CommentAdapter.OnLikeUnlikeComment() {
            @Override
            public void onLike(int position, PostWithComments.Comments comments, int status) {
                commentUpdate.setPosition(position);
                commentUpdate.setComment(comments);
                commentUpdate.setCommentStatus(status);
                likeComment(comments._id, status, comments.userInfo._id);
            }

            @Override
            public void onUnLike(int position, PostWithComments.Comments comments, int status) {
                commentUpdate.setPosition(position);
                commentUpdate.setComment(comments);
                commentUpdate.setCommentStatus(status);
                unLikeComment(comments._id, status);
            }

            @Override
            public void onCommentDelete(int position, PostWithComments.Comments comments) {
                commentUpdate.setPosition(position);
                commentUpdate.setComment(comments);
                deleteComment();
            }
        });
        recyclerView.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 500);

        getPostWithComments();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.llAddComment:
                Utils.hideSoftKeyboard(FeedDescriptionActivity.this);
                if (edComment.getText().toString().trim().isEmpty()) {
                    edComment.setError("Please enter comment");
                    return;
                }
                postComment();
                break;
        }
    }

    private void getPostWithComments() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_post_id, postId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(FeedDescriptionActivity.this);
        Globals.initRetrofit(FeedDescriptionActivity.this).getPostWithComments(object).enqueue(new Callback<PostWithComments>() {
            @Override
            public void onResponse(Call<PostWithComments> call, Response<PostWithComments> response) {
                Utils.closeProgressDialog();
                if (!response.isSuccessful()) {
                    Toast.makeText(FeedDescriptionActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body().response_code == 999) {
                    currApi = Constants.APIS.FEED_DESCRIPTION;
                    refreshToken();
                    return;
                }
                post = response.body().post;
                profileUrl = response.body().user_media_base_url;
                if (post == null) {
                    finish();
                    return;
                }
                commentsArrayList = (ArrayList<PostWithComments.Comments>) response.body().post.comments;
                setData();
            }

            @Override
            public void onFailure(Call<PostWithComments> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                Toast.makeText(FeedDescriptionActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        if (post.post_text == null || post.post_text.isEmpty()) {
            text.setVisibility(View.GONE);
        } else {
            text.setVisibility(View.VISIBLE);
        }
        text.setText(post.post_text != null ? post.post_text : "");

        if (commentsArrayList == null)
            commentsArrayList = new ArrayList<>();

        Locale LocaleBylanguageTag;
        TimeAgoMessages messages;

        LocaleBylanguageTag = getResources().getConfiguration().locale;
        messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();

        if (post.userInfo != null)
            if (post.userInfo.user_type == 0)
                username.setText(Utils.capitalizeString(post.userInfo.first_name + " " + post.userInfo.last_name));
            else
                username.setText(Utils.capitalizeString(post.userInfo.business_name));
        if (post.post_media.size() <= 0) {
            img.setVisibility(View.GONE);
            jzVideoPlayerStandard.setVisibility(View.GONE);
            pb.setVisibility(View.GONE);
        } else if (post.post_media.get(0).media_type.equalsIgnoreCase("video") ||
                post.post_media.get(0).media_type.equalsIgnoreCase("2")) {
            pb.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
            jzVideoPlayerStandard.setVisibility(View.VISIBLE);
            jzVideoPlayerStandard.setUp(imageUrl + "/" + post.userInfo._id + "/" + post.post_media.get(0).file_name
                    , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
        } else if (post.post_media.get(0).media_type.equalsIgnoreCase("image") ||
                post.post_media.get(0).media_type.equalsIgnoreCase("1")) {
            jzVideoPlayerStandard.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);

            final float scale = getResources().getDisplayMetrics().density;
            if (post.post_media.get(0).height > 500) {
                img.getLayoutParams().height = (int) (400 * scale + 0.5f);
            } else {
                img.getLayoutParams().height = (int) (250 * scale + 0.5f);
            }

            img.requestLayout();

            Glide.with(FeedDescriptionActivity.this).load(imageUrl + "/" + post.userInfo._id + "/" + post.post_media.get(0).file_name).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    pb.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    pb.setVisibility(View.GONE);
                    return false;
                }
            }).into(img);
        }

        if (profileUrl != null && post.userInfo.profile_image != null && !post.userInfo.profile_image.isEmpty()) {
            Glide.with(FeedDescriptionActivity.this).load(profileUrl + "/" + post.userInfo._id + "/" + post.userInfo.profile_image)
                    .centerCrop().into(ivProfile);
        } else {
            ivProfile.setImageResource(R.mipmap.avatar);
        }

        String text = TimeAgo.using(Long.parseLong(post.timestamp), messages);
        time.setText(text);
        likes.setText(String.valueOf(post.total_likes));
        unlikes.setText(String.valueOf(post.total_unlikes));
        tvComments.setText(String.valueOf(commentsArrayList.size()));
        adapter.doRefresh(commentsArrayList);

        ivLike.setImageResource(post.is_like ? R.mipmap.like_filled : R.mipmap.like);
        ivUnlike.setImageResource(post.is_unlike ? R.mipmap.unlike_filled : R.mipmap.unlike);

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canLikePost)
                    return;

                likePost(post.is_like ? 0 : 1);
            }
        });
        ivUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canLikePost)
                    return;

                unLikePost(post.is_unlike ? 0 : 1);
            }
        });

    }

    private void deleteComment() {

        if (commentUpdate.getComment() == null)
            return;

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_comment_id, commentUpdate.getComment()._id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(FeedDescriptionActivity.this);
        Globals.initRetrofit(FeedDescriptionActivity.this).deleteComment(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {

                    commentsArrayList.remove(commentUpdate.getPosition());
                    adapter.doRefresh(commentsArrayList);
                    commentUpdate = new CommentUpdate();

                } else if (response.body().response_code == 999) {
                    currApi = Constants.APIS.DELETE_COMMENT;
                    refreshToken();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(FeedDescriptionActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void postComment() {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_comment_text, edComment.getText().toString().trim());
            object.addProperty(Constants.MM_post_id, post._id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(FeedDescriptionActivity.this);
        Globals.initRetrofit(FeedDescriptionActivity.this).postComment(object).enqueue(new Callback<CommentRes>() {
            @Override
            public void onResponse(Call<CommentRes> call, Response<CommentRes> response) {
                Utils.closeProgressDialog();
                if (!response.isSuccessful()) {
                    Toast.makeText(FeedDescriptionActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }

                PostWithComments.Comments comments = new PostWithComments.Comments();
                comments.comment_text = edComment.getText().toString().trim();
                comments.timestamp = response.body().comment.timestamp;
                comments._id = response.body().comment._id;
                PostWithComments.UserInfo info = new PostWithComments.UserInfo();

                if (user.user_type == 0) {
                    info.last_name = user.last_name;
                    info.first_name = user.first_name;
                    info.user_type = 0;
                } else {
                    info.business_name = user.business_name;
                    info.user_type = 1;
                }

                info._id = user._id;
                comments.userInfo = info;

                commentsArrayList.add(comments);
                adapter.doRefresh(commentsArrayList);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);

                    }
                }, 500);
                edComment.setText("");
                tvComments.setText(String.valueOf(commentsArrayList.size()));
            }

            @Override
            public void onFailure(Call<CommentRes> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                Toast.makeText(FeedDescriptionActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void likePost(final int status) {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_post_id, post._id);
            object.addProperty(Constants.MM_status, status);
            object.addProperty(Constants.MM_user_id, post.userInfo._id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        canLikePost = false;
        Globals.initRetrofit(FeedDescriptionActivity.this).likePost(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {

                    post.is_like = !post.is_like;

                    if (post.is_unlike && post.is_like) {
                        post.is_unlike = false;
                        post.total_unlikes += -1;
                    }

                    ivUnlike.setImageResource(!post.is_unlike ? R.mipmap.unlike : R.mipmap.unlike_filled);
                    ivLike.setImageResource(!post.is_like ? R.mipmap.like : R.mipmap.like_filled);

                    post.total_likes += post.is_like ? 1 : -1;

                    likes.setText(String.valueOf(post.total_likes));
                    unlikes.setText(String.valueOf(post.total_unlikes));

                } else if (response.body().response_code == 999) {
                    currApi = Constants.APIS.LIKE;
                    refreshToken();
                }
                canLikePost = true;
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                canLikePost = true;
            }
        });
    }

    private void unLikePost(final int status) {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_post_id, post._id);
            object.addProperty(Constants.MM_status, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        canLikePost = false;
        Globals.initRetrofit(FeedDescriptionActivity.this).unlikePost(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {
                    post.is_unlike = !post.is_unlike;

                    if (post.is_unlike && post.is_like) {
                        post.is_like = false;
                        post.total_likes += -1;
                    }

                    ivUnlike.setImageResource(!post.is_unlike ? R.mipmap.unlike : R.mipmap.unlike_filled);
                    ivLike.setImageResource(!post.is_like ? R.mipmap.like : R.mipmap.like_filled);

                    post.total_unlikes += post.is_unlike ? 1 : -1;

                    likes.setText(String.valueOf(post.total_likes));
                    unlikes.setText(String.valueOf(post.total_unlikes));
                } else if (response.body().response_code == 999) {
                    currApi = Constants.APIS.UNLIKE;
                    refreshToken();
                }
                canLikePost = true;
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                canLikePost = true;
            }
        });
    }


    private void likeComment(final String commentId, final int status, String userId) {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_comment_id, commentId);
            object.addProperty(Constants.MM_status, status);
            object.addProperty(Constants.MM_user_id, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.canNotLikeComment();

        Globals.initRetrofit(FeedDescriptionActivity.this).likeComment(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {

                    commentUpdate.getComment().is_like = commentUpdate.getCommentStatus() == 1;
                    if (commentUpdate.getComment().is_like && commentUpdate.getComment().is_unlike) {
                        commentUpdate.getComment().is_unlike = false;
                        commentUpdate.getComment().total_unlikes += -1;
                    }

                    commentUpdate.getComment().total_likes += commentUpdate.getComment().is_like ? 1 : -1;

                    commentsArrayList.set(commentUpdate.getPosition(), commentUpdate.getComment());
                    adapter.doRefresh(commentsArrayList);
                    commentUpdate = new CommentUpdate();
                } else if (response.body().response_code == 999) {
                    currApi = Constants.APIS.LIKE_COMMENT;
                    refreshToken();
                }
                adapter.canLikeComment();
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                adapter.canLikeComment();
            }
        });
    }

    private void unLikeComment(final String postId, final int status) {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_comment_id, postId);
            object.addProperty(Constants.MM_status, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.canNotLikeComment();

        Globals.initRetrofit(FeedDescriptionActivity.this).unlikeComment(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().response_code == 200) {

                    commentUpdate.getComment().is_unlike = commentUpdate.getCommentStatus() == 1;
                    if (commentUpdate.getComment().is_unlike && commentUpdate.getComment().is_like) {
                        commentUpdate.getComment().is_like = false;
                        commentUpdate.getComment().total_likes += -1;
                    }

                    commentUpdate.getComment().total_unlikes += commentUpdate.getComment().is_unlike ? 1 : -1;

                    commentsArrayList.set(commentUpdate.getPosition(), commentUpdate.getComment());
                    adapter.doRefresh(commentsArrayList);
                    commentUpdate = new CommentUpdate();
                } else if (response.body().response_code == 999) {
                    currApi = Constants.APIS.UNLIKE_COMMENT;
                    refreshToken();
                }
                adapter.canLikeComment();
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                adapter.canLikeComment();
            }
        });
    }

    private void refreshToken() {
        new LoginHandler(FeedDescriptionActivity.this, Globals.login, new LoginHandler.LoginResponseHandler() {
            @Override
            public void onSuccess(BasicResponse response) {
                if (currApi == Constants.APIS.LIKE) {
                    likePost(post.is_like ? 0 : 1);
                } else if (currApi == Constants.APIS.UNLIKE) {
                    unLikePost(post.is_unlike ? 0 : 1);
                } else if (currApi == Constants.APIS.FEED_DESCRIPTION) {
                    getPostWithComments();
                } else if (currApi == Constants.APIS.LIKE_COMMENT) {
                    likeComment(commentUpdate.getComment()._id, commentUpdate.getCommentStatus(), commentUpdate.getComment().userInfo._id);
                } else if (currApi == Constants.APIS.UNLIKE_COMMENT) {
                    unLikeComment(commentUpdate.getComment()._id, commentUpdate.getCommentStatus());
                } else if (currApi == Constants.APIS.DELETE_COMMENT) {
                    deleteComment();
                }
            }

            @Override
            public void onFail(String msg) {
                Utils.setPreference(FeedDescriptionActivity.this, Constants.MM_UserDate, "");
                Utils.setPreference(FeedDescriptionActivity.this, Constants.MM_token, "");
                Intent i = new Intent(FeedDescriptionActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                FeedDescriptionActivity.this.finish();
            }
        });
    }


}
