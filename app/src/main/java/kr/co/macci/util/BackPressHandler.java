package kr.co.macci.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

// 뒤로가기버튼 두번 누르면 종료시키는 클래스
public class BackPressHandler {

    private static final String TAG = "BackPressHandler";
    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;
    // 종료시킬 Activity
    private Activity activity;
    private Context mContext;
    private WebView mWebView;

    /**
     * 생성자
     *
     * @param activity 종료시킬 Activity.
     */
    public BackPressHandler(Activity activity) {
        this.activity = activity;
    }

    public BackPressHandler() { }

    /**
     * Default onBackPressed()
     * 2 seconds
     */
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    /**
     * Default showGuide()
     */
    private void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}

