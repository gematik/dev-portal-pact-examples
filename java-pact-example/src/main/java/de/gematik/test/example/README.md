# Example Client App

This project contains an example consumer client (`ExampleClientApp`) that is tested using Pact.

`ExampleClientTest` shows how to write a Pact consumer test.
Running the test generates a `.pact` file in the `target` folder.

`ConsumerBrokerUtils` shows how to:
- publish the generated Pact contract to the Pact Broker
- run `canIDeploy` checks to detect compatibility errors

## Configuration

Configure broker and participant values in `src/test/resources/pactconfig.properties`.
For local overrides, use `src/test/resources/pactconfig-local.properties`.

To publish contracts or run `canIDeploy`, you need a Pact Broker API token.
Set the token in `pact.broker.api.key` (preferably in `pactconfig-local.properties`, which should not be committed).

