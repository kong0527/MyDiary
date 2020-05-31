package com.example.ma02_20160947;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ma02_20160947.DB.AlcoholDBHelper;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class DetailAlcoholActivity extends AppCompatActivity {
    TextView detailCategory;
    TextView detailDate;
    TextView detailName;
    TextView detailContent;
    TextView detailAddress;
    ImageView detailImg;
    String path;
    long id = 0;
    AlcoholDBHelper helper;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_alcohol);
        helper = new AlcoholDBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        detailCategory = (TextView)findViewById(R.id.detailCategory);
        detailDate = (TextView)findViewById(R.id.detailDate);
        detailName = (TextView)findViewById(R.id.detailName);
        detailContent = (TextView)findViewById(R.id.detailContent);
        detailImg = (ImageView)findViewById(R.id.detailImg);
        detailAddress = (TextView)findViewById(R.id.detailAddress);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", id);
        Cursor cursor = db.rawQuery("SELECT * FROM " + AlcoholDBHelper.TABLE_NAME + " WHERE _id = '" + id + "';", null);

        while (cursor.moveToNext()) {
            path = cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_PATH));
            File imgFile = new File(path);
           // uri = Uri.parse("content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F71/ORIGINAL/NONE/1486658596");
            //detailImg.setImageURI(Uri.parse(path));

            //Uri filePath = Uri.parse(path);
            //String file_name = filePath.getLastPathSegment().toString();
            //String file_path = filePath.getPath();

            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                detailImg.setImageBitmap(bitmap);
            }

            detailCategory.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_CATEGORY)));
            detailDate.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_DATE)));
            detailName.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_NAME)));
            detailContent.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_CONTENT)));
            detailAddress.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_ADDRESS)));
        }
        cursor.close();
        helper.close();
    }

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.btnDetail :
                finish();
                break;

            case R.id.btnEdit:
                Intent intent = new Intent(DetailAlcoholActivity.this, EditAlcoholActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
                break;
        }
    }
}
