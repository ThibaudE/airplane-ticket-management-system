package sample.command;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sample.Sample;

@Command(name = "PutPassengerInfo", description = "Put passenger information")
public class PutPassengerInfoCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "NAME", description = "name of the passenger")
    private String name;

    @Parameters(index = "1", paramLabel = "ADDRESS", description = "address of the passenger")
    private String address;

    @Parameters(index = "2", paramLabel = "TELEPHONE_NUMBER", description = "telephone number")
    private String telNumber;

    @Parameters(index = "3", paramLabel = "EMAIL", description = "email")
    private String email;

    @Parameters(index = "4", paramLabel = "PASSWORD", description = "password")
    private String password;

    @Parameters(index = "5", paramLabel = "CARD_NUMBER", description = "card number")
    private int cardNumber;

    @Parameters(index = "6", paramLabel = "CARD_TYPE", description = "card type")
    private String cardType;

    @Parameters(index = "7", paramLabel = "EXPIRATION_DATE", description = "expiration date")
    private String expirationDate;

    @Parameters(index = "8", paramLabel = "CREDIT_LIMIT", description = "credit limit")
    private int creditLimit;

    @Parameters(index = "9", paramLabel = "SPENDING", description = "spending")
    private int spending;

    @Override
    public Integer call() throws Exception {
        try (Sample sample = new Sample()) {
            System.out.println(sample.putPassengerInfo(name, address, telNumber, email, password,
                    cardNumber, cardType, expirationDate, creditLimit, spending));
        }
        return 0;
    }
}
