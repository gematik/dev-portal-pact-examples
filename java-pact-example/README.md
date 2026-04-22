# Java Pact Example

This module contains a minimal Java consumer example using Pact.
It shows how to generate a consumer contract and how to work with a Pact Broker.

## Prerequisites

- Java 17 (or newer)
- Maven 3.9+ (`mvn` available on your `PATH`)

## Project layout

- `src/main/java/de/gematik/test/example/ExampleClientApp.java` - example client code
- `src/test/java/de/gematik/test/pact/ExampleClientTest.java` - Pact consumer test
- `src/test/resources/pactconfig.properties` - broker/participant defaults
- `src/test/resources/pactconfig-local.properties.template` - local override template

## Getting started

1. Open a terminal in `pact-examples/java-pact-example`.
2. Run the tests:

```bash
mvn test
```

This executes `ExampleClientTest` and generates Pact files under `target/`.

## Broker setup (optional)

If you want to publish pacts or run `canIDeploy`, configure local broker values:

1. Copy `src/test/resources/pactconfig-local.properties.template`.
2. Save it as `src/test/resources/pactconfig-local.properties`.
3. Set your values, especially `pact.broker.api.key`.

Then run tests again to publish/check depending on your test flow.

## More details

See `src/main/java/de/gematik/test/example/README.md` for additional implementation notes.
