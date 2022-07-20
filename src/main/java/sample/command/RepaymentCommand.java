package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "Repayment", description = "Repayment")
public class RepaymentCommand implements Callable<Integer> {

  @Parameters(index = "0", paramLabel = "PROFILE_ID", description = "profile ID")
  private int profileId;

  @Parameters(index = "1", paramLabel = "AMOUNT", description = "amount of the money for repayment")
  private int amount;

  @Override
  public Integer call() throws Exception {
    try (Sample sample = new Sample()) {
      sample.repayment(profileId, amount);
    }
    return 0;
  }
}
