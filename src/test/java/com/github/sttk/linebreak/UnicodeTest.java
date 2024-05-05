package com.github.sttk.linebreak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static com.ibm.icu.lang.UCharacter.EastAsianWidth.NEUTRAL;
import static com.ibm.icu.lang.UCharacter.EastAsianWidth.AMBIGUOUS;
import static com.ibm.icu.lang.UCharacter.EastAsianWidth.HALFWIDTH;
import static com.ibm.icu.lang.UCharacter.EastAsianWidth.FULLWIDTH;
import static com.ibm.icu.lang.UCharacter.EastAsianWidth.NARROW;
import static com.ibm.icu.lang.UCharacter.EastAsianWidth.WIDE;

@SuppressWarnings("missing-explicit-ctor")
public class UnicodeTest {

  @DisabledIfEnvironmentVariable(named = "ALLOW_SLOW", matches = "1")
  @Test
  void testIsSpace_onlyUCS2() {
    for (long i = 0x0000; i <= 0xffff; i++) {
      checkIsSpace((int) i);
    }
  }

  @EnabledIfEnvironmentVariable(named = "ALLOW_SLOW", matches = "1")
  @Test
  void testIsSpace() {
    for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i++) {
      checkIsSpace((int) i);
    }
  }

  @DisabledIfEnvironmentVariable(named = "ALLOW_SLOW", matches = "1")
  @Test
  void testIsPrint_onlyUCS2() {
    for (long i = 0x0000; i <= 0xffff; i++) {
      checkIsPrint((int) i);
    }
  }

  @EnabledIfEnvironmentVariable(named = "ALLOW_SLOW", matches = "1")
  @Test
  void testIsPrint() {
    for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i++) {
      checkIsPrint((int) i);
    }
  }

  void checkIsSpace(int codepoint) {
    boolean b = Unicode.isSpace(codepoint);
    switch (codepoint) {
    case 0x09: // HT, CHARACTER TABULATION
    case 0x0A: // LF, LINE FEED
    case 0x0B: // VT, LINE TABULATION
    case 0x0C: // FF, FORM FEED
    case 0x0D: // CR, CARRIAGE RETURN
    case 0x20: // SP, SPACE
    case 0x85: // NEL, NEXT LINE
    case 0xA0: // NBSP, NO-BREAK SPACE
    case 0x1680: // , OGHAM SPACE MARK
    case 0x2000: // NQSP, EN QUAD
    case 0x2001: // MQSP, EM QUAD
    case 0x2002: // ENSP, EN SPACE
    case 0x2003: // EMSP, EM SPACE
    case 0x2004: // 3/MSP, THREE-PER-EM SPACE
    case 0x2005: // 4/MSP, FOUR-PER-EM SPACE
    case 0x2006: // 6/MSP, SIX-PER-EM SPACE
    case 0x2007: // FSP, FIGURE SPACE
    case 0x2008: // PSP, PUNCTUATION SPACE
    case 0x2009: // THSP, THIN SPACE
    case 0x200A: // HSP, HAIR SPACE
    case 0x2028: // LSEP, LINE SEPARATOR
    case 0x2029: // PSEP, PARAGRAPH SEPARATOR
    case 0x202F: // NNBSP, NARROW NO-BREAK SPACE
    case 0x205F: // MMSP, MEDIUM MATHEMATICAL SPACE
    case 0x3000: // IDSP, DEOGRAPHIC SPACE
      assertThat(b).isTrue();
      break;
    default:
      assertThat(b).isFalse();
      break;
    }
  }

  void checkIsPrint(int codepoint) {
    boolean b = Unicode.isPrint(codepoint);
    switch (Character.getType(codepoint)) {
    case Character.CONTROL: // Cc
    case Character.FORMAT: // Cf
    case Character.PRIVATE_USE: // Cp
    case Character.SURROGATE: // Cs
    case Character.UNASSIGNED: // Cn
    case Character.LINE_SEPARATOR: // Zl
    case Character.PARAGRAPH_SEPARATOR: // Zp
      assertThat(b).isFalse();
      break;
    case Character.SPACE_SEPARATOR: // Zs
      if (codepoint == 0x20) {
        assertThat(b).isTrue();
      } else {
        assertThat(b).isFalse();
      }
      break;
    default:
      assertThat(b).isTrue();
      break;
    }
  }

  @Test
  void testGetEastAsianWidth() {
    var cp = "क".codePointAt(0);
    assertThat(Unicode.getEastAsianWidth(cp)).isEqualTo(NEUTRAL);
    cp = "α'".codePointAt(0);
    assertThat(Unicode.getEastAsianWidth(cp)).isEqualTo(AMBIGUOUS);
    cp = "ｱ".codePointAt(0);
    assertThat(Unicode.getEastAsianWidth(cp)).isEqualTo(HALFWIDTH);
    cp = "Ａ".codePointAt(0);
    assertThat(Unicode.getEastAsianWidth(cp)).isEqualTo(FULLWIDTH);
    cp = "A".codePointAt(0);
    assertThat(Unicode.getEastAsianWidth(cp)).isEqualTo(NARROW);
    cp = "ア".codePointAt(0);
    assertThat(Unicode.getEastAsianWidth(cp)).isEqualTo(WIDE);
  }

  @Test
  void testGetCodepointWidth() {
    var cp = "क".codePointAt(0);
    assertThat(Unicode.getCodepointWidth(cp)).isEqualTo(1);
    cp = "α'".codePointAt(0);
    assertThat(Unicode.getCodepointWidth(cp)).isEqualTo(2);
    cp = "ｱ".codePointAt(0);
    assertThat(Unicode.getCodepointWidth(cp)).isEqualTo(1);
    cp = "Ａ".codePointAt(0);
    assertThat(Unicode.getCodepointWidth(cp)).isEqualTo(2);
    cp = "A".codePointAt(0);
    assertThat(Unicode.getCodepointWidth(cp)).isEqualTo(1);
    cp = "ア".codePointAt(0);
    assertThat(Unicode.getCodepointWidth(cp)).isEqualTo(2);
  }

  @Test
  void testGetTextWidth() {
    assertThat(Unicode.getTextWidth("abc")).isEqualTo(3);
    assertThat(Unicode.getTextWidth("あいう")).isEqualTo(6);
    assertThat(Unicode.getTextWidth("")).isEqualTo(0);
    assertThat(Unicode.getTextWidth(null)).isEqualTo(0);
  }
}
