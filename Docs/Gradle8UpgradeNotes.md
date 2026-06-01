# Gradle 8 Upgrade Notes

This file tracks Gradle 8 migration warnings found during modernization work.

## Known Warnings

- `saw:compileJava` still reports broad deprecated API and unchecked operation notes. Run an `-Xlint` scout pass before the Gradle 8 wrapper upgrade to identify the remaining source-level cleanup targets.

## Resolved

- `saw:compileTestJava` and `saw:test` previously reported implicit task dependency warnings because they used output from `saw:writeProperties` without declaring a dependency. SAW now writes build properties to generated resources and wires `processResources` to `writeProperties`, matching SSW. BFB was updated to the same pattern.
- `saw/gui/HMPReader.java` previously used deprecated-for-removal `new Long(...)` constructors for HMP lookup keys. These now use `Long.valueOf(...)`.

## Follow-Up Candidates

- Run a full `--warning-mode all` pass across all modules before the Gradle 8 wrapper upgrade.
