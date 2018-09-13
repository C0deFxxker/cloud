package com.lyl.study.cloud.cms.api;

public class CmsConstants {
    public static final int MEDIA_TYPE_IMAGE_INDEX = 1;
    public static final int MEDIA_TYPE_AUDIO_INDEX = 2;
    public static final int MEDIA_TYPE_VIDEO_INDEX = 3;

    public static final String MEDIA_TYPE_IMAGE = "image/*";
    public static final String MEDIA_TYPE_AUDIO = "audio/*";
    public static final String MEDIA_TYPE_VIDEO = "video/*";

    public static String getMediaTypeByIdx(int mediaTypeIdx) {
        switch (mediaTypeIdx) {
            case MEDIA_TYPE_IMAGE_INDEX:
                return MEDIA_TYPE_IMAGE;
            case MEDIA_TYPE_AUDIO_INDEX:
                return MEDIA_TYPE_AUDIO;
            case MEDIA_TYPE_VIDEO_INDEX:
                return MEDIA_TYPE_VIDEO;
            default:
                return null;
        }
    }
}
