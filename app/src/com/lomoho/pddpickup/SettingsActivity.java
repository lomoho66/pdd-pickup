package com.lomoho.pddpickup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 「更多」页：
 * 顶部信息卡（版本 + 介绍），下面依次是小组件、应用图标、关于。
 */
public class SettingsActivity extends Activity {

    private interface SegListener {
        void onSelect(int idx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = Ui.page(this, "更多");

        // 信息卡：版本 + 介绍
        LinearLayout info = Ui.whiteCard(this);
        TextView ver = new TextView(this);
        String v = "2.0";
        try {
            v = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ignored) {
        }
        ver.setText("PDD取件  " + v);
        ver.setTextSize(15);
        ver.setTextColor(Ui.dark(this));
        info.addView(ver);
        TextView intro = new TextView(this);
        intro.setText("去驿站取件，不用再打开拼多多扫码一层层翻找。软件不联网、不要任何权限，只是帮你把拼多多打开到对的那一页。");
        intro.setTextSize(14);
        intro.setTextColor(Ui.secondary(this));
        intro.setLineSpacing(0, 1.5f);
        LinearLayout.LayoutParams inLp = Ui.matchWrap();
        inLp.topMargin = Ui.dp(this, 8);
        info.addView(intro, inLp);
        LinearLayout.LayoutParams infoLp = Ui.matchWrap();
        infoLp.topMargin = Ui.dp(this, 20);
        root.addView(info, infoLp);

        // 小组件
        root.addView(groupLabel("小组件"));
        LinearLayout g2 = group();
        boolean iconMode = "icon".equals(Prefs.widgetMode(this));
        LinearLayout emojiRows = new LinearLayout(this);
        emojiRows.setOrientation(LinearLayout.VERTICAL);
        g2.addView(settingRow(
                seg(new String[]{"图标", "文字"}, iconMode ? 0 : 1, idx -> {
                    Prefs.setWidgetMode(this, idx == 0 ? "icon" : "text");
                    WidgetViews.refreshAll(this);
                    emojiRows.setVisibility(idx == 0 ? View.VISIBLE : View.GONE);
                }),
                "样式", "文字版固定显示「取 / ID」"));
        emojiRows.addView(hairline());
        emojiRows.addView(settingRow(emojiRight(true), "取快递图标", null, () -> {
            Intent it = new Intent(this, EmojiPickerActivity.class);
            it.putExtra(EmojiPickerActivity.EXTRA_PICK, true);
            startActivity(it);
        }));
        emojiRows.addView(hairline());
        emojiRows.addView(settingRow(emojiRight(false), "身份码图标", null, () -> {
            Intent it = new Intent(this, EmojiPickerActivity.class);
            it.putExtra(EmojiPickerActivity.EXTRA_PICK, false);
            startActivity(it);
        }));
        emojiRows.setVisibility(iconMode ? View.VISIBLE : View.GONE);
        g2.addView(emojiRows);
        root.addView(g2);

        // 应用图标：二选一
        root.addView(groupLabel("应用图标"));
        LinearLayout g4 = group();
        checkClassic = new TextView(this);
        checkMidnight = new TextView(this);
        g4.addView(iconRow(R.mipmap.ic_classic, "经典红", "红底白盒", true));
        g4.addView(hairline());
        g4.addView(iconRow(R.mipmap.ic_midnight, "午夜黑", "黑底红盒", false));
        root.addView(g4);
        refreshIconChecks();

        // 关于类目：只有作者信息（打赏入口已移进作者信息页下方）
        root.addView(groupLabel("关于"));
        LinearLayout g3 = group();
        g3.addView(menuRow(R.drawable.ic_info_red, "作者信息", "酷安、GitHub 与联系方式",
                () -> startActivity(new Intent(this, AboutActivity.class))));
        root.addView(g3);

        setContentView(Ui.scrollWrap(this, root));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从 emoji 选择页回来后，图标可能已变 — 简单起见整页重建以刷新右侧预览
        // （仅当本页已创建过且不在首次显示时）
        if (resumedOnce) {
            recreate();
        }
        resumedOnce = true;
    }

    private boolean resumedOnce = false;
    private TextView checkClassic;
    private TextView checkMidnight;

    private void refreshIconChecks() {
        boolean midnight = "midnight".equals(Prefs.iconChoice(this));
        styleCheck(checkClassic, !midnight);
        styleCheck(checkMidnight, midnight);
    }

    private void styleCheck(TextView c, boolean on) {
        if (c == null) {
            return;
        }
        c.setText(on ? "✓" : "");
        c.setTextSize(17);
        c.setTypeface(Typeface.DEFAULT_BOLD);
        c.setTextColor(Ui.RED);
    }

    /** 图标选项行：预览图 + 名称 + 说明 + 选中勾，整行可点。 */
    private LinearLayout iconRow(int iconRes, String name, String desc, boolean classic) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(Ui.dp(this, 14), Ui.dp(this, 11), Ui.dp(this, 14), Ui.dp(this, 11));
        row.setMinimumHeight(Ui.dp(this, 52));

        ImageView preview = new ImageView(this);
        preview.setImageResource(iconRes);
        preview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GradientDrawable clip = new GradientDrawable();
        clip.setColor(android.graphics.Color.TRANSPARENT);
        clip.setCornerRadius(Ui.dp(this, 10));
        preview.setBackground(clip);
        preview.setClipToOutline(true);
        row.addView(preview, new LinearLayout.LayoutParams(Ui.dp(this, 40), Ui.dp(this, 40)));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams txLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        txLp.leftMargin = Ui.dp(this, 12);
        texts.setLayoutParams(txLp);
        TextView t = new TextView(this);
        t.setText(name);
        t.setTextSize(16);
        t.setTextColor(Ui.dark(this));
        texts.addView(t);
        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextSize(12);
        d.setTextColor(Ui.grey(this));
        texts.addView(d);
        row.addView(texts);

        row.addView(classic ? checkClassic : checkMidnight);
        row.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            Prefs.setIconChoice(this, classic ? "classic" : "midnight");
            refreshIconChecks();
        });
        return row;
    }

    // ---------- 小部件 ----------

    private TextView groupLabel(String text) {
        TextView l = new TextView(this);
        l.setText(text);
        l.setTextSize(13);
        l.setTextColor(Ui.grey(this));
        LinearLayout.LayoutParams lp = Ui.matchWrap();
        lp.topMargin = Ui.dp(this, 20);
        lp.leftMargin = Ui.dp(this, 16);
        lp.bottomMargin = Ui.dp(this, 6);
        l.setLayoutParams(lp);
        return l;
    }

    private LinearLayout group() {
        LinearLayout g = new LinearLayout(this);
        g.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Ui.group(this));
        bg.setCornerRadius(Ui.dp(this, 14));
        g.setBackground(bg);
        g.setLayoutParams(Ui.matchWrap());
        return g;
    }

    private LinearLayout settingRow(View right, String label, String desc) {
        return settingRow(right, label, desc, null);
    }

    private LinearLayout settingRow(View right, String label, String desc, Runnable click) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(Ui.dp(this, 14), Ui.dp(this, 11), Ui.dp(this, 14), Ui.dp(this, 11));
        row.setMinimumHeight(Ui.dp(this, 52));
        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextSize(16);
        t.setTextColor(Ui.dark(this));
        texts.addView(t);
        if (desc != null) {
            TextView d = new TextView(this);
            d.setText(desc);
            d.setTextSize(12);
            d.setTextColor(Ui.grey(this));
            texts.addView(d);
        }
        row.addView(texts);
        if (right != null) {
            row.addView(right);
        }
        if (click != null) {
            row.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                click.run();
            });
        }
        return row;
    }

    private View hairline() {
        View v = new View(this);
        v.setBackgroundColor(Ui.separator(this));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, Ui.dp(this, 1) / 2));
        lp.leftMargin = Ui.dp(this, 14);
        v.setLayoutParams(lp);
        return v;
    }

    private LinearLayout seg(String[] labels, int selected, SegListener listener) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setPadding(Ui.dp(this, 2), Ui.dp(this, 2), Ui.dp(this, 2), Ui.dp(this, 2));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Ui.segBg(this));
        bg.setCornerRadius(Ui.dp(this, 9));
        box.setBackground(bg);
        TextView[] btns = new TextView[labels.length];
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            TextView b = new TextView(this);
            b.setText(labels[i]);
            b.setTextSize(13);
            b.setGravity(Gravity.CENTER);
            b.setPadding(Ui.dp(this, 10), Ui.dp(this, 5), Ui.dp(this, 10), Ui.dp(this, 5));
            b.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                styleSeg(btns, idx);
                listener.onSelect(idx);
            });
            btns[i] = b;
            box.addView(b);
        }
        styleSeg(btns, selected);
        return box;
    }

    private void styleSeg(TextView[] btns, int selected) {
        for (int i = 0; i < btns.length; i++) {
            TextView b = btns[i];
            b.setTextColor(Ui.dark(this));
            b.setTypeface(i == selected ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            if (i == selected) {
                GradientDrawable on = new GradientDrawable();
                on.setColor(Ui.segOn(this));
                on.setCornerRadius(Ui.dp(this, 7));
                b.setBackground(on);
            } else {
                b.setBackground(null);
            }
        }
    }

    private LinearLayout emojiRight(boolean pick) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setGravity(Gravity.CENTER_VERTICAL);
        TextView e = new TextView(this);
        e.setText(pick ? Prefs.emojiPick(this) : Prefs.emojiId(this));
        // 同选择页：显式不透明黑，避免主题默认色透明度让彩色 emoji 发灰
        e.setTextColor(0xFF000000);
        e.setTextSize(20);
        box.addView(e);
        ImageView arrow = new ImageView(this);
        arrow.setImageResource(Ui.night(this) ? R.drawable.ic_chev_dark : R.drawable.ic_chev);
        LinearLayout.LayoutParams aLp = new LinearLayout.LayoutParams(Ui.dp(this, 9), Ui.dp(this, 15));
        aLp.leftMargin = Ui.dp(this, 8);
        box.addView(arrow, aLp);
        return box;
    }

        /** 类目内的子菜单行：红色浅底图标块 + 标题 + 说明 + 箭头，整行可点。 */
    private LinearLayout menuRow(int iconRes, String titleText, String descText, Runnable click) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(Ui.dp(this, 14), Ui.dp(this, 11), Ui.dp(this, 14), Ui.dp(this, 11));
        row.setMinimumHeight(Ui.dp(this, 52));

        ImageView icon = new ImageView(this);
        int ip = Ui.dp(this, 7);
        icon.setPadding(ip, ip, ip, ip);
        GradientDrawable chipBg = new GradientDrawable();
        chipBg.setColor(Ui.chipTint(this));
        chipBg.setCornerRadius(Ui.dp(this, 8));
        icon.setBackground(chipBg);
        icon.setImageResource(iconRes);
        row.addView(icon, new LinearLayout.LayoutParams(Ui.dp(this, 30), Ui.dp(this, 30)));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams txLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        txLp.leftMargin = Ui.dp(this, 12);
        texts.setLayoutParams(txLp);
        TextView t = new TextView(this);
        t.setText(titleText);
        t.setTextSize(16);
        t.setTextColor(Ui.dark(this));
        texts.addView(t);
        TextView d = new TextView(this);
        d.setText(descText);
        d.setTextSize(12);
        d.setTextColor(Ui.grey(this));
        texts.addView(d);
        row.addView(texts);

        ImageView arrow = new ImageView(this);
        arrow.setImageResource(Ui.night(this) ? R.drawable.ic_chev_dark : R.drawable.ic_chev);
        row.addView(arrow, new LinearLayout.LayoutParams(Ui.dp(this, 9), Ui.dp(this, 15)));

        row.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            click.run();
        });
        return row;
    }
}
