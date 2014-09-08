package com.cetsoft.imcache.cache.redis.client;


public final class Protocol {

    public static final int DEFAULT_PORT = 6379;
    public static final int DEFAULT_SENTINEL_PORT = 26379;
    public static final int DEFAULT_TIMEOUT = 2000;
    public static final int DEFAULT_DATABASE = 0;

    public static final String CHARSET = "UTF-8";

    public static enum Command {
        PING, SET, GET, QUIT, EXISTS, DEL, TYPE, FLUSHDB, KEYS, EXPIRE, EXPIREAT, TTL;

        public final byte[] raw;

        Command() {
            raw = null;
        }
    }
}