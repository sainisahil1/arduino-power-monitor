package io.sahil.server.api;

import io.sahil.server.core.model.InfluxDTO;
import io.sahil.server.core.repository.InfluxDBRepository;
import io.sahil.server.util.HistoryType;
import io.sahil.server.util.QueryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Sahil Saini
 */
@RestController
public class InfluxDBController {

    private final InfluxDBRepository repository;

    @Autowired
    public InfluxDBController(InfluxDBRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/data")
    public InfluxDTO getData(
            @RequestParam(value = "time", defaultValue = "daily") String time,
            @RequestParam(value = "type", defaultValue = "historical") String type
    ){
        try {
            HistoryType historyType = HistoryType.valueOf(time.toUpperCase());
            QueryType queryType = QueryType.valueOf(type.toUpperCase());
            return repository.fetchData(historyType, queryType);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters: " + e.getMessage());
        }
    }

}
