/*
 * CodepointBuffer class.
 * Copyright (C) 2023 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak;

import java.util.Arrays;

class CodepointBuffer {
  int[] codepoints;
  int length;

  CodepointBuffer(int capacity) {
    this.codepoints = new int[capacity];
    this.length = 0;
  }

  boolean add(int ...codepoints) {
    int n = codepoints.length;
    if (this.length + n > this.codepoints.length) {
      return false;
    }

    System.arraycopy(codepoints, 0, this.codepoints, this.length, n);
    this.length += n;
    return true;
  }

  void cr(int start) {
    if (start < 0) {
      return;
    }
    if (start >= this.length) {
      this.length = 0;
      return;
    }

    int n = this.length - start;
    for (int i = 0; i < n; i++) {
      this.codepoints[i] = this.codepoints[i + start];
    }
    this.length = n;
  }

  int[] full() {
    return Arrays.copyOf(this.codepoints, this.length);
  }
}
