package org.constraintlayout.swing;

public @interface Language {
      String value();

     String prefix() default "";
     String suffix() default "";
}