/*
 * Copyright (C) 2014 zulily, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dropship;

import com.google.common.collect.Iterables;
import dagger.ObjectGraph;
import dropship.logging.Logger;
import dropship.snitch.Snitch;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Dropship main class. This class contains the entry point when dropship is
 * run as an executable jar.
 */
public final class Dropship {

  /**
   * Dropship command-line interface. Arguments specify an artifact by
   * group ID, artifact ID, and an optional version. This specification is
   * either explicit, using {@code group:artifact[:version]} syntax,
   * or implicit via an alias.
   */
  public static void main(String[] args) throws Exception {
    ObjectGraph.create(new DropshipModule(args)).get(Dropship.class).run();
  }

  private final Settings settings;
  private final Logger logger;
  private final Snitch snitch;
  private final ClassLoaderService classloaderService;

  @Inject
  Dropship(Settings settings, ClassLoaderService classloaderService, Logger logger, Snitch snitch) {
    this.settings = checkNotNull(settings, "settings");
    this.classloaderService = checkNotNull(classloaderService, "class loader service");
    this.logger = checkNotNull(logger, "logger");
    this.snitch = checkNotNull(snitch, "snitch");
  }

  private void run() throws Exception {
    logger.info("Starting Dropship v%s", settings.dropshipVersion());

    URLClassLoader loader = classloaderService.getClassLoader();

    if (loader == null) {
      logger.warn("Could not create class loader; shutting down");
      System.exit(1);
    }

    logger.info("Loading main class %s", settings.mainClassName());

    Class<?> mainClass = loader.loadClass(settings.mainClassName());

    Thread.currentThread().setContextClassLoader(loader);

    Method mainMethod = mainClass.getMethod("main", String[].class);

    logger.info("Invoking main method of %s", mainClass.getName());

    System.setProperty("dropship.running", "true");

    snitch.start();

    mainMethod.invoke(null, (Object) Iterables.toArray(settings.commandLineArguments(), String.class));
  }
}
