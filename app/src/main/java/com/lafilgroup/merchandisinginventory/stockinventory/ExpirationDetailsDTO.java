package com.lafilgroup.merchandisinginventory.stockinventory;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/5/2018.
 */

public class ExpirationDetailsDTO
{
    public String expiration_id;
    public String expiration_date;
    public String expiration_qty;

    public String getExpiration_id() {
        return expiration_id;
    }

    public void setExpiration_id(String expiration_id) {
        this.expiration_id = expiration_id;
    }

    public String getExpiration_date()
    {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date)
    {
        this.expiration_date = expiration_date;
    }

    public String getExpiration_qty()
    {
        return expiration_qty;
    }

    public void setExpiration_qty(String expiration_qty)
    {
        this.expiration_qty = expiration_qty;
    }

}
