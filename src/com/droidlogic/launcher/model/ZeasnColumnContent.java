package com.droidlogic.launcher.model;

import java.util.List;

public class ZeasnColumnContent {


    /**
     * data : [{"content":{"dataList":[{"adultLock":false,"attribution":"NetTV","briefDesc":"YouTube your way on the biggest screen in the house, from a playlist of music videos to your","deeplink":{"linux":""},"downloads":"44989","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255198113_217b93c6-acf4-4c0a-995c-1f12bc6a6d46.jpg","iconActive":"","name":"YouTube","pkg":"com.google.android.youtube.tv","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20001360\",\"appvsId\":\"637379237342939429\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Netflix is the world's leading subscription service for watching TV episodes and movies on your","deeplink":{},"downloads":"9425","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255225404_e2b4b610-6dba-45bb-b6e8-8070d937e2cf.jpg","iconActive":"","name":"Netflix","pkg":"com.netflix.mediaclient","poster":"","releaseTime":"1678245839127","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20005570\",\"appvsId\":\"20011189\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"HBO Max is the streaming platform that bundles all of HBO together with even more of your favorite","deeplink":{"linux":""},"downloads":"113227","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255337835_4f22bb04-9bfb-46fb-9376-5a513d3a5489.png","iconActive":"","name":"HBO Max","pkg":"com.hbo.hbonow","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20004691\",\"appvsId\":\"20017785\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Only Disney+ gives you endless access to your favorite movies and TV series from Disney, Pixar,","deeplink":{"linux":""},"downloads":"172521","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255363957_c7308708-945c-4090-99a2-f10bd6c92e31.png","iconActive":"","name":"Disney+","pkg":"com.disney.disneyplus","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20005668\",\"appvsId\":\"538832187773486343\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Get all your favorite TV, all in one app.\nWatch critically acclaimed Apple Original series and films","deeplink":{"linux":""},"downloads":"0","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255388525_962f6d4e-6593-4edc-976a-7f9caa69717d.png","iconActive":"","name":"Apple TV","pkg":"com.apple.atve.androidtv.appletv","poster":"","releaseTime":"1678255043065","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20006963\",\"appvsId\":\"616291643129603319\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Prime Video gives you two ways to instantly stream Videos on your Android TV device. Buy or rent","deeplink":{"linux":""},"downloads":"46317","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255466533_c188bbed-5cbf-4c2d-9cb1-eefc8b69312c.jpg","iconActive":"","name":"Prime Video - Android TV","pkg":"com.amazon.amazonvideo.livingroom","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20004855\",\"appvsId\":\"20017029\"}"},{"adultLock":false,"briefDesc":"Keep in touch with friends faster than ever. - look at what your friends are doing - share updates,","deeplink":{},"downloads":"156753","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255516731_05448c03-3de3-42df-b5fb-7dcf94585c4a.png","iconActive":"","name":"Facebook","pkg":"com.facebook.katana","poster":"","releaseTime":"1678247068395","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20001207\",\"appvsId\":\"20017934\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Spotify is now free on mobile and tablet. Listen to the right music, wherever you are.\n \n With","deeplink":{},"downloads":"19098","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255569759_5d03480c-4659-4ce7-813d-3cebf8d7ba06.jpg","iconActive":"","name":"SpotifyTV","pkg":"com.spotify.tv.android","poster":"","releaseTime":"1678245883614","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20002011\",\"appvsId\":\"20017661\"}"}],"name":"Featured App","resourceStatus":1,"type":"Banner"},"id":"637302339929638797"}]
     * errorCode : 0
     * timestamp : 1678333487170
     */

    private int errorCode;
    private String errorMsg;
    private long timestamp;
    private List<DataBean> data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * content : {"dataList":[{"adultLock":false,"attribution":"NetTV","briefDesc":"YouTube your way on the biggest screen in the house, from a playlist of music videos to your","deeplink":{"linux":""},"downloads":"44989","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255198113_217b93c6-acf4-4c0a-995c-1f12bc6a6d46.jpg","iconActive":"","name":"YouTube","pkg":"com.google.android.youtube.tv","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20001360\",\"appvsId\":\"637379237342939429\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Netflix is the world's leading subscription service for watching TV episodes and movies on your","deeplink":{},"downloads":"9425","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255225404_e2b4b610-6dba-45bb-b6e8-8070d937e2cf.jpg","iconActive":"","name":"Netflix","pkg":"com.netflix.mediaclient","poster":"","releaseTime":"1678245839127","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20005570\",\"appvsId\":\"20011189\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"HBO Max is the streaming platform that bundles all of HBO together with even more of your favorite","deeplink":{"linux":""},"downloads":"113227","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255337835_4f22bb04-9bfb-46fb-9376-5a513d3a5489.png","iconActive":"","name":"HBO Max","pkg":"com.hbo.hbonow","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20004691\",\"appvsId\":\"20017785\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Only Disney+ gives you endless access to your favorite movies and TV series from Disney, Pixar,","deeplink":{"linux":""},"downloads":"172521","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255363957_c7308708-945c-4090-99a2-f10bd6c92e31.png","iconActive":"","name":"Disney+","pkg":"com.disney.disneyplus","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20005668\",\"appvsId\":\"538832187773486343\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Get all your favorite TV, all in one app.\nWatch critically acclaimed Apple Original series and films","deeplink":{"linux":""},"downloads":"0","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255388525_962f6d4e-6593-4edc-976a-7f9caa69717d.png","iconActive":"","name":"Apple TV","pkg":"com.apple.atve.androidtv.appletv","poster":"","releaseTime":"1678255043065","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20006963\",\"appvsId\":\"616291643129603319\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Prime Video gives you two ways to instantly stream Videos on your Android TV device. Buy or rent","deeplink":{"linux":""},"downloads":"46317","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255466533_c188bbed-5cbf-4c2d-9cb1-eefc8b69312c.jpg","iconActive":"","name":"Prime Video - Android TV","pkg":"com.amazon.amazonvideo.livingroom","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20004855\",\"appvsId\":\"20017029\"}"},{"adultLock":false,"briefDesc":"Keep in touch with friends faster than ever. - look at what your friends are doing - share updates,","deeplink":{},"downloads":"156753","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255516731_05448c03-3de3-42df-b5fb-7dcf94585c4a.png","iconActive":"","name":"Facebook","pkg":"com.facebook.katana","poster":"","releaseTime":"1678247068395","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20001207\",\"appvsId\":\"20017934\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Spotify is now free on mobile and tablet. Listen to the right music, wherever you are.\n \n With","deeplink":{},"downloads":"19098","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255569759_5d03480c-4659-4ce7-813d-3cebf8d7ba06.jpg","iconActive":"","name":"SpotifyTV","pkg":"com.spotify.tv.android","poster":"","releaseTime":"1678245883614","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20002011\",\"appvsId\":\"20017661\"}"}],"name":"Featured App","resourceStatus":1,"type":"Banner"}
         * id : 637302339929638797
         */

        private ContentBean content;
        private String id;

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public static class ContentBean {
            /**
             * dataList : [{"adultLock":false,"attribution":"NetTV","briefDesc":"YouTube your way on the biggest screen in the house, from a playlist of music videos to your","deeplink":{"linux":""},"downloads":"44989","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255198113_217b93c6-acf4-4c0a-995c-1f12bc6a6d46.jpg","iconActive":"","name":"YouTube","pkg":"com.google.android.youtube.tv","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20001360\",\"appvsId\":\"637379237342939429\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Netflix is the world's leading subscription service for watching TV episodes and movies on your","deeplink":{},"downloads":"9425","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255225404_e2b4b610-6dba-45bb-b6e8-8070d937e2cf.jpg","iconActive":"","name":"Netflix","pkg":"com.netflix.mediaclient","poster":"","releaseTime":"1678245839127","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20005570\",\"appvsId\":\"20011189\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"HBO Max is the streaming platform that bundles all of HBO together with even more of your favorite","deeplink":{"linux":""},"downloads":"113227","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255337835_4f22bb04-9bfb-46fb-9376-5a513d3a5489.png","iconActive":"","name":"HBO Max","pkg":"com.hbo.hbonow","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20004691\",\"appvsId\":\"20017785\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Only Disney+ gives you endless access to your favorite movies and TV series from Disney, Pixar,","deeplink":{"linux":""},"downloads":"172521","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255363957_c7308708-945c-4090-99a2-f10bd6c92e31.png","iconActive":"","name":"Disney+","pkg":"com.disney.disneyplus","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20005668\",\"appvsId\":\"538832187773486343\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Get all your favorite TV, all in one app.\nWatch critically acclaimed Apple Original series and films","deeplink":{"linux":""},"downloads":"0","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255388525_962f6d4e-6593-4edc-976a-7f9caa69717d.png","iconActive":"","name":"Apple TV","pkg":"com.apple.atve.androidtv.appletv","poster":"","releaseTime":"1678255043065","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20006963\",\"appvsId\":\"616291643129603319\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Prime Video gives you two ways to instantly stream Videos on your Android TV device. Buy or rent","deeplink":{"linux":""},"downloads":"46317","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255466533_c188bbed-5cbf-4c2d-9cb1-eefc8b69312c.jpg","iconActive":"","name":"Prime Video - Android TV","pkg":"com.amazon.amazonvideo.livingroom","poster":"","releaseTime":"1678245797586","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20004855\",\"appvsId\":\"20017029\"}"},{"adultLock":false,"briefDesc":"Keep in touch with friends faster than ever. - look at what your friends are doing - share updates,","deeplink":{},"downloads":"156753","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255516731_05448c03-3de3-42df-b5fb-7dcf94585c4a.png","iconActive":"","name":"Facebook","pkg":"com.facebook.katana","poster":"","releaseTime":"1678247068395","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20001207\",\"appvsId\":\"20017934\"}"},{"adultLock":false,"attribution":"NetTV","briefDesc":"Spotify is now free on mobile and tablet. Listen to the right music, wherever you are.\n \n With","deeplink":{},"downloads":"19098","icon":"https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255569759_5d03480c-4659-4ce7-813d-3cebf8d7ba06.jpg","iconActive":"","name":"SpotifyTV","pkg":"com.spotify.tv.android","poster":"","releaseTime":"1678245883614","resolutions":["0"],"resourceStatus":1,"rsType":6,"value":"{\"appId\":\"20002011\",\"appvsId\":\"20017661\"}"}]
             * name : Featured App
             * resourceStatus : 1
             * type : Banner
             */

            private String name;
            private int resourceStatus;
            private String type;
            private List<DataListBean> dataList;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getResourceStatus() {
                return resourceStatus;
            }

            public void setResourceStatus(int resourceStatus) {
                this.resourceStatus = resourceStatus;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<DataListBean> getDataList() {
                return dataList;
            }

            public void setDataList(List<DataListBean> dataList) {
                this.dataList = dataList;
            }

            public static class DataListBean {
                /**
                 * adultLock : false
                 * attribution : NetTV
                 * briefDesc : YouTube your way on the biggest screen in the house, from a playlist of music videos to your
                 * deeplink : {"linux":""}
                 * downloads : 44989
                 * icon : https://cache.zeasn.tv/prod/zeasn-saas-sp/sp/pic/1678255198113_217b93c6-acf4-4c0a-995c-1f12bc6a6d46.jpg
                 * iconActive :
                 * name : YouTube
                 * pkg : com.google.android.youtube.tv
                 * poster :
                 * releaseTime : 1678245797586
                 * resolutions : ["0"]
                 * resourceStatus : 1
                 * rsType : 6
                 * value : {"appId":"20001360","appvsId":"637379237342939429"}
                 */

                private boolean adultLock;
                private String attribution;
                private String briefDesc;
                private DeeplinkBean deeplink;
                private String downloads;
                private String icon;
                private String iconActive;
                private String name;
                private String pkg;
                private String poster;
                private String releaseTime;
                private int resourceStatus;
                private int rsType;
                private String value;
                private List<String> resolutions;

                public boolean isAdultLock() {
                    return adultLock;
                }

                public void setAdultLock(boolean adultLock) {
                    this.adultLock = adultLock;
                }

                public String getAttribution() {
                    return attribution;
                }

                public void setAttribution(String attribution) {
                    this.attribution = attribution;
                }

                public String getBriefDesc() {
                    return briefDesc;
                }

                public void setBriefDesc(String briefDesc) {
                    this.briefDesc = briefDesc;
                }

                public DeeplinkBean getDeeplink() {
                    return deeplink;
                }

                public void setDeeplink(DeeplinkBean deeplink) {
                    this.deeplink = deeplink;
                }

                public String getDownloads() {
                    return downloads;
                }

                public void setDownloads(String downloads) {
                    this.downloads = downloads;
                }

                public String getIcon() {
                    return icon;
                }

                public void setIcon(String icon) {
                    this.icon = icon;
                }

                public String getIconActive() {
                    return iconActive;
                }

                public void setIconActive(String iconActive) {
                    this.iconActive = iconActive;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPkg() {
                    return pkg;
                }

                public void setPkg(String pkg) {
                    this.pkg = pkg;
                }

                public String getPoster() {
                    return poster;
                }

                public void setPoster(String poster) {
                    this.poster = poster;
                }

                public String getReleaseTime() {
                    return releaseTime;
                }

                public void setReleaseTime(String releaseTime) {
                    this.releaseTime = releaseTime;
                }

                public int getResourceStatus() {
                    return resourceStatus;
                }

                public void setResourceStatus(int resourceStatus) {
                    this.resourceStatus = resourceStatus;
                }

                public int getRsType() {
                    return rsType;
                }

                public void setRsType(int rsType) {
                    this.rsType = rsType;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public List<String> getResolutions() {
                    return resolutions;
                }

                public void setResolutions(List<String> resolutions) {
                    this.resolutions = resolutions;
                }

                public static class DeeplinkBean {
                    /**
                     * linux :
                     */

                    private String linux;

                    public String getLinux() {
                        return linux;
                    }

                    public void setLinux(String linux) {
                        this.linux = linux;
                    }

                    @Override
                    public String toString() {
                        return "DeeplinkBean{" +
                                "linux='" + linux + '\'' +
                                '}';
                    }
                }

                @Override
                public String toString() {
                    return "DataListBean{" +
                            "adultLock=" + adultLock +
                            ", attribution='" + attribution + '\'' +
                            ", briefDesc='" + briefDesc + '\'' +
                            ", deeplink=" + deeplink +
                            ", downloads='" + downloads + '\'' +
                            ", icon='" + icon + '\'' +
                            ", iconActive='" + iconActive + '\'' +
                            ", name='" + name + '\'' +
                            ", pkg='" + pkg + '\'' +
                            ", poster='" + poster + '\'' +
                            ", releaseTime='" + releaseTime + '\'' +
                            ", resourceStatus=" + resourceStatus +
                            ", rsType=" + rsType +
                            ", value='" + value + '\'' +
                            ", resolutions=" + resolutions +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "ContentBean{" +
                        "name='" + name + '\'' +
                        ", resourceStatus=" + resourceStatus +
                        ", type='" + type + '\'' +
                        ", dataList=" + dataList +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "content=" + content +
                    ", id='" + id + '\'' +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "ZeasnColumnContent{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}
