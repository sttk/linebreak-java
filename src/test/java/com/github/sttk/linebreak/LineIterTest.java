package com.github.sttk.linebreak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

@SuppressWarnings("missing-explicit-ctor")
public class LineIterTest {

  @Test
  void testNext_emptyText() {
    var text = "";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text);

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_nullText() {
    String text = null;
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_oneCharText() {
    var text = "a";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text);

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_oneCharText_lboBreak() {
    String text = "\n";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_lessThanLineWidth() {
    var text = "1234567890123456789";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text);

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_equalToLineWidth() {
    var text = "12345678901234567890";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text);

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_breakAtLineBreakOpportunity() {
    var text = "1234567890 abcdefghij";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(0, 10));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(11, 21));

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_removeHeadingSpaceOfEachLine() {
    var text = "12345678901234567890   abcdefghij";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(0, 20));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(23, 33));

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_removeTailingSpaceOfEachLine() {
    var text = "12345678901234567      abcdefghij";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(0, 17));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(23, 33));

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_removeSpacesOfAllSpaceLine() {
    var text = "       ";
    var iter = new LineIter(text, 10);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_thereIsNoLineBreakOpportunity() {
    var text = "12345678901234567890abcdefghij";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(0, 20));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(20, 30));

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testSetIndent() {
    var text = "12345678901234567890abcdefghij";
    var iter = new LineIter(text, 10);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(0, 10));

    iter.setIndent("   ");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("   " + text.substring(10, 17));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("   " + text.substring(17, 24));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("   " + text.substring(24, 30));

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testSetIndent_null() {
    var text = "12345678901234567890abcdefghij";
    var iter = new LineIter(text, 10);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(0, 10));

    iter.setIndent(null);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(10, 20));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(20, 30));

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testBreakPositionAfterIndentWidthIsIncreased() {
    var lineWidth = 30;
    var indent = " ".repeat(7);
    var text = "aaaaa " + "b".repeat(lineWidth - 7) + "c".repeat(lineWidth - 7) + "ddd";

    var iter = new LineIter(text, lineWidth);

    assertThat(iter.hasNext()).isTrue();
    var line = iter.next();
    assertThat(line).isEqualTo("aaaaa");
    assertThat(line).hasSize(5);

    iter.setIndent(indent);

    assertThat(iter.hasNext()).isTrue();
    line = iter.next();
    assertThat(line).isEqualTo(" ".repeat(7) + "b".repeat(lineWidth - 7));
    assertThat(line).hasSize(30);

    assertThat(iter.hasNext()).isTrue();
    line = iter.next();
    assertThat(line).isEqualTo(" ".repeat(7) + "c".repeat(lineWidth - 7));
    assertThat(line).hasSize(30);

    assertThat(iter.hasNext()).isTrue();
    line = iter.next();
    assertThat(line).isEqualTo(" ".repeat(7) + "ddd");
    assertThat(line).hasSize(10);

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testBreakPositionIfIndentContainsFullWidthChars() {
    var lineWidth = 30;
    var indent = "__ああ__";  // width is 8.
    var text = "aaaaa " + "b".repeat(lineWidth - 8) + "c".repeat(lineWidth - 8) + "ddd";

    var iter = new LineIter(text, lineWidth);

    assertThat(iter.hasNext()).isTrue();
    var line = iter.next();
    assertThat(line).isEqualTo("aaaaa");
    assertThat(line).hasSize(5);

    iter.setIndent(indent);

    assertThat(iter.hasNext()).isTrue();
    line = iter.next();
    assertThat(line).isEqualTo("__ああ__" + "b".repeat(lineWidth - 8));
    assertThat(Unicode.getTextWidth(line)).isEqualTo(30);

    assertThat(iter.hasNext()).isTrue();
    line = iter.next();
    assertThat(line).isEqualTo("__ああ__" + "c".repeat(lineWidth - 8));
    assertThat(Unicode.getTextWidth(line)).isEqualTo(30);

    assertThat(iter.hasNext()).isTrue();
    line = iter.next();
    assertThat(line).isEqualTo("__ああ__" + "ddd");
    assertThat(Unicode.getTextWidth(line)).isEqualTo(11);

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testInit() {
    var text = "12345678901234567890";
    var iter = new LineIter(text, 12);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(0, 12));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(12, 20));

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");

    text = "abcdefghijklmnopqrstuvwxyz";
    iter.init(text);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(0, 12));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(12, 24));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(24, 26));

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testInit_null() {
    var text = "12345678901234567890";
    var iter = new LineIter(text, 12);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(0, 12));

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo(text.substring(12, 20));

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");

    iter.init(null);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  // https://www.java.com/en/download/help/whatis_java.html
  final String longText = "Java is a programming language and computing platform first released by Sun Microsystems in 1995. It has evolved from humble beginnings to power a large share of today's digital world, by providing the reliable platform upon which many services and applications are built. New, innovative products and digital services designed for the future continue to rely on Java, as well.  ";

  @Test
  void testNext_tryLongText() {
    var iter = new LineIter(longText, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("Java is a");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("programming language");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("and computing");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("platform first");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("released by Sun");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("Microsystems in");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("1995. It has evolved");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("from humble");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("beginnings to power");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("a large share of");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("today's digital");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("world, by providing");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("the reliable");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("platform upon which");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("many services and");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("applications are");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("built. New,");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("innovative products");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("and digital services");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("designed for the");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("future continue to");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("rely on Java, as");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("well.");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void testNext_printLongText() {
    var iter = new LineIter(longText, 20);

    while (iter.hasNext()) {
      var line = iter.next();
      System.out.println(line);
    }
  }

  @Test
  void test_setIndentToLongText() {
    var iter = new LineIter(longText, 40);

    var line = iter.next();
    System.out.println(line);

    iter.setIndent(" ".repeat(8));

    while (iter.hasNext()) {
      line = iter.next();
      System.out.println(line);
    }
  }

  @Test
  void test_textContainsNonPrintChar() {
    var text = "abcdefg\u0002hijklmn";
    var iter = new LineIter(text, 10);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("abcdefghij");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("klmn");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void test_letterWithOfEastAsianWideLetter() {
    var text = "東アジアの全角文字は２文字分の幅をとります。";
    var iter = new LineIter(text, 20);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("東アジアの全角文字は");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("２文字分の幅をとりま");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("す。");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void test_lineBreaksOfEastAsianWideLetter() {
    var text = "東アジアの全角文字は基本的に、文字の前後どちらに行の終わりが" +
      "来ても改行が行われます。";
    var iter = new LineIter(text, 28);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("東アジアの全角文字は基本的");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("に、文字の前後どちらに行の終");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("わりが来ても改行が行われま");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("す。");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void test_prohibitionsOfLineBreakOfJapanese_start() {
    var text = "句読点は、行頭に置くことは禁止である。";
    var iter = new LineIter(text, 8);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("句読点");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("は、行頭");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("に置くこ");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("とは禁止");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("である。");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void test_prohibitionsOfLineBreakOfJapanese_end() {
    var text = "開き括弧は「行末に置く」ことは禁止である。";
    var iter = new LineIter(text, 12);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("開き括弧は");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("「行末に置");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("く」ことは禁");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("止である。");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void test_prohibitionsOfLineBreakOfEnglish() {
    var text = "abc def ghi(jkl mn opq rst uvw xyz)";
    var iter = new LineIter(text, 11);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("abc def ghi");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("(jkl mn opq");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("rst uvw");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("xyz)");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void test_prohibitionsOfLineBreakOfEnglish_quot() {
    var text = "abc def \" ghi j \" kl mno pq\" rst uvw\" xyz";
    var iter = new LineIter(text, 9);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("abc def");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("\" ghi j \"");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("kl mno pq");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("\" rst");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("uvw\" xyz");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void test_prohibitionsOfLineBreakOfEnglish_apos() {
    var text = "abc def ' ghi j ' kl mno pq' rst uvw' xyz";
    var iter = new LineIter(text, 9);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("abc def");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("' ghi j '");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("kl mno pq");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("' rst");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("uvw' xyz");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");
  }

  @Test
  void test_prohibitionsOfLineBreakOfEnglish_quoteAndApos() {
    var text = "abc def \" ghi j ' kl mno pq' rst uvw\" xyz";
    var iter = new LineIter(text, 9);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("abc def");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("\" ghi j");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("' kl mno");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("pq' rst");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("uvw\" xyz");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");

    iter.init(text);

    while (iter.hasNext()) {
      var line = iter.next();
      System.out.println(line);
    }
  }

  @Test
  void test_prohibitionsOfLineBreakOfEnglish_aposAndQuote() {
    var text = "abc def ' ghi j \" kl mno pq\" rst uvw' xyz";
    var iter = new LineIter(text, 9);

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("abc def");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("' ghi j");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("\" kl mno");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("pq\" rst");

    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.next()).isEqualTo("uvw' xyz");

    assertThat(iter.hasNext()).isFalse();
    assertThat(iter.next()).isEqualTo("");

    iter.init(text);

    while (iter.hasNext()) {
      var line = iter.next();
      System.out.println(line);
    }
  }

  @Test
  void test_japanese() {
    var text = "私はその人を常に先生と呼んでいた。だからここでもただ先生と" +
      "書くだけで本名は打ち明けない。これは世間を憚かる遠慮というよりも、" +
      "その方が私にとって自然だからである。私はその人の記憶を呼び起すごと" +
      "に、すぐ「先生」といいたくなる。筆を執っても心持は同じ事である。よそ" +
      "よそしい頭文字などはとても使う気にならない。\n（夏目漱石「こころ」" +
      "から引用）";

    var iter = new LineIter(text, 50);

    while (iter.hasNext()) {
      var line = iter.next();
      System.out.println(line);
    }
  }

  @Test
  void testContains() {
    final int a = "a".codePointAt(0);
    final int b = "b".codePointAt(0);
    final int c = "c".codePointAt(0);

    var iter = new LineIter("", 50);

    assertThat(iter.contains(new int[]{}, a)).isFalse();

    assertThat(iter.contains(new int[]{a}, a)).isTrue();
    assertThat(iter.contains(new int[]{b}, a)).isFalse();

    assertThat(iter.contains(new int[]{a, b}, a)).isTrue();
    assertThat(iter.contains(new int[]{a, b}, b)).isTrue();
    assertThat(iter.contains(new int[]{a, b}, c)).isFalse();
  }

  @Test
  void testTrimRightAndToString_buffer() {
    final int a = "a".codePointAt(0);
    final int sp = " ".codePointAt(0);

    var iter = new LineIter("", 20);

    var buf = new CodepointBuffer(5);
    assertThat(buf.length).isEqualTo(0);
    assertThat(buf.full()).isEqualTo(new int[]{});
    assertThat(iter.trimRightAndToString(buf)).isEqualTo("");

    buf.add(a);
    assertThat(buf.length).isEqualTo(1);
    assertThat(buf.full()).isEqualTo(new int[]{a});
    assertThat(iter.trimRightAndToString(buf)).isEqualTo("a");

    buf.add(sp, sp);
    assertThat(buf.length).isEqualTo(3);
    assertThat(buf.full()).isEqualTo(new int[]{a, sp, sp});
    assertThat(iter.trimRightAndToString(buf)).isEqualTo("a");
  }

  @Test
  void testTrimRightAndToString_buffer_start_end() {
    final int a = "a".codePointAt(0);
    final int sp = " ".codePointAt(0);

    var iter = new LineIter("", 20);

    var buf = new CodepointBuffer(5);
    assertThat(buf.length).isEqualTo(0);
    assertThat(buf.full()).isEqualTo(new int[]{});
    assertThat(iter.trimRightAndToString(buf, 0, 0)).isEqualTo("");

    buf.add(a);
    assertThat(buf.length).isEqualTo(1);
    assertThat(buf.full()).isEqualTo(new int[]{a});
    assertThat(iter.trimRightAndToString(buf, 0, 0)).isEqualTo("");
    assertThat(iter.trimRightAndToString(buf, 0, 1)).isEqualTo("a");
    assertThat(iter.trimRightAndToString(buf, 1, 1)).isEqualTo("");

    buf.add(a, sp, sp);
    assertThat(buf.length).isEqualTo(4);
    assertThat(buf.full()).isEqualTo(new int[]{a, a, sp, sp});
    assertThat(iter.trimRightAndToString(buf, 0, 0)).isEqualTo("");
    assertThat(iter.trimRightAndToString(buf, 0, 1)).isEqualTo("a");
    assertThat(iter.trimRightAndToString(buf, 0, 2)).isEqualTo("aa");
    assertThat(iter.trimRightAndToString(buf, 0, 3)).isEqualTo("aa");
    assertThat(iter.trimRightAndToString(buf, 0, 4)).isEqualTo("aa");
    assertThat(iter.trimRightAndToString(buf, 1, 1)).isEqualTo("");
    assertThat(iter.trimRightAndToString(buf, 1, 2)).isEqualTo("a");
    assertThat(iter.trimRightAndToString(buf, 1, 3)).isEqualTo("a");
    assertThat(iter.trimRightAndToString(buf, 1, 4)).isEqualTo("a");
    assertThat(iter.trimRightAndToString(buf, 2, 2)).isEqualTo("");
    assertThat(iter.trimRightAndToString(buf, 2, 3)).isEqualTo("");
    assertThat(iter.trimRightAndToString(buf, 2, 4)).isEqualTo("");
    assertThat(iter.trimRightAndToString(buf, 3, 3)).isEqualTo("");
    assertThat(iter.trimRightAndToString(buf, 3, 4)).isEqualTo("");
    assertThat(iter.trimRightAndToString(buf, 4, 4)).isEqualTo("");
  }
}
