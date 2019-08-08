package com.huriyo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huriyo.Model.Category;
import com.huriyo.R;
import com.huriyo.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jay on 22/12/17.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<Category.Categories> data;
    Context context;
    Category.Categories selectedCategory;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        ImageView ivCheck;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            ivCheck = (ImageView) view.findViewById(R.id.ivCheck);
            tvName.setTypeface(Utils.getTypefaceNormal(context));
        }
    }

    public Category.Categories getSelectedCategory(){
        return selectedCategory;
    }

    public void doRefresh(List<Category.Categories> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public List<Category.Categories> getData() {
        return data;
    }

    public CategoryAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item_layout, parent, false);

        return new CategoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.MyViewHolder holder, final int position) {
        holder.tvName.setText(data.get(position).category_name);
        if (data.get(position).cheked)
            holder.ivCheck.setVisibility(View.VISIBLE);
        else
            holder.ivCheck.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkItem(position);
            }
        });
    }


    public void checkItem(int position) {
        for (int i = 0; i < data.size(); i++) {
            if(i == position){
                data.get(i).cheked = !data.get(i).cheked;
                if(data.get(i).cheked)
                    selectedCategory = data.get(i);
                else
                    selectedCategory = null;
            }else{
                data.get(i).cheked = false;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}