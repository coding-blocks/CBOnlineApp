package com.codingblocks.onlineandroidapi;

import com.codingblocks.onlineandroidapi.api.QbountyApi;
import com.codingblocks.onlineandroidapi.models.qbounty.Claim;
import com.codingblocks.onlineandroidapi.models.qbounty.Task;
import com.codingblocks.onlineandroidapi.models.qbounty.User;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.RelationshipResolver;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import static org.junit.Assert.assertEquals;

public class QbountyTest {

    public ObjectMapper om;
    public QbountyApi api;
    public ResourceConverter rc;


    @Before
    public void setUp () {
        om = new ObjectMapper();
        rc = new ResourceConverter(om,
                Claim.class, Task.class, User.class);

        rc.setGlobalResolver(new RelationshipResolver() {
            @Override
            public byte[] resolve(String relationshipURL) {
                OkHttpClient client = new OkHttpClient();
                String[] parts = relationshipURL.split(":[0-9]{4}");
                String actualUrl = parts[0] + parts[1];
                try {
                    okhttp3.Call c = client.newCall(new Request.Builder().url(actualUrl).build());
                    ResponseBody b = c.execute().body();
                    return b.bytes();
                } catch (IOException e) {
                    e.printStackTrace();
                    return new byte[0];
                }
            }
        });

        api = new Retrofit.Builder()
                .baseUrl("https://cb-qbounty.herokuapp.com/api/")
                .addConverterFactory(new JSONAPIConverterFactory(rc))
                .build()
                .create(QbountyApi.class);
    }


    @Test
    public void testApi() throws IOException {

        ArrayList<Claim> claims = api.getClaims().execute().body();
        int l = claims.size();
        assertEquals(2, l);

    }
    @Test
    public void testTasks() throws IOException {


        ArrayList<Task> tasks = api.getTasks().execute().body();
        int l = tasks.size();
        assertEquals(3, l);

    }
}
