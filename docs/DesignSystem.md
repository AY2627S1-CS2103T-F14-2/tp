# TutorRoster design system

TutorRoster uses a light, calm visual system designed for a keyboard-first workflow. The interface is based on the current TutorRoster mock-up: a white brand header, a quiet off-white canvas, rounded information cards, restrained colour accents, and one prominent command entry point.

The implementation lives in [`DesignSystem.css`](../src/main/resources/view/DesignSystem.css). FXML defines structure and semantic component classes; the stylesheet owns colours, typography, spacing, borders, states, and control treatment.

## Design principles

1. **Keep the roster quiet.** The student list is the main work surface, so backgrounds and borders should be subtle.
2. **Use teal for action and orientation.** The brand colour identifies the product, primary actions, focus states, and selected records.
3. **Make information scannable.** Names are the strongest text, contact details are secondary, and addresses are tertiary.
4. **Keep the CLI visible.** The command bar is persistent and visually prominent without competing with the roster.
5. **Prefer semantic classes.** Use classes such as `person-card`, `primary-button`, and `result-display` instead of styling arbitrary nodes by position.

## Foundations

### Colour tokens

| Token | Value | Use |
| --- | --- | --- |
| Ink | `#173734` | Primary text and headings |
| Ink muted | `#647572` | Supporting text and metadata |
| Brand | `#177565` | Logo mark, primary action, and focus |
| Brand dark | `#0F5B50` | Hover and pressed action states |
| Canvas | `#F5F7F5` | Application background |
| Surface | `#FFFFFF` | Cards, header, and controls |
| Surface soft | `#EFF6F2` | Feedback and selected surfaces |
| Border | `#DCE5E1` | Quiet outlines and dividers |
| Danger | `#B84C4C` | Command and validation errors |

JavaFX does not support CSS custom properties, so these values are documented at the top of `DesignSystem.css` and repeated in the relevant component rules. Update the token comment and every corresponding rule when changing the palette.

### Type

The preferred typeface is `Helvetica`, with `Segoe UI` as the Windows fallback and the platform sans-serif font as the final fallback. Regular weight is the default; only page and dialog headings use bold weight.

| Role | Size | Weight | Example |
| --- | ---: | --- | --- |
| Page title | 22 px | Bold | `Your students` |
| Brand name | 17 px | Regular | `TutorRoster` |
| Card name | 15 px | Regular | `Alex Yeoh` |
| Body and command text | 13 px | Regular | Command input |
| Metadata | 11 px | Regular | Phone, email, address |
| Eyebrow and chip text | 10 px | Regular | `ROSTER`, `friends` |

Only page and dialog headings use bold weight. All other interface text uses regular weight; hierarchy comes from size, spacing, and colour.

### Spacing and shape

Use the spacing rhythm `4, 8, 12, 16, 24, 28, 32 px`. Cards use a 14 px radius, outer shells use a 16 px radius, and compact controls use an 8–9 px radius. Prefer whitespace over extra borders.

## Component catalogue

### Application shell

Use `.app-shell` on the root `VBox`. Put branding and menus in `.top-bar`, main content in `.content-area`, the command entry in `.command-region`, and the file-status line in `.status-region`.

```xml
<VBox styleClass="app-shell">
  <HBox styleClass="top-bar" />
  <VBox styleClass="content-area" VBox.vgrow="ALWAYS" />
  <StackPane styleClass="command-region" />
  <StackPane styleClass="status-region" />
</VBox>
```

### Roster surface and student card

Wrap a list in `.roster-surface`. Each custom list-cell should render a `.person-card`. A card contains:

- `.person-avatar` with `.avatar-label` for initials;
- `.card-name` for the student’s name;
- `.card-meta` for phone and optional email; and
- `.card-detail` for the ordered subjects and education level when present.

The list view must remain transparent so the roster shell supplies the visual boundary. Do not reintroduce alternating dark row colours.

### Primary button

Use `.primary-button` for the one main action in a view. The command bar uses it for `Run command`; the help window uses it for `Copy URL`. The stylesheet already provides hover, pressed, disabled-compatible, and focus-visible treatment. Avoid inventing a second teal button style.

### Command bar

Use `.command-region` around a `.command-box`. Put the text field inside the command box and let it grow horizontally. Use the prompt icon class `.command-icon` and the existing `CommandBox#handleCommandEntered` action for both the text field and the button so keyboard and mouse users share one execution path.

### Feedback

Use `.result-surface` for the layout slot and `.result-display-shell` for the feedback container. The `TextArea` should use `.result-display`, remain read-only, and wrap text. Errors should use the existing `.error` class; its red colour is intentionally reserved for failure feedback.

## Adding a component

1. Add the semantic FXML structure and a specific `styleClass`.
2. Reuse the existing tokens, spacing rhythm, and control states in `DesignSystem.css`.
3. Use the existing typography roles before adding a new font size or weight.
4. Add hover, pressed, focused, selected, empty, and error states where the component supports them.
5. Keep the component usable at the default 740 × 600 window size.
6. Update this catalogue when introducing a new reusable component or token.

Avoid inline `style` attributes for visual styling. Keep all reusable visual rules in `DesignSystem.css` so the application has one maintained visual source.

## Accessibility and interaction rules

- Maintain readable contrast between ink text and the canvas/surface colours.
- Never communicate an error through colour alone; keep the error message visible in the result area.
- Keep Enter-to-run behaviour in the command field.
- Preserve F1 for Help and keep the Help menu available for mouse users.
- Use focus styles on interactive controls rather than removing the JavaFX focus indicator without replacement.

## Verification checklist

Before committing a UI change, check that:

- the application launches with `DesignSystem.css` loaded;
- the roster remains readable with one, several, and zero records;
- long names and long addresses do not make cards overflow the window;
- command success and command failure are both legible;
- the command field works with Enter and the button;
- the Help menu and F1 shortcut still work; and
- the change does not add a new one-off colour or spacing value without documenting why.
