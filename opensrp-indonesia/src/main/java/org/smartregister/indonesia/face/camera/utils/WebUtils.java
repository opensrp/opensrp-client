package org.smartregister.indonesia.face.camera.utils;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by sid on 7/24/17.
 */


public class WebUtils {

    public static Response fetch(String url, String username, String password) throws Exception {

        OkHttpClient httpClient = createAuthenticatedClient(username, password);
        // execute request

        return doRequest(httpClient, url);

    }

    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        // build client with authentication information.
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                if (responseCount(response) >= 3) {
                    return null;
                }
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }

    private static Response doRequest(OkHttpClient httpClient, String anyURL) throws Exception {
        Request request = new Request.Builder().url(anyURL).build();
        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
//        System.out.println(response.body().string());
        Tools.insertOrUpdate(response.body().bytes());

        return response;
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
