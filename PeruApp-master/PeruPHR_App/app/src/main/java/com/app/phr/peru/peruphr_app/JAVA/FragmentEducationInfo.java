package com.app.phr.peru.peruphr_app.JAVA;

/**
 * Created by hansol on 2016-08-10.
 * tab layout 중에서 교육자료를 보여줄 fragment
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.app.phr.peru.peruphr_app.R;


public class FragmentEducationInfo extends Fragment{
    private WebView wv;

    public FragmentEducationInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_educationinfo, container, false);

        wv = (WebView) v.findViewById(R.id.webview);
        wv.loadUrl("http://m.newhealthadvisor.com/1-Month-Pregnant-Belly.html");

        return v;
    }

}
