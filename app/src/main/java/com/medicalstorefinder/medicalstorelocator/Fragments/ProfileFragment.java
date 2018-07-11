package com.medicalstorefinder.medicalstorelocator.Fragments;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.medicalstorefinder.medicalstorelocator.Constants.Constants;
import com.medicalstorefinder.medicalstorelocator.Constants.SharedPreference;
import com.medicalstorefinder.medicalstorelocator.Constants.Utilities;
import com.medicalstorefinder.medicalstorelocator.Models.UserProfile;
import com.medicalstorefinder.medicalstorelocator.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends android.support.v4.app.Fragment {

    UserProfile userProfile;
    TextView txtvuserId,txtvuserName,txtvFullName,txtvEmailID,txtvAdharCardNumber,txtvMobileNumber,txtvLocation,txtvcType,txtvcStatus,txtvActivated,txtvCreatedDate,txtvActivatedDate;
    CircleImageView profileImage;
    Bitmap bitmap12;
    String newpic;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    SharedPreference sharedPreference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.profile,null);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar_profile);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        sharedPreference = new SharedPreference();

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar_profile);
        collapsingToolbar.setTitle("Profile");

        txtvuserId=(TextView)v.findViewById(R.id.userid);
        txtvuserName=(TextView)v.findViewById(R.id.uname);
        txtvFullName=(TextView)v.findViewById(R.id.fullname);
        txtvEmailID=(TextView)v.findViewById(R.id.email);
        txtvAdharCardNumber=(TextView)v.findViewById(R.id.adhar_card_number);
        txtvMobileNumber=(TextView)v.findViewById(R.id.regmobile);
        txtvLocation=(TextView)v.findViewById(R.id.location);
        txtvcType=(TextView)v.findViewById(R.id.ctype);
        txtvcStatus=(TextView)v.findViewById(R.id.cstatus);
        txtvActivated=(TextView)v.findViewById(R.id.activated);
        txtvCreatedDate=(TextView)v.findViewById(R.id.created_date);
        txtvActivatedDate=(TextView)v.findViewById(R.id.activated_date);

        txtvuserId.setText(sharedPreference.getValue(getActivity().getApplicationContext(), Constants.PREF_ISAD, Constants.PREF_KEY_USER_Email));
        txtvuserName.setText(sharedPreference.getValue(getActivity().getApplicationContext(), Constants.PREF_ISAD, Constants.PREF_KEY_USER_NAME));

        profileImage=(CircleImageView)v.findViewById(R.id.profileImage);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        new ProfileRetrievedData().execute();

        return v;
    }

    private class LoadProfileImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                profileImage.setImageBitmap(image);
            }
        }
    }
    class ProfileRetrievedData extends AsyncTask<String,String,String>
    {
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            Utilities utilities = new Utilities( getActivity().getApplicationContext() );

            StringBuilder URL_ACCOUNT_GETPROFILEINFO = new StringBuilder( Constants.API_Account_IsAuthorised );
            URL_ACCOUNT_GETPROFILEINFO.append("?Id=1");
            URL_ACCOUNT_GETPROFILEINFO.append("&pppUserName=" + sharedPreference.getValue(getActivity().getApplicationContext(), Constants.PREF_ISAD, Constants.PREF_KEY_USER_Email) );
            URL_ACCOUNT_GETPROFILEINFO.append("&pppPassword=" + sharedPreference.getValue(getActivity().getApplicationContext(), Constants.PREF_ISAD, Constants.PREF_KEY_USER_PASS) );

            return utilities.apiCall( URL_ACCOUNT_GETPROFILEINFO.toString());
        }

        public void onPostExecute(String response) {

            try {

                if(response.equals("NO_INTERNET")) {
                    Toast.makeText( getActivity().getApplicationContext(), "Check internet connection", Toast.LENGTH_LONG).show();
                }
                else if(response.equals("ERROR") ) {
                    Toast.makeText( getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
                else {

                    JSONObject jsonObject = new JSONObject(response);

                    userProfile = new UserProfile();

                    userProfile.setFull_Name(jsonObject.getString("Full_Name"));
                    userProfile.setRegMobile(jsonObject.getString("Mobile_Number"));
                    userProfile.setEmail(jsonObject.getString("Email_Id"));
                    userProfile.setcStatus(jsonObject.getString("cStatus"));
                    userProfile.setcType(jsonObject.getString("cType"));
                    userProfile.setLocation(jsonObject.getString("Location"));
                    userProfile.setActivated(jsonObject.getString("Activated"));
                    userProfile.setAdhar_Card_Number(jsonObject.getString("Adhar_Card_Number"));
                    userProfile.setCreated_Date(jsonObject.getString("Created_Date"));
                    userProfile.setActivated_Date(jsonObject.getString("Activated_Date"));
                    userProfile.setProfilePicUrl(jsonObject.getString("photo"));

                    txtvFullName.setText(txtvFullName.getText() + " - " + userProfile.getFull_Name());
                    txtvEmailID.setText(txtvEmailID.getText() + " - " + userProfile.getEmail());
                    txtvMobileNumber.setText(txtvMobileNumber.getText() + " : " + userProfile.getRegMobile());
                    txtvLocation.setText(txtvLocation.getText() + " : " + userProfile.getLocation());
                    txtvcType.setText(txtvcType.getText() + " : " + userProfile.getcType());
                    txtvcStatus.setText(txtvcStatus.getText() + " : " + userProfile.getcStatus());
                    txtvActivated.setText(txtvActivated.getText() + " : " + userProfile.getActivated());
                    txtvAdharCardNumber.setText(txtvAdharCardNumber.getText() + " : " + userProfile.getAdhar_Card_Number());
                    txtvCreatedDate.setText(txtvCreatedDate.getText() + " : " + userProfile.getCreated_Date());
                    txtvActivatedDate.setText(txtvActivatedDate.getText() + " : " + userProfile.getActivated_Date());

                    new LoadProfileImage().execute(userProfile.getProfilePicUrl().replace("~", Constants.DOMAIN_NAME) );
                }
            } catch (JSONException e1) {
                Toast.makeText( getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
            finally {
                progressDialog.dismiss();
            }
        }
    }
}