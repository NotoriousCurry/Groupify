package com.tssssa.sgaheer.groupify;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;


public class HomeActivity extends AppCompatActivity {
    private TextView mLoggedInStatusTextview;
    private ViewPager mViewPager;
    private CpAdapter mCpAdapter;
    private Firebase mFirebaseRef;
    public final static String LOGOUT_MESSAGE = "com.tssssa.groupify.LOGOUT";
    public static final int NUM_ITEMS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mLoggedInStatusTextview = (TextView) findViewById(R.id.login_status);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        mCpAdapter = new CpAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCpAdapter);
        final ActionBar actionbar = getActionBar();



        Intent intent = getIntent();
        String user = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        //mLoggedInStatusTextview.setText("Logged in as: " + user);
        //mLoggedInStatusTextview.setVisibility(View.VISIBLE);
        supportInvalidateOptionsMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mFirebaseRef.unauth();
        Intent goToLogin = new Intent(this, LoginActivity.class);
        goToLogin.putExtra(LOGOUT_MESSAGE, "logout");
        startActivity(goToLogin);
    }


    public static class CpAdapter extends FragmentPagerAdapter {

        public CpAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position);
        }
    }

    public static class ArrayListFragment extends ListFragment {
        int mNum;


        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            if (mNum == 0) {
                ((TextView) tv).setText("Events");
            } else if (mNum == 1) {
                ((TextView) tv).setText("My Profile");
            } else if (mNum == 2) {
                ((TextView) tv).setText("Notifications");

            }
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (mNum == 0) {
                setListAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }
}
