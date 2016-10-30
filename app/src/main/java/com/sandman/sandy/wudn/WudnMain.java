package com.sandman.sandy.wudn;

import android.app.Activity;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.List;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        if (mLocationManager != null) {
            // ロケーションマネージャのリスナーを登録
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            String provider = mLocationManager.getBestProvider(criteria, true);
            mLocationManager.requestLocationUpdates(provider, 0, 0, this);
        } else {
            Log.v("MAIN", "no location manager");
            this.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // ロケーションが変更された時
        Log.v("MAIN", "Location Changed");   //log
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
            if (list_address.size() > 0) {
                Toast.makeText(this, list_address.get(0).getAddressLine(1), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No location found.", Toast.LENGTH_LONG).show();
            }

        } else {  // Address取れなかった場合
            Log.v("MAIN", "[Geocoder] No address found");   //log
            Toast.makeText(this, "Address not found!", Toast.LENGTH_LONG).show();
        }
    }

    public void saveCurrentLoc(View view) throws java.io.IOException {
        if (mCurrentLoc.isUpdated()) {  // CurrentLocが未更新ならば、何もせず
            Geocoder geocoder = new Geocoder(this, Locale.JAPAN);
            List<Address> list_address = geocoder.getFromLocation(mCurrentLoc.getCurrentLatitude(), mCurrentLoc.getCurrentLongitude(), 5);
            String address = "";
            if (!list_address.isEmpty()) {
                if (list_address.size() > 0) {
                    address = "Address" + list_address.get(0).getAddressLine(1);
                }
            }
            Date now = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日'　kk'時'mm'分'ss'秒'");
            String str = "Time:" + sdf.format(now) + "Lat:" + mCurrentLoc.getCurrentLatitude() + "Lon" + mCurrentLoc.getCurrentLongitude() + address;

            // 外部ストレージにログ用のディレクトリ作成 -> 使えない
            //File logpath = new File(Environment.getExternalStorageDirectory(), "wudn");
            // ダウンロードストレージにディレクトリ作成
            File logpath = new File(Environment.getDataDirectory(), "wudn");
            Log.v("MAIN", "Log file path :" + logpath.getAbsolutePath());   //log
            if (logpath.exists() != true) {  //ディレクトリがない場合は作成
                logpath.mkdir();
            }
            // Logfileのオープン
            File logfile = new File(logpath, "locLog.txt");
            FileWriter out = null;
            try {
                // ログの追加書き込み
                out = new FileWriter(logfile, true);
                out.write(str);
                out.write("\n");
                Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}