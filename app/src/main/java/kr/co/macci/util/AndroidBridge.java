package kr.co.macci.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;

import java.util.ArrayList;

import kr.co.macci.R;
import kr.co.macci.screen.MainActivity;

// WebView - JavaScript 통신을 위한 클래스
public class AndroidBridge {

    private static final String TAG = "AndroidBridge";
    
    private Context mContext;
    private WebView mWebView;
    private Activity mActivity;
    private String locationPermission;
    private View mView;
    int keyCode;
    KeyEvent event;

    private String serverJwtToken = null;
    private String webViewEdShops = null;
    private String userTermsCheck = null;
    private String webViewSearchData = null;
    private String jwtToken = null;

    private BackPressHandler backPressHandler = new BackPressHandler();
    public MainActivity mainActivity = new MainActivity();

    // 생성자
    public AndroidBridge(Context mContext, WebView mWebView, Activity mActivity, View mView, String locationPermissionm) {
        this.mContext = mContext;
        this.mWebView = mWebView;
        this.mActivity = mActivity;
        this.mView = mView;
        this.locationPermission = locationPermission;
    }

    // js에서 page = 6 일 때 호출 됨(카카오페이지 뒤로가기)
//    @JavascriptInterface
//    public void responcePage (final String pageUrl) {
//        mainActivity.onKeyUp(keyCode, event);
//    }

    //  js에서 공유 버튼 이벤트로 호출 됨(샵 공유하기)
    @JavascriptInterface
    public void responceShare (String shopUrl) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shopUrl);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        mContext.startActivity(shareIntent);
    }

    //  js에서 로그아웃 버튼 이벤트로 호출 됨(로그아웃)
    @JavascriptInterface
    public void responceClearJwt () {
        SharedPreferences sf = mContext.getSharedPreferences("webView", Context.MODE_PRIVATE);
        SharedPreferences.Editor sfEditor = sf.edit();
        sfEditor.putString("jwtToken", "");
        sfEditor.commit();
    }

    //  js에서 이벤트로 호출 됨(최근 검색어 파라미터로 받음)
    @JavascriptInterface
    public void responceSearchData (String searchData) {
        SharedPreferences sf = mContext.getSharedPreferences("webView", Context.MODE_PRIVATE);
        SharedPreferences.Editor sfEditor = sf.edit();
        sfEditor.putString("searchData", searchData);
        sfEditor.commit();
    }

    // js에서 회원가입시 호출 됨(약관동의 파라미터로 받음)
    @JavascriptInterface
    public void responceTermsCheck (String termsCheck) {
        SharedPreferences sf = mContext.getSharedPreferences("webView", Context.MODE_PRIVATE);
        SharedPreferences.Editor sfEditor = sf.edit();
        sfEditor.putString("termsCheck", termsCheck);
        sfEditor.commit();
    }

    //  js에서 이벤트로 호출 됨(최근 본 업체 파라미터로 받음)
    @JavascriptInterface
    public void responceViewEdShops (String viewEdShops) {
        SharedPreferences sf = mContext.getSharedPreferences("webView", Context.MODE_PRIVATE);
        SharedPreferences.Editor sfEditor = sf.edit();
        sfEditor.putString("viewEdShops", viewEdShops);
        sfEditor.commit();
    }

    // js에서 로그인 버튼 이벤트로 호출 됨(JWT 토큰 파라미터로 받음)
    @JavascriptInterface
    public void responceJwtToken (String jwtToken) {
        SharedPreferences sf = mContext.getSharedPreferences("webView", Context.MODE_PRIVATE);
        SharedPreferences.Editor sfEditor = sf.edit();
        sfEditor.putString("jwtToken", jwtToken);
        sfEditor.commit();
    }

    // js에서 예약하기 버튼 이벤트로 호출 됨(전화번호 파라미터로 받음)
    @JavascriptInterface
    public void responceCallNumber (final String tel) {
        if (tel.startsWith("tel:")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
            builder.setTitle("안내");
            builder.setMessage("전화연결 하시겠습니까?\n혜택을 받으시려면 마찌를 통해\n전화했다고 꼭 말씀해주세요.");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int permssionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
                        if (permssionCheck == -1) {
                            PermissionListener permissionListener = new PermissionListener() {

                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse(tel));
                                    mContext.startActivity(intent);
                                }

                                @Override
                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                }
                            };

                            TedPermission.with(mContext)
                                    .setPermissionListener(permissionListener)
                                    .setRationaleMessage("전화 권한이 허용되어야\n예약하기가 가능합니다.")
                                    .setPermissions(Manifest.permission.CALL_PHONE)
                                    .check();

                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(tel));
                            mContext.startActivity(intent);
                        }
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            // AlertDialog 크기 변경
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = 1020;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);

        } else {
            Toast.makeText(mContext, "고객센터에 문의해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // js에서 내비 버튼 이벤트로 호출 됨(좌표값 파라미터로 받음)
    // kakao Navi Legacy
    @JavascriptInterface
    public void responceForApp (String name, String y, String x) {
        // js에서 return 받은 좌표
        double doubleX = Double.valueOf(x);
        double doubleY = Double.valueOf(y);

        // 목적지 설정
        Location destination = Location.newBuilder(name, doubleX,  doubleY).build();

        // 내비 옵션 설정
        KakaoNaviParams params = KakaoNaviParams.newBuilder(destination)
                .setNaviOptions(NaviOptions.newBuilder()
                        .setCoordType(CoordType.WGS84) // 좌표 타입
                        .setRpOption(RpOption.FAST) // 경로옵션(FAST : 빠른길)
                        .setRouteInfo(true) // 전체 경로 보기 여부
                        .build())
                .build();

        // 길안내 시작
        KakaoNaviService.getInstance().navigate(mContext, params);
    }

    // js에 파라미터 넘기기(sf에서 꺼낸 searchData)
    @JavascriptInterface
    public String requestAppSearchData() {
        SharedPreferences sf = mContext.getSharedPreferences("webView", Context.MODE_PRIVATE);
        webViewSearchData = sf.getString("searchData","");
        return webViewSearchData;
    }

    // js에 파라미터 넘기기(sf에서 꺼낸 userTermsCheck)
    @JavascriptInterface
    public String requestAppTermsCheck() {
        SharedPreferences sf = mContext.getSharedPreferences("webView", Context.MODE_PRIVATE);
        userTermsCheck = sf.getString("termsCheck","");
        return userTermsCheck;
    }

    // js에 파라미터 넘기기(sf에서 꺼낸 webViewEdShops)
    @JavascriptInterface
    public String requestAppViewEdShops() {
        SharedPreferences sf = mContext.getSharedPreferences("webView", Context.MODE_PRIVATE);
        webViewEdShops = sf.getString("viewEdShops","");
        return webViewEdShops;
    }

    // js에 파라미터 넘기기(sf에서 꺼낸 serverToken)
    @JavascriptInterface
    public String requestAppToken() {
        SharedPreferences sf = mContext.getSharedPreferences("webView", Context.MODE_PRIVATE);
        serverJwtToken = sf.getString("jwtToken","");
        return serverJwtToken;
    }

    // js에 파라미터 넘기기(위치권한 허용 여부)
    @JavascriptInterface
    public String requestLocationPermission() {
        return locationPermission;
    }

    // js에 파라미터 넘기기(앱/웹 구분용)
    @JavascriptInterface
    public String requestFromApp() {
        return "app";
    }
}