package unisinos.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import unisinos.model.FogNode;
import java.util.Set;

@ApplicationScoped
public class ConnectionsManager
{

    private FogNodeClient clientManager;

    @PostConstruct
    public void init ()
    {
        clientManager = new FogNodeClient();
    }

    public void addNodeConnection (String nodeId,
                                   String uri)
    {
        try {
            clientManager.connect(nodeId,
                                  uri);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String nodeId, String message)
    {
        clientManager.sendMessage(nodeId, message);
    }

    @PreDestroy
    public void shutdown ()
    {
        try {
            clientManager.closeAllConnections();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}