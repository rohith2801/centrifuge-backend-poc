package com.tihor.centrifuge_poc.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Reaction implements Serializable {
    private String id;
    private String eventId;
    private String emoji;
    private String userId;
    private LocalDateTime ts;
}
