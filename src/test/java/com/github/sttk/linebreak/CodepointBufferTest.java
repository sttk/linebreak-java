package com.github.sttk.linebreak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class CodepointBufferTest {

  @Test
  void testConstructor_empty() {
    var buf = new CodepointBuffer(0);
    assertThat(buf.codepoints).hasSize(0);
    assertThat(buf.length).isEqualTo(0);
    assertThat(buf.full()).isEqualTo(new int[0]);
  }

  @Test
  void testAdd() {
    var buf = new CodepointBuffer(5);
    assertThat(buf.codepoints).isEqualTo(new int[]{0, 0, 0, 0, 0});
    assertThat(buf.length).isEqualTo(0);
    assertThat(buf.full()).isEqualTo(new int[0]);

    assertThat(buf.add("1".codePointAt(0))).isTrue();
    assertThat(buf.codepoints).isEqualTo(new int[]{0x31, 0, 0, 0, 0});
    assertThat(buf.length).isEqualTo(1);
    assertThat(buf.full()).isEqualTo(new int[]{0x31});

    assertThat(buf.add("2".codePointAt(0), "3".codePointAt(0))).isTrue();
    assertThat(buf.codepoints).isEqualTo(new int[]{0x31, 0x32, 0x33, 0, 0});
    assertThat(buf.length).isEqualTo(3);
    assertThat(buf.full()).isEqualTo(new int[]{0x31, 0x32, 0x33});

    assertThat(buf.add(
      "x".codePointAt(0), "y".codePointAt(0), "z".codePointAt(0)
    )).isFalse();
    assertThat(buf.codepoints).isEqualTo(new int[]{0x31, 0x32, 0x33, 0, 0});
    assertThat(buf.length).isEqualTo(3);
    assertThat(buf.full()).isEqualTo(new int[]{0x31, 0x32, 0x33});

    assertThat(buf.add("4".codePointAt(0), "5".codePointAt(0))).isTrue();
    assertThat(buf.codepoints).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });
    assertThat(buf.length).isEqualTo(5);
    assertThat(buf.full()).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });

    assertThat(buf.add("6".codePointAt(0))).isFalse();
    assertThat(buf.codepoints).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });
    assertThat(buf.length).isEqualTo(5);
    assertThat(buf.full()).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });
  }

  @Test
  void testCr() {
    var buf = new CodepointBuffer(5);
    assertThat(buf.codepoints).isEqualTo(new int[]{0, 0, 0, 0, 0});
    assertThat(buf.length).isEqualTo(0);
    assertThat(buf.full()).isEqualTo(new int[0]);

    assertThat(buf.add(
      "1".codePointAt(0), "2".codePointAt(0), "3".codePointAt(0),
      "4".codePointAt(0), "5".codePointAt(0)
    )).isTrue();
    assertThat(buf.codepoints).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });
    assertThat(buf.length).isEqualTo(5);
    assertThat(buf.full()).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });

    buf.cr(3);
    assertThat(buf.codepoints).isEqualTo(new int[]{
      0x34, 0x35, 0x33, 0x34, 0x35,
    });
    assertThat(buf.length).isEqualTo(2);
    assertThat(buf.full()).isEqualTo(new int[]{0x34, 0x35});

    assertThat(buf.add("6".codePointAt(0))).isTrue();
    assertThat(buf.codepoints).isEqualTo(new int[]{
      0x34, 0x35, 0x36, 0x34, 0x35,
    });
    assertThat(buf.length).isEqualTo(3);
    assertThat(buf.full()).isEqualTo(new int[]{0x34, 0x35, 0x36});

    buf.cr(3);
    assertThat(buf.codepoints).isEqualTo(new int[]{
      0x34, 0x35, 0x36, 0x34, 0x35,
    });
    assertThat(buf.length).isEqualTo(0);
    assertThat(buf.full()).isEqualTo(new int[0]);

    assertThat(buf.add(
      "1".codePointAt(0), "2".codePointAt(0), "3".codePointAt(0),
      "4".codePointAt(0), "5".codePointAt(0)
    )).isTrue();
    assertThat(buf.codepoints).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });
    assertThat(buf.length).isEqualTo(5);
    assertThat(buf.full()).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });

    buf.cr(0);
    assertThat(buf.codepoints).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });
    assertThat(buf.length).isEqualTo(5);
    assertThat(buf.full()).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });

    buf.cr(-1);
    assertThat(buf.codepoints).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });
    assertThat(buf.length).isEqualTo(5);
    assertThat(buf.full()).isEqualTo(new int[]{
      0x31, 0x32, 0x33, 0x34, 0x35
    });
  }
}
