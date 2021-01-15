/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.old2.mame.common.*;
import static gr.codebb.arcadeflex.old2.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;

public class marineb {

    public static UBytePtr marineb_column_scroll = new UBytePtr();
    public static int marineb_active_low_flipscreen;
    static int[] palbank = new int[1];

    public static WriteHandlerPtr marineb_palbank0_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int new_palbank = (palbank[0] & ~1) | (data & 1);
            set_vh_global_attribute(palbank, new_palbank);
        }
    };

    public static WriteHandlerPtr marineb_palbank1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int new_palbank = (palbank[0] & ~2) | ((data << 1) & 2);
            set_vh_global_attribute(palbank, new_palbank);
        }
    };

    public static WriteHandlerPtr marineb_flipscreen_x_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            flip_screen_x_w.handler(offset, data ^ marineb_active_low_flipscreen);
        }
    };

    public static WriteHandlerPtr marineb_flipscreen_y_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            flip_screen_y_w.handler(offset, data ^ marineb_active_low_flipscreen);
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
    static void draw_chars(osd_bitmap _tmpbitmap, osd_bitmap bitmap,
            int scroll_cols, int full_refresh) {
        int offs;

        if (full_refresh != 0) {
            memset(dirtybuffer, 1, videoram_size[0]);
        }

        /* for every character in the Video RAM, check if it has been modified */
 /* since last time and update it accordingly. */
        for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
            if (dirtybuffer[offs] != 0) {
                int sx, sy, flipx, flipy;

                dirtybuffer[offs] = 0;

                sx = offs % 32;
                sy = offs / 32;

                flipx = colorram.read(offs) & 0x20;
                flipy = colorram.read(offs) & 0x10;

                if (flip_screen_y[0] != 0) {
                    sy = 31 - sy;
                    flipy = NOT(flipy);
                }

                if (flip_screen_x[0] != 0) {
                    sx = 31 - sx;
                    flipx = NOT(flipx);
                }

                drawgfx(_tmpbitmap, Machine.gfx[0],
                        videoram.read(offs) | ((colorram.read(offs) & 0xc0) << 2),
                        (colorram.read(offs) & 0x0f) + 16 * palbank[0],
                        flipx, flipy,
                        8 * sx, 8 * sy,
                        null, TRANSPARENCY_NONE, 0);
            }
        }

        /* copy the temporary bitmap to the screen */
        {
            int[] scroll = new int[32];

            if (flip_screen_y[0] != 0) {
                for (offs = 0; offs < 32 - scroll_cols; offs++) {
                    scroll[offs] = 0;
                }

                for (; offs < 32; offs++) {
                    scroll[offs] = marineb_column_scroll.read(0);
                }
            } else {
                for (offs = 0; offs < scroll_cols; offs++) {
                    scroll[offs] = -marineb_column_scroll.read(0);
                }

                for (; offs < 32; offs++) {
                    scroll[offs] = 0;
                }
            }
            copyscrollbitmap(bitmap, tmpbitmap, 0, null, 32, scroll, Machine.visible_area, TRANSPARENCY_NONE, 0);
        }
    }

    public static VhUpdatePtr marineb_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            draw_chars(tmpbitmap, bitmap, 24, full_refresh);

            /* draw the sprites */
            for (offs = 0x0f; offs >= 0; offs--) {
                int gfx, sx, sy, code, col, flipx, flipy, offs2;

                if ((offs == 0) || (offs == 2)) {
                    continue;
                    /* no sprites here */
                }

                if (offs < 8) {
                    offs2 = 0x0018 + offs;
                } else {
                    offs2 = 0x03d8 - 8 + offs;
                }

                code = videoram.read(offs2);
                sx = videoram.read(offs2 + 0x20);
                sy = colorram.read(offs2);
                col = (colorram.read(offs2 + 0x20) & 0x0f) + 16 * palbank[0];
                flipx = code & 0x02;
                flipy = NOT(code & 0x01);

                if (offs < 4) {
                    /* big sprite */
                    gfx = 2;
                    code = (code >> 4) | ((code & 0x0c) << 2);
                } else {
                    /* small sprite */
                    gfx = 1;
                    code >>= 2;
                }

                if (flip_screen_y[0] == 0) {
                    sy = 256 - Machine.gfx[gfx].width - sy;
                    flipy = NOT(flipy);
                }

                if (flip_screen_x[0] != 0) {
                    sx++;
                }

                drawgfx(bitmap, Machine.gfx[gfx],
                        code,
                        col,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }
        }
    };

    public static VhUpdatePtr changes_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs, sx, sy, code, col, flipx, flipy;

            draw_chars(tmpbitmap, bitmap, 26, full_refresh);

            /* draw the small sprites */
            for (offs = 0x05; offs >= 0; offs--) {
                int offs2;

                offs2 = 0x001a + offs;

                code = videoram.read(offs2);
                sx = videoram.read(offs2 + 0x20);
                sy = colorram.read(offs2);
                col = (colorram.read(offs2 + 0x20) & 0x0f) + 16 * palbank[0];
                flipx = code & 0x02;
                flipy = NOT(code & 0x01);

                if (flip_screen_y[0] == 0) {
                    sy = 256 - Machine.gfx[1].width - sy;
                    flipy = NOT(flipy);
                }

                if (flip_screen_x[0] != 0) {
                    sx++;
                }

                drawgfx(bitmap, Machine.gfx[1],
                        code >> 2,
                        col,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }

            /* draw the big sprite */
            code = videoram.read(0x3df);
            sx = videoram.read(0x3ff);
            sy = colorram.read(0x3df);
            col = colorram.read(0x3ff);
            flipx = code & 0x02;
            flipy = NOT(code & 0x01);

            if (flip_screen_y[0] == 0) {
                sy = 256 - Machine.gfx[2].width - sy;
                flipy = NOT(flipy);
            }

            if (flip_screen_x[0] != 0) {
                sx++;
            }

            code >>= 4;

            drawgfx(bitmap, Machine.gfx[2],
                    code,
                    col,
                    flipx, flipy,
                    sx, sy,
                    Machine.visible_area, TRANSPARENCY_PEN, 0);

            /* draw again for wrap around */
            drawgfx(bitmap, Machine.gfx[2],
                    code,
                    col,
                    flipx, flipy,
                    sx - 256, sy,
                    Machine.visible_area, TRANSPARENCY_PEN, 0);
        }
    };

    public static VhUpdatePtr springer_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            draw_chars(tmpbitmap, bitmap, 0, full_refresh);

            /* draw the sprites */
            for (offs = 0x0f; offs >= 0; offs--) {
                int gfx, sx, sy, code, col, flipx, flipy, offs2;

                if ((offs == 0) || (offs == 2)) {
                    continue;
                    /* no sprites here */
                }

                offs2 = 0x0010 + offs;

                code = videoram.read(offs2);
                sx = 240 - videoram.read(offs2 + 0x20);
                sy = colorram.read(offs2);
                col = (colorram.read(offs2 + 0x20) & 0x0f) + 16 * palbank[0];
                flipx = NOT(code & 0x02);
                flipy = NOT(code & 0x01);

                if (offs < 4) {
                    /* big sprite */
                    sx -= 0x10;
                    gfx = 2;
                    code = (code >> 4) | ((code & 0x0c) << 2);
                } else {
                    /* small sprite */
                    gfx = 1;
                    code >>= 2;
                }

                if (flip_screen_y[0] == 0) {
                    sy = 256 - Machine.gfx[gfx].width - sy;
                    flipy = NOT(flipy);
                }

                if (flip_screen_x[0] == 0) {
                    sx--;
                }

                drawgfx(bitmap, Machine.gfx[gfx],
                        code,
                        col,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }
        }
    };

    public static VhUpdatePtr hoccer_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            draw_chars(tmpbitmap, bitmap, 0, full_refresh);

            /* draw the sprites */
            for (offs = 0x07; offs >= 0; offs--) {
                int sx, sy, code, col, flipx, flipy, offs2;

                offs2 = 0x0018 + offs;

                code = spriteram.read(offs2);
                sx = spriteram.read(offs2 + 0x20);
                sy = colorram.read(offs2);
                col = colorram.read(offs2 + 0x20);
                flipx = code & 0x02;
                flipy = NOT(code & 0x01);

                if (flip_screen_y[0] == 0) {
                    sy = 256 - Machine.gfx[1].width - sy;
                    flipy = NOT(flipy);
                }

                if (flip_screen_x[0] != 0) {
                    sx = 256 - Machine.gfx[1].width - sx;
                    flipx = NOT(flipx);
                }

                drawgfx(bitmap, Machine.gfx[1],
                        code >> 2,
                        col,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }
        }
    };

    public static VhUpdatePtr hopprobo_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            draw_chars(tmpbitmap, bitmap, 0, full_refresh);

            /* draw the sprites */
            for (offs = 0x0f; offs >= 0; offs--) {
                int gfx, sx, sy, code, col, flipx, flipy, offs2;

                if ((offs == 0) || (offs == 2)) {
                    continue;
                    /* no sprites here */
                }

                offs2 = 0x0010 + offs;

                code = videoram.read(offs2);
                sx = videoram.read(offs2 + 0x20);
                sy = colorram.read(offs2);
                col = (colorram.read(offs2 + 0x20) & 0x0f) + 16 * palbank[0];
                flipx = code & 0x02;
                flipy = NOT(code & 0x01);

                if (offs < 4) {
                    /* big sprite */
                    gfx = 2;
                    code = (code >> 4) | ((code & 0x0c) << 2);
                } else {
                    /* small sprite */
                    gfx = 1;
                    code >>= 2;
                }

                if (flip_screen_y[0] == 0) {
                    sy = 256 - Machine.gfx[gfx].width - sy;
                    flipy = NOT(flipy);
                }

                if (flip_screen_x[0] == 0) {
                    sx--;
                }

                drawgfx(bitmap, Machine.gfx[gfx],
                        code,
                        col,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }
        }
    };
}
