package com.lafilgroup.merchandisinginventory.logs;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/20/2018.
 */

public class LogsExpirationDTO
{
    public ArrayList<LogExpirationDTO> items;

    public ArrayList<LogExpirationDTO> getItems() {
        return items;
    }

    public void setItems(ArrayList<LogExpirationDTO> items) {
        this.items = items;
    }

    public class LogExpirationDTO
    {
        String entry_qty,expiration_date;

        public String getEntry_qty() {
            return entry_qty;
        }

        public void setEntry_qty(String entry_qty) {
            this.entry_qty = entry_qty;
        }

        public String getExpiration_date() {
            return expiration_date;
        }

        public void setExpiration_date(String expiration_date) {
            this.expiration_date = expiration_date;
        }
    }
}
