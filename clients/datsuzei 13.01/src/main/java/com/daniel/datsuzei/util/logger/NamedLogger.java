package com.daniel.datsuzei.util.logger;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.StringTemplate.STR;

@RequiredArgsConstructor
public class NamedLogger
{
    private final String name;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean logDetail = System.getProperty("log.detail", "false").equals("true");

    public void detail(String s)
    {
        if (logDetail)
        {
            LOGGER.info(STR."[\{name}] \{s}");
        }
    }

    public void info(String s)
    {
        LOGGER.info(STR."[\{name}] \{s}");
    }

    public void warn(String s)
    {
        LOGGER.warn(STR."[\{name}] \{s}");
    }

    public void warn(String s, Throwable t)
    {
        LOGGER.warn(STR."[\{name}] \{s}", t);
    }

    public void error(String s)
    {
        LOGGER.error(STR."[\{name}] \{s}");
    }

    public void error(String s, Throwable t)
    {
        LOGGER.error(STR."[\{name}] \{s}", t);
    }

}
