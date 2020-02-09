## Future Shipkit

Discussion of future development of Shipkit.

### What worked well

 - works well for projects like Mockito and few others
 - release notes are beautiful ;-)

### What did not work well

 - adoption is small (too opinionated? / too complicated?).
 - hard to resolve "challenging" issues that are not inline with Shipkit core design
 (concurent PR merging: [#395](https://github.com/mockito/shipkit/issues/395),
 multiple build invocations before release: [858](https://github.com/mockito/shipkit/issues/)

### Suggested future strategy

 - resolve "challenging" issues
 - reduce complexity:
  	- componentize (few smaller plugins, strip unnecessary features, some progress in [shipkit-auto-version](https://github.com/shipkit/shipkit-auto-version))
  	- reuse smartly (for example: [github-changelog-generator](https://github.com/github-changelog-generator/github-changelog-generator),
  	[jsemver](https://github.com/zafarkhaja/jsemver))
  	- decouple, make it easy to use outside of Travis CI, Bintray (example [#842](https://github.com/mockito/shipkit/issues/842))
 - make Shipkit useful for projects like [ambry](https://github.com/linkedin/ambry),
 [azkaban](https://github.com/azkaban/azkaban))
