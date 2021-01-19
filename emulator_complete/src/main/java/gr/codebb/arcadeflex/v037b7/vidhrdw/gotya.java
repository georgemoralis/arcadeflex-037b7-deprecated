/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.flip_screen;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class gotya {

    public static UBytePtr gotya_scroll = new UBytePtr();
    public static UBytePtr gotya_foregroundram = new UBytePtr();

    static int scroll_bit_8;

    /**
     * *************************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     * I'm using Pac Man resistor values
     *
     **************************************************************************
     */
    static int TOTAL_COLORS(int gfxn) {
        return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
    }
    public static VhConvertColorPromPtr gotya_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            //#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
            //#define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])

            int p_inc = 0;
            for (i = 0; i < Machine.drv.total_colors; i++) {
                int bit0, bit1, bit2;

                /* red component */
                bit0 = (color_prom.read() >> 0) & 0x01;
                bit1 = (color_prom.read() >> 1) & 0x01;
                bit2 = (color_prom.read() >> 2) & 0x01;
                palette[p_inc++] = ((char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2));
                /* green component */
                bit0 = (color_prom.read() >> 3) & 0x01;
                bit1 = (color_prom.read() >> 4) & 0x01;
                bit2 = (color_prom.read() >> 5) & 0x01;
                palette[p_inc++] = ((char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2));
                /* blue component */
                bit0 = 0;
                bit1 = (color_prom.read() >> 6) & 0x01;
                bit2 = (color_prom.read() >> 7) & 0x01;
                palette[p_inc++] = ((char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2));

                color_prom.inc();
            }

            color_prom.inc(0x18);
            /* color_prom now points to the beginning of the lookup table */

 /* character lookup table */
 /* sprites use the same color lookup table as characters */
            for (i = 0; i < TOTAL_COLORS(0); i++) {
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char) ((color_prom.readinc()) & 0x07);
            }
        }
    };

    public static WriteHandlerPtr gotya_video_control_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bit 0 - scroll bit 8
		   bit 1 - flip screen
		   bit 2 - sound disable ??? */

            scroll_bit_8 = data & 0x01;

            flip_screen_w.handler(offset, data & 0x02);
        }
    };

    public static VhStartPtr gotya_vh_start = new VhStartPtr() {
        public int handler() {
            if ((dirtybuffer = new char[videoram_size[0]]) == null) {
                return 1;
            }

            /* the background area is twice as wide as the screen (actually twice as tall, */
 /* because this is a vertical game) */
            if ((tmpbitmap = bitmap_alloc(2 * 256, Machine.drv.screen_height)) == null) {
                dirtybuffer = null;
                return 1;
            }

            return 0;
        }
    };

    static void draw_status_row(osd_bitmap bitmap, int sx, int col) {
        int row;

        if (flip_screen() != 0) {
            sx = 35 - sx;
        }

        for (row = 29; row >= 0; row--) {
            int sy;

            if (flip_screen() != 0) {
                sy = row;
            } else {
                sy = 31 - row;
            }

            drawgfx(bitmap, Machine.gfx[0],
                    gotya_foregroundram.read(row * 32 + col),
                    gotya_foregroundram.read(row * 32 + col + 0x10) & 0x0f,
                    flip_screen(), flip_screen(),
                    8 * sx, 8 * sy,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
        }
    }

    public static VhUpdatePtr gotya_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            if (full_refresh != 0) {
                memset(dirtybuffer, 1, videoram_size[0]);
            }

            /* for every character in the Video RAM, check if it has been modified */
 /* since last time and update it accordingly. */
            for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
                if (dirtybuffer[offs] != 0) {
                    int sx, sy;

                    dirtybuffer[offs] = 0;

                    sx = 31 - (offs % 32);
                    sy = 31 - ((offs & 0x03ff) / 32);

                    if (flip_screen() != 0) {
                        sx = 31 - sx;
                        sy = 31 - sy;
                    }

                    if (offs < 0x0400) {
                        sx = sx + 32;
                    }

                    drawgfx(tmpbitmap, Machine.gfx[0],
                            videoram.read(offs),
                            colorram.read(offs) & 0x0f,
                            flip_screen(), flip_screen(),
                            8 * sx, 8 * sy,
                            null, TRANSPARENCY_NONE, 0);
                }
            }

            /* copy the background graphics */
            {
                int scroll;

                scroll = gotya_scroll.read() + (scroll_bit_8 * 256) + 16;

                copyscrollbitmap(bitmap, tmpbitmap, 1, new int[]{scroll}, 0, null, Machine.visible_area, TRANSPARENCY_NONE, 0);
            }

            /* draw the sprites */
            for (offs = 2; offs < 0x0e; offs += 2) {
                int code, col, sx, sy;

                code = spriteram.read(offs + 0x01) >> 2;
                col = spriteram.read(offs + 0x11) & 0x0f;
                sx = 256 - spriteram.read(offs + 0x10) + (spriteram.read(offs + 0x01) & 0x01) * 256;
                sy = spriteram.read(offs + 0x00);

                if (flip_screen() != 0) {
                    sy = 240 - sy;
                }

                drawgfx(bitmap, Machine.gfx[1],
                        code, col,
                        flip_screen(), flip_screen(),
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }

            /* draw the status lines */
            draw_status_row(bitmap, 0, 1);
            draw_status_row(bitmap, 1, 0);
            draw_status_row(bitmap, 2, 2);
            /* these two are blank, but I dont' know if the data comes */
            draw_status_row(bitmap, 33, 13);
            /* from RAM or 'hardcoded' into the hardware. Likely the latter */
            draw_status_row(bitmap, 35, 14);
            draw_status_row(bitmap, 34, 15);
        }
    };
}
