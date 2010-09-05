package glass.eclipse.theme.utils;

import java.awt.*;


public class CDockColorMap {
   public Color colFocusedGlassCenter;
   public Color colFocusedGlassBoundary;
   public Color colFocusedGlassLight;

   public Color colSelectedGlassCenter;
   public Color colSelectedGlassBoundary;
   public Color colSelectedGlassLight;

   public CDockColorMap (Color... colors) {
      if (colors == null || colors.length < 6) {
         initDefaultMap();
      }
      else {
         colFocusedGlassCenter = colors[0];
         colFocusedGlassBoundary = colors[1];
         colFocusedGlassLight = colors[2];
         colSelectedGlassCenter = colors[3];
         colSelectedGlassBoundary = colors[4];
         colSelectedGlassLight = colors[5];
      }
   }

   public CDockColorMap (Color colFocusedGlassCenter, Color colFocusedGlassBoundary, Color colFocusedGlassLight, Color colSelectedGlassCenter, Color colSelectedGlassBoundary, Color colSelectedGlassLight) {
      this.colFocusedGlassCenter = colFocusedGlassCenter;
      this.colFocusedGlassBoundary = colFocusedGlassBoundary;
      this.colFocusedGlassLight = colFocusedGlassLight;
      this.colSelectedGlassCenter = colSelectedGlassCenter;
      this.colSelectedGlassBoundary = colSelectedGlassBoundary;
      this.colSelectedGlassLight = colSelectedGlassLight;
   }

   public CDockColorMap () {
      initDefaultMap();
   }

   protected void initDefaultMap () {
      colSelectedGlassCenter = new Color(222, 222, 222);
      colFocusedGlassLight = new Color(222, 222, 222);
      colSelectedGlassBoundary = new Color(0, 40, 255);

      colFocusedGlassCenter = new Color(0, 80, 150);
      colFocusedGlassLight = new Color(150, 222, 252);
      colFocusedGlassBoundary = new Color(0, 40, 80);
   }
}
