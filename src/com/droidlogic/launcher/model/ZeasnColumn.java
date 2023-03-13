package com.droidlogic.launcher.model;

import java.util.List;

public class ZeasnColumn {


    /**
     * data : [{"children":[{"children":[],"contentConfig":{"appSortType":0,"dataSize":8,"imgStyle":0,"needPoster":false,"showLiveLogo":false,"videoSortType":0},"contentType":1,"icon":"","id":"637302339929638797","name":"Featured App","tag":"","type":-1}],"contentConfig":{"appSortType":0,"dataSize":0,"needPoster":false,"videoSortType":0},"icon":"","id":"637302933889225792","name":"Home","tag":"Home","type":3}]
     * errorCode : 0
     * timestamp : 1678343578621
     */

    private int errorCode;
    private long timestamp;
    private List<DataBean> data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
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
         * children : [{"children":[],"contentConfig":{"appSortType":0,"dataSize":8,"imgStyle":0,"needPoster":false,"showLiveLogo":false,"videoSortType":0},"contentType":1,"icon":"","id":"637302339929638797","name":"Featured App","tag":"","type":-1}]
         * contentConfig : {"appSortType":0,"dataSize":0,"needPoster":false,"videoSortType":0}
         * icon :
         * id : 637302933889225792
         * name : Home
         * tag : Home
         * type : 3
         */

        private ContentConfigBean contentConfig;
        private String icon;
        private String id;
        private String name;
        private String tag;
        private int type;
        private List<ChildrenBean> children;

        public ContentConfigBean getContentConfig() {
            return contentConfig;
        }

        public void setContentConfig(ContentConfigBean contentConfig) {
            this.contentConfig = contentConfig;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<ChildrenBean> getChildren() {
            return children;
        }

        public void setChildren(List<ChildrenBean> children) {
            this.children = children;
        }

        public static class ContentConfigBean {
            /**
             * appSortType : 0
             * dataSize : 0
             * needPoster : false
             * videoSortType : 0
             */

            private int appSortType;
            private int dataSize;
            private boolean needPoster;
            private int videoSortType;

            public int getAppSortType() {
                return appSortType;
            }

            public void setAppSortType(int appSortType) {
                this.appSortType = appSortType;
            }

            public int getDataSize() {
                return dataSize;
            }

            public void setDataSize(int dataSize) {
                this.dataSize = dataSize;
            }

            public boolean isNeedPoster() {
                return needPoster;
            }

            public void setNeedPoster(boolean needPoster) {
                this.needPoster = needPoster;
            }

            public int getVideoSortType() {
                return videoSortType;
            }

            public void setVideoSortType(int videoSortType) {
                this.videoSortType = videoSortType;
            }

            @Override
            public String toString() {
                return "ContentConfigBean{" +
                        "appSortType=" + appSortType +
                        ", dataSize=" + dataSize +
                        ", needPoster=" + needPoster +
                        ", videoSortType=" + videoSortType +
                        '}';
            }
        }

        public static class ChildrenBean {
            /**
             * children : []
             * contentConfig : {"appSortType":0,"dataSize":8,"imgStyle":0,"needPoster":false,"showLiveLogo":false,"videoSortType":0}
             * contentType : 1
             * icon :
             * id : 637302339929638797
             * name : Featured App
             * tag :
             * type : -1
             */

            private int contentType;
            private String icon;
            private String id;
            private String name;
            private String tag;
            private int type;

            public int getContentType() {
                return contentType;
            }

            public void setContentType(int contentType) {
                this.contentType = contentType;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            @Override
            public String toString() {
                return "ChildrenBean{" +
                        "contentType=" + contentType +
                        ", icon='" + icon + '\'' +
                        ", id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", tag='" + tag + '\'' +
                        ", type=" + type +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "contentConfig=" + contentConfig +
                    ", icon='" + icon + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", tag='" + tag + '\'' +
                    ", type=" + type +
                    ", children=" + children +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "ZeasnColumn{" +
                "errorCode=" + errorCode +
                ", timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}
