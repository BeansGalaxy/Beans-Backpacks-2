package com.beansgalaxy.backpacks.access;

import net.minecraft.util.Mth;

import static java.lang.Math.*;

public class Tint {
      private int rgba;
      private double hue = 0;
      private double sat = 0;
      private double val = 0;
      public static int track = 0;

      public Tint(int rgb) {
            this.rgba = rgb | 0xff000000;
      }

      public Tint(int rgba, boolean hasAlpha) {
            this.rgba = rgba;
      }

      public HSV HSV() {
            return new HSV(this);
      }

      public HSL HSL() {
            return new HSL(this);
      }

      private void setRGB(double r, double g, double b) {
            rgba = ((Mth.floor(r * 255) & 0xFF) << 16) |
                   ((Mth.floor(g * 255) & 0xFF) << 8)  |
                   ((Mth.floor(b * 255) & 0xFF));
      }

      private void setRGB(int r, int g, int b) {
            rgba = ((r & 0xFF) << 16) |
                   ((g & 0xFF) << 8)  |
                   ((b & 0xFF));
      }

      private int setRGB(double r, double g, double b, double a) {
            return ((Mth.floor(a * 255) & 0xFF) << 24) |
                        ((Mth.floor(r * 255) & 0xFF) << 16) |
                        ((Mth.floor(g * 255) & 0xFF) << 8)  |
                        ((Mth.floor(b * 255) & 0xFF));
      }

      private int setRGB(float r, float g, float b, float a) {
            return ((Mth.floor(a * 255) & 0xFF) << 24) |
                        ((Mth.floor(r * 255) & 0xFF) << 16) |
                        ((Mth.floor(g * 255) & 0xFF) << 8)  |
                        ((Mth.floor(b * 255) & 0xFF));
      }

      public int getRGBA() {
            return rgba;
      }

      public Tint setAlpha(byte a) {
            rgba = (~a << 16) ^ (rgba | (0xff << 16));
            return this;
      }

      public Tint setAlpha(float a) {
            rgba |= Mth.floor(a * 255) << 24;
            return this;
      }

      public double brightness() {
            double r = Math.pow(getRed() / 255.0, 2.2);
            double b = Math.pow(getBlue() / 255.0, 4);
            double g = Math.pow(getGreen() / 255.0, 2.2);

//
            //float y = (float) Math.cbrt(r * 0.3926 + g * 0.7152 + b * 0.0122);
            //r * 0.2126 + g * 0.7152 + b * 0.0722;
            double y = r * 0.2126 + g * 0.7152 + b * 0.0722;
            return y;
      }

      public class HSV {
            private final Tint hsv;
            private double hue;
            private double val;
            private double sat;

            private HSV(Tint hsv) {
                  this.hsv = hsv;
                  int color = this.hsv.rgba;

                  double r = ((color >> 16) & 0xff) / 255.0f;
                  double g = ((color >>  8) & 0xff) / 255.0f;
                  double b = ((color      ) & 0xff) / 255.0f;

                  double cmax = max(r, max(g, b)); // maximum of r, g, b
                  double cmin = min(r, min(g, b)); // minimum of r, g, b
                  double diff = cmax - cmin; // diff of cmax and cmin.
                  double h = -1, s;

                  // if cmax and cmax are equal then h = 0
                  if (cmax == cmin)
                        h = 0;

                        // if cmax equal r then compute h
                  else if (cmax == r)
                        h = (60 * ((g - b) / diff) + 360) % 360;

                        // if cmax equal g then compute h
                  else if (cmax == g)
                        h = (60 * ((b - r) / diff) + 120) % 360;

                        // if cmax equal b then compute h
                  else if (cmax == b)
                        h = (60 * ((r - g) / diff) + 240) % 360;

                  // if cmax equal zero
                  if (cmax == 0)
                        s = 0;
                  else
                        s = (diff / cmax);

                  hue = h;
                  sat = s;
                  val = cmax;
            }

            public double getHue() {
                  return hue;
            }

            public HSV setHue(double hue) {
                  this.hue = hue;
                  return this;
            }

            public HSV rotate(double degrees) {
                  hue = (degrees + hue) % 360;
                  return this;
            }

            public double getSat() {
                  return sat;
            }

            public HSV setSat(double sat) {
                  this.sat = Mth.clamp(sat, 0, 1);
                  return this;
            }

            public HSV scaleSat(double scale) {
                  this.sat *= Mth.clamp(scale, 0, 1);
                  return this;
            }

            public double getVal() {
                  return val;
            }

            public HSV setVal(double value) {
                  this.val = Mth.clamp(value, 0, 1);
                  return this;
            }

            public HSV scaleVal(double scale) {
                  this.val *= Mth.clamp(scale, 0, 1);
                  return this;
            }

            public int rgb() {
                  double h = hue / 360;
                  double s = sat;
                  double v = val;

                  int i = Mth.floor(h * 6);
                  double f = h * 6 - i;
                  double p = v * (1 - s);
                  double q = v * (1 - f * s);
                  double t = v * (1 - (1 - f) * s);

                  return switch (i) {
                        case 0 -> setRGB(v, t, p, 1);
                        case 1 -> setRGB(q, v, p, 1);
                        case 2 -> setRGB(p, v, t, 1);
                        case 3 -> setRGB(p, q, v, 1);
                        case 4 -> setRGB(t, p, v, 1);
                        case 5 -> setRGB(v, p, q, 1);
                        default -> throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + sat + ", " + val);
                  };
            }

            public void push() {
                  hsv.setRGB(rgb());
            }

            @Override
            public String toString() {
                  return "HSV:[" + hue + ", " + sat + ", " + val + ']';
            }
      }


      public class HSL {
            private final Tint tint;
            private double hue;
            private double lum;
            private double sat;

            private HSL(Tint tint) {
                  this.tint = tint;
                  int color = this.tint.rgba;

                  double r = ((color >> 16) & 0xff) / 255.0f;
                  double g = ((color >>  8) & 0xff) / 255.0f;
                  double b = ((color      ) & 0xff) / 255.0f;

                  // Thanks to Yuku on blogspot.com
                  double max = Math.max(Math.max(r, g), b);
                  double min = Math.min(Math.min(r, g), b);
                  double c = max - min;

                  double h_ = 0.f;
                  if (c == 0) {
                        h_ = 0;
                  } else if (max == r) {
                        h_ = (g-b) / c;
                        if (h_ < 0) h_ += 6.f;
                  } else if (max == g) {
                        h_ = (b-r) / c + 2.f;
                  } else if (max == b) {
                        h_ = (r-g) / c + 4.f;
                  }
                  double h = 60.f * h_;

                  double l = (max + min) * 0.5f;

                  double s;
                  if (c == 0) {
                        s = 0.f;
                  } else {
                        s = c / (1 - Math.abs(2.f * l - 1.f));
                  }

                  hue = h;
                  sat = s;
                  lum = l;
            }

            public double getHue() {
                  return hue;
            }

            public HSL setHue(double hue) {
                  this.hue = hue;
                  return this;
            }

            public HSL rotate(double degrees) {
                  hue = (degrees + hue) % 360;
                  return this;
            }

            public double getSat() {
                  return sat;
            }

            public HSL setSat(double sat) {
                  this.sat = Mth.clamp(sat, 0, 1);
                  return this;
            }

            public HSL scaleSat(double scale) {
                  this.sat *= Mth.clamp(scale, 0, 1);
                  return this;
            }

            public double getLum() {
                  return lum;
            }

            public HSL setLum(double value) {
                  this.lum = Mth.clamp(value, 0, 1);
                  return this;
            }

            public HSL scaleVal(double scale) {
                  this.lum *= Mth.clamp(scale, 0, 1);
                  return this;
            }

            public int rgb() {
                  double h = hue;
                  double s = sat;
                  double l = lum;

                  // Thanks to Yuku on blogspot.com
                  double c = (1 - Math.abs(2.f * l - 1.f)) * s;
                  double h_ = h / 60.f;
                  double h_mod2 = h_;
                  if (h_mod2 >= 4.f) h_mod2 -= 4.f;
                  else if (h_mod2 >= 2.f) h_mod2 -= 2.f;

                  double x = c * (1 - Math.abs(h_mod2 - 1));

                  // My Contribution
                  int i = Mth.floor(h_);
                  double m = l - (0.5 * c);
                  c += m;
                  x += m;

                  return switch (i) {
                        case 0 -> setRGB(c, x, m, 1);
                        case 1 -> setRGB(x, c, m, 1);
                        case 2 -> setRGB(m, c, x, 1);
                        case 3 -> setRGB(m, x, c, 1);
                        case 4 -> setRGB(x, m, c, 1);
                       default -> setRGB(c, m, x, 1);
                  };
            }

            public void push() {
                  tint.setRGB(rgb());
            }


            @Override
            public String toString() {
                  return "HSL:[" + hue + ", " + sat + ", " + lum + ']';
            }
      }


      private void setRGB(int rgba) {
            this.rgba = rgba;
      }

      public float[] getFloats() {
            float r = ((rgba >> 16) & 0xff) / 255.0f;
            float g = ((rgba >>  8) & 0xff) / 255.0f;
            float b = ( rgba        & 0xff) / 255.0f;
            float a = ((rgba >> 24) & 0xff) / 255.0f;

            return new float[]{r, g, b, a};
      }

      public int getRed() {
            return  (rgba >> 16) & 0xff;
      }

      public int getGreen() {
            return  (rgba >> 8) & 0xff;
      }

      public int getBlue() {
            return  rgba & 0xff;
      }

      public int getAlpha() {
            return  (rgba >> 24) & 0xff;
      }

      @Override
      public String toString() {
            return "RGBA:[" + getRed() + ", " + getBlue() + ", " + getGreen() + ", " + getAlpha() + ']';
      }
}
