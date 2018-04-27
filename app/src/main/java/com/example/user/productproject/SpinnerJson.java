package com.example.user.productproject;

public class SpinnerJson {
    String sh_name;
    String sh_id;

    public SpinnerJson(String sh_name, String sh_id) {
        this.sh_name = sh_name;
        this.sh_id = sh_id;
    }

    public String getSh_name() {
        return sh_name;
    }

    public String getSh_id() {
        return sh_id;
    }

    public void setSh_name(String sh_name) {
        this.sh_name = sh_name;
    }

    public void setSh_id(String sh_id) {
        this.sh_id = sh_id;
    }
}
