package com.lomoho.pddpickup;

import android.app.Activity;
import android.os.Bundle;

/** 透明跳板 Activity：接住小组件的点击，完成跳转后立即关闭。 */
public class JumpActivity extends Activity {

    public static final String EXTRA_TARGET = "target";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String target = getIntent() != null ? getIntent().getStringExtra(EXTRA_TARGET) : null;
        PddLinks.open(this, target == null ? PddLinks.TARGET_PACKAGES : target);
        finish();
    }
}
