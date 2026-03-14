# Front-end Template Details

Last updated: 2026-03-14

This file describes the Thymeleaf fragments and front-end templates used by the application, Tailwind setup, and dark-mode behavior.

## Key fragments (src/main/resources/templates/fragments)

- `head.html` — includes meta tags, Font Awesome, Tailwind CDN, Tailwind config (darkMode: "class"), theme initialization script and links to `style.css` and favicon. The script reads `localStorage.theme` and applies `dark` or `light` to the `<html>` element. It exposes `toggleTheme()` and `updateThemeIcons()` on `window`.

- `navbar.html` — includes the dark-mode toggle button (ARIA labelled), login/register links, and navigation to posts/profile. The toggle button calls `toggleTheme()` and updates icons (elements with ids `theme-icon-sun` and `theme-icon-moon`).

- `layout.html` — a shared layout fragment used by pages to include the navbar, footer and a content region. Templates typically use `th:replace="fragments/layout :: layout"` to inject page content.

- `footer.html` — simple footer fragment.

## Templates (examples)

- `login.html`, `register.html`, `posts/list.html`, `posts/view.html`, `posts/form.html`, `profile.html`, `admin/users.html` — server-rendered pages using the fragments above.

## Styling

- Tailwind is loaded via CDN in `head.html`. The project relies on Tailwind utility classes for layout and responsive styling; a small `src/main/resources/static/css/style.css` complements Tailwind for minimal custom styles.

## Dark mode behavior

- Dark mode is class-based. The `head.html` script:
  - Reads `localStorage.getItem('theme')`.
  - Falls back to the OS preference via `prefers-color-scheme`.
  - Applies `dark` class to `<html>` when dark mode is active.
  - Persists choice in `localStorage.theme` when toggled.

## Notes for contributors

- If migrating Tailwind to a build-time workflow (recommended), remove the CDN script and compile utilities to reduce CSS size.
- When editing fragments, keep `head.html` small and place page-specific scripts at the bottom of page templates to avoid blocking.
