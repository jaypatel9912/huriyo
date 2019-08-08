package com.huriyo.Utility;

import com.google.gson.JsonObject;
import com.huriyo.Model.BasicResponse;
import com.huriyo.Model.BussinessUser;
import com.huriyo.Model.Category;
import com.huriyo.Model.CommentRes;
import com.huriyo.Model.Feed;
import com.huriyo.Model.Friend;
import com.huriyo.Model.FriendRequest;
import com.huriyo.Model.Login;
import com.huriyo.Model.MediaRequest;
import com.huriyo.Model.MediaResponse;
import com.huriyo.Model.Post;
import com.huriyo.Model.PostResponse;
import com.huriyo.Model.PostWithComments;
import com.huriyo.Model.SearchPost;
import com.huriyo.Model.SearchUser;
import com.huriyo.Model.Suggestions;
import com.huriyo.Model.UpdatePicture;
import com.huriyo.Model.User;
import com.huriyo.Model.UserDetails;
import com.huriyo.Model.UserExperience;
import com.huriyo.Model.UserNotification;
import com.huriyo.Model.UserRating;
import com.huriyo.Model.VerifyOtp;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Jay on 21/11/17.
 */

public interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_socialLogin)
    Call<BasicResponse> socialLogin(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_register)
    Call<BasicResponse> doRegister(@Body User user);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_login)
    Call<BasicResponse> doLogin(@Body Login login);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_feedlist)
    Call<Feed> getFeeds(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_verifyOTP)
    Call<BasicResponse> verifyOtp(@Body VerifyOtp verifyOtp);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_resendOTP)
    Call<BasicResponse> resendOtp(@Body VerifyOtp verifyOtp);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_createPost)
    Call<PostResponse> createPost(@Body Post verifyOtp);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_uploadPostMedia)
    Call<MediaResponse> uploadPostMedia(@Body MediaRequest request);

    @Headers("Content-Type: application/json")
    @GET(Constants.MM_API_businessCategoryList)
    Call<Category> getCategories();

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_setBusinessCategory)
    Call<Object> setCategory(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_postDetails)
    Call<PostWithComments> getPostWithComments(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_postComment)
    Call<CommentRes> postComment(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_likePost)
    Call<BasicResponse> likePost(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_unlikePost)
    Call<BasicResponse> unlikePost(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_likeComment)
    Call<BasicResponse> likeComment(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_unlikeComment)
    Call<BasicResponse> unlikeComment(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_updatePicture)
    Call<UpdatePicture> updatePicture(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_getUserDetails)
    Call<UserDetails> getUserDetails(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_updateProfile)
    Call<BasicResponse> updateProfile(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_saveExperience)
    Call<UserExperience> saveExperience(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_updateExperience)
    Call<UserExperience> updateExperience(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_suggestionList)
    Call<Suggestions> getSuggestions(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_search)
    Call<SearchUser> doSearchUser(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_search)
    Call<SearchPost> doSearchPost(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_sendFriendRequest)
    Call<FriendRequest> sendFriendRequest(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_acceptRejectFriendRequest)
    Call<FriendRequest> accpetRejectFriendRequest(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_userNotificationList)
    Call<UserNotification> getUserNotifications(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_markAsReadNotification)
    Call<BasicResponse> markNotificationAsRead(@Body JsonObject object);

    @GET(Constants.MM_API_terms_of_use)
    Call<ResponseBody> getTermsAndConditions();

    @GET(Constants.MM_API_privacy_policy)
    Call<ResponseBody> getPrivacyPolicy();

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_changePassword)
    Call<BasicResponse> changePassword(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_userRatingList)
    Call<UserRating> userRatingList(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_saveUserRating)
    Call<UserRating> saveUserRating(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_friendList)
    Call<Friend> getFriendList(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_friendRequestList)
    Call<Friend> getFriendRequests(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_forgotPassword)
    Call<BasicResponse> forgotPassword(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_verifyForgotPasswordOTP)
    Call<BasicResponse> verifyForgotPasswordOtp(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_resetPassword)
    Call<BasicResponse> resetPassword(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_setBusinessInfo)
    Call<BasicResponse> setBussinessInfo(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_businessUserList)
    Call<BussinessUser> businessUserList(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_getBusinessProfile)
    Call<BasicResponse> getBusinessProfile(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_contactUs)
    Call<BasicResponse> contactUs(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_deleteComment)
    Call<BasicResponse> deleteComment(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_addUpdateAndroidDeviceToken)
    Call<Object> addUpdateAndroidDeviceToken(@Body JsonObject object);

    @Headers("Content-Type: application/json")
    @POST(Constants.MM_API_logout)
    Call<Object> logout(@Body JsonObject object);

}
