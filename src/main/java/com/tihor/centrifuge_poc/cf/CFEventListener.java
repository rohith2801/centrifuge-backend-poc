package com.tihor.centrifuge_poc.cf;

import io.github.centrifugal.centrifuge.Client;
import io.github.centrifugal.centrifuge.ConnectedEvent;
import io.github.centrifugal.centrifuge.ConnectingEvent;
import io.github.centrifugal.centrifuge.DisconnectedEvent;
import io.github.centrifugal.centrifuge.ErrorEvent;
import io.github.centrifugal.centrifuge.EventListener;
import io.github.centrifugal.centrifuge.MessageEvent;
import io.github.centrifugal.centrifuge.ServerJoinEvent;
import io.github.centrifugal.centrifuge.ServerLeaveEvent;
import io.github.centrifugal.centrifuge.ServerPublicationEvent;
import io.github.centrifugal.centrifuge.ServerSubscribedEvent;
import io.github.centrifugal.centrifuge.ServerSubscribingEvent;
import io.github.centrifugal.centrifuge.ServerUnsubscribedEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class CFEventListener extends EventListener {
    @Override
    public void onConnected(Client client, ConnectedEvent event) {
        log.info("client - connected with client id {}", event.getClient());
    }

    @Override
    public void onConnecting(Client client, ConnectingEvent event) {
        log.debug("client - connecting: {}", event.getReason());
    }

    @Override
    public void onDisconnected(Client client, DisconnectedEvent event) {
        log.debug("client - disconnected {} {}", event.getCode(), event.getReason());
    }

    @Override
    public void onError(Client client, ErrorEvent event) {
        log.error("client - connection error: {}", event.getError().toString());
    }

    @Override
    public void onMessage(Client client, MessageEvent event) {
        String data = new String(event.getData(), StandardCharsets.UTF_8);
        log.info("client - message received: {}", data);
    }

    @Override
    public void onSubscribed(Client client, ServerSubscribedEvent event) {
        log.info("Client - server side subscribed: {}, recovered {}", event.getChannel(), event.getRecovered());
    }

    @Override
    public void onSubscribing(Client client, ServerSubscribingEvent event) {
        log.debug("Client - server side subscribing: {}", event.getChannel());
    }

    @Override
    public void onUnsubscribed(Client client, ServerUnsubscribedEvent event) {
        log.debug("Client - server side unsubscribed: {}", event.getChannel());
    }

    @Override
    public void onPublication(Client client, ServerPublicationEvent event) {
        String data = new String(event.getData(), StandardCharsets.UTF_8);
        log.debug("Client - server side publication: {}: {}", event.getChannel(), data);
    }

    @Override
    public void onJoin(Client client, ServerJoinEvent event) {
        log.debug("Client - server side join: {} from client {}", event.getChannel(), event.getInfo().getClient());
    }

    @Override
    public void onLeave(Client client, ServerLeaveEvent event) {
        log.debug("Client - server side leave: {} from client {}", event.getChannel(), event.getInfo().getClient());
    }
}
