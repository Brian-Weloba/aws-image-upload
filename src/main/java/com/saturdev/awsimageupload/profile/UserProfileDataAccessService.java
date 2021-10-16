package com.saturdev.awsimageupload.profile;

import com.amazonaws.services.greengrassv2.model.LambdaIsolationMode;
import com.saturdev.awsimageupload.datastore.FakeUserProfileDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserProfileDataAccessService {

    private final FakeUserProfileDataStore fakeUserProfileDataStore;

    @Autowired
    public UserProfileDataAccessService(FakeUserProfileDataStore fakeUserProfileDataStore){
        this.fakeUserProfileDataStore = fakeUserProfileDataStore;
    }

    List<UserProfile> getUserProfiles(){
        return fakeUserProfileDataStore.getUserProfiles();
    }

}
