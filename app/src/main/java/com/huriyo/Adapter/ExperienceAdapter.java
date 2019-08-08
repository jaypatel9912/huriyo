package com.huriyo.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huriyo.Model.UserDetails;
import com.huriyo.R;
import com.huriyo.Utility.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jay on 29/11/17.
 */

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.MyViewHolder> {

    private ArrayList<UserDetails.ExperienceInfo> list;
    Context context;
    ImageView ivEditExperience;
    Typeface tfBold, tfRegular, tfHeavy;
    OnExperienceEdit listener;
    boolean isUserSelf =false;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public interface OnExperienceEdit{
        public void onEdit(UserDetails.ExperienceInfo experienceInfo, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPeriod, tvCompany, tvPosition;

        public MyViewHolder(View view) {
            super(view);
            tvPeriod = (TextView) view.findViewById(R.id.tvPeriod);
            ivEditExperience = (ImageView) view.findViewById(R.id.ivEditExperience);
            tvPeriod.setTypeface(tfRegular);
            tvCompany = (TextView) view.findViewById(R.id.tvCompany);
            tvCompany.setTypeface(tfBold);
            tvPosition = (TextView) view.findViewById(R.id.tvPosition);
            tvPosition.setTypeface(tfBold);
        }
    }


    public ExperienceAdapter(Context context, ArrayList<UserDetails.ExperienceInfo> experienceInfos, boolean isOtherUser, OnExperienceEdit listener) {
        this.list = experienceInfos;
        this.listener = listener;
        this.context = context;
        tfRegular = Utils.getTypefaceNormal(context);
        tfBold = Utils.getTypefaceBold(context);
        this.isUserSelf = isOtherUser;
        tfHeavy = Utils.getTypefaceHeavy(context);
    }

    public void doRefresh(ArrayList<UserDetails.ExperienceInfo> experienceInfos) {
        this.list = experienceInfos;
        notifyDataSetChanged();
    }

    @Override
    public ExperienceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.experience_item, parent, false);

        return new ExperienceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExperienceAdapter.MyViewHolder holder, final int position) {
        final UserDetails.ExperienceInfo item = list.get(position);
        Date time=new Date(Long.parseLong(item.start_date));
        if (item.is_currently_work == 1) {
            holder.tvPeriod.setText(dateFormat.format(time) + " to continue");
        } else {
            Date time2=new Date(Long.parseLong(item.end_date));
            holder.tvPeriod.setText(dateFormat.format(time) + " to " + dateFormat.format(time2));
        }

        holder.tvPosition.setText(item.job_title);
        holder.tvCompany.setText(item.company_name);

        if(isUserSelf){
            ivEditExperience.setVisibility(View.VISIBLE);
        }else{
            ivEditExperience.setVisibility(View.GONE);
        }

        ivEditExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               listener.onEdit(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}