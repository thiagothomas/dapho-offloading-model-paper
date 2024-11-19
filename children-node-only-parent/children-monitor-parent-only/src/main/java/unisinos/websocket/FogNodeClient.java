package unisinos.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ClientEndpoint
public class FogNodeClient
{

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnMessage
    public void onMessage (String message,
                           Session session)
    {
        System.out.println("Mensagem recebida de " + message);
    }

    public void connect (String nodeId,
                         String uri) throws Exception
    {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();
        Session session = container.connectToServer(this,
                                                    new URI(uri));
        sessions.put(nodeId,
                     session);
    }

    public void sendMessage (String nodeId,
                             String message)
    {
        Session session = sessions.get(nodeId);
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }

    public void closeConnection (String nodeId) throws Exception
    {
        Session session = sessions.get(nodeId);
        if (session != null) {
            session.close();
            sessions.remove(nodeId);
        }
    }

    public void closeAllConnections () throws Exception
    {
        for (Session session : sessions.values()) {
            session.close();
        }
        sessions.clear();
    }
}
