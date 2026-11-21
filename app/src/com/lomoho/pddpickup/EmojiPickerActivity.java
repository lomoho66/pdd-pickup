package com.lomoho.pddpickup;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 图标自选页：一组常用 emoji 点选即生效（仅图标版小组件用；文字版的「取 / ID」不可改）。 */
public class EmojiPickerActivity extends Activity {

    public static final String EXTRA_PICK = "which_pick";

    private static final String[] EMOJIS = {
            "📦", "🪪", "🆔", "💳", "🎁", "🛍️",
            "📬", "📮", "📥", "📤", "🚚", "🏃",
            "🎫", "🔑", "📛", "👤", "✅", "⭐",
            "❤️", "🔥", "👍", "📍", "🧾", "📇",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean pick = getIntent().getBooleanExtra(EXTRA_PICK, true);

        LinearLayout root = Ui.page(this, pick ? "取快递图标" : "身份码图标");
        TextView hint = new TextView(this);
        hint.setText("点一个，桌面小组件立刻换上");
        hint.setTextSize(13);
        hint.setTextColor(Ui.grey(this));
        LinearLayout.LayoutParams hLp = Ui.matchWrap();
        hLp.topMargin = Ui.dp(this, 16);
        hLp.leftMargin = Ui.dp(this, 4);
        root.addView(hint, hLp);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(6);
        LinearLayout.LayoutParams gLp = Ui.matchWrap();
        gLp.topMargin = Ui.dp(this, 14);
        root.addView(grid, gLp);

        String current = pick ? Prefs.emojiPick(this) : Prefs.emojiId(this);
        int cell = Ui.dp(this, 52);
        for (String e : EMOJIS) {
            TextView t = new TextView(this);
            t.setText(e);
            t.setTextSize(28);
            t.setGravity(Gravity.CENTER);
            if (e.equals(current)) {
                android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
                bg.setColor(Ui.chipTint(this));
                bg.setCornerRadius(Ui.dp(this, 12));
                t.setBackground(bg);
            }
            t.setOnClickListener(v -> {
                Prefs.setEmoji(this, pick, e);
                WidgetViews.refreshAll(this);
                finish();
            });
            grid.addView(t, new GridLayout.LayoutParams(
                    new ViewGroup.LayoutParams(cell, cell)));
        }
        setContentView(Ui.scrollWrap(this, root));
    }
}
