package org.ozgecmis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Özgeçmişin tüm bölümlerini bir arada tutan ana model sınıfı.
 * Nesne, iç içe Builder sınıfı ile adım adım oluşturulur .
 */
public final class Ozgecmis {

    private final Kisi kisi;
    private final String profilOzeti;
    private final List<IsDeneyimi> deneyimler;
    private final List<Egitim> egitimler;
    private final List<Yetenek> yetenekler;
    private final List<Yetenek> diller;
    private final List<String> sertifikalar;
    private final List<String> ilgiAlanlari;

    private Ozgecmis(Builder b) {
        this.kisi = Objects.requireNonNull(b.kisi, "Kişi bilgisi zorunludur");
        this.profilOzeti = b.profilOzeti;
        this.deneyimler = List.copyOf(b.deneyimler);
        this.egitimler = List.copyOf(b.egitimler);
        this.yetenekler = List.copyOf(b.yetenekler);
        this.diller = List.copyOf(b.diller);
        this.sertifikalar = List.copyOf(b.sertifikalar);
        this.ilgiAlanlari = List.copyOf(b.ilgiAlanlari);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Kisi getKisi() {
        return kisi;
    }

    public String getProfilOzeti() {
        return profilOzeti;
    }

    public List<IsDeneyimi> getDeneyimler() {
        return deneyimler;
    }

    public List<Egitim> getEgitimler() {
        return egitimler;
    }

    public List<Yetenek> getYetenekler() {
        return yetenekler;
    }

    public List<Yetenek> getDiller() {
        return diller;
    }

    public List<String> getSertifikalar() {
        return sertifikalar;
    }

    public List<String> getIlgiAlanlari() {
        return ilgiAlanlari;
    }

    /** Ozgecmis nesnesini adım adım kuran yardımcı sınıf. */
    public static final class Builder {

        private Kisi kisi;
        private String profilOzeti;
        private final List<IsDeneyimi> deneyimler = new ArrayList<>();
        private final List<Egitim> egitimler = new ArrayList<>();
        private final List<Yetenek> yetenekler = new ArrayList<>();
        private final List<Yetenek> diller = new ArrayList<>();
        private final List<String> sertifikalar = new ArrayList<>();
        private final List<String> ilgiAlanlari = new ArrayList<>();

        private Builder() {
        }

        public Builder kisi(Kisi kisi) {
            this.kisi = kisi;
            return this;
        }

        public Builder profilOzeti(String profilOzeti) {
            this.profilOzeti = profilOzeti;
            return this;
        }

        public Builder deneyimEkle(IsDeneyimi deneyim) {
            this.deneyimler.add(deneyim);
            return this;
        }

        public Builder egitimEkle(Egitim egitim) {
            this.egitimler.add(egitim);
            return this;
        }

        public Builder yetenekEkle(String ad, Yetenek.Seviye seviye) {
            this.yetenekler.add(new Yetenek(ad, seviye));
            return this;
        }

        public Builder dilEkle(String ad, Yetenek.Seviye seviye) {
            this.diller.add(new Yetenek(ad, seviye));
            return this;
        }

        public Builder sertifikaEkle(String sertifika) {
            this.sertifikalar.add(sertifika);
            return this;
        }

        public Builder ilgiAlaniEkle(String ilgiAlani) {
            this.ilgiAlanlari.add(ilgiAlani);
            return this;
        }

        public Ozgecmis build() {
            return new Ozgecmis(this);
        }
    }
}