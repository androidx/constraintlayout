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

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@CacheableTask
public abstract class GenerateSourcePropsTask extends DefaultTask {

    @Input
    abstract Property<String> getGroupId();

    @Input
    abstract Property<String> getArtifactId();

    @Input
    abstract Property<String> getVersion();

    @Input
    abstract Property<String> getPomDescription();

    @Input
    abstract ListProperty<String> getDependencies();

    @OutputDirectory
    abstract DirectoryProperty getOutputDirectory();

    @Internal
    abstract RegularFileProperty getOutputFile();

    @Inject
    public GenerateSourcePropsTask(ProviderFactory providers) {
        Project project = getProject();
        getGroupId().convention(providers.provider(() -> findMavenPublication(project).getGroupId()));
        getArtifactId().convention(providers.provider(() -> findMavenPublication(project).getArtifactId()));
        getVersion().convention(providers.provider(() -> findMavenPublication(project).getVersion()));
        getPomDescription().convention(findMavenPublication(project).getPom().getDescription());
        getDependencies().convention(providers.provider(this::collectDependencies));
        getOutputFile().set(getOutputDirectory().file(providers.provider(() -> {
            MavenPublication mavenPublication = findMavenPublication(project);
            return mavenPublication.getGroupId().replace('.', '/') + "/" +
                    mavenPublication.getArtifactId() + "/" +
                    mavenPublication.getVersion() + "/" +
                    "source.properties";
        })));
    }

    private List<String> collectDependencies() {
        Configuration implementation = getProject().getConfigurations().getByName("implementation");
        return implementation.getAllDependencies().stream()
                .filter(ProjectDependency.class::isInstance)
                .map(ProjectDependency.class::cast)
                .map(p -> {
                    MavenPublication mavenPublication = findMavenPublication(p.getDependencyProject());
                    return mavenPublication.getGroupId() + ":" + mavenPublication.getArtifactId() + ":" + mavenPublication.getVersion();
                })
                .collect(Collectors.toList());
    }

    @TaskAction
    public void generate() throws IOException {
        String content = "Maven.GroupId=" + getGroupId().get() + "\n" +
                "Maven.ArtifactId=" + getArtifactId().get() + "\n" +
                "Maven.Version=" + getVersion().get() + "\n" +
                "Pkg.Desc=" + getPomDescription().get() + " " + getVersion().get() + "\n" +
                "Pkg.Revision=1\n" +
                "Extra.VendorId=android\n" +
                "Extra.VendorDisplay=Android\n" +
                "Maven.Dependencies=" + String.join(",", collectDependencies());
        File propertiesFile = getOutputFile().getAsFile().get();
        File parentFile = propertiesFile.getParentFile();
        if (parentFile.isDirectory() || parentFile.mkdirs()) {
            Files.write(getOutputFile().getAsFile().get().toPath(), content.getBytes(StandardCharsets.UTF_8));
        }
    }

    private MavenPublication findMavenPublication(Project p) {
        PublicationContainer publications = p.getExtensions().getByType(PublishingExtension.class).getPublications();
        return publications.stream()
                .filter(MavenPublication.class::isInstance)
                .map(MavenPublication.class::cast)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Maven publication found. Did you apply the `androidx.build.publishing` plugin?"));
    }
}
