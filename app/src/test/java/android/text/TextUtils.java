package android.text;

import com.lauchun.autoanswer.Cat;

/**
 * @author ：lauchun
 * @date ：Created in 3/8/22
 * @description ：
 * @version: 1.0
 */
public class TextUtils {

    public static String str = "";

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }
}
