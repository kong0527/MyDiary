package com.example.ma02_20160947.model;

import android.text.Html;
import android.text.Spanned;

import java.io.Serializable;

public class LocalDto implements Serializable {
    private int _id;
    private String title; //음식점 이름
    private String telephone; //음식점 번호
    private String address; //음식점 주소
    private int mapx; //장소의 x좌표 (카텍좌표임)
    private int mapy; //장소의 y좌표 (카텍좌표임)

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        Spanned spanned = Html.fromHtml(title);
        return spanned.toString();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMapx() {
        return mapx;
    }

    public void setMapx(int mapx) {
        this.mapx = mapx;
    }

    public int getMapy() {
        return mapy;
    }

    public void setMapy(int mapy) {
        this.mapy = mapy;
    }
}
