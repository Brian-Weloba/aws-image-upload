package com.saturdev.awsimageupload.datastore;

import com.saturdev.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {
    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("b59d2ec7-1c1a-470c-886b-49810eb9403d"), "janetjones", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("43aa8ec2-b029-42b4-bbf2-506e50564470"), "anotoniojunior", null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }

    public UserProfile getUserProfile(UUID userProfileId) {
        return USER_PROFILES.stream().filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId)).findFirst().orElse(null);
    }

    public void updateUserProfile(UserProfile userProfile) {
        int index = USER_PROFILES.indexOf(userProfile);
        USER_PROFILES.set(index, userProfile);
    }
}
