#!/usr/bin/env python3
"""Build and validate a clean, reproducible skill ZIP beside the product folder."""

from __future__ import annotations

import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path


EXCLUDED_DIRS = {".git", "__pycache__", ".pytest_cache", "node_modules", ".idea", ".gradle"}
EXCLUDED_NAMES = {".DS_Store", "Thumbs.db"}
EXCLUDED_SUFFIXES = {".pyc", ".tmp", ".swp"}


def package(root: Path) -> Path:
    root = root.resolve()
    archive = root.parent / f"{root.name}.zip"
    with tempfile.TemporaryDirectory(prefix="android-native-ui-ultimate-") as temporary:
        with zipfile.ZipFile(archive, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9) as output:
            for source in sorted(root.rglob("*")):
                if not source.is_file():
                    continue
                relative = source.relative_to(root)
                if any(part in EXCLUDED_DIRS for part in relative.parts):
                    continue
                if source.name in EXCLUDED_NAMES or source.suffix in EXCLUDED_SUFFIXES:
                    continue
                output.write(source, Path(root.name) / relative)

        with zipfile.ZipFile(archive) as built:
            bad_entry = built.testzip()
            if bad_entry:
                raise RuntimeError(f"ZIP integrity check failed at {bad_entry}")
            names = built.namelist()
            prefix = f"{root.name}/"
            if not names or any(not name.startswith(prefix) for name in names):
                raise RuntimeError("archive must contain files under one product directory")
            built.extractall(temporary)

        extracted_root = Path(temporary) / root.name
        subprocess.run(
            [sys.executable, str(root / "scripts" / "validate_skill.py"), str(extracted_root)],
            check=True,
        )
    return archive


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(__file__).resolve().parent.parent
    archive = package(root)
    print(f"Created and verified {archive} ({archive.stat().st_size} bytes)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())