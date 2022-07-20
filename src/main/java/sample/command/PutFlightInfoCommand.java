package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "PutFlightInfo", description = "Put flight information")
public class PutFlightInfoCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "FLIGHT_DEPARTURE_DATE", description = "date of departure of the flight")
    private String flightDepartureDate;

    @Parameters(index = "1", paramLabel = "FLIGHT_ARRIVAL_DATE", description = "date of the arrival of the flight")
    private String flightArrivalDate;

    @Parameters(index = "2", paramLabel = "AIRLINE_ID", description = "id of the airline")
    private int airlineId;

    @Parameters(index = "3", paramLabel = "AIRLINE_NAME", description = "name of the airline")
    private String airlineName;

    @Parameters(index = "4", paramLabel = "TRIP", description = "description of the trip")
    private String trip;

    @Parameters(index = "5", paramLabel = "DURATION", description = "duration of the trip")
    private String duration;

    @Parameters(index = "6", paramLabel = "TOTAL_SEATS", description = "total number of seats")
    private int totalSeats;

    @Parameters(index = "7", paramLabel = "AVAILABLE_SEATS", description = "number of available seats")
    private int availableSeats;

    @Override
    public Integer call() throws Exception {
        try (Sample sample = new Sample()) {
            System.out.println(
                    sample.putFlightInfo(flightDepartureDate, flightArrivalDate, airlineId, airlineName,
                            trip, duration, totalSeats, availableSeats));
        }
        return 0;
    }
}
