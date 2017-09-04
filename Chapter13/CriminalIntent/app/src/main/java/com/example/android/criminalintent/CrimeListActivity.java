package com.example.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by darkes on 4/5/17.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
