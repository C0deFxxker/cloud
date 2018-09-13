package com.lyl.study.cloud.cms.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.cms.api.dto.request.ResourceEntityListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ResourceEntitySaveForm;
import com.lyl.study.cloud.cms.api.dto.response.ResourceEntityDTO;
import com.lyl.study.cloud.cms.api.facade.ResourceEntityFacade;
import com.lyl.study.cloud.cms.web.UploadProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static com.lyl.study.cloud.cms.api.CmsErrorCode.*;

@RestController
@RequestMapping("/resourceEntity")
public class ResourceEntityController implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    @Reference
    private ResourceEntityFacade resourceEntityFacade;

    public Long getMaxImageSize() {
        return maxImageSize;
    }

    public void setMaxImageSize(Long maxImageSize) {
        this.maxImageSize = maxImageSize;
    }

    public Long getMaxAudioSize() {
        return maxAudioSize;
    }

    public void setMaxAudioSize(Long maxAudioSize) {
        this.maxAudioSize = maxAudioSize;
    }

    public Long getMaxVideoSize() {
        return maxVideoSize;
    }

    public void setMaxVideoSize(Long maxVideoSize) {
        this.maxVideoSize = maxVideoSize;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public Path getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(Path uploadPath) {
        this.uploadPath = uploadPath;
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("上传文件配置: {}", properties);
        BeanUtils.copyProperties(properties, this);
    }

    @PostMapping
    public Result uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            // 校验文件大小
            checkFileSize(multipartFile);
            // 计算文件保存地址
            Path path = resolveFilePath(multipartFile);
            // 保存到本地
            logger.info("保存文件: {}", path.toAbsolutePath().toString());
            multipartFile.transferTo(path.toAbsolutePath().toFile());
            // 创建数据库记录保存表单
            ResourceEntitySaveForm form = buildSaveForm(multipartFile, path);

            try {
                resourceEntityFacade.save(form);
                return new Result<>(OK, "上传成功", form.getUrl());
            } catch (Exception e) {
                logger.error("内部服务调用错误，将回滚上传的文件: {}\n\n{}",
                        path.toAbsolutePath().toString(), e.toString());
                Files.deleteIfExists(Paths.get(form.getFilepath()));
                return new Result<>(FILE_SAVE_INTERNAL_SERVICE_EXCEPTION, "内部服务调用错误", e.getMessage());
            }
        } catch (IOException e) {
            return new Result<>(FILE_SAVE_IO_EXCEPTION, "文件读写异常", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Long id) {
        logger.info("删除ID为" + id + "的资源文件");

        ResourceEntityDTO dto = resourceEntityFacade.getById(id);

        if (dto != null) {
            try {
                Files.delete(Paths.get(dto.getFilepath()));
                logger.info("文件删除成功: {}", dto.getFilepath());
            } catch (IOException e) {
                // 可能是文件或路径不正确，暂不处理
                logger.error("文件删除失败: {}", e.getMessage());
            }

            resourceEntityFacade.deleteById(id);
            return new Result<>(OK, "删除成功", null);
        } else {
            return new Result<>(NOT_FOUND, "找不到ID为" + id + "的资源文件", null);
        }
    }

    @GetMapping("/list")
    public Result<PageInfo<ResourceEntityDTO>> list(@ModelAttribute ResourceEntityListConditions conditions) {
        PageInfo<ResourceEntityDTO> page = resourceEntityFacade.list(conditions);
        page.getRecords().forEach(entity -> entity.setFilepath(null));
        return new Result<>(OK, "查询成功", page);
    }


    private ResourceEntitySaveForm buildSaveForm(MultipartFile file, Path localPath) {
        ResourceEntitySaveForm form = new ResourceEntitySaveForm();
        form.setMediaType(file.getContentType());
        form.setOriginalFilename(file.getOriginalFilename());
        form.setSize(file.getSize());
        form.setFilepath(localPath.toAbsolutePath().toString());
        form.setUrl(urlPrefix + uploadPath.relativize(localPath).toString());
        return form;
    }

    /**
     * 计算上传文件的存储路径
     *
     * @param multipartFile 文件信息
     * @return 文件存储路径
     */
    private Path resolveFilePath(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = getFileNameSuffix(originalFilename);

        Exception error = null;
        for (int i = 0; i < 10; i++) {
            String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
            try {
                Path filepath = uploadPath.resolve(Paths.get(filename));
                Files.createFile(filepath);
                return filepath;
            } catch (Exception e) {
                error = e;
            }
        }
        throw new RuntimeException("上传文件失败: " + error.getMessage());
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
    private void checkFileSize(MultipartFile file) {
        MediaType mediaType = MediaType.parseMediaType(file.getContentType());

        if (MEDIA_TYPE_IMAGE.includes(mediaType) && maxImageSize != null) {
            Assert.isTrue(maxImageSize > file.getSize(), "上传图片文件过大");
        } else if (MEDIA_TYPE_AUDIO.includes(mediaType) && maxAudioSize != null) {
            Assert.isTrue(maxAudioSize > file.getSize(), "上传音频文件过大");
        } else if (MEDIA_TYPE_VIDEO.includes(mediaType) && maxVideoSize != null) {
            Assert.isTrue(maxVideoSize > file.getSize(), "上传视频文件过大");
        } else {
            Assert.isTrue(maxFileSize > file.getSize(), "上传文件过大");
        }
    }
}