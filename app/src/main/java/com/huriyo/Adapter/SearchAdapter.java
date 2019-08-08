package com.huriyo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huriyo.R;
import com.huriyo.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jay on 09/11/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private List<String> mdata;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvName.setTypeface(Utils.getTypefaceNormal(context));
        }
    }

    public void doRefresh(List<String> data){
        this.mdata = data;
        notifyDataSetChanged();
    }

    public List<String> getMdata(){
        return mdata;
    }

    public SearchAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvName.setText(mdata.get(position));
    }

    @Override
    public int getItemCount() {
        return mdata == null ? 0 : mdata.size();
    }
}