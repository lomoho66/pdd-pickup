package com.lomoho.pddpickup;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 苹果风设计系统：iOS 配色（分组浅灰底 / 白卡 / 系统红点缀 / 系统灰文字），
 * 颜色随系统深色模式切换。圆角 20、发丝线分隔、不用重描边。
 */
public final class Ui {

    /** iOS systemRed */
    public static final int RED = Color.rgb(0xFF, 0x3B, 0x30);
    /** 兼容旧引用：等于浅色模式的分组底色 */
    public static final int BG = Color.rgb(0xF2, 0xF2, 0xF7);
    public static final int DARK = Color.rgb(0x1C, 0x1C, 0x1E);
    public static final int GREY = Color.rgb(0x8E, 0x8E, 0x93);

    private Ui() {
    }

    public static boolean night(Context ctx) {
        String mode = Prefs.themeMode(ctx);
        if ("dark".equals(mode)) {
            return true;
        }
        if ("light".equals(mode)) {
            return false;
        }
        return (ctx.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    /** 品牌红（拼多多红）：浅色 #E02E24，深色 #FF453A */
    public static int pdd(Context ctx) {
        return night(ctx) ? Color.rgb(0xFF, 0x45, 0x3A) : Color.rgb(0xE0, 0x2E, 0x24);
    }

    /** 身份码图标块的中性灰底（网页版同款，不用红色） */
    public static int chipNeutral(Context ctx) {
        return night(ctx) ? Color.rgb(0x2C, 0x2C, 0x2E) : Color.rgb(0xF2, 0xF2, 0xF7);
    }

    /** 设置抽屉里的分组卡底色（比页面卡再深一阶，深色模式更明显） */
    public static int group(Context ctx) {
        return night(ctx) ? Color.rgb(0x2C, 0x2C, 0x2E) : Color.WHITE;
    }

    /** 分段控件底色 / 选中块底色 */
    public static int segBg(Context ctx) {
        return night(ctx) ? Color.argb(61, 118, 118, 128) : Color.argb(31, 118, 118, 128);
    }

    public static int segOn(Context ctx) {
        return night(ctx) ? Color.rgb(0x63, 0x63, 0x66) : Color.WHITE;
    }

    public static int bg(Context ctx) {
        return night(ctx) ? Color.rgb(0x00, 0x00, 0x00) : Color.rgb(0xF2, 0xF2, 0xF7);
    }

    public static int card(Context ctx) {
        return night(ctx) ? Color.rgb(0x1C, 0x1C, 0x1E) : Color.WHITE;
    }

    public static int dark(Context ctx) {
        return night(ctx) ? Color.WHITE : Color.rgb(0x1C, 0x1C, 0x1E);
    }

    public static int grey(Context ctx) {
        return Color.rgb(0x8E, 0x8E, 0x93);
    }

    /** 正文次级文字：浅色模式深灰，深色模式浅灰，两种底上都够对比 */
    public static int secondary(Context ctx) {
        return night(ctx) ? Color.rgb(0xAE, 0xAE, 0xB2) : Color.rgb(0x48, 0x48, 0x4A);
    }

    public static int separator(Context ctx) {
        return night(ctx) ? Color.rgb(0x38, 0x38, 0x3A) : Color.rgb(0xE5, 0xE5, 0xEA);
    }

    /** 图标 chip 的浅红底（身份码用） */
    public static int chipTint(Context ctx) {
        return night(ctx) ? Color.rgb(0x40, 0x26, 0x25) : Color.rgb(0xFF, 0xEC, 0xEB);
    }

    /** 中性胶囊/按钮底（更多按钮、作者徽标） */
    public static int neutralFill(Context ctx) {
        return night(ctx) ? Color.rgb(0x1C, 0x1C, 0x1E) : Color.rgb(0xE9, 0xE9, 0xEE);
    }

    public static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }


    /** 把二级页内容包进可滚动容器：内容超过一屏时可以下滑（page 本身只是纵向布局，不带滚动）。 */
    public static ScrollView scrollWrap(Context ctx, View content) {
        ScrollView sv = new ScrollView(ctx);
        sv.setBackgroundColor(bg(ctx));
        content.setLayoutParams(new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sv.addView(content);
        return sv;
    }


    /** 状态栏高度（px），沉浸式布局时给内容顶部让位用。 */
    public static int statusBarHeight(Context ctx) {
        int id = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return id > 0 ? ctx.getResources().getDimensionPixelSize(id) : 0;
    }

    /**
     * 沉浸式状态栏：状态栏透明、App 背景色延伸到状态栏下面，内容顶部自动让出状态栏高度。
     * 浅色模式用深色状态栏图标，深色模式用浅色图标。
     */
    public static void edgeToEdge(Activity activity) {
        Window w = activity.getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(Color.TRANSPARENT);
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (!night(activity)) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        w.getDecorView().setSystemUiVisibility(flags);
    }

    /** 二级页骨架：顶部「← + 标题」，下面是内容区 root。 */
    public static LinearLayout page(Activity activity, String title) {
        edgeToEdge(activity);
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(activity, 20);
        root.setPadding(pad, dp(activity, 28) + statusBarHeight(activity), pad, pad);
        root.setBackgroundColor(bg(activity));

        LinearLayout head = new LinearLayout(activity);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = new TextView(activity);
        back.setText("‹");
        back.setTextSize(32);
        back.setTextColor(RED);
        back.setPadding(0, 0, dp(activity, 8), dp(activity, 4));
        back.setOnClickListener(v -> activity.finish());
        head.addView(back);

        TextView t = new TextView(activity);
        t.setText(title);
        t.setTextSize(22);
        t.setTextColor(dark(activity));
        t.setTypeface(Typeface.DEFAULT_BOLD);
        head.addView(t);
        root.addView(head);
        return root;
    }

    public static LinearLayout whiteCard(Context ctx) {
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.VERTICAL);
        int cp = dp(ctx, 18);
        card.setPadding(cp, cp, cp, cp);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(card(ctx));
        bg.setCornerRadius(dp(ctx, 20));
        card.setBackground(bg);
        return card;
    }

    public static LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static View divider(Context ctx) {
        View v = new View(ctx);
        v.setBackgroundColor(separator(ctx));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, dp(ctx, 1) / 2));
        lp.topMargin = dp(ctx, 12);
        lp.bottomMargin = dp(ctx, 12);
        v.setLayoutParams(lp);
        return v;
    }

    public static LinearLayout infoRow(Context ctx, String label, String value) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        TextView l = new TextView(ctx);
        l.setText(label);
        l.setTextSize(15);
        l.setTextColor(dark(ctx));
        row.addView(l, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView r = new TextView(ctx);
        r.setText(value);
        r.setTextSize(15);
        r.setTextColor(grey(ctx));
        r.setGravity(Gravity.END);
        row.addView(r, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
        return row;
    }
}
