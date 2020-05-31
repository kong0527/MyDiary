package com.example.ma02_20160947;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ma02_20160947.DB.AlcoholDBHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditAlcoholActivity extends AppCompatActivity {
    long id = 0;
    final CharSequence[] select = {"사진 찍기", "갤러리에서 불러오기"};
    final int SEARCH_CODE = 100;
    private static final int REQUEST_TAKE_PHOTO = 200;
    private static final int GALLERY_IMAGE_CODE = 300;
    AlcoholDBHelper helper;
    ImageView editImage;
    EditText editCategory;
    TextView editDate;
    EditText editTitle;
    EditText editContent;
    TextView editAddress;
    Calendar cal;
    int y, m, d;
    DatePicker dp;
    int checked;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alcohol);
        Intent intent = getIntent();
        id = intent.getLongExtra("id", id);
        helper = new AlcoholDBHelper(this);

        editImage = (ImageView)findViewById(R.id.editImage);
        editCategory = (EditText)findViewById(R.id.editCategory);
        editDate = (TextView)findViewById(R.id.editDate);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editContent = (EditText)findViewById(R.id.editContent);
        editAddress = (TextView)findViewById(R.id.editAddress);

        cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH)+1;
        d = cal.get(Calendar.DAY_OF_MONTH);

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + AlcoholDBHelper.TABLE_NAME + " WHERE _id = '" + id + "';", null);

        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_PATH));
            File imgFile = new File(path);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                editImage.setImageBitmap(bitmap);
            }
            mCurrentPhotoPath = path;
            editCategory.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_CATEGORY)));
            editDate.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_DATE)));
            editTitle.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_NAME)));
            editContent.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_CONTENT)));
            editAddress.setText(cursor.getString(cursor.getColumnIndex(AlcoholDBHelper.COL_ADDRESS)));
        }

        helper.close();
        cursor.close();

        editImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(EditAlcoholActivity.this);
                    builder.setTitle("사진을 불러올 방법을 선택하세요");
                    builder.setIcon(R.mipmap.gallery);
                    builder.setSingleChoiceItems(select, checked, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checked = i;
                        }
                    });
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (checked) {
                                case 0:
                                    dispatchTakePictureIntent();
                                    setPic();
                                    break;
                                case 1:
                                    selectGallery();
                                    break;
                            }
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.show();
                }
                return false;
            }
        });

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEditDate:
                final LinearLayout addLayout = (LinearLayout)View.inflate(this, R.layout.date_select, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                dp = (DatePicker)addLayout.findViewById(R.id.dateSel);
                builder.setTitle("날짜 선택");
                builder.setView(addLayout);
                dp.init(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        y = year;
                        m = monthOfYear+1;
                        d = dayOfMonth;
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editDate.setText(String.format("%d-%02d-%02d", y, m, d));;
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                break;

            case R.id.btnEditAddress:
                Intent intent = new Intent(this, SearchAddressActivity.class);
                startActivityForResult(intent, SEARCH_CODE);
                break;

            case R.id.editComplete:
                SQLiteDatabase db = helper.getWritableDatabase();
                String whereClause = AlcoholDBHelper.COL_ID + "=?";
                String[] whereArgs = new String[] { String.valueOf(id) };
                String category = editCategory.getText().toString();
                String date = editDate.getText().toString();
                String name = editTitle.getText().toString();
                String content = editContent.getText().toString();
                String address = editAddress.getText().toString();

                ContentValues row = new ContentValues();
                row.put(AlcoholDBHelper.COL_CATEGORY, category);
                row.put(AlcoholDBHelper.COL_DATE, date);
                row.put(AlcoholDBHelper.COL_NAME, name);
                row.put(AlcoholDBHelper.COL_PATH, mCurrentPhotoPath);
                row.put(AlcoholDBHelper.COL_CONTENT, content);
                row.put(AlcoholDBHelper.COL_ADDRESS, address);
                db.update(AlcoholDBHelper.TABLE_NAME,  row, whereClause, whereArgs);
                helper.close();
                finish();
                break;

            case R.id.editCancle:
                EditAlcoholActivity.this.finish();
                break;
        }
    }

    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_IMAGE_CODE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ma02_20160947.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File (mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = editImage.getWidth();
        int targetH = editImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        editImage.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    editAddress.setText(data.getStringExtra("address"));
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "주소 추가 취소", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }

        else if (requestCode == GALLERY_IMAGE_CODE && resultCode == RESULT_OK) {
            // String n = getImageNameToUri(data.getData());
            //Toast.makeText(this, n, Toast.LENGTH_SHORT).show();
            if (data.getData() != null) {
                try {
                    Uri photoUri = data.getData();
                    editImage.setImageURI(data.getData());
                    mCurrentPhotoPath = photoUri.toString();
                    String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                    //String fileName = getImageNameToUri(data.getData());
                   // File img = new File(storageDir+ "/" +fileName);
                   // mCurrentPhotoPath = img.getAbsolutePath();
                    //mCurrentPhotoPath = "/sdcard/DCIM/Camera/" + getImageNameToUri(data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
