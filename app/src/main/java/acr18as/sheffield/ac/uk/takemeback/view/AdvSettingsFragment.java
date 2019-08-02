package acr18as.sheffield.ac.uk.takemeback.view;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.receiver.ARBroadcastReceiver;
import acr18as.sheffield.ac.uk.takemeback.service.BackgroundDetectedActivitiesService;
import acr18as.sheffield.ac.uk.takemeback.service.DetectedActivitiesIntentService;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.VisitedLocationViewModel;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdvSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdvSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdvSettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "AdvSettingsFragment";

    public static AdvSettingsFragment getFragment() { return fragment; }
    public static void setFragment(AdvSettingsFragment fragment) {
        AdvSettingsFragment.fragment = fragment;
    }
    private static AdvSettingsFragment fragment;

    private ARBroadcastReceiver broadcastReceiver;

    private VisitedLocationViewModel visitedLocationViewModel;

    private SharedPreferences sharedPreferences;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AdvSettingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AdvSettingsFragment newInstance() {
        AdvSettingsFragment fragment = new AdvSettingsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.d(TAG, "Fragment has been created.");

        setFragment(this);

        broadcastReceiver = new ARBroadcastReceiver();
        visitedLocationViewModel = ViewModelProviders.of(getActivity()).get(VisitedLocationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_adv_settings, container, false);


        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new PreferencesFragment())
                .commit();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        if(sharedPreferences.getBoolean("automode_switch", true)) {
            startRecognitionService();
        }
        else {
            stopRecognitionService();
        }

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

    // --------------------------- Preferences Methods ---------------------------------------//

    /**
     * Static class for Preferences
     */
    public static class PreferencesFragment extends PreferenceFragmentCompat {

        private VisitedLocationViewModel visitedLocationViewModel;
        private AdvSettingsFragment advSettingsFragment;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            bindSummaryValue(findPreference("directions_mode"));

            visitedLocationViewModel = ViewModelProviders.of(getActivity()).get(VisitedLocationViewModel.class);
            advSettingsFragment = AdvSettingsFragment.getFragment();

            Preference clearHistoryPreference = (Preference) findPreference("clear_history");
            Preference switchPreference = (Preference) findPreference("automode_switch");
            Preference statisticsPreference = (Preference) findPreference("history_statistics");

            clearHistoryPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
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
                    return true;
                }
            });

            statisticsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), StatisticsActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue.equals(true))
                        advSettingsFragment.startRecognitionService();
                    else
                        advSettingsFragment.stopRecognitionService();
                    return true;
                }
            });
        }
    }

    private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            if(preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }
            return true;
        }
    };

    private static void bindSummaryValue(Preference preference) {
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }
}
