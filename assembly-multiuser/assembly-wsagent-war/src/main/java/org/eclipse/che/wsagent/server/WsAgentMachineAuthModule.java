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

import com.google.inject.AbstractModule;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.commons.auth.token.ChainedTokenExtractor;
import org.eclipse.che.commons.auth.token.RequestTokenExtractor;
import org.eclipse.che.inject.DynaModule;

/** Provide multi user specific implementation of ws-agent components. */
@DynaModule
public class WsAgentMachineAuthModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(HttpJsonRequestFactory.class).to(AgentHttpJsonRequestFactory.class);
    bind(RequestTokenExtractor.class).to(ChainedTokenExtractor.class);
  }
}
