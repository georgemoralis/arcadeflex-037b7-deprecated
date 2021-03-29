/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhStartPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhUpdatePtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;

public class xain {

    public static UBytePtr xain_charram = new UBytePtr();
    public static UBytePtr xain_bgram0 = new UBytePtr();
    public static UBytePtr xain_bgram1 = new UBytePtr();

    static struct_tilemap char_tilemap, bgram0_tilemap, bgram1_tilemap;

    /**
     * *************************************************************************
     *
     * Callbacks for the TileMap code
     *
     **************************************************************************
     */
    public static GetMemoryOffsetPtr back_scan = new GetMemoryOffsetPtr() {
        public int handler(int u32_col, int u32_row, int u32_num_cols, int u32_num_rows) {
            /* logical (col,row) . memory offset */
            return (u32_col & 0x0f) + ((u32_row & 0x0f) << 4) + ((u32_col & 0x10) << 4) + ((u32_row & 0x10) << 5);
        }
    };

    public static GetTileInfoPtr get_bgram0_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            int attr = xain_bgram0.read(tile_index | 0x400);
            SET_TILE_INFO(2, xain_bgram0.read(tile_index) | ((attr & 7) << 8), (attr & 0x70) >> 4);
            tile_info.u32_flags = (attr & 0x80) != 0 ? TILE_FLIPX : 0;
        }
    };

    public static GetTileInfoPtr get_bgram1_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            int attr = xain_bgram1.read(tile_index | 0x400);
            SET_TILE_INFO(1, xain_bgram1.read(tile_index) | ((attr & 7) << 8), (attr & 0x70) >> 4);
            tile_info.u32_flags = (attr & 0x80) != 0 ? TILE_FLIPX : 0;
        }
    };

    public static GetTileInfoPtr get_char_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            int attr = xain_charram.read(tile_index | 0x400);
            SET_TILE_INFO(0, xain_charram.read(tile_index) | ((attr & 3) << 8), (attr & 0xe0) >> 5);
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStartPtr xain_vh_start = new VhStartPtr() {
        public int handler() {
            bgram0_tilemap = tilemap_create(get_bgram0_tile_info, back_scan, TILEMAP_OPAQUE, 16, 16, 32, 32);
            bgram1_tilemap = tilemap_create(get_bgram1_tile_info, back_scan, TILEMAP_TRANSPARENT, 16, 16, 32, 32);
            char_tilemap = tilemap_create(get_char_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);

            if (bgram0_tilemap == null || bgram1_tilemap == null || char_tilemap == null) {
                return 1;
            }

            bgram1_tilemap.transparent_pen = 0;
            char_tilemap.transparent_pen = 0;

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
    public static WriteHandlerPtr xain_bgram0_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (xain_bgram0.read(offset) != data) {
                xain_bgram0.write(offset, data);
                tilemap_mark_tile_dirty(bgram0_tilemap, offset & 0x3ff);
            }
        }
    };

    public static WriteHandlerPtr xain_bgram1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (xain_bgram1.read(offset) != data) {
                xain_bgram1.write(offset, data);
                tilemap_mark_tile_dirty(bgram1_tilemap, offset & 0x3ff);
            }
        }
    };

    public static WriteHandlerPtr xain_charram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (xain_charram.read(offset) != data) {
                xain_charram.write(offset, data);
                tilemap_mark_tile_dirty(char_tilemap, offset & 0x3ff);
            }
        }
    };
    static /*unsigned char*/ int[] xain_scrollxP0 = new int[2];
    public static WriteHandlerPtr xain_scrollxP0_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            xain_scrollxP0[offset] = data & 0xFF;
            tilemap_set_scrollx(bgram0_tilemap, 0, xain_scrollxP0[0] | (xain_scrollxP0[1] << 8));
        }
    };

    static /*unsigned char*/ int[] xain_scrollyP0 = new int[2];
    public static WriteHandlerPtr xain_scrollyP0_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            xain_scrollyP0[offset] = data & 0xFF;
            tilemap_set_scrolly(bgram0_tilemap, 0, xain_scrollyP0[0] | (xain_scrollyP0[1] << 8));
        }
    };
    static /*unsigned char*/ int[] xain_scrollxP1 = new int[2];
    public static WriteHandlerPtr xain_scrollxP1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            xain_scrollxP1[offset] = data & 0xFF;
            tilemap_set_scrollx(bgram1_tilemap, 0, xain_scrollxP1[0] | (xain_scrollxP1[1] << 8));
        }
    };

    static /*unsigned char*/ int[] xain_scrollyP1 = new int[2];
    public static WriteHandlerPtr xain_scrollyP1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            xain_scrollyP1[offset] = data & 0xFF;
            tilemap_set_scrolly(bgram1_tilemap, 0, xain_scrollyP1[0] | (xain_scrollyP1[1] << 8));
        }
    };

    public static WriteHandlerPtr xain_flipscreen_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            flip_screen_w.handler(0, data & 1);
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
        int offs;

        for (offs = 0; offs < spriteram_size[0]; offs += 4) {
            int sx, sy, flipx;
            int attr = spriteram.read(offs + 1);
            int numtile = spriteram.read(offs + 2) | ((attr & 7) << 8);
            int color = (attr & 0x38) >> 3;

            sx = 239 - spriteram.read(offs + 3);
            if (sx <= -7) {
                sx += 256;
            }
            sy = 240 - spriteram.read(offs);
            if (sy <= -7) {
                sy += 256;
            }
            flipx = attr & 0x40;
            if (flip_screen() != 0) {
                sx = 239 - sx;
                sy = 240 - sy;
                flipx = NOT(flipx);
            }

            if ((attr & 0x80) != 0) /* double height */ {
                drawgfx(bitmap, Machine.gfx[3],
                        numtile,
                        color,
                        flipx, flip_screen(),
                        sx - 1, flip_screen() != 0 ? sy + 16 : sy - 16,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
                drawgfx(bitmap, Machine.gfx[3],
                        numtile + 1,
                        color,
                        flipx, flip_screen(),
                        sx - 1, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            } else {
                drawgfx(bitmap, Machine.gfx[3],
                        numtile,
                        color,
                        flipx, flip_screen(),
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }
        }
    }

    public static VhUpdatePtr xain_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            tilemap_update(ALL_TILEMAPS);

            palette_init_used_colors();
            memset(palette_used_colors, 128, PALETTE_COLOR_USED, 128);
            /* sprites */
            if (palette_recalc() != null) {
                tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
            }

            tilemap_render(ALL_TILEMAPS);

            tilemap_draw(bitmap, bgram0_tilemap, 0);
            tilemap_draw(bitmap, bgram1_tilemap, 0);
            draw_sprites(bitmap);
            tilemap_draw(bitmap, char_tilemap, 0);
        }
    };
}
