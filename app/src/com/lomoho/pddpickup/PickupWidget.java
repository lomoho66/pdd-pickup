package com.lomoho.pddpickup;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/** 2×2 桌面小组件：双入口，内容按小组件样式设置构建（见 WidgetViews）。 */
public class PickupWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            manager.updateAppWidget(id, WidgetViews.large(context, id));
        }
    }
}
