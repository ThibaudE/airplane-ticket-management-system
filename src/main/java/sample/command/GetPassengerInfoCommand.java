package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "GetPassengerInfo", description = "Get passenger information")
public class GetPassengerInfoCommand implements Callable<Integer> {

  @Parameters(index = "0", paramLabel = "PASSENGER_ID", description = "passenger ID")
  private int passengerId;

  @Override
  public Integer call() throws Exception {
    try (Sample sample = new Sample()) {
      System.out.println(sample.getPassengerInfo(passengerId));
    }
    return 0;
  }
}
