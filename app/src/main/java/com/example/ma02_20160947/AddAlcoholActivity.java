package com.example.ma02_20160947;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class AddAlcoholActivity extends AppCompatActivity {
    final int SEARCH_CODE = 100;
    private final int PERMISSION_REQ_CODE = 400;
    private static final int REQUEST_TAKE_PHOTO = 200;
    private static final int GALLERY_IMAGE_CODE = 300;
    final CharSequence[] select = {"사진 찍기", "갤러리에서 불러오기"};
    int checked;
    AlcoholDBHelper helper;
    ImageView addImage;
    EditText addCategory;
    TextView addDate;
    EditText addName;
    EditText addContent;
    TextView addAddress;
    DatePicker dp;
    Calendar cal;
    int y, m, d;
    String mCurrentPhotoPath;
    private Uri photoUri, albumUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alcohol);

        helper = new AlcoholDBHelper(this);
        addImage = (ImageView)findViewById(R.id.addImage);
        addCategory = (EditText)findViewById(R.id.addCategory);
        addDate = (TextView)findViewById(R.id.addDate);
        addName = (EditText)findViewById(R.id.addName);
        addContent = (EditText)findViewById(R.id.addContent);
        addAddress = (TextView)findViewById(R.id.addAddress);
        cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH)+1;
        d = cal.get(Calendar.DAY_OF_MONTH);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
        }

        addImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddAlcoholActivity.this);
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
        switch(v.getId()) {
            case R.id.btn_date:
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
                        addDate.setText(String.format("%d-%02d-%02d", y, m, d));;
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                break;

            case R.id.btn_address :
                Intent intent = new Intent(this, SearchAddressActivity.class);
                startActivityForResult(intent, SEARCH_CODE);
                break;

            case R.id.btnAdd :
                if (mCurrentPhotoPath == null) {
                    mCurrentPhotoPath = " ";
                }
                galleryAddPic();
                SQLiteDatabase db = helper.getWritableDatabase();

                String category = addCategory.getText().toString();
                String date = addDate.getText().toString();
                String name = addName.getText().toString();
                String content = addContent.getText().toString();
                String address = addAddress.getText().toString();

                ContentValues row = new ContentValues();
                row.put(AlcoholDBHelper.COL_CATEGORY, category);
                row.put(AlcoholDBHelper.COL_DATE, date);
                row.put(AlcoholDBHelper.COL_NAME, name);
                row.put(AlcoholDBHelper.COL_PATH, mCurrentPhotoPath);
                row.put(AlcoholDBHelper.COL_CONTENT, content);
                row.put(AlcoholDBHelper.COL_ADDRESS, address);
                db.insert(AlcoholDBHelper.TABLE_NAME, null, row);
                helper.close();
                finish();
                break;

            case R.id.btnAddCancle:
                File file = new File(mCurrentPhotoPath);
                file.delete();
                finish();
                break;
        }
    }

    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        //intent.setType("image/*");
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
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = addImage.getWidth();
        int targetH = addImage.getHeight();

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
        addImage.setImageBitmap(bitmap);
    }

    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_CODE) {
            switch (resultCode) {
                case RESULT_OK :
                    addAddress.setText(data.getStringExtra("address"));
                    break;
                case RESULT_CANCELED :
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
                    //photoUri = data.getData();
                    //addImage.setImageURI(photoUri);
                    //mCurrentPhotoPath = photoUri.getPath();
                    //String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                    //String fileName = getImageNameToUri(data.getData());
                    //File img = new File(storageDir+ "/" +fileName);
                    //mCurrentPhotoPath = img.getAbsolutePath();
                   // mCurrentPhotoPath = "/sdcard/DCIM/Camera/ + getImageNameToUri(data.getData());
                    photoUri = data.getData();
                    MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    addImage.setImageURI(photoUri);
                    mCurrentPhotoPath = getPath(data.getData());

                   // mCurrentPhotoPath = photoUri.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
