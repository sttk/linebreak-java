/*
 * LboRule class.
 * Copyright (C) 2023 Takayuki Sato. All Rights Reserved.
 */
package com.github.sttk.linebreak;

interface LboRule {

  static final int[] Breaks = {
    0x0a, // LF
    0x0d, // CR
  };

  // https://en.wikipedia.org/wiki/Line_breaking_rules_in_East_Asian_languages

  static final int[] Befores = {
    0x0028, // (
    0x005B, // [
    0x007B, // {
    0x00AB, // «
    0x3008, // 〈
    0x300A, // 《
    0x300C, // 「
    0x300E, // 『
    0x3010, // 【
    0x3014, // 〔
    0x3016, // 〖
    0x3018, // 〘
    0x301D, // 〝
    0xFF5F, // ｟
  };

  static final int[] Afters = {
    0x0021, // !
    0x0029, // )
    0x002C, // ,
    0x002E, // .
    0x002F, // /
    0x003A, // :
    0x003B, // ;
    0x003F, // ?
    0x30A0, // ゠
    0x30A1, // ァ
    0x30A3, // ィ
    0x30A5, // ゥ
    0x30A7, // ェ
    0x30A9, // ォ
    0x30C3, // ッ
    0x30E3, // ャ
    0x30E5, // ュ
    0x30E7, // ョ
    0x30EE, // ヮ
    0x30F5, // ヵ
    0x30F6, // ヶ
    0x3041, // ぁ
    0x3043, // ぃ
    0x3045, // ぅ
    0x3047, // ぇ
    0x3049, // ぉ
    0x3063, // っ
    0x3083, // ゃ
    0x3085, // ゅ
    0x3087, // ょ
    0x308E, // ゎ
    0x3095, // ゕ
    0x3096, // ゖ
    0x30FC, // ー
    0x3001, // 、
    0x3002, // 。
    0x3005, // 々
    0x3008, // 〈
    0x3009, // 〉
    0x300A, // 《
    0x300B, // 》
    0x300C, // 「
    0x300D, // 」
    0x300E, // 』
    0x300F, // 】
    0x3015, // 〕
    0x3017, // 〗
    0x3019, // 〙
    0x301F, // 〟
    0xFF09, // )
    0xFF5D, // ｝
  };
}
