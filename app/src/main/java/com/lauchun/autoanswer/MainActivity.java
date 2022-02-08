package com.lauchun.autoanswer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lauchun.utils.PermissionUtil;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE
    };

    private TextView tv_phone;
    private EditText tv_num;
    private Button bt_call;
    private TelephonyManager tManager;
    private StringBuilder info;
    private String[] phoneType = {"NONE", "GSM", "CDMA", "SIP"};
    private String[] simState = {"UNKNOWN", "ABSENT", "PIN_REQUIRED", "PUK_REQUIRED",
            "NETWORK_LOCKED", "READY", "NOT_READY", "PERM_DISABLED", "CARD_IO_ERROR",
            "CARD_RESTRICTED"};
    private String[] dataType = {"UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA", "EVDO_0", "EVDO_A",
            "1xRTT", "HSDPA", "HSUPA", "HSPA", "IDEN", "EVDO_B", "LTE", "EHRPD", "HSPAP", "GSM",
            "TD_SCDMA", "IWLAN", "LTE_CA"};


    private PermissionUtil mPermissionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_num = (EditText) findViewById(R.id.tv_num);
        bt_call = (Button) findViewById(R.id.bt_call);
        bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhone(tv_num.getText().toString());
            }
        });

        tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPermissionUtil = new PermissionUtil(this);
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPermissionUtil.checkPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    @SuppressLint("MissingPermission")
    private void bindViews() {
        tv_phone = findViewById(R.id.tv_phone);
        info = new StringBuilder();
        try {
            int dataNetworkType = tManager.getDataNetworkType();
            int phoneType = tManager.getPhoneType();
            int simState = tManager.getSimState();
            info.append("手机类型：" + this.phoneType[tManager.getPhoneType()]);
            info.append("\n运营商代号：" + tManager.getNetworkOperator());
            info.append("\n运营商名称：" + tManager.getNetworkOperatorName());
            info.append("\n网络类型：" + this.dataType[tManager.getNetworkType()]);
            info.append("\n手机号码：" + tManager.getLine1Number());
            info.append("\nSIM卡的国别：" + tManager.getSimCountryIso().toUpperCase());
            info.append("\nSIM卡状态：" + this.simState[tManager.getSimState()]);
        } catch (Exception e) {
            System.out.println("————————————————————开始Log————————————————————");
            e.printStackTrace();
            System.out.println("————————————————————结束Log————————————————————");
        }
        tv_phone.setText(info);
    }

    private void callPhone(String lineNumber) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (lineNumber.isEmpty()) {
            AlertDialog();
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + lineNumber);
            intent.setData(data);
            startActivity(intent);
        }
    }

    private void AlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("注意")
                .setMessage("手机号码不能为空！")
                .setPositiveButton("知道了", null)
                .show();
    }
}