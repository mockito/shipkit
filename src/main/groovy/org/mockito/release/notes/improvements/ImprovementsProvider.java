package org.mockito.release.notes.improvements;

import org.mockito.release.notes.model.ContributionSet;

import java.util.Collection;
import java.util.Map;

public interface ImprovementsProvider {

    Collection<Improvement> getImprovements(ContributionSet contributions, Map<String, String> labels);
}
