package org.example.warehouse;

import lombok.Data;
import org.json.JSONObject;

@Data
public class Group{

    private int id;
    private String name;
    private String description;
    private static int MAX_ID = 0;

    public Group(int newID, String newName, String newDescription) {
        this.id = newID;
        this.name = newName;
        this.description = newDescription;
        if (MAX_ID < newID){MAX_ID = newID;}
    }

    public Group(String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
        MAX_ID++;
        id = MAX_ID;
    }

    @Override
    public String toString() {
        return  this.id  + "|" + this.name + "|" + this.description ;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject(
                "{" + "\"id\":" + id +
                        ", \"name\":\"" + name +
                        "\", \"description\":\"" + description + "\"}");
        return json;
    }
}