
package com.library.util;

import java.util.Random;

public class BarcodeGenerator {

    private static final Random RANDOM = new Random();

    public static String generateCardNo() {
        return "C" + String.format("%03d", RANDOM.nextInt(900) + 100);
    }

    public static String generateBarcode(String isbn, int sequence) {
        return isbn + String.format("%04d", sequence);
    }

    public static String generateBarcode(String isbn) {
        int count = RANDOM.nextInt(9999) + 1;
        return isbn + String.format("%04d", count);
    }
}
