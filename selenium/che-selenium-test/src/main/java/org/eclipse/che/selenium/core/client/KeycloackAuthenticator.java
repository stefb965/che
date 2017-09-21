/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.selenium.core.client;

import static org.eclipse.che.commons.json.JsonNameConventions.CAMEL_UNDERSCORE;

import com.google.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.api.core.rest.HttpJsonResponse;
import org.eclipse.che.commons.json.JsonHelper;
import org.eclipse.che.commons.json.JsonParseException;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.selenium.core.provider.TestApiEndpointUrlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mihail Kuznyetsov
 */
public class KeycloackAuthenticator {
  private static final Logger LOG = LoggerFactory.getLogger(KeycloackAuthenticator.class);
  @Inject
  HttpJsonRequestFactory requestFactory;

  private final String cheApiEndpoint;

  private final String keycloakApiEndpoint;

  private final long tokenExpriationTime;

  private KeycloakToken keycloakToken;

  @Inject
  public KeycloackAuthenticator(TestApiEndpointUrlProvider cheApiEndpointProvider) {
    this.cheApiEndpoint = cheApiEndpointProvider.get().toString();
    this.keycloakApiEndpoint = retrieveKeycloakConfigurationFromChe();

  }

  public void checkAndGetAccessToken() {
    if (keycloakToken.getExpirationTime() > 0) {

    }
  }


  private String retrieveKeycloakConfigurationFromChe() {
    try {
      HttpJsonResponse response =
          requestFactory
              .fromUrl(cheApiEndpoint + "keycloak/settings/")
              .useGetMethod()
              .request();

      return JsonHelper.parseJson(response.asString()).getElement("che.keycloak.token.endpoint").getStringValue();
    } catch (Exception e) {
      throw new RuntimeException("Error during retrieving Che Keycloak configuration: ", e);
    }
  }

  public KeycloakToken getAccessToken(String username, String password) {
    StringBuilder jsonStringWithToken = new StringBuilder();
    BufferedReader br;
    KeycloakToken token = null;
    HttpURLConnection http = null;
    String line;
    try {
      String loginUrl = keycloakApiEndpoint;
      http = (HttpURLConnection) new URL(loginUrl).openConnection();
      http.setRequestMethod("POST");
      http.setAllowUserInteraction(false);
      http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      http.setInstanceFollowRedirects(true);
      http.setDoOutput(true);
      OutputStream output = http.getOutputStream();
      output.write(
          ("grant_type=password&client_id=che-public&username="
              + username
              + "&password="
              + password)
              .getBytes("UTF-8"));
      if (http.getResponseCode() != 200) {
        throw new RuntimeException(
            "Can not get token for user with login: '"
                + username
                + "' and password: '"
                + password
                + "' using the KeyCloak REST API. Server response code: "
                + loginUrl
                + " "
                + http.getResponseCode()
                + IoUtil.readStream(http.getErrorStream()));
      }

      output.close();
      br = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
      while ((line = br.readLine()) != null) {
        jsonStringWithToken.append(line);
      }
      token =
          JsonHelper.fromJson(
              jsonStringWithToken.toString(),
              KeycloakToken.class,
              null);

    } catch (IOException | JsonParseException e) {
      LOG.error(e.getLocalizedMessage(), e);
    } finally {
      if (http != null) {
        http.disconnect();
      }
    }
    return token;
  }
}
