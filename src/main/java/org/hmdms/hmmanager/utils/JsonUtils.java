package org.hmdms.hmmanager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    public static boolean jsonHasProperty(JsonNode node, String property) {
        if (node == null) throw new IllegalArgumentException("Expected node, received null");
        if (property == null || property.isEmpty()) throw new IllegalArgumentException(
                "Expected property, received null or empty String"
        );

        String[] parts = property.split("\\.");
        JsonNode currentNode = node;
        for (String part : parts) {
            try {
                currentNode = node.get(part);
                if (currentNode == null) {
                    logger.debug(String.format("Tree ends, does not have node %s", part));
                    return false;
                }
            } catch (Exception ex) {
                LoggingUtils.logException(ex, logger, "info", "%s Exception while walking json tree: %s");
                return false;
            }
        }
        if (currentNode != null) {
            // TODO better check what node.get returns, when the next part does not exist
            logger.debug("Given json has property that was checked");
            return true;
        } else {
            logger.debug("Given json does not have property that was checked");
            return false;
        }
    }

    public static boolean jsonHasProperty(String jsonString, String property)
            throws JsonProcessingException, IllegalArgumentException {

        if (jsonString == null || jsonString.isEmpty()) throw new IllegalArgumentException(
                "Expected jsonString, received null or empty String"
        );

        JsonNode node = new ObjectMapper().readTree(jsonString);
        logger.debug("Successfully parsed json String to json");
        return jsonHasProperty(node, property);
    }
}
