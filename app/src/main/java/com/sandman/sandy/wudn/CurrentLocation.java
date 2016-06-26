package com.sandman.sandy.wudn;

/**
 * Created by sandy on 4/17/16.
 * CurrentLocation: 現在位置の緯度経度を格納するクラス
 */
public class CurrentLocation {
    private double mLatitude;
    private double mLongitude;

    // コンストラクタ
    public CurrentLocation(){
        mLatitude = 0;
        mLongitude = 0;
    }

    // CurrentLocationが更新されているかどうかを確認
    public boolean isUpdated(){
        if (mLatitude == 0 && mLongitude == 0){
            return false;
        }else{
            return true;
        }
    }

    // CurrentLocationに現在の緯度経度をセット
    public void setCurrentLocation(double lati, double longi){
        mLatitude = lati;
        mLongitude = longi;
    }

    // 現在の緯度を返す
    public double getCurrentLatitude(){
        return mLatitude;
    }

    //現在の経度を返す
    public double getCurrentLongitude(){
        return mLongitude;
    }

}
