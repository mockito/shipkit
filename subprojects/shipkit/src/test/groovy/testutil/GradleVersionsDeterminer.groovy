package testutil

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.util.GradleVersion

@CompileStatic
@Slf4j
trait GradleVersionsDeterminer {

    private final static Logger log = Logging.getLogger(GradleVersionsDeterminer)

    private static final String REGRESSION_TESTS_ENV_NAME = "SHIPKIT_REGRESSION_TESTS"
    private static final String CURRENT_GRADLE_VERSION_ONLY_VALUE = "currentOnly"
    private static final String QUICK_GRADLE_VERSIONS_VALUE = "quick"

    List<String> determineGradleVersionsToTest() {
        String regressionTestsLevel = System.getenv(REGRESSION_TESTS_ENV_NAME)
        log.debug("$REGRESSION_TESTS_ENV_NAME set to '${regressionTestsLevel}'")
        switch (regressionTestsLevel) {
            case CURRENT_GRADLE_VERSION_ONLY_VALUE:
            case null:
                return [currentGradleVersion()]
            case QUICK_GRADLE_VERSIONS_VALUE:
                return [currentGradleVersion(), "5.0", "4.10.2", "4.5.1", "4.0.2"].unique()
            default:
                log.warn("Unsupported $REGRESSION_TESTS_ENV_NAME value '$regressionTestsLevel' (expected '$CURRENT_GRADLE_VERSION_ONLY_VALUE' or " +
                    "'$QUICK_GRADLE_VERSIONS_VALUE'). Assuming '$CURRENT_GRADLE_VERSION_ONLY_VALUE'.")
                return [currentGradleVersion()]
        }
    }

    private String currentGradleVersion() {
        return GradleVersion.current().version
    }
}
