package org.shipkit.internal.gradle.notes.tasks;

import org.shipkit.internal.gradle.util.team.TeamMember;
import org.shipkit.internal.gradle.util.team.TeamParser;
import org.shipkit.internal.notes.contributors.DefaultContributor;
import org.shipkit.internal.notes.model.Contributor;
import org.shipkit.internal.notes.util.Function;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

public class StringToDefaultContributorTransformer extends CollectionToMapTransformer<Collection<String>, String, String, Contributor> {

    public StringToDefaultContributorTransformer(final String githubUrl) {
        super(new Function<String, Map.Entry<String, Contributor>>() {
            @Override
            public Map.Entry<String, Contributor> apply(String s) {
                TeamMember member = TeamParser.parsePerson(s);
                String key = member.name;
                Contributor value = new DefaultContributor(member.name, member.gitHubUser,
                    githubUrl + "/" + member.gitHubUser);
                return new AbstractMap.SimpleImmutableEntry<String, Contributor>(key, value);
            }
        });
    }
}
