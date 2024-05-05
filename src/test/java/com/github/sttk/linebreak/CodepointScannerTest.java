package com.github.sttk.linebreak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

@SuppressWarnings("missing-explicit-ctor")
public class CodepointScannerTest {

  @Test
  void testConstructor_empty() {
    var scanner = new CodepointScanner();
    assertThat(scanner.hasNext()).isFalse();
    try {
      scanner.next();
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertThat(e).isNotNull();
    }
  }

  @Test
  void testConstructor_null() {
    var scanner = new CodepointScanner();
    scanner.setText(null);
    assertThat(scanner.hasNext()).isFalse();
    try {
      scanner.next();
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertThat(e).isNotNull();
    }
  }

  @Test
  void testNext_asciiChars() {
    var scanner = new CodepointScanner();
    scanner.setText("ab12#$");
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x61);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x62);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x31);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x32);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x23);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x24);
    assertThat(scanner.hasNext()).isFalse();
    try {
      scanner.next();
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertThat(e).isNotNull();
    }
  }

  @Test
  void testNext_japaneseChars() {
    var scanner = new CodepointScanner();
    scanner.setText("あい");
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x3042);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x3044);
    assertThat(scanner.hasNext()).isFalse();
    try {
      scanner.next();
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertThat(e).isNotNull();
    }
  }

  @Test
  void testNext_surrogatePairs() {
    var scanner = new CodepointScanner();
    scanner.setText("a😊b");
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x61);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x1f60a);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x62);
    assertThat(scanner.hasNext()).isFalse();
    try {
      scanner.next();
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertThat(e).isNotNull();
    }
  }

  @Test
  void testSetText_reset() {
    var scanner = new CodepointScanner();
    scanner.setText("ab");
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x61);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x62);
    assertThat(scanner.hasNext()).isFalse();
    try {
      scanner.next();
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertThat(e).isNotNull();
    }

    scanner.setText("cd");
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x63);
    scanner.setText("ef");
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x65);
    assertThat(scanner.hasNext()).isTrue();
    assertThat(scanner.next()).isEqualTo(0x66);
    assertThat(scanner.hasNext()).isFalse();
    try {
      scanner.next();
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertThat(e).isNotNull();
    }
  }
}
