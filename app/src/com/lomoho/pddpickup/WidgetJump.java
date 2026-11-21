package com.lomoho.pddpickup;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/** 小组件共用：生成跳板 PendingIntent，按组件实例 + 入口区分，避免互相覆盖。 */
public final class WidgetJump {

    private WidgetJump() {
    }

    public static PendingIntent pending(Context context, int widgetId, String target) {
        Intent intent = new Intent(context, JumpActivity.class);
        intent.putExtra(JumpActivity.EXTRA_TARGET, target);
        intent.setAction("com.lomoho.pddpickup.JUMP." + target + "." + widgetId);
        int requestCode = widgetId * 10 + (PddLinks.TARGET_CODE.equals(target) ? 2 : 1);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getActivity(context, requestCode, intent, flags);
    }
}
