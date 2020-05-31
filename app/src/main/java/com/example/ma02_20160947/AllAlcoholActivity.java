package com.example.ma02_20160947;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ma02_20160947.DB.AlcoholDBHelper;
import com.example.ma02_20160947.DB.MyCursorAdapter;

public class AllAlcoholActivity extends AppCompatActivity {

    ListView lvAlcohol = null;
    AlcoholDBHelper helper;
    Cursor cursor;
    MyCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_alcohol);
        lvAlcohol = (ListView)findViewById(R.id.lvAlcohol);
        helper = new AlcoholDBHelper(this);

//		  SimpleCursorAdapter 객체 생성
        adapter = new MyCursorAdapter (this, R.layout.listview_layout, null);
        lvAlcohol.setAdapter(adapter);

        lvAlcohol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllAlcoholActivity.this, DetailAlcoholActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        lvAlcohol.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AllAlcoholActivity.this);
                builder.setTitle("삭제하시겠습니까?");
                builder.setIcon(R.mipmap.delete);
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        String whereClause = "_id=?";
                        String[] whereArgs = new String[]{cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_ID))};
                        db.delete (AlcoholDBHelper.TABLE_NAME, whereClause , whereArgs);
                        cursor = db.rawQuery("select * from alcohol_table", null);
                        adapter.changeCursor(cursor);
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchGPS:
                Intent searchGPS = new Intent(this, SearchGPSActivity.class);
                startActivity(searchGPS);
                break;

            case R.id.add:
                Intent intent = new Intent(this, AddAlcoholActivity.class);
                startActivity(intent);
                break;

            case R.id.search:
                Intent search = new Intent(this, SearchDiaryActivity.class);
                startActivity(search);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DB에서 데이터를 읽어와 Adapter에 설정
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + AlcoholDBHelper.TABLE_NAME, null);
        adapter.changeCursor(cursor);
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}
