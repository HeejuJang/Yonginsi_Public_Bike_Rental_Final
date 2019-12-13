package kr.ac.dankook.yonginsi_public_bike_rental;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class GuideActivity extends AppCompatActivity {

    // 액션바 타이틀
    TextView actionBarTitle;
    ImageButton homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // 홈 버튼 클릭 시
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
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
        actionBarTitle.setText("사용 가이드");

        return true;
    }

    public void onHomeBtnClicked(View v){
        finish();
    }
}
