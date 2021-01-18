/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;

public class cbasebal {

    public static UBytePtr cbasebal_textram;
    public static UBytePtr cbasebal_scrollram;
    static struct_tilemap fg_tilemap, bg_tilemap;
    static int tilebank, spritebank;
    static int text_on, bg_on, obj_on;
    static int flipscreen;

    /**
     * *************************************************************************
     *
     * Callbacks for the TileMap code
     *
     **************************************************************************
     */
    public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attr = cbasebal_scrollram.read(2 * tile_index + 1);
            SET_TILE_INFO(1, cbasebal_scrollram.read(2 * tile_index) + ((attr & 0x07) << 8) + 0x800 * tilebank,
                    (attr & 0xf0) >> 4);
            tile_info.u32_flags = (attr & 0x08) != 0 ? TILE_FLIPX : 0;
        }
    };

    public static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attr = cbasebal_textram.read(tile_index + 0x800);
            SET_TILE_INFO(0, cbasebal_textram.read(tile_index) + ((attr & 0xf0) << 4), attr & 0x07);
            tile_info.u32_flags = (attr & 0x08) != 0 ? TILE_FLIPX : 0;
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStopPtr cbasebal_vh_stop = new VhStopPtr() {
        public void handler() {
            cbasebal_textram = null;
            cbasebal_scrollram = null;
        }
    };

    public static VhStartPtr cbasebal_vh_start = new VhStartPtr() {
        public int handler() {
            int i;

            cbasebal_textram = new UBytePtr(0x1000);
            cbasebal_scrollram = new UBytePtr(0x1000);

            bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 16, 16, 64, 32);
            fg_tilemap = tilemap_create(get_fg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 64, 32);

            if (cbasebal_textram == null || cbasebal_scrollram == null || bg_tilemap == null || fg_tilemap == null) {
                cbasebal_vh_stop.handler();
                return 1;
            }

            fg_tilemap.transparent_pen = 3;

            /*#define COLORTABLE_START(gfxn,color_code) Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + color_code * Machine.gfx[gfxn].color_granularity
	#define GFX_COLOR_CODES(gfxn) Machine.gfx[gfxn].total_colors
	#define GFX_ELEM_COLORS(gfxn) Machine.gfx[gfxn].color_granularity*/
            palette_init_used_colors();
            /* chars */
            for (i = 0; i < Machine.gfx[0].total_colors; i++) {
                memset(palette_used_colors, Machine.drv.gfxdecodeinfo[0].color_codes_start + i * Machine.gfx[0].color_granularity,
                        PALETTE_COLOR_USED,
                        Machine.gfx[0].color_granularity - 1);
            }
            /* bg tiles */
            for (i = 0; i < Machine.gfx[1].total_colors; i++) {
                memset(palette_used_colors, Machine.drv.gfxdecodeinfo[1].color_codes_start + i * Machine.gfx[1].color_granularity,
                        PALETTE_COLOR_USED,
                        Machine.gfx[1].color_granularity);
            }
            /* sprites */
            for (i = 0; i < Machine.gfx[2].total_colors; i++) {
                memset(palette_used_colors, Machine.drv.gfxdecodeinfo[2].color_codes_start + i * Machine.gfx[2].color_granularity,
                        PALETTE_COLOR_VISIBLE,
                        Machine.gfx[2].color_granularity - 1);
            }

            return 0;
        }
    };

    /**
     * *************************************************************************
     *
     * Memory handlers
     *
     **************************************************************************
     */
    public static WriteHandlerPtr cbasebal_textram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (cbasebal_textram.read(offset) != data) {
                cbasebal_textram.write(offset, data);
                tilemap_mark_tile_dirty(fg_tilemap, offset & 0x7ff);
            }
        }
    };

    public static ReadHandlerPtr cbasebal_textram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return cbasebal_textram.read(offset);
        }
    };

    public static WriteHandlerPtr cbasebal_scrollram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (cbasebal_scrollram.read(offset) != data) {
                cbasebal_scrollram.write(offset, data);
                tilemap_mark_tile_dirty(bg_tilemap, offset / 2);
            }
        }
    };

    public static ReadHandlerPtr cbasebal_scrollram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return cbasebal_scrollram.read(offset);
        }
    };

    public static WriteHandlerPtr cbasebal_gfxctrl_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bit 0 is unknown - toggles continuously */

 /* bit 1 is flip screen */
            flipscreen = data & 0x02;
            tilemap_set_flip(ALL_TILEMAPS, flipscreen != 0 ? (TILEMAP_FLIPY | TILEMAP_FLIPX) : 0);

            /* bit 2 is unknown - unused? */
 /* bit 3 is tile bank */
            if (tilebank != ((data & 0x08) >> 3)) {
                tilebank = (data & 0x08) >> 3;
                tilemap_mark_all_tiles_dirty(bg_tilemap);
            }

            /* bit 4 is sprite bank */
            spritebank = (data & 0x10) >> 4;

            /* bits 5 is text enable */
            text_on = ~data & 0x20;

            /* bits 6-7 are bg/sprite enable (don't know which is which) */
            bg_on = ~data & 0x40;
            obj_on = ~data & 0x80;

            /* other bits unknown, but used */
        }
    };
    static /*unsigned*/ char[] scroll_1 = new char[2];
    public static WriteHandlerPtr cbasebal_scrollx_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            scroll_1[offset] = (char) (data & 0xff);
            tilemap_set_scrollx(bg_tilemap, 0, scroll_1[0] + 256 * scroll_1[1]);
        }
    };
    static /*unsigned*/ char[] scroll_2 = new char[2];
    public static WriteHandlerPtr cbasebal_scrolly_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            scroll_2[offset] = (char) (data & 0xff);
            tilemap_set_scrolly(bg_tilemap, 0, scroll_2[0] + 256 * scroll_2[1]);
        }
    };

    /**
     * *************************************************************************
     *
     * Display refresh
     *
     **************************************************************************
     */
    static void draw_sprites(osd_bitmap bitmap) {
        int offs, sx, sy;

        /* the last entry is not a sprite, we skip it otherwise spang shows a bubble */
 /* moving diagonally across the screen */
        for (offs = spriteram_size[0] - 8; offs >= 0; offs -= 4) {
            int code = spriteram.read(offs);
            int attr = spriteram.read(offs + 1);
            int color = attr & 0x07;
            int flipx = attr & 0x08;
            sx = spriteram.read(offs + 3) + ((attr & 0x10) << 4);
            sy = ((spriteram.read(offs + 2) + 8) & 0xff) - 8;
            code += (attr & 0xe0) << 3;
            code += spritebank * 0x800;

            if (flipscreen != 0) {
                sx = 496 - sx;
                sy = 240 - sy;
                flipx = NOT(flipx);
            }

            drawgfx(bitmap, Machine.gfx[2],
                    code,
                    color,
                    flipx, flipscreen,
                    sx, sy,
                    Machine.visible_area, TRANSPARENCY_PEN, 15);
        }
    }

    public static VhUpdatePtr cbasebal_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            tilemap_update(ALL_TILEMAPS);

            if (palette_recalc() != null) {
                tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
            }

            tilemap_render(ALL_TILEMAPS);

            if (bg_on != 0) {
                tilemap_draw(bitmap, bg_tilemap, 0);
            } else {
                fillbitmap(bitmap, Machine.pens[768], Machine.visible_area);
            }

            if (obj_on != 0) {
                draw_sprites(bitmap);
            }

            if (text_on != 0) {
                tilemap_draw(bitmap, fg_tilemap, 0);
            }
        }
    };
}
