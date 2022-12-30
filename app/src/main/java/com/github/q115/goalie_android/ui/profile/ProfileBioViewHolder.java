package com.github.q115.goalie_android.ui.profile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.io.File;
import java.util.List;

/*
 * Copyright 2018 Qi Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ProfileBioViewHolder extends RecyclerView.ViewHolder {
    private View itemView;
    private Fragment fragment;
    private ImageView mEdit;
    private ImageView mProfile;
    private TextView emptyView;

    public ProfileBioViewHolder(View itemView, Fragment fragment) {
        super(itemView);

        this.itemView = itemView;
        this.fragment = fragment;
        mEdit = itemView.findViewById(R.id.profile_edit);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
        mProfile = itemView.findViewById(R.id.profile_image);
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhoto();
            }
        });
        emptyView = itemView.findViewById(R.id.empty);
    }

    public void setupUserProfile(String username, String bio, long points) {
        ((TextView) itemView.findViewById(R.id.profile_username)).setText(username);
        ((TextView) itemView.findViewById(R.id.profile_bio)).setText(bio);
        ((TextView) itemView.findViewById(R.id.profile_points)).setText(String.format(fragment.getString(R.string.reputation), points));

        User user = UserHelper.getInstance().getAllContacts().get(username);
        if (user != null && user.profileBitmapImage != null)
            mProfile.setImageDrawable(ImageHelper.getRoundedCornerDrawable(
                    fragment.getResources(), user.profileBitmapImage, Constants.ROUNDED_PROFILE));
    }


    private void editProfile() {
        UpdateProfileDialog passwordDialog = new UpdateProfileDialog();
        Bundle bundle = new Bundle();
        bundle.putString("bio", UserHelper.getInstance().getOwnerProfile().bio);
        passwordDialog.setArguments(bundle);
        passwordDialog.setTargetFragment(fragment, Constants.RESULT_PROFILE_UPDATE);
        passwordDialog.show(fragment.getActivity().getSupportFragmentManager(), "UpdateProfileDialog");
    }

    public void changePhoto() {
        int cameraPermission = ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.CAMERA);
        int storagePermission = ActivityCompat.checkSelfPermission(fragment.getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (cameraPermission != PackageManager.PERMISSION_GRANTED
                || storagePermission != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSIONS_CAMERA_STORAGE);
        } else {
            String[] opsChars = {fragment.getString(R.string.take_photo), fragment.getString(R.string.choose_photo)};

            AlertDialog.Builder getImageFrom = new AlertDialog.Builder(fragment.getActivity());
            getImageFrom.setTitle(fragment.getString(R.string.select_photo));
            getImageFrom.setItems(opsChars, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    changePhotoActionSelected(i);
                }
            });
            getImageFrom.show();
        }
    }

    private void changePhotoActionSelected(int position) {
        if (position == 0) { //take photo
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
                File newFile = ImageHelper.getInstance().getTempImageFileForUser(UserHelper.getInstance().getOwnerProfile().username);
                Uri uri = FileProvider.getUriForFile(fragment.getActivity(), fragment.getString(R.string.file_provider), newFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = fragment.getActivity().getPackageManager()
                        .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    fragment.getActivity().grantUriPermission(
                            activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                fragment.startActivityForResult(takePictureIntent, Constants.RESULT_PROFILE_IMAGE_TAKEN);
            }
        } else if (position == 1) { //select photo
            Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageIntent.setType("image/*");
            imageIntent.putExtra("return-data", true);
            imageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            fragment.startActivityForResult(
                    Intent.createChooser(imageIntent, fragment.getString(R.string.select_photo)),
                    Constants.RESULT_PROFILE_IMAGE_SELECTED);
        }
    }

    public void toggleOwnerSpecificFeatures(boolean enable) {
        mEdit.setEnabled(enable);
        mEdit.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        mProfile.setEnabled(enable);
    }

    public void uploadCompleted(Bitmap image) {
        mProfile.setImageDrawable(ImageHelper.getRoundedCornerDrawable(
                fragment.getResources(), image, Constants.ROUNDED_PROFILE));
    }

    public void bioUpdated(String newBio) {
        ((TextView) itemView.findViewById(R.id.profile_bio)).setText(newBio);
    }

    public void pointUpdated(long reputation) {
        ((TextView) itemView.findViewById(R.id.profile_points)).setText(
                String.format(fragment.getString(R.string.reputation), reputation));
    }

    public void toggleEmptyView(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
