package com.lomoho.pddpickup;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/** 桌面小组件：点任意位置直达拼多多取件码页。 */
public class PickupWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_pickup);
            views.setOnClickPendingIntent(R.id.widget_root, jumpIntent(context, id));
            manager.updateAppWidget(id, views);
        }
    }

    private PendingIntent jumpIntent(Context context, int widgetId) {
        Intent intent = new Intent(context, JumpActivity.class);
        intent.putExtra(JumpActivity.EXTRA_TARGET, PddLinks.TARGET_PACKAGES);
        intent.setAction("com.lomoho.pddpickup.JUMP." + widgetId);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getActivity(context, widgetId, intent, flags);
    }
}
