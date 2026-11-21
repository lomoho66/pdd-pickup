package com.lomoho.pddpickup;

import android.app.Activity;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.widget.FrameLayout;

/**
 * 透明跳板 Activity：接住小组件的点击，震一下（系统触感反馈，不需要震动权限）再跳转，随即关闭。
 * 用半透明主题 + 一个空视图承载触感反馈；NoDisplay 主题没有视图、触发不了震动。
 */
public class JumpActivity extends Activity {

    public static final String EXTRA_TARGET = "target";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        setContentView(frame);
        String target = getIntent() != null ? getIntent().getStringExtra(EXTRA_TARGET) : null;
        String dest = target == null ? PddLinks.TARGET_PACKAGES : target;
        frame.post(() -> {
            frame.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            PddLinks.open(this, dest);
            finish();
        });
    }
}
