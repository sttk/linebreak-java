/*
 * UnixTerminal class.
 * Copyright (C) 2023 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak.terminal;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.LastErrorException;
import java.util.Arrays;
import java.util.List;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.IOException;

class UnixTerminal extends Terminal {

  private final C lib = Native.load("c", C.class);

  private final NativeLong TIOCGWINSZ = new NativeLong(0x40087468L);
  private final int FD_STDOUT = 1;

  @Override
  public boolean isNotty(LastErrorException e) {
    // https://docs.oracle.com/cd/E19455-01/806-2720/msgs-378/index.html
    return (e.getErrorCode() == 25);
  }

  @Override
  public int getSizeX() throws LastErrorException {
    var ws = new winsize();
    lib.ioctl(FD_STDOUT, TIOCGWINSZ, ws);
    return Short.toUnsignedInt(ws.ws_col);
  }

  @Override
  public int[] getSizeXY() throws LastErrorException {
    var ws = new winsize();
    lib.ioctl(FD_STDOUT, TIOCGWINSZ, ws);
    return new int[]{
      Short.toUnsignedInt(ws.ws_col),
      Short.toUnsignedInt(ws.ws_row),
    };
  }

  public static class winsize extends Structure {
    public short ws_row;
    public short ws_col;
    public short ws_xpixel;
    public short ws_ypixel;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList(
        "ws_row",
        "ws_col",
        "ws_xpixel",
        "ws_ypixel"
      );
    }
  }

  static interface C extends Library {
    void ioctl(int fd, NativeLong cmd, winsize data) throws LastErrorException;
  }

  /*
  int[] getSizeXYByProcessStty() throws IOException {
    var pb = new ProcessBuilder("sh", "-c", "stty size < /dev/tty");
    var p = pb.start();
    try (
     var r = new BufferedReader(new InputStreamReader(p.getInputStream()))
    ) {
      String[] sz = r.readLine().split(" ");
      return new int[]{
        Integer.valueOf(sz[1]),
        Integer.valueOf(sz[0]),
      };
    }
  }
  */
}
