package com.app.phr.peru.peruphr_app.JAVA;

/**
 * Created by hansol on 2016-08-10.
 * fragment와 연결이 되어있는 main tab
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.app.phr.peru.peruphr_app.JAVA.BackPressCloseHandler;
import com.app.phr.peru.peruphr_app.R;

import java.util.ArrayList;
import java.util.List;

public class MainTab extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;
    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.mipmap.icon_phr,
            R.mipmap.icon_edu,
            R.mipmap.icon_myinfo
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PreferencePutter.PREF_FILE_NAME, Activity.MODE_PRIVATE);

        setContentView(R.layout.main_tab);
        backPressCloseHandler = new BackPressCloseHandler(this);
        //action bar 대신 toolbar를 사용
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar 에서 action bar의 support를 받아옴
        setSupportActionBar(toolbar);
        //타이틀 변경
        getSupportActionBar().setTitle(preferences.getString(PreferencePutter.PATIENT_NAME, "Peru PHR"));
        //새로고침 아이콘 표시
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        // 홈 아이콘 표시
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        //tab레이아웃을 사용함. main_tab.xml과 연결되어 있음
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    //tab 아이콘 바꿀 수 있음
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    //tab에서 보여줄 탭 fragment 이름
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if(NetworkUtil.getConnectivityStatusBoolean(this)){

        }
        adapter.addFrag(new FragmentPHR(), "PHR");
        //adapter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        adapter.addFrag(new FragmentEducationInfo(), "Edu-Info");
        adapter.addFrag(new FragmentMyInfo(), "My Info");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        //fragment 탭 이동마다 초기화 안될 시에 추가 할 것
//        @Override
//        public int getItemPosition(Object item) {
//            return POSITION_NONE;
//        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
