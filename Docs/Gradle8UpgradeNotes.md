# Gradle 8 Upgrade Notes

This file tracks Gradle 8 migration warnings found during modernization work.

## Known Warnings

- `saw:compileJava` reports removal warnings in `saw/gui/HMPReader.java` for deprecated `new Long(...)` constructors. Replace with `Long.valueOf(...)` or autoboxing during the SAW warning cleanup pass.

## Resolved

- `saw:compileTestJava` and `saw:test` previously reported implicit task dependency warnings because they used output from `saw:writeProperties` without declaring a dependency. SAW now writes build properties to generated resources and wires `processResources` to `writeProperties`, matching SSW. BFB was updated to the same pattern.

## Follow-Up Candidates

- Run a full `--warning-mode all` pass across all modules before the Gradle 8 wrapper upgrade.
