package com.tihor.centrifuge_poc.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Reaction implements Serializable {
    private String id;
    private String eventId;
    private String emoji;
    private String userId;
}
