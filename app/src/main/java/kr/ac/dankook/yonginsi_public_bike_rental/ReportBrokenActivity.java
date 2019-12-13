package kr.ac.dankook.yonginsi_public_bike_rental;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReportBrokenActivity extends AppCompatActivity {

    // 액션바 타이틀
    TextView actionBarTitle;
    ImageButton homeBtn;

    // 스피너
    Spinner reportSpinner;
    ArrayList<String> reportArrayList;
    ArrayAdapter<String> reportArrayadapter;
    EditText brokenBikeNumInput;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_broken);

        // UI
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);

        // 스피너
        brokenBikeNumInput = (EditText) findViewById(R.id.brokenBikeNumInput);
        reportSpinner = (Spinner) findViewById(R.id.reportSpinner);

        reportArrayList = new ArrayList<>();
        reportArrayList.add("브레이크 고장");
        reportArrayList.add("핸들 고장");
        reportArrayList.add("타이어 고장");
        reportArrayList.add("지지대 고장");
        reportArrayList.add("기어 고장");
        reportArrayList.add("안장 높낮이 조절부 고장");
        reportArrayList.add("벨 고장");

        reportArrayadapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, reportArrayList);
        reportSpinner.setAdapter(reportArrayadapter);
        reportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        View actionbar = inflater.inflate(R.layout.actionbar_menu, null);

        actionBar.setCustomView(actionbar);

        // 액션바 타이틀 지정하기
        actionBarTitle = (TextView) findViewById(R.id.actionbarTitleText);
        actionBarTitle.setText("고장 신고");

        return true;
    }

    public void onReportBtnClicked(View v){
        String brokenBike = brokenBikeNumInput.getText().toString();
        Toast.makeText(getApplicationContext(), "자전거 " + brokenBike + " 고장이 신고됐습니다.", Toast.LENGTH_LONG).show();
    }

    public void onHomeBtnClicked(View v){
        finish();
    }
}
