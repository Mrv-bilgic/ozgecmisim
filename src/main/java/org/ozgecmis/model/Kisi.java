package org.ozgecmis.model;

import java.util.Objects;

/**
 * Özgeçmiş sahibinin kimlik bilgileri
 */
public class Kisi {

    private final String ad;
    private final String soyad;
    private final String unvan;
    private final String fotografYolu;
    private final IletisimBilgisi iletisim;

    public Kisi(String ad, String soyad, String unvan, String fotografYolu, IletisimBilgisi iletisim) {
        this.ad = Objects.requireNonNull(ad, "Ad boş olamaz");
        this.soyad = Objects.requireNonNull(soyad, "Soyad boş olamaz");
        this.unvan = unvan;
        this.fotografYolu = fotografYolu;
        this.iletisim = Objects.requireNonNull(iletisim, "İletişim bilgisi boş olamaz");
    }

    /** Ad ve soyadı birleştirerek tam adı döndürür. */
    public String getTamAd() {
        return ad + " " + soyad;
    }

    public String getAd() {
        return ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public String getUnvan() {
        return unvan;
    }

    public String getFotografYolu() {
        return fotografYolu;
    }

    public IletisimBilgisi getIletisim() {
        return iletisim;
    }
}