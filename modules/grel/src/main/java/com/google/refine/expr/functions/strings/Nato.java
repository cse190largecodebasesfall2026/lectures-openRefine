/*******************************************************************************
 * Copyright (C) 2026, OpenRefine contributors
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package com.google.refine.expr.functions.strings;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.refine.expr.EvalError;
import com.google.refine.grel.ControlFunctionRegistry;
import com.google.refine.grel.EvalErrorMessage;
import com.google.refine.grel.Function;
import com.google.refine.grel.FunctionDescription;

/**
 * Spells a string using the NATO phonetic alphabet, for example "Amy" becomes "Alpha Mike Yankee".
 */
public class Nato implements Function {

    private static final String[] LETTERS = {
            "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "India", "Juliet",
            "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango",
            "Uniform", "Victor", "Whiskey", "Xray", "Yankee", "Zulu"
    };

    private static final String[] DIGITS = {
            "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"
    };

    private static final String SEPARATOR = "/";

    @Override
    public Object call(Properties bindings, Object[] args) {
        if (args.length != 1) {
            return new EvalError(EvalErrorMessage.expects_one_string(ControlFunctionRegistry.getFunctionName(this)));
        }
        Object o = args[0];
        if (o == null) {
            return "";
        }
        return expand(o.toString());
    }

    static String expand(String s) {
        // Fold accented letters to their base letter by dropping combining marks
        String folded = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        List<String> words = new ArrayList<>();
        boolean pendingSeparator = false;
        for (int i = 0; i < folded.length();) {
            int cp = folded.codePointAt(i);
            i += Character.charCount(cp);
            String word;
            if (Character.isWhitespace(cp)) {
                // Only put a separator between two words, never at the start, the end or twice in a row
                pendingSeparator = !words.isEmpty();
                continue;
            } else if (cp == '\'' || cp == '’' || cp == '‘' || cp == '-' || cp == '‐' || cp == '–') {
                continue;
            } else if (cp >= 'a' && cp <= 'z') {
                word = LETTERS[cp - 'a'];
            } else if (cp >= 'A' && cp <= 'Z') {
                word = LETTERS[cp - 'A'];
            } else if (cp >= '0' && cp <= '9') {
                word = DIGITS[cp - '0'];
            } else {
                word = new String(Character.toChars(cp));
            }
            if (pendingSeparator) {
                words.add(SEPARATOR);
                pendingSeparator = false;
            }
            words.add(word);
        }
        return String.join(" ", words);
    }

    @Override
    public String getDescription() {
        return FunctionDescription.str_nato();
    }

    @Override
    public String getParams() {
        return "string s";
    }

    @Override
    public String getReturns() {
        return "string";
    }
}
