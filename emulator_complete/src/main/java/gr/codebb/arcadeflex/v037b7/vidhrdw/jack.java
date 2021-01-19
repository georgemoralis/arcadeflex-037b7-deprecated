/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class jack {

    public static WriteHandlerPtr jack_paletteram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* RGB output is inverted */
            paletteram_BBGGGRRR_w.handler(offset, ~data);
        }
    };

    public static ReadHandlerPtr jack_flipscreen_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            flip_screen_w.handler(0, offset);
            return 0;
        }
    };

    public static WriteHandlerPtr jack_flipscreen_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            flip_screen_w.handler(0, offset);
        }
    };

    public static VhUpdatePtr jack_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            if (palette_recalc() != null || full_refresh != 0) {
                memset(dirtybuffer, 1, videoram_size[0]);
            }

            /* for every character in the Video RAM, check if it has been modified */
 /* since last time and update it accordingly. */
            for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
                if (dirtybuffer[offs] != 0) {
                    int sx, sy;

                    dirtybuffer[offs] = 0;

                    sx = offs / 32;
                    sy = 31 - offs % 32;

                    if (flip_screen() != 0) {
                        sx = 31 - sx;
                        sy = 31 - sy;
                    }

                    drawgfx(tmpbitmap, Machine.gfx[0],
                            videoram.read(offs) + ((colorram.read(offs) & 0x18) << 5),
                            colorram.read(offs) & 0x07,
                            flip_screen(), flip_screen(),
                            8 * sx, 8 * sy,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                }
            }

            /* copy the temporary bitmap to the screen */
            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_NONE, 0);

            /* draw sprites */
            for (offs = spriteram_size[0] - 4; offs >= 0; offs -= 4) {
                int sx, sy, num, color, flipx, flipy;

                sx = spriteram.read(offs + 1);
                sy = spriteram.read(offs);
                num = spriteram.read(offs + 2) + ((spriteram.read(offs + 3) & 0x08) << 5);
                color = spriteram.read(offs + 3) & 0x07;
                flipx = (spriteram.read(offs + 3) & 0x80);
                flipy = (spriteram.read(offs + 3) & 0x40);

                if (flip_screen() != 0) {
                    sx = 248 - sx;
                    sy = 248 - sy;
                    flipx = NOT(flipx);
                    flipy = NOT(flipy);
                }

                drawgfx(bitmap, Machine.gfx[0],
                        num,
                        color,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }
        }
    };
}
