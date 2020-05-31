package com.example.ma02_20160947;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.ma02_20160947.DB.AlcoholDBHelper;

public class SearchDiaryActivity extends AppCompatActivity {
    EditText etSearch;
    AlcoholDBHelper helper;
    long id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        helper = new AlcoholDBHelper(this);
        etSearch = (EditText)findViewById(R.id.etSearch);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                SQLiteDatabase db = helper.getReadableDatabase();
                String query = "SELECT " + AlcoholDBHelper.COL_ID + " FROM " + AlcoholDBHelper.TABLE_NAME + " WHERE " + AlcoholDBHelper.COL_NAME + "='" + etSearch.getText().toString() + "';";
                Cursor cursor = db.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    id = cursor.getLong(cursor.getColumnIndex(AlcoholDBHelper.COL_ID));
                }

                if (id == -1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("찾는 내용이 없습니다.");
                    builder.setPositiveButton("확인", null);
                    builder.show();
                }

                else {
                    Intent intent = new Intent(this, DetailAlcoholActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }

                id = -1;
                break;

            case R.id.btn_finSearch:
                finish();
                break;
        }
    }
}
