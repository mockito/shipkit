## How we work

We are a community project and we work as a team.
 - See [CONTRIBUTING.md](../CONTRIBUTING.md) for how to contribute
 - See [documentation index](../README.md#documentation)

### Getting contributions

Contributions are super important to build lively community and excite other engineers around the project vision. In "shipkit" we use ["please contribute!"](https://github.com/mockito/shipkit/issues?q=is%3Aissue+is%3Aopen+label%3A%22please+contribute%21%22) label on tickets to attract contributors. Before the ticket can be labeled with "please contribute!" it should:
 - sell the feature. If the value of the ticket is not clear, why would a contributor bother to invest his free time?
 - describe high level design. Otherwise it's hard to come up with the implementation. More over, lack of guiding high level design leads to more rework and ping-pong pull requests, with multiple pr->feedback->fixes cycles.
 - give a starting point, point to classes in question, suggest implementation, or even point to the branch that has some scaffolding prototype code. Example ["starting point" PR](https://github.com/mockito/shipkit/pull/100).
 - be like [#101](https://github.com/mockito/shipkit/issues/101), [#84](https://github.com/mockito/shipkit/issues/84), or [epic #85](https://github.com/mockito/shipkit/issues/85)

### How to give great code review feedback?

We can teach anyone to code but we cannot teach anyone to love coding.
Great code reviews can.
Great code review feedback streamlines team's productivity, amplifies learning and makes working together fun and engaging.

#### Great code review comments, non-technical

- try to give plenty of detailed, positive feedback.
It needs details because otherwise it does not have the positive effect we want to create.
We want fun, engaging environment where it’s a pleasure engineering!
If you see something nice, say it: “I like this change because it reduces complexity - we no longer need to maintain ...”, “-100 lines of code out our project - less maintenance, faster delivery!”.
- try to show gratitude and cherish every PR.
If there are no PRs from others, you would have to implement everything yourself.
One man wolf pack can hardly delivery an important, big project.
- try to avoid nitpicking, especially related to code formatting.
Tools should validate code format, not humans!
Instead of nitpicking on code format, setup automation to validate code style automatically.
Nitpicking diverts attention from important code comments, creates discussions around low priority code changes, slows down team’s momentum.
- try to thoroughly explain the “why”.
What’s obvious to you, is never obvious to other people.
Examples: “extracting out this class will save 50 lines of code”, “this refactoring will make the code easier to test because …”.
- try to clearly indicate if you want an action to be taken.
A comment like “It would be nice to change …” is not very clear.
How about “long term we might do X, at the moment no action needed”, “let’s add more unit tests”.
- try to clearly indicate importance: “this will break X”, “I think it is really important to ...”
- try to accept how others think.
There is no single, golden way of implementing any feature.
Request changes but respect if the submitter wants to do things his way.
Example: make recommendation how to change the code but still approve the PR and let the submitter decide whether to take the recommendation or not.
This typically applies only to core team members because outside contributors cannot merge.

#### Code review technical hints

- try to keep the momentum, allow to merge PRs even though some rough edges are not smooth yet, so long we don’t break compatibility.
Splitting bigger changes across multiple PRs is sometimes very useful and enables to keep nice pace of development.
Otherwise we’re bogged down reviewing the same PR over and over again.
Example: “this PR is getting big. How about we push ... but leave … for the next PR?”.
- pushing directly to the branch the contributor submitted the PR from is very powerful.
Instead of leaving lots of small suggestions that are typically hard to manage in any code review tool, you can make changes directly on the contributor’s branch and push.
This technique is very useful when pushing TODOs directly to the submitter’s branch.
TODOs are preserved in code / Git history and are very easy to manage, hard to forget.
Example, a TODO for team member with ‘SF’ initial: “TODO SF - can you try to …”.
- commit-by-commit review is very powerful. When working on a big change, having many commits usually helps.
Some commits are automated refactorings like renames and they don’t need a lot of scrutiny.
Some commits change the core of an algorithm and need to be reviewed very thoroughly.
The PR submitter can even indicate in PR description what commits require scrutiny.
