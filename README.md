# Airplane Ticket Management System

This is our final project for the Advanced Course in Database Systems.

## Prerequisites
- Java (OpenJDK 8 or higher)
- Gradle
- Docker, Docker Compose (not mandatory)
- MySQL
- Cassandra

## Installation

### With Docker 
It is possible to use Docker to install this application but we did not use it so it might not work and you might need to modify the `database-docker.properties`.

You need to run the following `docker-compose` command:
```
docker-compose up -d
```

This command starts Cassandra and MySQL, and loads the schema.

### Without docker
You first need to start your MySQL and Cassandra database manually.
You can then load the schema of the data with the following command:
```
java -jar scalardb-schema-loader-3.5.2.jar --config database.properties --schema-file schema.json --coordinator
```

Or this one if you prefer to use a postgresql database instead of a mysql one.
```
java -jar scalardb-schema-loader-3.5.2.jar --config database_postgresql.properties --schema-file schema.json --coordinator
```

## Schema

[The schema](schema.json) is as follows:

```json
{
  "customer.passenger_profile": {
    "transaction": true,
    "partition-key": [
      "profile_id"
    ],
    "columns": {
      "profile_id": "INT",
      "name": "TEXT",
      "address": "TEXT",
      "tel_no": "TEXT",
      "email": "TEXT",
      "password": "TEXT"
    }
  },
  "customer.credit_card_details": {
    "transaction": true,
    "partition-key": [
      "profile_id"
    ],
    "columns": {
      "profile_id": "INT",
      "card_number": "INT",
      "card_type": "TEXT",
      "expiration_date": "TEXT",
      "credit_limit": "INT",
      "spending": "INT"
    }
  },
  "customer.flight_details": {
    "transaction": true,
    "partition-key": [
      "flight_id"
    ],
    "columns": {
      "flight_id": "INT",
      "flight_departure_date": "TEXT",
      "flight_arrival_date": "TEXT",
      "airline_id": "INT",
      "airline_name": "TEXT",
      "trip": "TEXT",
      "duration": "TEXT",
      "total_seats": "INT",
      "available_seats": "INT"
    }
  },
  "order.orders": {
    "transaction": true,
    "partition-key": [
      "profile_id"
    ],
    "clustering-key": [
      "timestamp"
    ],
    "secondary-index": [
      "order_id"
    ],
    "columns": {
      "order_id": "TEXT",
      "profile_id": "INT",
      "timestamp": "BIGINT"
    }
  },
  "order.statements": {
    "transaction": true,
    "partition-key": [
      "order_id"
    ],
    "clustering-key": [
      "ticket_id"
    ],
    "columns": {
      "order_id": "TEXT",
      "ticket_id": "INT",
      "count": "INT"
    }
  },
  "order.ticket_info": {
    "transaction": true,
    "partition-key": [
      "ticket_id"
    ],
    "columns": {
      "ticket_id": "INT",
      "flight_id": "INT",
      "price": "INT"
    }
  }
}
```

The `passenger_profile`, `credit_card_details`and `flight_details` tables are created in the `customer` namespace. And the `orders`, `statements`, and `ticket_info` tables are created in the `order` namespace.


## Transactions

The following transactions are implemented in this application:

1. Getting information of a passenger, a credit card, a ticket and a flight.
3. Placing an order. An order is paid by a credit card. It first checks if the amount of the money of the order exceeds the credit limit of the card. If the check passes, it records order histories and updates the `spending` in the `credit_card_details` table.
4. Getting order information by order ID
5. Getting order information by customer ID
6. Exchange tickets between two passengers.
7. Put new passengers with their credit cards, flights and tickets in the databse.
8. Add some available seats for a specific flight. It increase the `available_seats` number.
9. Repayment. It reduces the amount of `spending`.

## Run the application

### Load Initial data

You first need to load initial data with the following command:
```
# ./gradlew run --args="LoadInitialData"
```
It will load the initial data as presented on the 6th, 7th and 8th slides of the [presentation](Final Database Presentation.pdf) of the project. 


### The get functions

Let's start with getting the passenger information:
```
# ./gradlew run --args="GetPassengerInfo <passenger_id>"
```

For example for the passenger whose ID is `1`:
```
# ./gradlew run --args="GetPassengerInfo 1"
...
{"id": 1, "name": "Passenger n°1", "address": "Hiyoshi International Dormitory", "tel_no": "N/A", "email": "passenger1@keio.jp", "password": "azertyuiop"}
...
```


To get the information of a credit card the command is the following one:
```
# ./gradlew run --args="GetCreditCardInfo <passenger_id>"
```


To get the information of a flight the command is the following one:
```
# ./gradlew run --args="GetFlightInfo <flight_id>"
```


Finally, to get the information of a ticket the command is the following one:
```
# ./gradlew run --args="GetTicketInfo <ticket_id>"
```

### The put functions
You can add a new passenger in the database with the command:
```
# ./gradlew run --args="PutPassengerInfo <name> <address> <telephone_number> <email_address> <card_number> <card_type> <card_expiraton_date> <credit_limit> <spending>"
```
It will return the id of this new passenger, for example:
```
# ./gradlew run --args="PutPassengerInfo NewPassenger address 9049-498-1459 test@keio.jp yjetzj(rytj 4989948 MasterCard 07/26 80000 80000"
...
{"profile_id": 4}
...
```


You can also add a new flight:
```
# ./gradlew run --args="PutFlightInfo <flight_departure_date> <flight_arrival_date> <airline_id> <airline_name> <trip_description> <duration> <total_seats> <available_seats>"
```
It will also return the id of this new flight.


You can add a new ticket:
```
# ./gradlew run --args="PutTicketInfo <flight_id> <price>"
```
It will also return the id of this new flight.

### Order new flight tickets

You can place an order for some tickets for example: two ticket_id=1 and one ticket_id=3 with passenger ID `1`. Note that the format of order is `<Passenger ID> <Ticket ID>:<Count>,<Ticket ID>:<Count>,...`:
```
# ./gradlew run --args="PlaceOrder 1 1:2,3:1"
...
{"order_id": "1895eca6-98b8-44985-a803-31fe89ade635ad"}
...
```

The command shows the order ID of the order.

You can then check the details of the order with the order ID:
```
# ./gradlew run --args="GetOrder 1895eca6-98b8-44985-a803-31fe89ade635ad"
...
{"order": {"order_id": "1895eca6-98b8-44985-a803-31fe89ade635ad","timestamp": 164548451652,"profile_id": 1,"profile_name": "Passenger n°1","statement": [{"ticket_id": 1,"flight_id": 1,"price": 1000,"count": 2,"total": 2000},{"ticket_id": 3,"flight_id": 3,"price": 2500,"count": 1,"total": 5000}],"total": 7000}}
...
```

It is also possible to obtain all the orders corresponding to a passenger with the following command:
```
# ./gradlew run --args="GetOrdersByProfileId <passenger_id>"
```
These histories are ordered by timestamp in a descending manner.

### Exchange tickets between two passengers
You can exchange tickets between passengers by using the following command:
```
# ./gradlew run --args="ExchangeTickets <profile_id_1> <ticket_id_1> <profile_id_2> <ticket_id_2>"
```
This function will first check if they both have their tickets and then create new orders with their new tickets.

### Add seats for a flight
You can add some available seats for a specific flight with the following command:
```
# ./gradlew run --args="AddAvailableSeats <flight_id> <number_of_seats_to_add>"
```
This function will return the new number of available seats for this flight.

### Repayment

If a flight is cancelled, the airline compagny must repay the passengers that is the reason why there is this function. You can use it with the following command:
```
# ./gradlew run --args="Repayment <passenger_id> <amount>"
```

## Integrity of the data
To ensure the integrity of the data, several tests are used in the functions. For example it is not possible to buy a flight ticket if you have reached your credit card's limit or you cannot add more seats that the maximum number of seats in a plane. When one of these tests is failled, an error occurs. 

## Clean up

To stop Cassandra and MySQL if you are using docker, run the following command:
```
# docker-compose down
```
