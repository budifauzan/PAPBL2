package com.example.android.papbl2;

public class Members {
    private int id, idGroup;
    private String namaMember, namaPanggung, tanggalLahir;

    public Members(int id, int idGroup, String namaMember, String namaPanggung, String tanggalLahir) {
        this.id = id;
        this.idGroup = idGroup;
        this.namaMember = namaMember;
        this.namaPanggung = namaPanggung;
        this.tanggalLahir = tanggalLahir;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public String getNamaMember() {
        return namaMember;
    }

    public void setNamaMember(String namaMember) {
        this.namaMember = namaMember;
    }

    public String getNamaPanggung() {
        return namaPanggung;
    }

    public void setNamaPanggung(String namaPanggung) {
        this.namaPanggung = namaPanggung;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
}
