package com.emudhra.Registration.generator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.MultiFormatWriter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class QrCodeGenerator {

    public static String generateQRCode(String text) throws Exception {

        BitMatrix matrix = new MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                250,
                250
        );

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(
                matrix,
                "PNG",
                baos
        );

        return Base64.getEncoder()
                .encodeToString(baos.toByteArray());
    }
}