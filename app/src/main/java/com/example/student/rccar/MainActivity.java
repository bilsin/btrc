package com.example.student.rccar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;

public class MainActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "Main";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Button btnConnect;

    private BluetoothService btService = null;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (btService == null) {
            btService = new BluetoothService(this, mHandler);
        }

        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 블루투스가 지원 가능할 경우
                if (btService.getDeviceState()) {
                    btService.enableBluetooth();
                } else {
                    finish();
                }
            }
        });

        checkDangerousPermission();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 블루투스 켜진 상태에서 버튼 클릭시
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    btService.getDeviceInfo(data);
                }
                break;

            // 블루투스 활성화 알림창 결과
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    btService.scanDevice();
                } else {
                    // 취소 눌렀을 때
                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }

    /**
     * 블루투스 검색을 위한 위치 권한 승인
     */
    void checkDangerousPermission() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);

            // 권한 승인
            if(permissionCheck == PackageManager.PERMISSION_GRANTED)    {
                Toast.makeText(getApplicationContext(), "Granted", Toast.LENGTH_SHORT).show();
            }
            else    {
                Toast.makeText(getApplicationContext(), "denied", Toast.LENGTH_SHORT).show();

                if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]))   {
                    Toast.makeText(getApplicationContext(), "Need Info", Toast.LENGTH_SHORT).show();
                }
                else    {
                    // 권한 재요청
                    ActivityCompat.requestPermissions(this, permissions, 1);
                }
            }
        }
    }
}
