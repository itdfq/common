package com.itdfq.common.utils;

/**
 * @author dfq 2025/3/26 14:30
 * @implNote
 */
public class StrUtils {

    /**
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StrUtils.isNotBlank(null)      = false
     * StrUtils.isNotBlank("")        = false
     * StrUtils.isNotBlank(" ")       = false
     * StrUtils.isNotBlank("bob")     = true
     * StrUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StrUtils.isBlank(null)      = true
     * StrUtils.isBlank("")        = true
     * StrUtils.isBlank(" ")       = true
     * StrUtils.isBlank("bob")     = false
     * StrUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static  boolean isBlank(String str){
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

}
