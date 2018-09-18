package com.lyl.study.cloud.admin.web.config;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Path;

@ToString
@ConfigurationProperties(prefix = "cloud.resources")
public class UploadProperties {
    private Path uploadPath;
    private String urlPrefix;
    private Long maxImageSize;
    private Long maxAudioSize;
    private Long maxVideoSize;
    private Long maxFileSize;

    public void setUploadPath(Resource uploadPath) throws IOException {
        this.uploadPath = uploadPath.getFile().toPath();
    }

    public void setUrlPrefix(String urlPrefix) {
        if (!urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix + "/";
        }

        this.urlPrefix = urlPrefix;
    }

    public void setMaxImageSize(String maxImageSize) {
        this.maxImageSize = transformToByteSize(maxImageSize);
    }

    public void setMaxAudioSize(String maxAudioSize) {
        this.maxAudioSize = transformToByteSize(maxAudioSize);
    }

    public void setMaxVideoSize(String maxVideoSize) {
        this.maxVideoSize = transformToByteSize(maxVideoSize);
    }

    @Value("${spring.http.multipart.max-file-size}")
    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = transformToByteSize(maxFileSize);
    }

    public Path getUploadPath() {
        return uploadPath;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public Long getMaxImageSize() {
        return maxImageSize;
    }

    public Long getMaxAudioSize() {
        return maxAudioSize;
    }

    public Long getMaxVideoSize() {
        return maxVideoSize;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * 单位转换
     *
     * @param sizeStr 描述文件大小的字符串
     * @return 字节大小
     */
    private long transformToByteSize(String sizeStr) {
        sizeStr = sizeStr.toLowerCase();
        if (sizeStr.endsWith("b")) {
            sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
        }

        long size = parseLong(sizeStr);
        String unit = sizeStr.substring(String.valueOf(size).length());
        if (unit.equals("g")) {
            size *= 1024L * 1024L * 1024L;
        } else if (unit.equals("m")) {
            size *= 1024L * 1024L;
        } else if (unit.equals("k")) {
            size *= 1024L;
        }
        return size;
    }

    private long parseLong(String sizeStr) {
        int digitStrLen = 0;
        for (int i = 0; i < sizeStr.length(); i++) {
            char c = sizeStr.charAt(i);
            if (c >= '0' && c <= '9') {
                digitStrLen++;
            } else {
                break;
            }
        }

        if (digitStrLen > 0) {
            return Long.parseLong(sizeStr.substring(0, digitStrLen));
        } else {
            return 0;
        }
    }
}
