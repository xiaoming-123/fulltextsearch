package com.zhangxiaobai.cloud.fulltextsearch.util;

import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件工具类
 *
 * @author zhangcq
 */
public class FileUtils {

    private FileUtils() {
    }

    /**
     * 保存路径
     */
    private final static String SAVE_URL = "D://elasticsearch//";

    /**
     * 保存文件
     *
     * @param file 文件
     * @return fullFilename 文件路径
     * @throws IOException io
     */
    public static String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.indexOf("."));
        String filename = UUID.randomUUID().toString() + suffix;
        String saveUrl = SAVE_URL + DateTimeUtils.getString("yyyy-MM-dd");
        String fullFilename = saveUrl + "//" + filename;
        File saveFile = new File(saveUrl);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        saveFile = new File(fullFilename);
        if (!saveFile.exists()) {
            saveFile.createNewFile();
        }
        byte[] bytes = file.getBytes();
        try (FileOutputStream fileOutputStream = new FileOutputStream(saveFile, false)) {
            fileOutputStream.write(bytes);
        }
        return fullFilename;
    }

    public static String getBase64String(MultipartFile file) throws IOException {
        return Base64Utils.encodeToString(file.getBytes());
    }

}
