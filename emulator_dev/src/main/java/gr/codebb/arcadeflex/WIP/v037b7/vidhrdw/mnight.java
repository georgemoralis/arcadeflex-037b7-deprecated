/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.copyscrollbitmap;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;

public class mnight {

    //#define COLORTABLE_START(gfxn,color)	Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + color * Machine.gfx[gfxn].color_granularity
    //#define GFX_COLOR_CODES(gfxn) 		Machine.gfx[gfxn].total_colors
    //#define GFX_ELEM_COLORS(gfxn) 		Machine.gfx[gfxn].color_granularity
    public static UBytePtr mnight_scrolly_ram=new UBytePtr();
    public static UBytePtr mnight_scrollx_ram=new UBytePtr();
    public static UBytePtr mnight_bgenable_ram=new UBytePtr();
    public static UBytePtr mnight_spoverdraw_ram=new UBytePtr();
    public static UBytePtr mnight_spriteram=new UBytePtr();
    public static int[] mnight_spriteram_size = new int[1];
    public static UBytePtr mnight_background_videoram=new UBytePtr();
    public static int[] mnight_backgroundram_size = new int[1];
    public static UBytePtr mnight_foreground_videoram=new UBytePtr();
    public static int[] mnight_foregroundram_size = new int[1];

    static osd_bitmap bitmap_bg;
    static osd_bitmap bitmap_sp;

    static char[] bg_dirtybuffer;
    static int bg_enable = 1;
    static int sp_overdraw = 0;

    public static VhStartPtr mnight_vh_start = new VhStartPtr() {
        public int handler() {
            int i;

            if ((bg_dirtybuffer = new char[1024]) == null) {
                return 1;
            }
            if ((bitmap_bg = bitmap_alloc(Machine.drv.screen_width * 2, Machine.drv.screen_height * 2)) == null) {
                bg_dirtybuffer = null;
                return 1;
            }
            if ((bitmap_sp = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                bg_dirtybuffer = null;
                bitmap_bg = null;
                return 1;
            }
            memset(bg_dirtybuffer, 1, 1024);

            /* chars, background tiles, sprites */
            memset(palette_used_colors, PALETTE_COLOR_USED, Machine.drv.total_colors);

            for (i = 0; i < Machine.gfx[1].total_colors; i++) {
                palette_used_colors.write(Machine.drv.gfxdecodeinfo[1].color_codes_start + i * Machine.gfx[1].color_granularity + 15, PALETTE_COLOR_TRANSPARENT);
                palette_used_colors.write(Machine.drv.gfxdecodeinfo[2].color_codes_start + i * Machine.gfx[2].color_granularity + 15, PALETTE_COLOR_TRANSPARENT);
            }
            return 0;
        }
    };

    public static VhStopPtr mnight_vh_stop = new VhStopPtr() {
        public void handler() {
            bitmap_free(bitmap_bg);
            bitmap_free(bitmap_sp);
            bg_dirtybuffer = null;
        }
    };

    public static WriteHandlerPtr mnight_bgvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (mnight_background_videoram.read(offset) != data) {
                bg_dirtybuffer[offset >> 1] = 1;
                mnight_background_videoram.write(offset, data);
            }
        }
    };

    public static WriteHandlerPtr mnight_fgvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (mnight_foreground_videoram.read(offset) != data) {
                mnight_foreground_videoram.write(offset, data);
            }
        }
    };

    public static WriteHandlerPtr mnight_background_enable_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (bg_enable != data) {
                mnight_bgenable_ram.write(offset, data);
                bg_enable = data;
                if (bg_enable != 0) {
                    memset(bg_dirtybuffer, 1, mnight_backgroundram_size[0] / 2);
                } else {
                    fillbitmap(bitmap_bg, palette_transparent_pen, null);
                }
            }
        }
    };

    public static WriteHandlerPtr mnight_sprite_overdraw_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (sp_overdraw != (data & 1)) {
                mnight_spoverdraw_ram.write(offset, data);
                fillbitmap(bitmap_sp, 15, Machine.visible_area);
                sp_overdraw = data & 1;
            }
        }
    };

    public static void mnight_draw_foreground(osd_bitmap bitmap) {
        int offs;

        /* Draw the foreground text */
        for (offs = 0; offs < mnight_foregroundram_size[0] / 2; offs++) {
            int sx, sy, tile, palette, flipx, flipy, lo, hi;

            if (mnight_foreground_videoram.read(offs * 2) != 0 | mnight_foreground_videoram.read(offs * 2 + 1) != 0) {
                sx = (offs % 32) << 3;
                sy = (offs >> 5) << 3;

                lo = mnight_foreground_videoram.read(offs * 2);
                hi = mnight_foreground_videoram.read(offs * 2 + 1);
                tile = ((hi & 0xc0) << 2) | lo;
                flipx = hi & 0x10;
                flipy = hi & 0x20;
                palette = hi & 0x0f;

                drawgfx(bitmap, Machine.gfx[3],
                        tile,
                        palette,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 15);
            }

        }
    }

    public static void mnight_draw_background(osd_bitmap bitmap) {
        int offs;

        /* for every character in the Video RAM, check if it has been modified */
 /* since last time and update it accordingly. */
        for (offs = 0; offs < mnight_backgroundram_size[0] / 2; offs++) {
            int sx, sy, tile, palette, flipy, lo, hi;

            if (bg_dirtybuffer[offs] != 0) {
                sx = (offs % 32) << 4;
                sy = (offs >> 5) << 4;

                bg_dirtybuffer[offs] = 0;

                lo = mnight_background_videoram.read(offs * 2);
                hi = mnight_background_videoram.read(offs * 2 + 1);
                tile = ((hi & 0x10) << 6) | ((hi & 0xc0) << 2) | lo;
                flipy = hi & 0x20;
                palette = hi & 0x0f;
                drawgfx(bitmap, Machine.gfx[0],
                        tile,
                        palette,
                        0, flipy,
                        sx, sy,
                        null, TRANSPARENCY_NONE, 0);
            }

        }
    }

    public static void mnight_draw_sprites(osd_bitmap bitmap) {
        int offs;

        /* Draw the sprites */
        for (offs = 11; offs < mnight_spriteram_size[0]; offs += 16) {
            int sx, sy, tile, palette, flipx, flipy, big;

            if ((mnight_spriteram.read(offs + 2) & 2) != 0) {
                sx = mnight_spriteram.read(offs + 1);
                sy = mnight_spriteram.read(offs);
                if ((mnight_spriteram.read(offs + 2) & 1) != 0) {
                    sx -= 256;
                }
                tile = mnight_spriteram.read(offs + 3) + ((mnight_spriteram.read(offs + 2) & 0xc0) << 2) + ((mnight_spriteram.read(offs + 2) & 0x08) << 7);
                big = mnight_spriteram.read(offs + 2) & 4;
                if (big != 0) {
                    tile /= 4;
                }
                flipx = mnight_spriteram.read(offs + 2) & 0x10;
                flipy = mnight_spriteram.read(offs + 2) & 0x20;
                palette = mnight_spriteram.read(offs + 4) & 0x0f;
                drawgfx(bitmap, Machine.gfx[(big) != 0 ? 2 : 1],
                        tile,
                        palette,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area,
                        TRANSPARENCY_PEN, 15);
            }
        }
    }

    /**
     * *************************************************************************
     *
     * Draw the game screen in the given osd_bitmap. Do NOT call
     * osd_update_display() from this function, it will be called by the main
     * emulation engine.
     *
     **************************************************************************
     */
    public static VhUpdatePtr mnight_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int[] scrollx = new int[1];
            int[] scrolly = new int[1];

            /* TODO: handle palette properly, it overflows */
            if (palette_recalc() != null) {
                memset(bg_dirtybuffer, 1, mnight_backgroundram_size[0] / 2);
            }

            if (bg_enable != 0) {
                mnight_draw_background(bitmap_bg);
            }

            scrollx[0] = -((mnight_scrollx_ram.read(0) + mnight_scrollx_ram.read(1) * 256) & 0x1FF);
            scrolly[0] = -((mnight_scrolly_ram.read(0) + mnight_scrolly_ram.read(1) * 256) & 0x1FF);

            if (sp_overdraw != 0) /* overdraw sprite mode */ {
                copyscrollbitmap(bitmap, bitmap_bg, 1, scrollx, 1, scrolly, Machine.visible_area, TRANSPARENCY_NONE, 0);
                mnight_draw_sprites(bitmap_sp);
                mnight_draw_foreground(bitmap_sp);
                copybitmap(bitmap, bitmap_sp, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_PEN, 15);
            } else /* normal sprite mode */ {
                copyscrollbitmap(bitmap, bitmap_bg, 1, scrollx, 1, scrolly, Machine.visible_area, TRANSPARENCY_NONE, 0);
                mnight_draw_sprites(bitmap);
                mnight_draw_foreground(bitmap);
            }

        }
    };
}
