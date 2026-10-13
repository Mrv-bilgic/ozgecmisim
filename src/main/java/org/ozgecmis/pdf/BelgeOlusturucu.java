package org.ozgecmis.pdf;

import org.ozgecmis.model.Ozgecmis;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Bir özgeçmişten belge üreten sınıfların uyması gereken sözleşme (interface).
 */
public interface BelgeOlusturucu {

    void olustur(Ozgecmis ozgecmis, Path ciktiYolu) throws IOException;
}