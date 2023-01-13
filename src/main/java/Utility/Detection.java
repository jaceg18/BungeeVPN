package Utility;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class Detection {
    public String status, node, ip, asn, provider, country, isocode, proxy, type, port, last_seen_human, last_seen_unix, query_time, message, error;
    private String api_key, tag;
    private String api_url = "http://proxycheck.io/v2/";
    private int api_timeout = 5000;
    private int useVpn = 0, useAsn = 0, useNode = 0, useTime = 0, useInf = 0, usePort = 0, useSeen = 0, useDays = 0;

    public Detection(String key) {
        this.api_key = key;
    }

    @SuppressWarnings("unused")
    public Detection(String key, int timeout) {
        this.api_key = key;
        this.api_timeout = timeout;
    }

    public void set_api_key(String key) {
        this.api_key = key;
    }

    public void setUseVpn(boolean var) {
        useVpn = (var) ? 1 : 0;
    }

    @SuppressWarnings("unused")
    public void setUseAsn(boolean var) {
        useAsn = (var) ? 1 : 0;
    }

    @SuppressWarnings("unused")
    public void setUseNode(boolean var) {
        useNode = (var) ? 1 : 0;
    }

    @SuppressWarnings("unused")
    public void setUseTime(boolean var) {
        useTime = (var) ? 1 : 0;
    }

    @SuppressWarnings("unused")
    public void setUseInf(boolean var) {
        useInf = (var) ? 1 : 0;
    }

    @SuppressWarnings("unused")
    public void setUsePort(boolean var) {
        usePort = (var) ? 1 : 0;
    }

    @SuppressWarnings("unused")
    public void setUseSeen(boolean var) {
        useSeen = (var) ? 1 : 0;
    }

    @SuppressWarnings("unused")
    public void setUseDays(int var) {
        useDays = var;
    }

    @SuppressWarnings("unused")
    public void setTag(String var) {
        tag = var;
    }

    @SuppressWarnings("unused")
    public void set_api_timeout(int timeout) {
        this.api_timeout = timeout;
    }

    public void useSSL() {
        this.api_url = this.api_url.replace("http://", "https://");
    }

    public void parseResults(String Ip) throws IOException, ParseException {


        String ipAddress = Ip;


        // If tested locally, IP address must run through 3rd party api

        if (Ip.startsWith("/127.0.0.1")) {
            try {
                URL url = new URL("https://ip.42.pl/json");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    content.append(inputLine);

                in.close();
                ipAddress = content.toString();
            } catch (Exception e) {throw new RuntimeException(e);}
        }


        ipAddress = ipAddress.split(":")[0];

        if (ipAddress.contains("/"))
            ipAddress = ipAddress.split("/")[1];


        String query_url = this.get_query_url(ipAddress);
        String query_result = this.query(query_url, this.api_timeout);

        JSONParser parser = new JSONParser();
        JSONObject main = (JSONObject) parser.parse(query_result);
        JSONObject sub = (JSONObject) main.get(ipAddress);

        ip = ipAddress;

        if (sub == null) return;


        status = (String) main.get("status");
        node = (String) main.get("node");
        asn = (String) sub.get("asn");
        provider = (String) sub.get("provider");
        country = (String) sub.get("country");
        isocode = (String) sub.get("isocode");
        proxy = (String) sub.get("proxy");
        type = (String) sub.get("type");
        port = (String) sub.get("port");
        last_seen_human = (String) sub.get("last seen human");
        last_seen_unix = (String) sub.get("last seen unix");
        query_time = (String) main.get("query time");
        message = (String) main.get("message");
        error = (String) sub.get("error");

    }

    @SuppressWarnings("unused")
    public String getResponseAsString(String ip) throws IOException {
        String query_url = this.get_query_url(ip);
        return this.query(query_url, this.api_timeout);
    }

    public String get_query_url(String ip) {
        return this.api_url + ip + "?key=" + this.api_key + "&vpn=" + useVpn + "&asn=" + useAsn + "&node=" + useNode + "&time=" + useTime
                + "&inf=" + useInf + "&port=" + usePort + "&seen=" + useSeen + "&days=" + useDays + "&tag=" + tag;
    }

    public String query(String url, int timeout) throws IOException {
        StringBuilder response = new StringBuilder();
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        connection.setConnectTimeout(timeout);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", "AntiVPN");
        connection.setRequestProperty("tag", "AntiVPN");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            while ((url = in.readLine()) != null)
                response.append(url);
        }
        return response.toString();
    }
}


