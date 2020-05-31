//과제02
//작성일 : 2018.11.01
//작성자 : 02분반 20160947 김효경
//상호명을 검색하면 정보(주소,이름,전화번호)를 반환. (네이버 검색 API의 지역부분 사용)
//반환된 주소로 지오코딩을 통해 위도, 경도를 얻은 후 지도에 표시 (네이버의 지도 API 이용)
package com.example.ma02_20160947;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.ma02_20160947.API.MyLocalAdapter;
import com.example.ma02_20160947.API.SearchXmlParser;
import com.example.ma02_20160947.API.ShowMap;
import com.example.ma02_20160947.model.LocalDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchAddressActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    EditText searchAddress; //입력받은 음식점명
    ListView lvList;
    String apiAddress;
    String query;
    MyLocalAdapter adapter;
    ArrayList<LocalDto> resultList;
    SearchXmlParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_address);

        searchAddress = (EditText) findViewById(R.id.searchAddress);
        lvList = (ListView) findViewById(R.id.lvList);
        resultList = new ArrayList();
        adapter = new MyLocalAdapter(this, R.layout.local_item, resultList);
        lvList.setAdapter(adapter);
        apiAddress = getResources().getString(R.string.api_url);
        parser = new SearchXmlParser();

        //리스트뷰 클릭시 해당 주소를 선택할 것인지 묻는다.
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchAddressActivity.this);
                builder.setTitle("이 주소를 선택하시겠습니까?");
                builder.setIcon(R.mipmap.beer);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.putExtra("address", resultList.get(position).getAddress());
                        setResult(RESULT_OK, intent);
                        SearchAddressActivity.this.finish();
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
            }
        });

        //리스트뷰 롱클릭시 해당 주소 지도 보여줌.
        lvList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchAddressActivity.this, ShowMapActivity.class);
                intent.putExtra("information", resultList.get(i));
                startActivity(intent);
                return true;

            }
        });
    }

    //버튼 클릭시 네이버 지역검색 API의 결과를 ListView에 띄워줌
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_searchAddress :
                query = searchAddress.getText().toString();
                new SearchAsyncTask().execute();
                break;
        }
    }

    //네이버 지역검색 API 실행 -> xml 파일 받아옴
    class SearchAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg  = ProgressDialog.show(SearchAddressActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer response = new StringBuffer();

            //네이버 클라이언트 아이디, 시크릿
            String clientId = getResources().getString(R.string.client_id);
            String clientSecret = getResources().getString(R.string.client_secret);

            try {
                String apiURL = apiAddress + URLEncoder.encode(query, "UTF-8");
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                //네이버 접속 시 필요한 정보들임
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // response 수신
                int responseCode = con.getResponseCode();
                if (responseCode==200) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                } else {
                    Log.e(TAG, "API 호출 에러 발생 : 에러코드=" + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            resultList = parser.parse(result);
            adapter.setList(resultList);
            adapter.notifyDataSetChanged(); //getView가 가지고 있는 데이터 만큼 실행
            progressDlg.dismiss();
        }
    }


}
