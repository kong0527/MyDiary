package com.example.ma02_20160947;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ma02_20160947.DB.AlcoholDBHelper;
import com.example.ma02_20160947.model.PWDto;

public class MainActivity extends AppCompatActivity {
    EditText inputPw;
    AlcoholDBHelper helper;
    PWDto dto;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputPw = (EditText)findViewById(R.id.inputPw);
        dto = null;
        helper = new AlcoholDBHelper(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
        if (helper != null) helper.close();
    }

    public void onClick(View v) {
        final LinearLayout changeLayout = (LinearLayout)View.inflate(this, R.layout.change_pw, null);
        final LinearLayout newLayout = (LinearLayout)View.inflate(this, R.layout.create_pw, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;
        switch(v.getId()) {
            case R.id.changePw :
                builder.setTitle("비밀번호 변경");
                builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder pwbuilder = new AlertDialog.Builder(MainActivity.this);
                        String opw = null;
                        Cursor cursor = db.rawQuery("SELECT * FROM " + AlcoholDBHelper.PW_TABLE_NAME, null);
                        while (cursor.moveToNext()) {
                            opw = cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_PW));
                        }
                        EditText originalPw = (EditText)changeLayout.findViewById(R.id.originalPw);
                        EditText etChange1 = (EditText)changeLayout.findViewById(R.id.etChange1);
                        EditText etChange2 = (EditText)changeLayout.findViewById(R.id.etChange2);

                        if (opw == null) {
                            pwbuilder.setTitle("비밀번호가 존재하지 않습니다.");
                            pwbuilder.setIcon(R.mipmap.warning);
                            pwbuilder.setPositiveButton("확인", null);
                            pwbuilder.show();
                        }

                        else if (opw.equals(originalPw.getText().toString())) {
                            if (etChange1.getText().toString().equals(etChange2.getText().toString())) {
                                db.execSQL("UPDATE " + AlcoholDBHelper.PW_TABLE_NAME + " SET " + AlcoholDBHelper.COL_PW + "='" + etChange1.getText().toString() + "';");
                                Toast.makeText(MainActivity.this, "비밀번호가 변경되었습니다: " + etChange1.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                pwbuilder.setTitle("비밀번호가 일치하지 않습니다.");
                                pwbuilder.setIcon(R.mipmap.warning);
                                pwbuilder.setPositiveButton("확인", null);
                                pwbuilder.show();
                            }
                        }

                        else {
                            pwbuilder.setTitle("기존 비밀번호와 다릅니다.");
                            pwbuilder.setIcon(R.mipmap.warning);
                            pwbuilder.setPositiveButton("확인", null);
                            pwbuilder.show();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.setView(changeLayout);
                builder.show();
                break;

            case R.id.createPw :
                String pw = null;
                cursor = db.rawQuery("SELECT * FROM " + AlcoholDBHelper.PW_TABLE_NAME, null);
                while (cursor.moveToNext()) {
                    pw = cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_PW));
                }
                if (pw == null) {
                    builder.setTitle("비밀번호 생성");
                    builder.setPositiveButton("생성", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText etCreate1 = (EditText)newLayout.findViewById(R.id.etCreate1);
                            EditText etCreate2 = (EditText)newLayout.findViewById(R.id.etCreate2);
                            if (etCreate1.getText().toString().equals(etCreate2.getText().toString())) {
                                db.execSQL("INSERT INTO " + AlcoholDBHelper.PW_TABLE_NAME + " VALUES (null, '" + etCreate1.getText().toString() + "');");
                                Toast.makeText(MainActivity.this, "비밀번호가 생성되었습니다: " + etCreate1.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                                builder2.setTitle("비밀번호가 일치하지 않습니다.");
                                builder2.setIcon(R.mipmap.warning);
                                builder2.setPositiveButton("확인", null);
                                builder2.show();
                            }
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.setView(newLayout);
                    builder.show();
                }
                else {
                    builder.setTitle("이미 비밀번호가 존재합니다.");
                    builder.setIcon(R.mipmap.warning);
                    builder.setPositiveButton("확인", null);
                    builder.show();
                }
                break;

            case R.id.btn_pw :
                String check = null;
                cursor = db.rawQuery("SELECT * FROM " + AlcoholDBHelper.PW_TABLE_NAME, null);
                while (cursor.moveToNext()) {
                    check = cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_PW));
                }

                if (check == null) {
                    builder.setTitle("비밀번호가 존재하지 않습니다.");
                    builder.setIcon(R.mipmap.warning);
                    builder.setPositiveButton("확인", null);
                    builder.show();
                }

                else {
                    if (check.equals(inputPw.getText().toString())) {
                        Intent intent = new Intent(MainActivity.this, AllAlcoholActivity.class);
                        startActivity(intent);
                    }
                    else {
                        builder.setTitle("비밀번호가 일치하지 않습니다.");
                        builder.setIcon(R.mipmap.warning);
                        builder.setPositiveButton("확인", null);
                        builder.show();
                    }
                }
                break;
        }
    }
}
