package kr.co.macci.screen;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.service.voice.AlwaysOnHotwordDetector;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import kr.co.macci.R;
import kr.co.macci.util.AndroidBridge;
import kr.co.macci.util.BackPressHandler;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main_Activity";

    private Context mContext = MainActivity.this;

    private Activity mActivity = MainActivity.this;
    private WebView mWebView;
    private WebSettings mWebSettings;
    private String myUrl = "https://testm.macci.co.kr";

    public View mView;
    private String pageUrl = "http://testm.macci.co.kr/my";

    private RelativeLayout ivMySplash;

    private BackPressHandler backPressHandler = new BackPressHandler(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.web_view);

        initPermissionTed();
        initObject();
    }

    private void initObject() {
        ivMySplash = findViewById(R.id.iv_my_splash);
        mView = findViewById(R.id.rootView);
    }

    private void initWebView(String locationPermission) {
        // 크롬에서 창 열기
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                // Dialog Create Code
                WebView newWebView = new WebView(mActivity);
                WebSettings webSettings = newWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                final Dialog dialog = new Dialog(mActivity);
                dialog.setContentView(newWebView);

                ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
                dialog.show();
                newWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onCloseWindow(WebView window) {
                        dialog.dismiss();
                    }
                });

                // WebView Popup에서 내용이 안보이고 빈 화면만 보여 아래 코드 추가
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        return false;
                    }
                });

                ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }

            // 웹뷰에 gps location 권한 사용
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }
        });

        mWebView.setWebViewClient(new WebViewClientClass());
        //WebView 셋팅
        mWebSettings = mWebView.getSettings();
        mWebSettings.setGeolocationEnabled(true); // GeoLocation 허용 코드
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부(캐시모드 X, 네트워크를 통해서만 호출)
        mWebSettings.setDomStorageEnabled(true); // 로컬 저장소 허용 여부
        mWebView.setWebContentsDebuggingEnabled(false); // 디버깅 허용 여부 (배포를 위해 금지)
        mWebSettings.setJavaScriptEnabled(true); // js 허용 코드
        // WebView - JavaScript 통신을 위한 클래스 호출
        mWebView.addJavascriptInterface(new AndroidBridge(mContext, mWebView, mActivity, mView, locationPermission), "HybridApp");
        mWebView.loadUrl(myUrl);
    }

    // 뒤로가기 두번 누르면 종료안내 Toast
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: mWebView ::: " + mWebView);
        Log.d(TAG, "onBackPressed: pageUrl ::: " + pageUrl);
        backPressHandler.onBackPressed();
    }

    // WebView 뒤로가기 로직
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            Log.d(TAG, "onKeyDown: " + mWebView.canGoBack());
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
//            Toast.makeText(mContext, "my 뒤로가기", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "onKeyDown: " + mWebView.canGoBack());
//            mWebView.loadUrl(pageUrl);
//            return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    // 기존창에서 실행도록 하는 클래스 생성
    private class WebViewClientClass extends WebViewClient {


        // 링크 클릭에 대한 반응
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        // 웹페이지 호출시 오류 발생 처리
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Toast.makeText(mContext, "error : " + error.toString(), Toast.LENGTH_SHORT).show();
        }

        // 페이지 로딩 시작시 스플래시이미지 호출
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            ivMySplash.setVisibility(View.VISIBLE);
        }

        // 페이지 로딩 종료시 스플래시이미지 종료
        @Override
        public void onPageFinished(WebView view, String url) {
            ivMySplash.setVisibility(View.GONE);
        }


    }

    // -----  권한 인증 코드 시작
    // implements TedPermissionsListener
    private void initPermissionTed() {

        PermissionListener permissionListener = new PermissionListener() {

            private String locationPermission = "";

            @Override
            public void onPermissionGranted() {
                locationPermission = "granted";
                initWebView(locationPermission);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                locationPermission = "denied";
                initWebView(locationPermission);
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("서비스 사용이 원활하지 않을 수 있습니다.\n[설정] > [권한] 에서 권한을 허용해 주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE)
                .check();
    }

    private abstract class OnKeyListener {
        public abstract boolean onKey(View v, int keyCode, KeyEvent event);
    }
}