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

import org.gradle.api.file.ProjectLayout;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;

public abstract class GlobalConfig {
    /**
     * We use a String to represent the repository URI instead of a DirectoryProperty
     * because we want to support both publishing to a local file repository and a
     * remote repository
     */
    abstract Property<String> getRepoLocation();

    abstract Property<String> getBuildNumber();

    abstract Property<String> getPomName();

    abstract Property<String> getPomDescription();

    @Inject
    public GlobalConfig(ProviderFactory providers, ProjectLayout rootProjectLayout) {
        getBuildNumber().convention(getEnv(providers, "BUILD"));
        getRepoLocation().convention(
                providers.gradleProperty("repo").forUseAtConfigurationTime()
                .orElse(rootProjectLayout.getBuildDirectory().dir("repo").map(d -> d.getAsFile().getAbsolutePath()))
        );
    }

    private static Provider<String> getEnv(ProviderFactory providers, String name) {
        return providers.environmentVariable(name).forUseAtConfigurationTime();
    }
}
