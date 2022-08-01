package com.lauchun.autoanswer;


import android.net.Uri;
import android.telephony.SubscriptionManager;

/**
 * @author ：lauchun
 * @date ：Created in 3/8/22
 * @description ：
 * @version: 1.0
 */
public class Test {

    @org.junit.Test
    public void equals() {
        Cat cat = new Cat();
        String str1 = cat.str;

        String[] str2 = extractUrisFromPipeSeparatedUriStrings(str1);
        for (String s : str2) {
            System.out.println(s);
        }
        System.out.println(str2[1]);
    }

    private String[] extractUrisFromPipeSeparatedUriStrings(String combinedUris) {
        if (combinedUris == null || combinedUris.length() <= 1) return null;
        String[] uriStrings = combinedUris.split(":");
        return uriStrings;
    }
}
