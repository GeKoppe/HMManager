package org.hmdms.hmmanager.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class consists of all sorts of logging utilities to guarantee consistent logging.
 */
public abstract class LoggingUtils {
    /**
     * Classes own logger
     */
    private static Logger loggingLogger = LoggerFactory.getLogger(LoggingUtils.class.getName());
    /**
     * Callback to {@link LoggingUtils#logException(Exception, Logger, String)} with level info for the message.
     * @param ex Exception that should be logged.
     * @param logger Logger with which to log the exception. Should be the logger of the class, which caught the
     *               Exception
     */
    public static void logException(Exception ex, Logger logger) throws IllegalArgumentException {
        LoggingUtils.logException(ex, logger, "info");
    }

    /**
     * Callback to {@link LoggingUtils#logException(Exception, Logger, String, String)} with a standard string
     * for logging the exceptions message. Will produce a message of the form "[Exception type]: [Exception message]".
     * @param ex Exception that should be logged.
     * @param logger Logger with which to log the exception. Should be the logger of the class, which caught the
     *               Exception
     * @param logLevel Loglevel with which the exception message should be logged. Can either be trace, debug, info,
     *                 warn or error. Stacktrace will always be logged at trace level. If no level is given, info is
     *                 assumed
     */
    public static void logException(Exception ex, Logger logger, String logLevel) throws IllegalArgumentException {
        LoggingUtils.logException(ex, logger, logLevel, "%s: %s");
    }

    /**
     * Logs the exceptions message and type with the given logger at the given level.
     * Tries to format the string to include the message and the exception with {@link String#format(String, Object...)}.
     * Stacktrace of the exception is always logged at trace level.
     * @param ex Exception that should be logged.
     * @param logger Logger with which to log the exception. Should be the logger of the class, which caught the
     *               Exception
     * @param logLevel Loglevel with which the exception message should be logged. Can either be trace, debug, info,
     *                 warn or error. Stacktrace will always be logged at trace level. If no level is given,
     *                 info is assumed.
     * @param formatString String which should be formatted to include the Exception type and the exception message.
     *                     Method looks for %s in the string. If none are present, "%s: %s" is added to the end of the
     *                     string. First placeholder is always replaced by the exception type, second by the exception
     *                     metho.d
     * @throws IllegalArgumentException Is thrown, when no logger or exception is given
     */
    public static void logException(Exception ex, Logger logger, String logLevel, String formatString)
            throws IllegalArgumentException {

        if (ex == null || logger == null) throw new IllegalArgumentException("No exception or logger given");

        // Set loglevel to info, if none is given
        String level;
        if (logLevel == null || logLevel.isEmpty()) level = "info";
        else level = logLevel;

        // If no logstring is given, set a default string
        String toLog = getLogString(ex, formatString);

        // Log the message at given level or info
        switch (level) {
            case "trace": logger.trace(toLog); break;
            case "debug": logger.debug(toLog); break;
            case "warn": logger.warn(toLog); break;
            case "error": logger.error(toLog); break;
            default: logger.info(toLog); break;
        }

        // Build a stacktrace string and log it
        StringBuilder sb = new StringBuilder();
        for (var stel : ex.getStackTrace()) {
            sb.append(stel.toString());
            sb.append("\n\t");
        }
        logger.trace(sb.toString());
    }

    /**
     * Builds a formatted exception message string
     * @param ex Exception which supplies the type and message
     * @param formatString String in which the exception metadata should be included. Look at
     *                     {@link LoggingUtils#logException(Exception, Logger, String, String)} for more information
     * @return Formatted string
     */
    private static String getLogString(Exception ex, String formatString) {
        String logString;
        if (formatString == null || formatString.isEmpty()) logString = "%s: %s";
        else logString = formatString;

        // If a logstring is given but no placeholders are present in the string, add placeholders at the end.
        if (!logString.contains("%s")) logString += " %s: %s";
        // Format the string with exception type and message
        return String.format((logString), ex.getClass().getName(), ex.getMessage());
    }
}
