package org.ozgecmis.veri;

import org.ozgecmis.model.Egitim;
import org.ozgecmis.model.IletisimBilgisi;
import org.ozgecmis.model.IsDeneyimi;
import org.ozgecmis.model.Kisi;
import org.ozgecmis.model.Ozgecmis;
import org.ozgecmis.model.Yetenek.Seviye;

import java.time.YearMonth;
import java.util.List;

/**
 * Örnek (hayali) özgeçmiş verilerini üreten yardımcı sınıf.
 * Kendi bilgilerinizi kullanmak için bu sınıftaki değerleri değiştirmeniz yeterlidir.
 */
public final class OrnekVeri {

    private OrnekVeri() {
        // örneklenemez yardımcı sınıf
    }

    public static Ozgecmis ozgecmisOlustur() {
        IletisimBilgisi iletisim = new IletisimBilgisi(
                "busra.kaya@ornekmail.com",
                "+90 555 123 45 67",
                "Kırklareli, Türkiye",
                "github.com/busrakaya");

        Kisi kisi = new Kisi("Büşra", "Kaya", "Yazılım Geliştirici",
                "/images/profil.png", iletisim);

        return Ozgecmis.builder()
                .kisi(kisi)
                .profilOzeti("Nesneye yönelik programlama ilkelerine hâkim, Java ve Spring Boot ile "
                        + "ölçeklenebilir arka uç servisleri geliştiren bir yazılım geliştiriciyim. "
                        + "Sağlık bilişimi alanındaki deneyimim sayesinde kullanıcı ihtiyacını "
                        + "teknik çözüme dönüştürmeye odaklanıyorum. Temiz kod, test edilebilirlik "
                        + "ve ekip içi bilgi paylaşımı çalışma biçimimin temelini oluşturuyor.")

                // ---- Hayali iş deneyimleri (3 adet) ----
                .deneyimEkle(new IsDeneyimi(
                        "Java Geliştirici", "Trakya Yazılım Teknolojileri A.Ş.", "Edirne",
                        YearMonth.of(2024, 3), null,
                        List.of(
                                "Spring Boot tabanlı randevu yönetim sisteminin REST servislerini geliştirdim.",
                                "JUnit 5 ve Mockito ile birim test kapsamını %45'ten %80'e çıkardım.",
                                "PostgreSQL sorgularını optimize ederek rapor sürelerini yaklaşık %35 kısalttım.")))
                .deneyimEkle(new IsDeneyimi(
                        "Yazılım Geliştirme Uzmanı", "Medikod Sağlık Bilişimi Ltd. Şti.", "İstanbul",
                        YearMonth.of(2022, 6), YearMonth.of(2024, 2),
                        List.of(
                                "Hasta takip uygulaması için laboratuvar sonuçlarını PDF'e aktaran modülü yazdım.",
                                "HL7 FHIR standardına uygun veri aktarım servislerinin geliştirilmesinde görev aldım.",
                                "Kod incelemesi süreçlerine katılarak ekip için kodlama standartları belgesi hazırladım.")))
                .deneyimEkle(new IsDeneyimi(
                        "Stajyer Yazılım Geliştirici", "Lüleburgaz Teknokent Ar-Ge Merkezi", "Kırklareli",
                        YearMonth.of(2021, 7), YearMonth.of(2021, 9),
                        List.of(
                                "Stok takip masaüstü uygulamasının Java Swing arayüzünü geliştirdim.",
                                "Git ve GitHub ile sürüm kontrolü, dal (branch) yönetimi ve pull request süreçlerini öğrendim.")))

                // ---- Eğitim ----
                .egitimEkle(new Egitim(
                        "Bilgisayar Mühendisliği (Lisans)", "Kırklareli Üniversitesi", "Kırklareli",
                        YearMonth.of(2023, 9), null, null,
                        "Nesneye Yönelik Programlama, Veri Tabanı Yönetim Sistemleri, Ayrık Yapılar dersleri."))
                .egitimEkle(new Egitim(
                        "Bilgisayar Programcılığı (Ön Lisans)", "Trakya Üniversitesi", "Edirne",
                        YearMonth.of(2019, 9), YearMonth.of(2021, 6), "3,42 / 4,00",
                        "Bitirme projesi: Java ile kütüphane otomasyon sistemi."))

                // ---- Yetenekler ----
                .yetenekEkle("Java / OOP", Seviye.ILERI)
                .yetenekEkle("Spring Boot", Seviye.IYI)
                .yetenekEkle("SQL / PostgreSQL", Seviye.IYI)
                .yetenekEkle("Git & GitHub", Seviye.IYI)
                .yetenekEkle("Docker", Seviye.ORTA)
                .yetenekEkle("Python", Seviye.ORTA)

                // ---- Diller ----
                .dilEkle("Türkçe (Ana dil)", Seviye.ILERI)
                .dilEkle("İngilizce (B2)", Seviye.IYI)
                .dilEkle("Almanca (A2)", Seviye.BASLANGIC)

                // ---- Sertifikalar ----
                .sertifikaEkle("Oracle Certified Associate, Java SE Programmer (2023)")
                .sertifikaEkle("BTK Akademi – Spring Boot ile Web Geliştirme (2022)")

                // ---- İlgi alanları ----
                .ilgiAlaniEkle("Sağlık bilişimi")
                .ilgiAlaniEkle("Açık kaynak projeler")
                .ilgiAlaniEkle("TEKNOFEST yarışmaları")
                .ilgiAlaniEkle("Kitap okumak")
                .build();
    }
}