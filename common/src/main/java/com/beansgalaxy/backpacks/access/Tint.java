package com.beansgalaxy.backpacks.access;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
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

      private int setRGB(int r, int g, int b) {
            return ((r & 0xFF) << 16) |
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

      public static ClampedItemPropertyFunction COLOR_PREDICATE = (itemStack, clientLevel, livingEntity, i) -> {
            CompoundTag display = itemStack.getTagElement("display");
            if (display == null || !display.contains("color"))
                  return 0;

            int color = display.getInt("color");
            return isYellow(color) ? 1
                 : isGreen(color) ? 2
                 : 0;
      };

      public static boolean isYellow(int color) {
            Tint.HSL hsl1 = new Tint(color).HSL();
            double hue = hsl1.getHue();
            double lum = hsl1.getLum();
            return hue > 40 && hue < 70 && lum > .20 && lum < .90 && hsl1.getSat() > .15;
      }

      public static boolean isGreen(int color) {
            Tint.HSL hsl1 = new Tint(color).HSL();
            double hue = hsl1.getHue();
            double lum = hsl1.getLum();
            return hue > 69 && hue < 140 && lum > .30 && lum < .90 && hsl1.getSat() > .15;
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

      public class LCH {
            private double l = 0; // Brightness
            private double a = 0; // Red - Green
            private double b = 0; // Blue - Yellow

            double linear(int x)
            {
                  if (x >= 0.0031308)
                        return ((1.055) * Math.pow(x , 1.0/2.4) - 0.055);
                  else
                        return 12.92 * x;
            }

            double non_linear(double x)
            {
                  if (x >= 0.04045)
                        return Math.pow((x + 0.055)/(1 + 0.055), 2.4);
                  else
                        return x / 12.92f;
            }

            private LCH() {
                  double r = linear((rgba >> 16) & 0xff);
                  double g = linear((rgba >>  8) & 0xff);
                  double b = linear( rgba        & 0xff);

                  double l = 0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b;
                  double m = 0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b;
                  double s = 0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b;

                  double l_ = Math.cbrt(l);
                  double m_ = Math.cbrt(m);
                  double s_ = Math.cbrt(s);

                  this.l = 0.2104542553f*l_ + 0.7936177850f*m_ - 0.0040720468f*s_;
                  this.a = 1.9779984951f*l_ - 2.4285922050f*m_ + 0.4505937099f*s_;
                  this.b = 0.0259040371f*l_ + 0.7827717662f*m_ - 0.8086757660f*s_;
            }

            public int rgb() {
                  double l_ = l + 0.3963377774f * a + 0.2158037573f * b;
                  double m_ = l - 0.1055613458f * a - 0.0638541728f * b;
                  double s_ = l - 0.0894841775f * a - 1.2914855480f * b;

                  double l = l_*l_*l_;
                  double m = m_*m_*m_;
                  double s = s_*s_*s_;

                  int r = (int) (non_linear(+4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s) * 255);
                  int g = (int) (non_linear(-1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s) * 255);
                  int b = (int) (non_linear(-0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s) * 255);

                  return setRGB(r, g, b);
            }

            public void push() {
                  rgba = rgb();
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
