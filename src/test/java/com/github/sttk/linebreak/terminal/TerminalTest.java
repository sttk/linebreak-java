package com.github.sttk.linebreak.terminal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import com.sun.jna.LastErrorException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class TerminalTest {

  @Test
  void testGetSizeX() {
    try {
      int result = Terminal.getInstance().getSizeX();
      int[] expected = getTermSizeXY();
      assertThat(result).isEqualTo(expected[0]);
    } catch (LastErrorException e) {
      assertThat(Terminal.getInstance().isNotty(e)).isTrue();
    }
  }

  public static boolean isTerminal() {
    try {
      Terminal.getInstance().getSizeX();
      return true;
    } catch (LastErrorException e) {
      if (Terminal.getInstance().isNotty(e)) {
        return false;
      }
      throw e;
    }
  }

  public static int[] getTermSizeXY() {
    try {
      var os = System.getProperty("os.name").toLowerCase();
      if (os.contains("windows")) {
        var pb = new ProcessBuilder("cmd", "/c", "mode");
        var p = pb.start();
        try (var r = new BufferedReader(new InputStreamReader(
            p.getInputStream()))) {
          int cols = 0, rows = 0;
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
      } else {
        var pb = new ProcessBuilder("sh", "-c", "stty size < /dev/tty");
        var p = pb.start();
        try (var r = new BufferedReader(new InputStreamReader(
            p.getInputStream()))) {
          String[] sz = r.readLine().split(" ");
          return new int[] {
            Integer.valueOf(sz[1]),
            Integer.valueOf(sz[0]),
          };
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
