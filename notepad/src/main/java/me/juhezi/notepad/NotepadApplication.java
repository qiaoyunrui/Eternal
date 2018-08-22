package me.juhezi.notepad;

import android.content.Context;
import android.support.annotation.NonNull;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import me.juhezi.eternal.base.BaseApplication;

/**
 * Created by Juhezi[juhezix@163.com] on 2018/8/22.
 */
public class NotepadApplication extends BaseApplication {

    static {    // static 代码段防止内存泄漏
        // 全局设置 Header
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //全局设置主题颜色
//                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
                //指定为经典Header
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
