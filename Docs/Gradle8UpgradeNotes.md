# Gradle 8 Upgrade Notes

This file tracks Gradle 8 migration warnings found during modernization work.

## Current Wrapper

- Gradle wrapper upgraded from 7.6.6 to 8.14.4.

## Known Warnings

- `saw:compileJava` still reports unchecked operation notes. Continue clearing raw collections and Swing models in focused cleanup passes.
- `BFB:compileJava` is clean in the checked Java 17/Gradle 8 compile pass.

## Resolved

- `saw:compileTestJava` and `saw:test` previously reported implicit task dependency warnings because they used output from `saw:writeProperties` without declaring a dependency. SAW now writes build properties to generated resources and wires `processResources` to `writeProperties`, matching SSW. BFB was updated to the same pattern.
- `saw/gui/HMPReader.java` previously used deprecated-for-removal `new Long(...)` constructors for HMP lookup keys. These now use `Long.valueOf(...)`.
- Gradle 8 reported deprecated Groovy DSL space-assignment syntax and `WriteProperties.outputFile` usage. Build scripts now use assignment syntax and `WriteProperties.destinationFile`.
- BFB no longer uses deprecated `JList.getSelectedValues()` or `InputEvent.*_MASK` constants in its GUI package.
- SAW vehicle forms no longer use deprecated `InputEvent.*_MASK` constants for menu accelerators.
- SAW `HTMLWriter` no longer emits the large raw collection warning cluster during the checked Java 17/Gradle 8 compile pass.
- SAW `dlgPrefs` now uses typed string combo boxes/model swaps and no longer appears in the checked compile warning list.
- BFB `dlgOpen`, `dlgGroup`, `dlgManeiDomini`, and `dlgOmnis` no longer emit checked compile warnings after typing their Swing models and small helper collections.
- BFB `frmBase` no longer emits checked compile warnings after typing its directory, Warchest, and random assignment list models.
- SAW `frmVee` no longer uses deprecated `setNextFocusableComponent(...)`; explicit traversal rules now live behind a `LayoutFocusTraversalPolicy`.
- SAW `dlgOpen` no longer emits checked compile warnings after typing its Swing combo models. The vehicle handoff still uses a narrow suppression because `DesignForm#setUnit` is typed for `Mech` while SAW passes a `CombatVehicle`.
- SAW `HMPReader` no longer emits checked compile warnings after typing the HMP compatibility error and critical-location collections.

## Follow-Up Candidates

- Continue unchecked generics cleanup in the SAW vehicle forms.
- Revisit the shared `DesignForm#setUnit` contract so SSW `Mech` and SAW `CombatVehicle` handoffs can share a type-safe unit interface.
