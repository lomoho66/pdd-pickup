package com.lomoho.pddpickup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 关于页（更多页入口）：只列作者与致谢。作者含酷安与 GitHub，两项都是超链接可点跳转。 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = Ui.page(this, "作者信息");

        LinearLayout card = Ui.whiteCard(this);
        LinearLayout.LayoutParams cardLp = Ui.matchWrap();
        cardLp.topMargin = Ui.dp(this, 24);
        card.setLayoutParams(cardLp);

        TextView heading = new TextView(this);
        heading.setText("作者");
        heading.setTextSize(13);
        heading.setTextColor(Ui.grey(this));
        card.addView(heading);

        card.addView(linkRow(R.drawable.logo_coolapk_red, "酷安", "@LOMOHO", "https://www.coolapk.com/u/514048"), rowLp(12));
        card.addView(linkRow(R.drawable.logo_github_red, "GitHub", "@lomoho", "https://github.com/lomoho66"), rowLp(12));
        card.addView(mailRow(), rowLp(12));

        card.addView(Ui.divider(this));

        TextView thanksHeading = new TextView(this);
        thanksHeading.setText("致谢");
        thanksHeading.setTextSize(13);
        thanksHeading.setTextColor(Ui.grey(this));
        card.addView(thanksHeading);

        card.addView(Ui.infoRow(this, "技术支持", "🥐 Muse by Meta"), rowLp(12));

        root.addView(card);

        // 打赏入口：页面下方居中（从更多页移入）
        LinearLayout donateWrap = new LinearLayout(this);
        donateWrap.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView donate = new TextView(this);
        donate.setText("打赏");
        donate.setTextSize(15);
        donate.setTypeface(Typeface.DEFAULT_BOLD);
        donate.setTextColor(android.graphics.Color.WHITE);
        donate.setGravity(Gravity.CENTER);
        donate.setPadding(Ui.dp(this, 30), Ui.dp(this, 12), Ui.dp(this, 30), Ui.dp(this, 12));
        android.graphics.drawable.GradientDrawable dBg = new android.graphics.drawable.GradientDrawable();
        dBg.setColors(new int[]{android.graphics.Color.rgb(0xFF, 0x5A, 0x4F), android.graphics.Color.rgb(0xE0, 0x2E, 0x24)});
        dBg.setOrientation(android.graphics.drawable.GradientDrawable.Orientation.TL_BR);
        dBg.setCornerRadius(Ui.dp(this, 24));
        donate.setBackground(dBg);
        donate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coffee_white, 0, 0, 0);
        donate.setCompoundDrawablePadding(Ui.dp(this, 7));
        donate.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            startActivity(new Intent(this, DonateActivity.class));
        });
        donateWrap.addView(donate);
        LinearLayout.LayoutParams dwLp = Ui.matchWrap();
        dwLp.topMargin = Ui.dp(this, 30);
        root.addView(donateWrap, dwLp);

        setContentView(Ui.scrollWrap(this, root));
    }

    /** 联系作者行：点一下用 ACTION_SENDTO 拉起邮箱 App 写信页，收件人已预填。 */
    private LinearLayout mailRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO,
                        Uri.parse("mailto:lomoho@duck.com"));
                startActivity(intent);
            } catch (Exception e) {
                android.widget.Toast.makeText(this, "没找到邮箱 App", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        android.widget.ImageView icon = new android.widget.ImageView(this);
        icon.setImageResource(R.drawable.ic_mail_red);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(Ui.dp(this, 24), Ui.dp(this, 24));
        iconLp.rightMargin = Ui.dp(this, 10);
        row.addView(icon, iconLp);
        TextView l = new TextView(this);
        l.setText("联系作者");
        l.setTextSize(16);
        l.setTextColor(Ui.dark(this));
        row.addView(l, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView r = new TextView(this);
        r.setText("lomoho@duck.com");
        r.setTextSize(16);
        r.setTextColor(Ui.RED);
        r.setPaintFlags(r.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        r.setGravity(Gravity.END);
        row.addView(r, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
        return row;
    }

    private LinearLayout.LayoutParams rowLp(int topDp) {
        LinearLayout.LayoutParams lp = Ui.matchWrap();
        lp.topMargin = Ui.dp(this, topDp);
        return lp;
    }

    /** 超链接行：平台图标 + 左侧标签，右侧红色下划线显示名，整行可点，跳浏览器打开 url。 */
    private LinearLayout linkRow(int iconRes, String label, String display, String url) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception ignored) {
            }
        });
        android.widget.ImageView icon = new android.widget.ImageView(this);
        icon.setImageResource(iconRes);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(Ui.dp(this, 24), Ui.dp(this, 24));
        iconLp.rightMargin = Ui.dp(this, 10);
        row.addView(icon, iconLp);
        TextView l = new TextView(this);
        l.setText(label);
        l.setTextSize(16);
        l.setTextColor(Ui.dark(this));
        row.addView(l, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView r = new TextView(this);
        r.setText(display);
        r.setTextSize(16);
        r.setTextColor(Ui.RED);
        r.setPaintFlags(r.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        r.setGravity(Gravity.END);
        row.addView(r, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
        return row;
    }
}
