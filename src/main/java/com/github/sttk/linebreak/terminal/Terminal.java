/*
 * Terminal class.
 * Copyright (C) 2023 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak.terminal;

import com.sun.jna.LastErrorException;

public abstract class Terminal {

  private static Terminal INSTANCE = newInstance();

  public abstract boolean isNotty(LastErrorException e);
  public abstract int getSizeX() throws LastErrorException;
  public abstract int[] getSizeXY() throws LastErrorException;

  private static Terminal newInstance() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      return new WindowsTerminal();
    } else {
      return new UnixTerminal();
    }
  }

  public static Terminal getInstance() {
    return INSTANCE;
  }
}
