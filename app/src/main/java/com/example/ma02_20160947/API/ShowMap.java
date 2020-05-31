package com.example.ma02_20160947.API;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ma02_20160947.R;
import com.example.ma02_20160947.model.LocalDto;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

//네이버 지도 API 이용
public class ShowMap extends NMapActivity implements NMapView.OnMapStateChangeListener {
    LocalDto dto;
    NMapView mMapView = null; // 맵 컨트롤러
    NMapController mMapController = null; // 맵을 추가할 레이아웃
    LinearLayout MapContainer;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_address);
        Intent intent = getIntent();
        dto = (LocalDto) intent.getSerializableExtra("result");
        Toast.makeText(this, dto.getAddress(), Toast.LENGTH_LONG).show();
        MapContainer = (LinearLayout) findViewById(R.id.map_view);

        //네이버 지역 검색 API에서는 카텍좌표계를 사용하기 때문에 앞서 받아온 주소를 위도, 경도로 변환하는 지오코딩이 필요
        Geocoder coder = new Geocoder(this);
        try {
            List<Address> addrList = coder.getFromLocationName(dto.getAddress(),3);
            Iterator<Address> addrs = addrList.iterator();
            String infoAddr = "";
            while(addrs.hasNext()) {
                Address loc = addrs.next();
                infoAddr += String.format("Coord : %f, %f", loc.getLatitude(), loc.getLongitude());
                lat = loc.getLatitude();
                lng = loc.getLongitude();
            }
            Toast.makeText(this, infoAddr, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 네이버 지도 API 객체 생성;
        mMapView = new NMapView(this);
        mMapView.setClientId(getResources().getString(R.string.client_id));
        mMapController = mMapView.getMapController(); // 지도 객체로부터 컨트롤러 추출
        MapContainer.addView(mMapView); // 생성된 네이버 지도 객체를 LinearLayout에 추가시킨다.
        mMapView.setClickable(true); // 지도를 터치할 수 있도록 옵션 활성화
        mMapView.setScalingFactor(3, true); //지도를 좀더 확대
        mMapView.setOnMapStateChangeListener(this); // 지도에 대한 상태 변경 이벤트 연결
        onMapInitHandler(mMapView, null); //map을 표시
    }


    @Override
    public void onMapInitHandler(NMapView mapview, NMapError errorInfo) {
        if (errorInfo == null) { // success
            mMapController.setMapCenter(
                    new NGeoPoint(lng, lat), 11);
        } else { // fail
            android.util.Log.e("NMAP", "onMapInitHandler: error="
                    + errorInfo.toString());
        }
    }

    @Override
    public void onZoomLevelChange(NMapView mapview, int level) {
    }

    /**
     * 지도 중심 변경 시 호출되며 변경된 중심 좌표가 파라미터로 전달된다.
     */
    @Override
    public void onMapCenterChange(NMapView mapview, NGeoPoint center) {
    }

    /**
     * 지도 애니메이션 상태 변경 시 호출된다.
     * animType : ANIMATION_TYPE_PAN or ANIMATION_TYPE_ZOOM
     * animState : ANIMATION_STATE_STARTED or ANIMATION_STATE_FINISHED
     */
    @Override
    public void onAnimationStateChange(
            NMapView arg0, int animType, int animState) {
    }

    @Override
    public void onMapCenterChangeFine(NMapView arg0) {
    }

}
