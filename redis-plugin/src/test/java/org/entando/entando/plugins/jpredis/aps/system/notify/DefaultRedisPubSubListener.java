package org.entando.entando.plugins.jpredis.aps.system.notify;

import io.lettuce.core.internal.LettuceFactories;
import io.lettuce.core.pubsub.RedisPubSubListener;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultRedisPubSubListener implements RedisPubSubListener<String, String> {

    private BlockingQueue<String> channels;
    private BlockingQueue<String> patterns;
    private BlockingQueue<String> messages;
    private BlockingQueue<Long> counts;

    public DefaultRedisPubSubListener(BlockingQueue<String> messages, BlockingQueue<String> channels, BlockingQueue<Long> counts) {
        this.channels = channels;
        patterns = LettuceFactories.newBlockingQueue();
        this.messages = messages;
        this.counts = counts;
    }

    @Override
    public void message(String channel, String message) {
        log.info("Channel: '{}', Message: '{}'", channel, message);
        channels.add(channel);
        messages.add(message);
    }

    @Override
    public void message(String pattern, String channel, String message) {
        log.info("pattern: '{}', Channel: '{}', Message: '{}'", pattern, channel, message);
        patterns.add(pattern);
        channels.add(channel);
        messages.add(message);
    }

    @Override
    public void subscribed(String channel, long count) {
        log.info("channel: '{}', count: '{}'", channel, count);
        channels.add(channel);
        counts.add(count);
    }

    @Override
    public void psubscribed(String pattern, long count) {
        log.info("pattern: '{}', pattern: '{}'", pattern, count);
        patterns.add(pattern);
        counts.add(count);
    }

    @Override
    public void unsubscribed(String channel, long count) {
        channels.add(channel);
        counts.add(count);
    }

    @Override
    public void punsubscribed(String pattern, long count) {
        patterns.add(pattern);
        counts.add(count);
    }

    protected BlockingQueue<String> getChannels() {
        return channels;
    }
    protected void setChannels(BlockingQueue<String> channels) {
        this.channels = channels;
    }

    protected BlockingQueue<String> getPatterns() {
        return patterns;
    }
    protected void setPatterns(BlockingQueue<String> patterns) {
        this.patterns = patterns;
    }

    protected BlockingQueue<String> getMessages() {
        return messages;
    }
    protected void setMessages(BlockingQueue<String> messages) {
        this.messages = messages;
    }

    protected BlockingQueue<Long> getCounts() {
        return counts;
    }
    protected void setCounts(BlockingQueue<Long> counts) {
        this.counts = counts;
    }
    
}
