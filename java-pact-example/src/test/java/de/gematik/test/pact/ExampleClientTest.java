/*
 * Copyright 2026, gematik GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes
 * by gematik, find details in the "Readme" file.
 */

package de.gematik.test.pact;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import de.gematik.test.example.ExampleClientApp;
import de.gematik.test.pact.pactutils.PactConfig;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@MockServerConfig(hostInterface = "localhost")
class ExampleClientTest {
  static PactConfig pactConfig = PactConfig.createConfig();

  @BeforeEach
  void beforeEach() {
    // In this example we use the PactConfig class to group all our configuration properties.
    // The Pact Framwork expects this values as a system property, so that they are resolved in the
    // @Pact Annotation.
    System.setProperty("pact.consumer.name", pactConfig.getPactConsumerName());
    System.setProperty("pact.provider.name", pactConfig.getPactProviderName());
  }

  @AfterEach()
  void afterEach() {
    System.clearProperty("pact.consumer.name");
    System.clearProperty("pact.provider.name");
  }

  @Pact(provider = "${pact.provider.name}", consumer = "${pact.consumer.name}")
  RequestResponsePact getPatientBundle(PactDslWithProvider builder) {

    System.out.println("This a test");
    return builder
        .given("A patient with ID 10 exists")
        .uponReceiving("a request to get a patient")
        .path("/vsdservice/v1/vsdmbundle")
        .method("GET")
        .willRespondWith()
        .status(200)
        .matchHeader("Content-Type", "application/json; charset=utf-8")
        .body(EXPECTED_RESPONSE_EXAMPLE)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "getPatientBundle", pactVersion = PactSpecVersion.V3)
  void testGetPatientResponseStatusCodeIs200(MockServer mockServer) throws IOException {

    ExampleClientApp exampleClientApp = initializeClient(mockServer.getUrl());

    var response = exampleClientApp.requestVsd();
    assertThat(response.getEntry().getFirst().getResource().getResourceType()).isEqualTo("Patient");
  }

  private ExampleClientApp initializeClient(String url) {
    return new ExampleClientApp(URI.create(url));
  }

  public static final String EXPECTED_RESPONSE_EXAMPLE =
"""
{
  "resourceType": "Bundle",
  "id": "019aa697-51f0-7259-8059-ba2e44cd6360",
  "meta": {
    "profile": [
      "https://gematik.de/fhir/vsdm2/StructureDefinition/VSDMBundle|1.0.0"
    ],
    "lastUpdated": "2025-07-14 15:16:17.890000+01:00"
  },
  "type": "collection",
  "timestamp": "2025-07-14 15:16:17.890000+01:00",
  "entry": [
    {
      "fullUrl": "https://gematik.de/fhir/Patient/019aa693-f5c1-7f37-a51f-0cffee08db71",
      "resource": {
        "resourceType": "Patient",
        "id": "019aa693-f5c1-7f37-a51f-0cffee08db71",
        "meta": {
          "profile": [
            "https://gematik.de/fhir/vsdm2/StructureDefinition/VSDMPatient|1.0.0"
          ]
        },
        "identifier": [
          {
            "system": "http://fhir.de/sid/gkv/kvid-10",
            "value": "L234567896"
          }
        ],
        "name": [
          {
            "use": "official",
            "text": "Lisa Lachmöwe",
            "family": "Lachmöwe",
            "_family": {
              "extension": [
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/humanname-own-name",
                  "valueString": "Lachmöwe"
                }
              ]
            },
            "given": [
              "Lisa"
            ]
          }
        ],
        "address": [
          {
            "type": "physical",
            "line": [
              "Möwenstraße 12b"
            ],
            "_line": [
              {
                "extension": [
                  {
                    "url": "http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-houseNumber",
                    "valueString": "12b"
                  },
                  {
                    "url": "http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-streetName",
                    "valueString": "Möwenstraße"
                  }
                ]
              }
            ],
            "city": "Lüneburg",
            "postalCode": "21337",
            "country": "Deutschland",
            "_country": {
              "extension": [
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/iso21090-codedString",
                  "valueCoding": {
                    "code": "DE",
                    "system": "urn:iso:std:iso:3166"
                  }
                },
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/iso21090-codedString",
                  "valueCoding": {
                    "code": "D",
                    "system": "http://fhir.de/CodeSystem/deuev/anlage-8-laenderkennzeichen"
                  }
                }
              ]
            }
          }
        ],
        "birthDate": "2022-07-02",
        "gender": "female"
      }
    },
    {
      "fullUrl": "https://gematik.de/fhir/Coverage/019aa695-ad4a-787f-b8f4-a13e81110e87",
      "resource": {
        "resourceType": "Coverage",
        "id": "019aa695-ad4a-787f-b8f4-a13e81110e87",
        "meta": {
          "profile": [
            "https://gematik.de/fhir/vsdm2/StructureDefinition/VSDMCoverageGKV|1.0.0"
          ]
        },
        "type": {
          "coding": [
            {
              "code": "GKV",
              "system": "http://fhir.de/CodeSystem/versicherungsart-de-basis"
            }
          ]
        },
        "extension": [
          {
            "url": "http://fhir.de/StructureDefinition/gkv/wop",
            "valueCoding": {
              "system": "https://fhir.kbv.de/CodeSystem/KBV_CS_SFHIR_ITA_WOP",
              "code": "17",
              "display": "Niedersachsen"
            }
          },
          {
            "url": "http://fhir.de/StructureDefinition/gkv/versichertenart",
            "valueCoding": {
              "code": "3",
              "system": "https://fhir.kbv.de/CodeSystem/KBV_CS_SFHIR_KBV_VERSICHERTENSTATUS",
              "display": "Familienangehoerige"
            }
          },
          {
            "url": "http://fhir.de/StructureDefinition/gkv/zuzahlungsstatus",
            "extension": [
              {
                "url": "status",
                "valueBoolean": true
              },
              {
                "url": "gueltigBis",
                "valueDate": "2040-07-02"
              }
            ]
          },
          {
            "url": "http://fhir.de/StructureDefinition/gkv/kostenerstattung",
            "extension": [
              {
                "url": "aerztlicheVersorgung",
                "valueBoolean": true
              },
              {
                "url": "zahnaerztlicheVersorgung",
                "valueBoolean": true
              },
              {
                "url": "stationaererBereich",
                "valueBoolean": true
              },
              {
                "url": "veranlassteLeistungen",
                "valueBoolean": true
              }
            ]
          }
        ],
        "payor": [
          {
            "extension": [
              {
                "valueCoding": {
                  "system": "https://gematik.de/fhir/vsdm2/CodeSystem/VSDMKostentraegerRolleCS",
                  "code": "H",
                  "display": "Haupt-Kostenträger"
                },
                "url": "https://gematik.de/fhir/vsdm2/StructureDefinition/VSDMKostentraegerRolle"
              }
            ],
            "reference": "https://gematik.de/fhir/Organization/019aa693-005c-7770-a3b5-870d536e2163"
          }
        ],
        "status": "active",
        "beneficiary": {
          "reference": "https://gematik.de/fhir/Patient/019aa693-f5c1-7f37-a51f-0cffee08db71"
        },
        "period": {
          "start": "2022-07-02"
        }
      }
    },
    {
      "fullUrl": "https://gematik.de/fhir/Organization/019aa693-005c-7770-a3b5-870d536e2163",
      "resource": {
        "resourceType": "Organization",
        "id": "019aa693-005c-7770-a3b5-870d536e2163",
        "meta": {
          "profile": [
            "https://gematik.de/fhir/vsdm2/StructureDefinition/VSDMPayorOrganization|1.0.0"
          ]
        },
        "identifier": [
          {
            "system": "http://fhir.de/sid/arge-ik/iknr",
            "value": "102343996"
          }
        ],
        "address": [
          {
            "country": "Deutschland",
            "_country": {
              "extension": [
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/iso21090-codedString",
                  "valueCoding": {
                    "code": "DE",
                    "system": "urn:iso:std:iso:3166"
                  }
                },
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/iso21090-codedString",
                  "valueCoding": {
                    "code": "D",
                    "system": "http://fhir.de/CodeSystem/deuev/anlage-8-laenderkennzeichen"
                  }
                }
              ]
            }
          }
        ],
        "name": "Beispielkostenträger Lüneburg",
        "contact": [
          {
            "purpose": {
              "coding": [
                {
                  "code": "BILL",
                  "system": "http://terminology.hl7.org/CodeSystem/contactentity-type"
                }
              ],
              "text": "Kontakt für Abrechnungsfragen"
            },
            "telecom": [
              {
                "system": "phone",
                "value": "+4952116391643"
              },
              {
                "system": "email",
                "value": "contact@kostentraeger.invalid"
              }
            ]
          }
        ]
      }
    }
  ]
}
""";
}
