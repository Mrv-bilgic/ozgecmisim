package org.ozgecmis.model;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Başlangıç ve bitiş tarihi olan özgeçmiş kayıtları için soyut  temel sınıf.
 * IsDeneyimi ve Egitim bu sınıftan kalıtım alır.
 */
public abstract class ZamanAralikliKayit {

    private static final DateTimeFormatter TARIH_BICIMI = DateTimeFormatter.ofPattern("MM/yyyy");

    protected final String baslik;
    protected final String kurum;
    protected final String konum;
    protected final YearMonth baslangic;
    /** null ise kayıt hâlâ devam ediyor demektir. */
    protected final YearMonth bitis;

    protected ZamanAralikliKayit(String baslik, String kurum, String konum,
                                 YearMonth baslangic, YearMonth bitis) {
        this.baslik = Objects.requireNonNull(baslik, "Başlık boş olamaz");
        this.kurum = Objects.requireNonNull(kurum, "Kurum boş olamaz");
        this.konum = konum;
        this.baslangic = Objects.requireNonNull(baslangic, "Başlangıç tarihi boş olamaz");
        if (bitis != null && bitis.isBefore(baslangic)) {
            throw new IllegalArgumentException("Bitiş tarihi başlangıçtan önce olamaz: " + baslik);
        }
        this.bitis = bitis;
    }

    /** Örnek çıktı: "09/2021 – 06/2023" veya "03/2024 – Devam ediyor". */
    public String getTarihAraligi() {
        String son = (bitis == null) ? "Devam ediyor" : bitis.format(TARIH_BICIMI);
        return baslangic.format(TARIH_BICIMI) + " – " + son;
    }

    public boolean devamEdiyorMu() {
        return bitis == null;
    }

    public String getBaslik() {
        return baslik;
    }

    public String getKurum() {
        return kurum;
    }

    public String getKonum() {
        return konum;
    }

    /** Her alt sınıf bu metodu kendine göre uygular */
    public abstract List<String> getAciklamaMaddeleri();
}