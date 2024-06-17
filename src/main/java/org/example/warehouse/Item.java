package org.example.warehouse;

import lombok.Data;
import org.json.JSONObject;

@Data
public class Item {

    private int id;
    private String name;
    private String description;
    private String supplier;
    private int stock_qty;
    private double price;
    private int group_id;
    private static int MAX_ID = 0;

    public Item(int newID, String newName, String newDescription, String newSupplier, int newStock_qty, double newPrice, int newGroupId) {
        this.id = newID;
        this.name = newName;
        this.description = newDescription;
        this.supplier = newSupplier;
        this.stock_qty = newStock_qty;
        this.price = newPrice;
        this.group_id = newGroupId;
        if (MAX_ID < newID) {
            MAX_ID = newID;
        }
    }

    public Item(String newName, String newDescription, String newSupplier, int newStock_qty, double newPrice, int newGroupId) {
        this.name = newName;
        this.description = newDescription;
        this.supplier = newSupplier;
        this.stock_qty = newStock_qty;
        this.price = newPrice;
        this.group_id = newGroupId;
        MAX_ID++;
        id = MAX_ID;
    }

    @Override
    public String toString() {
        return this.id + "|" + this.name + "|" + this.description + "|" + this.supplier + "|" + this.stock_qty + "|" + this.price + "|" + this.group_id;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject(
                "{\"id\":" + id +
                        ", \"name\":\"" + name +
                        "\", \"description\":\"" + description +
                        "\", \"supplier\":\""+ supplier +
                        "\", \"stock_qty\":" + stock_qty +
                        ", \"price\":" + price +
                        ", \"group_id\":" + group_id + "}");
        return json;
    }
}