package com.lafilgroup.merchandisinginventory.logs;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/20/2018.
 */

public class LogsItemDTO
{
    public ArrayList<LogItemDTO> items;

    public ArrayList<LogItemDTO> getItems() {
        return items;
    }

    public void setItems(ArrayList<LogItemDTO> items) {
        this.items = items;
    }

    public class LogItemDTO
    {
        String transaction_number, material_code, entry_uom, inventory_type, entry_qty, remarks, delivery_date, return_date, material_description, inventory_type_description;

        public String getTransaction_number() {
            return transaction_number;
        }

        public void setTransaction_number(String transaction_number) {
            this.transaction_number = transaction_number;
        }

        public String getMaterial_code() {
            return material_code;
        }

        public void setMaterial_code(String material_code) {
            this.material_code = material_code;
        }



        public String getInventory_type() {
            return inventory_type;
        }

        public void setInventory_type(String inventory_type) {
            this.inventory_type = inventory_type;
        }

        public String getEntry_uom() {
            return entry_uom;
        }

        public void setEntry_uom(String entry_uom) {
            this.entry_uom = entry_uom;
        }

        public String getEntry_qty() {
            return entry_qty;
        }

        public void setEntry_qty(String entry_qty) {
            this.entry_qty = entry_qty;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getDelivery_date() {
            return delivery_date;
        }

        public void setDelivery_date(String delivery_date) {
            this.delivery_date = delivery_date;
        }

        public String getReturn_date() {
            return return_date;
        }

        public void setReturn_date(String return_date) {
            this.return_date = return_date;
        }

        public String getMaterial_description() {
            return material_description;
        }

        public void setMaterial_description(String material_description) {
            this.material_description = material_description;
        }

        public String getInventory_type_description() {
            return inventory_type_description;
        }

        public void setInventory_type_description(String inventory_type_description) {
            this.inventory_type_description = inventory_type_description;
        }
    }
}
