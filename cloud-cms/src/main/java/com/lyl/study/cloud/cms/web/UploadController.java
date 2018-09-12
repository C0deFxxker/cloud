package com.lyl.study.cloud.cms.web;

import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.Random;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.cms.UploadProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController("/upload")
public class UploadController implements InitializingBean {
    private final MediaType MEDIA_TYPE_IMAGE = MediaType.parseMediaType("image/*");
    private final MediaType MEDIA_TYPE_AUDIO = MediaType.parseMediaType("audio/*");
    private final MediaType MEDIA_TYPE_VIDEO = MediaType.parseMediaType("video/*");

    private Long maxImageSize;
    private Long maxAudioSize;
    private Long maxVideoSize;
    private Long maxFileSize;
    private String urlPrefix;
    private Path uploadPath;

    @Autowired
    private UploadProperties properties;

    @Override
    public void afterPropertiesSet() {
        urlPrefix = properties.getUrlPrefix();
        uploadPath = properties.getUploadPath();
        maxImageSize = properties.getMaxImageSize();
        maxAudioSize = properties.getMaxAudioSize();
        maxVideoSize = properties.getMaxVideoSize();
        maxFileSize = properties.getMaxFileSize();
    }

    @PostMapping
    public Result uploadTmpFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        checkFileSize(multipartFile);
        Path path = resolveFilePath(multipartFile);
        multipartFile.transferTo(path.toAbsolutePath().toFile());
        return new Result<>(CommonErrorCode.OK, "上传成功",
                urlPrefix + uploadPath.relativize(path).toString());
    }

    /**
     * 计算上传文件的存储路径
     *
     * @param multipartFile 文件信息
     * @return 文件存储路径
     */
    protected Path resolveFilePath(MultipartFile multipartFile) {
        MediaType.parseMediaType("image/*");
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = getFileNameSuffix(originalFilename);

        for (int i = 0; i < 10; i++) {
            String filename = System.currentTimeMillis()
                    + String.format("%04d", new Random().nextInt(10000)) + suffix;

            try {
                Path filepath = uploadPath.resolve(Paths.get(filename));
                Files.createFile(filepath);
                return filepath;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("上传文件失败");
    }

    private String getFileNameSuffix(String filename) {
        int index = filename.lastIndexOf(".");
        return index == -1 ? "" : filename.substring(index);
    }

    /**
     * 验证文件大小
     *
     * @param file 文件
     */
    protected void checkFileSize(MultipartFile file) {
        MediaType mediaType = MediaType.parseMediaType(file.getContentType());

        if (MEDIA_TYPE_IMAGE.includes(mediaType)) {
            Assert.isTrue(maxImageSize <= file.getSize(), "上传图片文件过大");
        } else if (MEDIA_TYPE_AUDIO.includes(mediaType)) {
            Assert.isTrue(maxAudioSize <= file.getSize(), "上传音频文件过大");
        } else if (MEDIA_TYPE_VIDEO.includes(mediaType)) {
            Assert.isTrue(maxVideoSize <= file.getSize(), "上传视频文件过大");
        } else {
            Assert.isTrue(maxFileSize <= file.getSize(), "上传文件过大");
        }
    }
}