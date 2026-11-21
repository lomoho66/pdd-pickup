package com.lomoho.pddpickup;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/** 1×1 桌面小组件（取快递）：内容按小组件样式设置构建（见 WidgetViews）。 */
public class PickupWidgetSmallPick extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            manager.updateAppWidget(id, WidgetViews.small(context, id, true));
        }
    }
}
