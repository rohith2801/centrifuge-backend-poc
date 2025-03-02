package com.tihor.centrifuge_poc.runner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.centrifugal.centrifuge.Client;
import io.github.centrifugal.centrifuge.ClientState;
import io.github.centrifugal.centrifuge.ConnectingEvent;
import io.github.centrifugal.centrifuge.ConnectionTokenEvent;
import io.github.centrifugal.centrifuge.ConnectionTokenGetter;
import io.github.centrifugal.centrifuge.DuplicateSubscriptionException;
import io.github.centrifugal.centrifuge.HistoryOptions;
import io.github.centrifugal.centrifuge.JoinEvent;
import io.github.centrifugal.centrifuge.LeaveEvent;
import io.github.centrifugal.centrifuge.Options;
import io.github.centrifugal.centrifuge.EventListener;
import io.github.centrifugal.centrifuge.ServerJoinEvent;
import io.github.centrifugal.centrifuge.ServerLeaveEvent;
import io.github.centrifugal.centrifuge.ServerPublicationEvent;
import io.github.centrifugal.centrifuge.ServerSubscribedEvent;
import io.github.centrifugal.centrifuge.ServerSubscribingEvent;
import io.github.centrifugal.centrifuge.ServerUnsubscribedEvent;
import io.github.centrifugal.centrifuge.Subscription;
import io.github.centrifugal.centrifuge.SubscriptionEventListener;
import io.github.centrifugal.centrifuge.ConnectedEvent;
import io.github.centrifugal.centrifuge.DisconnectedEvent;
import io.github.centrifugal.centrifuge.ErrorEvent;
import io.github.centrifugal.centrifuge.MessageEvent;
import io.github.centrifugal.centrifuge.SubscribingEvent;
import io.github.centrifugal.centrifuge.PublicationEvent;
import io.github.centrifugal.centrifuge.SubscriptionErrorEvent;
import io.github.centrifugal.centrifuge.SubscribedEvent;
import io.github.centrifugal.centrifuge.SubscriptionOptions;
import io.github.centrifugal.centrifuge.TokenCallback;
import io.github.centrifugal.centrifuge.UnsubscribedEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ReactionApplicationRunner implements ApplicationRunner {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${centrifuge.base-url}")
    private String centrifugeBaseUrl;

    @Value("${centrifuge.subscribe-url}")
    private String centrifugeSubscribeUrl;

    @Value("${centrifuge.api-key}")
    private String apiKey;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "apikey " + apiKey);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("channel", "event-channel-" + 1);
//        body.put("user", "admin");
//
//        final String centrifugoApiUrl = centrifugeBaseUrl + centrifugeSubscribeUrl;
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//        log.info("Request Body: {}", new ObjectMapper().writeValueAsString(body));
//
//        String response = restTemplate.postForObject(centrifugoApiUrl, request, String.class);
//        log.info("Response: {}", response);

        EventListener listener = new EventListener() {
            @Override
            public void onConnected(Client client, ConnectedEvent event) {
                System.out.printf("connected with client id %s%n", event.getClient());
            }

            @Override
            public void onConnecting(Client client, ConnectingEvent event) {
                System.out.printf("client - connecting: %s%n", event.getReason());
            }

            @Override
            public void onDisconnected(Client client, DisconnectedEvent event) {
                System.out.printf("client - disconnected %d %s%n", event.getCode(), event.getReason());
            }

            @Override
            public void onError(Client client, ErrorEvent event) {
                System.out.printf("client - connection error: %s%n", event.getError().toString());
            }

            @Override
            public void onMessage(Client client, MessageEvent event) {
                String data = new String(event.getData(), StandardCharsets.UTF_8);
                System.out.println("client - message received: " + data);
            }

            @Override
            public void onSubscribed(Client client, ServerSubscribedEvent event) {
                System.out.println("Client - server side subscribed: " + event.getChannel() + ", recovered " + event.getRecovered());
            }

            @Override
            public void onSubscribing(Client client, ServerSubscribingEvent event) {
                System.out.println("Client - server side subscribing: " + event.getChannel());
            }

            @Override
            public void onUnsubscribed(Client client, ServerUnsubscribedEvent event) {
                System.out.println("Client - server side unsubscribed: " + event.getChannel());
            }

            @Override
            public void onPublication(Client client, ServerPublicationEvent event) {
                String data = new String(event.getData(), StandardCharsets.UTF_8);
                System.out.println("Client - server side publication: " + event.getChannel() + ": " + data);
            }

            @Override
            public void onJoin(Client client, ServerJoinEvent event) {
                System.out.println("Client - server side join: " + event.getChannel() + " from client " + event.getInfo().getClient());
            }

            @Override
            public void onLeave(Client client, ServerLeaveEvent event) {
                System.out.println("Client - server side leave: " + event.getChannel() + " from client " + event.getInfo().getClient());
            }
        };

        Options opts = new Options();
//        opts.setTokenGetter(new ConnectionTokenGetter() {
//            @Override
//            public void getConnectionToken(ConnectionTokenEvent event, TokenCallback cb) {
//                // At this place you must request the token from the backend in real app.
//                cb.Done(null, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTY2MDA3MDYzMiwiaWF0IjoxNjU5NDY1ODMyfQ.EWBmBsvbUsOublFJeG0fAMQz_RnX3ZQwd5E00ldyyh0");
//            }
//        });

        Client client = new Client("ws://localhost:8000/connection/websocket", opts, listener);
        client.connect();

        SubscriptionEventListener subListener = new SubscriptionEventListener() {
            @Override
            public void onSubscribed(Subscription sub, SubscribedEvent event) {
                System.out.println("Subscription - subscribed to " + sub.getChannel() + ", recovered " + event.getRecovered());
            }

            @Override
            public void onSubscribing(Subscription sub, SubscribingEvent event) {
                System.out.printf("Subscription - subscribing: %s%n", event.getReason());
            }

            @Override
            public void onUnsubscribed(Subscription sub, UnsubscribedEvent event) {
                System.out.println("Subscription - unsubscribed " + sub.getChannel() + ", reason: " + event.getReason());
            }

            @Override
            public void onError(Subscription sub, SubscriptionErrorEvent event) {
                System.out.println("Subscription - subscription error " + sub.getChannel() + " " + event.getError().toString());
            }

            @Override
            public void onPublication(Subscription sub, PublicationEvent event) {
                String data = new String(event.getData(), StandardCharsets.UTF_8);
                System.out.println("Subscription - message from " + sub.getChannel() + " " + data);
            }

            @Override
            public void onJoin(Subscription sub, JoinEvent event) {
                System.out.println("Subscription - client " + event.getInfo().getClient() + " joined channel " + sub.getChannel());
            }

            @Override
            public void onLeave(Subscription sub, LeaveEvent event) {
                System.out.println("Subscription - client " + event.getInfo().getClient() + " left channel " + sub.getChannel());
            }
        };

        Subscription sub;
        SubscriptionOptions subOpts = new SubscriptionOptions();
        try {
            sub = client.newSubscription("event-channel-1", subOpts, subListener);
        } catch (DuplicateSubscriptionException e) {
            e.printStackTrace();
            return;
        }
        sub.subscribe();

        String data = "{\"emoji\": \"hellooo.. \", \"userId\": \"admin\"}";

        client.publish("event-channel-1", data.getBytes(), (err, res) -> {
            if (err != null) {
                System.out.println("error publish: " + err);
                return;
            }
            System.out.println("successfully published");
        });

        // Publish via subscription (will wait for subscribe success before publishing).
        sub.publish(data.getBytes(), (err, res) -> {
            if (err != null) {
                System.out.println("error publish: " + err);
                return;
            }
            System.out.println("successfully published");
        });

        sub.presenceStats((err, res) -> {
            if (err != null) {
                System.out.println("error presence stats: " + err);
                return;
            }
            System.out.println("Num clients connected: " + res.getNumClients());
        });

        sub.history(new HistoryOptions.Builder().withLimit(-1).build(), (err, res) -> {
            if (err != null) {
                System.out.println("error history: " + err);
                return;
            }
            System.out.println("Num history publication: " + res.getPublications().size());
            System.out.println("Top stream offset: " + res.getOffset());
        });

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//
//        sub.unsubscribe();
//        client.removeSubscription(sub);
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//        client.disconnect();
//
//        try {
//            boolean ok = client.close(5000);
//            if (!ok) {
//                System.out.println("client was not gracefully closed");
//            } else {
//                System.out.println("client gracefully closed");
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
