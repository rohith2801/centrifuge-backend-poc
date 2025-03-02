package com.tihor.centrifuge_poc.controller;

import com.tihor.centrifuge_poc.model.Reaction;
import com.tihor.centrifuge_poc.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping
    public ResponseEntity<Void> sendReaction(@RequestBody Reaction reaction) throws Exception {
        reactionService.publishReaction(reaction);
        return ResponseEntity.ok().build();
    }
}
