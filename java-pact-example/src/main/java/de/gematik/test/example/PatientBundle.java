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

package de.gematik.test.example;

import com.squareup.moshi.Json;
import java.util.List;
import lombok.Data;

/**
 * Minimal FHIR bundle model for the VSDM response payload.
 */
@Data
public class PatientBundle {
  private String resourceType;
  private String id;
  private Meta meta;
  private String type;
  private String timestamp;
  private List<Entry> entry;

  @Data
  public static class Meta {
    private List<String> profile;
    private String lastUpdated;
  }

  @Data
  public static class Entry {
    private String fullUrl;
    private Resource resource;
  }

  @Data
  public static class Resource {
    private String resourceType;
    private String id;
    private Meta meta;
    private List<Identifier> identifier;
    private Object name;
    private List<Address> address;
    private String birthDate;
    private String gender;
    private CodeableConcept type;
    private List<Extension> extension;
    private List<Reference> payor;
    private String status;
    private Reference beneficiary;
    private Period period;
    private List<Contact> contact;
  }

  @Data
  public static class Identifier {
    private String system;
    private String value;
  }

  @Data
  public static class HumanName {
    private String use;
    private String text;
    private String family;

    @Json(name = "_family")
    private PrimitiveElement familyElement;

    private List<String> given;
  }

  @Data
  public static class Address {
    private String type;
    private List<String> line;

    @Json(name = "_line")
    private List<PrimitiveElement> lineElements;

    private String city;
    private String postalCode;
    private String country;

    @Json(name = "_country")
    private PrimitiveElement countryElement;
  }

  @Data
  public static class PrimitiveElement {
    private List<Extension> extension;
  }

  @Data
  public static class Extension {
    private String url;
    private ValueCoding valueCoding;
    private String valueString;
    private Boolean valueBoolean;
    private String valueDate;
    private List<Extension> extension;
  }

  @Data
  public static class ValueCoding {
    private String code;
    private String system;
    private String display;
  }

  @Data
  public static class CodeableConcept {
    private List<Coding> coding;
    private String text;
  }

  @Data
  public static class Coding {
    private String code;
    private String system;
    private String display;
  }

  @Data
  public static class Reference {
    private String reference;
    private List<Extension> extension;
  }

  @Data
  public static class Period {
    private String start;
  }

  @Data
  public static class Contact {
    private CodeableConcept purpose;
    private List<ContactPoint> telecom;
  }

  @Data
  public static class ContactPoint {
    private String system;
    private String value;
  }
}
