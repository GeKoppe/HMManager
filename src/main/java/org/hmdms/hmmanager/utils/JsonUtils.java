package org.hmdms.hmmanager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for json operations
 */
public abstract class JsonUtils {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * Default constructor
     */
    public JsonUtils() { }
    /**
     * Checks, whether the json object in {@param node} has the property {@param property}.
     * @param node JSON Object to be checked
     * @param property Property for which to check
     * @return True, if {@param node} contains a value in {@param property}, false otherwise.
     */
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

    /**
     * Checks, if property {@param property} exists in Json String {@param jsonString}.
     * First converts the jsonString into a {@link JsonNode} and then returns result of
     * {@link JsonUtils#jsonHasProperty(JsonNode, String)}, called with the new node.
     * @param jsonString Json String to be checked
     * @param property Property for which the {@param jsonString} should be checked
     * @return True, if the property exists in {@param jsonString}
     * @throws JsonProcessingException Thrown, when {@param jsonString} could not be converted into a {@link JsonNode}.
     * @throws IllegalArgumentException Thrown, when either of the params are null or empty.
     */
    public static boolean jsonHasProperty(String jsonString, String property)
            throws JsonProcessingException, IllegalArgumentException {

        if (jsonString == null || jsonString.isEmpty()) throw new IllegalArgumentException(
                "Expected jsonString, received null or empty String"
        );

        if (property == null || property.isEmpty()) throw new IllegalArgumentException(
                "Expected property, received null or empty String"
        );

        JsonNode node = new ObjectMapper().readTree(jsonString);
        logger.debug("Successfully parsed json String to json");
        return jsonHasProperty(node, property);
    }


    /**
     * Instantiates a {@link JsonNode} from given {@param jsonString}
     * @param jsonString String that should be converted into a {@link JsonNode}
     * @return Created node
     * @throws JsonProcessingException Thrown, when the string could not be deserialized into a node
     */
    public static JsonNode getNodeFromString(String jsonString) throws JsonProcessingException {
        if (jsonString == null || jsonString.isEmpty()) {
            logger.debug("No string given for processing");
            throw new IllegalArgumentException("No json string given");
        }

        JsonNode node = new ObjectMapper().readTree(jsonString);
        return node;
    }
}
