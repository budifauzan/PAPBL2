package com.example.android.papbl2;

public class Groups {
    private int id;
    private String namaGroup, agensi, tanggalDebut, jumlahMember;

    public Groups(int id, String namaGroup, String agensi, String tanggalDebut, String jumlahMember) {
        this.id = id;
        this.namaGroup = namaGroup;
        this.agensi = agensi;
        this.tanggalDebut = tanggalDebut;
        this.jumlahMember = jumlahMember;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaGroup() {
        return namaGroup;
    }

    public void setNamaGroup(String namaGroup) {
        this.namaGroup = namaGroup;
    }

    public String getAgensi() {
        return agensi;
    }

    public void setAgensi(String agensi) {
        this.agensi = agensi;
    }

    public String getTanggalDebut() {
        return tanggalDebut;
    }

    public void setTanggalDebut(String tanggalDebut) {
        this.tanggalDebut = tanggalDebut;
    }

    public String getJumlahMember() {
        return jumlahMember;
    }

    public void setJumlahMember(String jumlahMember) {
        this.jumlahMember = jumlahMember;
    }
}
