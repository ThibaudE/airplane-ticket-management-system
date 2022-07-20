package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "ExchangeTickets", description = "Exchange tickets between two passengers")
public class ExchangeTicketsCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "PROFILE_ID_PASSENGER_1", description = "profile ID of the first passenger")
    private int profile1Id;

    @Parameters(index = "1", paramLabel = "TICKET_ID_PASSENGER_1", description = "ticket ID of the first passenger")
    private int ticket1Id;

    @Parameters(index = "2", paramLabel = "PROFILE_ID_PASSENGER_2", description = "profile ID of the second passenger")
    private int profile2Id;

    @Parameters(index = "3", paramLabel = "TICKET_ID_PASSENGER_2", description = "ticket ID of the second passenger")
    private int ticket2Id;

    @Override
    public Integer call() throws Exception {
        try (Sample sample = new Sample()) {
            sample.exchangeTickets(profile1Id, ticket1Id, profile2Id, ticket2Id);
        }
        return 0;
    }
}
