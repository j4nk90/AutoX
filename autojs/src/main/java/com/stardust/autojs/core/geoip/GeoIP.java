package com.stardust.autojs.core.geoip;

import android.util.Log;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;

import org.json.JSONObject;

public class GeoIP {
    private static final String TAG = "GeoIP";
    private Map<String, String> db = new HashMap<>();
    private Map<String, DatabaseReader> reader = new HashMap<>();
    public GeoIP() {
        try {
            db.put("asn", "/sdcard/Download/GeoLite2-ASN.mmdb");
            db.put("city", "/sdcard/Download/GeoLite2-City.mmdb");
            db.put("country", "/sdcard/Download/GeoLite2-Country.mmdb");
            rebuildReader();
        }
        catch (Exception e) {
            Log.e(TAG, "GeoIP: ", e);
        }
    }


    public String getDBPath(String type) {
        try {
            return db.get(type);
        }
        catch (Exception e) {
            Log.e(TAG, "getDBPath: ", e);
            return null;
        }
    }
    public boolean setDBPath(String type, String path) {
        try {
            if (path != null) {
                db.put(type, path);
            }
            rebuildReader();
            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "setDBPath: ", e);
            return false;
        }
    }

    private void rebuildReader(){
        for (String type : db.keySet()) {
            try {
                DatabaseReader reader = (DatabaseReader) this.reader.get(type);
                if (reader != null) {
                    reader.close();
                    this.reader.put(type, null);
                }
                this.reader.put(type, new DatabaseReader.Builder(new File(getDBPath(type))).build());
            }
            catch (Exception e) {
                Log.e(TAG, "rebuildReader: ", e);
            }
        }
    }

    public JSONObject getGeoIP(String ip) {
        JSONObject json = new JSONObject();
        try {
            DatabaseReader asnReader = reader.get("asn");
            DatabaseReader countryReader = reader.get("country");

            if (asnReader==null || countryReader==null) {
                throw new Exception("数据库加载失败，请使用 setDBPath(type, path) 方法设置正确的路径。");
            }

            AsnResponse response = asnReader.asn(InetAddress.getByName(ip));
            json.put("ip", ip);
            json.put("asn", response.getAutonomousSystemNumber());
            CountryResponse countryResponse = countryReader.country(InetAddress.getByName(ip));
            String country = countryResponse.getCountry().getNames().get("zh-CN");
            if (country == null || country.isEmpty()) {
                country = countryResponse.getCountry().getNames().get("en");
            }
            if (country == null || country.isEmpty()) {
                country = countryResponse.getRegisteredCountry().getNames().get("zh-CN");
            }
            if (country == null || country.isEmpty()) {
                country = countryResponse.getRegisteredCountry().getNames().get("en");
            }
            json.put("country", country);
            json.put("status", true);
        } catch (Exception e) {
            Log.e(TAG, "getGeoIP: ", e);
            try {
                json.put("status", false);
            } catch (Exception e2) {
                Log.e(TAG, "getGeoIP: ", e2);
            }
        }
        return json;
    }
}
