package com.medicalstorefinder.medicalstoreslocatorss.Activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.medicalstorefinder.medicalstoreslocatorss.Constants.Constants;
import com.medicalstorefinder.medicalstoreslocatorss.Constants.SharedPreference;
import com.medicalstorefinder.medicalstoreslocatorss.R;

public class RoleSelectorActivity extends AppCompatActivity {
//    static int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selector);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

    }
    public static class PlaceholderFragment extends Fragment{
        SharedPreference sharedPreference;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_user_role_selector, container, false);

            Button serviceProviderBtn = (Button)rootView.findViewById(R.id.serviceProviderSelected);
            Button userBtn = (Button)rootView.findViewById(R.id.userSelected);


            sharedPreference = new SharedPreference();

            sharedPreference.clearSharedPreference(getActivity(), Constants.PREF_USER_ROLE);
            sharedPreference.createSharedPreference(getActivity(), Constants.PREF_USER_ROLE);


            serviceProviderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(count!=0) {
                    sharedPreference.putValue(getActivity(), Constants.PREF_USER_ROLE, Constants.PREF_USER_ROLE,"medical");

                    Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                   /* }else{
                        Toast.makeText(getActivity(), "Please select atleast one type", Toast.LENGTH_SHORT).show();
                    }*/
                }
            });

            userBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(count!=0) {

                    sharedPreference.putValue(getActivity(), Constants.PREF_USER_ROLE, Constants.PREF_USER_ROLE,"Customer");

                    Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                   /* }else{
                        Toast.makeText(getActivity(), "Please select atleast one type", Toast.LENGTH_SHORT).show();
                    }*/
                }
            });



           /* rootView.findViewById(R.id.userSelected).setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View view) {
                            rootView.findViewById(R.id.userSelected).setBackgroundResource(R.drawable.customer2);
                            rootView.findViewById(R.id.serviceProviderSelected).setBackgroundResource(R.drawable.medical);
                            sharedPreference.putValue(getActivity(), Constants.PREF_USER_ROLE, Constants.PREF_USER_ROLE,"Customer");

                            count=1;

                        }
                    });
                    rootView.findViewById(R.id.serviceProviderSelected).setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View view) {

                            rootView.findViewById(R.id.userSelected).setBackgroundResource(R.drawable.customer);
                            rootView.findViewById(R.id.serviceProviderSelected).setBackgroundResource(R.drawable.medical2);
                            sharedPreference.putValue(getActivity(), Constants.PREF_USER_ROLE, Constants.PREF_USER_ROLE,"ServiceProvider");

                            count=1;
                        }
                    });*/

            return rootView;
        }


    }

}
