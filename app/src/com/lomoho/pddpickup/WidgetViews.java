package com.lomoho.pddpickup;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

/**
 * 小组件内容构建：按用户选的样式出图 —
 * 图标版用 emoji（可自选），文字版取快递显示「取」、身份码显示「ID」（文字不可改）。
 * 三个 provider 与设置页的立即刷新都走这里。
 */
public final class WidgetViews {

    private WidgetViews() {
    }

    public static RemoteViews large(Context ctx, int widgetId) {
        RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.widget_pickup);
        boolean icon = "icon".equals(Prefs.widgetMode(ctx));
        views.setTextViewText(R.id.widget_btn_pick,
                (icon ? Prefs.emojiPick(ctx) : "取") + " 取快递");
        views.setTextViewText(R.id.widget_btn_id,
                (icon ? Prefs.emojiId(ctx) : "ID") + " 身份码");
        views.setOnClickPendingIntent(R.id.widget_btn_pick,
                WidgetJump.pending(ctx, widgetId, PddLinks.TARGET_PACKAGES));
        views.setOnClickPendingIntent(R.id.widget_btn_id,
                WidgetJump.pending(ctx, widgetId, PddLinks.TARGET_CODE));
        return views;
    }

    public static RemoteViews small(Context ctx, int widgetId, boolean pick) {
        RemoteViews views = new RemoteViews(ctx.getPackageName(),
                pick ? R.layout.widget_small_pick : R.layout.widget_small_id);
        boolean icon = "icon".equals(Prefs.widgetMode(ctx));
        String glyph = icon
                ? (pick ? Prefs.emojiPick(ctx) : Prefs.emojiId(ctx))
                : (pick ? "取" : "ID");
        views.setTextViewText(R.id.widget_glyph, glyph);
        views.setTextViewTextSize(R.id.widget_glyph, TypedValue.COMPLEX_UNIT_SP, icon ? 30 : 32);
        views.setTextColor(R.id.widget_glyph, pick ? 0xFFFFFFFF : 0xFFE02E24);
        views.setViewVisibility(R.id.widget_label, icon ? View.VISIBLE : View.GONE);
        views.setOnClickPendingIntent(R.id.widget_small_root,
                WidgetJump.pending(ctx, widgetId,
                        pick ? PddLinks.TARGET_PACKAGES : PddLinks.TARGET_CODE));
        return views;
    }

    /** 设置变更后立即刷新全部已添加的组件实例。 */
    public static void refreshAll(Context ctx) {
        AppWidgetManager manager = AppWidgetManager.getInstance(ctx);
        int[] large = manager.getAppWidgetIds(new ComponentName(ctx, PickupWidget.class));
        for (int id : large) {
            manager.updateAppWidget(id, large(ctx, id));
        }
        int[] picks = manager.getAppWidgetIds(new ComponentName(ctx, PickupWidgetSmallPick.class));
        for (int id : picks) {
            manager.updateAppWidget(id, small(ctx, id, true));
        }
        int[] ids = manager.getAppWidgetIds(new ComponentName(ctx, PickupWidgetSmallId.class));
        for (int id : ids) {
            manager.updateAppWidget(id, small(ctx, id, false));
        }
    }
}
