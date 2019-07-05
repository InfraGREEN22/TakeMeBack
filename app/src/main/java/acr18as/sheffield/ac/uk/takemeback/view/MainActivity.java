package acr18as.sheffield.ac.uk.takemeback.view;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.adapters.PagerAdapter;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private PagerAdapter mFragmentAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Creating tabs
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Map").setIcon(R.drawable.ic_map_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("Settings").setIcon(R.drawable.ic_settings_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("About").setIcon(R.drawable.ic_info_outline_white_24dp));
        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.White), PorterDuff.Mode.SRC_IN);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        //Apply the Adapter
        mFragmentAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager = (ViewPager) findViewById(R.id.vpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mFragmentAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.White);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                // refreshing the MapFragment to apply changes made in SettingsFragment
                if(tab.getPosition() == 0) {
                    MapFragment.getFragment().onResume();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.Black);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Creating a new User instance and setting the value to the Singleton object
        User user = new User();
        ((UserClient)getApplicationContext()).setUser(user);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
