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
package org.eclipse.che.wsagent.server;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;
import org.eclipse.che.api.core.websocket.commons.WebSocketMessageReceiver;
import org.eclipse.che.api.core.websocket.impl.BasicWebSocketEndpoint;
import org.eclipse.che.api.core.websocket.impl.GuiceInjectorEndpointConfigurator;
import org.eclipse.che.api.core.websocket.impl.MessagesReSender;
import org.eclipse.che.api.core.websocket.impl.WebSocketSessionRegistry;
import org.eclipse.che.api.core.websocket.impl.WebsocketIdService;

/**
 * Implementation of BasicWebSocketEndpoint for Che packaging. Add only mapping
 * "/websocket/{endpoint-id}".
 *
 * @author Vitalii Parfonov
 */
@ServerEndpoint(value = "/wsagent", configurator = GuiceInjectorEndpointConfigurator.class)
public class CheWebSocketEndpoint extends BasicWebSocketEndpoint {

  @Inject
  public CheWebSocketEndpoint(
      WebSocketSessionRegistry registry,
      MessagesReSender reSender,
      WebSocketMessageReceiver receiver,
      WebsocketIdService websocketIdService) {
    super(registry, reSender, receiver, websocketIdService);
  }

  @Override
  protected String getEndpointId() {
    return "ws-agent-websocket-endpoint";
  }
}
