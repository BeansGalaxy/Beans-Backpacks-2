package com.beansgalaxy.backpacks.screen;

import org.joml.Vector2d;

import javax.annotation.Nullable;

import static java.lang.Math.*;

public class SecondOrderDynamics {
      private double xp;
      private double y, yd;
      private double k1, k2, k3;

      public SecondOrderDynamics(double f, double z, double r, double xo)
      {
            k1 = z / (PI * f);
            k2 = 1 / pow(2 * PI * f, 2);
            k3 = r * z / (2 * PI * f);

            xp = xo;
            y = xo;
            yd = 0;
      }

      public double update(double T, double x) {
            double xd = (x - xp) /T;
            xp = x;

            return update(T, x, xd);
      }

      public double update(double T, double x, double xd) {
            double k2_stable = max(k2, 1.1 * (T * T/4 + T * k1/2));
            y = y + T * yd;
            yd = yd + T * (x + k3*xd - y - k1 * yd) / k2_stable;
            return y;
      }
}
