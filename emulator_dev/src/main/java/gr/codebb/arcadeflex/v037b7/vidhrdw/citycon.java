/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.old.arcadeflex.video.osd_free_bitmap;

public class citycon {

    public static UBytePtr citycon_charlookup = new UBytePtr();
    public static UBytePtr citycon_scroll = new UBytePtr();
    static osd_bitmap tmpbitmap2;

    static int[] bg_image = new int[1];
    static char[] dirtylookup = new char[32];

    /**
     * *************************************************************************
     * Start the video hardware emulation.
     * *************************************************************************
     */
    public static VhStartPtr citycon_vh_start = new VhStartPtr() {
        public int handler() {
            if ((dirtybuffer = new char[videoram_size[0]]) == null) {
                return 1;
            }
            memset(dirtybuffer, 1, videoram_size[0]);

            /* forces creating the background */
            schedule_full_refresh();

            /* CityConnection has a virtual screen 4 times as large as the visible screen */
            if ((tmpbitmap = bitmap_alloc(4 * Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                dirtybuffer = null;
                return 1;
            }

            /* And another one for background */
            if ((tmpbitmap2 = bitmap_alloc(4 * Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                osd_free_bitmap(tmpbitmap);
                dirtybuffer = null;
                return 1;
            }

            return 0;

        }
    };

    /**
     * *************************************************************************
     * Stop the video hardware emulation.
     * *************************************************************************
     */
    public static VhStopPtr citycon_vh_stop = new VhStopPtr() {
        public void handler() {
            dirtybuffer = null;
            osd_free_bitmap(tmpbitmap);
            osd_free_bitmap(tmpbitmap2);
        }
    };

    public static WriteHandlerPtr citycon_charlookup_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (citycon_charlookup.read(offset) != data) {
                citycon_charlookup.write(offset, data);

                dirtylookup[offset / 8] = 1;
            }

        }
    };

    public static WriteHandlerPtr citycon_background_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bits 4-7 control the background image */
            set_vh_global_attribute(bg_image, data >> 4);

            /* bit 0 flips screen */
 /* it is also used to multiplex player 1 and player 2 controls */
            flip_screen_w.handler(offset, data & 0x01);

            /* bits 1-3 are unknown */
            if ((data & 0x0e) != 0) {
                logerror("background register = %02x\n", data);
            }
        }
    };

    public static ReadHandlerPtr citycon_in_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return readinputport(flip_screen() != 0 ? 1 : 0);
        }
    };

    /**
     * *************************************************************************
     * Draw the game screen in the given osd_bitmap. Do NOT call
     * osd_update_display() from this function, it will be called by the main
     * emulation engine.
     * *************************************************************************
     */
    public static VhUpdatePtr citycon_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            palette_init_used_colors();

            for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
                int code, color;

                code = memory_region(REGION_GFX4).read(0x1000 * bg_image[0] + offs);
                color = memory_region(REGION_GFX4).read(0xc000 + 0x100 * bg_image[0] + code);
                //memset(&palette_used_colors[256 + 16 * color],PALETTE_COLOR_USED,16);
                for (int i = 0; i < 16; i++) {
                    palette_used_colors.write(256 + 16 * color + i, PALETTE_COLOR_USED);
                }
            }
            for (offs = 0; offs < 256; offs++) {
                int color;

                color = citycon_charlookup.read(offs);
                palette_used_colors.write(512 + 4 * color, PALETTE_COLOR_TRANSPARENT);
                //memset(&palette_used_colors[512 + 4 * color + 1],PALETTE_COLOR_USED,3);
                for (int i = 0; i < 3; i++) {
                    palette_used_colors.write(512 + 4 * color + 1 + i, PALETTE_COLOR_USED);
                }
            }
            for (offs = spriteram_size[0] - 4; offs >= 0; offs -= 4) {
                int color;

                color = spriteram.read(offs + 2) & 0x0f;
                //memset(&palette_used_colors[16 * color + 1],PALETTE_COLOR_USED,15);
                for (int i = 0; i < 15; i++) {
                    palette_used_colors.write(16 * color + 1 + i, PALETTE_COLOR_USED);
                }
            }

            if (palette_recalc() != null || full_refresh != 0) {
                memset(dirtybuffer, 1, videoram_size[0]);

                /* create the background */
                for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
                    int sx, sy, code;

                    sy = offs / 32;
                    sx = (offs % 32) + (sy & 0x60);
                    sy = sy & 31;
                    if (flip_screen() != 0) {
                        sx = 127 - sx;
                        sy = 31 - sy;
                    }

                    code = memory_region(REGION_GFX4).read(0x1000 * bg_image[0] + offs);

                    drawgfx(tmpbitmap2, Machine.gfx[3 + bg_image[0]],
                            code,
                            memory_region(REGION_GFX4).read(0xc000 + 0x100 * bg_image[0] + code),
                            flip_screen(), flip_screen(),
                            8 * sx, 8 * sy,
                            null, TRANSPARENCY_NONE, 0);
                }
            }

            /* copy the temporary bitmap to the screen */
            {
                int scroll;

                if (flip_screen() != 0) {
                    scroll = 256 + ((citycon_scroll.read(0) * 256 + citycon_scroll.read(1)) >> 1);
                } else {
                    scroll = -((citycon_scroll.read(0) * 256 + citycon_scroll.read(1)) >> 1);
                }

                copyscrollbitmap(bitmap, tmpbitmap2, 1, new int[]{scroll}, 0, null, Machine.visible_area, TRANSPARENCY_NONE, 0);
            }

            /* for every character in the Video RAM, check if it has been modified */
 /* since last time and update it accordingly. */
            for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
                int sx, sy;

                sy = offs / 32;
                sx = (offs % 32) + (sy & 0x60);
                sy = sy & 0x1f;

                if (dirtybuffer[offs] != 0 || dirtylookup[sy] != 0) {
                    int i;
                    rectangle clip = new rectangle();

                    dirtybuffer[offs] = 0;

                    if (flip_screen() != 0) {
                        sx = 127 - sx;
                        sy = 31 - sy;
                    }
                    clip.min_x = 8 * sx;
                    clip.max_x = 8 * sx + 7;

                    /* City Connection controls the color code for each _scanline_, not */
 /* for each character as happens in most games. Therefore, we have to draw */
 /* the character eight times, each time clipped to one line and using */
 /* the color code for that scanline */
                    for (i = 0; i < 8; i++) {
                        clip.min_y = 8 * sy + i;
                        clip.max_y = 8 * sy + i;

                        drawgfx(tmpbitmap, Machine.gfx[0],
                                videoram.read(offs),
                                citycon_charlookup.read(flip_screen() != 0 ? (255 - 8 * sy - i) : 8 * sy + i),
                                flip_screen(), flip_screen(),
                                8 * sx, 8 * sy,
                                clip, TRANSPARENCY_NONE, 0);
                    }
                }
            }

            /* copy the temporary bitmap to the screen */
            {
                int i;
                int[] scroll = new int[32];

                if (flip_screen() != 0) {
                    for (i = 0; i < 6; i++) {
                        scroll[31 - i] = 256;
                    }
                    for (i = 6; i < 32; i++) {
                        scroll[31 - i] = 256 + (citycon_scroll.read(0) * 256 + citycon_scroll.read(1));
                    }
                } else {
                    for (i = 0; i < 6; i++) {
                        scroll[i] = 0;
                    }
                    for (i = 6; i < 32; i++) {
                        scroll[i] = -(citycon_scroll.read(0) * 256 + citycon_scroll.read(1));
                    }
                }
                copyscrollbitmap(bitmap, tmpbitmap, 32, scroll, 0, null,
                        Machine.visible_area, TRANSPARENCY_PEN, palette_transparent_pen);
            }

            for (offs = spriteram_size[0] - 4; offs >= 0; offs -= 4) {
                int sx, sy, flipx;

                sx = spriteram.read(offs + 3);
                sy = 239 - spriteram.read(offs);
                flipx = ~spriteram.read(offs + 2) & 0x10;
                if (flip_screen() != 0) {
                    sx = 240 - sx;
                    sy = 238 - sy;
                    flipx = NOT(flipx);
                }

                drawgfx(bitmap, Machine.gfx[(spriteram.read(offs + 1) & 0x80) != 0 ? 2 : 1],
                        spriteram.read(offs + 1) & 0x7f,
                        spriteram.read(offs + 2) & 0x0f,
                        flipx, flip_screen(),
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }

            for (offs = 0; offs < 32; offs++) {
                dirtylookup[offs] = 0;
            }
        }
    };
}
