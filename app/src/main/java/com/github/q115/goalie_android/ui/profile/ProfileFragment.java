package com.github.q115.goalie_android.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.services.MessagingServiceUtil;
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/*
 * Copyright 2017 Qi Li
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

public class ProfileFragment extends Fragment implements ProfileFragmentView, MessagingServiceUtil.MessagingServiceListener {
    private ProfileFragmentPresenter mPresenter;

    private ImageView mEdit;
    private ImageView mProfile;
    private DelayedProgressDialog mProgressDialog;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        RecyclerView activityList = rootView.findViewById(R.id.profile_activity_list);
        activityList.setLayoutManager(new LinearLayoutManager(getContext()));
        activityList.setHasFixedSize(true);
        activityList.setAdapter(new ProfileFragmentRecycler(getActivity()));

        mEdit = rootView.findViewById(R.id.profile_edit);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
        mProfile = rootView.findViewById(R.id.profile_image);
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhoto();
            }
        });

        mProgressDialog = new DelayedProgressDialog();
        mProgressDialog.setCancelable(false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        showHideEmptyMessage();
        MessagingServiceUtil.setMessagingServiceListener("Profile", this);
    }

    @Override
    public void onDestroy() {
        MessagingServiceUtil.setMessagingServiceListener("Profile", null);
        super.onDestroy();
    }

    @Override
    public void setPresenter(ProfileFragmentPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setupView(String username, String bio, long points) {
        if (getView() != null) {
            View view = getView();
            ((TextView) view.findViewById(R.id.profile_username)).setText(username);
            ((TextView) view.findViewById(R.id.profile_bio)).setText(bio);
            ((TextView) view.findViewById(R.id.profile_points)).setText(String.format(getString(R.string.reputation), points));

            User user = UserHelper.getInstance().getAllContacts().get(username);
            if (user != null && user.profileBitmapImage != null)
                mProfile.setImageDrawable(ImageHelper.getRoundedCornerDrawable(
                        getResources(), user.profileBitmapImage, Constants.ROUNDED_PROFILE));
        }
    }

    @Override
    public void toggleOwnerSpecificFeatures(boolean isOwner) {
        mEdit.setEnabled(isOwner);
        mProfile.setEnabled(isOwner);
        mEdit.setVisibility(isOwner ? View.VISIBLE : View.INVISIBLE);
    }

    private void editProfile() {
        UpdateProfileDialog passwordDialog = new UpdateProfileDialog();
        Bundle bundle = new Bundle();
        bundle.putString("bio", UserHelper.getInstance().getOwnerProfile().bio);
        passwordDialog.setArguments(bundle);
        passwordDialog.setTargetFragment(ProfileFragment.this, Constants.RESULT_PROFILE_UPDATE);
        passwordDialog.show(getActivity().getSupportFragmentManager(), "UpdateProfileDialog");
    }

    private void changePhoto() {
        int cameraPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int storagePermission = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (cameraPermission != PackageManager.PERMISSION_GRANTED
                || storagePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSIONS_CAMERA_STORAGE);
        } else {
            String[] opsChars = {getString(R.string.take_photo), getString(R.string.choose_photo)};

            AlertDialog.Builder getImageFrom = new AlertDialog.Builder(getActivity());
            getImageFrom.setTitle(getString(R.string.select_photo));
            getImageFrom.setItems(opsChars, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    changePhotoActionSelected(i);
                }
            });
            getImageFrom.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_PERMISSIONS_CAMERA_STORAGE:
                boolean isAllPermissionsGranted = true;
                for (int grantResult : grantResults) {
                    isAllPermissionsGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
                }

                if (isAllPermissionsGranted)
                    changePhoto();
                else
                    Toast.makeText(getActivity(), getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }

    private void changePhotoActionSelected(int position) {
        if (position == 0) { //take photo
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                File newFile = mPresenter.getTempImageFileForOwner();
                Uri uri = FileProvider.getUriForFile(getActivity(), getString(R.string.file_provider), newFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(
                            activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(takePictureIntent, Constants.RESULT_PROFILE_IMAGE_TAKEN);
            }
        } else if (position == 1) { //select photo
            Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageIntent.setType("image/*");
            imageIntent.putExtra("return-data", true);
            imageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(
                    Intent.createChooser(imageIntent, getString(R.string.select_photo)),
                    Constants.RESULT_PROFILE_IMAGE_SELECTED);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RESULT_PROFILE_IMAGE_SELECTED) { // image selected. Crop the image
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                startImageCrop(data.getData());
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.image_selection_error), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.RESULT_PROFILE_IMAGE_TAKEN) { // image taken. Crop the image
            if (resultCode == RESULT_OK) {
                Uri uri = FileProvider.getUriForFile(getActivity(), getString(R.string.file_provider),
                        mPresenter.getTempImageFileForOwner());
                getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                startImageCrop(uri);
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.image_taking_error), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { // image cropped
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = ImageHelper.decodeSampledBitmapFromUri(getActivity().getContentResolver(),
                        result.getUri(), Constants.PROFILE_IMAGE_WIDTH, Constants.PROFILE_IMAGE_HEIGHT);
                mPresenter.newProfileImageSelected(bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Diagnostic.logError(Diagnostic.DiagnosticFlag.Other, "Image failed to be cropped");
                Toast.makeText(getActivity(), getString(R.string.image_selection_error), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.RESULT_PROFILE_UPDATE && resultCode == Activity.RESULT_OK) { // bio updated
            if (getView() != null) {
                ((TextView) getView().findViewById(R.id.profile_bio)).setText(data.getStringExtra("bio"));
                Toast.makeText(getActivity(), getString(R.string.updated), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startImageCrop(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .start(getActivity(), this);
    }

    @Override
    public void updateProgress(boolean shouldShow) {
        if (shouldShow) {
            mProgressDialog.show(getActivity().getSupportFragmentManager(), "DelayedProgressDialog");
        } else {
            mProgressDialog.cancel();
        }
    }

    public void uploadComplete(boolean isSuccessful, Bitmap image, String err) {
        if (isSuccessful) {
            mProfile.setImageDrawable(ImageHelper.getRoundedCornerDrawable(
                    getResources(), image, Constants.ROUNDED_PROFILE));
            Toast.makeText(getActivity(), getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), err, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void reloadList() {
        if (getView() != null) {
            RecyclerView profileList = getView().findViewById(R.id.profile_activity_list);
            ((ProfileFragmentRecycler) profileList.getAdapter()).notifyDataSetHasChanged();
            showHideEmptyMessage();
        }
    }

    private void showHideEmptyMessage() {
        if (getView() != null) {
            RecyclerView profileList = getView().findViewById(R.id.profile_activity_list);

            if (profileList.getAdapter().getItemCount() == 0) {
                profileList.setVisibility(View.GONE);
                getView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
            } else {
                profileList.setVisibility(View.VISIBLE);
                getView().findViewById(R.id.empty).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNotification() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getView() != null) {
                    ((TextView) getView().findViewById(R.id.profile_points)).setText(
                            String.format(getString(R.string.reputation), UserHelper.getInstance().getOwnerProfile().reputation));
                }
                reloadList();
            }
        });
    }
}
