package com.flawlessyou.backend.entity.cloudinary;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.flawlessyou.backend.entity.cloudinary.CloudinaryResponse;
import com.flawlessyou.backend.exception.FuncErrorException;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Transactional
    public CloudinaryResponse uploadFile(final MultipartFile file, final String fileName) {
        try {
            final Map    result   = this.cloudinary.uploader()
                                              .upload(file.getBytes(),
                                                      Map.of("public_id",
                                                             "nhndev/product/"
                                                                          + fileName));
            final String url      = (String) result.get("secure_url");
            final String publicId = (String) result.get("public_id");
            return CloudinaryResponse.builder().publicId(publicId).url(url)
                                     .build();

        } catch (final Exception e) {
            throw new FuncErrorException("Failed to upload file");
        }
    }
}