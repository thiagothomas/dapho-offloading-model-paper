package unisinos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.opencsv.CSVWriter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/results")
public class ResultsResource
{
    private static final Logger LOGGER = Logger.getLogger(ResultsResource.class.getName());

    private final List<Result> results = new ArrayList<>();
    private final ObjectMapper objectMapper;

    public ResultsResource ()
    {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register JSR310 module
    }

    @PostMapping
    public ResponseEntity<Result> createResult (@RequestBody Result result)
    {
        results.add(result);
        LOGGER.info("POST - Result added: " + result);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<Result>> getAllResults ()
    {
        LOGGER.info("GET - Results requested.");
        return ResponseEntity.ok(results);
    }

    @PostMapping("/writeToFile")
    public ResponseEntity<String> writeResultsToFile ()
    {
        try {
            objectMapper.writeValue(new File("results.json"),
                                    results);
            LOGGER.info("Results written to file successfully.");
            return ResponseEntity.ok("Results written to file successfully.");
        }
        catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error writing results to file.");
        }
    }

    @PostMapping("/writeToCsv")
    public ResponseEntity<String> writeResultsToCsv ()
    {
        try (CSVWriter writer = new CSVWriter(new FileWriter("results.csv"))) {
            writer.writeNext(new String[] { "userPriority", "servicePriority", "firstArrivalAtNode",
                            "executedAt", "offloadedBy", "offloadCount", "serviceDuration" });

            for (Result result : results) {
                writer.writeNext(new String[] { result.userPriority().toString(),
                                result.servicePriority().toString(),
                                result.firstArrivalAtNode().toString(),
                                result.executedAt().toString(), result.offloadedBy().toString(),
                                result.offloadedBy().size() + "",
                                result.serviceDuration().toMillis() + "" });
            }
            LOGGER.info("Results written to CSV file successfully.");
            return ResponseEntity.ok("Results written to CSV file successfully.");
        }
        catch (IOException e) {
            return ResponseEntity.status(500).body("Error writing results to CSV file.");
        }
    }

    @DeleteMapping("/deleteFiles")
    public ResponseEntity<String> deleteResultsFiles ()
    {
        File jsonFile = new File("results.json");
        File csvFile = new File("results.csv");

        boolean jsonDeleted = jsonFile.delete();
        boolean csvDeleted = csvFile.delete();

        if (jsonDeleted && csvDeleted) {
            return ResponseEntity.ok("Results files deleted successfully.");
        }
        else {
            return ResponseEntity.status(500).body("Error deleting results files.");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteResults ()
    {
        results.clear();
        return ResponseEntity.ok("Results deleted successfully.");
    }

}
