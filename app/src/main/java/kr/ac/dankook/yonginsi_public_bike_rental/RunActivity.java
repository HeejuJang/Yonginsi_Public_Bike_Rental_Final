package kr.ac.dankook.yonginsi_public_bike_rental;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RunActivity extends AppCompatActivity {

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

    // 이용 내역 버튼
    ImageButton historyBtn;
    // 고장 신고 버튼
    ImageButton reportBrokenBtn;
    // 문의 사항 버튼
    ImageButton questionBtn;

    LinearLayout beforeScanView;
    LinearLayout afterScanView;
    int viewIndex = 1;

    SupportMapFragment mapFragment;
    GoogleMap map;

    Marker myLocationMarker;


    // QR코드 스캔 버튼
    Button qrScanBtn;

    // QR코드 스캐너
    IntentIntegrator qrScan;

    // 카드
    TextView cardInfoText;

    // polyline 시작점
    private LatLng startLatLng = new LatLng(0, 0);
    // polyline 끝점
    private LatLng endLatLng = new LatLng(0, 0);
    private List<Polyline> polylines;

    // 자전거 라이딩 상태
    private boolean ridingState = false;
    // 현재 위치
    private Location mCurrentLocation;
    float distance;
    String meter;
    private Location locA;
    private Location locB;

    // 타이머
    TextView runningTimeText;
    long baseTime;
    long totalRunningTime;
    int timerState = 0;
    int card_flag = 0;
    int start_flag = 0;
    int run_flag = 0;
    String end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        beforeScanView = (LinearLayout) findViewById(R.id.beforeScanView);
        afterScanView = (LinearLayout) findViewById(R.id.afterScanView);

        // UI
        slidingPage = (LinearLayout)findViewById(R.id.slidingPage);
        menuBtn = (ImageButton)findViewById(R.id.menuBtn);
        slideCloseBtn = (ImageButton)findViewById(R.id.slideCloseBtn);
        historyBtn = (ImageButton)findViewById(R.id.sideHistoryBtn);
        reportBrokenBtn = (ImageButton)findViewById(R.id.sideReportBrokenBtn);
        questionBtn = (ImageButton)findViewById(R.id.sideQuestionBtn);
        runningTimeText = (TextView)findViewById(R.id.runningTimeText);
        cardInfoText = (TextView)findViewById(R.id.cardInfoText);

        // 애니메이션
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);

        // 애니메이션 리스너 설정
        RunActivity.SlidingPageAnimationListener animationListener = new RunActivity.SlidingPageAnimationListener();
        translateRightAnim.setAnimationListener(animationListener);
        translateLeftAnim.setAnimationListener(animationListener);

        polylines = new ArrayList<>();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainMap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

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

                setDefaultLocation();
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

        // 스캔 초기화
        qrScan = new IntentIntegrator(this);

    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(RunActivity.this, "스캔 취소", Toast.LENGTH_SHORT).show();
            } else {
                if(run_flag == 0) {
                    // 거리 초기화
                    distance = 0;
                    // qrcode 결과가 있으면
                    Toast.makeText(RunActivity.this, "스캔 완료", Toast.LENGTH_SHORT).show();
                    // 스캔 완료시 사용 중 화면으로 프레임 전환
                    changeView();
                    // 라이딩 상태 변경
                    changeRidingState();
                    // 타이머 작동 시작
                    startTimer();
                    run_flag = 1;
                    try {
                        //data를 json으로 변환
                        JSONObject obj = new JSONObject(result.getContents());
                        //textViewName.setText(obj.getString("name"));
                        //textViewAddress.setText(obj.getString("address"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Toast.makeText(MainActivity.this, result.getContents(), Toast.LENGTH_LONG).show();
                        //textViewResult.setText(result.getContents());
                    }
                }
                else{
                    endTimer();
                    changeView();
                    changeRidingState();
                    map.clear();

                    start_flag = 0;
                    run_flag = 0;

                    try {
                        //data를 json으로 변환
                        JSONObject obj = new JSONObject(result.getContents());
                        //textViewName.setText(obj.getString("name"));
                        //textViewAddress.setText(obj.getString("address"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Toast.makeText(MainActivity.this, result.getContents(), Toast.LENGTH_LONG).show();
                        //textViewResult.setText(result.getContents());
                    }

                    // 히스토리 메뉴로 데이터 전달
                    Intent intent = new Intent(RunActivity.this, HistoryActivity.class);
                    intent.putExtra("time", end_time);
                    intent.putExtra("distance", meter);

                    startActivity(intent);
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void startTimer(){
        baseTime = SystemClock.elapsedRealtime();
        // 빈 메시지를 보내서 타이머 핸들러 호출
        TimerHandler.sendEmptyMessage(0);
        // Timer 상태 ON으로 변경
        timerState = 1;
    }

    private void endTimer(){
        // 핸들러 메시지 제거
        TimerHandler.removeMessages(0);
        totalRunningTime = SystemClock.elapsedRealtime();
        // Timer 상태 OFF로 변경
        timerState = 0;

        end_time = runningTimeText.getText().toString();
        // Toast.makeText(RunActivity.this, end_time, Toast.LENGTH_LONG).show();
        //meter = Double.toString(distance);
        meter = String.format("%.2f", distance/1000);
        //Toast.makeText(RunActivity.this, meter + "km", Toast.LENGTH_LONG).show();
    }
    Handler TimerHandler = new Handler(){
        public void handleMessage(Message msg){
            runningTimeText.setText(getTimeOut());

            TimerHandler.sendEmptyMessage(0);
        }
    };

    String getTimeOut(){
        // 애플리케이션 실행되고나서 실제로 경과된 시간
        long now = SystemClock.elapsedRealtime();
        long out = now - baseTime;
        String outTime = String.format("%02d : %02d", out/1000/ 60, (out/1000)%60);
        return outTime;
    }

    private void changeRidingState(){
        if(!ridingState) {
            Toast.makeText(getApplicationContext(), "쭉쭉이 이용을 시작합니다.", Toast.LENGTH_SHORT).show();
            ridingState = true;
            startLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());        //현재 위치를 시작점으로 설정
           // locA.setLatitude(mCurrentLocation.getLatitude());
           // locA.setLongitude(mCurrentLocation.getLongitude());
        }else{
            Toast.makeText(getApplicationContext(), "쭉쭉이 이용을 마쳤습니다.", Toast.LENGTH_SHORT).show();
            ridingState = false;
        }
    }

    private void drawPath(){        //polyline을 그려주는 메소드
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(15).color(R.color.colorAccent).geodesic(true);
        polylines.add(map.addPolyline(options));

        // 순간 거리
//        distance += locA.distanceTo(locB);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 18));
    }



    public void setDefaultLocation() {

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.323201, 127.125756);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (myLocationMarker != null) myLocationMarker.remove();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        map.moveCamera(cameraUpdate);
    }

    public void onQrCodeScanBtnClicked(View v){

        if(card_flag == 1) {
            //scan option
            qrScan.setPrompt("Scanning...");
            //qrScan.setOrientationLocked(false);
            qrScan.initiateScan();
        }
        else{
            Toast.makeText(RunActivity.this, "등록된 카드가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRegisterCardBtnClicked(View v){

        card_flag = 1;

        cardInfoText.setText("우리 1234");
        cardInfoText.setTextColor(Color.WHITE);
    }

    public void onReturnBikeBtnClicked(View v){

        qrScan.setPrompt("Scanning...");
        //qrScan.setOrientationLocked(false);
        qrScan.initiateScan();

        /*
        endTimer();
        changeView();
        changeRidingState();
        map.clear();

        start_flag = 0;
        // 히스토리 메뉴로 데이터 전달
        Intent intent = new Intent(RunActivity.this, HistoryActivity.class);
        intent.putExtra("time", end_time);
        intent.putExtra("distance", meter);

        startActivity(intent);
        */
    }

    private void changeView(){
        if(viewIndex == 0) {
            beforeScanView.setVisibility(View.VISIBLE);
            afterScanView.setVisibility(View.INVISIBLE);

            viewIndex = 1;
        }
        else{
            beforeScanView.setVisibility(View.INVISIBLE);
            afterScanView.setVisibility(View.VISIBLE);

            viewIndex = 0;
        }
    }

    private void requestMyLocation() {
        LocationManager manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            long minTime = 500;
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
                            mCurrentLocation = location;
                            if(start_flag == 0){
                                locA = location;
                                start_flag = 1;
                            }
                            distance += locA.distanceTo(location);
                            locA = location;

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
        double latitude = location.getLatitude(),
                longtitude = location.getLongitude();

        if(myLocationMarker != null) myLocationMarker.remove();

        LatLng currentLatLang = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLang);
        markerOptions.title("● 내 위치\n");
        markerOptions.snippet("● GPS로 확인한 위치");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));

        myLocationMarker = map.addMarker(markerOptions);

        // 자전거 이용이 시작되었을 때
        if(ridingState){
            endLatLng = new LatLng(latitude, longtitude);   // 현재 위치를 끝점으로 설정
            // 순간 출발 위치
            //locA.setLatitude(endLatLng.latitude);
            //locA.setLongitude(endLatLng.longitude);
            drawPath();                                     // polyline 그리기
            startLatLng = new LatLng(latitude, longtitude); // 시작점을 끝점으로 다시 설정
            //locB.setLatitude(startLatLng.latitude);
            //locB.setLongitude(startLatLng.longitude);

        }
    }

    // 메뉴버튼 클릭시
    public void onMenuBtnClicked(View v){
        if(!isPageOpen){ // 열기
            slidingPage.setVisibility(View.VISIBLE);
            slidingPage.startAnimation(translateRightAnim);
        }
    }

    // 도움말 버튼 클릭 시
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
