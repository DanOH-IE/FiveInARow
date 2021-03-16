
# Five In A Row

## Instructions

There are 2 separate spring boot apps. 

1. five-in-a-row 
2. five-in-a-row-client

### five-in-a-row
This contains the server side implementation. To run

- open CLI
- navigate to `/five-in-a-row`
- run start command
  ```
  mvn spring-boot:run
  ```

### five-in-a-row-client
This contains the client side implementation, and is where the player will interact with the game

To run 
- open CLI
- navigate to `/five-in-a-row-client`
- run start command
  ```
  mvn spring-boot:run
  ```
- follow on screen instructions


### Design
This is a basic client-server implementation, using REST API for the client to communicate with the server. Client polls for the game status and updates the client as appropriate.


### Alternative solutions
Other solutions which can push notifications to the client were considered, and may be more suitable in the long-term, e.g. Websockets, GRPC. 

