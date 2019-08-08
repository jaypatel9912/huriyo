package com.huriyo.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huriyo.Model.GalleryItem;
import com.huriyo.Model.MediaRequest;
import com.huriyo.Model.MediaResponse;
import com.huriyo.Model.Post;
import com.huriyo.Model.PostResponse;
import com.huriyo.Model.User;
import com.huriyo.R;
import com.huriyo.Ui.GalleryActivity;
import com.huriyo.Ui.HomeActivity;
import com.huriyo.Ui.PersonaInformationActivity;
import com.huriyo.Utility.CameraEntity;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.FragmentBase;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.PathUtil;
import com.huriyo.Utility.Picture;
import com.huriyo.Utility.Screen;
import com.huriyo.Utility.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddPostFragment extends FragmentBase<HomeActivity> implements View.OnClickListener {

    Button btnPublish;
    LinearLayout llVideo;
    RelativeLayout llGallery;
    EditText edPost;
    View view;
    ArrayList<GalleryItem> selectedList;
    int cnt = 0;
    User user;
    ArrayList<String> images;
    Globals globals;
    TextView tvCounter;
    int quality = 50;
    private Picture picture;
    public static CameraEntity cameraEntity = null;
    ArrayList<String> selectedImages = new ArrayList<>();

    public static AddPostFragment newInstance() {
        AddPostFragment fragment = new AddPostFragment();
        return fragment;
    }

    @Override
    public Screen getCode() {
        return Screen.NEW_POST;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (globals == null) {
            globals = ((Globals) getActivity().getApplicationContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addpost, container, false);
        user = new Gson().fromJson(Utils.getPreference(getActivity(), Constants.MM_UserDate), User.class);
        btnPublish = (Button) view.findViewById(R.id.btnPublish);
        btnPublish.setOnClickListener(this);

        tvCounter = (TextView) view.findViewById(R.id.tvCounter);
        picture = new Picture(getActivity());

        llVideo = (LinearLayout) view.findViewById(R.id.llVideo);
        llVideo.setOnClickListener(this);
        llGallery = (RelativeLayout) view.findViewById(R.id.llGallery);
        llGallery.setOnClickListener(this);
        images = new ArrayList<>();
        edPost = (EditText) view.findViewById(R.id.edPost);

        if (Utils.getPreference(getActivity(), Constants.MM_quality).equalsIgnoreCase("1"))
            quality = 40;
        else if (Utils.getPreference(getActivity(), Constants.MM_quality).equalsIgnoreCase("2"))
            quality = 60;
        else if (Utils.getPreference(getActivity(), Constants.MM_quality).equalsIgnoreCase("3"))
            quality = 80;
        return view;
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(Utils.getClickRipple(getActivity()));
        switch (view.getId()) {
            case R.id.btnPublish:
                Utils.hideSoftKeyboard(getActivity());
                Utils.showProgressDialog(getActivity());
                if (selectedList != null && selectedList.size() > 0) {
                    uploadPhoto();
                    return;
                }
                postFeed();
                break;

            case R.id.llVideo:
                picture.intentCamera();
                break;
            case R.id.llGallery:
                getActivity().startActivityForResult(new Intent(getActivity(), GalleryActivity.class), 10101);
                break;
        }
    }

    private void showDialogOption() {
        final String[] items = getResources().getStringArray(R.array.camera_option2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.select_dialog_item_material, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.picker_select_image));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                try {
                    if (item == 0) {
                        picture.intentCamera();
                    } else {
                        File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ " + System.currentTimeMillis() + ".mp4");
//                        videoPath = mediaFile.getAbsolutePath();
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                        Uri videoUri = Uri.fromFile(mediaFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                        getActivity().startActivityForResult(intent, 19514);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == 19514) {
//            if (selectedList == null)
//                selectedList = new ArrayList<>();
//
//            if (videoPath == null)
//                return;
//
//            GalleryItem item = new GalleryItem();
//            item.setPath(videoPath);
//            item.setType(2);
//            item.setSelected(true);
//            if (selectedImages.contains(videoPath)) {
//                return;
//            }
//            selectedImages.add(videoPath);
//            selectedList.add(item);
//            if (selectedList != null && selectedList.size() > 0) {
//                tvCounter.setVisibility(View.VISIBLE);
//                tvCounter.setText(String.valueOf(selectedList.size()));
//            }
//            videoPath = null;
        } else if (resultCode == getActivity().RESULT_OK && requestCode == 10101) {
            Type type = new TypeToken<ArrayList<GalleryItem>>() {
            }.getType();

            if (selectedList == null)
                selectedList = new ArrayList<>();

            ArrayList<GalleryItem> list = new Gson().fromJson(data.getStringExtra(Constants.MM_Data), type);

            for (GalleryItem item : list) {
                if (selectedImages.contains(item.getPath())) {
                    return;
                }
                selectedImages.add(item.getPath());
                selectedList.add(item);
            }

            if (selectedList != null && selectedList.size() > 0) {
                tvCounter.setVisibility(View.VISIBLE);
                tvCounter.setText(String.valueOf(selectedList.size()));
            }
        } else {
            if (requestCode != 203) {
                this.picture.result(requestCode, resultCode, data, new PictureListener());
            } else if (resultCode == -1) {
                try {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (result != null) {
                        if (cameraEntity == null) {
                            cameraEntity = new CameraEntity();
                        }
                        cameraEntity.setPath(PathUtil.getPath(getActivity(), result.getUri()));
                        cameraEntity.setCropped(true);

                        if (selectedList == null)
                            selectedList = new ArrayList<>();

                        GalleryItem item = new GalleryItem();
                        item.setPath(PathUtil.getPath(getActivity(), result.getUri()));
                        item.setType(1);
                        item.setSelected(true);
                        if (selectedImages.contains(PathUtil.getPath(getActivity(), result.getUri()))) {
                            return;
                        }
                        selectedImages.add(PathUtil.getPath(getActivity(), result.getUri()));
                        selectedList.add(item);
                        if (selectedList != null && selectedList.size() > 0) {
                            tvCounter.setVisibility(View.VISIBLE);
                            tvCounter.setText(String.valueOf(selectedList.size()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void postFeed() {

        String postText = edPost.getText().toString().trim();
        if ((images == null || images.size() <= 0) && (postText == null || postText.isEmpty())) {
            edPost.setError(getString(R.string.err_post_text));
            Utils.closeProgressDialog();
            return;
        }

        Post post = new Post();

        post.user_id = user._id;
        post.post_text = edPost.getText().toString().trim();
        post.post_media = images;
        post.latitude = globals.getLatitude();
        post.longitude = globals.getLongitude();
        Globals.initRetrofit(getActivity()).createPost(post).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    activity.selectHome();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                t.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void uploadPhoto() {

        if (cnt >= selectedList.size()) {
            postFeed();
            return;
        }
        boolean isVideo = false;
        if (selectedList.get(cnt).getType() == 2)
            isVideo = true;
        try {
            File file = new File(selectedList.get(cnt).getPath());
            if (!file.exists()) {
                cnt++;
                uploadPhoto();
                return;
            }
            String temp = null;
            Bitmap bitmap = null;
            if (isVideo) {
                FileInputStream objFileIS = null;
                try {
                    String selectedImagePath = file.getAbsolutePath();
                    System.out.println("file = >>>> <<<<<" + selectedImagePath);
                    objFileIS = new FileInputStream(selectedImagePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream objByteArrayOS = new ByteArrayOutputStream();
                byte[] byteBufferString = new byte[1024];
                try {
                    for (int readNum; (readNum = objFileIS.read(byteBufferString)) != -1; ) {
                        objByteArrayOS.write(byteBufferString, 0, readNum);
                        System.out.println("read " + readNum + " bytes,");
                    }
                    temp = Base64.encodeToString(byteBufferString, Base64.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                bitmap = getBitmapFromPath(file);

                if (bitmap == null) {
                    Utils.closeProgressDialog();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    return;
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                byte[] byteArray = out.toByteArray();
                temp = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }


            Log.d("", "uploadPhoto: " + temp);

            if (temp == null || temp.isEmpty()) {
                cnt++;
                uploadPhoto();
                return;
            }

            MediaRequest request = new MediaRequest();
            request.user_id = user._id;
            request.base64 = temp;
            if (bitmap != null)
                request.height = bitmap.getHeight();

            request.media_type = isVideo ? "video" : "image";
            request.extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));

            Globals.initRetrofit(getActivity()).uploadPostMedia(request).enqueue(new Callback<MediaResponse>() {
                @Override
                public void onResponse(Call<MediaResponse> call, Response<MediaResponse> response) {
                    if (response.isSuccessful() && response.body().response_code == 200) {
                        images.add(response.body().media._id);
                    }
                    cnt++;
                    uploadPhoto();
                }

                @Override
                public void onFailure(Call<MediaResponse> call, Throwable t) {
                    t.printStackTrace();
                    cnt++;
                    uploadPhoto();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromPath(File fIle) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(fIle.getAbsolutePath(), bmOptions);
    }

    class PictureListener implements Picture.OnPictureUpdate {
        PictureListener() {
        }

        public void onUpdateCamera(String path) {
            savePicture(path, false, Picture.REQUEST_CAMERA);
        }

        public void onUpdateGallery(Uri uri) {
            savePicture(uri.toString(), false, Picture.REQUEST_GALLERY);
        }

        private void savePicture(String path, boolean cropped, int request) {
            if (cameraEntity == null) {
                cameraEntity = new CameraEntity();
            }
            cameraEntity.setPath(path);
            cameraEntity.setCropped(cropped);
            cameraEntity.setRequestType(request);
            int from = cameraEntity.getRequestType();
            if (cropped) {
//                if (selectedList == null)
//                    selectedList = new ArrayList<>();
//
//                GalleryItem item = new GalleryItem();
//                item.setPath(cameraEntity.getPath());
//                item.setType(1);
//                item.setSelected(true);
//                if (selectedImages.contains(videoPath)) {
//                    return;
//                }
//                selectedImages.add(videoPath);
//                selectedList.add(item);
            } else {
                CropImage.activity(from == Picture.REQUEST_CAMERA ? Uri.fromFile(new File(path)) : Uri.parse(path)).setCropShape(CropImageView.CropShape.RECTANGLE).setGuidelines(CropImageView.Guidelines.ON).setRequestedSize(400, 400).setFixAspectRatio(false).start(getActivity());
            }
        }
    }
}
