/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> http://io7m.com
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
import com.io7m.jwheatsheaf.api.JWFileKind;
import com.io7m.jwheatsheaf.ui.internal.JWFileChooserFilterAllFiles;
import com.io7m.jwheatsheaf.ui.internal.JWFileChooserFilterOnlyDirectories;
import com.io7m.jwheatsheaf.ui.internal.JWFileItem;
import com.io7m.jwheatsheaf.ui.internal.JWFileList;
import com.io7m.jwheatsheaf.ui.internal.JWStrings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Objects;

public final class JWFileListTest
{
  private JWFileItem item0;
  private JWFileItem item1;
  private JWFileItem item2;
  private List<JWFileItem> baseItems;
  private JWFileChooserFilterType filterAll;
  private JWStrings strings;

  @BeforeEach
  public void testSetup()
  {
    this.strings =
      JWStrings.of(JWStrings.getResourceBundle());
    this.filterAll =
      JWFileChooserFilterAllFiles.create(this.strings);

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

  @Test
  public void testListBase()
  {
    final var items = new JWFileList(this.filterAll);

    items.setItems(this.baseItems);
    Assertions.assertEquals(this.baseItems, items.items());
  }

  @Test
  public void testListSearch()
  {
    final var items = new JWFileList(this.filterAll);

    items.setItems(this.baseItems);
    items.setSearch("x");
    Assertions.assertEquals(List.of(this.item0), items.items());
    items.setSearch("");
    Assertions.assertEquals(this.baseItems, items.items());
  }

  @Test
  public void testListFilter()
  {
    final var items = new JWFileList(this.filterAll);

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

    Assertions.assertEquals(List.of(this.item1), items.items());
    items.setFilter(this.filterAll);
    Assertions.assertEquals(this.baseItems, items.items());
  }

  @Test
  public void testListFilterDirectories()
  {
    final var items = new JWFileList(this.filterAll);

    items.setItems(this.baseItems);
    items.setFilter(JWFileChooserFilterOnlyDirectories.create(this.strings));
    Assertions.assertEquals(List.of(), items.items());
    items.setFilter(this.filterAll);
    Assertions.assertEquals(this.baseItems, items.items());
  }
}
