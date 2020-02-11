package com.changgou.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.canal.listener
 * @version 1.0
 * @date 2020/1/2
 */
@CanalEventListener//标识该类 用来监听数据的变化
public class MyEventListener {

  /*  @InsertListenPoint//当发生新增的操作的触发该方法的执行
    public void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //do something...
    }*/

   /* @UpdateListenPoint
    public void onEvent1(CanalEntry.RowData rowData) {

        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            System.out.println(column.getName()+":"+column.getValue());
        }
        System.out.println("====================================");
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println(column.getName()+":"+column.getValue());
        }

    }*/
/*
    @DeleteListenPoint
    public void onEvent3(CanalEntry.EventType eventType) {
        //do something...
    }
*/
    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content"}, eventType = CanalEntry.EventType.UPDATE)
    public void onEvent4(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            System.out.println(column.getName()+":"+column.getValue());
        }
        System.out.println("====================================");
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println(column.getName()+":"+column.getValue());
        }
    }
}
