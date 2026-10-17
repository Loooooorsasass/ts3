#!/usr/bin/env python3
"""Validate required package structure, frontmatter, and local references."""

from __future__ import annotations

import re
import sys
from pathlib import Path
from urllib.parse import unquote, urlsplit


REQUIRED_FILES = (
    "SKILL.md",
    "README.md",
    "CHANGELOG.md",
    "LICENSE",
    "docs/source-audit.md",
    "docs/architecture.md",
    "docs/verification-report.md",
    "docs/installation.md",
    "references/01-repository-discovery.md",
    "references/workflow-new-ui.md",
    "references/workflow-debug-refactor.md",
    "references/workflow-motion-performance.md",
    "references/workflow-accessibility-input.md",
    "references/workflow-services-multiplayer.md",
    "references/workflow-test-release.md",
    "assets/templates/README.md",
    "assets/templates/scripts/port_check.sh",
    "scripts/validate_skill.py",
    "scripts/package_skill.sh",
    "tests/test_validate_skill.py",
    "benchmarks/README.md",
    "benchmarks/tasks.csv",
)
REQUIRED_DIRS = ("references", "assets/templates", "scripts", "tests", "benchmarks", "docs")
FORBIDDEN_PARTS = {".git", "__pycache__", ".pytest_cache", "node_modules"}
FORBIDDEN_SUFFIXES = {".pyc", ".tmp", ".swp"}
MARKDOWN_LINK = re.compile(r"!??\[[^\]]*\]\(([^)]+)\)")
FILE_REFERENCE = re.compile(
    r"(?<![A-Za-z0-9_])((?:references|assets/templates|docs|scripts|tests|benchmarks)/[A-Za-z0-9_./-]+\.(?:md|py|sh|kt|xml|csv|zip))"
)


def check_required_files(root: Path) -> list[str]:
    errors = [f"missing required file: {path}" for path in REQUIRED_FILES if not (root / path).is_file()]
    errors.extend(f"missing required directory: {path}" for path in REQUIRED_DIRS if not (root / path).is_dir())
    return errors


def check_metadata(root: Path) -> list[str]:
    path = root / "SKILL.md"
    if not path.is_file():
        return ["missing required file: SKILL.md"]
    content = path.read_text(encoding="utf-8")
    if not content.startswith("---\n"):
        return ["SKILL.md must start with YAML frontmatter"]
    parts = content.split("---", 2)
    if len(parts) != 3:
        return ["SKILL.md frontmatter is not closed"]
    frontmatter = parts[1]
    errors = []
    for field in ("name", "description"):
        if not re.search(rf"(?m)^{field}:\s*\S", frontmatter):
            errors.append(f"SKILL.md frontmatter missing non-empty {field}")
    if not re.search(r"(?m)^name:\s*android-native-ui-ultimate\s*$", frontmatter):
        errors.append("SKILL.md name must be android-native-ui-ultimate")
    if not re.search(r"(?m)^  version:\s*2\.0\.0\s*$", frontmatter):
        errors.append("SKILL.md metadata version must be 2.0.0")
    return errors


def local_target_exists(source: Path, link: str, root: Path) -> bool:
    parsed = urlsplit(link.strip().strip("<>"))
    if parsed.scheme or parsed.netloc or not parsed.path:
        return True
    target = (source.parent / unquote(parsed.path)).resolve()
    try:
        target.relative_to(root.resolve())
    except ValueError:
        return False
    return target.exists()


def check_markdown_links(root: Path) -> list[str]:
    errors = []
    for source in sorted(root.rglob("*.md")):
        if any(part in FORBIDDEN_PARTS for part in source.parts):
            continue
        content = source.read_text(encoding="utf-8")
        for match in MARKDOWN_LINK.finditer(content):
            link = match.group(1).split()[0]
            if not local_target_exists(source, link, root):
                errors.append(f"broken local Markdown link: {source.relative_to(root)} -> {link}")
    return errors


def check_file_references(root: Path) -> list[str]:
    errors = []
    for source in sorted(root.rglob("*.md")):
        if any(part in FORBIDDEN_PARTS for part in source.parts):
            continue
        for target in FILE_REFERENCE.findall(source.read_text(encoding="utf-8")):
            if not (root / target).is_file():
                errors.append(f"broken local file reference: {source.relative_to(root)} -> {target}")
    return errors


def check_distribution_cleanliness(root: Path) -> list[str]:
    errors = []
    seen = {}
    for path in root.rglob("*"):
        if not path.is_file():
            continue
        relative = path.relative_to(root)
        if any(part in FORBIDDEN_PARTS for part in relative.parts) or path.suffix in FORBIDDEN_SUFFIXES:
            errors.append(f"generated/cache/temp file must not ship: {relative}")
        normalized = relative.as_posix().casefold()
        if normalized in seen:
            errors.append(f"duplicate case-insensitive path: {seen[normalized]} and {relative}")
        seen[normalized] = relative
    return errors


def validate(root: Path) -> list[str]:
    if not root.is_dir():
        return [f"skill directory does not exist: {root}"]
    return (
        check_required_files(root)
        + check_metadata(root)
        + check_markdown_links(root)
        + check_file_references(root)
        + check_distribution_cleanliness(root)
    )


def main() -> int:
    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path(__file__).resolve().parent.parent
    errors = validate(root)
    if errors:
        print("Skill validation failed:")
        for error in errors:
            print(f"- {error}")
        return 1
    print(f"Skill validation passed: {root}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())