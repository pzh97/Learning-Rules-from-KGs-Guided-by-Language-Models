package de.mpii.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;

public class Crawler {
    public static int CONNECT_TIME_OUT = 10 * 1000;
    public static int READ_TIME_OUT = 300 * 1000;
    public static int NUM_RETRY_CONNECTION = 3;

    private static final int MAX_CONTENT_LENGTH = 1024 * 1024 * 1024; // max content length: 1G
    private static final int BUFFER_SIZE = 8 * 1024; // buffer size: 8K

    private static final boolean FOLLOW_REDIRECT = true;

    private static final String J_CONNECTION = "close";
    private static final String J_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like " +
            "Gecko) Chrome/35.0.1916.153 Safari/537.36";
    private static final String J_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static final String J_ACCEPT_CHARSET = "UTF-8,iso-8859-1;q=0.7,*;q=0.7";
    private static final String J_ACCEPT_ENCODING = "gzip,deflate,sdch";
    private static final String J_ACCEPT_LANGUAGE = "vi-VN,vi;q=0.8,fr-FR;q=0.6,fr;q=0.4,en-US;q=0.2,en;q=0.2";

    private static HttpURLConnection connect(URL url, String method) {
        try {
            URLConnection ucon;
            ucon = url.openConnection();
            HttpURLConnection conn = (HttpURLConnection) ucon;
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(true);
            conn.setRequestMethod(method);

            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);

            HttpURLConnection.setFollowRedirects(FOLLOW_REDIRECT);
            conn.setInstanceFollowRedirects(FOLLOW_REDIRECT);

            conn.addRequestProperty("Connection", J_CONNECTION);
            conn.addRequestProperty("User-Agent", J_USER_AGENT);
            conn.addRequestProperty("Accept", J_ACCEPT);
            conn.addRequestProperty("Accept-Charset", J_ACCEPT_CHARSET);
            conn.addRequestProperty("Accept-Encoding", J_ACCEPT_ENCODING);
            conn.addRequestProperty("Accept-Language", J_ACCEPT_LANGUAGE);

            conn.setDoOutput(true);
            conn.connect();
            return conn;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getContentFromUrl(String url) {
        return getContentFromUrl(url, "GET");
    }
	
    public static String getContentFromUrl(String url, String method) {
        boolean useGZip = false;
        int nRetry =  NUM_RETRY_CONNECTION;
        for (int i = 0; i < nRetry; i++) {
            try {
                HttpURLConnection hc = connect(new URL(url), method);
                int content_length = hc.getContentLength();
                if ((content_length > MAX_CONTENT_LENGTH) || (content_length == -1)) {
                    content_length = MAX_CONTENT_LENGTH;
                }

                String contentEncoding = null;
                try {
                    contentEncoding = hc.getContentEncoding();
                } catch (Exception ex) {
                }
                if (contentEncoding != null && contentEncoding.equals("gzip")) useGZip = true;
                StringBuilder sb = new StringBuilder();
                int c = 0;
                InputStream is = null;
                int responseCode = hc.getResponseCode();

                if (responseCode != -1 && hc.getResponseCode() < 400) {
                    is = hc.getInputStream();
                } else {
                    is = hc.getErrorStream();
                }

                if (useGZip) {
                    is = new GZIPInputStream(is);
                    content_length = (MAX_CONTENT_LENGTH / 8) * 6;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                char[] ch = new char[BUFFER_SIZE];

                while (c < content_length) {
                    int t = br.read(ch, 0, ch.length);
                    if (t >= 0) {
                        sb.append(ch, 0, t);
                        c = c + t;
                    } else {
                        break;
                    }
                }

                br.close();
                return sb.toString();
            } catch (Exception ex) {
            }
        }
        return null;
    }
}
	
