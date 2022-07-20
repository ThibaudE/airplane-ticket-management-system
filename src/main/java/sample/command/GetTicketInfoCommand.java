package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "GetTicketInfo", description = "Get ticket information")
public class GetTicketInfoCommand implements Callable<Integer> {

  @Parameters(index = "0", paramLabel = "TICKET_ID", description = "ticket ID")
  private int ticketId;

  @Override
  public Integer call() throws Exception {
    try (Sample sample = new Sample()) {
      System.out.println(sample.getTicketInfo(ticketId));
    }
    return 0;
  }
}
