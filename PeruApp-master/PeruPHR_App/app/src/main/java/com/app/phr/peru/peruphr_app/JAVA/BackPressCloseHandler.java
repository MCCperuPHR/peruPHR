package com.app.phr.peru.peruphr_app.JAVA;

/**
 * Created by hansol on 2016-08-10.
 * 뒤로가기 버튼 눌렀을 때 토스트 띄워주고 한번 더 눌렀을 때 앱 종료 시킴
 */
import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            //activity.finish();
            activity.moveTaskToBack(true);
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "Press \'Back\' button to finish APP", Toast.LENGTH_SHORT);
        toast.show();
    }
}
