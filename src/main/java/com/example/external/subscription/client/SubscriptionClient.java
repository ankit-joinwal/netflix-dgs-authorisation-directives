package com.example.external.subscription.client;

import kotlin.Pair;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Mock client to return user permissions.
 * In real world, you would call a remote subscription service.
 */
@Component
public class SubscriptionClient {

    private static Map<String, String> USER_VIDEO_PERMISSIONS = Map.of("a18c0991-eb8f-319a-84bf-57d48cbd543c","play");

    public String getUserPermission(String asset,String userUuid){
        if(Objects.nonNull(userUuid) && asset.equalsIgnoreCase("video")) {
            return Optional.ofNullable(USER_VIDEO_PERMISSIONS.get(userUuid)).orElse( "None");
        }else{
            return "None";
        }
    }
}
