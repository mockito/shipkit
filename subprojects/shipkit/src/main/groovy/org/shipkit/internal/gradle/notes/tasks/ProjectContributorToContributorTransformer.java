package org.shipkit.internal.gradle.notes.tasks;

import org.shipkit.internal.notes.model.Contributor;
import org.shipkit.internal.notes.model.ProjectContributor;
import org.shipkit.internal.notes.util.Function;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

public class ProjectContributorToContributorTransformer extends CollectionToMapTransformer<Collection<ProjectContributor>, ProjectContributor, String, Contributor> {

    public ProjectContributorToContributorTransformer() {
        super(new Function<ProjectContributor, Map.Entry<String, Contributor>>() {
            @Override
            public Map.Entry<String, Contributor> apply(ProjectContributor projectContributor) {
                String key = projectContributor.getName();
                Contributor value = projectContributor;
                return new AbstractMap.SimpleImmutableEntry<String, Contributor>(key, value);
            }
        });
    }
}
