package com.medicalstorefinder.medicalstoreslocatorss.Fragments;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.medicalstorefinder.medicalstoreslocatorss.Activity.MainActivity;
import com.medicalstorefinder.medicalstoreslocatorss.Constants.Constants;
import com.medicalstorefinder.medicalstoreslocatorss.Constants.CustomToast;
import com.medicalstorefinder.medicalstoreslocatorss.Constants.Utilities;
import com.medicalstorefinder.medicalstoreslocatorss.Constants.Utils1;
import com.medicalstorefinder.medicalstoreslocatorss.Geofencing;
import com.medicalstorefinder.medicalstoreslocatorss.Models.ApiUser;
import com.medicalstorefinder.medicalstoreslocatorss.Adapter.PlaceListAdapter;
import com.medicalstorefinder.medicalstoreslocatorss.Provider.PlaceContract;
import com.medicalstorefinder.medicalstoreslocatorss.Provider.PlaceDbHelper;
import com.medicalstorefinder.medicalstoreslocatorss.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class SignUp_Fragment extends Fragment implements OnClickListener,GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	public static final String TAG = MainActivity.class.getSimpleName();
	private static final int PLACE_PICKER_REQUEST = 1;

	// Member variables
	private PlaceListAdapter mAdapter;
	private RecyclerView mRecyclerView;
	private GoogleApiClient mClient;
	private Geofencing mGeofencing;

	private static View view;
	private static EditText firstName,lastName, emailId, mobileNumber, shopName,address,latitude,langitude,
			password, confirmPassword;
	private static TextView login;
	private static Button signUpButton;
	private static Button addLocationBtn;
	private static CheckBox terms_conditions;

	private static FragmentManager fragmentManager;
	int pos;
	ApiUser user = new ApiUser();

	public String res="";

	ProgressDialog progressDialog;

	public SignUp_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.signup_layout, container, false);
		initViews();


		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		setListeners();

		PlaceDbHelper mPlaceDbHelper = new PlaceDbHelper(getContext());
		SQLiteDatabase db = mPlaceDbHelper.getWritableDatabase();
		db.delete(PlaceContract.PlaceEntry.TABLE_NAME, null, null);
//            db.execSQL("DROP TABLE IF EXISTS places");
		db.close();

		// Set up the recycler view
		mRecyclerView = (RecyclerView) view.findViewById(R.id.address);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mAdapter = new PlaceListAdapter(getActivity(), null);
		mRecyclerView.setAdapter(mAdapter);


		mClient = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.addApi(Places.GEO_DATA_API)
				.enableAutoManage(getActivity(), this)
				.build();

		mGeofencing = new Geofencing(getActivity(), mClient);
		mGeofencing.registerAllGeofences();

		return view;
	}

	// Initialize all views
	private void initViews() {
		firstName = (EditText) view.findViewById(R.id.firstName);
		lastName = (EditText) view.findViewById(R.id.lastName);
		emailId = (EditText) view.findViewById(R.id.userEmailId);
		mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
		shopName = (EditText) view.findViewById(R.id.shop_name);
//		address = (EditText) view.findViewById(R.id.address);
		password = (EditText) view.findViewById(R.id.password);
		confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
		signUpButton = (Button) view.findViewById(R.id.signUpBtn);
		addLocationBtn=(Button)view.findViewById(R.id.addLocationBtn);
		login = (TextView) view.findViewById(R.id.already_user);
		terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
	}

	// Set Listeners
	private void setListeners() {
		signUpButton.setOnClickListener(this);
		addLocationBtn.setOnClickListener(this);
		login.setOnClickListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		mClient.stopAutoManage(getActivity());
		mClient.disconnect();
	}
	@Override
	public void onStart() {
		super.onStart();

		mClient.stopAutoManage(getActivity());
		mClient.connect();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.addLocationBtn:

			if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
					!= PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(getContext(), getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
				return;
			}
			try {

				PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
				Intent i = builder.build(getActivity());
				startActivityForResult(i, PLACE_PICKER_REQUEST);
			} catch (GooglePlayServicesRepairableException e) {
				Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
			} catch (GooglePlayServicesNotAvailableException e) {
				Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
			} catch (Exception e) {
				Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
			}

			break;

		case R.id.signUpBtn:

// Call checkValidation method
			checkValidation();

			user.setFirst_Name(firstName.getText().toString());
			user.setLast_Name(lastName.getText().toString());
			user.setShop_Name(shopName.getText().toString());
			user.setEmail(emailId.getText().toString());
			user.setRegMobile(mobileNumber.getText().toString());


            String title = ((TextView) mRecyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.name_text_view)).getText().toString();
			String address = ((TextView) mRecyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.address_text_view)).getText().toString();
			String latLongitude = ((TextView) mRecyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.latLong_text_view)).getText().toString();

			String answer = latLongitude.substring(latLongitude.indexOf("(")+1,latLongitude.indexOf(")"));
			String[] s = answer.split(",");
			String lat = s[0];
			String longi = s[1];

			user.setLatitude(lat);
			user.setLongitude(longi);

            user.setAddress(title +","+address);
			user.setPasswords(password.getText().toString());
			break;

		case R.id.already_user:
			// Replace login fragment

			mClient.stopAutoManage(getActivity());
			mClient.disconnect();

			new MainActivity().replaceLoginFragment();
			break;
		}

	}

	// Check Validation Method
	private void checkValidation() {
			// Get all edittext texts
			String getFirstName = firstName.getText().toString();
			String getLastName = lastName.getText().toString();
			String getEmailId = emailId.getText().toString();
			String getMobileNumber = mobileNumber.getText().toString();

			String title = ((TextView) mRecyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.name_text_view)).getText().toString();
			String address = ((TextView) mRecyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.address_text_view)).getText().toString();
			String latLongitude = ((TextView) mRecyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.latLong_text_view)).getText().toString();

			String answer = latLongitude.substring(latLongitude.indexOf("(")+1,latLongitude.indexOf(")"));
			String[] s = answer.split(",");
			String lat = s[0];
			String longi = s[1];

			String getLatitude =lat;
			String getLongitude =longi;

			String getAddress =title +","+address;
			String getShopName = shopName.getText().toString();
			String getPassword = password.getText().toString();
			String getConfirmPassword = confirmPassword.getText().toString();
			// Pattern match for email id
			Pattern p = Pattern.compile(Utils1.regEx);
			Matcher m = p.matcher(getEmailId);

			// Check if all strings are null or not
			if (getFirstName.equals("") || getFirstName.length() == 0
					|| getLastName.equals("") || getLastName.length() == 0
					|| getEmailId.equals("") || getEmailId.length() == 0
					|| getMobileNumber.equals("") || getMobileNumber.length() == 0
					|| getAddress.equals("") || getAddress.length() == 0
					|| getShopName.equals("") || getShopName.length() == 0
					|| getPassword.equals("") || getPassword.length() == 0
					|| getConfirmPassword.equals("")
					|| getConfirmPassword.length() == 0)

				new CustomToast().Show_Toast(getActivity(), view,
						"All fields are required.");

				// Check if email id valid or not
			else if (!m.find())
				new CustomToast().Show_Toast(getActivity(), view,
						"Your Email Id is Invalid.");

				// Check if both password should be equal
			else if (!getConfirmPassword.equals(getPassword))
				new CustomToast().Show_Toast(getActivity(), view,
						"Both password doesn't match.");

				// Make sure user should check Terms and Conditions checkbox
			else if (!terms_conditions.isChecked())
				new CustomToast().Show_Toast(getActivity(), view,
						"Please select Terms and Conditions.");

				// Else do signup or do your stuff
			else
				new SetSignup().execute();

	}

	@Override
	public void onConnected(@Nullable Bundle connectionHint) {
		refreshPlacesData();
		Log.i(TAG, "API Client Connection Successful!");
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "API Client Connection Suspended!");
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult result) {
		Log.e(TAG, "API Client Connection Failed!");
	}

	public void refreshPlacesData() {
		Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
		Cursor data = getActivity().getContentResolver().query(
				uri,
				null,
				null,
				null,
				null);

		if (data == null || data.getCount() == 0) return;
		List<String> guids = new ArrayList<String>();
		while (data.moveToNext()) {
			guids.add(data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID)));
		}

		PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mClient,
				guids.toArray(new String[guids.size()]));
		placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
			@Override
			public void onResult(@NonNull PlaceBuffer places) {
				mAdapter.swapPlaces(places);
				mGeofencing.updateGeofencesList(places);
				mGeofencing.registerAllGeofences();
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
			Place place = PlacePicker.getPlace(getContext(), data);
			if (place == null) {
				Log.i(TAG, "No place selected");
				return;
			}

			String placeID = place.getId();

			// Insert a new place into DB
			ContentValues contentValues = new ContentValues();
			contentValues.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeID);
			getActivity().getContentResolver().delete(PlaceContract.PlaceEntry.CONTENT_URI,null,null);
			getActivity().getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, contentValues);
			// Get live data information
			refreshPlacesData();
		}
	}

	public final class SetSignup extends AsyncTask<String,String,String>
	{
		protected void onPreExecute() {
				progressDialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {

			Utilities utilities = new Utilities(getContext());

			String address = Constants.API_SIGN_UP;
			Map<String, String> params = new HashMap<>();
			params.put("firstname", user.getFirst_Name());
			params.put("lastname", user.getLast_Name());
			params.put("shopname", user.getShop_Name());
			params.put("email", user.getEmail());
			params.put("password", user.getPasswords());
			params.put("mobile", user.getRegMobile());
			params.put("address", user.getAddress());
			params.put("latitude", user.getLatitude());
			params.put("longitude", user.getLongitude());

			return utilities.apiCalls(address,params);
		}

		public void onPostExecute(String response) {

			try {
				JSONObject jsonObject1 = new JSONObject(response);
				JSONObject jsonObject2 = new JSONObject(jsonObject1.getString("Content"));



				if(response.equalsIgnoreCase("ERROR") ) {
//                    Toast.makeText(getContext(), "Please try again later config 2", Toast.LENGTH_LONG).show();
				}else if (jsonObject2.getString("status").equalsIgnoreCase("error")) {
					Toast.makeText(getContext(), jsonObject2.getString("message"), Toast.LENGTH_LONG).show();
				}
				else if (jsonObject2.getString("status").equalsIgnoreCase("success")) {
                    Toast.makeText(getContext(), jsonObject2.getString("message"), Toast.LENGTH_LONG).show();
					fragmentManager = getActivity().getSupportFragmentManager();

					fragmentManager
							.beginTransaction()
							.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
							.replace(R.id.frameContainer, new Login_Fragment(),
									Utils1.Login_Fragment).commit();

				}

			} catch (Exception e1) {

//                Toast.makeText( getContext(), "Please try again later config 3", Toast.LENGTH_LONG).show();

			}
			finally {
				progressDialog.dismiss();

			}

		}
	}
}
