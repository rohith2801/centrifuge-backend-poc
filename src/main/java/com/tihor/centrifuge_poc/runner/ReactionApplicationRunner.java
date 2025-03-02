package com.tihor.centrifuge_poc.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tihor.centrifuge_poc.model.Reaction;
import com.tihor.centrifuge_poc.repository.ReactionRepository;
import com.tihor.centrifuge_poc.service.ReactionService;
import io.github.centrifugal.centrifuge.Client;
import io.github.centrifugal.centrifuge.DuplicateSubscriptionException;
import io.github.centrifugal.centrifuge.HistoryOptions;
import io.github.centrifugal.centrifuge.JoinEvent;
import io.github.centrifugal.centrifuge.LeaveEvent;
import io.github.centrifugal.centrifuge.Subscription;
import io.github.centrifugal.centrifuge.SubscriptionEventListener;
import io.github.centrifugal.centrifuge.SubscribingEvent;
import io.github.centrifugal.centrifuge.PublicationEvent;
import io.github.centrifugal.centrifuge.SubscriptionErrorEvent;
import io.github.centrifugal.centrifuge.SubscribedEvent;
import io.github.centrifugal.centrifuge.SubscriptionOptions;
import io.github.centrifugal.centrifuge.UnsubscribedEvent;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Slf4j
public class ReactionApplicationRunner implements ApplicationRunner {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private Client client;

    @Value("${centrifuge.base-url}")
    private String centrifugeBaseUrl;

    @Value("${centrifuge.subscribe-url}")
    private String centrifugeSubscribeUrl;

    @Value("${centrifuge.api-key}")
    private String apiKey;

    @Override
    public void run(ApplicationArguments args) {
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
                reactionService.saveReaction(event.getData());
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
            log.error("Exception while new subscription: {}", e.getMessage(), e);
            return;
        }

        sub.subscribe();

//        String data = "{\"emoji\": \"hellooo.. \", \"userId\": \"admin\"}";
//
//        client.publish("event-channel-1", data.getBytes(), (err, res) -> {
//            if (err != null) {
//                System.out.println("error publish: " + err);
//                return;
//            }
//            System.out.println("successfully published");
//        });
//
//        sub.publish(data.getBytes(), (err, res) -> {
//            if (err != null) {
//                System.out.println("error publish: " + err);
//                return;
//            }
//            System.out.println("successfully published");
//        });
//
//        sub.presenceStats((err, res) -> {
//            if (err != null) {
//                System.out.println("error presence stats: " + err);
//                return;
//            }
//            System.out.println("Num clients connected: " + res.getNumClients());
//        });
//
//        sub.history(new HistoryOptions.Builder().withLimit(-1).build(), (err, res) -> {
//            if (err != null) {
//                System.out.println("error history: " + err);
//                return;
//            }
//            System.out.println("Num history publication: " + res.getPublications().size());
//            System.out.println("Top stream offset: " + res.getOffset());
//        });

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
