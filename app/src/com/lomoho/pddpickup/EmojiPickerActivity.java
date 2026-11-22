package com.lomoho.pddpickup;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 图标自选页：全部 emoji 点选即生效（仅图标版小组件用；文字版的「取 / ID」不可改）。 */
public class EmojiPickerActivity extends Activity {

    public static final String EXTRA_PICK = "which_pick";

    private String current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean pick = getIntent().getBooleanExtra(EXTRA_PICK, true);
        current = pick ? Prefs.emojiPick(this) : Prefs.emojiId(this);

        LinearLayout root = Ui.page(this, pick ? "取快递图标" : "身份码图标");
        TextView hint = new TextView(this);
        hint.setText("点一个，桌面小组件立刻换上");
        hint.setTextSize(13);
        hint.setTextColor(Ui.grey(this));
        LinearLayout.LayoutParams hLp = Ui.matchWrap();
        hLp.topMargin = Ui.dp(this, 16);
        hLp.leftMargin = Ui.dp(this, 4);
        root.addView(hint, hLp);

        GridView grid = new GridView(this);
        grid.setNumColumns(6);
        grid.setVerticalSpacing(Ui.dp(this, 4));
        grid.setHorizontalSpacing(Ui.dp(this, 4));
        grid.setAdapter(new EmojiAdapter());
        grid.setOnItemClickListener((parent, view, position, id) -> {
            Prefs.setEmoji(this, pick, Emojis.ALL[position]);
            WidgetViews.refreshAll(this);
            finish();
        });
        LinearLayout.LayoutParams gLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        gLp.topMargin = Ui.dp(this, 14);
        root.addView(grid, gLp);
        setContentView(root);

        int pos = -1;
        for (int i = 0; i < Emojis.ALL.length; i++) {
            if (Emojis.ALL[i].equals(current)) {
                pos = i;
                break;
            }
        }
        if (pos >= 0) {
            final int p = pos;
            grid.post(() -> grid.setSelection(p));
        }
    }

    private class EmojiAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Emojis.ALL.length;
        }

        @Override
        public Object getItem(int position) {
            return Emojis.ALL[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView t;
            if (convertView instanceof TextView) {
                t = (TextView) convertView;
            } else {
                t = new TextView(EmojiPickerActivity.this);
                int cell = Ui.dp(EmojiPickerActivity.this, 52);
                t.setLayoutParams(new GridView.LayoutParams(cell, cell));
                t.setTextSize(28);
                t.setGravity(Gravity.CENTER);
                // 必须给不透明黑：主题默认文字色带透明度（87% 黑），彩色 emoji 会跟着发灰发淡
                t.setTextColor(0xFF000000);
            }
            String e = Emojis.ALL[position];
            t.setText(e);
            if (e.equals(current)) {
                GradientDrawable bg = new GradientDrawable();
                bg.setColor(Ui.chipTint(EmojiPickerActivity.this));
                bg.setCornerRadius(Ui.dp(EmojiPickerActivity.this, 12));
                t.setBackground(bg);
            } else {
                t.setBackground(null);
            }
            return t;
        }
    }
}
