package com.lyl.study.cloud.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.*;


public class ZipUtils {
    private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    /**
     * 工具类不允许创建实例
     */
    private ZipUtils() {
    }

    /**
     * 递归压缩文件夹
     *
     * @param srcRootDir 压缩文件夹根目录的子路径
     * @param file       当前递归压缩的文件或目录对象
     * @param zos        压缩文件存储对象
     */
    private static void zip(String srcRootDir, File file, ZipOutputStream zos) throws IOException {
        if (file == null) {
            return;
        }

        //如果是文件，则直接压缩该文件
        if (file.isFile()) {
            int count, bufferLen = 1024;
            byte data[] = new byte[bufferLen];

            //获取文件相对于压缩文件夹根目录的子路径
            String subPath = file.getAbsolutePath();
            int index = subPath.indexOf(srcRootDir);
            if (index != -1) {
                subPath = subPath.substring(srcRootDir.length() + File.separator.length());
            }
            ZipEntry entry = new ZipEntry(subPath);
            zos.putNextEntry(entry);
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                while ((count = bis.read(data, 0, bufferLen)) != -1) {
                    zos.write(data, 0, count);
                }
            }
            zos.closeEntry();
        }
        //如果是目录，则压缩整个目录
        else {
            //压缩目录中的文件或子目录
            File[] childFileList = file.listFiles();
            if (childFileList != null) {
                for (File aChildFileList : childFileList) {
                    zip(srcRootDir + File.separator + file.getName(), aChildFileList, zos);
                }
            }
            zos.closeEntry();
        }
    }

    /**
     * 对文件或文件目录进行压缩
     *
     * @param srcPath     要压缩的源文件路径。如果压缩一个文件，则为该文件的全路径；如果压缩一个目录，则为该目录的顶层目录路径
     * @param zipFileName 压缩文件名
     */
    public static void zip(String srcPath, String zipFileName) throws IOException {
        File srcFile = Paths.get(srcPath).toFile();
        String zipPath = Paths.get(zipFileName).toAbsolutePath().getParent().toString();

        //判断压缩文件保存的路径是否存在，如果不存在，则创建目录
        File zipDir = new File(zipPath);
        if (!zipDir.exists() || !zipDir.isDirectory()) {
            zipDir.mkdirs();
        }

        //创建压缩文件保存的文件对象
        File zipFile = new File(zipFileName);
        Files.deleteIfExists(Paths.get(zipFileName));

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
             ZipOutputStream zos = new ZipOutputStream(cos)
        ) {
            //如果只是压缩一个文件，则需要截取该文件的父目录
            String srcRootDir = Paths.get(srcPath).toAbsolutePath().getParent().toString();
            //调用递归压缩方法进行目录或文件压缩
            zip(srcRootDir, srcFile, zos);
            zos.flush();
        }
    }

    /**
     * 解压缩zip包
     *
     * @param zipFilePath        zip文件的全路径
     * @param unzipFilePath      解压后的文件保存的路径
     * @param includeZipFileName 解压后的文件保存的路径是否包含压缩文件的文件名。true-包含；false-不包含
     */
    @SuppressWarnings("unchecked")
    public static void unzip(String zipFilePath, String unzipFilePath, boolean includeZipFileName) throws IOException {
        File zipFile = new File(zipFilePath);
        //如果解压后的文件保存路径包含压缩文件的文件名，则追加该文件名到解压路径
        if (includeZipFileName) {
            String fileName = zipFile.getName();
            if (!StringUtils.isEmpty(fileName)) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            unzipFilePath = unzipFilePath + File.separator + fileName;
        }
        //创建解压缩文件保存的路径
        File unzipFileDir = new File(unzipFilePath);
        if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
            unzipFileDir.mkdirs();
        }

        //开始解压
        ZipEntry entry;
        String entryFilePath, entryDirPath;
        File entryFile, entryDir;
        int index, count, bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
            //循环对压缩包里的每一个文件进行解压
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                //构建压缩包中一个文件解压后保存的文件全路径
                entryFilePath = unzipFilePath + File.separator + entry.getName();
                //构建解压后保存的文件夹路径
                index = entryFilePath.lastIndexOf(File.separator);
                if (index != -1) {
                    entryDirPath = entryFilePath.substring(0, index);
                } else {
                    entryDirPath = "";
                }
                entryDir = new File(entryDirPath);
                //如果文件夹路径不存在，则创建文件夹
                if (!entryDir.exists() || !entryDir.isDirectory()) {
                    entryDir.mkdirs();
                }

                //创建解压文件
                entryFile = new File(entryFilePath);
                if (entryFile.exists()) {
                    //检测文件是否允许删除，如果不允许删除，将会抛出SecurityException
                    SecurityManager securityManager = new SecurityManager();
                    securityManager.checkDelete(entryFilePath);
                    //删除已存在的目标文件
                    entryFile.delete();
                }

                //写入文件
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(entryFile));
                     BufferedInputStream bis = new BufferedInputStream(zip.getInputStream(entry))) {
                    while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
                        bos.write(buffer, 0, count);
                    }
                }
            }
        }
    }

//    public static void main(String[] args) {
//        String dir = "demo-api";
//        String zipFileName = "/Users/liyilin/Downloads/test.zip";
//        try {
//            zip(dir, zipFileName);
//        } catch (Exception e) {
//            LOG.error(e.getMessage());
//        }
//
//        String zipFilePath = "D:\\ziptest\\zipPath\\test.zip";
//        String unzipFilePath = "D:\\ziptest\\zipPath";
//        try {
//            unzip(zipFilePath, unzipFilePath, true);
//        } catch (Exception e) {
//            LOG.error(e.getMessage());
//        }
//    }
}