package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "GetCreditCardInfo", description = "Get credit card information depending on the profile")
public class GetCreditCardInfoCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "PROFILE_ID", description = "profile ID")
    private int profileId;

    @Override
    public Integer call() throws Exception {
        try (Sample sample = new Sample()) {
            System.out.println(sample.getCreditCardInfo(profileId));
        }
        return 0;
    }
}