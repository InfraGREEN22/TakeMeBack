package acr18as.sheffield.ac.uk.takemeback.adapters;

import acr18as.sheffield.ac.uk.takemeback.view.AboutFragment;
import acr18as.sheffield.ac.uk.takemeback.view.MapFragment;
import acr18as.sheffield.ac.uk.takemeback.view.SettingsFragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagesAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    public PagesAdapter(@NonNull FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MapFragment.newInstance();
            case 1:
                return SettingsFragment.newInstance();
            case 2:
                return AboutFragment.newInstance();
        }
        return null;
    }
}
