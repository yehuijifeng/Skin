package com.admin.skin;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * User: LuHao
 * Date: 2019/9/12 19:31
 * Describe: view的属性处理类，采集view和替换资源
 */
public class SkinAttribute {

    //需要换肤的属性集合
    private static final List<String> mAttribute = new ArrayList<>();

    //需要换肤的view
    private List<SkinView> skinViews = new ArrayList<>();

    //需要更换的字体
    private Typeface typeface;

    //以下这些事需要换肤的属性
    static {
        mAttribute.add("background");
        mAttribute.add("src");
        mAttribute.add("textColor");
        mAttribute.add("drawableLeft");
        mAttribute.add("drawableRight");
        mAttribute.add("drawableTop");
        mAttribute.add("drawableBottom");
        mAttribute.add("skinTypeface");
    }

    public SkinAttribute(Typeface typeface) {
        this.typeface = typeface;
    }

    /**
     * 加载view的属性缓存起来
     *
     * @param view         view
     * @param attributeSet 属性
     */
    public void load(View view, AttributeSet attributeSet) {
        List<SkinPain> skinPains = new ArrayList<>();
        //先筛选一遍，需要修改属性的才往下走
        for (int i = 0; i < attributeSet.getAttributeCount(); i++) {
            //获取属性名字
            String attributeName = attributeSet.getAttributeName(i);
            //如果当前属性名字是需要修改的属性则去处理
            if (mAttribute.contains(attributeName)) {
                //拿到属性值，@2130968664
                String attributeValue = attributeSet.getAttributeValue(i);
                //写死的色值，不需要修改
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                //？开头的是系统参数，如下修改
                if (attributeValue.startsWith("?")) {
                    //拿到去掉？后的值。
                    //强转成int，系统编译后的值为int型，即R文件中的id，例如：？123456
                    //系统的资源id下只有一个标签，类似于resource标签下的style标签，但是style下只有一个item标签
                    //所以只拿第一个attrid；
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    //获得资源id
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                } else {
                    //其他正常的标签则直接拿到@color/black中在R文件中的@123456
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                if (resId != 0) {
                    //保存属性名字和对应的id
                    SkinPain skinPain = new SkinPain(attributeName, resId);
                    skinPains.add(skinPain);
                }
            }
        }
        //如果当前view检查出来了需要替换的资源id，则保存起来
        if (!skinPains.isEmpty() || view instanceof TextView) {
            SkinView skinView = new SkinView(view, skinPains);
            skinView.applySkin(typeface);
            skinViews.add(skinView);
        }
    }

    //保存的所有的view进行替换皮肤
    public void applySkin() {
        for (SkinView skinView : skinViews) {
            skinView.applySkin(typeface);
        }
    }

    //设置字体
    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    //保存view与之对应的SkinPain对象
    public class SkinView {
        View view;
        List<SkinPain> skinPains;

        public SkinView(View view, List<SkinPain> skinPains) {
            this.view = view;
            this.skinPains = skinPains;
        }

        //替换皮肤资源
        public void applySkin(Typeface typeface) {
            applyTypeface(typeface);
            for (SkinPain skinPain : skinPains) {
                Drawable left = null, right = null, top = null, bottom = null;
                switch (skinPain.attrubuteName) {
                    case "background"://更换背景色
                        //获得resid的资源
                        Object background = SkinResources.getInstance().getBackground(skinPain.resId);
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPain.resId);
                        if (view instanceof ImageView) {
                            ImageView imageView = ((ImageView) view);
                            if (background instanceof Integer) {
                                imageView.setImageDrawable(new ColorDrawable((Integer) background));
                            } else if (background instanceof Drawable) {
                                imageView.setImageDrawable((Drawable) background);
                            }
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList(skinPain.resId));
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPain.resId);
                        ((TextView) view).setCompoundDrawables(left, top, right, bottom);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPain.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPain.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPain.resId);
                        break;
                    case "skinTypeface":
                        applyTypeface(SkinResources.getInstance().getTypeface(skinPain.resId));
                        break;
                    default:
                        break;
                }
            }
        }

        //替换字体
        private void applyTypeface(Typeface typeface) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }
    }

    public class SkinPain {
        String attrubuteName;//参数名
        int resId;//资源id

        public SkinPain(String attrubuteName, int resId) {
            this.attrubuteName = attrubuteName;
            this.resId = resId;
        }
    }
}
