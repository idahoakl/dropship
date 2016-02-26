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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import dagger.Module;
import dagger.Provides;
import dropship.logging.Logger;
import dropship.logging.LoggingModule;
import dropship.snitch.SnitchModule;

import javax.inject.Named;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.google.common.base.Preconditions.checkNotNull;

@Module(
  injects = Dropship.class,
  includes = {
    SettingsModule.class,
    LoggingModule.class,
    SnitchModule.class
  })
final class DropshipModule {

  private final ImmutableList<String> args;

  DropshipModule(String[] args) {
    this.args = ImmutableList.copyOf(checkNotNull(args, "args"));
  }

  @Provides
  @Named("args")
  ImmutableList<String> provideArgs() {
    return args;
  }

  @Provides
  @Named("jvm-name")
  String provideJvmName() {
    return ManagementFactory.getRuntimeMXBean().getName();
  }

  @Provides
  SimpleDateFormat provideDateFormat() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
  }

  @Provides
  PrintStream provideLoggerDestination() {
    return System.err;
  }

  @Provides
  MavenArtifactResolution.ArtifactResolutionBuilder provideArtifactResolutionBuilder(Settings settings, Logger logger) {
    Optional<String> override = settings.mavenRepoUrl();
    if (override.isPresent()) {
      List<String> urls = new ArrayList<String>();
      Scanner tokenizer = new Scanner(override.get()).useDelimiter(",");
      while(tokenizer.hasNext()) {
        urls.add(tokenizer.next());
      }

      logger.info("Will load artifacts from %s", override);
      return MavenArtifactResolution.using(settings, logger, urls);
    } else {
      logger.info("Loading artifacts from maven central repo");
      return MavenArtifactResolution.usingCentralRepo(settings, logger);
    }
  }

}
