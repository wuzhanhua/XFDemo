package com.example.tufei.xfdemo.mvp.talk;

/**
 * @author wzh
 * @date 2017/11/4
 */
public class TalkPresenter implements TalkContract.Presenter {
    public TalkPresenter(TalkContract.View mView) {
        this.mView = mView;
    }

    private TalkContract.View mView;

    @Override
    public void attachView(TalkContract.View view) {
        mView = view;
    }


    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void startAutoTalk() {
        //...
        if(mView != null){
            mView.showToast("获得结果");
        }

    }
}
