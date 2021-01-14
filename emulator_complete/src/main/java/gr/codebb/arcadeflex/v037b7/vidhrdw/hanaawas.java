/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.mame.osdependH.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.old2.mame.common.*;
import static gr.codebb.arcadeflex.old2.mame.mame.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.commonH.flip_screen;
import static gr.codebb.arcadeflex.re.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;

import gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class hanaawas {

    /**
     * *************************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     **************************************************************************
     */
    static int TOTAL_COLORS(int gfxn) {
        return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
    }

    public static VhConvertColorPromPtr hanaawas_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            int p_ptr = 0;
            for (i = 0; i < Machine.drv.total_colors; i++) {
                int bit0, bit1, bit2;

                /* red component */
                bit0 = (color_prom.read() >> 0) & 0x01;
                bit1 = (color_prom.read() >> 1) & 0x01;
                bit2 = (color_prom.read() >> 2) & 0x01;
                palette[p_ptr++] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                /* green component */
                bit0 = (color_prom.read() >> 3) & 0x01;
                bit1 = (color_prom.read() >> 4) & 0x01;
                bit2 = (color_prom.read() >> 5) & 0x01;
                palette[p_ptr++] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                /* blue component */
                bit0 = 0;
                bit1 = (color_prom.read() >> 6) & 0x01;
                bit2 = (color_prom.read() >> 7) & 0x01;
                palette[p_ptr++] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);

                color_prom.inc();
            }

            color_prom.inc(0x10);
            /* color_prom now points to the beginning of the lookup table */

 /* character lookup table.  The 1bpp tiles really only use colors 0-0x0f and the
		   3bpp ones 0x10-0x1f */
            for (i = 0; i < TOTAL_COLORS(0) / 8; i++) {
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (i * 8 + 0)] = (char) (color_prom.read(i * 4 + 0x00) & 0x0f);
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (i * 8 + 1)] = (char) (color_prom.read(i * 4 + 0x01) & 0x0f);
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (i * 8 + 2)] = (char) (color_prom.read(i * 4 + 0x02) & 0x0f);
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (i * 8 + 3)] = (char) (color_prom.read(i * 4 + 0x03) & 0x0f);
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (i * 8 + 4)] = (char) (color_prom.read(i * 4 + 0x80) & 0x0f);
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (i * 8 + 5)] = (char) (color_prom.read(i * 4 + 0x81) & 0x0f);
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (i * 8 + 6)] = (char) (color_prom.read(i * 4 + 0x82) & 0x0f);
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (i * 8 + 7)] = (char) (color_prom.read(i * 4 + 0x83) & 0x0f);
            }
        }
    };

    public static WriteHandlerPtr hanaawas_colorram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int offs2;

            colorram.write(offset, data);

            /* dirty both current and next offsets */
            offs2 = (offset + (flip_screen() != 0 ? -1 : 1)) & 0x03ff;

            dirtybuffer[offset] = 1;
            dirtybuffer[offs2] = 1;
        }
    };

    public static WriteHandlerPtr hanaawas_portB_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bit 7 is flip screen */
            flip_screen_w.handler(offset, ~data & 0x80);
        }
    };

    /**
     * *************************************************************************
     *
     * Draw the game screen in the given osd_bitmap. Do NOT call
     * osd_update_display() from this function, it will be called by the main
     * emulation engine.
     *
     **************************************************************************
     */
    public static VhUpdatePtr hanaawas_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs, offs_adj;

            if (full_refresh != 0) {
                memset(dirtybuffer, 1, videoram_size[0]);
            }

            offs_adj = flip_screen() != 0 ? 1 : -1;

            for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
                if (dirtybuffer[offs] != 0) {
                    int sx, sy, col, code, bank, offs2;

                    dirtybuffer[offs] = 0;
                    sx = offs % 32;
                    sy = offs / 32;

                    if (flip_screen() != 0) {
                        sx = 31 - sx;
                        sy = 31 - sy;
                    }

                    /* the color is determined by the current color byte, but the bank is via the
				   previous one!!! */
                    offs2 = (offs + offs_adj) & 0x03ff;

                    col = colorram.read(offs) & 0x1f;
                    code = videoram.read(offs) + ((colorram.read(offs2) & 0x20) << 3);
                    bank = (colorram.read(offs2) & 0x40) >> 6;

                    drawgfx(bitmap, Machine.gfx[bank],
                            code, col,
                            flip_screen(), flip_screen(),
                            sx * 8, sy * 8,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                }
            }
        }
    };
}
