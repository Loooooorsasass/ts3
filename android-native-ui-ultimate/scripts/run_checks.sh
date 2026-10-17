#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
python3 "$root/scripts/validate_skill.py" "$root"
PYTHONDONTWRITEBYTECODE=1 python3 -m unittest discover -s "$root/tests" -v