package com.bbblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

@RestController
public class ImageUploadController {
    private String rootPath = this.getClass().getResource("/").getPath() +
            "static" + File.separator +"images" + File.separator;
    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @PostMapping(value = {"/u/{userName}/blogs/edit/upload", "/u/{userName}/blogs/upload"})
    public ObjectNode upload(@PathVariable("userName") String userName, @RequestParam(value = "editormd-image-file", required = false) MultipartFile multipartFile) {
        File filePath = new File(rootPath + userName);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File realFile = new File(filePath + File.separator + multipartFile.getOriginalFilename());
        ObjectNode jsonObject = objectMapper.createObjectNode();
        try {
            OutputStream is = new FileOutputStream(realFile);
            is.write(multipartFile.getBytes());
            is.flush();

            jsonObject.put("success", 1);
            jsonObject.put("message", "上传成功");
            jsonObject.put("url", "/images/" + URLEncoder.encode(userName, "UTF-8") + "/" + multipartFile.getOriginalFilename());
        } catch (IOException e) {
            jsonObject.put("success", 0);

        }
        return jsonObject;
    }

    @PostMapping("/{userName}/avatar")
    public String avatarUpload(@PathVariable("userName") String userName, @RequestParam(value = "file", required = false) MultipartFile multipartFile) {
        File file = new File(rootPath +"avatar" + File.separator +
                multipartFile.getOriginalFilename() + "_" + userName);
        try {
            if(!file.exists()){
                file.createNewFile();
            }

            OutputStream is = new FileOutputStream(file);
            is.write(multipartFile.getBytes());
            is.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "/images/avatar/" + multipartFile.getOriginalFilename() + "_" + userName;

    }

}
