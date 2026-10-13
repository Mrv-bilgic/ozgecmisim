package org.ozgecmis;

import org.ozgecmis.model.Ozgecmis;
import org.ozgecmis.pdf.BelgeOlusturucu;
import org.ozgecmis.pdf.OzgecmisPdfOlusturucu;
import org.ozgecmis.veri.OrnekVeri;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Uygulamanın giriş noktası.
 * Main yalnızca nesneleri oluşturur ve bağlar; PDF oluşturma işi
 * OzgecmisPdfOlusturucu sınıfında yapılır.
 */
public class Main {

    private static final String VARSAYILAN_CIKTI = "cikti/ozgecmis.pdf";

    public static void main(String[] args) {
        Path ciktiYolu = Path.of(args.length > 0 ? args[0] : VARSAYILAN_CIKTI);

        // 1) Veri modelini oluştur
        Ozgecmis ozgecmis = OrnekVeri.ozgecmisOlustur();

        // 2) Değişken tipi arayüz, nesne tipi somut sınıf
        BelgeOlusturucu olusturucu = new OzgecmisPdfOlusturucu();

        // 3) PDF'i üret
        try {
            olusturucu.olustur(ozgecmis, ciktiYolu);
            System.out.println("Özgeçmiş başarıyla oluşturuldu: " + ciktiYolu.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("PDF oluşturulurken hata oluştu: " + e.getMessage());
            System.exit(1);
        }
    }
}