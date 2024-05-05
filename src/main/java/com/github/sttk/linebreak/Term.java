/*
 * Term class.
 * Copyright (C) 2023-2024 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak;

import com.sun.jna.LastErrorException;

/**
 * {@code Term} is the class that provides static methods to get the
 * informations about the terminal.
 */
public final class Term {

  /**
   * {@code Size} is the record class which represents a terminzal size
   * holding its column count and row count.
   *
   * @param cols  The column count of the terminal.
   * @param rows  The row count of the terminal.
   */
  public record Size(int cols, int rows) {}

  private Term() {}

  /**
   * Returns the column count of the current terminal.
   * The column count is the count of ASCII printable characters.
   * If it failed to get the count, this function returns the fixed number: 80.
   *
   * @return  Terminal column count.
   */
  public static int getCols() {
    try {
      return AbstractTerminal.getInstance().getCols();
    } catch (Exception e) {
      return 80;
    }
  }

  /**
   * Gets the size of the current terminal.
   *
   * @return  A {@link Size} object holding the current terminal size.
   */
  public static Size getSize() {
    try {
      var size = AbstractTerminal.getInstance().getSize();
      return new Size(size[0], size[1]);
    } catch (Exception e) {
      return new Size(80, 24);
    }
  }
}
