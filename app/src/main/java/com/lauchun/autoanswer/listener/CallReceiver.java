package com.lauchun.autoanswer.listener;

import android.content.Context;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lauchun.autoanswer.CallActivity;
import com.lauchun.autoanswer.utils.PhoneUtil;

/**
 * @author ：lauchun
 * @date ：Created in 2/9/22
 * @description ：
 * @version: 1.0
 */
public class CallReceiver extends PhoneStateListener {

    private Context mContext;
    private int calls;

    public CallReceiver(Context context) {
        mContext = context;
    }

    public CallReceiver() {

    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        Log.d("PhoneListen", "CallReceiver onServiceStateChanged: " + serviceState);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.d("PhoneListen", "CallReceiver state: "
                + state + " incomingNumber: " + incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                if (CallActivity.sCall_op == CallActivity.CALL_OP.AUTO_CALL) {
                    for (int i = 1; i < PhoneUtil.getCallTimes(); i++) {
                        SystemClock.sleep(4 * 1000);
                        Log.d("PhoneListen", "CallReceiver onCallStateChanged callPhone");
                        PhoneUtil.callPhone(mContext, PhoneUtil.getPhoneNum());
                    }
                    CallActivity.sCall_op = CallActivity.CALL_OP.NONE;
                }
            case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                if (CallActivity.sCall_op == CallActivity.CALL_OP.CALL_ACCEPT) {
                    Log.d("PhoneListen", "CallReceiver onCallStateChanged acceptCall");
                    SystemClock.sleep(2 * 1000);
                    PhoneUtil.acceptCall(mContext);
                    SystemClock.sleep(4 * 1000);
                    CallActivity.sCall_op = CallActivity.CALL_OP.CALL_END;

                } else {
                    return;
                }
            case TelephonyManager.CALL_STATE_OFFHOOK:   // 来电接通 或者 去电，去电接通
                if (CallActivity.sCall_op == CallActivity.CALL_OP.CALL_END) {
                    Log.d("PhoneListen", "CallReceiver onCallStateChanged endCall");
                    PhoneUtil.endCall(mContext);
                    CallActivity.sCall_op = CallActivity.CALL_OP.CALL_ACCEPT;
                } else {
                    return;
                }
        }
    }
}
