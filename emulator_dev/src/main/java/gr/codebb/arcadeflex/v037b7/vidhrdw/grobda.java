/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.flip_screen;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;
import static gr.codebb.arcadeflex.old.mame.drawgfx.copybitmap;

public class grobda {

    public static VhConvertColorPromPtr grobda_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;

            for (i = 0; i < 32; i++) {
                int bit0, bit1, bit2;

                bit0 = (color_prom.read(i) >> 0) & 0x01;
                bit1 = (color_prom.read(i) >> 1) & 0x01;
                bit2 = (color_prom.read(i) >> 2) & 0x01;
                palette[3 * i] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                bit0 = (color_prom.read(i) >> 3) & 0x01;
                bit1 = (color_prom.read(i) >> 4) & 0x01;
                bit2 = (color_prom.read(i) >> 5) & 0x01;
                palette[3 * i + 1] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                bit0 = 0;
                bit1 = (color_prom.read(i) >> 6) & 0x01;
                bit2 = (color_prom.read(i) >> 7) & 0x01;
                palette[3 * i + 2] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
            }
            /* characters */
            for (i = 0; i < 256; i++) {
                colortable[i] = (char) (0x1f - (color_prom.read(i + 32) & 0x0f));
            }
            /* sprites */
            for (i = 256; i < 512; i++) {
                colortable[i] = (char) (color_prom.read(i + 32) & 0x0f);
            }
        }
    };

    /**
     * *************************************************************************
     * Screen Refresh
     **************************************************************************
     */
    static void grobda_draw_sprites(osd_bitmap bitmap) {
        int offs;

        for (offs = 0; offs < 0x80; offs += 2) {
            int number = spriteram.read(offs + 0x0780);
            int color = spriteram.read(offs + 0x0781);
            int sx = (spriteram.read(offs + 0x0f81) - 40) + 0x100 * (spriteram.read(offs + 0x1781) & 1);
            int sy = 28 * 8 - spriteram.read(offs + 0x0f80) - 16;
            int flipx = spriteram.read(offs + 0x1780) & 1;
            int flipy = spriteram.read(offs + 0x1780) & 2;
            int width, height;

            if (flip_screen() != 0) {
                flipx = NOT(flipx);
                flipy = NOT(flipy);
            }

            if ((spriteram.read(offs + 0x1781) & 2) != 0) {
                continue;
            }

            switch (spriteram.read(offs + 0x1780) & 0x0c) {
                case 0x0c:
                    /* 2x both ways */
                    width = height = 2;
                    number &= (~3);
                    break;
                case 0x08:
                    /* 2x vertical */
                    width = 1;
                    height = 2;
                    number &= (~2);
                    break;
                case 0x04:
                    /* 2x horizontal */
                    width = 2;
                    height = 1;
                    number &= (~1);
                    sy += 16;
                    break;
                default:
                    /* normal sprite */
                    width = height = 1;
                    sy += 16;
                    break;
            }

            {
                int[] x_offset = {0x00, 0x01};
                int[] y_offset = {0x00, 0x02};
                int x, y, ex, ey;

                for (y = 0; y < height; y++) {
                    for (x = 0; x < width; x++) {
                        ex = flipx != 0 ? (width - 1 - x) : x;
                        ey = flipy != 0 ? (height - 1 - y) : y;

                        drawgfx(bitmap, Machine.gfx[1],
                                (number) + x_offset[ex] + y_offset[ey],
                                color,
                                flipx, flipy,
                                sx + x * 16, sy + y * 16,
                                Machine.visible_area,
                                TRANSPARENCY_PEN, 0);
                    }
                }
            }
        }
    }

    public static VhUpdatePtr grobda_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;
            if (full_refresh != 0) {
                memset(dirtybuffer, 1, videoram_size[0]);
            }

            for (offs = videoram_size[0] - 1; offs > 0; offs--) {
                if (dirtybuffer[offs] != 0) {
                    int mx, my, sx, sy;

                    dirtybuffer[offs] = 0;
                    mx = offs % 32;
                    my = offs / 32;

                    if (my < 2) {
                        if (mx < 2 || mx >= 30) {
                            continue; /* not visible */
                        }
                        sx = my + 34;
                        sy = mx - 2;
                    } else if (my >= 30) {
                        if (mx < 2 || mx >= 30) {
                            continue; /* not visible */
                        }
                        sx = my - 30;
                        sy = mx - 2;
                    } else {
                        sx = mx + 2;
                        sy = my - 2;
                    }

                    if (flip_screen() != 0) {
                        sx = 35 - sx;
                        sy = 27 - sy;
                    }

                    drawgfx(tmpbitmap, Machine.gfx[0],
                            videoram.read(offs),
                            colorram.read(offs) & 0x3f,
                            flip_screen(), flip_screen(),
                            sx * 8, sy * 8,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                }
            }

            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_NONE, 0);

            grobda_draw_sprites(bitmap);
        }
    };
}
