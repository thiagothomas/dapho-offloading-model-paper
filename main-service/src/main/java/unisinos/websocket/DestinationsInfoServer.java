package unisinos.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import unisinos.model.node.FogNode;
import unisinos.services.providers.AvailableNodesProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebSocket(path = "/destinations/{nodeId}")
@ApplicationScoped
public class DestinationsInfoServer
{
    private static final Logger LOGGER = Logger.getLogger(DestinationsInfoServer.class.getName());

    @Inject
    WebSocketConnection connection;

    @Inject
    AvailableNodesProvider availableNodesProvider;

    @OnOpen
    public String onOpen ()
    {
        LOGGER.info("Parent connected: " + connection.pathParam("nodeId"));
        return "connected";
    }

    @OnClose
    public void onClose ()
    {
        LOGGER.info("Parent disconnected: " + connection.pathParam("nodeId"));
    }

    @OnTextMessage
    public void onTextMessage (String message)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register JSR310 module

        List<FogNode> fogNodes = new ArrayList<>();
        try {
            fogNodes = objectMapper.readerFor(new TypeReference<List<FogNode>>()
            {
            }).readValue(message);
            if (fogNodes != null && !fogNodes.isEmpty()) {
                availableNodesProvider.setAvailableNodes(fogNodes);
            }
        }
        catch (Exception e) {
            LOGGER.severe("Failed to parse message: " + message);
        }
        LOGGER.info("Received text message from " + connection.pathParam("nodeId") + ": "
                                    + fogNodes);

    }

}
