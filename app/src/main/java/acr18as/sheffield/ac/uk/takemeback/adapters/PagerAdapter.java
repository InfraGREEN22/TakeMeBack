package acr18as.sheffield.ac.uk.takemeback.adapters;

import acr18as.sheffield.ac.uk.takemeback.view.AboutFragment;
import acr18as.sheffield.ac.uk.takemeback.view.AdvSettingsFragment;
import acr18as.sheffield.ac.uk.takemeback.view.MapFragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int numberOfTabs) {
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
                return AdvSettingsFragment.newInstance();
            case 2:
                return AboutFragment.newInstance();
        }
        return null;
    }
}
