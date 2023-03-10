package com.nbird.call_random.CALL.MODEL;

public class AgoraData {
    String appId,appCertificate;

    public AgoraData() {
    }

    public AgoraData(String appId, String appCertificate) {
        this.appId = appId;
        this.appCertificate = appCertificate;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppCertificate() {
        return appCertificate;
    }

    public void setAppCertificate(String appCertificate) {
        this.appCertificate = appCertificate;
    }
}
