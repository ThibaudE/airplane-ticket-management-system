package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "PlaceOrder", description = "Place an order")
public class PlaceOrderCommand implements Callable<Integer> {

  @Parameters(index = "0", paramLabel = "PROFILE_ID", description = "profile ID")
  private int profileId;

  @Parameters(index = "1", paramLabel = "ORDERS", description = "orders. The format is \"<Ticket ID>:<Count>,<Ticket ID>:<Count>,...\"")
  private String orders;

  @Override
  public Integer call() throws Exception {
    String[] split = orders.split(",", -1);
    int[] ticketIds = new int[split.length];
    int[] ticketCounts = new int[split.length];

    for (int i = 0; i < split.length; i++) {
      String[] s = split[i].split(":", -1);
      ticketIds[i] = Integer.parseInt(s[0]);
      ticketCounts[i] = Integer.parseInt(s[1]);
    }

    try (Sample sample = new Sample()) {
      System.out.println(sample.placeOrder(profileId, ticketIds, ticketCounts));
    }

    return 0;
  }
}
