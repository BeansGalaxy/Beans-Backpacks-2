package com.beansgalaxy.backpacks.data;

public class Viewable {
      public float headPitch = 0;
      public float lastPitch = 0;
      public float velocity = 0;
      public float lastDelta = 0;
      private byte viewers = 0;

      boolean isOpen() {
            return getViewers() > 0;
      }

      public void updateOpen() {
            float impulse = 22f;
            float resonance = 9f;
            float height = 18f;
            float closing = 11f;
            velocity = isOpen()
                        ? headPitch == 0
                        ? impulse
                        : (velocity + (lastPitch * resonance + height))
                        : velocity > 0
                        ? 0
                        : (velocity - 0.1f) * closing;

            float resistance = 0.3f;
            velocity *= resistance;
            float newPitch = headPitch - velocity * 0.1f;

            if (newPitch > 0)
                  newPitch = 0;

            //newPitch = -3f; // HOLDS TOP OPEN FOR TEXTURING
            lastPitch = headPitch;
            headPitch = newPitch;
      }

      public byte getViewers() {
            return viewers;
      }

      public void setViewers(byte viewers) {
            this.viewers = viewers;
      }
}
