package com.huriyo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.huriyo.Model.Feed;
import com.huriyo.R;
import com.huriyo.Ui.FeedDescriptionActivity;
import com.huriyo.Ui.ImageCropActivity;
import com.huriyo.Ui.MainActivity;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.jzvd.JZVideoPlayerStandard;

/**
 * Created by Jay on 06/11/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    List<Feed.Posts> posts;
    Context context;
    Typeface tfBold, tfRegular, tfHeavy;
    String imageUrl, profileUrl;
    OnLikeUnlikePost onLikeUnlikePost;
    boolean canLikeUnlike = true;

    public interface OnLikeUnlikePost {
        public void onLike(int position, Feed.Posts post, int status);

        public void onUnLike(int position, Feed.Posts post, int status);
    }

    public void canLike() {
        canLikeUnlike = true;
        notifyDataSetChanged();
    }

    public void canNotLike() {
        canLikeUnlike = false;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text, username, time, likes, unlikes, comments;
        ImageView img, ivLike, ivUnlike, ivOptions, imgViewer;
        CircleImageView ivProfile;
        ProgressBar pb;
        FrameLayout llMain;
        JZVideoPlayerStandard jzVideoPlayerStandard;
        LinearLayout llUser, llComment;

        public MyViewHolder(View view) {
            super(view);
            llUser = (LinearLayout) view.findViewById(R.id.llUser);
            llComment = (LinearLayout) view.findViewById(R.id.llComment);
            img = (ImageView) view.findViewById(R.id.img);
            imgViewer = (ImageView) view.findViewById(R.id.imgViewer);
            ivProfile = (CircleImageView) view.findViewById(R.id.ivProfile);
            ivLike = (ImageView) view.findViewById(R.id.ivLike);
            ivUnlike = (ImageView) view.findViewById(R.id.ivUnlike);
            text = (TextView) view.findViewById(R.id.text);
            text.setTypeface(tfRegular);
            jzVideoPlayerStandard = (JZVideoPlayerStandard) view.findViewById(R.id.videoplayer);
            username = (TextView) view.findViewById(R.id.username);
            username.setTypeface(tfBold);
            time = (TextView) view.findViewById(R.id.time);
            time.setTypeface(tfBold);
            pb = (ProgressBar) view.findViewById(R.id.pb);
            llMain = (FrameLayout) view.findViewById(R.id.llMain);
            likes = (TextView) view.findViewById(R.id.likes);
            likes.setTypeface(tfRegular);
            unlikes = (TextView) view.findViewById(R.id.unlikes);
            unlikes.setTypeface(tfRegular);
            comments = (TextView) view.findViewById(R.id.comments);
            comments.setTypeface(tfRegular);
            ivOptions = (ImageView) view.findViewById(R.id.ivOptions);
        }
    }

    public List<Feed.Posts> getPosts() {
        return this.posts;
    }

    public void doEmpty() {
        this.posts = new ArrayList<>();
        notifyDataSetChanged();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void doRefreshAll(List<Feed.Posts> posts, String imageUrl, String profileUrl) {
        this.posts = posts;
        this.imageUrl = imageUrl;
        this.profileUrl = profileUrl;
        notifyDataSetChanged();
    }

    public void doRefresh(List<Feed.Posts> posts, String imageUrl, String profileUrl) {
        this.posts = posts;
        this.imageUrl = imageUrl;
        this.profileUrl = profileUrl;
        notifyDataSetChanged();
    }

    Locale LocaleBylanguageTag;
    TimeAgoMessages messages;

    public FeedAdapter(Context context, OnLikeUnlikePost onLikeUnlikePost) {
        this.posts = new ArrayList<>();
        this.context = context;
        this.onLikeUnlikePost = onLikeUnlikePost;
        tfRegular = Utils.getTypefaceNormal(context);
        tfBold = Utils.getTypefaceBold(context);
        tfHeavy = Utils.getTypefaceHeavy(context);

        LocaleBylanguageTag = context.getResources().getConfiguration().locale;
        messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_main_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Feed.Posts post = posts.get(position);
        holder.text.setText(post.post_text != null ? post.post_text : "");

        if (post.post_text == null || post.post_text.isEmpty()) {
            holder.text.setVisibility(View.GONE);
        } else {
            holder.text.setVisibility(View.VISIBLE);
        }

        String text = TimeAgo.using(Long.parseLong(post.timestamp), messages);
        holder.time.setText(text);
        holder.likes.setText(String.valueOf(post.total_likes));
        holder.unlikes.setText(String.valueOf(post.total_unlikes));
        holder.comments.setText(String.valueOf(post.comment_count));

        holder.ivLike.setImageResource(post.is_like ? R.mipmap.like_filled : R.mipmap.like);
        holder.ivUnlike.setImageResource(post.is_unlike ? R.mipmap.unlike_filled : R.mipmap.unlike);

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canLikeUnlike)
                    return;

                onLikeUnlikePost.onLike(position, post, post.is_like ? 0 : 1);
            }
        });
        holder.ivUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canLikeUnlike)
                    return;

                onLikeUnlikePost.onUnLike(position, post, post.is_unlike ? 0 : 1);
            }
        });

        if (post.userInfo != null) {
            if (post.userInfo.user_type == 0)
                holder.username.setText(Utils.capitalizeString(post.userInfo.first_name + " " + post.userInfo.last_name));
            else
                holder.username.setText(Utils.capitalizeString(post.userInfo.business_name));
        }

        if (post.post_media.size() <= 0) {
            holder.img.setVisibility(View.GONE);
            holder.jzVideoPlayerStandard.setVisibility(View.GONE);
            holder.pb.setVisibility(View.GONE);
            holder.imgViewer.setVisibility(View.GONE);
        } else if (post.post_media.get(0).media_type.equalsIgnoreCase("video") ||
                post.post_media.get(0).media_type.equalsIgnoreCase("2")) {
            holder.pb.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
            holder.imgViewer.setVisibility(View.GONE);
            holder.jzVideoPlayerStandard.setUp(imageUrl + "/" + post.userInfo._id + "/" + post.post_media.get(0).file_name
                    , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
        } else if (post.post_media.get(0).media_type.equalsIgnoreCase("image") ||
                post.post_media.get(0).media_type.equalsIgnoreCase("1")) {
            holder.jzVideoPlayerStandard.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);
            holder.imgViewer.setVisibility(View.VISIBLE);
            final float scale = context.getResources().getDisplayMetrics().density;

            if (post.post_media.get(0).height > 500) {
                holder.img.getLayoutParams().height =  (int) (400 * scale + 0.5f);
            } else {
                holder.img.getLayoutParams().height = (int) (250 * scale + 0.5f);
            }

            holder.img.requestLayout();
            Glide.with(context).load(imageUrl + "/" + post.userInfo._id + "/" + post.post_media.get(0).file_name).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    holder.pb.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    holder.pb.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.img);

//            Picasso.with(context)
//                    .load(imageUrl + "/" + post.userInfo._id + "/" + post.post_media.get(0).file_name)
//                    .into(holder.img, new com.squareup.picasso.Callback() {
//                        @Override
//                        public void onSuccess() {
//                            holder.pb.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onError() {
//                            holder.pb.setVisibility(View.GONE);
//                        }
//                    });
        }

        if (post.userInfo.profile_image != null && !post.userInfo.profile_image.isEmpty()) {
            Glide.with(context).load(profileUrl + "/" + post.userInfo._id + "/" + post.userInfo.profile_image)
                    .centerCrop().into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.mipmap.avatar);
        }

        holder.imgViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageCropActivity.class);
                intent.putExtra(Constants.MM_Data, new Gson().toJson(post.post_media));
                intent.putExtra(Constants.MM_view, true);
                intent.putExtra(Constants.MM_imagepath, imageUrl);
                context.startActivity(intent);
            }
        });

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FeedDescriptionActivity.class);
                intent.putExtra(Constants.MM_Feed, post._id);
                intent.putExtra(Constants.MM_image_url, imageUrl);
                context.startActivity(intent);
            }
        });

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FeedDescriptionActivity.class);
                intent.putExtra(Constants.MM_Feed, post._id);
                intent.putExtra(Constants.MM_image_url, imageUrl);
                context.startActivity(intent);
            }
        });

        holder.llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FeedDescriptionActivity.class);
                intent.putExtra(Constants.MM_Feed, post._id);
                intent.putExtra(Constants.MM_image_url, imageUrl);
                context.startActivity(intent);
            }
        });

        holder.llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FeedDescriptionActivity.class);
                intent.putExtra(Constants.MM_Feed, post._id);
                intent.putExtra(Constants.MM_image_url, imageUrl);
                context.startActivity(intent);
            }
        });


        holder.ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions(position, holder.ivOptions);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void showOptions(int pos, ImageView view) {
        //Creating the instance of PopupMenu
        Context wrapper = new ContextThemeWrapper(context, R.style.popupStyle);
        PopupMenu popup = new PopupMenu(wrapper, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.feed_options, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(context, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();//showing popup menu
    }
}