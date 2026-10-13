package org.ozgecmis.model;

import java.util.Objects;

/**
 * Bir yeteneği  ve yetkinlik seviyesini temsil eder.
 */
public class Yetenek {

    /** Yetkinlik seviyeleri; her seviye PDF'teki çubuğun doluluk oranını taşır. */
    public enum Seviye {
        BASLANGIC("Başlangıç", 0.30f),
        ORTA("Orta", 0.55f),
        IYI("İyi", 0.75f),
        ILERI("İleri", 0.95f);

        private final String etiket;
        private final float oran;

        Seviye(String etiket, float oran) {
            this.etiket = etiket;
            this.oran = oran;
        }

        public String getEtiket() {
            return etiket;
        }

        public float getOran() {
            return oran;
        }
    }

    private final String ad;
    private final Seviye seviye;

    public Yetenek(String ad, Seviye seviye) {
        this.ad = Objects.requireNonNull(ad);
        this.seviye = Objects.requireNonNull(seviye);
    }

    public String getAd() {
        return ad;
    }

    public Seviye getSeviye() {
        return seviye;
    }
}