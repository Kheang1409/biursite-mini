# Project Verification — 2026-03-09

- Java: 25
- Spring Boot: 3.5.11
- Database: PostgreSQL 15 (docker-compose)
- Tailwind: CDN (used in templates)
- Thymeleaf fragments: `fragments/head`, `fragments/navbar`, `fragments/footer`, `fragments/layout` (the `layout` fragment is used for shared page layout and theme init)
- Verified against: `pom.xml`, `docker-compose.yml`, `src/main/resources/application.yml`

# BiUrSite UI Architecture

**Version**: 1.0  
**Last Updated**: March 8, 2026  
**Framework**: Tailwind CSS 3 (CDN) + Font Awesome 6.5.1 + Thymeleaf

---

## Overview

BiUrSite's user interface is built with modern CSS frameworks and follows responsive design principles to provide an excellent experience across all devices.

### Key Technologies

| Layer                 | Technology                          | Purpose                     |
| --------------------- | ----------------------------------- | --------------------------- |
| **Template Engine**   | Thymeleaf + Spring Security Dialect | Server-side HTML rendering  |
| **Styling Framework** | Tailwind CSS 3 (CDN)                | Utility-first CSS styling   |
| **Icon Library**      | Font Awesome 6.5.1 (CDN)            | Consistent icon system      |
| **Authentication UI** | HTML Forms + Thymeleaf              | Server-rendered auth pages  |
| **Interactivity**     | Vanilla JavaScript                  | Modal dialogs, theme toggle |
| **Color Scheme**      | Tailwind Dark Mode (class-based)    | Light/dark theme support    |

---

## Design System

### Color Palette

#### Light Mode (Default)

```
Primary:
  Background: #FFFFFF (bg-white)
  Text: #1F2937 (text-gray-800)
  Accent: #3B82F6 (blue-500)

Components:
  Cards: #F3F4F6 (bg-gray-100)
  Borders: #E5E7EB (border-gray-200)
  Hover: #EFF6FF (hover:bg-blue-50)
```

#### Dark Mode

```
Primary:
  Background: #111827 (dark:bg-gray-900)
  Text: #F3F4F6 (dark:text-gray-100)
  Accent: #60A5FA (dark:bg-blue-400)

Components:
  Cards: #1F2937 (dark:bg-gray-800)
  Borders: #374151 (dark:border-gray-700)
  Hover: #1E3A8A (dark:hover:bg-blue-700)
```

### Typography

```css
/* Heading Styles (Tailwind classes) */
h1 {
  @apply text-4xl font-bold text-gray-900 dark:text-gray-100;
}
h2 {
  @apply text-3xl font-semibold text-gray-800 dark:text-gray-200;
}
h3 {
  @apply text-2xl font-medium text-gray-700 dark:text-gray-300;
}

/* Body Text */
body {
  @apply text-base text-gray-600 dark:text-gray-400;
}
p {
  @apply leading-relaxed;
}
```

---

## Layout Structure

### HTML Structure

```html
<!-- Document -->
<html lang="en" id="html-root">
  <head>
    <!-- Metadata, title, Tailwind CDN -->
  </head>
  <body class="bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100">
    <!-- Shared layout fragment (navbar + footer) -->
    <div th:replace="fragments/layout :: layout">
      <!-- Page-specific content injected here -->
      <div class="container mx-auto px-4 py-8">
        <th:block th:include="pages/**"></th:block>
      </div>
    </div>
  </body>
</html>
```

---

## Component Library

(Full component examples omitted here; archived original contains complete fragments and scripts.)

---

## Dark Mode Implementation

**Type**: Class-based dark mode (Tailwind Configuration)

**Theme Toggle Script**: implemented in layout; persists preference in `localStorage`.

---

## Responsive Design

Tailwind breakpoints used; mobile-first philosophy followed. Component samples and templates available in archived original.

---

## Page Templates & Examples

- `login.html`, `register.html`, `posts/list.html`, `posts/view.html`, `posts/form.html`, `profile.html` — templates and fragments are documented in the archived file.

---

## Best Practices & Future Enhancements

- Migrate Tailwind CDN to build-time compilation
- Improve accessibility and WCAG audit
- Add loading states and toast notifications

---

(Full UI examples, scripts, and CSS were copied to `docs/archive/UI_ARCHITECTURE.md`.)

-- Implementation references --

For exact fragment and template details see:

- `docs/archive/TEMPLATES_DETAILS.md` — lists `head.html`, `navbar.html`, `layout.html`, `footer.html`, Tailwind CDN setup, and dark-mode scripts.

Runtime notes:

- Tailwind is currently loaded via CDN in `fragments/head.html`. The project uses class-based dark mode and a small `style.css` for custom styles.
