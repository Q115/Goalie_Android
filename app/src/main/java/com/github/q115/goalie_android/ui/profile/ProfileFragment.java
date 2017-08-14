package com.github.q115.goalie_android.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Qi on 8/4/2017.
 */

public class ProfileFragment extends Fragment implements ProfileView {
    private ProfilePresenter mPresenter;

    private ImageView mEdit;
    private ImageView mProfile;
    private ProgressDialog mProgressDialog;

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
        activityList.setAdapter(new ProfileActivitiesRecycler(getActivity()));

        View.OnClickListener mEditInfo = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfileDialog passwordDialog = new UpdateProfileDialog();
                Bundle bundle = new Bundle();
                bundle.putString("bio", UserHelper.getInstance().getOwnerProfile().bio);
                passwordDialog.setArguments(bundle);
                passwordDialog.setTargetFragment(ProfileFragment.this, Constants.RESULT_PROFILE_UPDATE);
                passwordDialog.show(getActivity().getSupportFragmentManager(), "UpdateProfileDialog");
            }
        };

        View.OnClickListener mEditProfileImage = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhoto();
            }
        };
        mEdit = rootView.findViewById(R.id.profile_edit);
        mEdit.setOnClickListener(mEditInfo);
        mProfile = rootView.findViewById(R.id.profile_image);
        mProfile.setOnClickListener(mEditProfileImage);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.uploading));
        mProgressDialog.setCancelable(false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        reloadList(false);
    }

    @Override
    public void setPresenter(ProfilePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setupForOwner(boolean isOwner) {
        mEdit.setEnabled(isOwner);
        mProfile.setEnabled(isOwner);

        mEdit.setVisibility(isOwner ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setupView(String username, String bio, long points, ArrayList<Goal> goalList) {
        if (getView() != null) {
            View view = getView();
            ((TextView) view.findViewById(R.id.profile_username)).setText(username);
            ((TextView) view.findViewById(R.id.profile_bio)).setText(bio);
            ((TextView) view.findViewById(R.id.profile_points)).setText(String.format(getString(R.string.reputation), points));

            User user = UserHelper.getInstance().getAllContacts().get(username);
            if (user != null && user.profileBitmapImage != null)
                mProfile.setImageDrawable(ImageHelper.getRoundedCornerBitmap(getResources(), user.profileBitmapImage, Constants.ROUNDED_PROFILE));

            ProfileActivitiesRecycler par = (ProfileActivitiesRecycler) ((RecyclerView) view.findViewById(R.id.profile_activity_list)).getAdapter();
            par.notifyDataSetChanged(goalList);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RESULT_PROFILE_IMAGE_SELECTED) { // image taken or selected. Crop the image
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .start(getActivity(), this);
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.image_selection_error), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.RESULT_PROFILE_IMAGE_TAKEN) {
            if (resultCode == RESULT_OK) {
                File newFile = new File(ImageHelper.getInstance().getImagePrivateStorageDirectory(UserHelper.getInstance().getOwnerProfile().username + "Temp.png"));
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.github.q115.goalie_android.fileprovider", newFile);
                getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .start(getActivity(), this);
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.image_selection_error), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final Bitmap bitmap = ImageHelper.decodeSampledBitmapFromUri(getActivity().getContentResolver(),
                        resultUri, Constants.PROFILE_IMAGE_WIDTH, Constants.PROFILE_IMAGE_HEIGHT);
                mPresenter.newProfileImageSelected(bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                Exception error = result.getError();
                Diagnostic.logError(Diagnostic.DiagnosticFlag.Other, "Image failed to be selected null" + error.toString());
                Toast.makeText(getActivity(), getString(R.string.image_selection_error), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.RESULT_PROFILE_UPDATE && resultCode == Activity.RESULT_OK) {
            if (getView() != null) {
                ((TextView) getView().findViewById(R.id.profile_bio)).setText(data.getAction());
                Toast.makeText(getActivity(), getString(R.string.updated), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changePhoto() {
        if (requestCameraPermission() && requestStoragePermission()) {
            AlertDialog.Builder getImageFrom = new AlertDialog.Builder(getActivity());
            getImageFrom.setTitle("Change photo:");

            String[] opsChars = {"Capture photo using Camera", "Choose photo from Gallery"};
            getImageFrom.setItems(opsChars, changePhotoActionSelected());
            getImageFrom.show();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DialogInterface.OnClickListener changePhotoActionSelected() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) { //take photo
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File newFile = new File(ImageHelper.getInstance().getImagePrivateStorageDirectory(UserHelper.getInstance().getOwnerProfile().username + "Temp.png"));
                        if (!newFile.exists()) {
                            newFile.getParentFile().mkdirs();
                        }
                        Uri uri = FileProvider.getUriForFile(getActivity(), "com.github.q115.goalie_android.fileprovider", newFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                        List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                                .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo activity : cameraActivities) {
                            getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }

                        startActivityForResult(takePictureIntent, Constants.RESULT_PROFILE_IMAGE_TAKEN);
                    }
                } else if (i == 1) { //select photo
                    Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    imageIntent.setType("image/*");
                    imageIntent.putExtra("return-data", true);
                    imageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(Intent.createChooser(imageIntent, "Select photo"), Constants.RESULT_PROFILE_IMAGE_SELECTED);
                }
            }
        };
    }

    private boolean requestCameraPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_PERMISSIONS_CAMERA);
            return false;
        } else {
            return true;
        }
    }

    private boolean requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSIONS_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_PERMISSIONS_STORAGE:
            case Constants.REQUEST_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    changePhoto();
                }
            default:
                break;
        }
    }

    @Override
    public void updateProgress(boolean shouldShow) {
        if (shouldShow) {
            mProgressDialog.show();
        } else if (mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    public void showUploadError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void uploadSuccess(Bitmap image) {
        mProfile.setImageDrawable(ImageHelper.getRoundedCornerBitmap(getResources(), image, Constants.ROUNDED_PROFILE));
        Toast.makeText(getActivity(), getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reloadList(boolean shouldReloadList) {
        if (getView() != null) {
            RecyclerView profileList = getView().findViewById(R.id.profile_activity_list);

            if (profileList.getAdapter().getItemCount() == 0) {
                profileList.setVisibility(View.GONE);
                getView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
            } else {
                profileList.setVisibility(View.VISIBLE);
                getView().findViewById(R.id.empty).setVisibility(View.GONE);
            }

            if (shouldReloadList)
                profileList.getAdapter().notifyDataSetChanged();
        }
    }
}