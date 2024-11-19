package unisinos.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;
import unisinos.model.message.EdgeMessage;
import unisinos.services.MessageProcessingService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/vital-sign/{nodeId}")
@ApplicationScoped
public class VitalSignServer
{

    private static final Logger LOG = Logger.getLogger(VitalSignServer.class);
    @Inject
    MessageProcessingService messageProcessingService;

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen (Session session,
                        @PathParam("nodeId") String nodeId)
    {
        LOG.info("New connection: " + nodeId);
        sessions.put(nodeId,
                     session);
    }

    @OnClose
    public void onClose (Session session,
                         @PathParam("nodeId") String nodeId)
    {
        LOG.info("Connection closed: " + nodeId);
        sessions.remove(nodeId);
    }

    @OnError
    public void onError (Session session,
                         @PathParam("nodeId") String nodeId,
                         Throwable throwable)
    {
        LOG.error("Error on connection: " + nodeId);
        sessions.remove(nodeId);
        LOG.error("onError",
                  throwable);
    }

    @OnMessage
    public void onMessage (Session session,
                           String message,
                           @PathParam("nodeId") String nodeId)
    {
        LOG.info("Received message from " + nodeId + ": " + message);
        try  {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            EdgeMessage vitalSign = objectMapper.readValue(message,
                                                           EdgeMessage.class);
            messageProcessingService.processMessage(vitalSign);
        }
        catch (Exception e) {
            LOG.error("Failed to parse message: " + message,
                      e);
            throw new RuntimeException(e);
        }
    }

}