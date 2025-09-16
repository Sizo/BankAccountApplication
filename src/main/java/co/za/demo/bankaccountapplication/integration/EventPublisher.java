package co.za.demo.bankaccountapplication.integration;

import co.za.demo.bankaccountapplication.exception.EventPublishingException;

/**
 * Generic interface for publishing events to external systems.
 *
 * @param <T> the type of event to publish
 */
public interface EventPublisher<T> {

  /**
   * Publishes an event to the configured destination.
   *
   * @param topic the topic/destination to publish to
   * @param event the event to publish
   * @throws EventPublishingException if publishing fails
   */
  void publish(String topic, T event) throws EventPublishingException;

  /**
   * Publishes an event with a message key for partitioning.
   *
   * @param topic the topic/destination to publish to
   * @param key   the message key for partitioning
   * @param event the event to publish
   * @throws EventPublishingException if publishing fails
   */
  void publish(String topic, String key, T event) throws EventPublishingException;
}
