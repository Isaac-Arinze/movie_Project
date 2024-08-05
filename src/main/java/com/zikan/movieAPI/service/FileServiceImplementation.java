package com.zikan.movieAPI.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImplementation implements FileService {

    //this is a file uploaded on the backend server
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // get name of file
        String filename = file.getOriginalFilename();

        //ge the file path
        String filePath = path + File.separator + filename;

        //create file object
        File f = new File(path);
        if(!f.exists()){
            f.mkdir();
        }
        //copy the file or upload the file to the path

        Files.copy(file.getInputStream(), Paths.get(filePath));

        return filename;
    }

    @Override
    public InputStream getResourceFile(String path, String filename) throws FileNotFoundException {

        //get filePath
        String filePath = path + File.separator + filename;
        //this will provide the file in the form of input stream
        return new FileInputStream(filePath);
    }
}
