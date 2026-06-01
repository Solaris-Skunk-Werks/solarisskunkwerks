# Print Workflow Isolation

This document tracks the print workflow exploration branch. The goal is to isolate OS-facing print orchestration without changing rendered record sheets, preview behavior, or unit data handling.

## Current Shape

- SSW uses `ssw.print.Printer` as its main print coordinator.
  - Owns `Book`, `Paper`, and `PageFormat`.
  - Uses `PrinterJobGateway` for OS-facing print dialog and print calls.
  - Builds `PrintMech` instances.
  - Opens SSW-specific print options dialogs.
  - Handles direct printing and preview page generation.
- SAW uses shared `Print.PagePrinter` from `sswlib`.
  - `frmVee` and `frmVeeWide` build a `PrintVehicle`.
  - The form appends that printable to `PagePrinter`.
  - Preview is handled by `Print.preview.dlgPreview`.
- BFB uses two paths.
  - `Print.BFBPrinter` owns force-list/declaration print jobs.
  - `Print.BFBPrinter` uses `PrinterJobGateway` for OS-facing print dialog and print calls.
  - Some force/unit previews also assemble `PagePrinter` manually.
- Shared renderers live in `sswlib/src/main/java/Print`.
  - `PrintMech`, `PrintVehicle`, force list, BattleForce, QuickStrike, and Alpha Strike card printers are `Printable` renderers.
  - These renderers should remain mostly unchanged during workflow isolation.
- Preview dialogs are Swing UI surfaces.
  - SSW has `ssw.printpreview`.
  - Shared/BFB/SAW flows use `Print.preview`.

## Pain Points

- UI actions directly construct print coordinators and preview dialogs.
- Print coordinators historically owned `PrinterJob.printDialog()` and `PrinterJob.print()` directly. These calls are now routed through `PrinterJobGateway` for SSW `Printer`, shared `PagePrinter`, and `BFBPrinter`.
- SSW, SAW, and BFB duplicate print job setup patterns.
- Image selection and image inclusion are partly UI-dialog behavior and partly renderer behavior.
- Windows print-spooler behavior is difficult to isolate because OS calls are embedded in app workflows.

## Boundary Recommendation

Introduce workflow-level classes that sit above `Printable` renderers and below Swing action handlers.

Initial concepts:

- `PrintRequest`
  - Job name
  - Paper size
  - One or more `Printable` pages
  - Preview/direct-print intent
  - Shared `PagePrinter` now builds a `PrintRequest` before preview or direct print.
- `PrintWorkflow`
  - Creates a `Book`
  - Shows preview when requested
  - Invokes direct print when requested
- `PrinterJobGateway`
  - Small wrapper around `PrinterJob`
  - Owns `printDialog()` and `print()`
  - Future home for OS-specific print behavior or diagnostics

The first implementation should preserve Swing behavior and existing dialogs. This is an isolation pass, not a UX rewrite.

## Suggested Sequence

1. Add a tiny `PrinterJobGateway` wrapper in `sswlib`. Done.
2. Refactor `Print.PagePrinter` to use the gateway internally without changing its public API. Done.
3. Add tests around `PagePrinter` preview/book generation and direct-print cancellation behavior using a test gateway. Done.
4. Refactor SSW `Printer` to use the gateway behind the current API. Done.
5. Refactor `BFBPrinter` to use the gateway behind the current API. Done.
6. Introduce request objects for new print features.
   - Shared `PagePrinter` now builds `PrintRequest` objects before converting to `Book`.
   - Next request-object pass should decide whether SSW and BFB print coordinators should consume the same request type directly or hand off to a small workflow class.
7. Add Alpha Strike card generation as a print target after the request boundary exists.

## Guardrails

- Keep `PrintMech` and `PrintVehicle` rendering stable.
- Keep existing print preview dialogs working.
- Keep direct print behavior unchanged unless a bug is explicitly being fixed.
- Keep image lookup behavior covered by regression tests.
- Do not introduce JavaFX or a new UI framework in this phase.
