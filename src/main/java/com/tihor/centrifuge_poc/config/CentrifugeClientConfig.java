package com.tihor.centrifuge_poc.config;

import com.tihor.centrifuge_poc.cf.CFEventListener;
import io.github.centrifugal.centrifuge.Client;
import io.github.centrifugal.centrifuge.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CentrifugeClientConfig {
    @Value("${centrifuge.client-ws-url}")
    private String centrifugeClientWsUrl;

    @Bean
    public Client getClient() {
        Options opts = new Options();
//        opts.setTokenGetter(new ConnectionTokenGetter() {
//            @Override
//            public void getConnectionToken(ConnectionTokenEvent event, TokenCallback cb) {
//                // At this place you must request the token from the backend in real app.
//                cb.Done(null, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTY2MDA3MDYzMiwiaWF0IjoxNjU5NDY1ODMyfQ.EWBmBsvbUsOublFJeG0fAMQz_RnX3ZQwd5E00ldyyh0");
//            }
//        });

        Client client = new Client(centrifugeClientWsUrl, opts, new CFEventListener());
        client.connect();

        return client;
    }
}
