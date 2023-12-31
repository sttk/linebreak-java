/*
 * CodepointScanner class.
 * Copyright (C) 2023 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak;

class CodepointScanner {
  private int index;
  private String text = "";

  CodepointScanner() {}

  void setText(String text) {
    this.text = (text != null) ? text : "";
    this.index = 0;
  }

  boolean hasNext() {
    return (index < this.text.length());
  }

  int next() {
    int cp = this.text.codePointAt(this.index);
    this.index += Character.charCount(cp);
    return cp;
  }
}
