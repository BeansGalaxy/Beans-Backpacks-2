package com.beansgalaxy.backpacks.config.types;

import com.beansgalaxy.backpacks.Constants;

public abstract class ConfigVariant<T> implements ConfigLine {
      protected final String name;
      protected final String comment;
      protected final boolean hasComment;
      protected final T defau;
      protected T value;

      protected ConfigVariant(String name, T defau, String comment) {
            this.name = name;
            this.defau = defau;
            this.value = defau;
            this.comment = comment;
            this.hasComment = !Constants.isEmpty(comment);
      }

      @Override
      public String comment() {
            String autoComment = autoComment();
            boolean hasTwo = !Constants.isEmpty(comment) && !Constants.isEmpty(autoComment);
            return hasTwo
                        ? comment + " : " + autoComment
                        : comment + autoComment;
      }

      public String autoComment() {
            return "";
      }

      public String name() {
            return name;
      }

      public String formattedName() {
            return '"' + name() + "\": ";
      }

      public void set(T value) {
            this.value = value;
      }

      public T get() {
            return value;
      }
}
