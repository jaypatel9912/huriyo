package com.huriyo.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.Gson;
import com.huriyo.Model.PostWithComments;
import com.huriyo.Model.User;
import com.huriyo.R;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Utils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Jay on 08/11/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private ArrayList<PostWithComments.Comments> list;
    Context context;
    Typeface tfBold, tfRegular;
    Locale LocaleBylanguageTag;
    TimeAgoMessages messages;
    OnLikeUnlikeComment onLikeUnlikeComment;
    boolean canLikeUnlike = true;
    String userId = "";

    public interface OnLikeUnlikeComment {
        public void onLike(int position, PostWithComments.Comments comments, int status);

        public void onUnLike(int position, PostWithComments.Comments comments, int status);

        public void onCommentDelete(int position, PostWithComments.Comments comments);
    }

    public void canLikeComment() {
        canLikeUnlike = true;
        notifyDataSetChanged();
    }

    public void canNotLikeComment() {
        canLikeUnlike = false;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text, username, time, likes, unlikes;
        CircularImageView userImage;
        ImageView ivLike, ivUnlike, ivOptions;


        public MyViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
            text.setTypeface(tfRegular);
            time = (TextView) view.findViewById(R.id.time);
            time.setTypeface(tfBold);
            likes = (TextView) view.findViewById(R.id.likes);
            likes.setTypeface(tfRegular);
            unlikes = (TextView) view.findViewById(R.id.unlikes);
            unlikes.setTypeface(tfRegular);
            username = (TextView) view.findViewById(R.id.username);
            username.setTypeface(tfBold);
            userImage = (CircularImageView) view.findViewById(R.id.userImage);
            ivLike = (ImageView) view.findViewById(R.id.ivLike);
            ivUnlike = (ImageView) view.findViewById(R.id.ivUnlike);
            ivOptions = (ImageView) view.findViewById(R.id.ivOptions);
        }
    }

    public CommentAdapter(Context context, OnLikeUnlikeComment onLikeUnlikeComment) {
        this.onLikeUnlikeComment = onLikeUnlikeComment;
        list = new ArrayList<>();
        LocaleBylanguageTag = context.getResources().getConfiguration().locale;
        messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();
        this.context = context;
        tfRegular = Utils.getTypefaceNormal(context);
        tfBold = Utils.getTypefaceBold(context);
        userId = new Gson().fromJson(Utils.getPreference(context, Constants.MM_UserDate), User.class)._id;
    }


    public void doRefresh(ArrayList<PostWithComments.Comments> comments) {
        this.list = comments;
        notifyDataSetChanged();
    }

    public void addComment(PostWithComments.Comments comment) {
        list.add(comment);
        notifyDataSetChanged();
    }

    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item_layout, parent, false);

        return new CommentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.MyViewHolder holder, final int position) {
        final PostWithComments.Comments comment = list.get(position);
        holder.text.setText(comment.comment_text);
        holder.time.setText(TimeAgo.using(Long.parseLong(comment.timestamp), messages));
        holder.likes.setText(String.valueOf(comment.total_likes));
        holder.unlikes.setText(String.valueOf(comment.total_unlikes));

        if (comment.userInfo.user_type == 0)
            holder.username.setText(Utils.capitalizeString(comment.userInfo.first_name + " " + comment.userInfo.last_name));
        else
            holder.username.setText(Utils.capitalizeString(comment.userInfo.business_name));


        holder.ivLike.setImageResource(comment.is_like ? R.mipmap.like_filled : R.mipmap.like);
        holder.ivUnlike.setImageResource(comment.is_unlike ? R.mipmap.unlike_filled : R.mipmap.unlike);

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canLikeUnlike)
                    return;

                onLikeUnlikeComment.onLike(position, comment, comment.is_like ? 0 : 1);
            }
        });
        holder.ivUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canLikeUnlike)
                    return;

                onLikeUnlikeComment.onUnLike(position, comment, comment.is_unlike ? 0 : 1);
            }
        });

        holder.ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentOptions(position, holder.ivOptions);
            }
        });


    }

    private void showCommentOptions(final int pos, ImageView view) {
        //Creating the instance of PopupMenu
        Context wrapper = new ContextThemeWrapper(context, R.style.popupStyle);
        PopupMenu popup = new PopupMenu(wrapper, view);
        //Inflating the Popup using xml file
        if (userId.equalsIgnoreCase(list.get(pos).userInfo._id))
            popup.getMenuInflater().inflate(R.menu.comment_options, popup.getMenu());
        else
            popup.getMenuInflater().inflate(R.menu.feed_options, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.deleteComment) {
                    onLikeUnlikeComment.onCommentDelete(pos, list.get(pos));
                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
