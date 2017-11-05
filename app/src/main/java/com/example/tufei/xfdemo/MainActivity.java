package com.example.tufei.xfdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tufei.xfdemo.bean.RecognizerData;
import com.example.tufei.xfdemo.bean.UnderStanderData;
import com.example.tufei.xfdemo.utils.ToastUtil;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private TextView tvRecognizer;
    private TextView tvUnderstander;

    private Button btnTalk;
    private Button btnUnderstander;

    private SharedPreferences mSharedPreferences;

    // 语音听写UI
    private RecognizerDialog mIatDialog;


    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;


    // 语音合成对象
    private SpeechSynthesizer mTts;

//    // 语义理解对象（语音到语义）。
//    private SpeechUnderstander mSpeechUnderstander;

    // 语义理解对象（文本到语义）。
    private TextUnderstander  mTextUnderstander;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();



        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(this, mInitListener);

        // 清空参数
        mIatDialog.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIatDialog.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIatDialog.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //设置普通话
        mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin");



        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this, null);

//        // 云端发音人名称列表
//        mCloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
//        mCloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);
//        // 初始化对象
//        mSpeechUnderstander = SpeechUnderstander.createUnderstander(MainActivity.this, null);

        //初始化文本到语义对象
        mTextUnderstander = TextUnderstander.createTextUnderstander(MainActivity.this,null );


    }

    //初始化界面UI
    private void init() {
        tvRecognizer = findViewById(R.id.tv_recognizer);
        tvUnderstander = findViewById(R.id.tv_understander);
        btnTalk = findViewById(R.id.Btn_talk);
        btnUnderstander= (Button) findViewById(R.id.btn_understander);

        tvRecognizer.setOnClickListener(MainActivity.this);
        tvUnderstander.setOnClickListener(MainActivity.this);
        btnTalk.setOnClickListener(MainActivity.this);
        btnUnderstander.setOnClickListener(MainActivity.this);
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                ToastUtil.showToast("初始化失败，错误码：" + code);
            }
        }
    };

    int ret = 0;// 函数调用返回值

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //语音听写
            case R.id.Btn_talk:
                //开始听写
                mIatDialog.setListener(mRecognizerDialogListener);
                mIatDialog.show();
                break;


            //语义理解
            case R.id.btn_understander:
                tvUnderstander.setText("");

                String text = tvRecognizer.getText().toString();

                if (TextUtils.isEmpty(text)) {
                    ToastUtil.showToast("抱歉，你说什么了吗，听不到");
                }else{
                    if(mTextUnderstander.isUnderstanding()){
                        mTextUnderstander.cancel();
                        ToastUtil.showToast("取消");
                    }else {
                        // 设置语义情景
                        //mTextUnderstander.setParameter(SpeechConstant.SCENE, "main");
                        ret = mTextUnderstander.understandText(text, mTextUnderstanderListener);
                        if(ret != 0)
                        {
                            ToastUtil.showToast("语义理解失败,错误码:"+ ret);
                        }
                    }
                }

                break;


            case R.id.tv_recognizer:

                //语音合成
                if (tvRecognizer.isClickable() && !TextUtils.isEmpty(tvRecognizer.getText().toString())) {
                    mTts.startSpeaking(tvRecognizer.getText().toString(), mTtsListener);
                } else {
                    ToastUtil.showToast("无内容");
                }
                break;


            case R.id.tv_understander:

                //语音合成
                if (tvUnderstander.isClickable() && !TextUtils.isEmpty(tvUnderstander.getText().toString())) {
                    mTts.startSpeaking(tvUnderstander.getText().toString(), mTtsListener);
                } else {
                    ToastUtil.showToast("无内容");
                }
                break;
        }

    }


    /**
     * 语音听写监听
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            if (!isLast) {
                if (!TextUtils.isEmpty(recognizerResult.getResultString())) {
                    parseRecognizerResult(recognizerResult.getResultString());
                }

            }
        }

        @Override
        public void onError(SpeechError speechError) {

            //错误
            ToastUtil.showToast(speechError.getErrorDescription());
        }
    };


    /**
     * 语音合成监听
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {


        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPo) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null) {
                ToastUtil.showToast("播放完成");
            } else if (speechError != null) {
                ToastUtil.showToast(speechError.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };




    /**
     * 文本语义监听
     */
    public static final String errorTip = "请确认是否有在 aiui.xfyun.cn 配置语义。（另外，已开通语义，但从1115（含1115）以前的SDK更新到1116以上版本SDK后，语义需要重新到 aiui.xfyun.cn 配置）";

    private TextUnderstanderListener mTextUnderstanderListener = new TextUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            if (null != result) {
                // 获取结果json
                String json = result.getResultString();
                if (!TextUtils.isEmpty(json)) {
                    //解析
                    String text=parseUnderstanderResult(json);
                    //显示
                    tvUnderstander.setText(text);

                    Log.v(TAG, "语义理解结果json："+json);
                    if( 0 != getResultError(json) ){
                        ToastUtil.showToast(errorTip);
                    }
                }
            } else {
                Log.d(TAG, "understander result:null");
                ToastUtil.showToast("识别结果不正确");
            }
        }

        @Override
        public void onError(SpeechError error) {
            // 文本语义不能使用回调错误码14002，请确认您下载sdk时是否勾选语义场景和私有语义的发布
            // 请到 aiui.xfyun.cn 配置语义，从1115前的SDK更新到1116以上版本SDK后，语义需要重新到 aiui.xfyun.cn 配置
            ToastUtil.showToast("onError Code："	+ error.getErrorCode()+", "+errorTip);
        }
    };




    private int getResultError(final String resultText) {
        int error = 0;
        try {
            final String KEY_ERROR = "error";
            final String KEY_CODE = "code";
            final JSONObject joResult = new JSONObject(resultText);
            final JSONObject joError = joResult.optJSONObject(KEY_ERROR);
            if (null != joError) {
                error = joError.optInt(KEY_CODE);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }//end of try-catch

        return error;
    }

    /**
     * 解析语音听写返回的json
     */
    private void parseRecognizerResult(String recognizerResult) {

        String speechText;
        StringBuffer str = new StringBuffer();
        Gson gson = new Gson();
        RecognizerData data = gson.fromJson(recognizerResult, RecognizerData.class);
        List<RecognizerData.WsBean> ws = data.getWs();
        for (RecognizerData.WsBean w : ws) {
            str.append(w.getCw().get(0).getW());
        }

        speechText = str.toString();

        tvRecognizer.setText(speechText);
        Log.v(TAG, "返回结果：" + speechText);
    }

    /**
     * 解析语义理解返回的json
     * @param json
     * @return
     */
    private String parseUnderstanderResult(String json) {
        Gson gson = new Gson();
        UnderStanderData data = gson.fromJson(json, UnderStanderData.class);
        String text=data.getAnswer().getText();

        return text;
    }

    /**
     * 语义理解参数设置
     *
     * @return
     */
//    public void setParam() {
//        String lang = mSharedPreferences.getString("understander_language_preference", "mandarin");
//        if (lang.equals("en_us")) {
//            // 设置语言
//            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
//            mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, null);
//        } else {
//            // 设置语言
//            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//            // 设置语言区域
//            mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lang);
//        }
//        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
//        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("understander_vadbos_preference", "4000"));
//
//        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("understander_vadeos_preference", "1000"));
//
//        // 设置标点符号，默认：1（有标点）
//        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("understander_punc_preference", "1"));
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/sud.wav");
//
//        // 设置语义情景
//        //mSpeechUnderstander.setParameter(SpeechConstant.SCENE, "main");
//    }

}
