package com.project;

public class Ciutada {
    private long ciutadaId;
    private long ciutatId;
    private String nom;
    private String cognom;
    private int edat;

    private Ciutat ciutat;

    public Ciutada() {
    }

    public Ciutada(String nom, String cognom, int edat) {
        this.nom = nom;
        this.cognom = cognom;
        this.edat = edat;
    }

    public long getCiutadaId() {
        return ciutadaId;
    }

    public void setCiutadaId(long ciutadaId) {
        this.ciutadaId = ciutadaId;
    }

    public long getCiutatId() {
        return ciutatId;
    }

    public void setCiutatId(long ciutatId) {
        this.ciutatId = ciutatId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognom() {
        return cognom;
    }

    public void setCognom(String cognom) {
        this.cognom = cognom;
    }

    public int getEdat() {
        return edat;
    }

    public void setEdat(int edat) {
        this.edat = edat;
    }

    public Ciutat getCiutat() {
        return ciutat;
    }

    public void setCiutat(Ciutat ciutat) {
        this.ciutat = ciutat;
    }


    @Override
    public String toString() {
        return this.getCiutadaId() + ": " + this.getNom() + " " + this.getCognom() + ", " + this.getEdat();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ciutada ciut = (Ciutada) o;
        return ciutadaId == ciut.ciutadaId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(ciutadaId);
    }
}