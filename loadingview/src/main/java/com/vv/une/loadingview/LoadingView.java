package com.vv.une.loadingview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

/**
 * Created by VV on 2016/9/2.
 * 状态视图管理
 */
public class LoadingView extends View {

    private int mEmptyResId;
    private int mRetryResId;
    private int mLoadingResId;
    private RelativeLayout.LayoutParams mLayoutParams;
    private View mEmptyView;
    private View mLoadingView;
    private View mRetryView;
    private RetryClickListener mListener;
    private LayoutInflater mInflater;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义资源视图
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        mEmptyResId = a.getResourceId(R.styleable.LoadingView_emptyResId, 0);
        mRetryResId = a.getResourceId(R.styleable.LoadingView_retryResId, 0);
        mLoadingResId = a.getResourceId(R.styleable.LoadingView_loadingResId, 0);
        a.recycle();
        //空数据视图
        if (mEmptyResId == 0) {
            mEmptyResId = R.layout.base_loading_empty;
        }
        //重试视图
        if (mRetryResId == 0) {
            mRetryResId = R.layout.base_loading_retry;
        }
        //加载中视图
        if (mLoadingResId == 0) {
            mLoadingResId = R.layout.base_loading_loading;
        }
        //根布局参数
        if (attrs == null) {
            mLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            mLayoutParams = new RelativeLayout.LayoutParams(context, attrs);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @Override
    public void setVisibility(int visibility) {
        setVisibility(mEmptyView, visibility);
        setVisibility(mRetryView, visibility);
        setVisibility(mLoadingView, visibility);
    }

    private void setVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    /**
     * 注入到activity中
     *
     * @param activity Activity
     * @return StateView
     */
    public static LoadingView inject(@NonNull Activity activity) {
        return inject(activity, false);
    }

    /**
     * 注入到activity中
     *
     * @param activity Activity
     * @param hasActionBar 是否有actionbar/toolbar,
     *                     true: 会setMargin top, margin大小是状态栏高度 + 工具栏高度
     *                     false: not set
     * @return StateView
     */
    public static LoadingView inject(@NonNull Activity activity, boolean hasActionBar) {
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        return inject(rootView, hasActionBar, true);
    }

    /**
     * 注入到ViewGroup中
     *
     * @param parent extends ViewGroup
     * @return StateView
     */
    public static LoadingView inject(@NonNull ViewGroup parent) {
        return inject(parent, false);
    }

    /**
     * 注入到ViewGroup中
     *
     * @param parent extends ViewGroup
     * @param hasActionBar 是否有actionbar/toolbar,
     *                     true: 会setMargin top, margin大小是actionbarSize
     *                     false: not set
     * @return StateView
     */
    public static LoadingView inject(@NonNull ViewGroup parent, boolean hasActionBar) {
        return inject(parent, hasActionBar, false);
    }

    /**
     * 注入到ViewGroup中
     *
     * @param parent extends ViewGroup
     * @param hasActionBar 是否有actionbar/toolbar
     * @param isActivity 是否注入到Activity
     * @return StateView
     */
    private static LoadingView inject(@NonNull ViewGroup parent, boolean hasActionBar, boolean isActivity) {
        LoadingView stateView = new LoadingView(parent.getContext());
        parent.addView(stateView);
        if (hasActionBar) {
            stateView.setTopMargin(isActivity);
        }
        return stateView;
    }

    /**
     * 注入到View中
     *
     * @param view instanceof ViewGroup
     * @return StateView
     */
    public static LoadingView inject(@NonNull View view) {
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            return inject(parent);
        } else {
            throw new ClassCastException("view must be ViewGroup");
        }
    }

    /**
     * 注入到View中
     *
     * @param view instanceof ViewGroup
     * @param hasActionBar 是否有actionbar/toolbar
     * @return StateView
     */
    public static LoadingView inject(@NonNull View view, boolean hasActionBar) {
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            return inject(parent, hasActionBar);
        } else {
            throw new ClassCastException("view must be ViewGroup");
        }
    }


    /**
     * 视图控制显示
     *
     * @param view
     */
    private void show(View view) {
        setVisibility(view, VISIBLE);
        if (mEmptyView == view) {
            setVisibility(mLoadingView, GONE);
            setVisibility(mRetryView, GONE);
        } else if (mLoadingView == view) {
            setVisibility(mEmptyView, GONE);
            setVisibility(mRetryView, GONE);
        } else {
            setVisibility(mEmptyView, GONE);
            setVisibility(mLoadingView, GONE);
        }
    }

    /**
     * @return 状态栏的高度
     */
    private int getStatusBarHeight() {
        int height = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            height = getResources().getDimensionPixelSize(resId);
        }
        return height;
    }

    /**
     * @return actionBarSize
     */
    private int getActionBarHeight() {
        int height = 0;
        TypedValue tv = new TypedValue();
        if (getContext().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return height;
    }


    /**
     * 注入重试点击监听
     *
     * @param listener
     */
    public void setRetryOnClickListener(RetryClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置自定义空数据视图
     *
     * @param emptyResId
     */
    public void setEmptyResId(@LayoutRes int emptyResId) {
        this.mEmptyResId = emptyResId;
    }

    /**
     * 设置重试自定义视图
     *
     * @param retryResId
     */
    public void setRetryResId(@LayoutRes int retryResId) {
        this.mRetryResId = retryResId;
    }

    /**
     * 设置加载中自定义视图
     *
     * @param loadingResId
     */
    public void setLoadingResId(@LayoutRes int loadingResId) {
        this.mLoadingResId = loadingResId;
    }

    /**
     * 获取布局填充器
     *
     * @return
     */
    public LayoutInflater getInflater() {
        return mInflater;
    }

    /**
     * 设置布局填充器
     *
     * @param inflater
     */
    public void setInflater(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    /**
     * 设置topMargin, 当有actionbar/toolbar的时候
     * @param isActivity if true: 注入到Activity, 需要加上状态栏的高度
     */
    public void setTopMargin(boolean isActivity){
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        layoutParams.topMargin = isActivity ?
                getStatusBarHeight() + getActionBarHeight() : getActionBarHeight();
    }

    public View inflate(@LayoutRes int layoutResource) {
        final ViewParent viewParent = getParent();

        if (viewParent != null && viewParent instanceof ViewGroup) {
            if (layoutResource != 0) {
                final ViewGroup parent = (ViewGroup) viewParent;
                final LayoutInflater factory;
                if (mInflater != null) {
                    factory = mInflater;
                } else {
                    factory = LayoutInflater.from(getContext());
                }
                final View view = factory.inflate(layoutResource, parent, false);

                final int index = parent.indexOfChild(this);
                //防止还能触摸底下的View
                view.setClickable(true);

                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
                if (layoutParams != null) {
                    if (parent instanceof RelativeLayout) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) layoutParams;
                        mLayoutParams.setMargins(lp.leftMargin, lp.topMargin,
                                lp.rightMargin, lp.bottomMargin);

                        parent.addView(view, index, mLayoutParams);
                    }else {
                        parent.addView(view, index, layoutParams);
                    }
                } else {
                    parent.addView(view, index);
                }

                if (mLoadingView != null && mRetryView != null && mEmptyView != null){
                    parent.removeViewInLayout(this);
                }

                return view;
            } else {
                throw new IllegalArgumentException("StateView must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("StateView must have a non-null ViewGroup viewParent");
        }
    }

    //--------------------------视图控制------------------------------

    /**
     * 显示成功视图,即内容视图
     */
    public void showContent() {
        setVisibility(GONE);
    }

    /**
     * 显示空数据视图
     */
    public View showEmpty() {
        if (mEmptyView == null) {
            mEmptyView = inflate(mEmptyResId);
        }
        show(mEmptyView);
        return mEmptyView;
    }

    /**
     * 显示加载中视图
     */
    public View showLoading() {
        if (mLoadingView == null) {
            mLoadingView = inflate(mLoadingResId);
        }
        show(mLoadingView);
        return mLoadingView;
    }

    /**
     * 显示重试视图
     * TODO 设置重试提示语
     */
    public View showRetry() {
        if (mRetryView == null) {
            mRetryView = inflate(mRetryResId);
            mRetryView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        showLoading();
                        //为了保证回调有效,延迟200ms
                        mRetryView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mListener.onRetyrClick();
                            }
                        }, 200);
                    }
                }
            });
        }
        show(mRetryView);
        return mRetryView;
    }


    public interface RetryClickListener {
        void onRetyrClick();
    }

}
