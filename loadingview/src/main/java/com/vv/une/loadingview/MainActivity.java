package com.vv.une.loadingview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mLoadingView = LoadingView.inject(this);
        //如果有toolbar或者actionbar 用下面这个注入方法
        mLoadingView = LoadingView.inject(this, true);
        mLoadingView.showLoading();
        mLoadingView.setRetryOnClickListener(new LoadingView.RetryClickListener() {
            @Override
            public void onRetyrClick() {
//                mLoadingView.showLoading();
                mLoadingView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingView.showContent();
                    }
                }, 3000);
            }
        });
        mLoadingView.postDelayed(new Runnable() {
            @Override
            public void run() {
//               mLoadingView.showContent();
                mLoadingView.showRetry();
            }
        }, 5000);
    }
}
