package com.github.q115.goalie_android.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.services.MessagingServiceUtil;
import com.github.q115.goalie_android.ui.DelayedProgressDialog;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
    private DelayedProgressDialog mProgressDialog;
    private ProfileBioViewHolder mProfileBioViewHolder;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        View itemView = inflater.inflate(R.layout.list_header_profile_bio, container, false);
        mProfileBioViewHolder = new ProfileBioViewHolder(itemView, this);

        RecyclerView activityList = rootView.findViewById(R.id.profile_activity_list);
        activityList.setLayoutManager(new LinearLayoutManager(getContext()));
        activityList.setHasFixedSize(true);
        activityList.setAdapter(new ProfileFragmentHeaderRecycler(getActivity(), mProfileBioViewHolder));

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
        if (getView() != null && mProfileBioViewHolder != null) {
            mProfileBioViewHolder.setupUserProfile(username, bio, points);
        }
    }

    @Override
    public void toggleOwnerSpecificFeatures(boolean isOwner) {
        mProfileBioViewHolder.toggleOwnerSpecificFeatures(isOwner);
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
                    mProfileBioViewHolder.changePhoto();
                else
                    Toast.makeText(getActivity(), getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
            default:
                break;
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
                        ImageHelper.getInstance().getTempImageFileForUser(UserHelper.getInstance().getOwnerProfile().username));
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
                mProfileBioViewHolder.bioUpdated(data.getStringExtra("bio"));
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
            mProfileBioViewHolder.uploadCompleted(image);
            Toast.makeText(getActivity(), getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), err, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void reloadList() {
        if (getView() != null) {
            RecyclerView profileList = getView().findViewById(R.id.profile_activity_list);
            ((ProfileFragmentHeaderRecycler) profileList.getAdapter()).notifyDataSetHasChanged();
            showHideEmptyMessage();
        }
    }

    @Override
    public void onNotification() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getView() != null) {
                    mProfileBioViewHolder.pointUpdated(UserHelper.getInstance().getOwnerProfile().reputation);
                }
                reloadList();
            }
        });
    }

    private void showHideEmptyMessage() {
        if (getView() != null && mProfileBioViewHolder != null) {
            RecyclerView profileList = getView().findViewById(R.id.profile_activity_list);
            mProfileBioViewHolder.toggleEmptyView(profileList.getAdapter().getItemCount() == 1);
        }
    }
}
