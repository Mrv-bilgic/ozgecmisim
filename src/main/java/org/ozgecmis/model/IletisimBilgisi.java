package org.ozgecmis.model;

import java.util.Objects;

/**
 * Bir kişinin iletişim bilgilerini tutan değişmez  sınıf.
 */
public final class IletisimBilgisi {

    private final String eposta;
    private final String telefon;
    private final String adres;
    private final String web;

    public IletisimBilgisi(String eposta, String telefon, String adres, String web) {
        this.eposta = Objects.requireNonNull(eposta, "E-posta boş olamaz");
        this.telefon = telefon;
        this.adres = adres;
        this.web = web;
    }

    public String getEposta() {
        return eposta;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getAdres() {
        return adres;
    }

    public String getWeb() {
        return web;
    }
}