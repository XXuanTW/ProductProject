package com.example.user.productproject;

public class Json {
    String shname;
    String sh_id;
    String p_id;
    String p_name;
    String p_no;
    String p_barcode;
    String p_inventory_date;
    String p_photo;
    String p_count;
    String p_inventory;
    String p_inventory_eid;

    public String getShname() {
        return shname;
    }

    public String getSh_id() {
        return sh_id;
    }

    public String getP_id() {
        return p_id;
    }

    public String getP_name() {
        return p_name;
    }

    public String getP_no() {
        return p_no;
    }

    public String getP_barcode() {
        return p_barcode;
    }

    public String getP_inventory_date() {
        return p_inventory_date;
    }

    public String getP_photo() {
        return p_photo;
    }

    public String getP_count() {
        return p_count;
    }

    public String getP_inventory() {
        return p_inventory;
    }

    public String getP_inventory_eid() {
        return p_inventory_eid;
    }

    public Json(String shname, String sh_id, String p_id, String p_name, String p_no, String p_barcode, String p_inventory_date, String p_photo, String p_count, String p_inventory, String p_inventory_eid) {
        this.shname = shname;
        this.sh_id = sh_id;
        this.p_id = p_id;
        this.p_name = p_name;
        this.p_no = p_no;
        this.p_barcode = p_barcode;
        this.p_inventory_date = p_inventory_date;
        this.p_photo = p_photo;
        this.p_count = p_count;
        this.p_inventory = p_inventory;
        this.p_inventory_eid = p_inventory_eid;

    }

    public void setShname(String shname) {
        this.shname = shname;
    }

    public void setSh_id(String sh_id) {
        this.sh_id = sh_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public void setP_no(String p_no) {
        this.p_no = p_no;
    }

    public void setP_barcode(String p_barcode) {
        this.p_barcode = p_barcode;
    }

    public void setP_inventory_date(String p_inventory_date) {
        this.p_inventory_date = p_inventory_date;
    }

    public void setP_photo(String p_photo) {
        this.p_photo = p_photo;
    }

    public void setP_count(String p_count) {
        this.p_count = p_count;
    }

    public void setP_inventory(String p_inventory) {
        this.p_inventory = p_inventory;
    }

    public void setP_inventory_eid(String p_inventory_eid) {
        this.p_inventory_eid = p_inventory_eid;
    }
}