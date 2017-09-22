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

import static javax.ws.rs.HttpMethod.POST;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.rest.DefaultHttpJsonRequestFactory;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.selenium.core.provider.TestApiEndpointUrlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Mykhailo Kuznietsov */
public class KeycloakTestAuthServiceClient implements TestAuthServiceClient {

  private static final Logger LOG = LoggerFactory.getLogger(KeycloakTestAuthServiceClient.class);

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String FORM_MIME_TYPE = "application/x-www-form-urlencoded";

  private final String apiEndpoint;
  private final KeycloakSettings keycloakSettings;
  private final DefaultHttpJsonRequestFactory requestFactory;
  private final Gson gson;
  private final ConcurrentMap<String, KeycloakToken> tokens;

  @Inject
  public KeycloakTestAuthServiceClient(
      TestApiEndpointUrlProvider cheApiEndpointProvider,
      DefaultHttpJsonRequestFactory requestFactory) {
    this.apiEndpoint = cheApiEndpointProvider.get().toString();
    this.requestFactory = requestFactory;
    this.gson = new Gson();
    this.tokens = new ConcurrentHashMap<>();
    this.keycloakSettings = getKeycloakConfiguration();
  }

  @Override
  public String login(String username, String password) throws Exception {
    final KeycloakToken token = tokens.get(username);
    if (token != null) { // TODO is token valid
      return token.getAccessToken();
    }
    final KeycloakToken newToken = getAccessToken(username, password);
    tokens.putIfAbsent(username, newToken);
    return newToken.getAccessToken();
  }

  @Override
  public void logout(String authToken) {
    try {
      requestFactory.fromUrl(keycloakSettings.getKeycloakLogoutEndpoint()).request();
    } catch (ApiException | IOException ex) {
      LOG.error(ex.getLocalizedMessage(), ex);
    }
  }

  private KeycloakToken getAccessToken(String username, String password) {
    KeycloakToken token = null;
    HttpURLConnection http = null;
    final String keycloakApiEndpoint = keycloakSettings.getKeycloakTokenEndpoint();
    try {
      http = (HttpURLConnection) new URL(keycloakApiEndpoint).openConnection();
      http.setRequestMethod(POST);
      http.setAllowUserInteraction(false);
      http.setRequestProperty(CONTENT_TYPE, FORM_MIME_TYPE);
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
                + keycloakApiEndpoint
                + " "
                + http.getResponseCode()
                + IoUtil.readStream(http.getErrorStream()));
      }
      output.close();

      final BufferedReader response =
          new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
      token = gson.fromJson(response, KeycloakToken.class);
    } catch (IOException | JsonSyntaxException ex) {
      LOG.error(ex.getMessage(), ex);
    } finally {
      if (http != null) {
        http.disconnect();
      }
    }
    return token;
  }

  private KeycloakSettings getKeycloakConfiguration() {
    try {
      return gson.fromJson(
          requestFactory
              .fromUrl(apiEndpoint + "keycloak/settings/")
              .useGetMethod()
              .request()
              .asString(),
          KeycloakSettings.class);
    } catch (ApiException | IOException | JsonSyntaxException ex) {
      throw new RuntimeException("Error during retrieving Che Keycloak configuration: ", ex);
    }
  }
}
