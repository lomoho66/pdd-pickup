package com.lomoho.pddpickup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 打赏页：展示爱发电卡片（扫码发电），点图或按钮跳浏览器打开爱发电主页。零权限，走系统跳转。 */
public class DonateActivity extends Activity {

    private static final String AFDIAN_URL = "https://afdian.com/a/lomoho";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = Ui.page(this, "打赏");

        TextView desc = new TextView(this);
        desc.setText("如果这个小工具帮你省了扫码翻找的时间，\n可以随手发电支持一下，金额随意。");
        desc.setTextSize(14);
        desc.setTextColor(Ui.grey(this));
        desc.setGravity(Gravity.CENTER);
        desc.setLineSpacing(0, 1.5f);
        LinearLayout.LayoutParams dLp = Ui.matchWrap();
        dLp.topMargin = Ui.dp(this, 18);
        root.addView(desc, dLp);

        // 爱发电卡片图（点图打开爱发电主页）
        ImageView cardImg = new ImageView(this);
        cardImg.setImageResource(R.drawable.afdian_card);
        cardImg.setAdjustViewBounds(true);
        cardImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        GradientDrawable clip = new GradientDrawable();
        clip.setColor(Color.TRANSPARENT);
        clip.setCornerRadius(Ui.dp(this, 20));
        cardImg.setBackground(clip);
        cardImg.setClipToOutline(true);
        cardImg.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            openAfdian();
        });
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgLp.topMargin = Ui.dp(this, 20);
        root.addView(cardImg, imgLp);

        // 打开爱发电按钮
        LinearLayout btnWrap = new LinearLayout(this);
        btnWrap.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView btn = new TextView(this);
        btn.setText("打开爱发电");
        btn.setTextSize(15);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setTextColor(Color.WHITE);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(Ui.dp(this, 30), Ui.dp(this, 12), Ui.dp(this, 30), Ui.dp(this, 12));
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColors(new int[]{Color.rgb(0xFF, 0x5A, 0x4F), Color.rgb(0xE0, 0x2E, 0x24)});
        btnBg.setOrientation(GradientDrawable.Orientation.TL_BR);
        btnBg.setCornerRadius(Ui.dp(this, 24));
        btn.setBackground(btnBg);
        btn.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            openAfdian();
        });
        btnWrap.addView(btn);
        LinearLayout.LayoutParams bLp = Ui.matchWrap();
        bLp.topMargin = Ui.dp(this, 22);
        root.addView(btnWrap, bLp);

        TextView hint = new TextView(this);
        hint.setText("点击图片或按钮支持作者");
        hint.setTextSize(12);
        hint.setTextColor(Ui.grey(this));
        hint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hLp = Ui.matchWrap();
        hLp.topMargin = Ui.dp(this, 12);
        root.addView(hint, hLp);

        setContentView(Ui.scrollWrap(this, root));
    }

    private void openAfdian() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AFDIAN_URL)));
        } catch (Exception ignored) {
        }
    }
}
