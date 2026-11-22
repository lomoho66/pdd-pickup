package com.lomoho.pddpickup;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

/** 轻量设置存储：外观模式、小组件样式（文字/图标 + 自定义 emoji）。零权限，存应用私有目录。 */
public final class Prefs {

    private static final String FILE = "ui_prefs";

    private Prefs() {
    }

    private static SharedPreferences sp(Context ctx) {
        return ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    /** 外观：auto / light / dark */
    public static String themeMode(Context ctx) {
        return sp(ctx).getString("theme_mode", "auto");
    }

    public static void setThemeMode(Context ctx, String v) {
        sp(ctx).edit().putString("theme_mode", v).apply();
    }

    /** 小组件样式：icon 图标版 / text 文字版 */
    public static String widgetMode(Context ctx) {
        return sp(ctx).getString("widget_mode", "icon");
    }

    public static void setWidgetMode(Context ctx, String v) {
        sp(ctx).edit().putString("widget_mode", v).apply();
    }

    public static String emojiPick(Context ctx) {
        return sp(ctx).getString("emoji_pick", "📦");
    }

    public static String emojiId(Context ctx) {
        return sp(ctx).getString("emoji_id", "🪪");
    }

    public static void setEmoji(Context ctx, boolean pick, String emoji) {
        sp(ctx).edit().putString(pick ? "emoji_pick" : "emoji_id", emoji).apply();
    }

    /** 桌面图标：classic 经典红 / midnight 午夜黑（activity-alias 切换） */
    public static String iconChoice(Context ctx) {
        return sp(ctx).getString("icon_choice", "classic");
    }

    public static void setIconChoice(Context ctx, String v) {
        sp(ctx).edit().putString("icon_choice", v).apply();
        PackageManager pm = ctx.getPackageManager();
        String pkg = ctx.getPackageName();
        ComponentName classic = new ComponentName(pkg, pkg + ".IconClassic");
        ComponentName midnight = new ComponentName(pkg, pkg + ".IconMidnight");
        boolean useMidnight = "midnight".equals(v);
        // 先启用新别名再停用旧的，避免瞬间无桌面图标
        pm.setComponentEnabledSetting(useMidnight ? midnight : classic,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(useMidnight ? classic : midnight,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
