package com.example.tasol.myrowitems;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;


public class RentItLoginActivity extends AppCompatActivity {

    public ViewPager viewPager;
    TabLayout tabLayout;
    KudosTextView txtRent, txtIt, txtDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_rent_it_login);

        txtRent = (KudosTextView) findViewById(R.id.txtRent);
        txtIt = (KudosTextView) findViewById(R.id.txtIt);
        txtDesc = (KudosTextView) findViewById(R.id.txtDesc);


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        LoginManager.getInstance().logOut();

        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);

    }

    public void selectFragment(int position) {
        viewPager.setCurrentItem(position, true);
        // true is to animate the transaction
    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
            //additional code
        } else {
            selectFragment(0);
        }

    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RentItLoginFragment(), "Login");
        adapter.addFragment(new RentItSignupFragment(), "Create Account");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
