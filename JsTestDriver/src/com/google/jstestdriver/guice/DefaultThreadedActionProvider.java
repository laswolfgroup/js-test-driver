/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.jstestdriver.guice;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.ActionFactory;
import com.google.jstestdriver.ResponseStreamFactory;
import com.google.jstestdriver.RunTestsAction;
import com.google.jstestdriver.ThreadedAction;

/**
 * Provides ThreadedActions based constructor arguments. This is the default implementation for 
 * JsTestDriver.
 * 
 * @author corysmith
 */
public class DefaultThreadedActionProvider implements ThreadedActionProvider {
  private final ActionFactory actionFactory;
  private final boolean reset;
  private final boolean dryRun;
  private final List<String> tests;
  private final List<String> commands;
  private final ResponseStreamFactory responseStreamFactory;
  private final boolean captureConsole;

  @Inject
  public DefaultThreadedActionProvider(ActionFactory actionFactory,
                                ResponseStreamFactory responseStreamFactory,
                                @Named("reset") boolean reset,
                                @Named("dryRun") boolean dryRun,
                                @Named("captureConsole") boolean captureConsole,
                                @Named("tests") List<String> tests,
                                @Named("arguments") List<String> commands) {
        this.actionFactory = actionFactory;
        this.reset = reset;
        this.dryRun = dryRun;
        this.captureConsole = captureConsole;
        this.tests = tests;
        this.commands = commands;
        this.responseStreamFactory = responseStreamFactory;
  }

  public List<ThreadedAction> get() {
    List<ThreadedAction> threadedActions = new ArrayList<ThreadedAction>();

    if (reset) {
      threadedActions.add(actionFactory.createResetAction(responseStreamFactory));
    }
    if (dryRun) {
      threadedActions.add(actionFactory.createDryRunAction(responseStreamFactory));
    }
    if (!tests.isEmpty()) {
      RunTestsAction runTestsAction = actionFactory.createRunTestsAction(responseStreamFactory,
          tests, captureConsole);
      threadedActions.add(runTestsAction);
    }
    if (!commands.isEmpty()) {
      for (String cmd : commands) {
        threadedActions.add(actionFactory.createEvalAction(responseStreamFactory, cmd));
      }
    }
    return threadedActions;
  }
}