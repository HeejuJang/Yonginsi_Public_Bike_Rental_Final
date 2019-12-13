package kr.ac.dankook.yonginsi_public_bike_rental;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {

    // 액션바 타이틀
    TextView actionBarTitle;
    ImageButton homeBtn;

    // 리스트뷰
    ArrayList<String> items;
    ArrayAdapter adapter;
    ListView listview;

    String time;
    int price;
    String cardName;
    String distance;

    /*
    // DB
    private static String IP_ADDRESS = "220.149.235.53";
    private static String TAG = "phptest";

    private EditText mEditTextName;
    private EditText mEditTextCountry;
    private TextView mTextViewResult;
    private ArrayList<PersonalData> mArrayList;
    private UsersAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private EditText mEditTextSearchKeyword;
    private String mJsonString;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent(); /*데이터 수신*/

        String time = intent.getExtras().getString("time"); /*String형*/
        String distance = intent.getExtras().getString("distance");
        double dis_num = Double.parseDouble(distance);
        double cal_num = (65+10)*1000*0.11*(dis_num/25.7);
        String calories = String.format("%.2f", cal_num);
        //Toast.makeText(HistoryActivity.this, calories + " kcal", Toast.LENGTH_LONG).show();

        // 2차원 리스트뷰
        ExpandableListView mList;


        // 홈 버튼 클릭 시
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);

        // 오늘 날짜 불러오기
        final Calendar cal = Calendar.getInstance();
        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        listview = (ListView) findViewById(R.id.historyListView);
        listview.setAdapter(adapter);

        int count;
        int year = cal.get(cal.YEAR);
        int month = cal.get(cal.MONTH) + 1;
        int date = cal.get(cal.DATE);
        count = adapter.getCount();

        items.add("날   짜: " + Integer.toString(year) + "." + Integer.toString(month) + "." + Integer.toString(date) +
                "\n" + "시   간: " + time + "\n" + "이   동: " + distance + " km\n" +
                "운동량: " + calories + " kcal\n" +
                "결   제: (우리 1234) 1000원");

        adapter.notifyDataSetChanged();

        /*
        // DB
        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView_main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());



        mArrayList = new ArrayList<>();

        mAdapter = new UsersAdapter(this, mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        Button button_all = (Button) findViewById(R.id.button_main_all);
        button_all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                GetData task = new GetData();

                // 뒤에 /getjson.php가 테이블 이름
                task.execute( "http://" + IP_ADDRESS + "/getjson.php", "");
            }
        });
        */
    }

    public void onDeleteBtnClicked(View view) {
        int count, checked;
        count = adapter.getCount();

        if (count > 0) {
            checked = listview.getCheckedItemPosition();

            if (checked > -1 && checked < count) {
                items.remove(checked);
                listview.clearChoices();
                adapter.notifyDataSetChanged();
            }
        }
    }
/*

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(HistoryActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){

        String TAG_JSON="webnautes";
        String TAG_ID = "id";
        String TAG_NAME = "pass";
        String TAG_COUNTRY ="name";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String country = item.getString(TAG_COUNTRY);

                PersonalData personalData = new PersonalData();

                personalData.setMember_id(id);
                personalData.setMember_name(name);
                personalData.setMember_country(country);

                mArrayList.add(personalData);
                mAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
*/


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            ActionBar actionBar = getSupportActionBar();

            // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);         //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
            actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
            actionBar.setDisplayShowHomeEnabled(false);         //홈 아이콘을 숨김처리합니다.


            //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View actionbar = inflater.inflate(R.layout.actionbar_menu, null);

            actionBar.setCustomView(actionbar);

            // 액션바 타이틀 지정하기
            actionBarTitle = (TextView) findViewById(R.id.actionbarTitleText);
            actionBarTitle.setText("이용 내역");

            return true;
        }

        public void onHomeBtnClicked (View v){
            finish();
        }
    }

