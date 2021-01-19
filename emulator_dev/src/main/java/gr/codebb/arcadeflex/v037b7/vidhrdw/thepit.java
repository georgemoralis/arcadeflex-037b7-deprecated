/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.SubArrays.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.copyscrollbitmap;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.old.sound.mixer.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.v037b7.vidhrdw.galaxian.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class thepit {

    static int[] graphics_bank = new int[1];

    static rectangle spritevisiblearea = new rectangle(
            2 * 8 + 1, 32 * 8 - 1,
            2 * 8, 30 * 8 - 1
    );
    static rectangle spritevisibleareaflipx = new rectangle(
            0 * 8, 30 * 8 - 2,
            2 * 8, 30 * 8 - 1
    );

    /**
     * *************************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     * bit 7 -- 220 ohm resistor -- BLUE -- 470 ohm resistor -- BLUE -- 220 ohm
     * resistor -- GREEN -- 470 ohm resistor -- GREEN -- 1 kohm resistor --
     * GREEN -- 220 ohm resistor -- RED -- 470 ohm resistor -- RED bit 0 -- 1
     * kohm resistor -- RED
     *
     *
     **************************************************************************
     */
    public static VhConvertColorPromPtr thepit_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            int p_inc = 0;
            /* first of all, allocate primary colors for the background and foreground */
 /* this is wrong, but I don't know where to pick the colors from */
            for (i = 0; i < 8; i++) {
                palette[p_inc] = (char) (0xff * ((i >> 2) & 1));
                palette[p_inc] = (char) (0xff * ((i >> 1) & 1));
                palette[p_inc] = (char) (0xff * ((i >> 0) & 1));
            }

            for (i = 0; i < Machine.drv.total_colors - 8; i++) {
                int bit0, bit1, bit2;

                bit0 = (color_prom.read(i) >> 0) & 0x01;
                bit1 = (color_prom.read(i) >> 1) & 0x01;
                bit2 = (color_prom.read(i) >> 2) & 0x01;
                palette[p_inc] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                bit0 = (color_prom.read(i) >> 3) & 0x01;
                bit1 = (color_prom.read(i) >> 4) & 0x01;
                bit2 = (color_prom.read(i) >> 5) & 0x01;
                palette[p_inc] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                bit0 = 0;
                bit1 = (color_prom.read(i) >> 6) & 0x01;
                bit2 = (color_prom.read(i) >> 7) & 0x01;
                palette[p_inc] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
            }

            for (i = 0; i < Machine.drv.total_colors - 8; i++) {
                colortable[i] = (char) (i + 8);
            }
        }
    };

    public static WriteHandlerPtr intrepid_graphics_bank_select_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            set_vh_global_attribute(graphics_bank, data << 1);
        }
    };

    public static ReadHandlerPtr thepit_input_port_0_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            /* Read either the real or the fake input ports depending on the
		   horizontal flip switch. (This is how the real PCB does it) */
            if (flip_screen_x[0] != 0) {
                return input_port_3_r.handler(offset);
            } else {
                return input_port_0_r.handler(offset);
            }
        }
    };

    public static WriteHandlerPtr thepit_sound_enable_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            mixer_sound_enable_global_w(data);
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
    static void drawtiles(osd_bitmap bitmap, int priority) {
        int offs, spacechar = 0;

        if (priority == 1) {
            /* find the space character */
            while ((Machine.gfx[0].pen_usage[spacechar] & ~1) != 0) {
                spacechar++;
            }
        }

        /* for every character in the Video RAM, check if it has been modified */
 /* since last time and update it accordingly. */
        for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
            int bgcolor;

            bgcolor = (colorram.read(offs) & 0x70) >> 4;

            if ((priority == 0 && dirtybuffer[offs] != 0)
                    || (priority == 1 && bgcolor != 0 && (colorram.read(offs) & 0x80) == 0)) {
                int sx, sy, code, bank, color;

                dirtybuffer[offs] = 0;

                sx = (offs % 32);
                sy = 8 * (offs / 32);

                if (priority == 0) {
                    code = videoram.read(offs);
                    bank = graphics_bank[0];
                } else {
                    code = spacechar;
                    bank = 0;

                    sy = (sy - galaxian_attributesram.read(2 * sx)) & 0xff;
                }

                if (flip_screen_x[0] != 0) {
                    sx = 31 - sx;
                }
                if (flip_screen_y[0] != 0) {
                    sy = 248 - sy;
                }

                color = colorram.read(offs) & 0x07;

                /* set up the background color */
                Machine.gfx[bank].colortable.write(color * Machine.gfx[graphics_bank[0]].color_granularity,
                        Machine.pens[bgcolor]);

                drawgfx(priority == 0 ? tmpbitmap : bitmap, Machine.gfx[bank],
                        code,
                        color,
                        flip_screen_x[0], flip_screen_y[0],
                        8 * sx, sy,
                        null, TRANSPARENCY_NONE, 0);
            }
        }

        /* copy the temporary bitmap to the screen */
        if (priority == 0) {
            int i;
            int[] scroll = new int[32];

            if (flip_screen_x[0] != 0) {
                for (i = 0; i < 32; i++) {
                    scroll[31 - i] = -galaxian_attributesram.read(2 * i);
                    if (flip_screen_y[0] != 0) {
                        scroll[31 - i] = -scroll[31 - i];
                    }
                }
            } else {
                for (i = 0; i < 32; i++) {
                    scroll[i] = -galaxian_attributesram.read(2 * i);
                    if (flip_screen_y[0] != 0) {
                        scroll[i] = -scroll[i];
                    }
                }
            }

            copyscrollbitmap(bitmap, tmpbitmap, 0, null, 32, scroll, Machine.visible_area, TRANSPARENCY_NONE, 0);
        }
    }

    static void drawsprites(osd_bitmap bitmap, int priority) {
        int offs;

        /* draw low priority sprites */
        for (offs = spriteram_size[0] - 4; offs >= 0; offs -= 4) {
            if (((spriteram.read(offs + 2) & 0x08) >> 3) == priority) {
                int sx, sy, flipx, flipy;

                if (spriteram.read(offs + 0) == 0
                        || spriteram.read(offs + 3) == 0) {
                    continue;
                }

                sx = (spriteram.read(offs + 3) + 1) & 0xff;
                sy = 240 - spriteram.read(offs);

                flipx = spriteram.read(offs + 1) & 0x40;
                flipy = spriteram.read(offs + 1) & 0x80;

                if (flip_screen_x[0] != 0) {
                    sx = 242 - sx;
                    flipx = NOT(flipx);
                }

                if (flip_screen_y[0] != 0) {
                    sy = 240 - sy;
                    flipy = NOT(flipy);
                }

                /* Sprites 0-3 are drawn one pixel to the left */
                if (offs <= 3 * 4) {
                    sy++;
                }

                drawgfx(bitmap, Machine.gfx[graphics_bank[0] | 1],
                        spriteram.read(offs + 1) & 0x3f,
                        spriteram.read(offs + 2) & 0x07,
                        flipx, flipy,
                        sx, sy,
                        (flip_screen_x[0] & 1) != 0 ? spritevisibleareaflipx : spritevisiblearea, TRANSPARENCY_PEN, 0);
            }
        }
    }

    public static VhUpdatePtr thepit_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            if (full_refresh != 0) {
                memset(dirtybuffer, 1, videoram_size[0]);
            }

            /* low priority tiles */
            drawtiles(bitmap, 0);

            /* low priority sprites */
            drawsprites(bitmap, 0);

            /* high priority tiles */
            drawtiles(bitmap, 1);

            /* high priority sprites */
            drawsprites(bitmap, 1);
        }
    };
}
