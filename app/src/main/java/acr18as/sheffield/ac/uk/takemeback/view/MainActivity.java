package acr18as.sheffield.ac.uk.takemeback.view;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.adapters.PagesAdapter;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getName();
    private PagesAdapter mFragmentAdapter;
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
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Apply the Adapter
        mFragmentAdapter = new PagesAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager = (ViewPager) findViewById(R.id.vpager);
        mViewPager.setAdapter(mFragmentAdapter);

        mViewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Creating a new User instance
        User user = new User();
        ((UserClient)getApplicationContext()).setUser(user);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
