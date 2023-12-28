/*
 * Unicode class.
 * Copyright (C) 2023 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak;

import com.ibm.icu.lang.CharacterProperties;
import com.ibm.icu.lang.UCharacter.EastAsianWidth;
import com.ibm.icu.lang.UProperty;
import com.ibm.icu.util.CodePointMap;

/**
 * {@code Unicode} is the class that provides static methods to operate or
 * check Unicode characters or texts.
 */
public final class Unicode {

  private static final CodePointMap eastAsianWidthMap =
    CharacterProperties.getIntPropertyMap(UProperty.EAST_ASIAN_WIDTH);

  private Unicode() {}

  /**
   * Checks whether the specified codepoint is a space character as defined by
   * Unicode's White Space property.
   *
   * @param codepoint  An Unicode codepoint.
   * @return  True, if the codepoint is a space character.
   */
  public static boolean isSpace(int codepoint) {
    if (0x09 <= codepoint && codepoint <= 0x0d) {
      return true;
    }
    return switch (codepoint) {
      case 0x20, 0x85 -> true;
      default -> switch (Character.getType(codepoint)) {
        case Character.SPACE_SEPARATOR,
             Character.LINE_SEPARATOR,
             Character.PARAGRAPH_SEPARATOR -> true;
        default -> false;
      };
    };
  }

  /**
   * Checks whether the specified codepoint is one of the printable characters
   * that includes letters, marks, numbers, punctuations, symbols from Unicode
   * categories L, M, N, P, S, and the ASCII space character, 
   *
   * @param codepoint  An Unicode codepoint.
   * @return  True, if the codepoint is a printable character.
   */
  public static boolean isPrint(int codepoint) {
    if (codepoint == 0x20) { // SP, SPACE
      return true;
    }
    switch (Character.getType(codepoint)) {
    case Character.LOWERCASE_LETTER: // Ll
    case Character.MODIFIER_LETTER: // Lm
    case Character.OTHER_LETTER: // Lo
    case Character.TITLECASE_LETTER: // Lt
    case Character.UPPERCASE_LETTER: // Lu
    case Character.COMBINING_SPACING_MARK: // Mc
    case Character.ENCLOSING_MARK: // Me
    case Character.NON_SPACING_MARK: // Mn
    case Character.DECIMAL_DIGIT_NUMBER: // Nd
    case Character.LETTER_NUMBER: // Nl
    case Character.OTHER_NUMBER: // No
    case Character.CONNECTOR_PUNCTUATION: // Pc
    case Character.DASH_PUNCTUATION: // Pd
    case Character.END_PUNCTUATION: // Pe
    case Character.FINAL_QUOTE_PUNCTUATION: // Pf
    case Character.INITIAL_QUOTE_PUNCTUATION: // Pi
    case Character.OTHER_PUNCTUATION: // Po
    case Character.START_PUNCTUATION: // Ps
    case Character.CURRENCY_SYMBOL: // Sc
    case Character.MODIFIER_SYMBOL: // Sk
    case Character.MATH_SYMBOL: // Sm
    case Character.OTHER_SYMBOL: // So
      return true;
    }
    return false;
  }

  static int getEastAsianWidth(int codepoint) {
    return eastAsianWidthMap.get(codepoint);
  }

  /**
   * Returns the display width of the specified Unicode codepoiot.
   * A display width is determined by the Unicode Standard Annex #11 (UAX11)
   * East-Asian-Width.
   *
   * @param codepoint  An Unicode codepoint.
   * @return  A codepoint display width.
   */
  public static int getCodepointWidth(int codepoint) {
    if (! isPrint(codepoint)) {
      return 0;
    }

    return switch (eastAsianWidthMap.get(codepoint)) {
      case EastAsianWidth.NARROW,
           EastAsianWidth.HALFWIDTH,
           EastAsianWidth.NEUTRAL -> 1;
      case EastAsianWidth.WIDE,
           EastAsianWidth.FULLWIDTH -> 2;
      default /* AMBIGUOUS */ -> 2;
    };
  }

  /**
   * Returns the display width of the specified Unicode text.
   * The display width is calculated by taking into account the letter width
   * determined by the Unicode Standard Annex #11 (UAX11) East-Asian-Width.
   *
   * @param text  A text of which the width is calculated.
   * @return  A text display width.
   */
  public static int getTextWidth(String text) {
    if (text == null) {
      return 0;
    }

    final CodepointScanner scanner = new CodepointScanner();
    scanner.setText(text);

    int w = 0;
    while (scanner.hasNext()) {
      w += getCodepointWidth(scanner.next());
    }
    return w;
  }
}
