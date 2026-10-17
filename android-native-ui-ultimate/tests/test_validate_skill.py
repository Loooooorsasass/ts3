from __future__ import annotations

import sys
import tempfile
import unittest
from pathlib import Path

sys.dont_write_bytecode = True
sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "scripts"))

from validate_skill import check_markdown_links, check_metadata, check_required_files


class SkillValidatorTests(unittest.TestCase):
    def test_product_has_required_structure(self) -> None:
        root = Path(__file__).resolve().parents[1]
        self.assertEqual([], check_required_files(root))

    def test_product_metadata_has_expected_name_and_version(self) -> None:
        root = Path(__file__).resolve().parents[1]
        self.assertEqual([], check_metadata(root))

    def test_missing_required_file_is_reported(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "SKILL.md").write_text("---\nname: sample\ndescription: test\n---\n", encoding="utf-8")
            errors = check_required_files(root)
            self.assertTrue(any("missing required file: README.md" in error for error in errors))

    def test_broken_relative_markdown_link_is_reported(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "docs").mkdir()
            source = root / "docs" / "index.md"
            source.write_text("[Missing](missing.md)\n", encoding="utf-8")
            errors = check_markdown_links(root)
            self.assertEqual(1, len(errors))
            self.assertIn("missing.md", errors[0])


if __name__ == "__main__":
    unittest.main()