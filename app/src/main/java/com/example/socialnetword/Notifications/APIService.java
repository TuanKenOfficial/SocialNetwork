package com.example.socialnetword.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAZ6y04IE:APA91bEk5e9rktYc8vOUTy07ZdksO79yUAV5NhKVTFrTqiyqCmoBxn0Dqz1qAOZkrY5UMiJ3uG87rpFoJAT_hXKMfOaWY-84S7Z926BGBcs0XuZANT7FsnmZ3QxiKK8_0fb8s_7Jbq2J"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
