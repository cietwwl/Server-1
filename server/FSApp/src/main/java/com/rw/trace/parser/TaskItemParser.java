package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rwbase.dao.task.pojo.TaskItem;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.common.enu.eTaskFinishDef;
import com.alibaba.fastjson.JSONObject;

public class TaskItemParser implements DataValueParser<TaskItem> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public TaskItem copy(TaskItem entity) {
        TaskItem taskItemCopy = new TaskItem();
        taskItemCopy.setId(entity.getId());
        taskItemCopy.setUserId(entity.getUserId());
        taskItemCopy.setTaskId(entity.getTaskId());
        taskItemCopy.setFinishType(writer.copyObject(entity.getFinishType()));
        taskItemCopy.setSuperType(entity.getSuperType());
        taskItemCopy.setDrawState(entity.getDrawState());
        taskItemCopy.setCurProgress(entity.getCurProgress());
        taskItemCopy.setTotalProgress(entity.getTotalProgress());
        return taskItemCopy;
    }

    @Override
    public JSONObject recordAndUpdate(TaskItem entity1, TaskItem entity2) {
        JSONObject jsonMap = null;
        String id1 = entity1.getId();
        String id2 = entity2.getId();
        if (!writer.equals(id1, id2)) {
            entity1.setId(id2);
            jsonMap = writer.write(jsonMap, "id", id2);
        }
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        int taskId1 = entity1.getTaskId();
        int taskId2 = entity2.getTaskId();
        if (taskId1 != taskId2) {
            entity1.setTaskId(taskId2);
            jsonMap = writer.write(jsonMap, "taskId", taskId2);
        }
        eTaskFinishDef finishType1 = entity1.getFinishType();
        eTaskFinishDef finishType2 = entity2.getFinishType();
        if (finishType1 != finishType2) {
            entity1.setFinishType(finishType2);
            jsonMap = writer.write(jsonMap, "finishType", finishType2);
        }
        int superType1 = entity1.getSuperType();
        int superType2 = entity2.getSuperType();
        if (superType1 != superType2) {
            entity1.setSuperType(superType2);
            jsonMap = writer.write(jsonMap, "superType", superType2);
        }
        int drawState1 = entity1.getDrawState();
        int drawState2 = entity2.getDrawState();
        if (drawState1 != drawState2) {
            entity1.setDrawState(drawState2);
            jsonMap = writer.write(jsonMap, "drawState", drawState2);
        }
        int curProgress1 = entity1.getCurProgress();
        int curProgress2 = entity2.getCurProgress();
        if (curProgress1 != curProgress2) {
            entity1.setCurProgress(curProgress2);
            jsonMap = writer.write(jsonMap, "curProgress", curProgress2);
        }
        int totalProgress1 = entity1.getTotalProgress();
        int totalProgress2 = entity2.getTotalProgress();
        if (totalProgress1 != totalProgress2) {
            entity1.setTotalProgress(totalProgress2);
            jsonMap = writer.write(jsonMap, "totalProgress", totalProgress2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(TaskItem entity1, TaskItem entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (!writer.equals(entity1.getUserId(), entity2.getUserId())) {
            return true;
        }
        if (entity1.getTaskId() != entity2.getTaskId()) {
            return true;
        }
        if (entity1.getFinishType() != entity2.getFinishType()) {
            return true;
        }
        if (entity1.getSuperType() != entity2.getSuperType()) {
            return true;
        }
        if (entity1.getDrawState() != entity2.getDrawState()) {
            return true;
        }
        if (entity1.getCurProgress() != entity2.getCurProgress()) {
            return true;
        }
        if (entity1.getTotalProgress() != entity2.getTotalProgress()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(TaskItem entity) {
        JSONObject json = new JSONObject(11);
        json.put("id", entity.getId());
        json.put("userId", entity.getUserId());
        json.put("taskId", entity.getTaskId());
        Object finishTypeJson = writer.toJSON(entity.getFinishType());
        if (finishTypeJson != null) {
            json.put("finishType", finishTypeJson);
        }
        json.put("superType", entity.getSuperType());
        json.put("drawState", entity.getDrawState());
        json.put("curProgress", entity.getCurProgress());
        json.put("totalProgress", entity.getTotalProgress());
        return json;
    }

}