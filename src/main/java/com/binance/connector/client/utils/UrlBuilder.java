package com.binance.connector.client.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


public final class UrlBuilder {
    private static final int DIFF_TILL_POSITION_INDEX = 1;
    private static final int MAX_DECIMAL_DIGITS = 30;
    private static DecimalFormat df;

    private UrlBuilder() {
    }

    public static String buildFullUrl(String baseUrl, String urlPath, Map<String, Object> parameters) {
        StringBuilder sb = new StringBuilder(baseUrl).append(urlPath);
        if (parameters != null && !parameters.isEmpty()) {
            sb.append("?");
            sb.append(joinQueryParameters(parameters));
        }
        return sb.toString();
    }

    public static String buildStreamUrl(String baseUrl, ArrayList<String> streams) {
        StringBuilder sb = new StringBuilder(baseUrl).append("/stream");
        if (streams != null && !streams.isEmpty()) {
            sb.append("?streams=");
            sb.append(joinStreamUrls(streams));
        }
        return sb.toString();
    }

    /**
     * Joins query parameters from a Map into a String representation.
     * @param params The Map containing the query parameters.
     * @return The String representation of the joined query parameters.
    */
    public static String joinQueryParameters(Map<String, Object> params) {
        return joinQueryParameters(new StringBuilder(), params).toString();
    }

    /**
     * Joins query parameters from a Map into a StringBuilder representation.
     * @param sb The StringBuilder to append the query parameters to.
     * @param params The Map containing the query parameters.
     * @return The StringBuilder representation of the joined query parameters.
    */
    @SuppressWarnings({ "unchecked" })
    public static StringBuilder joinQueryParameters(StringBuilder sb, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            Iterator<String> keys = params.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                sb.append(key);
                sb.append("=");

                String value;
                if (params.get(key) instanceof Double) {
                    value = getFormatter().format(params.get(key));
                } else if (params.get(key) instanceof List list) {
                    value = (String) list.stream().map(Object::toString).collect(Collectors.joining(","));
                } else {
                    value = params.get(key).toString();
                }
                sb.append(urlEncode(value));

                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - DIFF_TILL_POSITION_INDEX);
        }
        return sb;
    }

    /**
     * Joins streams from an ArrayList into a String representation.
     * @param params The ArrayList containing the streams.
     * @return The String representation of the joined streams.
    */
    public static String joinStreamUrls(ArrayList<String> streams) {
        return joinStreamUrls(new StringBuilder(), streams).toString();
    }

    /**
     * Joins streams from an ArrayList into a StringBuilder representation.
     * @param sb The StringBuilder to append the streams to.
     * @param params The ArrayList containing the streams.
     * @return The StringBuilder representation of the joined streams.
    */
    public static StringBuilder joinStreamUrls(StringBuilder sb, ArrayList<String> streams) {

        if (streams != null && !streams.isEmpty()) {
            for (String stream: streams) {
                sb.append(stream);
                sb.append("/");
            }
            sb.deleteCharAt(sb.length() - DIFF_TILL_POSITION_INDEX);
        }
        return sb;
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(StandardCharsets.UTF_8.name() + " is unsupported", e);
        }
    }

    public static String buildTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    private static DecimalFormat getFormatter() {
        if (null == df) {
            // Overrides the default Locale
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
            df = new DecimalFormat("#,##0.###", symbols);
            df.setMaximumFractionDigits(MAX_DECIMAL_DIGITS);
            df.setGroupingUsed(false);
        }
        return df;
    }

}
