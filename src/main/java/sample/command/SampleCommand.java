package sample.command;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "bin/sample", description = "Sample application for Microservice Transaction", subcommands = {
    LoadInitialDataCommand.class,
    PlaceOrderCommand.class,
    GetOrderCommand.class,
    GetOrdersCommand.class,
    GetPassengerInfoCommand.class,
    GetTicketInfoCommand.class,
    GetFlightInfoCommand.class,
    GetCreditCardInfoCommand.class,
    ExchangeTicketsCommand.class,
    RepaymentCommand.class,
    PutPassengerInfoCommand.class,
    PutFlightInfoCommand.class,
    PutTicketInfoCommand.class,
    AddAvailableSeatsCommand.class
})
public class SampleCommand implements Runnable {

  @Option(names = { "-h",
      "--help" }, usageHelp = true, description = "Displays this help message and quits.", defaultValue = "true")
  private Boolean showHelp;

  @Override
  public void run() {
    if (showHelp) {
      CommandLine.usage(this, System.out);
    }
  }

  public static void main(String[] args) {
    new CommandLine(new SampleCommand()).execute(args);
  }
}
