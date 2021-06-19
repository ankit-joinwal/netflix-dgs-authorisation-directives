package com.example.directive.helper;

import com.example.external.subscription.client.SubscriptionClient;
import org.springframework.stereotype.Component;

@Component("authFunction")
public class AuthFunction {

    //Mock Subscription Service Client
    private SubscriptionClient subscriptionClient;

    public AuthFunction(SubscriptionClient subscriptionClient) {
        this.subscriptionClient = subscriptionClient;
    }

    public boolean hasOffer(String userUuid, String asset, String permission){
        String userPermission = subscriptionClient.getUserPermission(asset,userUuid);
        return permission.equalsIgnoreCase(userPermission);

    }
}
