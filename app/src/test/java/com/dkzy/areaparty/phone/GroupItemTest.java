package com.dkzy.areaparty.phone;

import android.content.SyncStatusObserver;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import protocol.Data.GroupData;

import static org.junit.Assert.assertEquals;

public class GroupItemTest {
    @Test
    public void ListTest() throws Exception {
        List<GroupData.GroupItem> groups = new ArrayList<GroupData.GroupItem>();
        GroupData.GroupItem.Builder ggb = GroupData.GroupItem.newBuilder();
        ggb.setCreaterUserId("1");
        ggb.setGroupId("1");
        ggb.setGroupName("1");
        ggb.addMemberUserId("1");
        groups.add(ggb.build());
        GroupData.GroupItem.Builder ggb2 = GroupData.GroupItem.newBuilder();
        ggb2.setCreaterUserId("2");
        ggb2.setGroupId("2");
        ggb2.setGroupName("2");
        ggb2.addMemberUserId("2");
        groups.add(ggb2.build());
        Iterator<GroupData.GroupItem> iterator = groups.iterator();
        GroupData.GroupItem.Builder gg = null;
        GroupData.GroupItem.Builder ggx = null;
        for (int i = 0; i < groups.size(); i++) {
            GroupData.GroupItem g = groups.get(i);
            if("1".equals(g.getGroupId())) {
                gg = g.toBuilder();
                gg.addMemberUserId("3");
                groups.set(i,gg.build());
            }
        }

//        for (GroupData.GroupItem g: groups ) {
//            if("1".equals(g.getGroupId())){
//                ggx = g.toBuilder();
//                ggx.addMemberUserId("3");
//                g = ggx.build();
//            }
//        }
//        while (iterator.hasNext()){
//            GroupData.GroupItem gg = iterator.next();
//            System.out.println(gg.getCreaterUserId());
//            if("1".equals(gg.getGroupId())){
//                GroupData.GroupItem.Builder ggx = gg.toBuilder();
//                ggx.addMemberUserId("3");
//                gg = ggx.build();
//            }
//        }
        System.out.print("Over"+ggb);
    }
}
