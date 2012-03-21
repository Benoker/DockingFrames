package glass.eclipse.theme.utils;

import java.awt.*;


public class CDockColorMap {
   public Color colFocusedGlassCenter;
   public Color colFocusedGlassBoundary;
   public Color colFocusedGlassLight;

   public Color colSelectedGlassCenter;
   public Color colSelectedGlassBoundary;
   public Color colSelectedGlassLight;

   public Color colUnSelectedGlassCenter;
   public Color colUnSelectedGlassBoundary;
   public Color colUnSelectedGlassLight;
   
   public Color colDisabledGlassCenter;
   public Color colDisabledGlassBoundary;
   public Color colDisabledGlassLight;

/* Rather strange constructors... if someone was using them and now is reading this text: 
 * use the default constructor and re-assign the fields you really need. 
 * Beni */ 
   
//   public CDockColorMap (Color... colors) {
//      if (colors == null || colors.length < 9) {
//         initDefaultMap();
//      }
//      else {
//         colFocusedGlassCenter = colors[0];
//         colFocusedGlassBoundary = colors[1];
//         colFocusedGlassLight = colors[2];
//         colSelectedGlassCenter = colors[3];
//         colSelectedGlassBoundary = colors[4];
//         colSelectedGlassLight = colors[5];
//         colUnSelectedGlassCenter = colors[6];
//         colUnSelectedGlassBoundary = colors[7];
//         colUnSelectedGlassLight = colors[8];
//      }
//   }
//
//   public CDockColorMap (Color colFocusedGlassCenter, Color colFocusedGlassBoundary, Color colFocusedGlassLight, Color colSelectedGlassCenter, Color colSelectedGlassBoundary, Color colSelectedGlassLight, Color colUnSelectedGlassCenter, Color colUnSelectedGlassBoundary, Color colUnSelectedGlassLight) {
//      this.colFocusedGlassCenter = colFocusedGlassCenter;
//      this.colFocusedGlassBoundary = colFocusedGlassBoundary;
//      this.colFocusedGlassLight = colFocusedGlassLight;
//      this.colSelectedGlassCenter = colSelectedGlassCenter;
//      this.colSelectedGlassBoundary = colSelectedGlassBoundary;
//      this.colSelectedGlassLight = colSelectedGlassLight;
//      this.colUnSelectedGlassCenter = colUnSelectedGlassCenter;
//      this.colUnSelectedGlassBoundary = colUnSelectedGlassBoundary;
//      this.colUnSelectedGlassLight = colUnSelectedGlassLight;
//   }

   public CDockColorMap () {
      initDefaultMap();
   }

   protected void initDefaultMap () {
      colSelectedGlassCenter = new Color(222, 222, 222);
      colSelectedGlassLight = new Color(222, 222, 222);
      colSelectedGlassBoundary = new Color(0, 40, 255);

      colFocusedGlassCenter = new Color(0, 80, 150);
      colFocusedGlassLight = new Color(150, 222, 252);
      colFocusedGlassBoundary = new Color(0, 40, 80);

      colUnSelectedGlassCenter = new Color(222, 222, 222, 64);
      colUnSelectedGlassLight = new Color(222, 222, 222, 64);
      colUnSelectedGlassBoundary = new Color(128, 128, 128, 64);
      
      colDisabledGlassCenter = new Color(200, 200, 200);
      colDisabledGlassLight = new Color(200, 200, 200);
      colDisabledGlassBoundary = new Color(150, 150, 150);
   }
}
