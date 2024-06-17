package org.example.database.interfaces;

import org.example.warehouse.Group;

public interface IGroupDAO {
    void initGroupsTable();
    void dropGroupsTable();
    Group readGroup(int id);
    int addGroup(Group group);
    int updateGroup(Group group);
    boolean isNameUnique(String groupName);
    int deleteGroup(int id);
    Group getGroupByName(String name);
}
