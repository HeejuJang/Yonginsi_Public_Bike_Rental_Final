package kr.ac.dankook.yonginsi_public_bike_rental;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "220.149.235.53";
    private static String TAG = "phptest";

    // 액션바 타이틀
    TextView actionBarTitle;
    ImageButton homeBtn;

    EditText joinUserIdInput;
    EditText joinUserPasswordInput;
    EditText joinUserNameInput;
    EditText joinUserPhoneInput;
    EditText joinUserWeightInput;

    TextView mTextViewResult;

    Button joinBtn;
    Button checkIdRedundantBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // UI
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        joinUserIdInput = (EditText)findViewById(R.id.joiningUserIdInput);
        joinUserPasswordInput = (EditText)findViewById(R.id.joiningUserPasswordInput);
        joinUserNameInput = (EditText)findViewById(R.id.joiningUserNameInput);
        joinUserPhoneInput = (EditText)findViewById(R.id.joiningUserPhoneInput);
        joinUserWeightInput = (EditText)findViewById(R.id.joiningUserWeightInput);
        joinBtn = (Button)findViewById(R.id.joinBtn);
        checkIdRedundantBtn = (Button)findViewById(R.id.checkIdRedundantBtn);

        mTextViewResult = (TextView)findViewById(R.id.resultText);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

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
        actionBarTitle.setText("회원가입");

        return true;
    }

    public void onHomeBtnClicked(View v){
        finish();
    }

    public void onJoinBtnClicked(View v){
        String userId = joinUserIdInput.getText().toString();
        String userPassword = joinUserPasswordInput.getText().toString();
        String userName = joinUserNameInput.getText().toString();
        String userPhone = joinUserPhoneInput.getText().toString();
        String userWeight = joinUserWeightInput.getText().toString();

        InsertData task = new InsertData();
        task.execute("http://" + IP_ADDRESS + "/insert_2.php", userId, userPassword, userName, userPhone, userWeight);


        joinUserIdInput.setText("");
        joinUserPasswordInput.setText("");
        joinUserNameInput.setText("");
        joinUserPhoneInput.setText("");
        joinUserWeightInput.setText("");
    }

    public void onCheckIdRedundantBtnClicked(View v){

    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(JoinActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String userId = (String)params[1];
            String userPassword = (String)params[2];
            String userName = (String)params[3];
            String userPhone = (String)params[4];
            String userWeight = (String)params[5];

            String serverURL = (String)params[0];
            String postParameters = "id=" + userId + "&pass=" + userPassword + "&name=" + userName +
                    "&Num=" + userPhone + "&weight=" + userWeight;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

}
