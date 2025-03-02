package com.tihor.centrifuge_poc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tihor.centrifuge_poc.model.Reaction;
import com.tihor.centrifuge_poc.repository.ReactionRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class ReactionService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Resource
    private ReactionRepository reactionRepository;

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

    public List<Reaction> saveReaction(byte[] data) {
        try {
            Reaction reaction = objectMapper.readValue(data, Reaction.class);

            reaction.setTs(LocalDateTime.now());
            reactionRepository.save(reaction);

            return StreamSupport.stream(reactionRepository.findAll().spliterator(), false)
                    .toList();
        } catch (IOException e) {
            log.error("Error while saving reaction: {}", e.getMessage());
        }

        return Collections.emptyList();
    }
}
