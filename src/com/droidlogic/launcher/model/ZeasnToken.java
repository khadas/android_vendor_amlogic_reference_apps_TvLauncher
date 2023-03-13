package com.droidlogic.launcher.model;

public class ZeasnToken{


    /**
     * data : {"expiredAt":"1678355533674","serviceList":[],"ifCheckServiceList":"true","token":"11b9d5017ebd534e42831ac5ffbd44c910.40.aml950d..080027f7f814"}
     * errorCode : 0
     * timestamp : 1678343564674
     */

    private TokenBean data;
    private String errorCode;
    private String timestamp;

    public TokenBean getData() {
        return data;
    }

    public void setData(TokenBean data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class TokenBean {
        /**
         * expiredAt : 1678355533674
         * serviceList : []
         * ifCheckServiceList : true
         * token : 11b9d5017ebd534e42831ac5ffbd44c910.40.aml950d..080027f7f814
         */

        private String expiredAt;
        private String ifCheckServiceList;
        private String token;

        public String getExpiredAt() {
            return expiredAt;
        }

        public void setExpiredAt(String expiredAt) {
            this.expiredAt = expiredAt;
        }

        public String getIfCheckServiceList() {
            return ifCheckServiceList;
        }

        public void setIfCheckServiceList(String ifCheckServiceList) {
            this.ifCheckServiceList = ifCheckServiceList;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "TokenBean{" +
                    "expiredAt='" + expiredAt + '\'' +
                    ", ifCheckServiceList='" + ifCheckServiceList + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }
    }

}
