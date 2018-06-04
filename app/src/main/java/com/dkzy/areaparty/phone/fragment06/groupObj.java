package com.dkzy.areaparty.phone.fragment06;

import java.util.List;

public class groupObj {
    String groupId;
    String groupName;
    String groupCreateId;
    List<String> memberUserId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCreateId() {
        return groupCreateId;
    }

    public void setGroupCreateId(String groupCreateId) {
        this.groupCreateId = groupCreateId;
    }

    public List<String> getMemberUserId() {
        return memberUserId;
    }

    public void setMemberUserId(List<String> memberUserId) {
        this.memberUserId = memberUserId;
    }
}
