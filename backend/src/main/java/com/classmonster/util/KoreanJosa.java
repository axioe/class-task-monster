package com.classmonster.util;

/**
 * 이름 끝 글자의 받침 여부에 따라 조사를 붙인다. (예: 몽글이가 / 몽글곰이)
 */
public final class KoreanJosa {

    private KoreanJosa() {
    }

    /** 이/가 */
    public static String subject(String word) {
        return word + pick(word, "이", "가");
    }

    /** 과/와 */
    public static String with(String word) {
        return word + pick(word, "과", "와");
    }

    private static String pick(String word, String withFinal, String withoutFinal) {
        if (word == null || word.isEmpty()) {
            return withoutFinal;
        }
        char last = word.charAt(word.length() - 1);
        if (last < 0xAC00 || last > 0xD7A3) {
            return withoutFinal;
        }
        return (last - 0xAC00) % 28 == 0 ? withoutFinal : withFinal;
    }
}
