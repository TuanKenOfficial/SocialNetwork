package com.example.socialnetword.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAwD6WktA:APA91bHLNpxNCk82k5lZ2pvG9y-DWi7lfRr3FsKarn6qQnbU0t9d4yRBk8aNlkRTUUiCb9p7YK5aSetKxdtkB5jKWwJ3AoytIdAroT2UA6QLSH5UfDhbctnDKjzukhv07Y88P_owlkLw"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
