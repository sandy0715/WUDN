package com.sandman.sandy.wudn;

import android.app.Activity;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;
import java.util.List;

public class WudnMain extends Activity implements LocationListener {
    private LocationManager mLocationManager;
    private CurrentLocation mCurrentLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wudn_main);
        // LocationManagerの生成
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 現在情報のメンバ変数の生成
        mCurrentLoc = new CurrentLocation();
        // dis spinnerのadaptorのセット
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("10m");
        adapter.add("100m");
        adapter.add("1km");
        adapter.add("5km");
        adapter.add("10km");
        adapter.add("20km");
        Spinner spinner = (Spinner) findViewById(R.id.dis_spinner);
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wudn_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        // ロケーションマネージャのリスナーを解除
        mLocationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationManager!=null) {
            // ロケーションマネージャのリスナーを登録
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            String provider = mLocationManager.getBestProvider(criteria, true);
            mLocationManager.requestLocationUpdates(provider, 0, 0, this);
        } else {
            Log.v("MAIN","no location manager");
            this.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // ロケーションが変更された時
        Log.v("MAIN","Location Changed");   //log
        if (!mCurrentLoc.isUpdated()) {  // CurrentLocが未更新ならば、更新されることを通知（本当はGoボタンを非アクティブにしたい...）
            Toast.makeText(this, "Connected! You are able to Go!", Toast.LENGTH_LONG).show();
        }
        mCurrentLoc.setCurrentLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        // ロケーションプロバイダーが無効になった時
    }

    @Override
    public void onProviderEnabled(String provider) {
        // ロケーションプロバイダーが有効になった時
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // ロケーションプロバイダーが変更された時
    }


    // go buttonのonClickメソッド
    public void goStandby(View view) throws java.io.IOException {
        // 緯度経度をトースト表示
        //Toast.makeText(this,String.valueOf(mCurrentLoc.getCurrentLatitude()) + "," + String.valueOf(mCurrentLoc.getCurrentLongitude()), Toast.LENGTH_LONG).show();
        if (!mCurrentLoc.isUpdated()) {  // CurrentLocが未更新ならば、エラーメッセージを出して終了
            Log.v("MAIN", "Go button pressed but CurrentLoc is not updated yet.");   //log
            Toast.makeText(this, "Connecting...Please wait.", Toast.LENGTH_LONG).show();
            return;
        }
        Geocoder geocoder = new Geocoder(this, Locale.JAPAN);
        List<Address> list_address = geocoder.getFromLocation(mCurrentLoc.getCurrentLatitude(), mCurrentLoc.getCurrentLongitude(), 5);
        if (!list_address.isEmpty()) {
            for (int i = 0; i < list_address.size(); i++) {
                Log.v("MAIN", "AddressLine[" + i + "]:" + list_address.get(i).getAddressLine(1));   //log
            }
            Toast.makeText(this, list_address.get(2).getAddressLine(1), Toast.LENGTH_LONG).show();
        } else {  // Address取れなかった場合
            Log.v("MAIN", "[Geocoder] No address found");   //log
            Toast.makeText(this, "Address not found!", Toast.LENGTH_LONG).show();
        }
    }
}
