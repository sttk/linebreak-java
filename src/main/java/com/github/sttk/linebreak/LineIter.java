/*
 * LineIter class.
 * Copyright (C) 2023 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak;

import com.ibm.icu.lang.CharacterProperties;
import com.ibm.icu.lang.UCharacter.EastAsianWidth;
import com.ibm.icu.lang.UProperty;
import com.ibm.icu.util.CodePointMap;
import java.util.Arrays;
import java.util.Iterator;

/**
 * {@code LineIter} is the class that outputs the given string line by line.
 * This class can control the overall line width and the indentation from any
 * desired line.
 */
public class LineIter implements Iterator<String> {

  enum LboType {
    Never,
    Before,
    After,
    Both,
    Break,
    Space,
  }

  class LboState {
    LboType lboType;
    LboType lboPrev;
    byte openApos; // 0:not, 1:opened, 2:opened inside "..."
    byte openQuot; // 0:not, 1:opened, 2:opened inside "..."
  }

  private String text;
  private final CodepointScanner scanner = new CodepointScanner();
  private final CodepointBuffer buffer;
  private final int[] width = new int[2];
  private int lboPos = 0;
  private int limit = 0;
  private String indent = "";
  private int indentCodepointCount = 0;
  private byte openQuot;
  private byte openApos;
  private boolean hasNext = true;

  /**
   * Is the constructor that creates a {@code LineIter} instance which outputs
   * the given string line by line.
   *
   * @param text  A string to be output with line breaking.
   * @param lineWidth  A width of the output lines.
   */
  public LineIter(String text, int lineWidth) {
    if (text == null) text = "";
    this.text = text;
    this.scanner.setText(text);
    this.buffer = new CodepointBuffer(lineWidth);
    this.limit = lineWidth;
  }

  /**
   * Sets an indentation for the subsequent lines.
   *
   * @param indent  A string to be used for indentation.
   */
  public void setIndent(String indent) {
    if (indent == null) indent = "";
    this.indent = indent;
    this.indentCodepointCount = indent.codePointCount(0, indent.length());
  }

  /**
   * Re-initializes with an argument string for reusing this instance.
   *
   * @param text  A string to be output with line breaking.
   */
  public void init(String text) {
    if (text == null) text = "";
    this.text = text;
    this.scanner.setText(text);
    this.buffer.length = 0;
    this.width[0] = 0;
    this.width[1] = 0;
    this.lboPos = 0;
    this.openQuot = 0;
    this.openApos = 0;
    this.hasNext = true;
  }

  /**
   * Checks whether the remaining string exists or not.
   *
   * @return  True if the remaining string exists.
   */
  @Override
  public boolean hasNext() {
    return this.hasNext;
  }

  /**
   * Returns a string of the next line.
   *
   * @return  A next line string.
   */
  @Override
  public String next() {
    int limit = this.limit - this.indentCodepointCount;

    if (this.width[0] > limit) {
        int diff = this.width[0] - limit;
        this.width[0] = diff;
        for (int i = this.buffer.length - 1; i >= 0; i--) {
            int cp = this.buffer.codepoints[i];
            int cpWidth = Unicode.getCodepointWidth(cp);
            if (diff <= cpWidth) {
                String line = trimRightAndToString(this.buffer, 0, i);
                this.buffer.cr(i);
                if (! line.isEmpty()) {
                  line = this.indent + line;
                }
                this.hasNext = true;
                return line;
            }
            diff -= cpWidth;
        }
    } else if (this.width[0] == limit) {
        this.width[0] = 0;
        String line = trimRightAndToString(this.buffer);
        this.buffer.cr(0);
        if (! line.isEmpty()) {
          line = this.indent + line;
        }
        this.hasNext = true;
        return line;
    }

    String line = "";

    LboState state = new LboState();
    state.openQuot = this.openQuot;
    state.openApos = this.openApos;

    while (this.scanner.hasNext()) {
      int cp = this.scanner.next();
      lineBreakOpportunity(cp, state);

      if (state.lboType == LboType.Break) {
        line = trimRightAndToString(this.buffer);
        this.buffer.length = 0;
        this.width[0] = 0;
        this.width[1] = 0;
        this.openQuot = 0;
        this.openApos = 0;
        this.lboPos = 0;
        if (! line.isEmpty()) {
          line = this.indent + line;
        }
        this.hasNext = true;
        return line;
      }

      if (this.buffer.length == 0 && state.lboType == LboType.Space) {
        continue;
      }

      int cpWidth = Unicode.getCodepointWidth(cp);
      int lboPos = this.lboPos;

      if ((this.width[0] + this.width[1] + cpWidth) > limit) {
        if (state.lboPrev == LboType.Before) {
          line = trimRightAndToString(this.buffer, 0, lboPos);
          this.buffer.cr(lboPos);

          this.buffer.add(cp);
          this.width[0] = this.width[1] + cpWidth;
          this.width[1] = 0;
          this.lboPos = this.buffer.length;

          this.openQuot = state.openQuot;
          this.openApos = state.openApos;

          if (! line.isEmpty()) {
            line = this.indent + line;
          }
          this.hasNext = true;
          return line;
        }

        switch (state.lboType) {
        case LboType.Before, LboType.Both, LboType.Space:
          lboPos = this.buffer.length;
          break;
        }
        // break forcely when no lbo in the current line
        if (lboPos == 0) {
          this.width[0] += this.width[1];
          this.width[1] = 0;
          lboPos = this.buffer.length;
        }

        line = trimRightAndToString(this.buffer, 0, lboPos);
        this.buffer.cr(lboPos);

        switch (state.lboType) {
        case LboType.Space:
          this.width[0] = 0;
          this.width[1] = 0;
          this.lboPos = 0;
          break;
        case LboType.Before, LboType.Both:
          this.buffer.add(cp);
          this.width[0] = cpWidth;
          this.width[1] = 0;
          this.lboPos = 0;
          break;
        case LboType.After:
          this.buffer.add(cp);
          this.width[0] = this.width[1] + cpWidth;
          this.width[1] = 0;
          this.lboPos = this.buffer.length;
          break;
        default:
          this.buffer.add(cp);
          this.width[0] = this.width[1] + cpWidth;
          this.width[1] = 0;
          this.lboPos = 0;
          break;
        }

        this.openQuot = state.openQuot;
        this.openApos = state.openApos;

        if (! line.isEmpty()) {
          line = this.indent + line;
        }
        this.hasNext = true;
        return line;
      }

      if (cpWidth > 0) {
        this.buffer.add(cp);
      }
      switch (state.lboType) {
      case LboType.Before:
        if (state.lboPrev != LboType.Before) {
          this.lboPos = this.buffer.length - 1;
        }
        this.width[0] += this.width[1];
        this.width[1] = cpWidth;
        break;
      case LboType.Both:
        this.lboPos = this.buffer.length - 1;
        this.width[0] += this.width[1];
        this.width[1] = cpWidth;
        break;
      case LboType.After, LboType.Space:
        this.lboPos = this.buffer.length;
        this.width[0] += this.width[1] + cpWidth;
        this.width[1] = 0;
        break;
      default:
        this.width[1] += cpWidth;
        break;
      }
    }

    line = trimRightAndToString(this.buffer);
    this.buffer.length = 0;

    if (! line.isEmpty()) {
      line = this.indent + line;
    }
    this.hasNext = false;
    return line;
  }

  void lineBreakOpportunity(int codepoint, LboState state) {
    state.lboPrev = state.lboType;

    switch (codepoint) {
    case 0x22: // "
      if (state.openQuot == 0) { // open
        state.openQuot = (byte)(state.openApos + 1);
        state.lboType = LboType.Before;
      } else { // close
        if (state.openQuot < state.openApos) {
          state.openApos = 0;
        }
        state.openQuot = 0;
        state.lboType = LboType.After;
      }
      return;
    case 0x27: // '
      if (state.openApos == 0) { // open
        state.openApos = (byte)(state.openQuot + 1);
        state.lboType = LboType.Before;
      } else { // close
        if (state.openApos < state.openQuot) {
          state.openQuot = 0;
        }
        state.openApos = 0;
        state.lboType = LboType.After;
      }
      return;
    }

    if (contains(LboRule.Breaks, codepoint)) {
      state.lboType = LboType.Break;
      return;
    }

    if (contains(LboRule.Befores, codepoint)) {
      state.lboType = LboType.Before;
      return;
    }

    if (contains(LboRule.Afters, codepoint)) {
      state.lboType = LboType.After;
      return;
    }

    if (Unicode.isSpace(codepoint)) {
      state.lboType = LboType.Space;
      return;
    }

    switch (Unicode.getEastAsianWidth(codepoint)) {
    case EastAsianWidth.WIDE, EastAsianWidth.FULLWIDTH:
      state.lboType = LboType.Both;
      return;
    };

    state.lboType = LboType.Never;
  }

  boolean contains(int[] candidates, int codepoint) {
    for (int i = 0; i < candidates.length; i++) {
      if (candidates[i] == codepoint) {
        return true;
      }
    }
    return false;
  }

  String trimRightAndToString(CodepointBuffer buffer) {
    for (int i = buffer.length - 1; i >= 0; i--) {
      if (!Unicode.isSpace(buffer.codepoints[i])) {
        return new String(buffer.codepoints, 0, i+1);
      }
    }
    return "";
  }

  String trimRightAndToString(CodepointBuffer buffer, int start, int end) {
    for (int i = end - 1; i >= start; i--) {
      if (!Unicode.isSpace(buffer.codepoints[i])) {
        return new String(buffer.codepoints, start, (i+1 - start));
      }
    }
    return "";
  }
}
