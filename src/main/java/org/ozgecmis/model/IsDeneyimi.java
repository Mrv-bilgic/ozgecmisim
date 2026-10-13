package org.ozgecmis.model;

import java.time.YearMonth;
import java.util.List;

/**
 * Tek bir iş deneyimini temsil eder. ZamanAralikliKayit sınıfından türetilmiştir.
 */
public class IsDeneyimi extends ZamanAralikliKayit {

    private final List<String> sorumluluklar;

    public IsDeneyimi(String pozisyon, String sirket, String konum,
                      YearMonth baslangic, YearMonth bitis, List<String> sorumluluklar) {
        super(pozisyon, sirket, konum, baslangic, bitis);
        this.sorumluluklar = List.copyOf(sorumluluklar);
    }

    @Override
    public List<String> getAciklamaMaddeleri() {
        return sorumluluklar;
    }
}