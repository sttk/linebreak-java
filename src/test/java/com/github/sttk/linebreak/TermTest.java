package com.github.sttk.linebreak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import static com.github.sttk.linebreak.terminal.TerminalTest.isTerminal;
import static com.github.sttk.linebreak.terminal.TerminalTest.getTermSizeXY;

public class TermTest {

  @Test
  void testGetCols() {
    if (isTerminal()) {
      assertThat(Term.getCols()).isEqualTo(getTermSizeXY()[0]);
    } else {
      assertThat(Term.getCols()).isEqualTo(80);
    }
  }

  @Test
  void testGetSize() {
    var resultSize = Term.getSize();
    if (isTerminal()) {
      var expectedSize = getTermSizeXY();
      assertThat(resultSize.cols()).isEqualTo(expectedSize[0]);
      assertThat(resultSize.rows()).isEqualTo(expectedSize[1]);
    } else {
      assertThat(resultSize.cols()).isEqualTo(80);
      assertThat(resultSize.rows()).isEqualTo(24);
    }
  }
}
