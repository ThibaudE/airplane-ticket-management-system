package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "GetFlightInfo", description = "Get flight information")
public class GetFlightInfoCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "FLIGHT_ID", description = "flight ID")
    private int flightId;

    @Override
    public Integer call() throws Exception {
        try (Sample sample = new Sample()) {
            System.out.println(sample.getFlightInfo(flightId));
        }
        return 0;
    }
}
