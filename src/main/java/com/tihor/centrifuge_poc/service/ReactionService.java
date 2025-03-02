package com.tihor.centrifuge_poc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tihor.centrifuge_poc.model.Reaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ReactionService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${centrifuge.base-url}")
    private String centrifugeBaseUrl;

    @Value("${centrifuge.publish-url}")
    private String centrifugePublishUrl;

    @Value("${centrifuge.api-key}")
    private String apiKey;

    public void publishReaction(Reaction reaction) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("reaction", reaction);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "apikey " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("channel", "event-channel-" + reaction.getEventId());
        body.put("data", data);

        final String centrifugoApiUrl = centrifugeBaseUrl + centrifugePublishUrl;

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        log.info("Request Body: {}", new ObjectMapper().writeValueAsString(body));

        String response = restTemplate.postForObject(centrifugoApiUrl, request, String.class);
        log.info("Response: {}", response);
    }
}
