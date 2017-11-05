package com.example.tufei.xfdemo.mvp.talk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.tufei.xfdemo.R;

public class TalkActivity extends AppCompatActivity implements TalkContract.View{

    private TalkContract.Presenter mTalkPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        initView();
        initData();
    }

    private void initView() {

    }

    private void initData() {
        mTalkPresenter = new TalkPresenter(this);
        mTalkPresenter.attachView(this);
        mTalkPresenter.startAutoTalk();
    }

    @Override
    public void showToast(String tip) {

    }

    @Override
    protected void onDestroy() {
        mTalkPresenter.detachView();
        super.onDestroy();
    }
}
