#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
python3 "$root/scripts/validate_skill.py" "$root"
python3 "$root/scripts/package_skill.py" "$root"