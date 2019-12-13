package kr.ac.dankook.yonginsi_public_bike_rental;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // 액션바 타이틀
    TextView actionBarTitle;
    ImageButton homeBtn;

    EditText userIdInput;
    EditText userPasswordInput;
    Button realLoginBtn;
    TextView findLoginInfoText;
    TextView joinAppText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        userIdInput = (EditText)findViewById(R.id.userIdInput);
        userPasswordInput =  (EditText)findViewById(R.id.userPasswordInput);
        realLoginBtn = (Button)findViewById(R.id.realLoginBtn);
        findLoginInfoText = (TextView)findViewById(R.id.findingLoginInfoTextBtn);
        joinAppText = (TextView)findViewById(R.id.joinTextBtn);

        // 텍스트에 밑줄 긋기
        findLoginInfoText.setPaintFlags(findLoginInfoText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        findLoginInfoText.setText(Html.fromHtml(getResources().getString(R.string.find_login_info)));
        joinAppText.setPaintFlags(joinAppText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        joinAppText.setText(Html.fromHtml(getResources().getString(R.string.join_app)));
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
        actionBarTitle.setText("로그인");

        return true;
    }

    public void onHomeBtnClicked(View v){
        finish();
    }

    public void onRealLoginBtnClicked(View v){
        String user_id = userIdInput.getText().toString();
        String user_pw = userPasswordInput.getText().toString();

       // Toast.makeText(LoginActivity.this, user_id, Toast.LENGTH_SHORT).show();
       // Toast.makeText(LoginActivity.this, user_pw, Toast.LENGTH_SHORT).show();
        if(user_id.equals("guest01")){
            if(user_pw.equals("1234")){  // 로그인 성공
                Intent intent = new Intent(getApplicationContext(), RunActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(LoginActivity.this, "아이디 또는 패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(LoginActivity.this, "아이디 또는 패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onJoinTextBtnClicked(View v){

        Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        startActivity(intent);
    }

}
