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

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.URI;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Example Client App which shall be tested using PACT
 */
public class ExampleClientApp {

  private final URI baseUri;
  private final OkHttpClient httpClient;
  private final JsonAdapter<PatientBundle> patientBundleAdapter;

  public ExampleClientApp(URI baseUri) {
    this(baseUri, new OkHttpClient(), new Moshi.Builder().build());
  }

  ExampleClientApp(URI baseUri, OkHttpClient httpClient, Moshi moshi) {
    this.baseUri = baseUri;
    this.httpClient = httpClient;
    this.patientBundleAdapter = moshi.adapter(PatientBundle.class);
  }

  public PatientBundle requestVsd() throws IOException {
    Request request =
        new Request.Builder()
            .url(baseUri.resolve("/vsdservice/v1/vsdmbundle").toString())
            .get()
            .addHeader("Authorization", "Bearer mybeautifultoken")
            .addHeader("If-None-Match", "")
            .addHeader("PoPP", "anothertoken")
            .addHeader("DPoP", "yetanothertoken")
            .addHeader("Accept", "application/fhir+json")
            .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected status " + response.code());
      }

      var responseJson = response.body().string();
      var parsedBundle = patientBundleAdapter.fromJson(responseJson);
      if (parsedBundle == null) {
        throw new IOException("Unable to parse VSD response as PatientBundle");
      }
      return parsedBundle;
    }
  }
}
