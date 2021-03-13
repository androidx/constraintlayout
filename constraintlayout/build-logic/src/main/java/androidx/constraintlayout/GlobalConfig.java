/*
 * Copyright 2003-2012 the original author or authors.
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
 */
package androidx.constraintlayout;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;

/**
 * With the build server you are given two env variables.
 * The OUT_DIR is a temporary directory you can use to put things during the build.
 * The DIST_DIR is where you want to save things from the build.
 *
 * The build server will copy the contents of DIST_DIR to somewhere and make it available.
 *
 * WARNING:
 * The build directory is set OUTSIDE of the checked out directory by default!
 */
public abstract class GlobalConfig {
    abstract DirectoryProperty getAndroidHostOut();

    abstract DirectoryProperty getAndroidHostDist();

    abstract Property<String> getBuildNumber();

    abstract Property<String> getPomName();

    abstract Property<String> getPomDescription();

    @Inject
    public GlobalConfig(ProviderFactory providers, ObjectFactory objects, ProjectLayout layout) {
        getAndroidHostOut().convention(
                getEnv(providers, "OUT_DIR")
                        .flatMap(s -> objects.directoryProperty().dir(s))
                        .orElse(layout.getProjectDirectory().dir("../../out"))
        );
        getAndroidHostDist().convention(
                getEnv(providers, "DIST_DIR")
                        .flatMap(s -> objects.directoryProperty().dir(s))
                        .orElse(getAndroidHostOut().dir("dist"))
        );
        getBuildNumber().convention(getEnv(providers, "BUILD"));
    }

    private static Provider<String> getEnv(ProviderFactory providers, String name) {
        return providers.environmentVariable(name).forUseAtConfigurationTime();
    }
}
