package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "AddAvailableSeats", description = "Add more seats to a specific flight")
public class AddAvailableSeatsCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "FLIGHT_ID", description = "id of the corresponding flight")
    private int flightId;

    @Parameters(index = "1", paramLabel = "SEATS_TO_ADD", description = "number of seats to add")
    private int seatsToAdd;

    @Override
    public Integer call() throws Exception {
        try (Sample sample = new Sample()) {
            System.out.println(
                    sample.addAvailableSeats(flightId, seatsToAdd));
        }
        return 0;
    }
}
