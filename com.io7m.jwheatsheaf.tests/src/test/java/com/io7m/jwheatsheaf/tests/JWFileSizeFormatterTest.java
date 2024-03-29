/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jwheatsheaf.tests;

import com.io7m.jwheatsheaf.api.JWFileChooserFilterType;
import com.io7m.jwheatsheaf.api.JWFileChoosersType;
import com.io7m.jwheatsheaf.api.JWFileKind;
import com.io7m.jwheatsheaf.ui.JWFileChoosers;
import com.io7m.jwheatsheaf.ui.internal.JWFileChooserFilterOnlyDirectories;
import com.io7m.jwheatsheaf.ui.internal.JWFileItem;
import com.io7m.jwheatsheaf.ui.internal.JWFileList;
import com.io7m.jwheatsheaf.ui.internal.JWStrings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.io7m.jwheatsheaf.api.JWFileChooserConfigurationDefaults.fileSizeFormatter;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class JWFileSizeFormatterTest
{
  private static DynamicTest testOf(
    final long size,
    final String text)
  {
    return DynamicTest.dynamicTest(
      String.format("test_FormatSize_%d_%s", Long.valueOf(size), text),
      () -> {
        assertEquals(text, fileSizeFormatter().formatSize(size));
      }
    );
  }

  /**
   * Formatting a negative size yields the empty string.
   */

  @Test
  public void test_FormatSize_Negative_Empty()
  {
    assertEquals("", fileSizeFormatter().formatSize(-1L));
  }

  /**
   * Formatting a range of sizes works correctly.
   *
   * @return A stream of tests
   */

  @TestFactory
  public Stream<DynamicTest> test_FormatSize_VariousUnits_CorrectString()
  {
    return Stream.of(
      testOf(1L, "1B"),
      testOf(1_000L, "1.00kB"),
      testOf(10_000L, "10.00kB"),
      testOf(100_000L, "100.00kB"),
      testOf(1_000_000L, "1.00MB"),
      testOf(10_000_000L, "10.00MB"),
      testOf(100_000_000L, "100.00MB"),
      testOf(1_000_000_000L, "1.00GB"),
      testOf(10_000_000_000L, "10.00GB"),
      testOf(100_000_000_000L, "100.00GB"),
      testOf(1000_000_000_000L, "1.00TB"),
      testOf(10_000_000_000_000L, "10.00TB"),
      testOf(100_000_000_000_000L, "100.00TB"),
      testOf(1000_000_000_000_000L, "1000.00TB")
    );
  }

  public static final class JWFileListTest
  {
    private JWFileItem item0;
    private JWFileItem item1;
    private JWFileItem item2;
    private List<JWFileItem> baseItems;
    private JWStrings strings;
    private JWFileChoosersType choosers;

    @BeforeEach
    public void testSetup()
    {
      this.strings =
        JWStrings.of(JWStrings.getResourceBundle());
      this.choosers =
        JWFileChoosers.create();

      this.item0 =
        JWFileItem.builder()
          .setPath(Paths.get("/x"))
          .setModifiedTime(FileTime.fromMillis(0L))
          .setKind(JWFileKind.REGULAR_FILE)
          .setSize(0L)
          .build();
      this.item1 =
        JWFileItem.builder()
          .setPath(Paths.get("/y"))
          .setModifiedTime(FileTime.fromMillis(0L))
          .setKind(JWFileKind.REGULAR_FILE)
          .setSize(0L)
          .build();
      this.item2 =
        JWFileItem.builder()
          .setPath(Paths.get("/z"))
          .setModifiedTime(FileTime.fromMillis(0L))
          .setKind(JWFileKind.REGULAR_FILE)
          .setSize(0L)
          .build();

      this.baseItems =
        List.of(this.item0, this.item1, this.item2);
    }

    @AfterEach
    public void testShutdown()
      throws IOException
    {
      this.choosers.close();
    }

    @Test
    public void testListBase()
    {
      final var items = new JWFileList(this.choosers.filterForAllFiles());

      items.setItems(this.baseItems);
      assertEquals(this.baseItems, items.items());
    }

    @Test
    public void testListSearch()
    {
      final var items = new JWFileList(this.choosers.filterForAllFiles());

      items.setItems(this.baseItems);
      items.setSearch("x");
      assertEquals(List.of(this.item0), items.items());
      items.setSearch("");
      assertEquals(this.baseItems, items.items());
    }

    @Test
    public void testListFilter()
    {
      final var items = new JWFileList(this.choosers.filterForAllFiles());

      items.setItems(this.baseItems);
      items.setFilter(new JWFileChooserFilterType()
      {
        @Override
        public String description()
        {
          return "y";
        }

        @Override
        public boolean isAllowed(final Path path)
        {
          return Objects.equals(path.toString(), "/y");
        }
      });

      assertEquals(List.of(this.item1), items.items());
      items.setFilter(this.choosers.filterForAllFiles());
      assertEquals(this.baseItems, items.items());
    }

    @Test
    public void testListFilterDirectories()
    {
      final var items = new JWFileList(this.choosers.filterForAllFiles());

      items.setItems(this.baseItems);
      items.setFilter(JWFileChooserFilterOnlyDirectories.create(this.strings));
      assertEquals(List.of(), items.items());
      items.setFilter(this.choosers.filterForAllFiles());
      assertEquals(this.baseItems, items.items());
    }
  }
}
