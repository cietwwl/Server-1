package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rwbase.dao.email.EmailItem;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class EmailItemParser implements DataValueParser<EmailItem> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public EmailItem copy(EmailItem entity) {
        EmailItem emailItemCopy = new EmailItem();
        emailItemCopy.setEmailId(entity.getEmailId());
        emailItemCopy.setChecked(entity.isChecked());
        emailItemCopy.setReceive(entity.isReceive());
        emailItemCopy.setEmailAttachment(entity.getEmailAttachment());
        emailItemCopy.setDeleteType(entity.getDeleteType());
        emailItemCopy.setDeadlineTimeInMill(entity.getDeadlineTimeInMill());
        emailItemCopy.setSendTime(entity.getSendTime());
        emailItemCopy.setTitle(entity.getTitle());
        emailItemCopy.setContent(entity.getContent());
        emailItemCopy.setSender(entity.getSender());
        emailItemCopy.setCheckIcon(entity.getCheckIcon());
        emailItemCopy.setSubjectIcon(entity.getSubjectIcon());
        emailItemCopy.setTaskId(entity.getTaskId());
        emailItemCopy.setCoolTime(entity.getCoolTime());
        emailItemCopy.setBeginTime(entity.getBeginTime());
        emailItemCopy.setEndTime(entity.getEndTime());
        emailItemCopy.setCfgid(entity.getCfgid());
        return emailItemCopy;
    }

    @Override
    public JSONObject recordAndUpdate(EmailItem entity1, EmailItem entity2) {
        JSONObject jsonMap = null;
        String emailId1 = entity1.getEmailId();
        String emailId2 = entity2.getEmailId();
        if (!writer.equals(emailId1, emailId2)) {
            entity1.setEmailId(emailId2);
            jsonMap = writer.write(jsonMap, "emailId", emailId2);
        }
        boolean checked1 = entity1.isChecked();
        boolean checked2 = entity2.isChecked();
        if (checked1 != checked2) {
            entity1.setChecked(checked2);
            jsonMap = writer.write(jsonMap, "checked", checked2);
        }
        boolean receive1 = entity1.isReceive();
        boolean receive2 = entity2.isReceive();
        if (receive1 != receive2) {
            entity1.setReceive(receive2);
            jsonMap = writer.write(jsonMap, "receive", receive2);
        }
        String emailAttachment1 = entity1.getEmailAttachment();
        String emailAttachment2 = entity2.getEmailAttachment();
        if (!writer.equals(emailAttachment1, emailAttachment2)) {
            entity1.setEmailAttachment(emailAttachment2);
            jsonMap = writer.write(jsonMap, "emailAttachment", emailAttachment2);
        }
        int deleteType1 = entity1.getDeleteType();
        int deleteType2 = entity2.getDeleteType();
        if (deleteType1 != deleteType2) {
            entity1.setDeleteType(deleteType2);
            jsonMap = writer.write(jsonMap, "deleteType", deleteType2);
        }
        long deadlineTimeInMill1 = entity1.getDeadlineTimeInMill();
        long deadlineTimeInMill2 = entity2.getDeadlineTimeInMill();
        if (deadlineTimeInMill1 != deadlineTimeInMill2) {
            entity1.setDeadlineTimeInMill(deadlineTimeInMill2);
            jsonMap = writer.write(jsonMap, "deadlineTimeInMill", deadlineTimeInMill2);
        }
        long sendTime1 = entity1.getSendTime();
        long sendTime2 = entity2.getSendTime();
        if (sendTime1 != sendTime2) {
            entity1.setSendTime(sendTime2);
            jsonMap = writer.write(jsonMap, "sendTime", sendTime2);
        }
        String title1 = entity1.getTitle();
        String title2 = entity2.getTitle();
        if (!writer.equals(title1, title2)) {
            entity1.setTitle(title2);
            jsonMap = writer.write(jsonMap, "title", title2);
        }
        String content1 = entity1.getContent();
        String content2 = entity2.getContent();
        if (!writer.equals(content1, content2)) {
            entity1.setContent(content2);
            jsonMap = writer.write(jsonMap, "content", content2);
        }
        String sender1 = entity1.getSender();
        String sender2 = entity2.getSender();
        if (!writer.equals(sender1, sender2)) {
            entity1.setSender(sender2);
            jsonMap = writer.write(jsonMap, "sender", sender2);
        }
        String checkIcon1 = entity1.getCheckIcon();
        String checkIcon2 = entity2.getCheckIcon();
        if (!writer.equals(checkIcon1, checkIcon2)) {
            entity1.setCheckIcon(checkIcon2);
            jsonMap = writer.write(jsonMap, "checkIcon", checkIcon2);
        }
        String subjectIcon1 = entity1.getSubjectIcon();
        String subjectIcon2 = entity2.getSubjectIcon();
        if (!writer.equals(subjectIcon1, subjectIcon2)) {
            entity1.setSubjectIcon(subjectIcon2);
            jsonMap = writer.write(jsonMap, "subjectIcon", subjectIcon2);
        }
        long taskId1 = entity1.getTaskId();
        long taskId2 = entity2.getTaskId();
        if (taskId1 != taskId2) {
            entity1.setTaskId(taskId2);
            jsonMap = writer.write(jsonMap, "taskId", taskId2);
        }
        long coolTime1 = entity1.getCoolTime();
        long coolTime2 = entity2.getCoolTime();
        if (coolTime1 != coolTime2) {
            entity1.setCoolTime(coolTime2);
            jsonMap = writer.write(jsonMap, "coolTime", coolTime2);
        }
        long beginTime1 = entity1.getBeginTime();
        long beginTime2 = entity2.getBeginTime();
        if (beginTime1 != beginTime2) {
            entity1.setBeginTime(beginTime2);
            jsonMap = writer.write(jsonMap, "beginTime", beginTime2);
        }
        long endTime1 = entity1.getEndTime();
        long endTime2 = entity2.getEndTime();
        if (endTime1 != endTime2) {
            entity1.setEndTime(endTime2);
            jsonMap = writer.write(jsonMap, "endTime", endTime2);
        }
        String cfgid1 = entity1.getCfgid();
        String cfgid2 = entity2.getCfgid();
        if (!writer.equals(cfgid1, cfgid2)) {
            entity1.setCfgid(cfgid2);
            jsonMap = writer.write(jsonMap, "cfgid", cfgid2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(EmailItem entity1, EmailItem entity2) {
        if (!writer.equals(entity1.getEmailId(), entity2.getEmailId())) {
            return true;
        }
        if (entity1.isChecked() != entity2.isChecked()) {
            return true;
        }
        if (entity1.isReceive() != entity2.isReceive()) {
            return true;
        }
        if (!writer.equals(entity1.getEmailAttachment(), entity2.getEmailAttachment())) {
            return true;
        }
        if (entity1.getDeleteType() != entity2.getDeleteType()) {
            return true;
        }
        if (entity1.getDeadlineTimeInMill() != entity2.getDeadlineTimeInMill()) {
            return true;
        }
        if (entity1.getSendTime() != entity2.getSendTime()) {
            return true;
        }
        if (!writer.equals(entity1.getTitle(), entity2.getTitle())) {
            return true;
        }
        if (!writer.equals(entity1.getContent(), entity2.getContent())) {
            return true;
        }
        if (!writer.equals(entity1.getSender(), entity2.getSender())) {
            return true;
        }
        if (!writer.equals(entity1.getCheckIcon(), entity2.getCheckIcon())) {
            return true;
        }
        if (!writer.equals(entity1.getSubjectIcon(), entity2.getSubjectIcon())) {
            return true;
        }
        if (entity1.getTaskId() != entity2.getTaskId()) {
            return true;
        }
        if (entity1.getCoolTime() != entity2.getCoolTime()) {
            return true;
        }
        if (entity1.getBeginTime() != entity2.getBeginTime()) {
            return true;
        }
        if (entity1.getEndTime() != entity2.getEndTime()) {
            return true;
        }
        if (!writer.equals(entity1.getCfgid(), entity2.getCfgid())) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(EmailItem entity) {
        JSONObject json = new JSONObject(23);
        json.put("emailId", entity.getEmailId());
        json.put("checked", entity.isChecked());
        json.put("receive", entity.isReceive());
        json.put("emailAttachment", entity.getEmailAttachment());
        json.put("deleteType", entity.getDeleteType());
        json.put("deadlineTimeInMill", entity.getDeadlineTimeInMill());
        json.put("sendTime", entity.getSendTime());
        json.put("title", entity.getTitle());
        json.put("content", entity.getContent());
        json.put("sender", entity.getSender());
        json.put("checkIcon", entity.getCheckIcon());
        json.put("subjectIcon", entity.getSubjectIcon());
        json.put("taskId", entity.getTaskId());
        json.put("coolTime", entity.getCoolTime());
        json.put("beginTime", entity.getBeginTime());
        json.put("endTime", entity.getEndTime());
        json.put("cfgid", entity.getCfgid());
        return json;
    }

}