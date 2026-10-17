# Installation

## Local bundle layout

This package is designed to be copied as a self-contained skill folder into an agent environment that supports local skill discovery. The important requirement is that the folder remains intact with the sibling directories and the `SKILL.md` frontmatter name matches the folder name.

Typical layout:

```text
android-native-ui-ultimate/
├── SKILL.md
├── README.md
├── CHANGELOG.md
├── LICENSE
├── references/
├── assets/
├── docs/
├── scripts/
├── tests/
├── benchmarks/
└── ...
```

## Install into an agent

Copy the entire `android-native-ui-ultimate/` directory into the agent's configured skill or project-scoped skill path, then verify the agent is loading the folder that contains `SKILL.md`.

Examples: 

- `.agents/skills/android-native-ui-ultimate/SKILL.md`
- `.claude/skills/android-native-ui-ultimate/SKILL.md`

The exact path depends on the tool/host. Keep `references/`, `assets/`, `docs/`, and `scripts/` beside `SKILL.md` so local relative links continue to resolve.

## Verify the bundle

From the bundle root, run:

```bash
python3 scripts/validate_skill.py .
python3 -m unittest discover -s tests -v
```

These checks validate the skill package structure, required files, metadata, relative links, and local references. They do not prove Android compilation or device behavior; those still require a real Android project and toolchain.

## Usage notes

- Keep secrets out of logs, patches, and generated artifacts.
- Prefer the consuming repository's own design tokens, localization strings, and component system.
- Treat the bundled templates as examples that need verification in the target app.
- Do not claim Android compile/test success unless the project and toolchain actually ran those checks.
