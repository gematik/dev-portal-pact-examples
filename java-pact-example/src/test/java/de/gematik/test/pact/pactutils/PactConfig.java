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

import java.nio.file.Path;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

@Data
@Slf4j
public class PactConfig {
  private String pactProviderName;

  private String pactConsumerName;

  private String pactBrokerUrl;

  private String pactBrokerApiKey;

  private String consumerVersion;

  private String consumerBranch;

  private String consumerBuildUrl;

  private String contractPath;

  public Path getPactContractPath() {
    return Path.of(System.getProperty("user.dir")).resolve(contractPath);
  }

  /*Method to create config without spring container*/
  public static PactConfig createConfig() {
    Configurations configs = new Configurations();
    CompositeConfiguration compositeConfiguration = new CompositeConfiguration();

    try {
      // First added config has priority. here we add local things that will have priority over the
      // baseconfig.
      PropertiesConfiguration localConfig = configs.properties("pactconfig-local.properties");
      compositeConfiguration.addConfiguration(localConfig);

      PropertiesConfiguration baseConfig = configs.properties("pactconfig.properties");
      compositeConfiguration.addConfiguration(baseConfig);


      PactConfig pactConfig = new PactConfig();
      pactConfig.pactProviderName = compositeConfiguration.getString("pact.provider.name");
      pactConfig.pactConsumerName = compositeConfiguration.getString("pact.consumer.name");
      pactConfig.pactBrokerUrl = compositeConfiguration.getString("pact.broker.url");
      pactConfig.pactBrokerApiKey = compositeConfiguration.getString("pact.broker.api.key");
      pactConfig.consumerVersion = compositeConfiguration.getString("pact.consumer.version");
      pactConfig.consumerBranch = compositeConfiguration.getString("pact.consumer.branch");
      pactConfig.consumerBuildUrl = compositeConfiguration.getString("pact.consumer.build.url");
      pactConfig.contractPath = compositeConfiguration.getString("pact.contract.path");

      return pactConfig;
    } catch (ConfigurationException e) {
      log.error("Unable to create configuration", e);
      throw new RuntimeException(e);
    }
  }
}
