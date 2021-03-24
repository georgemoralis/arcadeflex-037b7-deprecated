/*
 * ported to 0.37b7 
 */
package gr.codebb.arcadeflex.WIP.v037b7.mame;

import gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.rectangle;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;

public class artworkH {

    public static class artwork_info {

        /* Publically accessible */
        osd_bitmap artwork;
        osd_bitmap artwork1;
        osd_bitmap alpha;
        osd_bitmap orig_artwork;
        /* needed for palette recalcs */
        char[] /*UINT8*/ u8_orig_palette;
        /* needed for restoring the colors after special effects? */
        int num_pens_used;
        char[] /*UINT8*/ u8_transparency;
        int num_pens_trans;
        int start_pen;
        char[] /*UINT8*/ u8_brightness;
        /* brightness of each palette entry */
 /*TODO*///	UINT64 *rgb;
        char[] /*UINT8*/ u8_pTable;
        /* Conversion table usually used for mixing colors */
    }

    public static class artwork_element {

        rectangle box;
        int/*UINT8*/ red, green, blue;
        int/*UINT16*/ alpha;

        /* 0x00-0xff or OVERLAY_DEFAULT_OPACITY */
        public artwork_element(drawgfxH.rectangle box, int red, int green, int blue, int alpha) {
            this.box = box;
            this.red = red & 0xFF;
            this.green = green & 0xFF;
            this.blue = blue & 0xFF;
            this.alpha = alpha & 0xFF;
        }

    }
    /*TODO*///
/*TODO*///struct artwork_size_info
/*TODO*///{
/*TODO*///	int width, height;         /* widht and height of the artwork */
/*TODO*///	struct rectangle screen;   /* location of the screen relative to the artwork */
/*TODO*///};
/*TODO*///
    public static final int OVERLAY_DEFAULT_OPACITY = 0xffff;
    /*TODO*///
/*TODO*///  
}
