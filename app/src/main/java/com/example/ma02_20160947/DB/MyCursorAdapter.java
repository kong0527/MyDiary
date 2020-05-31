package com.example.ma02_20160947.DB;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ma02_20160947.R;

import java.io.File;

public class MyCursorAdapter extends CursorAdapter {
    private int layout;
    LayoutInflater inflator;
    Cursor cursor;

    public MyCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cursor = c;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView alcoholImg = (ImageView)view.findViewById(R.id.alcoholImg);
        TextView alcoholName = (TextView)view.findViewById(R.id.alcoholName);
        TextView alcoholDate = (TextView)view.findViewById(R.id.alcoholDate);
        File img = new File(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_PATH)));
        if (img.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
            alcoholImg.setImageBitmap(bitmap);
        }
        //cursor.getInt(cursor.getColumnIndex(AlcoholDBHelper.COL_IMG))
        alcoholName.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_NAME)));
        alcoholDate.setText(cursor.getString(cursor.getColumnIndex((AlcoholDBHelper.COL_DATE))));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View listItemLayout = inflator.inflate(R.layout.listview_layout, viewGroup, false);
        return listItemLayout;
    }
}
