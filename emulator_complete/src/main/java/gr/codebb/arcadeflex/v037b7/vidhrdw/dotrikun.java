/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.plot_pixel;

public class dotrikun {

    /**
     * *****************************************************************
     *
     * Palette Setting.
     *
     ******************************************************************
     */
    public static WriteHandlerPtr dotrikun_color_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int r, g, b;

            r = ((data & 0x08) != 0 ? 0xff : 0x00);
            g = ((data & 0x10) != 0 ? 0xff : 0x00);
            b = ((data & 0x20) != 0 ? 0xff : 0x00);
            palette_change_color(0, r, g, b);		// BG color

            r = ((data & 0x01) != 0 ? 0xff : 0x00);
            g = ((data & 0x02) != 0 ? 0xff : 0x00);
            b = ((data & 0x04) != 0 ? 0xff : 0x00);
            palette_change_color(1, r, g, b);		// DOT color
        }
    };

    /**
     * *****************************************************************
     *
     * Draw Pixel.
     *
     ******************************************************************
     */
    public static WriteHandlerPtr dotrikun_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int i;
            int x, y;
            int color;

            videoram.write(offset, data);

            x = 2 * (((offset % 16) * 8));
            y = 2 * ((offset / 16));

            if (x >= Machine.visible_area.min_x
                    && x <= Machine.visible_area.max_x
                    && y >= Machine.visible_area.min_y
                    && y <= Machine.visible_area.max_y) {
                for (i = 0; i < 8; i++) {
                    color = Machine.pens[((data >> i) & 0x01)];

                    /* I think the video hardware doubles pixels, screen would be too small otherwise */
                    plot_pixel.handler(Machine.scrbitmap, x + 2 * (7 - i), y, color);
                    plot_pixel.handler(Machine.scrbitmap, x + 2 * (7 - i) + 1, y, color);
                    plot_pixel.handler(Machine.scrbitmap, x + 2 * (7 - i), y + 1, color);
                    plot_pixel.handler(Machine.scrbitmap, x + 2 * (7 - i) + 1, y + 1, color);
                }
            }
        }
    };

    public static VhUpdatePtr dotrikun_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            if (palette_recalc() != null || full_refresh != 0) {
                int offs;

                /* redraw bitmap */
                for (offs = 0; offs < videoram_size[0]; offs++) {
                    dotrikun_videoram_w.handler(offs, videoram.read(offs));
                }
            }
        }
    };
}
