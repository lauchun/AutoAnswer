package com.lauchun.autoanswer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

/**
 * @author ：lauchun
 * @date ：Created in 2/8/22
 * @description ：
 * @version: 1.0
 */
public class CallActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public enum CALL_OP {
        CALL_ACCEPT,
        CALL_END,
        AUTO_CALL,
        NONE
    }

    public static CALL_OP sCall_op = CALL_OP.NONE;
}
