package com.tihor.centrifuge_poc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tihor.centrifuge_poc.model.Reaction;
import io.github.centrifugal.centrifuge.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CentrifugeService {
    @Autowired
    private Client client;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishHostAnalyticsData(final String channelName, final List<Reaction> reactions) {
        try {
            client.publish(channelName, objectMapper.writeValueAsBytes(reactions), (err, res) -> {
                if (err != null) {
                    System.out.println("error publish: " + err);
                    return;
                }

                System.out.println("successfully published for host analytics");
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
