package com.lomoho.pddpickup;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * 拼多多深链跳转工具（自研实现）。
 * 深链地址为拼多多 App 对外暴露的标准 scheme，属于公开的互操作接口。
 */
public final class PddLinks {

    /** 拼多多包名 */
    public static final String PKG = "com.xunmeng.pinduoduo";

    /** 身份码页（ID_CODE 标签：跟人走的身份码，不是单件包裹的取快递） */
    public static final String URI_CODE =
            "pinduoduo://com.xunmeng.pinduoduo/mdkd/package?tab=ID_CODE&entry_source=11";

    /** 取快递页（待取件包裹列表；必须显式带 tab=PACKAGE，否则从身份码页切不回来——拼多多对已打开的页面不重置标签，2026-11-22 实测验证） */
    public static final String URI_PACKAGES =
            "pinduoduo://com.xunmeng.pinduoduo/mdkd/package?tab=PACKAGE&entry_source=11";

    /** 网页兜底（App 未装时用浏览器打开） */
    public static final String URI_WEB =
            "https://m.pinduoduo.net/mdkd/package?entry_source=18";

    public static final String TARGET_CODE = "code";
    public static final String TARGET_PACKAGES = "packages";

    private PddLinks() {
    }

    public static String uriFor(String target) {
        return TARGET_PACKAGES.equals(target) ? URI_PACKAGES : URI_CODE;
    }

    /** 依次尝试：深链直达 → 拉起拼多多首页 → 网页兜底 → 提示未安装。 */
    public static void open(Context ctx, String target) {
        String uri = uriFor(target);
        // 1) 深链直达目标页
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            i.setPackage(PKG);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return;
        } catch (ActivityNotFoundException | SecurityException ignored) {
        }
        // 2) 不带包名再试一次（个别 ROM 对 setPackage 有限制）
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return;
        } catch (ActivityNotFoundException | SecurityException ignored) {
        }
        // 3) 至少把拼多多打开
        try {
            Intent launch = ctx.getPackageManager().getLaunchIntentForPackage(PKG);
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(launch);
                return;
            }
        } catch (Exception ignored) {
        }
        // 4) 网页兜底
        try {
            Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(URI_WEB));
            web.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(web);
            return;
        } catch (ActivityNotFoundException | SecurityException ignored) {
        }
        Toast.makeText(ctx, "未检测到拼多多 App", Toast.LENGTH_LONG).show();
    }
}
