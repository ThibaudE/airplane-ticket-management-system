package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "GetOrdersByProfileId", description = "Get order information by passenger ID")
public class GetOrdersCommand implements Callable<Integer> {

  @Parameters(index = "0", paramLabel = "PROFILE_ID", description = "profile ID")
  private int profileId;

  @Override
  public Integer call() throws Exception {
    try (Sample sample = new Sample()) {
      System.out.println(sample.getOrdersByPassengerId(profileId));
    }
    return 0;
  }
}
