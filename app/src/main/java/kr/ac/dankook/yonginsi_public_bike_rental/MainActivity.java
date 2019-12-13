package kr.ac.dankook.yonginsi_public_bike_rental;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE_MENU = 101;

    // 슬라이드 열기/닫기 플래그
    boolean isPageOpen = false;
    // 슬라이드 열기 애니메이션
    Animation translateRightAnim;
    // 슬라이드 닫기 애니메이션
    Animation translateLeftAnim;
    // 슬라이드 레이아웃
    LinearLayout slidingPage;
    // 메뉴 버튼
    ImageButton menuBtn;
    // 슬라이드 메뉴 닫기 버튼
    ImageButton slideCloseBtn;

    TextView slideUserName;
    TextView slideUserId;

    // 이용 내역 버튼
    ImageButton historyBtn;
    // 고장 신고 버튼
    ImageButton reportBrokenBtn;
    // 문의 사항 버튼
    ImageButton questionBtn;

    // 로그인 버튼
    Button mainLoginBtn;

    SupportMapFragment mapFragment;
    GoogleMap map;

    MarkerOptions myLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI
        slidingPage = (LinearLayout)findViewById(R.id.slidingPage);
        menuBtn = (ImageButton)findViewById(R.id.menuBtn);
        slideCloseBtn = (ImageButton)findViewById(R.id.slideCloseBtn);
        historyBtn = (ImageButton)findViewById(R.id.sideHistoryBtn);
        reportBrokenBtn = (ImageButton)findViewById(R.id.sideReportBrokenBtn);
        questionBtn = (ImageButton)findViewById(R.id.sideQuestionBtn);
        mainLoginBtn = (Button)findViewById(R.id.mainLoginBtn);

        slideUserName = (TextView)findViewById(R.id.slideUserNameText);
        slideUserId = (TextView)findViewById(R.id.slideUserLoginText);

        // 애니메이션
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);

        // 애니메이션 리스너 설정
        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();
        translateRightAnim.setAnimationListener(animationListener);
        translateLeftAnim.setAnimationListener(animationListener);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainMap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "GoogleMap is ready.");

                map = googleMap;

                // 대여소 위치 지정
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                markerOptions.position(new LatLng(37.319327, 127.127401)).title("대여소 01");
                markerOptions.snippet("경기도 용인시 기흥구 죽전로 37.319327, 127.127401");
                map.addMarker(markerOptions);
                markerOptions.position(new LatLng(37.319800, 127.128344)).title("대여소 02");
                markerOptions.snippet("경기도 용인시 기흥구 죽전로 37.319800, 127.128344");
                map.addMarker(markerOptions);
            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch(Exception e) {
            e.printStackTrace();
        }

        ImageButton button = (ImageButton) findViewById(R.id.curLocationBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMyLocation();
            }
        });
    }

    // 메뉴버튼 클릭시
    public void onMenuBtnClicked(View v){
        if(!isPageOpen){ // 열기
            slidingPage.setVisibility(View.VISIBLE);
            slidingPage.startAnimation(translateRightAnim);
        }
    }

    public void onHelpBtnClicked(View v){
        Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
        startActivity(intent);
    }

    // 슬라이드 닫기 버튼 클릭 시
    public void onSlideCloseBtnClicked(View v){
        if(isPageOpen){ // 닫기
            // 애니메이션 시작
            slidingPage.startAnimation(translateLeftAnim);
        }
    }

    // 사이드 메뉴 [이용 내역] 버튼 클릭 시
    public void onSideHistoryBtnClicked(View v){
        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
        startActivity(intent);
    }

    // 사이드 메뉴 [고장 신고] 버튼 클릭 시
    public void onSideReportBrokenBtnClicked(View v){
        Intent intent = new Intent(getApplicationContext(), ReportBrokenActivity.class);
        startActivity(intent);
    }

    // 사이드 메뉴 [문의 사항] 버튼 클릭 시
    public void onSideQuestionBtnClicked(View v){
        Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
        startActivity(intent);
    }

    // 메인 화면 [로그인] 버튼 클릭 시
    public void onMainLoginBtnClicked(View v){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_MENU){
            Log.d(TAG, "onActivityResult 메소드 호출됨. 요청 코드: " + requestCode +
            ", 결과 코드: " + resultCode);
        }

        if(resultCode == RESULT_OK){
            Log.d(TAG, "onActivityResult 메소드 결과 코드: RESULT_OK");
        }
    }

    // 애니메이션 리스너
    private class SlidingPageAnimationListener implements  Animation.AnimationListener{
        @Override
        public void onAnimationEnd(Animation animation) {
            //슬라이드 열기->닫기
            if(isPageOpen){
                slidingPage.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            }
            //슬라이드 닫기->열기
            else{
                isPageOpen = true;
            }
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {

        }
    }

    private void requestMyLocation() {
        LocationManager manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            long minTime = 1000;
            float minDistance = 0;
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            showCurrentLocation(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }
            );

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                showCurrentLocation(lastLocation);
            }

            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            showCurrentLocation(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }
            );


        } catch(SecurityException e) {
            e.printStackTrace();
        }

    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 17));

        showMyLocationMarker(location);
    }

    private void showMyLocationMarker(Location location) {
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            myLocationMarker.title("● 내 위치\n");
            myLocationMarker.snippet("● GPS로 확인한 위치");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            map.addMarker(myLocationMarker);
        } else {
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);         //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);         //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar_main, null);

        actionBar.setCustomView(actionbar);

        return true;
    }
}
