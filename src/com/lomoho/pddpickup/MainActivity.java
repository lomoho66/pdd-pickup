package com.lomoho.pddpickup;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 主界面：一个大按钮「打开待取件包裹」+ 添加小组件说明 + 关于卡片。 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        int pad = dp(24);
        root.setPadding(pad, dp(48), pad, pad);
        root.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("拼多多取件码");
        title.setTextSize(24);
        title.setTextColor(Color.rgb(0x21, 0x21, 0x21));
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView sub = new TextView(this);
        sub.setText("点按钮，或把桌面小组件放到桌面，一点直达待取件包裹");
        sub.setTextSize(14);
        sub.setTextColor(Color.rgb(0x75, 0x75, 0x75));
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = matchWrap();
        subLp.topMargin = dp(8);
        sub.setLayoutParams(subLp);
        root.addView(sub);

        Button btnPkg = new Button(this);
        btnPkg.setText("打开待取件包裹");
        btnPkg.setTextSize(17);
        btnPkg.setTextColor(Color.WHITE);
        btnPkg.setBackgroundColor(Color.rgb(0xE0, 0x2E, 0x24));
        btnPkg.setAllCaps(false);
        btnPkg.setOnClickListener(v -> PddLinks.open(this, PddLinks.TARGET_PACKAGES));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(60));
        bLp.topMargin = dp(36);
        root.addView(btnPkg, bLp);

        TextView tip = new TextView(this);
        tip.setText("添加小组件：长按手机桌面空白处 → 小组件 → 找到「拼多多取件码」→ 拖到桌面。以后在桌面点它即可直接打开待取件包裹页面。");
        tip.setTextSize(13);
        tip.setTextColor(Color.rgb(0x9E, 0x9E, 0x9E));
        LinearLayout.LayoutParams tipLp = matchWrap();
        tipLp.topMargin = dp(36);
        tip.setLayoutParams(tipLp);
        root.addView(tip);

        root.addView(buildAboutCard());

        setContentView(root);
    }

    /** 关于卡片：程序名、版本、简介、作者、致谢。 */
    private LinearLayout buildAboutCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        int cp = dp(16);
        card.setPadding(cp, cp, cp, cp);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(0xF5, 0xF5, 0xF5));
        bg.setCornerRadius(dp(16));
        card.setBackground(bg);
        LinearLayout.LayoutParams cardLp = matchWrap();
        cardLp.topMargin = dp(40);
        card.setLayoutParams(cardLp);

        TextView name = new TextView(this);
        name.setText("拼多多取件码");
        name.setTextSize(18);
        name.setTextColor(Color.rgb(0x21, 0x21, 0x21));
        card.addView(name);

        String ver = "1.2";
        try {
            ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        TextView version = new TextView(this);
        version.setText("版本 " + ver);
        version.setTextSize(13);
        version.setTextColor(Color.rgb(0x75, 0x75, 0x75));
        LinearLayout.LayoutParams vLp = matchWrap();
        vLp.topMargin = dp(4);
        version.setLayoutParams(vLp);
        card.addView(version);

        TextView intro = new TextView(this);
        intro.setText("一个小工具：点一下，直达待取件包裹。无广告、不联网、不要权限。");
        intro.setTextSize(14);
        intro.setTextColor(Color.rgb(0x42, 0x42, 0x42));
        LinearLayout.LayoutParams iLp = matchWrap();
        iLp.topMargin = dp(10);
        intro.setLayoutParams(iLp);
        card.addView(intro);

        card.addView(divider());
        card.addView(infoRow("作者", "酷安 @LOMOHO"));
        card.addView(divider());
        card.addView(infoRow("致谢", "Muse by Meta"));
        return card;
    }

    private View divider() {
        View v = new View(this);
        v.setBackgroundColor(Color.rgb(0xE0, 0xE0, 0xE0));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
        lp.topMargin = dp(12);
        lp.bottomMargin = dp(12);
        v.setLayoutParams(lp);
        return v;
    }

    private LinearLayout infoRow(String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        TextView l = new TextView(this);
        l.setText(label);
        l.setTextSize(15);
        l.setTextColor(Color.rgb(0x21, 0x21, 0x21));
        row.addView(l, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView r = new TextView(this);
        r.setText(value);
        r.setTextSize(15);
        r.setTextColor(Color.rgb(0x75, 0x75, 0x75));
        r.setGravity(Gravity.END);
        row.addView(r, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
        return row;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
