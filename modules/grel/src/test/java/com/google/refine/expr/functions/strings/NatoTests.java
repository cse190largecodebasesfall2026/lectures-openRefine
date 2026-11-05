
package com.google.refine.expr.functions.strings;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.refine.expr.EvalError;
import com.google.refine.grel.GrelTestBase;

public class NatoTests extends GrelTestBase {

    @Test
    public void testPlainName() {
        assertEquals(invoke("nato", "Amy"), "Alpha Mike Yankee");
    }

    @Test
    public void testCaseIsIgnored() {
        assertEquals(invoke("nato", "aMY"), "Alpha Mike Yankee");
        assertEquals(invoke("nato", "AMY"), "Alpha Mike Yankee");
    }

    @Test
    public void testAccentsAreFolded() {
        assertEquals(invoke("nato", "José"), "Juliet Oscar Sierra Echo");
        assertEquals(invoke("nato", "Zoë"), "Zulu Oscar Echo");
    }

    @Test
    public void testDigits() {
        assertEquals(invoke("nato", "R2D2"), "Romeo Two Delta Two");
        assertEquals(invoke("nato", 123), "One Two Three");
    }

    @Test
    public void testSpaceBetweenNameParts() {
        assertEquals(invoke("nato", "Al Li"), "Alpha Lima / Lima India");
        assertEquals(invoke("nato", "  Al   Li  "), "Alpha Lima / Lima India");
    }

    @Test
    public void testApostropheAndHyphenAreSkipped() {
        assertEquals(invoke("nato", "O'Neil"), "Oscar November Echo India Lima");
        assertEquals(invoke("nato", "Ann-Bo"), "Alpha November November Bravo Oscar");
    }

    @Test
    public void testOtherCharactersPassThrough() {
        assertEquals(invoke("nato", "A.B"), "Alpha . Bravo");
    }

    @Test
    public void testNullAndEmpty() {
        assertEquals(invoke("nato", (Object) null), "");
        assertEquals(invoke("nato", ""), "");
        assertEquals(invoke("nato", "   "), "");
    }

    @Test
    public void testWrongArgumentCount() {
        assertTrue(invoke("nato") instanceof EvalError);
        assertTrue(invoke("nato", "a", "b") instanceof EvalError);
    }
}
