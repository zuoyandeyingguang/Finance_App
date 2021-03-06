package com.example.econonew.resource.msg;

import com.example.econonew.db.DBHelperFactory;
import com.example.econonew.entity.ChannelEntity;
import com.example.econonew.view.fragment.ChannelMessageFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户自定义频道的消息管理类
 * Created by mengfei on 2016/10/2.
 */

public class ChannelMessage implements IMessage<ChannelEntity> {

    private static Map<String, ChannelMessage> msgManager = new HashMap<>();

    private String allMsgName;
    private ChannelMessageFragment mFragment;

    private List<ChannelEntity> mChannelList; // 自定义频道的列表

    /**
     * 定义消息管理的类
     *
     * @param allMsgName 每一栏消息的名称，可以通过消息的名称获得具体消息的实例
     */
    public ChannelMessage(ChannelMessageFragment fragment, String allMsgName) {
        this.allMsgName = allMsgName;
        this.mFragment = fragment;
        mChannelList = new ArrayList<>();
        msgManager.put(allMsgName, this);
    }

    public static ChannelMessage getInstance(String name) {
        if (msgManager.containsKey(name)) {
            return msgManager.get(name);
        } else {
            return null;
        }
    }


    /**
     * 通知Fragment停止刷新操作
     */
    public void stopFresh() {
        mFragment.stopFresh();
    }

    /**
     * 获得频道的名称
     */
    public String getName() {
        return allMsgName;
    }


    @Override
    public boolean removeMsg(ChannelEntity item) {
        boolean isRemove = false;
        if (this.mChannelList.contains(item)) {
            DBHelperFactory.getDBHelper().deleteItemById(ChannelEntity.class, item.getId());
            isRemove = this.mChannelList.remove(item);
            sentMsgToUiAndVoice();
        }
        return isRemove;
    }

    /**
     * 向UI线程和Voice发送消息
     */
    private void sentMsgToUiAndVoice() {
        this.mFragment.setList(mChannelList);
    }

    @Override
    public void setMessage(List<ChannelEntity> msgList, boolean isAddEnd, boolean isSaveToDatabase) {
        if (!isAddEnd) {
            mChannelList.clear();
        }
        if (msgList != null) {
            mChannelList.addAll(msgList);
        }
        sentMsgToUiAndVoice();
        if (isSaveToDatabase) {
            DBHelperFactory.getDBHelper().insertAllItems(ChannelEntity.class, msgList);
//            dataBaseManager.insertItems(table, msgList);
        }
    }

    @Override
    public List<ChannelEntity> getMessage() {
        return mChannelList;
    }

    @Override
    public int getMsgCount() {
        return getMessage().size();
    }

}
