package com.admin.skin;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * User: LuHao
 * Date: 2019/9/12 19:46
 * Describe:获取系统资源的值
 */
public class SkinThemeUtils {

    /**根据参数的值拿到参数的资源id
     * @param context
     * @param attrs 参数值
     * @return
     */
    public static int[] getResId(Context context, int[] attrs) {
        int[] ints = new int[attrs.length];
        //获得样式属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        for (int i = 0; i < typedArray.length(); i++) {
            ints[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        return ints;
    }
}
