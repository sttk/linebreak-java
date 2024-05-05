/*
 * AbstractTerminal class.
 * Copyright (C) 2023-2024 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak;

import com.sun.jna.LastErrorException;

abstract class AbstractTerminal {

  private static AbstractTerminal INSTANCE = newInstance();

  abstract boolean isNotty(LastErrorException e);
  abstract int getCols() throws LastErrorException;
  abstract int[] getSize() throws LastErrorException;

  private static AbstractTerminal newInstance() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      return new WindowsTerminal();
    } else {
      return new UnixTerminal();
    }
  }

  static AbstractTerminal getInstance() {
    return INSTANCE;
  }
}
