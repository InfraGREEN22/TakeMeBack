package acr18as.sheffield.ac.uk.takemeback.view;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.receiver.ARBroadcastReceiver;
import acr18as.sheffield.ac.uk.takemeback.service.BackgroundDetectedActivitiesService;
import acr18as.sheffield.ac.uk.takemeback.service.DetectedActivitiesIntentService;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.VisitedLocationViewModel;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static String TAG = "SettingsFragment";

    // Status of the automatic mode and directions mode;
    public static int AUTOMODE = 0;
    public static int DIRECTIONS_MODE = 0;

    private RadioButton radioWalkingMode;
    private RadioButton radioDrivingMode;
    private RadioGroup radioGroup;
    private Switch modeSwitch;
    private Button clearDatabaseButton;

    private ARBroadcastReceiver broadcastReceiver;

    private VisitedLocationViewModel visitedLocationViewModel;

    private static Fragment fragment;
    public static void setFragment(Fragment fragment) {
        SettingsFragment.fragment = fragment;
    }
    public static Fragment getFragment() { return fragment; }

    public SettingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setFragment(this);
        Log.d(TAG, "Fragment has been created.");

        broadcastReceiver = new ARBroadcastReceiver();
        visitedLocationViewModel = ViewModelProviders.of(getActivity()).get(VisitedLocationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        radioWalkingMode = rootView.findViewById(R.id.walking_radio_button);
        radioDrivingMode = rootView.findViewById(R.id.driving_radio_button);
        radioGroup = rootView.findViewById(R.id.directions_mode_radio_group);
        radioWalkingMode.setChecked(true);
        modeSwitch = rootView.findViewById(R.id.activity_recognition_mode_switch);
        clearDatabaseButton = rootView.findViewById(R.id.clear_database_button);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonID);
                String selectedText = (String) radioButton.getText();
                switch (selectedText) {
                    case "Walking":
                        DIRECTIONS_MODE = 0;
                        break;
                    case "Driving":
                        DIRECTIONS_MODE = 1;
                        break;
                }
            }
        });

        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AUTOMODE = 1;
                    startRecognitionService();
                }
                else {
                    AUTOMODE = 0;
                    stopRecognitionService();
                }
            }
        });

        clearDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete all the previously visited points? This action cannot be undone!")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                try {
                                    visitedLocationViewModel.deleteAllLocations();
                                }
                                catch (Exception e) {
                                    Log.e(TAG, "Something's went wrong with the db purging");
                                    e.printStackTrace();
                                }
                                Toast.makeText(getActivity(), "All the visited locations have been successfully deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //--------------------------------------------------------------------------------------------//
    private void startRecognitionService() {
        if(!isRecognitionServiceRunning()){
            Intent serviceIntent = new Intent(getContext(), BackgroundDetectedActivitiesService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                getContext().startForegroundService(serviceIntent);
            }else{
                getContext().startService(serviceIntent);
            }
        }
    }

    private void stopRecognitionService() {
        Intent serviceIntent = new Intent(getContext(), BackgroundDetectedActivitiesService.class);
        getContext().stopService(serviceIntent);
        Intent detectedARIntent = new Intent(getContext(), DetectedActivitiesIntentService.class);
        getContext().stopService(detectedARIntent);
    }

    private boolean isRecognitionServiceRunning() {
        ActivityManager manager = (ActivityManager)getContext().getSystemService(getContext().ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("acr18as.sheffield.ac.uk.takemeback.services.BackgroundDetectedActivitiesService".equals(service.service.getClassName())) {
                Log.d(TAG, "isRecognitionServiceRunning: recognition service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isRecognitionServiceRunning: recognition service is not running.");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
