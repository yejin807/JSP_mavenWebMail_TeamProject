/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import java.util.ArrayList;
import javax.mail.Message;

/**
 *
 * @author gleyd
 */
public abstract class MessageAgent {

    private boolean NeedUpdate = false;
    private ArrayList<Integer> msgIdList = new ArrayList<Integer>();

    protected boolean isNeedUpdate() {
        return NeedUpdate;
    }

    protected void setNeedUpdate(boolean status) {
        this.NeedUpdate = status;
    }

    protected ArrayList<Integer> getMsgIdList() {
        return msgIdList;
    }

    protected void addMsgId(int msgid) {
        msgIdList.add(msgid);
    }

    protected void removeMsgId(int msgid) {
        msgIdList.remove(Integer.valueOf(msgid));
    }

    protected void updateMsgId(int index, int newMsgId) {
        msgIdList.set(index, newMsgId);
    }

    protected int getMsgIdSize() {
        return msgIdList.size();
    }

    protected int getMsgIdValue(int index) {
        return msgIdList.get(index);
    }

    protected void resetMsgIdList() {
        msgIdList = null;
        msgIdList = new ArrayList<Integer>();
    }

    protected abstract boolean setMsgIdList();

    public abstract ArrayList<Message> getMessageList(Message[] messages);
    //public abstract void getMessageList(Message[] messages);

    protected abstract ArrayList<Message> filter(Message[] messages, ArrayList<Integer> msgIdList);

    protected abstract boolean insertMsgId(int msgid);

    protected abstract boolean deleteMsgId(int msgid);

    public abstract boolean addMessage(int msgid);

    public abstract boolean removeMessage(int msgid);

    public abstract void updateMsgId(int msgid);

}
