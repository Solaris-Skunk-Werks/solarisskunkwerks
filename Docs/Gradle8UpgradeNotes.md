# Gradle 8 Upgrade Notes

This file tracks Gradle 8 migration warnings found during modernization work.

## Known Warnings

- `saw:compileTestJava` and `saw:test` report implicit task dependency warnings because they use output from `saw:writeProperties` without declaring a dependency. Gradle 7.6.6 disables execution optimizations to preserve correctness; Gradle 8 may treat this more strictly.
- `saw:compileJava` reports removal warnings in `saw/gui/HMPReader.java` for deprecated `new Long(...)` constructors. Replace with `Long.valueOf(...)` or autoboxing during the SAW warning cleanup pass.

## Follow-Up Candidates

- Declare explicit task dependencies between SAW test tasks and `saw:writeProperties`.
- Run a full `--warning-mode all` pass across all modules before the Gradle 8 wrapper upgrade.
