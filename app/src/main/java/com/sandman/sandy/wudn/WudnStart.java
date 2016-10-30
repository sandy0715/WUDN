package com.sandman.sandy.wudn;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.widget.Toast;


// WUDN起動前の処理（Permissionの確認、など）
public class WudnStart extends AppCompatActivity  {

    private final int REQUEST_PERMISSION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wudn_start);
        // Android 6, API 23 以上ならばPermission確認
        if (Build.VERSION.SDK_INT >= 23){
            checkPermission();
        } else{
            launchWudnMain();
        }
    }

    // Check permission
    private void checkPermission(){
        // Already accepted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            launchWudnMain();
        }
        // Not accepted
        else{
            requestLocPermission();
        }
    }

    // Check location permission
    private void requestLocPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        } else {
            Toast.makeText(this, "Cannot use this app without allowing permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // Accepted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchWudnMain();
                return;

            } else {
                // それでも拒否された時の対応
                Toast.makeText(this, "Cannot use this app without allowing permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // WUDN Mainの起動intent発行
    private void launchWudnMain(){
        Intent intent = new Intent(this,WudnMain.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wudn_start, menu);
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
}
