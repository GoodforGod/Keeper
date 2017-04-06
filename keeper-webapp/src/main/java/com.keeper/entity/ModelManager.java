package com.keeper.entity;

/*
 * Created by @GoodforGod on 7.04.2017.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Default Comment
 */
public class ModelManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelManager.class);

    public static void logConstactionError(String msg, Throwable throwable) {
        LOGGER.error(msg, throwable);
    }

    public static void logSetupError(String msg, Throwable throwable) {
        LOGGER.error(msg, throwable);
    }
}