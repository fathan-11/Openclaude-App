# OpenClaude App — UI/UX Design Proposal v2
## Warm Wellness Aesthetic

---

## 1. Executive Summary

This proposal presents a comprehensive UI/UX redesign of the OpenClaude Android application, transitioning from a dark glassmorphism theme to a **warm, soft wellness-inspired aesthetic**. The design is directly inspired by modern mental health and wellness app patterns, featuring peachy-coral tones, cream backgrounds, rounded cards, and calming visual elements.

**Target Audience:** Tech-savvy users aged 18-35 who appreciate modern aesthetics, clean interfaces, and a mindful development experience.

---

## 2. Design DNA — Extracted from Reference

### 2.1 Visual Analysis of Reference
The Pinterest reference (Mental Health App Design) reveals:

- **Color Temperature:** Warm, inviting — peachy-coral as primary accent
- **Background:** Soft cream/beige (#FFF8F0) — NOT pure white or dark
- **Cards:** Large border-radius (20-24px), subtle shadows, white backgrounds
- **Typography:** Rounded sans-serif (Nunito/Quicksand family)
- **Navigation:** Bottom tab bar with 4-5 items, active state highlighted
- **Spacing:** Generous padding, breathing room between elements
- **Mood:** Calming, approachable, friendly — NOT aggressive or techy

### 2.2 Design Principles
1. **Warmth Over Cold** — Cream backgrounds, coral accents instead of neon/dark
2. **Softness Over Sharpness** — Rounded corners (16-24px), gentle shadows
3. **Calm Over Chaos** — Generous whitespace, limited color count per screen
4. **Approachability Over Complexity** — Simple icons, clear hierarchy
5. **Mindful Over Mechanical** — Wellness check-ins, mood tracking integration

---

## 3. Color System

### 3.1 Primary Palette — Coral
| Token | Hex | Usage |
|-------|-----|-------|
| Coral 50 | #FFF5F0 | Light backgrounds |
| Coral 100 | #FFE8DD | Card highlights, icon bg |
| Coral 200 | #FFD0B8 | Hover states |
| Coral 300 | #FFB088 | Gradients |
| Coral 400 | #F4845F | **Primary accent** |
| Coral 500 | #E86F48 | Primary hover/pressed |

### 3.2 Secondary Palette — Lavender
| Token | Hex | Usage |
|-------|-----|-------|
| Lavender 50 | #F5F0FF | Secondary bg |
| Lavender 100 | #EDE5FF | Icon bg, tags |
| Lavender 200-500 | #D4C4FF → #7C5CFC | Accents, badges |

### 3.3 Accent Palette — Mint
| Token | Hex | Usage |
|-------|-----|-------|
| Mint 50-100 | #F0FFF8 → #D4F5E4 | Success states |
| Mint 400 | #52C27D | Online indicators |
| Mint 500 | #3DAF68 | Success actions |

### 3.4 Background & Surface
| Token | Hex | Usage |
|-------|-----|-------|
| Background | #FFF8F0 | App background |
| Surface | #FFFFFF | Cards, modals |
| Surface Hover | #FFF5ED | Interactive states |

---

## 4. Typography

### 4.1 Font Stack
- **Display:** Quicksand (700) — Headings, titles, hero text
- **Body:** Nunito (500-800) — All body text, labels, buttons
- **Code:** JetBrains Mono (400-600) — Terminal, code blocks

### 4.2 Type Scale
| Level | Font | Weight | Size | Usage |
|-------|------|--------|------|-------|
| Display | Quicksand | 700 | 36-42px | Splash, hero |
| H1 | Quicksand | 700 | 28px | Screen titles |
| H2 | Nunito | 800 | 20px | Section headers |
| H3 | Nunito | 700 | 16-17px | Card titles |
| Body | Nunito | 600 | 14-15px | Primary text |
| Caption | Nunito | 500 | 12-13px | Metadata, timestamps |
| Small | Nunito | 600 | 11px | Nav labels, badges |

---

## 5. Component System

### 5.1 Buttons
- **Primary (Coral):** Gradient linear-gradient(135deg, #F4845F, #E86F48), rounded-full, shadow-coral
- **Secondary (Lavender):** Gradient with lavender tones
- **Success (Mint):** Gradient with mint tones
- **Outline:** 2px border coral, transparent bg
- **Ghost:** Coral 50 bg, coral text
- **Border Radius:** 16-20px (rounded-full for pill buttons)

### 5.2 Cards
- **Base:** White background, border-radius 20-24px, box-shadow 0 2px 12px rgba(0,0,0,0.04)
- **Gradient Cards:** Coral-50 → Peach-50, Lavender-50 → Lavender-100
- **Interactive:** Hover translateY(-2px), shadow increase
- **Padding:** 18-24px internal

### 5.3 Chips / Tags
- **Coral Chip:** Coral-100 bg, Coral-600 text
- **Lavender Chip:** Lavender-100 bg, Lavender-500 text
- **Mint Chip:** Mint-100 bg, Mint-500 text
- **Border Radius:** Full (pill shape)

### 5.4 Navigation
- **Style:** Bottom tab bar, floating with rounded top corners
- **Items:** Icon + label, 4-5 items
- **Active State:** Coral-100 icon background, coral text
- **Inactive:** Muted gray text
- **Border Radius:** 28px top corners

### 5.5 Toggle Switch
- **Off:** #E0E0E8 background
- **On:** Coral gradient background
- **Thumb:** White circle with subtle shadow
- **Animation:** Smooth translateX on toggle

---

## 6. Screen Specifications

### 6.1 Splash Screen (01-splash-screen.html)
**Purpose:** First-time user experience, brand introduction
- Warm gradient background (cream → coral)
- Animated floating logo
- Welcoming title with Quicksand display font
- Progress dots for onboarding flow
- "Get Started" CTA button with coral gradient
- Decorative circles for depth

### 6.2 Home Dashboard (02-home-dashboard.html)
**Purpose:** Daily hub — mood check-in, quick actions, activity feed
- Personalized greeting with user name
- **Mood Card:** Full-width coral gradient, emoji selection
- **Quick Actions:** 2x2 grid (Chat, Terminal, Files, GitHub)
- **Activity Feed:** Recent items with timestamps
- Notification bell with badge

### 6.3 AI Chat (03-chat-screen.html)
**Purpose:** Conversational AI interface
- Top bar with bot avatar and online status
- Message bubbles: AI (white, left) / User (coral gradient, right)
- Code blocks with syntax highlighting (dark bg inside warm theme)
- Typing indicator animation
- Quick suggestion chips
- Input with attach button and send button

### 6.4 Terminal (04-terminal-screen.html)
**Purpose:** Command-line interface
- Dark terminal panel (#2D2D3A) inside warm shell
- Syntax-colored output (green success, red error, purple paths)
- Blinking cursor animation
- Quick command chips below terminal
- Input with run button

### 6.5 Settings (05-settings-screen.html)
**Purpose:** App configuration and profile
- Profile card with avatar, name, email, pro badge
- Categorized settings groups (General, AI & Models, About)
- Toggle switches for notifications, dark mode
- Arrow indicators for navigation items
- Version info footer

### 6.6 GitHub (06-github-screen.html)
**Purpose:** Repository and PR management
- Search bar with warm styling
- Tab navigation (Repos, PRs, Activity)
- Repository cards with language, stars, forks
- PR status indicators (green open, purple merged)
- Activity feed with dot indicators

### 6.7 Files (07-files-screen.html)
**Purpose:** Project file browser
- Breadcrumb navigation
- File/folder list with type-specific icons
- Code preview panel with syntax highlighting
- Storage usage card with progress bar

---

## 7. Accessibility & Usability

### 7.1 Accessibility
- **Color Contrast:** All text meets WCAG AA (4.5:1 minimum)
- **Touch Targets:** Minimum 44x44dp for all interactive elements
- **Font Scaling:** Supports system font size up to 200%
- **Screen Reader:** All icons have semantic labels
- **Focus States:** Visible focus indicators for keyboard navigation

### 7.2 Usability
- **Consistent Navigation:** Bottom nav on all main screens
- **Clear Hierarchy:** Title → subtitle → content → action
- **Progressive Disclosure:** Settings grouped by category
- **Error Prevention:** Confirmation dialogs for destructive actions
- **Feedback:** Visual feedback on all interactions (hover, press, success)

### 7.3 Responsive Design
- **Mobile:** 390px (iPhone 15 Pro) — primary target
- **Tablet:** Scaled up with maintained proportions
- **Desktop:** Centered phone frame with decorative background

---

## 8. Animation & Motion

### 8.1 Micro-interactions
- **Button Press:** Scale(0.98) on press, spring back
- **Card Hover:** translateY(-2px) with shadow increase
- **Nav Switch:** Background color transition 0.2s
- **Toggle:** Smooth translateX 0.3s

### 8.2 Page Transitions
- **Fade In:** 0.3s ease-out for content load
- **Staggered Load:** Cards animate in sequence (0.1s delay each)
- **Typing Indicator:** Bouncing dots with 0.2s offset

### 8.3 Ambient Motion
- **Logo Float:** Gentle Y-axis oscillation (3s loop)
- **Decorative Circles:** Subtle parallax on scroll

---

## 9. Implementation Roadmap

### Phase 1: Design System (Week 1)
- [ ] Finalize color tokens in Color.kt
- [ ] Create typography scale in Theme.kt
- [ ] Build component library (Button, Card, Chip, Toggle)

### Phase 2: Core Screens (Week 2-3)
- [ ] Implement Splash/Onboarding
- [ ] Build Home Dashboard with mood check-in
- [ ] Redesign Chat screen with warm bubbles
- [ ] Update Terminal with warm shell

### Phase 3: Secondary Screens (Week 4)
- [ ] Settings with profile card
- [ ] GitHub integration screen
- [ ] Files browser

### Phase 4: Polish (Week 5)
- [ ] Animations and micro-interactions
- [ ] Accessibility audit
- [ ] Performance optimization
- [ ] User testing

---

## 10. Design Files

| File | Description |
|------|-------------|
| `00-design-system.html` | Complete color palette, typography, components |
| `01-splash-screen.html` | Welcome/onboarding screen |
| `02-home-dashboard.html` | Main dashboard with mood check-in |
| `03-chat-screen.html` | AI chat interface |
| `04-terminal-screen.html` | Command terminal |
| `05-settings-screen.html` | App settings & profile |
| `06-github-screen.html` | GitHub repos & PRs |
| `07-files-screen.html` | File browser |

All files are self-contained HTML with inline CSS — open in any browser to preview.

---

*Design System v2.0 • Warm Wellness Aesthetic • OpenClaude App*
