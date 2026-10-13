package org.ozgecmis.model;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Bir eğitim kaydını temsil eder. ZamanAralikliKayit sınıfından türetilmiştir.
 */
public class Egitim extends ZamanAralikliKayit {

    private final String notOrtalamasi;
    private final String aciklama;

    public Egitim(String bolum, String okul, String konum,
                  YearMonth baslangic, YearMonth bitis, String notOrtalamasi, String aciklama) {
        super(bolum, okul, konum, baslangic, bitis);
        this.notOrtalamasi = notOrtalamasi;
        this.aciklama = aciklama;
    }

    @Override
    public List<String> getAciklamaMaddeleri() {
        List<String> maddeler = new ArrayList<>();
        if (notOrtalamasi != null && !notOrtalamasi.isBlank()) {
            maddeler.add("Not ortalaması: " + notOrtalamasi);
        }
        if (aciklama != null && !aciklama.isBlank()) {
            maddeler.add(aciklama);
        }
        return List.copyOf(maddeler);
    }

    public String getNotOrtalamasi() {
        return notOrtalamasi;
    }
}