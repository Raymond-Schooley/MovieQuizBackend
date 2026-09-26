package com.moviequiz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the correct answer for a quiz question that has been handed to a
 * client but not yet answered.
 *
 * <p>This is deliberately in-memory rather than persisted: a quiz question
 * is short-lived, single-use scratch state, not a durable record. That
 * keeps the API stateless from the client's point of view (it only ever
 * needs the opaque questionId) while avoiding a database round trip for
 * something the app doesn't need to remember. For a real production
 * deployment running more than one backend instance behind a load
 * balancer, swap this out for a shared store (Redis, or a short-lived DB
 * table) so any instance can validate any answer.
 */
@Component
@Slf4j
public class QuizSessionStore {

    private static final long TTL_SECONDS = 300;

    public record PendingAnswer(String correctOptionId, String correctAnswerText, Instant expiresAt) {
    }

    private final Map<String, PendingAnswer> pending = new ConcurrentHashMap<>();

    public void put(String questionId, String correctOptionId, String correctAnswerText) {
        pending.put(questionId, new PendingAnswer(correctOptionId, correctAnswerText, Instant.now().plusSeconds(TTL_SECONDS)));
    }

    /**
     * Answers are single-use: looking one up removes it so the same
     * question can't be submitted twice.
     */
    public Optional<PendingAnswer> take(String questionId) {
        PendingAnswer answer = pending.remove(questionId);
        if (answer == null || answer.expiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        return Optional.of(answer);
    }

    /**
     * Non-consuming lookup, mainly useful for tests and diagnostics -
     * production code should use {@link #take(String)} so a question can't
     * be answered twice.
     */
    public Optional<PendingAnswer> peek(String questionId) {
        PendingAnswer answer = pending.get(questionId);
        if (answer == null || answer.expiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        return Optional.of(answer);
    }

    @Scheduled(fixedRate = 60_000)
    void evictExpired() {
        Instant now = Instant.now();
        int before = pending.size();
        pending.values().removeIf(answer -> answer.expiresAt().isBefore(now));
        int removed = before - pending.size();
        if (removed > 0) {
            log.debug("Evicted {} expired quiz question(s)", removed);
        }
    }
}
