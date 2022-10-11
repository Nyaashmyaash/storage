package com.cdek.storage.utils;

public class RequestTestUtils {

    public static String createFixDateOfReceiptRequest() {
        return "{\n" +
                "    \"orderNumbers\": [\n" +
                "        \"1107841747\"\n" +
                "    ],\n" +
                "    \"tariffMode\": \"1\"\n" +
                "}";
    }
}
