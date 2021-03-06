package com.example.econonew.presenter;

import android.util.Log;

import com.android.volley.VolleyError;
import com.example.econonew.db.DBHelperFactory;
import com.example.econonew.entity.MsgItemEntity;
import com.example.econonew.resource.Constant;
import com.example.econonew.resource.msg.MainMessage;
import com.example.econonew.server.NetClient;
import com.example.econonew.server.URLManager;
import com.example.econonew.server.json.JsonCast;
import com.example.econonew.server.json.ResponseJsonHelper;
import com.example.econonew.view.activity.BaseActivity;
import com.example.econonew.view.activity.FinanceApplication;

import org.json.JSONObject;

import java.util.List;

/**
 * 主界面有关消息的Presenter，处理有关消息的逻辑
 * Created by mengfei on 2016/10/23.
 */

public class MsgPresenter extends BasePresenter<BaseActivity> {

    private static final String TAG = "MsgPresenter";


    public MsgPresenter(BaseActivity activity) {
        super(activity);
    }

    public void refreshPublicData() {
        new Thread() {
            public void run() {
                String url = URLManager.getConnectURL();
                NetClient.OnResultListener listener = new NetClient.OnResultListener() {

                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "onSuccess: " + response);
                        JSONObject map = JsonCast.getJsonObject(response);
                        new ResponseJsonHelper().handleInformation(map);
                    }

                    public void onError(VolleyError error) {
                        loadDatasFromDatabase();
                    }
                };
                NetClient.getInstance().executeGetForString(FinanceApplication.getInstance(), url, listener);
            }
        }.start();
    }

    //从数据库里面加载数据
    private void loadDatasFromDatabase() {
        for(String tabName : Constant.publicItemNames) {
            MainMessage message = MainMessage.getInstance(tabName);
            List<MsgItemEntity> list = DBHelperFactory.getDBHelper().queryAllItems(MsgItemEntity.class);
            Log.e(TAG, "loadDatasFromDatabase: " + tabName + " " + list.size());
            if (message != null) {
                message.setMessage(list, false, false);
            }
        }
    }
}
