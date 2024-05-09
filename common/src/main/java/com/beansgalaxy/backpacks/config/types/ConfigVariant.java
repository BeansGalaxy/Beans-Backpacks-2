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
      public String comment(int whiteSpace) {
            String autoComment = autoComment();
            boolean noComment = Constants.isEmpty(comment);
            boolean noAuto = Constants.isEmpty(autoComment);

            StringBuilder sb = new StringBuilder();
            if (!noComment || !noAuto) {
                  sb.append(" ".repeat(whiteSpace));
                  sb.append(" // ").append(comment);
                  if (!noComment && !noAuto)
                        sb.append(" : ");
                  sb.append(autoComment);
            }
            return sb.toString();
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
