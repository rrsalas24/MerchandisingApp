package com.lafilgroup.merchandisinginventory.logs;

import com.lafilgroup.merchandisinginventory.announcement.AnnouncementsDTO;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/19/2018.
 */

public class LogsDTO
{
    public ArrayList<LogDTO> items;

    public ArrayList<LogDTO> getItems() {
        return items;
    }

    public void setItems(ArrayList<LogDTO> items) {
        this.items = items;
    }

    public class LogDTO
    {
        public String transaction_number, customer_name, remarks, created_at;

        public String getTransaction_number() {
            return transaction_number;
        }

        public void setTransaction_number(String transaction_number) {
            this.transaction_number = transaction_number;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
