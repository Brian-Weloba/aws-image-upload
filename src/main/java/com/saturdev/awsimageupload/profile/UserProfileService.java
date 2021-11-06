package com.saturdev.awsimageupload.profile;

import com.saturdev.awsimageupload.bucket.BucketName;
import com.saturdev.awsimageupload.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

    void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        //1. check if image is not empty
        isFileEmpty(file);

        //2. check if file is an image
        isImage(file);

        //3. check if user exists in database
        UserProfile user = getUserProfileOrThrow(userProfileId);


        //4. grab some metadata from file if any
        Map<String, String> metadata = extractMetadata(file);


        //5. store the image in s3 and
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),user.getUserProfileId());
        String filename = String.format("%s-%s",file.getOriginalFilename() ,UUID.randomUUID());
        try {
            fileStore.save(path,filename,Optional.of(metadata),file.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Could not save file");
        }

        //6.update database (userProfileImageLink) with an s3 image link
        user.setUserProfileImageLink(filename);

    }

    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String,String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId) {
        return userProfileDataAccessService
                .getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found",userProfileId)));
    }

    private void isImage(MultipartFile file) {
        if(Arrays.asList(ContentType.IMAGE_JPEG, ContentType.IMAGE_PNG,ContentType.IMAGE_GIF).contains(file.getContentType())){
            throw new IllegalStateException("File is not an image");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if(file.isEmpty()) {
            throw new IllegalStateException("File is empty");
        }
    }

    public byte[] downloadProfileImage(UUID userProfileId) {
        //check if user exists
        UserProfile user = getUserProfileOrThrow(userProfileId);
        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());
        //check if user has an image
        if(user.getUserProfileImageLink() == null) {
            throw new IllegalStateException("User has no image");
        }
        return user.getUserProfileImageLink()
                .map(Key -> fileStore.download(path,Key))
                .orElse(new byte[0]);

    }
}
