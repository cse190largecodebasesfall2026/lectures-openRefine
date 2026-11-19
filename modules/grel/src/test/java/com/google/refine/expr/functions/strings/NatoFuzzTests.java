package com.google.refine.expr.functions.strings;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Random;

import org.testng.annotations.Test;

import com.google.refine.grel.GrelTestBase;

public class NatoFuzzTests extends GrelTestBase {

    private static final int RUNS = 10000;
    private static final long SEED = 190;

    private static final String ASCII = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 .,'-";
    private static final String[] UNICODE_PIECES = {
            "a", "Z", "7", " ", "\t", ".", "'", "-", "é", "ö", "ñ", "ß", "’", "–", "中", "😀", "🎉", "𝓐"
    };

    /** A random string of letters, digits, spaces and a little punctuation. */
    static String randomAscii(Random random) {
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(12);
        for (int i = 0; i < length; i++) {
            sb.append(ASCII.charAt(random.nextInt(ASCII.length())));
        }
        return sb.toString();
    }

    /** A random string built from pieces that include accents, other scripts and emoji. */
    static String randomUnicode(Random random) {
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(12);
        for (int i = 0; i < length; i++) {
            sb.append(UNICODE_PIECES[random.nextInt(UNICODE_PIECES.length)]);
        }
        return sb.toString();
    }

    @Test
    public void fuzzNeverThrows() {
        Random random = new Random(SEED);
        for (int run = 0; run < RUNS; run++) {
            String input = randomAscii(random);
            invoke("nato", input);
        }
    }

    @Test
    public void fuzzNoStraySpaces() {
        Random random = new Random(SEED);
        for (int run = 0; run < RUNS; run++) {
            String input = randomAscii(random);
            String output = (String) invoke("nato", input);
            String message = "input [" + input + "] gave [" + output + "]";
            assertFalse(output.startsWith(" "), message);
            assertFalse(output.endsWith(" "), message);
            assertFalse(output.contains("  "), message);
        }
    }

    @Test
    public void fuzzNoBrokenCharacters() {
        Random random = new Random(SEED);
        for (int run = 0; run < RUNS; run++) {
            String input = randomUnicode(random);
            String output = (String) invoke("nato", input);
            String message = "input [" + input + "] gave [" + output + "]";
            assertTrue(output.codePoints().noneMatch(cp -> cp >= 0xD800 && cp <= 0xDFFF), message);
        }
    }
}
