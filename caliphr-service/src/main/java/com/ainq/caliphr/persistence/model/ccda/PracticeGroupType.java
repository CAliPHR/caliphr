package com.ainq.caliphr.persistence.model.ccda;

/**
 * 
 * This class was not created as a proper enum so it can be extended
 * 
 * Created by mmelusky on 2/15/2016.
 */
public class PracticeGroupType {

    public static final PracticeGroupType UNKNOWN_GROUP = new PracticeGroupType(0);

    private Integer groupId;

    // Constructor
    public PracticeGroupType(Integer groupId) {
        this.groupId = groupId;
    }

    // Getter
    public Integer getGroupId() {
        return groupId;
    }
}
