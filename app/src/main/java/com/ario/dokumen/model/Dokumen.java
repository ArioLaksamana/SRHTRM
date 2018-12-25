package com.ario.dokumen.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Dokumen {

    private String nomor;
    private String perihal;
    private String tanggal;
    private String signatureUrl;
    private String penerima;

    public Dokumen() {
    }

    public Dokumen(String nomor, String perihal, String tanggal, String signatureUrl, String penerima) {
        this.nomor = nomor;
        this.perihal = perihal;
        this.tanggal = tanggal;
        this.signatureUrl = signatureUrl;
        this.penerima = penerima;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getPerihal() {
        return perihal;
    }

    public void setPerihal(String perihal) {
        this.perihal = perihal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public String getPenerima() {
        return penerima;
    }

    public void setPenerima(String penerima) {
        this.penerima = penerima;
    }
}
