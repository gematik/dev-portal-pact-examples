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
package de.gematik.test.pact.pactutils;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PactBrokerClient {
  private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

  private final String pactBrokerUrl;
  private final String pactBrokerApiKey;
  private final OkHttpClient okHttpClient;
  private final JsonAdapter<PublishPactRequest> publishPactRequestAdapter;
  private final JsonAdapter<PublishPactResponse> publishPactResponseAdapter;
  private final JsonAdapter<CanIReleaseResponse> canIReleaseResponseAdapter;

  public PactBrokerClient(String pactBrokerUrl, String pactBrokerApiKey) {
    this(pactBrokerUrl, pactBrokerApiKey, new OkHttpClient(), new Moshi.Builder().build());
  }

  PactBrokerClient(
      String pactBrokerUrl, String pactBrokerApiKey, OkHttpClient okHttpClient, Moshi moshi) {
    this.pactBrokerUrl = pactBrokerUrl;
    this.pactBrokerApiKey = pactBrokerApiKey;
    this.okHttpClient = okHttpClient;
    this.publishPactRequestAdapter = moshi.adapter(PublishPactRequest.class);
    this.publishPactResponseAdapter = moshi.adapter(PublishPactResponse.class);
    this.canIReleaseResponseAdapter = moshi.adapter(CanIReleaseResponse.class);
  }

  public PublishPactResponse publishPact(PublishPactRequest publishPactRequest) throws IOException {
    var url = "%s/contracts/publish".formatted(pactBrokerUrl);

    Headers headers =
        Headers.of(
            "Content-Type",
            "application/json",
            "Accept",
            "application/hal+json",
            "Authorization",
            "Bearer %s".formatted(pactBrokerApiKey));

    var requestJson = publishPactRequestAdapter.toJson(publishPactRequest);
    RequestBody body = RequestBody.create(requestJson, JSON_MEDIA_TYPE);
    Request request = new Request.Builder().url(url).headers(headers).post(body).build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      var responseBodyString = readResponseBody(response);
      if (!response.isSuccessful()) {
        throw new IOException(responseBodyString);
      }

      var publishResponse = publishPactResponseAdapter.fromJson(responseBodyString);
      if (publishResponse == null) {
        throw new IOException("Unable to parse publish pact response");
      }
      return publishResponse;
    }
  }

  public CanIReleaseResponse canIDeploy(String pacticipant, String version, String environmentName)
      throws IOException {
    var url = "%s/can-i-deploy".formatted(pactBrokerUrl);

    Headers headers =
        Headers.of(
            "Accept",
            "application/hal+json",
            "Authorization",
            "Bearer %s".formatted(pactBrokerApiKey));

    HttpUrl.Builder urlBuilder =
        HttpUrl.parse(url)
            .newBuilder()
            .addQueryParameter("pacticipant", pacticipant)
            .addQueryParameter("version", version)
            .addQueryParameter("environment", environmentName);

    Request request = new Request.Builder().url(urlBuilder.build()).headers(headers).get().build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      var responseBodyString = readResponseBody(response);
      if (!response.isSuccessful()) {
        throw new IOException(responseBodyString);
      }

      var canIReleaseResponse = canIReleaseResponseAdapter.fromJson(responseBodyString);
      if (canIReleaseResponse == null) {
        throw new IOException("Unable to parse can-i-deploy response");
      }
      return canIReleaseResponse;
    }
  }

  private String readResponseBody(Response response) throws IOException {
    if (response.body() == null) {
      throw new IOException("Pact Broker response body is empty");
    }
    return response.body().string();
  }

  @Data
  public static class CanIReleaseResponse {
    private List<MatrixEntry> matrix;
    private List<Notice> notices;
    private Summary summary;
  }

  @Data
  public static class MatrixEntry {
    private Participant consumer;
    private Pact pact;
    private Participant provider;
    private VerificationResult verificationResult;
  }

  @Data
  public static class Participant {
    private String name;
    private ParticipantVersion version;

    @Json(name = "_links")
    private Links links;
  }

  @Data
  public static class ParticipantVersion {
    private String number;
    private String branch;
    private List<Branch> branches;
    private List<Branch> branchVersions;
    private List<Environment> environments;
    private List<String> tags;

    @Json(name = "_links")
    private Links links;
  }

  @Data
  public static class Branch {
    private String name;
    private boolean latest;

    @Json(name = "_links")
    private Links links;
  }

  @Data
  public static class Environment {
    private String uuid;
    private String name;
    private String displayName;
    private boolean production;
    private String createdAt;

    @Json(name = "_links")
    private Links links;
  }

  @Data
  public static class Pact {
    private String createdAt;

    @Json(name = "_links")
    private Links links;
  }

  @Data
  public static class VerificationResult {
    private boolean success;
    private String verifiedAt;

    @Json(name = "_links")
    private Links links;
  }

  @Data
  public static class Links {
    private Link self;

    @Json(name = "pfi:ui")
    private Link pfiUi;
  }

  @Data
  public static class Link {
    private String href;
    private String name;
    private boolean templated;
    private String title;
  }

  @Data
  public static class Notice {
    private String text;
    private String type;
  }

  @Data
  public static class Summary {
    private boolean deployable;
    private int failed;
    private String reason;
    private int success;
    private int unknown;
  }

  @Data
  @Builder
  public static class PublishPactRequest {
    private String pacticipantName;
    private String pacticipantVersionNumber;
    private List<String> tags;
    private String branch;
    private String buildUrl;
    private List<Contract> contracts;
  }

  @Data
  @Builder
  public static class Contract {
    private String consumerName;
    private String providerName;
    private String content;
    @Builder.Default private String contentType = "application/json";
    @Builder.Default private String specification = "pact";
  }

  @Data
  public static class PublishPactResponse {
    private List<Notice> notices;

    @Json(name = "_embedded")
    private Embedded embedded;

    @Json(name = "_links")
    private PublishPactLinks links;
  }

  @Data
  public static class Embedded {
    private Pacticipant pacticipant;
    private Version version;
  }

  @Data
  public static class Pacticipant {
    private String name;

    @Json(name = "_links")
    private Links links;
  }

  @Data
  public static class Version {
    private String number;

    @Json(name = "_links")
    private Links links;
  }

  @Data
  public static class PublishPactLinks {
    @Json(name = "pb:pacticipant")
    private Link pbPacticipant;

    @Json(name = "pb:pacticipant-version")
    private Link pbPacticipantVersion;

    @Json(name = "pb:pacticipant-version-tags")
    private List<Link> pbPacticipantVersionTags;

    @Json(name = "pb:contracts")
    private List<Link> pbContracts;
  }
}

