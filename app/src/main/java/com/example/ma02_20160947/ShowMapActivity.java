package com.example.ma02_20160947;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ma02_20160947.model.LocalDto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class


ShowMapActivity extends AppCompatActivity {
    private final static int PERMISSION_REQ_CODE = 100;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private List<Address> addressList;
    LocalDto dto;
    private  String address;
    private MarkerOptions markerOptions;
    private Marker marker;
    private String title;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        Intent intent = getIntent();
        dto = (LocalDto)intent.getSerializableExtra("information");
        address = dto.getAddress();
        title  = dto.getTitle();
        phone = dto.getTelephone();
        addressList = null;
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(mapReadyCallBack);
        geocoder = new Geocoder(this);
        try
        {
            addressList = geocoder.getFromLocationName(address, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap map) {
            googleMap = map;
            LatLng loc = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
            if (loc == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowMapActivity.this);
                builder.setTitle("지도 정보를 불러올 수 없습니다. 다시 시도해주세요");
                builder.setPositiveButton("확인", null);
                builder.show();
            }

            else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18));

                markerOptions = new MarkerOptions();
                markerOptions.position(loc);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.yellow_marker));
                markerOptions.title(title);
                markerOptions.snippet(phone);
                marker = googleMap.addMarker(markerOptions);

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        marker.showInfoWindow();
                    }
                });
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back :
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("돌아가시겠습니까?");
                builder.setIcon(R.mipmap.back);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ShowMapActivity.this.finish();
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_REQ_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission was granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission was denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
