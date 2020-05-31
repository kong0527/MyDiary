package com.example.ma02_20160947;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ma02_20160947.model.MyPlace;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;


public class SearchGPSActivity extends AppCompatActivity {
    EditText etSearch;
    private  final static int REQ_PERMISSIONS = 100;
    private GoogleMap mGoogleMap;
    private LocationManager mLocManager;
    private GeoDataClient mGeoDataClient;
    private Location mLastLoc;
    ProgressDialog progressDialog;
    final static String TAG = "SearchGPSActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_gps);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                    REQ_PERMISSIONS);
            return;
        }

        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        Provider로부터 최종 수신 위치를 받음
        mLastLoc = mLocManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

//        최종 수신 위치가 없을 경우 기본 위치 설정
        if (mLastLoc == null) {
            mLastLoc.setLatitude( Double.valueOf(getResources().getString(R.string.init_latitude)) );
            mLastLoc.setLongitude( Double.valueOf(getResources().getString(R.string.init_latitude)) );
        }

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

//        위치 상세정보를 얻기 위한 Google place의 GedDataClient 준비
        mGeoDataClient = Places.getGeoDataClient(this);

    }

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLoc.getLatitude(), mLastLoc.getLongitude()), 17));

//            Marker의 window를 클릭할 경우
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    MyPlace place = (MyPlace) marker.getTag();  // Marker가 저장하고 있는 MyPlace 확인
                    Log.i(TAG, place.getName() + ": " + place.getAddress());
                }
            });
        }
    };

    private void addMarker(MyPlace place) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
        markerOptions.title(place.getName());
        markerOptions.snippet(place.getPhone());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        Marker item = mGoogleMap.addMarker(markerOptions);
        item.setTag(place);     // Marker 에 표시하는 대상 MyPlace 를 저장 -> Marker 를 클릭할 경우 getTag() 로 place 확인
    }

    PlacesListener placesListener = new PlacesListener() {

        @Override
        public void onPlacesFailure(PlacesException e) {
        }

        //        요청 시작 시 상태창 출력
        @Override
        public void onPlacesStart() {
            progressDialog = ProgressDialog.show(SearchGPSActivity.this, "Wait", "Searching...");
        }

        @Override
        public void onPlacesSuccess(final List<Place> places) {
            Log.i(TAG, "onPlaceSuccess");   // 수행상황을 확인하기 위해 log 를 찍어볼 것
            for (Place place : places) {
//                응답 결과의 place에서 확인한 place id 로 Google GeoDataClient에게 상세정보 요청
                Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(place.getPlaceId());

                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        Log.i(TAG, "onComplete");       // 수행상황을 확인하기 위해 log 를 찍어볼 것
                        if (task.isSuccessful()) {
                            // 확인이 필요할 경우 각 변수들을 log로 찍어볼 것
                            PlaceBufferResponse response = task.getResult();
                            com.google.android.gms.location.places.Place detailedPlace = response.get(0);   // 결과로 받은 Google Place의 place 객체

                            double latitude = Double.valueOf(detailedPlace.getLatLng().latitude);
                            double longitude = Double.valueOf(detailedPlace.getLatLng().longitude);

//                            개발자가 직접 정의한 MyPlace 객체에 정보 저장
                            MyPlace myPlace = new MyPlace(detailedPlace.getName().toString(), detailedPlace.getId(), latitude , longitude);
                            myPlace.setAddress(detailedPlace.getAddress().toString());
                            myPlace.setPhone(detailedPlace.getPhoneNumber().toString());

//                            Marker 생성 - 코드 가독성을 위해 메소드로 분리
                            addMarker(myPlace);

                            response.release();     // 결과 response 는 반드시 해제 수행
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });
            }

        }

        //        요청 종료 시 상태창 종료
        @Override
        public void onPlacesFinished() {
            progressDialog.dismiss();
        }
    };


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                finish();
                break;

            case R.id.btn_gps:
                mGoogleMap.clear();
                new NRPlaces.Builder().listener(placesListener)
                        .key(getResources().getString(R.string.google_api_key))
                        .latlng(mLastLoc.getLatitude(), mLastLoc.getLongitude())
                        .radius(200)
                        .type(PlaceType.BAR)
                        .build()
                        .execute();
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SearchGPSActivity.this, R.string.permission_ok_toast, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchGPSActivity.this, R.string.permission_failed_toast, Toast.LENGTH_SHORT).show();
                }
        }
    }

}

