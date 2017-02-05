package org.mockito.release.notes.improvements;

import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;

import java.util.Collection;

public interface ImprovementsProvider {

    Collection<Improvement> getImprovements(ContributionSet contributions);
}
