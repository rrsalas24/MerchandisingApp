package com.lafilgroup.merchandisinginventory.schedule;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by RR SALAS on 3/20/2018.
 */

public class SchedulesDTO
{
    public ArrayList<ScheduleDTO> items;

    public ArrayList<ScheduleDTO> getItems()
    {
        return items;
    }

    public void setItems(ArrayList<ScheduleDTO> items)
    {
        this.items = items;
    }

    public class ScheduleDTO
    {
        private String id;
        private String date;
        private String customer_name;
        private String address;
        private String status;
        private String status_description;
        private String last_name;
        private String first_name;
        private String remarks;
        private String customer_code;
        private String customer_id;
        private String time_in;
        private String time_out;



        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getCustomer_code() {
            return customer_code;
        }

        public void setCustomer_code(String customer_code) {
            this.customer_code = customer_code;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDate()
        {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus_description() {
            return status_description;
        }

        public void setStatus_description(String status_description) {
            this.status_description = status_description;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        public String getTime_in() {
            return time_in;
        }

        public void setTime_in(String time_in) {
            this.time_in = time_in;
        }

        public String getTime_out() {
            return time_out;
        }

        public void setTime_out(String time_out) {
            this.time_out = time_out;
        }
    }
}
