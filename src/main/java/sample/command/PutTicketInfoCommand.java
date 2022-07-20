package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "PutTicketInfo", description = "Put ticket information")
public class PutTicketInfoCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "FLIGHT_ID", description = "id of the corresponding flight")
    private int flightId;

    @Parameters(index = "1", paramLabel = "PRICE", description = "price of the ticket")
    private int price;

    @Override
    public Integer call() throws Exception {
        try (Sample sample = new Sample()) {
            System.out.println(
                    sample.putTicketInfo(flightId, price));
        }
        return 0;
    }
}
