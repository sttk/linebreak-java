/*
 * module-info class.
 * Copyright (C) 2023-2024 Takayuki Sato. All Rights Reserved.
 */

/**
 * Defines the module of this library.
 */
module com.github.sttk.linebreak {
  exports com.github.sttk.linebreak;
  exports com.github.sttk.linebreak.terminal to com.sun.jna;
  requires transitive com.ibm.icu;
  requires transitive com.sun.jna;
}
