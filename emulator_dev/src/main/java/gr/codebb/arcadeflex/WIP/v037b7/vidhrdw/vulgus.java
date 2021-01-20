/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.old.mame.common.coin_counter_w;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.flip_screen_w;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.flip_screen;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class vulgus {

    public static UBytePtr vulgus_fgvideoram = new UBytePtr();
    public static UBytePtr vulgus_bgvideoram = new UBytePtr();
    public static UBytePtr vulgus_scroll_low = new UBytePtr();
    public static UBytePtr vulgus_scroll_high = new UBytePtr();

    static int vulgus_palette_bank;
    static struct_tilemap fg_tilemap, bg_tilemap;

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
    public static VhConvertColorPromPtr vulgus_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            int p_inc = 0;
            for (i = 0; i < Machine.drv.total_colors; i++) {
                int bit0, bit1, bit2, bit3;

                bit0 = (color_prom.read(0) >> 0) & 0x01;
                bit1 = (color_prom.read(0) >> 1) & 0x01;
                bit2 = (color_prom.read(0) >> 2) & 0x01;
                bit3 = (color_prom.read(0) >> 3) & 0x01;
                palette[p_inc++] = ((char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3));
                bit0 = (color_prom.read(Machine.drv.total_colors) >> 0) & 0x01;
                bit1 = (color_prom.read(Machine.drv.total_colors) >> 1) & 0x01;
                bit2 = (color_prom.read(Machine.drv.total_colors) >> 2) & 0x01;
                bit3 = (color_prom.read(Machine.drv.total_colors) >> 3) & 0x01;
                palette[p_inc++] = ((char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3));
                bit0 = (color_prom.read(2 * Machine.drv.total_colors) >> 0) & 0x01;
                bit1 = (color_prom.read(2 * Machine.drv.total_colors) >> 1) & 0x01;
                bit2 = (color_prom.read(2 * Machine.drv.total_colors) >> 2) & 0x01;
                bit3 = (color_prom.read(2 * Machine.drv.total_colors) >> 3) & 0x01;
                palette[p_inc++] = ((char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3));

                color_prom.inc();
            }

            color_prom.inc(2 * Machine.drv.total_colors);
            /* color_prom now points to the beginning of the lookup table */

 /* characters use colors 32-47 (?) */
            for (i = 0; i < TOTAL_COLORS(0); i++) {
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char) ((color_prom.readinc()) + 32);
            }

            /* sprites use colors 16-31 */
            for (i = 0; i < TOTAL_COLORS(2); i++) {
                colortable[Machine.drv.gfxdecodeinfo[2].color_codes_start + i] = (char) ((color_prom.readinc()) + 16);
            }

            /* background tiles use colors 0-15, 64-79, 128-143, 192-207 in four banks */
            for (i = 0; i < TOTAL_COLORS(1) / 4; i++) {
                colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + i] = (char) (color_prom.read());
                colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + i + 32 * 8] = (char) (color_prom.read() + 64);
                colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + i + 2 * 32 * 8] = (char) (color_prom.read() + 128);
                colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + i + 3 * 32 * 8] = (char) (color_prom.read() + 192);
                color_prom.inc();
            }
        }
    };

    /**
     * *************************************************************************
     *
     * Callbacks for the TileMap code
     *
     **************************************************************************
     */
    public static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            int code, color;

            code = vulgus_fgvideoram.read(tile_index);
            color = vulgus_fgvideoram.read(tile_index + 0x400);
            SET_TILE_INFO(0, code + ((color & 0x80) << 1), color & 0x3f);
        }
    };

    public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            int code, color;

            code = vulgus_bgvideoram.read(tile_index);
            color = vulgus_bgvideoram.read(tile_index + 0x400);
            SET_TILE_INFO(1, code + ((color & 0x80) << 1), (color & 0x1f) + (0x20 * vulgus_palette_bank));
            tile_info.u32_flags = TILE_FLIPYX((color & 0x60) >> 5);
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStartPtr vulgus_vh_start = new VhStartPtr() {
        public int handler() {
            fg_tilemap = tilemap_create(get_fg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT_COLOR, 8, 8, 32, 32);
            bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_cols, TILEMAP_OPAQUE, 16, 16, 32, 32);

            if (fg_tilemap == null || bg_tilemap == null) {
                return 1;
            }

            fg_tilemap.transparent_pen = 47;

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
    public static WriteHandlerPtr vulgus_fgvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            vulgus_fgvideoram.write(offset, data);
            tilemap_mark_tile_dirty(fg_tilemap, offset & 0x3ff);
        }
    };

    public static WriteHandlerPtr vulgus_bgvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            vulgus_bgvideoram.write(offset, data);
            tilemap_mark_tile_dirty(bg_tilemap, offset & 0x3ff);
        }
    };

    public static WriteHandlerPtr vulgus_c804_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bits 0 and 1 are coin counters */
            coin_counter_w.handler(0, data & 0x01);
            coin_counter_w.handler(1, data & 0x02);

            /* bit 7 flips screen */
            flip_screen_w.handler(offset, data & 0x80);
        }
    };

    public static WriteHandlerPtr vulgus_palette_bank_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (vulgus_palette_bank != data) {
                tilemap_mark_all_tiles_dirty(bg_tilemap);
            }

            vulgus_palette_bank = data;
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

        for (offs = spriteram_size[0] - 4; offs >= 0; offs -= 4) {
            int code, i, col, sx, sy, dir;

            code = spriteram.read(offs);
            col = spriteram.read(offs + 1) & 0x0f;
            sx = spriteram.read(offs + 3);
            sy = spriteram.read(offs + 2);
            dir = 1;
            if (flip_screen() != 0) {
                sx = 240 - sx;
                sy = 240 - sy;
                dir = -1;
            }

            i = (spriteram.read(offs + 1) & 0xc0) >> 6;
            if (i == 2) {
                i = 3;
            }

            do {
                drawgfx(bitmap, Machine.gfx[2],
                        code + i,
                        col,
                        flip_screen(), flip_screen(),
                        sx, sy + 16 * i * dir,
                        Machine.visible_area, TRANSPARENCY_PEN, 15);

                /* draw again with wraparound */
                drawgfx(bitmap, Machine.gfx[2],
                        code + i,
                        col,
                        flip_screen(), flip_screen(),
                        sx, sy + 16 * i * dir - dir * 256,
                        Machine.visible_area, TRANSPARENCY_PEN, 15);
                i--;
            } while (i >= 0);
        }
    }

    public static VhUpdatePtr vulgus_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            tilemap_set_scrollx(bg_tilemap, 0, vulgus_scroll_low.read(1) + 256 * vulgus_scroll_high.read(1));
            tilemap_set_scrolly(bg_tilemap, 0, vulgus_scroll_low.read(0) + 256 * vulgus_scroll_high.read(0));

            tilemap_update(ALL_TILEMAPS);
            tilemap_render(ALL_TILEMAPS);

            tilemap_draw(bitmap, bg_tilemap, 0);
            draw_sprites(bitmap);
            tilemap_draw(bitmap, fg_tilemap, 0);
        }
    };
}
