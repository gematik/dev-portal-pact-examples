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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ConsumerBrokerUtils {
  private final PactConfig pactConfig;
  private final PactBrokerClient pactBrokerClient;

  public static void main(String[] args) throws IOException {
    ConsumerBrokerUtils utils = new ConsumerBrokerUtils();
    utils.publishPact();
    boolean isDeployable = utils.canIDeploy("test");
    log.info("isDeployable = {}", isDeployable);
  }

  public ConsumerBrokerUtils() {
    this.pactConfig = PactConfig.createConfig();
    this.pactBrokerClient =
        new PactBrokerClient(pactConfig.getPactBrokerUrl(), pactConfig.getPactBrokerApiKey());
  }

  public boolean canIDeploy(String environmentName) throws IOException {
    log.info("Can I deploy?");
    var targetEnvironment = StringUtils.isNotBlank(environmentName) ? environmentName : "test";

    var pactResponse =
        pactBrokerClient.canIDeploy(
            pactConfig.getPactConsumerName(), pactConfig.getConsumerVersion(), targetEnvironment);

    log.info(pactResponse.getSummary().getReason());
    log.info(String.valueOf(pactResponse.getMatrix()));
    return pactResponse.getSummary().isDeployable();
  }

  public void publishPact() throws IOException {
    String pactFile = Files.readString(pactConfig.getPactContractPath());

    var publishPactRequest =
        PactBrokerClient.PublishPactRequest.builder()
            .pacticipantName(pactConfig.getPactConsumerName())
            .pacticipantVersionNumber(pactConfig.getConsumerVersion())
            .tags(List.of())
            .branch(pactConfig.getConsumerBranch())
            .buildUrl(pactConfig.getConsumerBuildUrl())
            .contracts(
                List.of(
                    PactBrokerClient.Contract.builder()
                        .consumerName(pactConfig.getPactConsumerName())
                        .providerName(pactConfig.getPactProviderName())
                        .content(
                            Base64.getEncoder()
                                .encodeToString(pactFile.getBytes(StandardCharsets.UTF_8)))
                        .build()))
            .build();

    var publishResponse = pactBrokerClient.publishPact(publishPactRequest);
    log.info("Pact contract published");
    if (publishResponse.getNotices() != null) {
      log.info("Publish returned {} notice(s)", publishResponse.getNotices().size());
    }
  }
}
