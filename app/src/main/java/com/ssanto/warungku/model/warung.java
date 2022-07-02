package com.ssanto.warungku.model;

public class warung {
    private String id="";
    private String nama="";
    private String koordinat="";
    private String alamat="";
    private String base64="";

    public warung(String id,String nama, String koordinat, String alamat, String base64) {
        this.id = id;
        this.nama = nama;
        this.koordinat = koordinat;
        this.alamat = alamat;
        this.base64 = base64;
    }

    public String getId() {
        return id;
    }
    public String getNama() {
        return nama;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getBase64() {
        return base64;
    }
}
