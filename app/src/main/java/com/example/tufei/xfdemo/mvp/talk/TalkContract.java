package com.example.tufei.xfdemo.mvp.talk;

import com.example.tufei.xfdemo.base.BasePresenter;
import com.example.tufei.xfdemo.base.BaseView;

/**
 * @author tufei
 * @date 2017/11/4
 */

public interface TalkContract {
    interface View extends BaseView{

        void showToast(String tip);
    }

    interface Presenter extends BasePresenter<View>{

        void startAutoTalk();
    }
}
