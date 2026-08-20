# Task List - PERFORMANCE ENGINE AND NAVIGATION STUTTER FIXES

- [x] Pass `isVisible` states inside `HorizontalPager` pages: Page 0 is visible when current index is 0, Page 2 is visible when current index is 2.
- [x] Update `LivePreview` to only loop delays at 60fps when `isVisible` is true, dropping to 1fps when the screen is hidden.
- [x] Offload timing loop ticks to `Dispatchers.Default` background threads.
- [x] Compile and verify the build.
