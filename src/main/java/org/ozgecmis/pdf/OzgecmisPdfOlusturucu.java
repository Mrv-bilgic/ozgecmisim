package org.ozgecmis.pdf;

import org.ozgecmis.model.IletisimBilgisi;
import org.ozgecmis.model.Kisi;
import org.ozgecmis.model.Ozgecmis;
import org.ozgecmis.model.Yetenek;
import org.ozgecmis.model.ZamanAralikliKayit;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Ozgecmis nesnesini Apache PDFBox kullanarak A4 boyutunda PDF'e dönüştürür.
 * Solda koyu renkli kenar çubuğu (fotoğraf, iletişim, yetenekler, diller, ilgi alanları),
 * sağda ana içerik (ad-unvan, profil, iş deneyimi, eğitim, sertifikalar) bulunur.
 */
public class OzgecmisPdfOlusturucu implements BelgeOlusturucu {

    // ---- Sayfa ölçüleri (1 pt = 1/72 inç) ----
    private static final PDRectangle SAYFA_BOYUTU = PDRectangle.A4;
    private static final float SAYFA_GENISLIK = SAYFA_BOYUTU.getWidth();
    private static final float SAYFA_YUKSEKLIK = SAYFA_BOYUTU.getHeight();

    private static final float KENAR_CUBUGU_GENISLIK = 190f;
    private static final float KENAR_IC_BOSLUK = 20f;
    private static final float ANA_SOL = KENAR_CUBUGU_GENISLIK + 26f;
    private static final float ANA_SAG = SAYFA_GENISLIK - 34f;
    private static final float ANA_GENISLIK = ANA_SAG - ANA_SOL;
    private static final float UST_BOSLUK = 48f;
    private static final float ALT_SINIR = 46f;
    private static final float FOTOGRAF_CAP = 120f;

    // ---- Renkler ----
    private static final Color KENAR_RENGI = new Color(31, 47, 70);
    private static final Color VURGU_RENGI = new Color(38, 137, 150);
    private static final Color ACIK_VURGU = new Color(120, 200, 205);
    private static final Color CUBUK_ZEMIN = new Color(64, 84, 110);
    private static final Color BEYAZ = Color.WHITE;
    private static final Color KOYU_METIN = new Color(33, 37, 41);
    private static final Color GRI_METIN = new Color(100, 108, 116);
    private static final Color CIZGI_RENGI = new Color(210, 216, 222);

    // ---- Kaynak dosyalar (src/main/resources altında) ----
    private static final String NORMAL_FONT_YOLU = "/fonts/DejaVuSans.ttf";
    private static final String KALIN_FONT_YOLU = "/fonts/DejaVuSans-Bold.ttf";

    /** Bu sınıfla üretilen toplam belge sayısı (sınıf düzeyinde, static). */
    private static int olusturulanBelgeSayisi = 0;

    // ---- Bir belge üretimi süresince geçerli olan durum ----
    private PDDocument belge;
    private PDPageContentStream akis;
    private PDFont normalFont;
    private PDFont kalinFont;
    private float imlecY;

    @Override
    public void olustur(Ozgecmis ozgecmis, Path ciktiYolu) throws IOException {
        Objects.requireNonNull(ozgecmis, "Özgeçmiş boş olamaz");
        Objects.requireNonNull(ciktiYolu, "Çıktı yolu boş olamaz");

        // try-with-resources: blok bitince (hata olsa bile) PDDocument.close() otomatik çağrılır
        try (PDDocument yeniBelge = new PDDocument()) {
            this.belge = yeniBelge;
            belgeBilgileriniAyarla(ozgecmis.getKisi());
            fontlariYukle();

            yeniSayfaAc();
            kenarCubugunuCiz(ozgecmis);
            anaIcerigiYaz(ozgecmis);
            akisiKapat();

            Path klasor = ciktiYolu.toAbsolutePath().getParent();
            if (klasor != null) {
                Files.createDirectories(klasor);
            }
            yeniBelge.save(ciktiYolu.toFile());
        } finally {
            akisiKapat();
            // Kapanan nesnelere referans tutulmasın; çöp toplayıcı (GC) toplayabilsin
            this.belge = null;
            this.normalFont = null;
            this.kalinFont = null;
        }
        olusturulanBelgeSayisi++;
    }

    public static int getOlusturulanBelgeSayisi() {
        return olusturulanBelgeSayisi;
    }

    // ===================== Hazırlık =====================

    private void belgeBilgileriniAyarla(Kisi kisi) {
        PDDocumentInformation bilgi = belge.getDocumentInformation();
        bilgi.setTitle(kisi.getTamAd() + " - Özgeçmiş");
        bilgi.setAuthor(kisi.getTamAd());
        bilgi.setCreator("Java PDF Özgeçmiş Oluşturucu");
        bilgi.setCreationDate(Calendar.getInstance());
    }

    /** Türkçe karakterleri destekleyen TrueType fontları PDF'e gömer. */
    private void fontlariYukle() throws IOException {
        normalFont = fontYukle(NORMAL_FONT_YOLU);
        kalinFont = fontYukle(KALIN_FONT_YOLU);
    }

    private PDFont fontYukle(String kaynakYolu) throws IOException {
        try (InputStream girdi = getClass().getResourceAsStream(kaynakYolu)) {
            if (girdi == null) {
                throw new IOException("Font bulunamadı: " + kaynakYolu);
            }
            return PDType0Font.load(belge, girdi);
        }
    }

    // ===================== Sayfa yönetimi =====================

    private void yeniSayfaAc() throws IOException {
        akisiKapat();
        PDPage sayfa = new PDPage(SAYFA_BOYUTU);
        belge.addPage(sayfa);
        akis = new PDPageContentStream(belge, sayfa);

        dikdortgenCiz(0, 0, KENAR_CUBUGU_GENISLIK, SAYFA_YUKSEKLIK, KENAR_RENGI);
        imlecY = SAYFA_YUKSEKLIK - UST_BOSLUK;
    }

    private void akisiKapat() throws IOException {
        if (akis != null) {
            akis.close();
            akis = null;
        }
    }

    /** Ana sütunda yeterli yer yoksa yeni sayfa açar. */
    private void yerAyir(float gerekenYukseklik) throws IOException {
        if (imlecY - gerekenYukseklik < ALT_SINIR) {
            yeniSayfaAc();
        }
    }

    // ===================== Sol kenar çubuğu =====================

    private void kenarCubugunuCiz(Ozgecmis ozgecmis) throws IOException {
        float x = KENAR_IC_BOSLUK;
        float genislik = KENAR_CUBUGU_GENISLIK - 2 * KENAR_IC_BOSLUK;
        float y = SAYFA_YUKSEKLIK - 36f;

        // Fotoğraf
        float merkezX = KENAR_CUBUGU_GENISLIK / 2f;
        float merkezY = y - FOTOGRAF_CAP / 2f;
        yuvarlakFotografCiz(ozgecmis.getKisi().getFotografYolu(), merkezX, merkezY, FOTOGRAF_CAP / 2f);
        y -= FOTOGRAF_CAP + 30f;

        // İletişim
        y = kenarBaslikYaz("İLETİŞİM", x, y, genislik);
        IletisimBilgisi iletisim = ozgecmis.getKisi().getIletisim();
        y = kenarBilgiYaz("E-posta", iletisim.getEposta(), x, y, genislik);
        y = kenarBilgiYaz("Telefon", iletisim.getTelefon(), x, y, genislik);
        y = kenarBilgiYaz("Adres", iletisim.getAdres(), x, y, genislik);
        y = kenarBilgiYaz("Web", iletisim.getWeb(), x, y, genislik);
        y -= 10f;

        // Yetenekler
        if (!ozgecmis.getYetenekler().isEmpty()) {
            y = kenarBaslikYaz("YETENEKLER", x, y, genislik);
            for (Yetenek yetenek : ozgecmis.getYetenekler()) {
                y = seviyeCubuguCiz(yetenek, x, y, genislik);
            }
            y -= 10f;
        }

        // Diller
        if (!ozgecmis.getDiller().isEmpty()) {
            y = kenarBaslikYaz("YABANCI DİLLER", x, y, genislik);
            for (Yetenek dil : ozgecmis.getDiller()) {
                y = seviyeCubuguCiz(dil, x, y, genislik);
            }
            y -= 10f;
        }

        // İlgi alanları
        if (!ozgecmis.getIlgiAlanlari().isEmpty()) {
            y = kenarBaslikYaz("İLGİ ALANLARI", x, y, genislik);
            for (String ilgi : ozgecmis.getIlgiAlanlari()) {
                for (String satir : satirlaraBol("• " + ilgi, normalFont, 8.5f, genislik)) {
                    metinYaz(satir, normalFont, 8.5f, BEYAZ, x, y);
                    y -= 12f;
                }
            }
        }
    }

    private float kenarBaslikYaz(String baslik, float x, float y, float genislik) throws IOException {
        metinYaz(baslik, kalinFont, 10.5f, ACIK_VURGU, x, y);
        y -= 6f;
        dikdortgenCiz(x, y, genislik, 1f, ACIK_VURGU);
        return y - 16f;
    }

    private float kenarBilgiYaz(String etiket, String deger, float x, float y, float genislik)
            throws IOException {
        if (deger == null || deger.isBlank()) {
            return y;
        }
        metinYaz(etiket, kalinFont, 8f, ACIK_VURGU, x, y);
        y -= 11f;
        for (String satir : satirlaraBol(deger, normalFont, 8.5f, genislik)) {
            metinYaz(satir, normalFont, 8.5f, BEYAZ, x, y);
            y -= 11f;
        }
        return y - 5f;
    }

    private float seviyeCubuguCiz(Yetenek yetenek, float x, float y, float genislik) throws IOException {
        metinYaz(yetenek.getAd(), normalFont, 8.5f, BEYAZ, x, y);
        String etiket = yetenek.getSeviye().getEtiket();
        float etiketGenislik = metinGenisligi(etiket, normalFont, 7.5f);
        metinYaz(etiket, normalFont, 7.5f, ACIK_VURGU, x + genislik - etiketGenislik, y);
        y -= 7f;
        dikdortgenCiz(x, y, genislik, 4f, CUBUK_ZEMIN);
        dikdortgenCiz(x, y, genislik * yetenek.getSeviye().getOran(), 4f, VURGU_RENGI);
        return y - 15f;
    }

    /** Fotoğrafı daire şeklinde kırparak çizer ve etrafına çerçeve ekler. */
    private void yuvarlakFotografCiz(String yol, float mx, float my, float r) throws IOException {
        byte[] veri = fotografOku(yol);
        PDImageXObject resim = PDImageXObject.createFromByteArray(belge, veri, "profil");

        akis.saveGraphicsState();
        dairePathOlustur(mx, my, r);
        akis.clip();
        float olcek = Math.max((2 * r) / resim.getWidth(), (2 * r) / resim.getHeight());
        float w = resim.getWidth() * olcek;
        float h = resim.getHeight() * olcek;
        akis.drawImage(resim, mx - w / 2f, my - h / 2f, w, h);
        akis.restoreGraphicsState();

        akis.setStrokingColor(VURGU_RENGI);
        akis.setLineWidth(3f);
        dairePathOlustur(mx, my, r + 1.5f);
        akis.stroke();
    }

    /** Dört Bézier eğrisi ile daire çizer (PDF'te hazır daire komutu yoktur). */
    private void dairePathOlustur(float mx, float my, float r) throws IOException {
        final float k = 0.552284749f * r;
        akis.moveTo(mx + r, my);
        akis.curveTo(mx + r, my + k, mx + k, my + r, mx, my + r);
        akis.curveTo(mx - k, my + r, mx - r, my + k, mx - r, my);
        akis.curveTo(mx - r, my - k, mx - k, my - r, mx, my - r);
        akis.curveTo(mx + k, my - r, mx + r, my - k, mx + r, my);
        akis.closePath();
    }

    /** Fotoğrafı önce dosya sisteminden, bulunamazsa resources klasöründen okur. */
    private byte[] fotografOku(String yol) throws IOException {
        if (yol == null || yol.isBlank()) {
            throw new IOException("Fotoğraf yolu belirtilmemiş");
        }
        Path dosya = Path.of(yol);
        if (Files.isRegularFile(dosya)) {
            return Files.readAllBytes(dosya);
        }
        String kaynak = yol.startsWith("/") ? yol : "/" + yol;
        try (InputStream girdi = getClass().getResourceAsStream(kaynak)) {
            if (girdi == null) {
                throw new IOException("Fotoğraf bulunamadı: " + yol);
            }
            return girdi.readAllBytes();
        }
    }

    // ===================== Sağ ana içerik =====================

    private void anaIcerigiYaz(Ozgecmis ozgecmis) throws IOException {
        Kisi kisi = ozgecmis.getKisi();

        metinYaz(kisi.getTamAd().toUpperCase(Locale.forLanguageTag("tr")),
                kalinFont, 24f, KOYU_METIN, ANA_SOL, imlecY);
        imlecY -= 22f;
        if (kisi.getUnvan() != null) {
            metinYaz(kisi.getUnvan(), normalFont, 12.5f, VURGU_RENGI, ANA_SOL, imlecY);
            imlecY -= 14f;
        }
        dikdortgenCiz(ANA_SOL, imlecY, 60f, 3f, VURGU_RENGI);
        imlecY -= 24f;

        if (ozgecmis.getProfilOzeti() != null && !ozgecmis.getProfilOzeti().isBlank()) {
            bolumBasligiYaz("PROFİL");
            paragrafYaz(ozgecmis.getProfilOzeti(), 9.5f, ANA_SOL, ANA_GENISLIK, 13.5f);
            imlecY -= 12f;
        }

        if (!ozgecmis.getDeneyimler().isEmpty()) {
            bolumBasligiYaz("İŞ DENEYİMİ");
            for (ZamanAralikliKayit deneyim : ozgecmis.getDeneyimler()) {
                kayitYaz(deneyim);
            }
            imlecY -= 4f;
        }

        if (!ozgecmis.getEgitimler().isEmpty()) {
            bolumBasligiYaz("EĞİTİM");
            for (ZamanAralikliKayit egitim : ozgecmis.getEgitimler()) {
                kayitYaz(egitim);
            }
            imlecY -= 4f;
        }

        if (!ozgecmis.getSertifikalar().isEmpty()) {
            bolumBasligiYaz("SERTİFİKALAR");
            for (String sertifika : ozgecmis.getSertifikalar()) {
                maddeYaz(sertifika, ANA_SOL, ANA_GENISLIK);
            }
        }
    }

    private void bolumBasligiYaz(String baslik) throws IOException {
        yerAyir(48f);
        metinYaz(baslik, kalinFont, 12f, KOYU_METIN, ANA_SOL, imlecY);
        imlecY -= 6f;
        dikdortgenCiz(ANA_SOL, imlecY, ANA_GENISLIK, 0.8f, CIZGI_RENGI);
        imlecY -= 16f;
    }

    /** İş deneyimi veya eğitim kaydını yazar (çok biçimlilik: parametre soyut sınıf tipinde). */
    private void kayitYaz(ZamanAralikliKayit kayit) throws IOException {
        yerAyir(40f);

        String tarih = kayit.getTarihAraligi();
        float tarihGenislik = metinGenisligi(tarih, normalFont, 8.5f);
        metinYaz(kayit.getBaslik(), kalinFont, 10.5f, KOYU_METIN, ANA_SOL, imlecY);
        metinYaz(tarih, normalFont, 8.5f, GRI_METIN, ANA_SAG - tarihGenislik, imlecY);
        imlecY -= 13f;

        String kurumSatiri = kayit.getKurum()
                + (kayit.getKonum() != null ? "  |  " + kayit.getKonum() : "");
        metinYaz(kurumSatiri, normalFont, 9f, VURGU_RENGI, ANA_SOL, imlecY);
        imlecY -= 13f;

        for (String madde : kayit.getAciklamaMaddeleri()) {
            maddeYaz(madde, ANA_SOL, ANA_GENISLIK);
        }
        imlecY -= 7f;
    }

    private void maddeYaz(String metin, float x, float genislik) throws IOException {
        final float boyut = 9f;
        final float girinti = 11f;
        List<String> satirlar = satirlaraBol(metin, normalFont, boyut, genislik - girinti);
        yerAyir(satirlar.size() * 12f);
        metinYaz("•", normalFont, boyut, VURGU_RENGI, x + 2f, imlecY);
        for (String satir : satirlar) {
            metinYaz(satir, normalFont, boyut, KOYU_METIN, x + girinti, imlecY);
            imlecY -= 12f;
        }
        imlecY -= 1.5f;
    }

    private void paragrafYaz(String metin, float boyut, float x, float genislik, float satirAraligi)
            throws IOException {
        for (String satir : satirlaraBol(metin, normalFont, boyut, genislik)) {
            yerAyir(satirAraligi);
            metinYaz(satir, normalFont, boyut, KOYU_METIN, x, imlecY);
            imlecY -= satirAraligi;
        }
    }

    // ===================== Çizim yardımcıları =====================

    private void metinYaz(String metin, PDFont font, float boyut, Color renk, float x, float y)
            throws IOException {
        akis.beginText();
        akis.setFont(font, boyut);
        akis.setNonStrokingColor(renk);
        akis.newLineAtOffset(x, y);
        akis.showText(metin);
        akis.endText();
    }

    private void dikdortgenCiz(float x, float y, float w, float h, Color renk) throws IOException {
        akis.setNonStrokingColor(renk);
        akis.addRect(x, y, w, h);
        akis.fill();
    }

    private float metinGenisligi(String metin, PDFont font, float boyut) throws IOException {
        return font.getStringWidth(metin) / 1000f * boyut;
    }

    /** Metni verilen genişliği aşmayacak şekilde kelime kelime satırlara böler. */
    private List<String> satirlaraBol(String metin, PDFont font, float boyut, float maksGenislik)
            throws IOException {
        List<String> satirlar = new ArrayList<>();
        StringBuilder satir = new StringBuilder();
        for (String kelime : metin.trim().split("\\s+")) {
            String deneme = satir.length() == 0 ? kelime : satir + " " + kelime;
            if (metinGenisligi(deneme, font, boyut) <= maksGenislik || satir.length() == 0) {
                satir.setLength(0);
                satir.append(deneme);
            } else {
                satirlar.add(satir.toString());
                satir.setLength(0);
                satir.append(kelime);
            }
        }
        if (satir.length() > 0) {
            satirlar.add(satir.toString());
        }
        return satirlar;
    }
}