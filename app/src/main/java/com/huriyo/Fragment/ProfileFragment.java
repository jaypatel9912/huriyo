package com.huriyo.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huriyo.Adapter.ExperienceAdapter;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.FriendRequest;
import com.huriyo.Model.User;
import com.huriyo.Model.UserDetails;
import com.huriyo.R;
import com.huriyo.Ui.BussinessInfoActivity;
import com.huriyo.Ui.BussinessUsersActivity;
import com.huriyo.Ui.ExperienceActivity;
import com.huriyo.Ui.MainActivity;
import com.huriyo.Ui.PersonaInformationActivity;
import com.huriyo.Ui.SearchResultActivity;
import com.huriyo.Ui.UserFriendsActivity;
import com.huriyo.Ui.UserPostsActivity;
import com.huriyo.Ui.UserRatingActivity;
import com.huriyo.Utility.CircleImageView;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.Utils;
import com.willy.ratingbar.ScaleRatingBar;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jay on 07/11/17.
 */


public class ProfileFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    View view;
    NestedScrollView scrollView;
    TextView aboutUser, tvAboutName, tvName;

    ImageView editPersonalInfo, ivExperience;
    CircleImageView ivProfile;
    UserDetails userDetails;
    BasicResponse basicResponse;
    ImageView ivCover;
    ArrayList<UserDetails.ExperienceInfo> experienceInfos;
    ExperienceAdapter adapter;
    Button btnAddExperience;
    ScaleRatingBar simpleRatingBar;
    TextView reviews, tvPosts, tvFriends;
    String userId = "";
    boolean isUserSelf = true;
    LinearLayout llFriends, llPosts, llRequest;
    TextView friendStatus;
    Button btnAccept, btnDelete;
    User currUser;
    LinearLayout llexperience;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null && getArguments().containsKey(Constants.MM_user_id)) {
            userId = getArguments().getString(Constants.MM_user_id);
        } else {
            userId = new Gson().fromJson(Utils.getPreference(getActivity(), Constants.MM_UserDate), User.class)._id;
        }

        currUser = new Gson().fromJson(Utils.getPreference(getActivity(), Constants.MM_UserDate), User.class);
        if (currUser == null)
            isUserSelf = false;
        else if (currUser != null && !currUser._id.equalsIgnoreCase(userId))
            isUserSelf = false;


        view = inflater.inflate(R.layout.fragment_profile, container, false);

        llRequest = (LinearLayout) view.findViewById(R.id.llRequest);
        llexperience = (LinearLayout) view.findViewById(R.id.llexperience);
        friendStatus = (TextView) view.findViewById(R.id.friendStatus);
        btnAccept = (Button) view.findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);
        btnDelete = (Button) view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);

        ivProfile = (CircleImageView) view.findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(this);

        llFriends = (LinearLayout) view.findViewById(R.id.llFriends);
        llPosts = (LinearLayout) view.findViewById(R.id.llPosts);

        simpleRatingBar = (ScaleRatingBar) view.findViewById(R.id.simpleRatingBar);
        reviews = (TextView) view.findViewById(R.id.reviews);
        reviews.setOnClickListener(this);
        experienceInfos = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new ExperienceAdapter(getActivity(), experienceInfos, isUserSelf, new ExperienceAdapter.OnExperienceEdit() {
            @Override
            public void onEdit(UserDetails.ExperienceInfo experienceInfo, int position) {
                Intent intent = new Intent(getActivity(), ExperienceActivity.class);
                intent.putExtra(Constants.MM_Experience_data, new Gson().toJson(experienceInfo));
                intent.putExtra(Constants.MM_position, position);
                startActivityForResult(intent, 11522);
            }
        });
        recyclerView.setAdapter(adapter);

        btnAddExperience = (Button) view.findViewById(R.id.btnAddExperience);
        btnAddExperience.setOnClickListener(this);
        ivCover = (ImageView) view.findViewById(R.id.ivCover);
        scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        aboutUser = (TextView) view.findViewById(R.id.aboutUser);
        tvAboutName = (TextView) view.findViewById(R.id.tvAboutName);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvPosts = (TextView) view.findViewById(R.id.tvPosts);
        tvFriends = (TextView) view.findViewById(R.id.tvFriends);
        llPosts.setOnClickListener(this);
        llFriends.setOnClickListener(this);

        editPersonalInfo = (ImageView) view.findViewById(R.id.editPersonalInfo);
        editPersonalInfo.setOnClickListener(this);
        ivExperience = (ImageView) view.findViewById(R.id.ivExperience);
        ivExperience.setOnClickListener(this);

        if (!isUserSelf) {
            ivExperience.setVisibility(View.GONE);
            btnAddExperience.setVisibility(View.GONE);
            editPersonalInfo.setVisibility(View.GONE);
        }

        if (currUser == null) {
            getBussinessUserDetails();
        } else {
            getUserDetails();
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.editPersonalInfo:
                if (userDetails == null)
                    return;

                intent = new Intent(getActivity(), PersonaInformationActivity.class);
                intent.putExtra(Constants.MM_UserDate, new Gson().toJson(userDetails));
                intent.putExtra(Constants.MM_type, userDetails.user.user_type);
                startActivityForResult(intent, 12354);

                break;
            case R.id.btnAddExperience:
                intent = new Intent(getActivity(), ExperienceActivity.class);
                startActivityForResult(intent, 12453);
                break;
            case R.id.llPosts:
                if (currUser == null)
                    showLoginSialog();
                else {
                    intent = new Intent(getActivity(), UserPostsActivity.class);
                    intent.putExtra(Constants.MM_user_id, userId);
                    startActivityForResult(intent, 12453);
                }
                break;
            case R.id.llFriends:
                if (currUser == null)
                    showLoginSialog();
                else {
                    intent = new Intent(getActivity(), UserFriendsActivity.class);
                    intent.putExtra(Constants.MM_user_id, userId);
                    startActivityForResult(intent, 12453);
                }
                break;
            case R.id.ivProfile:
//                ((HomeActivity) getActivity()).handleIntent(ivProfile.getId(), null, true);
                break;
            case R.id.reviews:
                if (currUser == null)
                    showLoginSialog();
                else {


                    if (userDetails == null)
                        return;

                    intent = new Intent(getActivity(), UserRatingActivity.class);
                    intent.putExtra(Constants.MM_user_id, userId);
                    intent.putExtra(Constants.MM_user_name, userDetails.user.first_name + userDetails.user.last_name);
                    if (!isUserSelf && !userDetails.user.is_rating_submitted)
                        intent.putExtra(Constants.MM_give_rate, true);

                    startActivityForResult(intent, 14532);
                }
                break;
            case R.id.btnAccept:
                if (userDetails == null)
                    return;

                if (userDetails.user.request_status == 0)
                    sendFriendRequest(userId);
                else if (userDetails.user.request_status == 3)
                    acceptRejectRequest(userId, 1);

                break;
            case R.id.btnDelete:
                acceptRejectRequest(userId, 0);
                break;
        }
    }

    public void onImageSelected(int imgResID, Bitmap bitmap, String encodeImageString, File imageFile) {
        ivProfile.setImageBitmap(bitmap);
    }

    private void getUserDetails() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_user_id, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(getActivity());
        Globals.initRetrofit(getActivity()).getUserDetails(object).enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    userDetails = response.body();
                    if (userDetails != null)
                        setData();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        if (userDetails.user.cover_image != null) {
            Glide.with(getActivity()).load(userDetails.user.cover_image).into(ivCover);
        }
        if (userDetails.user.profile_image != null) {
            Glide.with(getActivity()).load(userDetails.user.profile_image).into(ivProfile);
        }

        if (userDetails.user.first_name != null && userDetails.user.last_name != null) {
            if (userDetails.user.user_type == 0) {
                tvName.setText(Utils.capitalizeString(userDetails.user.first_name + " " + userDetails.user.last_name));
                tvAboutName.setText(Utils.capitalizeString("About " + userDetails.user.first_name + " " + userDetails.user.last_name));
            } else {
                tvName.setText(Utils.capitalizeString(userDetails.user.business_name));
                tvAboutName.setText(Utils.capitalizeString("About " + userDetails.user.business_name));
            }
        }
        try {
            if (userDetails.user.user_type == 0)
                aboutUser.setText(userDetails.user.about_me);
            else
                aboutUser.setText(userDetails.user.business_description);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userDetails.user.experienceInfo != null && userDetails.user.experienceInfo.size() > 0) {
            experienceInfos = (ArrayList<UserDetails.ExperienceInfo>) userDetails.user.experienceInfo;
            adapter.doRefresh(experienceInfos);
        }

        simpleRatingBar.setRating(userDetails.user.avg_rating);
        tvPosts.setText(String.valueOf(userDetails.user.total_post));
        tvFriends.setText(String.valueOf(userDetails.user.total_friends));

        if (isUserSelf) {
            llRequest.setVisibility(View.GONE);
        } else {
            llRequest.setVisibility(View.VISIBLE);
            if (userDetails.user.request_status == 0) {
                friendStatus.setText("Send connect request to " + userDetails.user.first_name + " " + userDetails.user.last_name);
                btnAccept.setText("Add Connect");
            } else if (userDetails.user.request_status == 1) {
                friendStatus.setText("You and " + userDetails.user.first_name + " " + userDetails.user.last_name + " are friends.");
                btnAccept.setVisibility(View.GONE);
            } else if (userDetails.user.request_status == 2) {
                friendStatus.setText("Connect request sent to " + userDetails.user.first_name + " " + userDetails.user.last_name + ".");
                btnAccept.setVisibility(View.GONE);
            } else if (userDetails.user.request_status == 3) {
                friendStatus.setText(userDetails.user.first_name + " " + userDetails.user.last_name + " has sent you a connect request.");
                btnDelete.setVisibility(View.VISIBLE);
                btnAccept.setText("Confirm");
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == 12354) {
            if (data.getExtras().containsKey(Constants.MM_UserDate)) {
                if (new Gson().fromJson(data.getExtras().getString(Constants.MM_UserDate), UserDetails.class) != null) {
                    userDetails = new Gson().fromJson(data.getExtras().getString(Constants.MM_UserDate), UserDetails.class);
                    setData();
                }
            }
        }
        if (resultCode == getActivity().RESULT_OK && requestCode == 12453) {
            if (data.getExtras().containsKey(Constants.MM_Experience_data)) {
                if (new Gson().fromJson(data.getExtras().getString(Constants.MM_Experience_data), UserDetails.ExperienceInfo.class) != null) {
                    experienceInfos.add(new Gson().fromJson(data.getExtras().getString(Constants.MM_Experience_data), UserDetails.ExperienceInfo.class));
                    adapter.doRefresh(experienceInfos);
                }
            }
        }

        if (resultCode == getActivity().RESULT_OK && requestCode == 11522) {
            if (data.getExtras().containsKey(Constants.MM_Experience_data)) {
                if (new Gson().fromJson(data.getExtras().getString(Constants.MM_Experience_data), UserDetails.ExperienceInfo.class) != null) {
                    UserDetails.ExperienceInfo experienceInfo = (new Gson().fromJson(data.getExtras().getString(Constants.MM_Experience_data), UserDetails.ExperienceInfo.class));
                    experienceInfos.set(data.getExtras().getInt(Constants.MM_position), experienceInfo);
                    adapter.doRefresh(experienceInfos);
                }
            }
        }

        if (resultCode == getActivity().RESULT_OK && requestCode == 14532) {
            if (data.getExtras().containsKey(Constants.MM_give_rate) && data.getExtras().getBoolean(Constants.MM_give_rate)) {
                getUserDetails();
            }
        }

        if (resultCode == getActivity().RESULT_OK && requestCode == 21333) {
            getUserDetails();
        }

    }

    private void sendFriendRequest(final String userId) {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_request_user_id, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(getActivity());
        Globals.initRetrofit(getActivity()).sendFriendRequest(object).enqueue(new Callback<FriendRequest>() {
            @Override
            public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    friendStatus.setText("Friend request sent to " + userDetails.user.first_name + " " + userDetails.user.last_name + ".");
                    btnAccept.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FriendRequest> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptRejectRequest(final String userId, final int status) {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_request_user_id, userId);
            object.addProperty(Constants.MM_status, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(getActivity());
        Globals.initRetrofit(getActivity()).accpetRejectFriendRequest(object).enqueue(new Callback<FriendRequest>() {
            @Override
            public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {

                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (status == 0) {
                        friendStatus.setText("Send connect request to " + userDetails.user.first_name + " " + userDetails.user.last_name);
                        btnAccept.setText("Add Connect");
                        btnDelete.setVisibility(View.GONE);
                    } else {
                        friendStatus.setText("You and " + userDetails.user.first_name + " " + userDetails.user.last_name + " are friends.");
                        btnAccept.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                    }
                } else {
                    Utils.closeProgressDialog();
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FriendRequest> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBussinessUserDetails() {

        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_user_id, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(getActivity());
        Globals.initRetrofit(getActivity()).getBusinessProfile(object).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful() && response.body() != null && response.body().response_code == 200) {
                    basicResponse = response.body();
                    if (basicResponse != null) {
                        editPersonalInfo.setVisibility(View.GONE);
                        Glide.with(getActivity()).load(basicResponse.user.profile_image).into(ivProfile);
                        simpleRatingBar.setRating(basicResponse.user.avg_rating);
                        tvPosts.setText(String.valueOf(basicResponse.user.total_post));
                        tvFriends.setText(String.valueOf(basicResponse.user.total_friends));
                        tvName.setText(basicResponse.user.first_name + " " + basicResponse.user.last_name);
                        tvAboutName.setText("About " + basicResponse.user.first_name + " " + basicResponse.user.last_name);
                        btnAddExperience.setVisibility(View.GONE);
                        llexperience.setVisibility(View.GONE);
                        Glide.with(getActivity()).load(basicResponse.user.cover_image).into(ivCover);
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoginSialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.login_needed);
        builder.setMessage(R.string.do_sign_in_now);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

