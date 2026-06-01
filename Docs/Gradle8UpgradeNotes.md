# Gradle 8 Upgrade Notes

This file tracks Gradle 8 migration warnings found during modernization work.

## Current Wrapper

- Gradle wrapper upgraded from 7.6.6 to 8.14.4.

## Known Warnings

- `saw:compileJava` still reports broad deprecated API and unchecked operation notes. Run an `-Xlint` scout pass to identify the remaining source-level cleanup targets.
- `BFB:compileJava` reports broad deprecated API and unchecked operation notes. These are Java source cleanup targets, not Gradle 8 build blockers.

## Resolved

- `saw:compileTestJava` and `saw:test` previously reported implicit task dependency warnings because they used output from `saw:writeProperties` without declaring a dependency. SAW now writes build properties to generated resources and wires `processResources` to `writeProperties`, matching SSW. BFB was updated to the same pattern.
- `saw/gui/HMPReader.java` previously used deprecated-for-removal `new Long(...)` constructors for HMP lookup keys. These now use `Long.valueOf(...)`.
- Gradle 8 reported deprecated Groovy DSL space-assignment syntax and `WriteProperties.outputFile` usage. Build scripts now use assignment syntax and `WriteProperties.destinationFile`.

## Follow-Up Candidates

- Run an `-Xlint` scout pass for SAW and BFB source warnings.
