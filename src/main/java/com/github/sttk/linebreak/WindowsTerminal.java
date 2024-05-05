/*
 * WindowTerminal class.
 * Copyright (C) 2023-2024 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.LastErrorException;
import java.util.Arrays;
import java.util.List;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.IOException;

class WindowsTerminal extends AbstractTerminal {

  private Kernel32 lib = Native.load("kernel32", Kernel32.class);

  // https://learn.microsoft.com/ja-jp/windows/console/getstdhandle
  private final int STD_OUTPUT_HANDLE = -11;

  @Override
  boolean isNotty(LastErrorException e) {
    // https://docs.oracle.com/cd/E19455-01/806-2720/msgs-378/index.html
    return (e.getErrorCode() == 6);
  }

  @Override
  int getCols() throws LastErrorException {
    int h = lib.GetStdHandle(STD_OUTPUT_HANDLE);
    var bi = new CONSOLE_SCREEN_BUFFER_INFO();
    lib.GetConsoleScreenBufferInfo(h, bi);
    return bi.wWindowRight - bi.wWindowLeft + 1;
  }

  @Override
  int[] getSize() throws LastErrorException {
    int h = lib.GetStdHandle(STD_OUTPUT_HANDLE);
    var bi = new CONSOLE_SCREEN_BUFFER_INFO();
    lib.GetConsoleScreenBufferInfo(h, bi);
    return new int[]{
      bi.wWindowRight - bi.wWindowLeft + 1,
      bi.wWindowBottom - bi.wWindowTop + 1
    };
  }

  public static class CONSOLE_SCREEN_BUFFER_INFO extends Structure {
    public short wSizeX;
    public short wSizeY;
    public short wCursorPositionX;
    public short wCursorPositionY;
    public short wAttributes;
    public short wWindowLeft;
    public short wWindowTop;
    public short wWindowRight;
    public short wWindowBottom;
    public short wMaximumWindowSizeX;
    public short wMaximumWindowSizeY;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList( //
        "wSizeX",
        "wSizeY",
        "wCursorPositionX",
        "wCursorPositionY",
        "wAttributes",
        "wWindowLeft",
        "wWindowTop",
        "wWindowRight",
        "wWindowBottom",
        "wMaximumWindowSizeX",
        "wMaximumWindowSizeY"
        );
    }
  }

  static interface Kernel32 extends StdCallLibrary {
    int GetStdHandle(int stdHandleNo) throws LastErrorException;
    int GetConsoleScreenBufferInfo(int handle, CONSOLE_SCREEN_BUFFER_INFO bi)
      throws LastErrorException;
  }

  /*
  int[] getSizeXYByProcessCmd() throws IOException, RuntimeException {
    var pb = new ProcessBuilder("cmd", "/c", "mode");
    var p = pb.start();
    try (
      var r = new BufferedReader(new InputStreamReader(p.getInputStream()))
    ) {
      int rows = 0, cols = 0;
      String line;
      while ((line = r.readLine()) != null) {
        if (line.contains("Columns:")) {
          String[] sz = line.trim().split("\\s+");
          cols = Integer.valueOf(sz[1]);
        } else if (line.contains("Lines:")) {
          String[] sz = line.trim().split("\\s+");
          rows = Integer.valueOf(sz[1]);
        }
      }
      return new int[]{cols, rows};
    }
    throw new RuntimeException("No data.");
  }
  */
}
