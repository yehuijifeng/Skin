package com.admin.skin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * User: LuHao
 * Date: 2019/9/10 19:49
 * Describe:布局换肤的工厂类，用于采集需要换肤的view
 */
public class SkinLayoutFactory implements LayoutInflater.Factory2, Observer {
    private Activity activity;
    //系统原生view的路径，属于这些路径的才可以换肤，减少消耗和判断
    private static final String[] mClassPrefixList = {
            "android.webkit.",
            "android.widget.",
            "android.view.",
    };

    //获取view的class的构造方法的参数
    private static final Class[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};

    //缓存已经通过反射得到某个view的构造函数，例如textview、button
    private static final HashMap<String, Constructor<? extends View>> mConstructorCache = new HashMap<>();

    //view属性处理类
    private SkinAttribute skinAttribute;

    //初始化的时候去创建SkinAttribute类
    public SkinLayoutFactory(Activity activity, Typeface typeface) {
        this.activity = activity;
        this.skinAttribute = new SkinAttribute(typeface);
    }

    //在创建view的时候去采集view，这里一个layout.xml文件中的所有view标签都会在创建的时候进入该方法
    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String s, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        //如果是系统的view，则可以通过全类名得到view
        View view = createViewFromTag(s, context, attributeSet);
        //如果通过全类名拿不到view，则说明当前view是自定义view
        //如果是自定义view则调用createview方法
        if (view == null) {
            view = createView(s, context, attributeSet);
        }
        //将当前view的所有参数遍历，拿到符合换肤的参数以及对应的resid
        //第一步，采集view，在这里已经完成
        skinAttribute.load(view, attributeSet);
        return view;
    }

    /**
     * 创建原生view
     *
     * @param name         标签名。例如：TextView;Button
     * @param context      上下文
     * @param attributeSet 标签参数
     * @return
     */
    private View createViewFromTag(String name, Context context, AttributeSet attributeSet) {
        //检查当前view是否是自定义的，自定义的view才会带.
        if (name.contains(".")) {
            return null;
        } else {
            //获取原生view
            View view = null;
            //循环保存的原生标签
            for (int i = 0; i < mClassPrefixList.length; i++) {
                //如果是原生标签，则去创建，获取到全类名
                view = createView(mClassPrefixList[i] + name, context, attributeSet);
                if (view != null) {//通过全类名拿到了view，直接返回出去
                    break;
                }
            }
            return view;
        }
    }

    /**
     * 创建一个view
     *
     * @param name         全类名
     * @param context      上下文
     * @param attributeSet 标签名
     * @return
     */
    private View createView(String name, Context context, AttributeSet attributeSet) {
        //添加缓存，一个xml中如果有多个重复的view，例如多个textview或者button，则缓存的作用就体现出来了
        //只要是相同的view，则不需要每次都去通过反射拿view
        Constructor<? extends View> constructor = mConstructorCache.get(name);
        //没有缓存则创建
        if (constructor == null) {
            try {
                //通过全类名拿到class对象
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                //获取到当前class对象中的构造方法
                constructor = aClass.getConstructor(mConstructorSignature);
                mConstructorCache.put(name, constructor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != constructor) {
            try {
                //这个操作相当于new 一个对象，new的时候传入构造方法的参数
                return constructor.newInstance(context, attributeSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String s, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        return null;
    }

    //通知观察者，在这里接收到了消息
    @Override
    public void update(Observable o, Object arg) {
        SkinThemeUtils.updateStatusBarColor(activity);
        Typeface typeface=SkinThemeUtils.getSkinTypeface(activity);
        skinAttribute.setTypeface(typeface);
        //更换皮肤
        skinAttribute.applySkin();
    }
}
