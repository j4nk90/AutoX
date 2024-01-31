module.exports = function (runtime, scope) {
    importClass(com.stardust.autojs.core.geoip.GeoIP);

    var geoip = {};

    geoip.__ins__ = new GeoIP();

    geoip.setDBPath = function (type, path) {
        return geoip.__ins__.setDBPath(type, path);
    }

    geoip.getDBPath = function (type) {
        return geoip.__ins__.getDBPath(type);
    }

    geoip.GeoIP = function (ip) {
        return geoip.__ins__.getGeoIP(ip);
    }

    return geoip;
}