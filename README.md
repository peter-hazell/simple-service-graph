# simple-service-graph

This service uses [cytoscape.js](https://js.cytoscape.org/) to create a graph visualisation of 
service to service interactions based on the static analysis of application config (`.conf`) files that describe a 
service's interactions.

It depends on having a system of services that follow a standard pattern within the application config
files for statically describing each of it's service interactions.

# How to use

## Pre-configuration
- The application config files for your services should contain the pattern in this 
example to describe the other services that it needs to interact with. E.g. the following would be the config 
file for `service-a`; which interacts with `service-b`, `service-c` and `service-d`):
  
  ```
  microservice {
    services {
      service-b {
        host = localhost
        port = 2222
      }
  
      service-c {
        host = localhost
        port = 3333
      }
  
      service-d {
        host = localhost
        port = 4444
      }
    }
  }
  ```
- In the root of this project, rename the file `local.example.conf` to `local.conf`.
- Update the values in `local.conf` to the correct ones as per the comments.

## How to run
1. Execute `sbt run`.
2. Go to [http://localhost:9003/simple-service-graph]()
3. Enter the service name you want to generate a graph for and click the button `Generate service graph`.