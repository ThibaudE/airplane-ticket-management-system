package sample;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Sample implements AutoCloseable {

  private final DistributedTransactionManager manager;

  public Sample() throws IOException {
    // Create a transaction manager object
    TransactionFactory factory = new TransactionFactory(new DatabaseConfig(new File("database.properties")));
    manager = factory.getTransactionManager();
  }

  public void loadInitialData() throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      transaction = manager.start();
      loadPassengerIfNotExists(transaction, 1, "Passenger n°1", "Hiyoshi International Dormitory", "N/A", "azertyuiop",
          "passenger1@keio.jp");
      loadPassengerIfNotExists(transaction, 2, "Passenger n°2", "Yagami Dormitory", "+33 6 58 45 29 84", "password",
          "passenger2@keio.jp");
      loadPassengerIfNotExists(transaction, 3, "Passenger n°3", "2bis place barthelemy, Rouen", "0595-456-6844",
          "jaipasdinspi", "passenger3@keio.jp");

      loadCreditCardIfNotExists(transaction, 1, 41564615, "Visa", "06/18/23", 15000, 0);
      loadCreditCardIfNotExists(transaction, 2, 14566844, "MasterCard", "07/03/26", 20000, 0);
      loadCreditCardIfNotExists(transaction, 3, 88465455, "Visa", "12/01/28", 300000, 0);

      loadFlightDetailsIfNotExists(transaction, 1, "03/22/22", "03/24/22", 84, "Air France", "From Paris to Tokyo",
          "26 hours", 456, 456);
      loadFlightDetailsIfNotExists(transaction, 2, "11/12/23", "11/12/23", 65, "Lufthansa", "From Dubai to Budapest",
          "6 h", 156, 156);
      loadFlightDetailsIfNotExists(transaction, 3, "06/30/26", "06/31/26", 48, "Quatar Airline",
          "From Montreal to Nantes", "16h", 159, 1);

      loadTicketIfNotExists(transaction, 1, 1, 1000);
      loadTicketIfNotExists(transaction, 2, 2, 2000);
      loadTicketIfNotExists(transaction, 3, 3, 2500);
      loadTicketIfNotExists(transaction, 4, 1, 5000);
      loadTicketIfNotExists(transaction, 5, 3, 3000);
      transaction.commit();
    } catch (TransactionException e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  private void loadPassengerIfNotExists(
      DistributedTransaction transaction,
      int passengerId,
      String name,
      String address,
      String telNumber,
      String password,
      String email)
      throws TransactionException {
    Optional<Result> passenger = transaction.get(
        new Get(new Key("profile_id", passengerId))
            .forNamespace("customer")
            .forTable("passenger_profile"));
    if (!passenger.isPresent()) {
      transaction.put(
          new Put(new Key("profile_id", passengerId))
              .withValue("name", name)
              .withValue("address", address)
              .withValue("tel_no", telNumber)
              .withValue("email", email)
              .withValue("password", password)
              .forNamespace("customer")
              .forTable("passenger_profile"));
    }
  }

  private void loadCreditCardIfNotExists(
      DistributedTransaction transaction,
      int passengerId,
      int cardNumber,
      String cardType,
      String expirationDate,
      int creditLimit,
      int spending)
      throws TransactionException {
    Optional<Result> credit_card = transaction.get(
        new Get(new Key("profile_id", passengerId))
            .forNamespace("customer")
            .forTable("credit_card_details"));
    if (!credit_card.isPresent()) {
      transaction.put(
          new Put(new Key("profile_id", passengerId))
              .withValue("card_number", cardNumber)
              .withValue("card_type", cardType)
              .withValue("expiration_date", expirationDate)
              .withValue("credit_limit", creditLimit)
              .withValue("spending", spending)
              .forNamespace("customer")
              .forTable("credit_card_details"));
    }
  }

  private void loadTicketIfNotExists(
      DistributedTransaction transaction,
      int ticketId,
      int flightId,
      int price)
      throws TransactionException {
    Optional<Result> ticket = transaction.get(
        new Get(new Key("ticket_id", ticketId)).forNamespace("order").forTable("ticket_info"));
    if (!ticket.isPresent()) {
      transaction.put(
          new Put(new Key("ticket_id", ticketId)) // There might have an error here
              .withValue("flight_id", flightId)
              .withValue("price", price)
              .forNamespace("order")
              .forTable("ticket_info"));
    }
  }

  private void loadFlightDetailsIfNotExists(
      DistributedTransaction transaction,
      int flightId,
      String flightDepartureDate,
      String flightArrivalDate,
      int arilineId,
      String airlineName,
      String trip,
      String duration,
      int totalSeats,
      int availableSeats)
      throws TransactionException {
    Optional<Result> flight = transaction.get(
        new Get(new Key("flight_id", flightId)).forNamespace("customer").forTable("flight_details"));
    if (!flight.isPresent()) {
      transaction.put(
          new Put(new Key("flight_id", flightId))
              .withValue("flight_departure_date", flightDepartureDate)
              .withValue("flight_arrival_date", flightArrivalDate)
              .withValue("airline_id", arilineId)
              .withValue("airline_name", airlineName)
              .withValue("trip", trip)
              .withValue("duration", duration)
              .withValue("total_seats", totalSeats)
              .withValue("available_seats", availableSeats)
              .forNamespace("customer")
              .forTable("flight_details"));
    }
  }

  public String getPassengerInfo(int passengerId) throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      // Retrieve the passenger info for the specified passenger ID from the
      // passenger_profile table
      Optional<Result> passenger = transaction.get(
          new Get(new Key("profile_id", passengerId))
              .forNamespace("customer")
              .forTable("passenger_profile"));

      if (!passenger.isPresent()) {
        // If the customer info the specified customer ID doesn't exist, throw an
        // exception
        throw new RuntimeException("Passenger not found");
      }

      // Commit the transaction (even when the transaction is read-only, we need to
      // commit)
      transaction.commit();

      // Return the customer info as a JSON format
      return String.format(
          "{\"profile_id\": %d, \"name\": \"%s\", \"address\": \"%s\", \"tel_no\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}",
          passengerId,
          passenger.get().getValue("name").get().getAsString().get(),
          passenger.get().getValue("address").get().getAsString().get(),
          passenger.get().getValue("tel_no").get().getAsString().get(),
          passenger.get().getValue("email").get().getAsString().get(),
          passenger.get().getValue("password").get().getAsString().get());
    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String putPassengerInfo(
      String name,
      String address,
      String telNumber,
      String email,
      String password,
      int cardNumber,
      String cardType,
      String expirationDate,
      int creditLimit, int spending) throws TransactionException {

    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      Boolean exist = true;
      int profileId = 0;
      while (!exist) {
        profileId = (int) Math.floor(Math.random() * 1000);

        // Check if the id is already in the database
        Optional<Result> passenger = transaction.get(
            new Get(new Key("profile_id", profileId))
                .forNamespace("customer")
                .forTable("passenger_profile"));

        if (passenger.isPresent()) {
          exist = false;
        }

      }

      // Put the passenger info into the passenger_profile table
      transaction.put(
          new Put(
              new Key("profile_id", profileId))
              .withValue("name", name)
              .withValue("address", address)
              .withValue("tel_no", telNumber)
              .withValue("email", email)
              .withValue("password", password)
              .forNamespace("customer")
              .forTable("passenger_profile"));

      // Put the credit card info into the credit_card_details table
      transaction.put(
          new Put(
              new Key("profile_id", profileId))
              .withValue("card_number", cardNumber)
              .withValue("card_type", cardType)
              .withValue("expiration_date", expirationDate)
              .withValue("credit_limit", creditLimit)
              .withValue("spending", spending)
              .forNamespace("customer")
              .forTable("credit_card_details"));

      // Commit the transaction (even when the transaction is read-only, we need to
      // commit)
      transaction.commit();

      // Return the passenger info as a JSON format
      return String.format(
          "{\"profile_id\": %d}", profileId);

    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String getTicketInfo(int ticketId) throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      // Retrieve the ticket info for the specified ticket ID from the ticket_info
      // table
      Optional<Result> ticket = transaction.get(
          new Get(new Key("ticket_id", ticketId))
              .forNamespace("order")
              .forTable("ticket_info"));

      if (!ticket.isPresent()) {
        // If the ticket info the specified ticket ID doesn't exist, throw an exception
        throw new RuntimeException("Ticket not found");
      }

      // Commit the transaction (even when the transaction is read-only, we need to
      // commit)
      transaction.commit();

      // Return the ticket info as a JSON format
      return String.format(
          "{\"ticket_id\": %d, \"flight_id\": %d, \"price\": %d}",
          ticketId,
          ticket.get().getValue("flight_id").get().getAsInt(),
          ticket.get().getValue("price").get().getAsInt());
    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String putTicketInfo(int flightId, int price) throws TransactionException {

    DistributedTransaction transaction = null;
    try {

      // Start a transaction
      transaction = manager.start();

      Boolean exist = true;
      int ticketId = 0;
      while (!exist) {
        ticketId = (int) Math.floor(Math.random() * 1000);

        // Check if the id is already in the database
        Optional<Result> ticket = transaction.get(
            new Get(new Key("ticket_id", ticketId))
                .forNamespace("order")
                .forTable("ticket_info"));

        if (ticket.isPresent()) {
          exist = false;
        }
      }

      // Put the ticket info into the ticket_info table
      transaction.put(
          new Put(
              new Key("ticket_id", ticketId))
              .withValue("flight_id", flightId)
              .withValue("price", price)
              .forNamespace("order")
              .forTable("ticket_info"));

      // Commit the transaction (even when the transaction is read-only, we need to
      // commit)
      transaction.commit();

      // Return the ticket id as a JSON format
      return String.format(
          "{\"ticket_id\": %d}", ticketId);

    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String getFlightInfo(int flightId) throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      // Retrieve the flight info for the specified flight ID from the flight_details
      // table
      Optional<Result> flight = transaction.get(
          new Get(new Key("flight_id", flightId))
              .forNamespace("customer")
              .forTable("flight_details"));

      if (!flight.isPresent()) {
        // If the flight info the specified flight ID doesn't exist, throw an exception
        throw new RuntimeException("Flight not found");
      }

      // Commit the transaction (even when the transaction is read-only, we need to
      // commit)
      transaction.commit();

      // Return the flight info as a JSON format
      return String.format(
          "{\"flight_id\": %d, \"flight_departure_date\": \"%s\", \"flight_arrival_date\": \"%s\", \"airline_id\": %d, \"airline_name\": \"%s\", \"trip\": \"%s\", \"duration\": \"%s\", \"total_seats\": %d, \"available_seats\": %d}",
          flightId,
          flight.get().getValue("flight_departure_date").get().getAsString().get(),
          flight.get().getValue("flight_arrival_date").get().getAsString().get(),
          flight.get().getValue("airline_id").get().getAsInt(),
          flight.get().getValue("airline_name").get().getAsString().get(),
          flight.get().getValue("trip").get().getAsString().get(),
          flight.get().getValue("duration").get().getAsString().get(),
          flight.get().getValue("total_seats").get().getAsInt(),
          flight.get().getValue("available_seats").get().getAsInt());
    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String putFlightInfo(
      String flightDepartureDate,
      String flightArrivalDate,
      int airlineId,
      String airlineName,
      String trip,
      String duration,
      int totalSeats,
      int availableSeats) throws TransactionException {

    DistributedTransaction transaction = null;
    try {

      // Start a transaction
      transaction = manager.start();

      Boolean exist = true;
      int flightId = 0;
      while (!exist) {
        flightId = (int) Math.floor(Math.random() * 1000);

        // Check if the id is already in the database
        Optional<Result> flight = transaction.get(
            new Get(new Key("flight_id", flightId))
                .forNamespace("customer")
                .forTable("flight_details"));

        if (flight.isPresent()) {
          exist = false;
        }

      }

      // Put the flight info into the flight_details table
      transaction.put(
          new Put(
              new Key("flight_id", flightId))
              .withValue("flight_departure_date", flightDepartureDate)
              .withValue("flight_arrival_date", flightArrivalDate)
              .withValue("airline_id", airlineId)
              .withValue("airline_name", airlineName)
              .withValue("trip", trip)
              .withValue("duration", duration)
              .withValue("total_seats", totalSeats)
              .withValue("available_seats", availableSeats)
              .forNamespace("customer")
              .forTable("flight_details"));

      // Commit the transaction (even when the transaction is read-only, we need to
      // commit)
      transaction.commit();

      // Return the flight id as a JSON format
      return String.format(
          "{\"flight_id\": %d}", flightId);

    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String getCreditCardInfo(int profileId) throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      // Retrieve the credit card info for the specified profile ID from the
      // credit_card_details table
      Optional<Result> credit_card = transaction.get(
          new Get(new Key("profile_id", profileId))
              .forNamespace("customer")
              .forTable("credit_card_details"));

      if (!credit_card.isPresent()) {
        // If the credit card info the specified profile ID doesn't exist, throw an
        // exception
        throw new RuntimeException("Credit Card not found");
      }

      // Commit the transaction (even when the transaction is read-only, we need to
      // commit)
      transaction.commit();

      // Return the credit card info as a JSON format
      return String.format(
          "{\"profile_id\": %d, \"card_number\": %d, \"card_type\": \"%s\", \"expiration_date\": \"%s\", \"credit_limit\": %d, \"spending\": %d}",
          profileId,
          credit_card.get().getValue("card_number").get().getAsInt(),
          credit_card.get().getValue("card_type").get().getAsString().get(),
          credit_card.get().getValue("expiration_date").get().getAsString().get(),
          credit_card.get().getValue("credit_limit").get().getAsInt(),
          credit_card.get().getValue("spending").get().getAsInt());
    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String placeOrder(int profileId, int[] ticketIds, int[] ticketCounts)
      throws TransactionException {
    assert ticketIds.length == ticketCounts.length;

    DistributedTransaction transaction = null;
    try {
      String orderId = UUID.randomUUID().toString();

      // Start a transaction
      transaction = manager.start();

      // Put the order info into the orders table
      transaction.put(
          new Put(
              new Key("profile_id", profileId),
              new Key("timestamp", System.currentTimeMillis()))
              .withValue("order_id", orderId)
              .forNamespace("order")
              .forTable("orders"));

      int amount = 0;
      for (int i = 0; i < ticketIds.length; i++) {
        int ticketId = ticketIds[i];
        int count = ticketCounts[i];

        // Put the order statement into the statements table
        transaction.put(
            new Put(new Key("order_id", orderId), new Key("ticket_id", ticketId))
                .withValue("count", count)
                .forNamespace("order")
                .forTable("statements"));

        // Retrieve the ticket info from the ticket table
        Optional<Result> ticket = transaction.get(
            new Get(
                new Key("ticket_id", ticketId))
                .forNamespace("order")
                .forTable("ticket_info"));
        if (!ticket.isPresent()) {
          throw new RuntimeException("Ticket not found");
        }

        // Calculate the total amount
        amount += ticket.get().getValue("price").get().getAsInt() * count;

        int flightId = ticket.get().getValue("flight_id").get().getAsInt();
        // Retrieve the ticket info from the ticket table
        Optional<Result> flight = transaction.get(
            new Get(
                new Key("flight_id", flightId))
                .forNamespace("customer")
                .forTable("flight_details"));
        if (!flight.isPresent()) {
          throw new RuntimeException("Flight not found");
        }

        int availableSeats = flight.get().getValue("available_seats").get().getAsInt();

        if (availableSeats - count < 0) {
          throw new RuntimeException("Maximum number of seats exceeded");
        }

        // Update the number of available seats
        transaction.put(
            new Put(
                new Key("flight_id", flightId))
                .withValue("available_seats", availableSeats - count)
                .forNamespace("customer")
                .forTable("flight_details"));
      }

      // Check if the spending exceeds the credit limit after payment
      Optional<Result> credit_card = transaction.get(
          new Get(new Key("profile_id", profileId))
              .forNamespace("customer")
              .forTable("credit_card_details"));
      if (!credit_card.isPresent()) {
        throw new RuntimeException("Credit card of the passenger not found");
      }
      int creditLimit = credit_card.get().getValue("credit_limit").get().getAsInt();
      int spending = credit_card.get().getValue("spending").get().getAsInt();
      if (spending + amount > creditLimit) {
        throw new RuntimeException("Credit limit exceeded");
      }

      // Update spending for the passenger
      transaction.put(
          new Put(new Key("profile_id", profileId))
              .withValue("spending", spending + amount)
              .forNamespace("customer")
              .forTable("credit_card_details"));

      // Commit the transaction
      transaction.commit();

      // Return the order id
      return String.format("{\"order_id\": \"%s\"}", orderId);
    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  private String getOrderJson(DistributedTransaction transaction, String orderId)
      throws TransactionException {
    // Retrieve the order info for the order ID from the orders table
    Optional<Result> order = transaction.get(
        new Get(new Key("order_id", orderId)).forNamespace("order").forTable("orders"));
    if (!order.isPresent()) {
      throw new RuntimeException("Order not found");
    }

    int profileId = order.get().getValue("profile_id").get().getAsInt();

    // Retrieve the passenger info for the specified profile ID from the
    // passenger_profile table
    Optional<Result> passenger = transaction.get(
        new Get(new Key("profile_id", profileId))
            .forNamespace("customer")
            .forTable("passenger_profile"));

    // Retrieve the order statements for the order ID from the statements table
    List<Result> statements = transaction.scan(
        new Scan(new Key("order_id", orderId)).forNamespace("order").forTable("statements"));

    // Make the statements JSONs
    List<String> statementJsons = new ArrayList<>();
    int total = 0;
    for (Result statement : statements) {
      int ticketId = statement.getValue("ticket_id").get().getAsInt();

      // Retrieve the item data from the items table
      Optional<Result> ticket = transaction.get(
          new Get(new Key("ticket_id", ticketId)).forNamespace("order").forTable("ticket_info"));
      if (!ticket.isPresent()) {
        throw new RuntimeException("Ticket not found");
      }

      int price = ticket.get().getValue("price").get().getAsInt();
      int count = statement.getValue("count").get().getAsInt();

      statementJsons.add(
          String.format(
              "{\"ticket_id\": %d, \"flight_id\": %d, \"price\": %d, \"count\": %d, \"total\": %d}",
              ticketId,
              ticket.get().getValue("flight_id").get().getAsInt(),
              price,
              count,
              price * count));

      total += price * count;
    }

    // Return the order info as a JSON format
    return String.format(
        "{\"order_id\": \"%s\",\"timestamp\": %d,\"profile_id\": %d,\"passenger_name\": \"%s\",\"statement\": [%s],\"total\": %d}",
        orderId,
        order.get().getValue("timestamp").get().getAsLong(),
        profileId,
        passenger.get().getValue("name").get().getAsString().get(),
        String.join(",", statementJsons),
        total);
  }

  public String getOrderByOrderId(String orderId) throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      // Get an order JSON for the specified order ID
      String orderJson = getOrderJson(transaction, orderId);

      // Commit the transaction (even when the transaction is read-only, we need to
      // commit)
      transaction.commit();

      // Return the order info as a JSON format
      return String.format("{\"order\": %s}", orderJson);
    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String getOrdersByPassengerId(int profileId) throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      // Retrieve the order info for the profile ID from the orders table
      List<Result> orders = transaction.scan(
          new Scan(new Key("profile_id", profileId))
              .forNamespace("order")
              .forTable("orders"));

      // Make order JSONs for the orders of the customer
      List<String> orderJsons = new ArrayList<>();
      for (Result order : orders) {
        orderJsons.add(
            getOrderJson(transaction, order.getValue("order_id").get().getAsString().get()));
      }

      // Commit the transaction (even when the transaction is read-only, we need to
      // commit)
      transaction.commit();

      // Return the order info as a JSON format
      return String.format("{\"order\": [%s]}", String.join(",", orderJsons));
    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String placeOrderExchangeTickets(
      DistributedTransaction transaction,
      int profileId, int ticketId) throws TransactionException {

    String orderId = UUID.randomUUID().toString();
    // Put the order info into the orders table
    transaction.put(
        new Put(
            new Key("profile_id", profileId),
            new Key("timestamp", System.currentTimeMillis()))
            .withValue("order_id", orderId)
            .forNamespace("order")
            .forTable("orders"));

    // Put the order statement into the statements table
    transaction.put(
        new Put(new Key("order_id", orderId), new Key("ticket_id", ticketId))
            .withValue("count", 1)
            .forNamespace("order")
            .forTable("statements"));

    // Return the order id
    return String.format("{\"profile_id\": %d, \"order_id\": \"%s\"}",
        profileId,
        orderId);
  }

  public String exchangeTickets(int profile1Id, int ticket1Id, int profile2Id, int ticket2Id)
      throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      // Check if passenger 1 has ticket 1
      // Retrieve the order info for the profile 1 from the orders table
      List<Result> orders1 = transaction.scan(
          new Scan(new Key("profile_id", profile1Id))
              .forNamespace("order")
              .forTable("orders"));

      boolean find1 = false;
      for (Result order : orders1) {
        String order1Id = order.getValue("order_id").get().getAsString().get();
        Optional<Result> statement = transaction.get(
            new Get(
                new Key("order_id", order1Id),
                new Key("ticket_id", ticket1Id))
                .forNamespace("order")
                .forTable("statements"));

        if (statement.isPresent()) {
          find1 = true;
          break;
        }
      }
      if (!find1) {
        throw new RuntimeException("Ticket not found for the first passenger");
      }

      // Check if passenger 2 has ticket 2
      // Retrieve the order info for the profile 2 from the orders table
      List<Result> orders2 = transaction.scan(
          new Scan(new Key("profile_id", profile2Id))
              .forNamespace("order")
              .forTable("orders"));

      boolean find2 = false;
      for (Result order : orders2) {
        String order2Id = order.getValue("order_id").get().getAsString().get();
        Optional<Result> statement = transaction.get(
            new Get(
                new Key("order_id", order2Id),
                new Key("ticket_id", ticket2Id))
                .forNamespace("order")
                .forTable("statements"));

        if (statement.isPresent()) {
          find2 = true;
          break;
        }
      }
      if (!find2) {
        throw new RuntimeException("Ticket not found for the second passenger");
      }

      // Make order JSONs for the exchange of the passengers
      List<String> orderJsons = new ArrayList<>();
      orderJsons.add(placeOrderExchangeTickets(transaction, profile1Id, ticket1Id));
      orderJsons.add(placeOrderExchangeTickets(transaction, profile2Id, ticket2Id));

      // Commit the transaction
      transaction.commit();

      // Return the order info as a JSON format
      return String.format("{\"order\": [%s]}", String.join(",", orderJsons));

    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public String addAvailableSeats(int flightId, int seatsToAdd) throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      // Retrieve the flight info for the specified flight ID from the flight_details
      // table
      Optional<Result> flight = transaction.get(
          new Get(new Key("flight_id", flightId))
              .forNamespace("customer")
              .forTable("flight_details"));

      if (!flight.isPresent()) {
        // If the flight info the specified flight ID doesn't exist, throw an exception
        throw new RuntimeException("Flight not found");
      }

      int numberAvailableSeats = flight.get().getValue("available_seats").get().getAsInt();
      int totalSeatsNumber = flight.get().getValue("total_seats").get().getAsInt();

      // Check if there are enough seats in the plane
      if (numberAvailableSeats + seatsToAdd > totalSeatsNumber) {
        throw new RuntimeException("Maximum number of seats exceeded");
      }

      // Put the flight info into the flight_details table
      transaction.put(
          new Put(
              new Key("flight_id", flightId))
              .withValue("available_seats", numberAvailableSeats + seatsToAdd)
              .forNamespace("customer")
              .forTable("flight_details"));

      // Commit the transaction
      transaction.commit();

      // Return the order id
      return String.format("{\"flight_id\": %d, \"available_seats\": %d}",
          flightId,
          numberAvailableSeats + seatsToAdd);

    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  public void repayment(int profileId, int amount) throws TransactionException {
    DistributedTransaction transaction = null;
    try {
      // Start a transaction
      transaction = manager.start();

      // Retrieve the credit card info for the specified profile ID from the
      // credit_card_details
      Optional<Result> credit_card = transaction.get(
          new Get(new Key("profile_id", profileId))
              .forNamespace("customer")
              .forTable("credit_card_details"));
      if (!credit_card.isPresent()) {
        throw new RuntimeException("Credit card corresponding to the profile not found");
      }

      int updatedCreditLimit = credit_card.get().getValue("spending").get().getAsInt() - amount;

      // Check if over repayment or not
      if (updatedCreditLimit < 0) {
        throw new RuntimeException("Over repayment");
      }

      // Reduce spending in the customer
      transaction.put(
          new Put(new Key("profile_id", profileId))
              .withValue("spending", updatedCreditLimit)
              .forNamespace("customer")
              .forTable("credit_card_details"));

      // Commit the transaction
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        // If an error occurs, abort the transaction
        transaction.abort();
      }
      throw e;
    }
  }

  @Override
  public void close() {
    manager.close();
  }
}
