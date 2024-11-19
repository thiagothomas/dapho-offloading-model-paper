import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EdgeDeviceSimulation
{

    //    private static final String[] NODES = { "http://3.22.120.152:8082/vital-sign",
    //                    "http://52.15.49.121:8082/vital-sign" };
    private static final String[] NODES = { "http://3.139.97.33:8082/vital-sign",
                    "http://18.222.119.116:8082/vital-sign" };
    private static final String[] SERVICES = { "ML_PREDICTION", "NEWS2" };
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Random random = new Random();

    public static void main (String[] args)
    {
        int numNodes = NODES.length; // Assuming each thread handles one node
        int totalVitalSigns = 40; // Total number of vital signs to be sent
        int edgeMessagesPerRequest = 10; // Number of messages in each request

        int totalVitalSignsPerNode =
                        totalVitalSigns / numNodes; // Calculate total vital signs per node
        int requestsPerNode = totalVitalSignsPerNode
                        / edgeMessagesPerRequest; // Calculate requests per node

        System.out.printf("Total Vital Signs per Node: %d%n",
                          totalVitalSignsPerNode);
        System.out.printf("Requests per Node: %d%n",
                          requestsPerNode);

        ExecutorService executor = Executors.newFixedThreadPool(numNodes);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < numNodes; i++) {
            final int nodeIndex = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processRequests(nodeIndex,
                                                                                              requestsPerNode,
                                                                                              edgeMessagesPerRequest),
                                                                        executor);
            futures.add(future);
        }

        // Shutdown executor and wait for tasks to finish
        executor.shutdown();
        CompletableFuture<Void> allRequests = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allRequests.join();
            System.out.println("All requests completed");
        }
        catch (Exception e) {
            System.err.println(
                            "Interrupted while waiting for tasks to complete: " + e.getMessage());
        }
    }

    private static void processRequests (int nodeIndex,
                                         int requestsPerThread,
                                         int edgeMessagesPerRequest)
    {
        String nodeUrl = NODES[nodeIndex];
        for (int i = 0; i < requestsPerThread; i++) {
            List<VitalSign> edgeMessages = generateEdgeMessages(edgeMessagesPerRequest);
            Collections.shuffle(edgeMessages);
            String messageList = String.join(",",
                                                   edgeMessages.toString());
            sendRequest(nodeUrl,
                        messageList);

            try {
                Thread.sleep(5000); // 1-second window between each request
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted: " + e.getMessage());
            }
        }
    }

    private static List<VitalSign> generateEdgeMessages (int edgeMessagesPerRequest)
    {
        List<VitalSign> messages = new ArrayList<>();
        int priorities = 5; // User priorities from 1 to 5
        int messagesPerPriority = edgeMessagesPerRequest / priorities; // 2 messages per priority

        for (int priority = 1; priority <= priorities; priority++) {
            for (int j = 0; j < messagesPerPriority; j++) {
                // Alternate between services for each message
                String service = SERVICES[j % SERVICES.length];
                messages.add(generateVitalSign(priority,
                                               service));
            }
        }
        return messages;
    }

    private static VitalSign generateVitalSign (int userPriority,
                                                String service)
    {
        VitalSign vitalSign = new VitalSign();
        vitalSign.setId(UUID.randomUUID().toString());
        vitalSign.setUserId("test_" + UUID.randomUUID().toString().substring(0,
                                                                             12));
        vitalSign.setUserPriority(userPriority);
        vitalSign.setService(service);

        vitalSign.setRespRate(10.0 + (random.nextInt(10) + random.nextDouble()));
        vitalSign.setHypercapnicFailure(random.nextBoolean());
        vitalSign.setO2Sat(80.0 + random.nextDouble() * 20);
        vitalSign.setO2Supplement(random.nextBoolean());
        vitalSign.setTemperature(35.0 + random.nextDouble() * 3);
        vitalSign.setSystolicBp(90.0 + random.nextDouble() * 30);
        vitalSign.setPulseRate(60 + random.nextInt(40));
        vitalSign.setConsciousness("Alert");
        return vitalSign;
    }

    private static void sendRequest (String nodeUrl,
                                     String messageList)
    {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(nodeUrl))
                        .header("Content-Type",
                                "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(messageList)).build();

        httpClient.sendAsync(request,
                             HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::statusCode)
                        .thenAccept(status -> System.out.println(
                                        "Sent to " + nodeUrl + ": Status " + status))
                        .exceptionally(e -> {
                            System.err.println(
                                            "Error sending to " + nodeUrl + ": " + e.getMessage());
                            return null;
                        });
    }
}

class VitalSign
{
    private String id;
    private String userId;
    private Integer userPriority;
    private String service;
    private Double respRate;
    private Boolean hypercapnicFailure;
    private Double o2Sat;
    private Boolean o2Supplement;
    private Double temperature;
    private Double systolicBp;
    private Integer pulseRate;
    private String consciousness;

    // Getters and Setters
    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getUserId ()
    {
        return userId;
    }

    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    public Integer getUserPriority ()
    {
        return userPriority;
    }

    public void setUserPriority (Integer userPriority)
    {
        this.userPriority = userPriority;
    }

    public String getService ()
    {
        return service;
    }

    public void setService (String service)
    {
        this.service = service;
    }

    public Double getRespRate ()
    {
        return respRate;
    }

    public void setRespRate (Double respRate)
    {
        this.respRate = respRate;
    }

    public Boolean getHypercapnicFailure ()
    {
        return hypercapnicFailure;
    }

    public void setHypercapnicFailure (Boolean hypercapnicFailure)
    {
        this.hypercapnicFailure = hypercapnicFailure;
    }

    public Double getO2Sat ()
    {
        return o2Sat;
    }

    public void setO2Sat (Double o2Sat)
    {
        this.o2Sat = o2Sat;
    }

    public Boolean getO2Supplement ()
    {
        return o2Supplement;
    }

    public void setO2Supplement (Boolean o2Supplement)
    {
        this.o2Supplement = o2Supplement;
    }

    public Double getTemperature ()
    {
        return temperature;
    }

    public void setTemperature (Double temperature)
    {
        this.temperature = temperature;
    }

    public Double getSystolicBp ()
    {
        return systolicBp;
    }

    public void setSystolicBp (Double systolicBp)
    {
        this.systolicBp = systolicBp;
    }

    public Integer getPulseRate ()
    {
        return pulseRate;
    }

    public void setPulseRate (Integer pulseRate)
    {
        this.pulseRate = pulseRate;
    }

    public String getConsciousness ()
    {
        return consciousness;
    }

    public void setConsciousness (String consciousness)
    {
        this.consciousness = consciousness;
    }

    @Override
    public String toString ()
    {
        return String.format("""
                                             {
                                               "id": "%s",
                                               "userId": "%s",
                                               "userPriority": %d,
                                               "service": "%s",
                                               "vitalSign": {
                                                 "respRate": %.1f,
                                                 "hypercapnicFailure": %b,
                                                 "o2Sat": %.1f,
                                                 "o2Supplement": %b,
                                                 "temperature": %.1f,
                                                 "systolicBp": %.1f,
                                                 "pulseRate": %d,
                                                 "consciousness": "%s"
                                               },
                                               "offloadedBy": []
                                             }
                                             """,
                             id,
                             userId,
                             userPriority,
                             service,
                             respRate,
                             hypercapnicFailure,
                             o2Sat,
                             o2Supplement,
                             temperature,
                             systolicBp,
                             pulseRate,
                             consciousness);
    }
}
