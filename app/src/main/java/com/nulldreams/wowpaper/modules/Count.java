package com.nulldreams.wowpaper.modules;

/**
 * Created by boybe on 2017/3/23.
 */

public class Count {
    public int month_count, last_month_count;
    public float month_price, last_month_price;

    @Override
    public String toString() {
        return "month count:" + month_count + "\nmonth price:$" + month_price;
    }
}
