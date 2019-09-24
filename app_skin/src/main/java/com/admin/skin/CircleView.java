package com.admin.skin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * User: LuHao
 * Date: 2019/9/23 20:20
 * Describe:
 */
public class CircleView extends View implements SkinViewSupport {
    private int colorResId;
    private Paint mTextPain;

    public CircleView(Context context) {
        super(context, null);
    }
    //构造方法
    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //拿到自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        colorResId = typedArray.getColor(R.styleable.CircleView_circleTextColor, Color.RED);
        typedArray.recycle();
        //画一个圆
        mTextPain = new Paint();
        //设置颜色
        mTextPain.setColor(getResources().getColor(colorResId));
        //抗锯齿
        mTextPain.setAntiAlias(true);
        //文本相对于原点中见
        mTextPain.setTextAlign(Paint.Align.CENTER);
    }

    //实现接口
    @Override
    public void applySkin() {
        if (colorResId != 0) {
            int color = SkinResources.getInstance().getColor(colorResId);
            mTextPain.setColor(color);
            //更新view
            invalidate();
        }
    }
}
