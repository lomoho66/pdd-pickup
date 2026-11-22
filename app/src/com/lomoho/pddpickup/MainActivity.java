package com.lomoho.pddpickup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 主页：
 * 大标题（PDD 红 + 紧字距）、左上作者徽标、右上明暗切换、
 * 列表式入口卡（取快递红渐变图标块 / 身份码中性灰图标块）、底部「更多」入口。
 * 交互：点按震动反馈、弹簧式按压缩放、开场一次淡入上浮。
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.edgeToEdge(this);
        boolean night = Ui.night(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = Ui.dp(this, 22);
        root.setPadding(pad, Ui.dp(this, 30) + Ui.statusBarHeight(this), pad, pad);

        // 顶行：作者徽标（点进关于） + 明暗切换
        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        TextView pill = new TextView(this);
        pill.setText("@lomoho 🦉");
        pill.setTextSize(12);
        pill.setTextColor(Ui.grey(this));
        int ph = Ui.dp(this, 12), pv = Ui.dp(this, 5);
        pill.setPadding(ph, pv, ph, pv);
        GradientDrawable pillBg = new GradientDrawable();
        pillBg.setColor(Ui.neutralFill(this));
        pillBg.setCornerRadius(Ui.dp(this, 20));
        pill.setBackground(pillBg);
        pill.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
        head.addView(pill, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View spacer = new View(this);
        head.addView(spacer, new LinearLayout.LayoutParams(0, 1, 1));

        // 明暗按钮：自动挡显示字母 A，浅色太阳、深色月亮
        android.widget.FrameLayout modeBtn = new android.widget.FrameLayout(this);
        GradientDrawable modeBg = new GradientDrawable();
        modeBg.setColor(Ui.card(this));
        modeBg.setShape(GradientDrawable.OVAL);
        modeBtn.setBackground(modeBg);
        String themeMode = Prefs.themeMode(this);
        TextView aTxt = new TextView(this);
        aTxt.setText("A");
        aTxt.setTextSize(15);
        // 细体，和太阳/月亮的线性图标画风统一（粗体 A 太壮了）
        aTxt.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        aTxt.setTextColor(Ui.grey(this));
        aTxt.setGravity(Gravity.CENTER);
        aTxt.setVisibility("auto".equals(themeMode) ? View.VISIBLE : View.GONE);
        modeBtn.addView(aTxt, new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView modeIcon = new ImageView(this);
        modeIcon.setImageResource(night ? R.drawable.ic_moon : R.drawable.ic_sun);
        int mp = Ui.dp(this, 10);
        modeIcon.setPadding(mp, mp, mp, mp);
        modeIcon.setVisibility("auto".equals(themeMode) ? View.GONE : View.VISIBLE);
        modeBtn.addView(modeIcon, new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        modeBtn.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            // 三档循环：自动 → 浅色 → 深色 → 自动（默认自动跟随系统）
            String cur = Prefs.themeMode(this);
            String next = "auto".equals(cur) ? "light" : ("light".equals(cur) ? "dark" : "auto");
            Prefs.setThemeMode(this, next);
            android.widget.Toast.makeText(this,
                    "外观：" + ("auto".equals(next) ? "自动" : ("light".equals(next) ? "浅色" : "深色")),
                    android.widget.Toast.LENGTH_SHORT).show();
            recreate();
        });
        head.addView(modeBtn, new LinearLayout.LayoutParams(Ui.dp(this, 40), Ui.dp(this, 40)));
        root.addView(head);

        // 大标题：PDD 品牌红，其余主色，紧字距
        TextView title = new TextView(this);
        SpannableString titleText = new SpannableString("PDD取件");
        titleText.setSpan(new ForegroundColorSpan(Ui.pdd(this)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(titleText);
        title.setTextSize(44);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Ui.dark(this));
        title.setLetterSpacing(-0.02f);
        LinearLayout.LayoutParams tLp = Ui.matchWrap();
        tLp.topMargin = Ui.dp(this, 34);
        root.addView(title, tLp);

        TextView sub = new TextView(this);
        sub.setText("不用扫码，一触即达");
        sub.setTextSize(16);
        sub.setTextColor(Ui.grey(this));
        LinearLayout.LayoutParams sLp = Ui.matchWrap();
        sLp.topMargin = Ui.dp(this, 8);
        root.addView(sub, sLp);

        // 入口卡
        LinearLayout pick = entryCard(true, "取快递", "驿站取件凭证");
        pick.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            PddLinks.open(this, PddLinks.TARGET_PACKAGES);
        });
        LinearLayout.LayoutParams pLp = Ui.matchWrap();
        pLp.topMargin = Ui.dp(this, 30);
        root.addView(pick, pLp);

        LinearLayout id = entryCard(false, "身份码", "身份核验专用");
        id.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            PddLinks.open(this, PddLinks.TARGET_CODE);
        });
        LinearLayout.LayoutParams iLp = Ui.matchWrap();
        iLp.topMargin = Ui.dp(this, 12);
        root.addView(id, iLp);

        TextView tip = new TextView(this);
        tip.setText("添加桌面小组件/长按 APP 图标\n均可呼出快捷入口");
        tip.setLineSpacing(0, 1.3f);
        tip.setTextSize(12);
        tip.setTextColor(Ui.grey(this));
        tip.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tipLp = Ui.matchWrap();
        tipLp.topMargin = Ui.dp(this, 18);
        root.addView(tip, tipLp);

        // 底部：文字版「更多」按钮（进独立更多页）
        LinearLayout moreWrap = new LinearLayout(this);
        moreWrap.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView more = new TextView(this);
        more.setText("更多 ›");
        more.setTextSize(15);
        more.setTextColor(Ui.dark(this));
        more.setGravity(Gravity.CENTER);
        more.setPadding(Ui.dp(this, 26), Ui.dp(this, 11), Ui.dp(this, 26), Ui.dp(this, 11));
        GradientDrawable moreBg = new GradientDrawable();
        moreBg.setColor(Ui.card(this));
        moreBg.setCornerRadius(Ui.dp(this, 22));
        if (night) {
            moreBg.setStroke(Math.max(1, Ui.dp(this, 1) / 2), Color.argb(20, 255, 255, 255));
        }
        more.setBackground(moreBg);
        more.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            startActivity(new Intent(this, SettingsActivity.class));
        });
        attachPressScale(more);
        moreWrap.addView(more);
        LinearLayout.LayoutParams mLp = Ui.matchWrap();
        mLp.topMargin = Ui.dp(this, 26);
        root.addView(moreWrap, mLp);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.addView(root);
        // 背景纯色
        scroll.setBackgroundColor(Ui.bg(this));
        setContentView(scroll);

        entrance(head, title, sub, pick, id, tip, moreWrap);
    }

    /** 开场动画：依次淡入上浮，只播一次。 */
    private void entrance(View... views) {
        for (int i = 0; i < views.length; i++) {
            View v = views[i];
            v.setAlpha(0f);
            v.setTranslationY(Ui.dp(this, 16));
            v.animate().alpha(1f).translationY(0f)
                    .setStartDelay(i * 80L).setDuration(550)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    /** 列表式入口卡：白卡 24 圆角；取快递红渐变图标块，身份码中性灰图标块；真箭头图标。 */
    private LinearLayout entryCard(boolean primary, String titleText, String descText) {
        boolean night = Ui.night(this);
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        int cp = Ui.dp(this, 18);
        card.setPadding(cp, Ui.dp(this, 16), cp, Ui.dp(this, 16));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Ui.card(this));
        bg.setCornerRadius(Ui.dp(this, 24));
        if (night) {
            bg.setStroke(Math.max(1, Ui.dp(this, 1) / 2), Color.argb(20, 255, 255, 255));
        }
        card.setBackground(bg);

        ImageView icon = new ImageView(this);
        int ip = Ui.dp(this, 12);
        icon.setPadding(ip, ip, ip, ip);
        GradientDrawable chipBg = new GradientDrawable();
        if (primary) {
            icon.setImageResource(R.drawable.ic_package_white);
            chipBg.setColors(new int[]{Color.rgb(0xFF, 0x5A, 0x4F), Color.rgb(0xE0, 0x2E, 0x24)});
            chipBg.setOrientation(GradientDrawable.Orientation.TL_BR);
        } else {
            icon.setImageResource(night ? R.drawable.ic_id_white : R.drawable.ic_id_dark);
            chipBg.setColor(Ui.chipNeutral(this));
        }
        chipBg.setCornerRadius(Ui.dp(this, 14));
        icon.setBackground(chipBg);
        card.addView(icon, new LinearLayout.LayoutParams(Ui.dp(this, 50), Ui.dp(this, 50)));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams txLp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        txLp.leftMargin = Ui.dp(this, 14);
        texts.setLayoutParams(txLp);
        TextView t = new TextView(this);
        t.setText(titleText);
        t.setTextSize(18);
        t.setTextColor(Ui.dark(this));
        texts.addView(t);
        TextView d = new TextView(this);
        d.setText(descText);
        d.setTextSize(13);
        d.setTextColor(Ui.grey(this));
        LinearLayout.LayoutParams dLp = Ui.matchWrap();
        dLp.topMargin = Ui.dp(this, 2);
        texts.addView(d, dLp);
        card.addView(texts);

        ImageView arrow = new ImageView(this);
        arrow.setImageResource(night ? R.drawable.ic_chev_dark : R.drawable.ic_chev);
        card.addView(arrow, new LinearLayout.LayoutParams(Ui.dp(this, 9), Ui.dp(this, 15)));

        attachPressScale(card);
        return card;
    }

    /** 弹簧式按压反馈：按下缩到 0.965 带回弹，松开弹回。 */
    private void attachPressScale(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.965f).scaleY(0.965f).setDuration(160)
                            .setInterpolator(new OvershootInterpolator(1.5f)).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(280)
                            .setInterpolator(new OvershootInterpolator(1.5f)).start();
                    break;
                default:
                    break;
            }
            return false;
        });
    }
}
