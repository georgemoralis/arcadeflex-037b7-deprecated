/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class blktiger {

    public static UBytePtr blktiger_txvideoram = new UBytePtr();

    public static final int BGRAM_BANK_SIZE = 0x1000;
    public static final int BGRAM_BANKS = 4;

    static int blktiger_scroll_bank;
    static UBytePtr scroll_ram;
    static int screen_layout;
    static int chon, objon, bgon;

    static struct_tilemap tx_tilemap, bg_tilemap8x4, bg_tilemap4x8;

    /**
     * *************************************************************************
     *
     * Callbacks for the TileMap code
     *
     **************************************************************************
     */
    public static GetMemoryOffsetPtr bg8x4_scan = new GetMemoryOffsetPtr() {
        public int handler(int u32_col, int u32_row, int u32_num_cols, int u32_num_rows) {
            /* logical (col,row) . memory offset */
            return (u32_col & 0x0f) + ((u32_row & 0x0f) << 4) + ((u32_col & 0x70) << 4) + ((u32_row & 0x30) << 7);
        }
    };
    public static GetMemoryOffsetPtr bg4x8_scan = new GetMemoryOffsetPtr() {
        public int handler(int u32_col, int u32_row, int u32_num_cols, int u32_num_rows) {
            /* logical (col,row) . memory offset */
            return (u32_col & 0x0f) + ((u32_row & 0x0f) << 4) + ((u32_col & 0x30) << 4) + ((u32_row & 0x70) << 6);
        }
    };

    public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /* the tile priority table is a guess compiled by looking at the game. It
		   was not derived from a PROM so it could be wrong. */
            int split_table[]
                    = {
                        3, 0, 2, 2, /* the fourth could be 1 instead of 2 */
                        0, 1, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0
                    };
            int attr = scroll_ram.read(2 * tile_index + 1);
            int color = (attr & 0x78) >> 3;
            SET_TILE_INFO(1, scroll_ram.read(2 * tile_index) + ((attr & 0x07) << 8), color);
            tile_info.u32_flags = TILE_SPLIT(split_table[color]);
            if ((attr & 0x80) != 0) {
                tile_info.u32_flags |= TILE_FLIPX;
            }
        }
    };

    public static GetTileInfoPtr get_tx_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            int attr = blktiger_txvideoram.read(tile_index + 0x400);
            SET_TILE_INFO(0, blktiger_txvideoram.read(tile_index) + ((attr & 0xe0) << 3), attr & 0x1f);
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStopPtr blktiger_vh_stop = new VhStopPtr() {
        public void handler() {
            scroll_ram = null;
        }
    };

    public static VhStartPtr blktiger_vh_start = new VhStartPtr() {
        public int handler() {
            scroll_ram = new UBytePtr(BGRAM_BANK_SIZE * BGRAM_BANKS);

            tx_tilemap = tilemap_create(get_tx_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);
            bg_tilemap8x4 = tilemap_create(get_bg_tile_info, bg8x4_scan, TILEMAP_TRANSPARENT | TILEMAP_SPLIT, 16, 16, 128, 64);
            bg_tilemap4x8 = tilemap_create(get_bg_tile_info, bg4x8_scan, TILEMAP_TRANSPARENT | TILEMAP_SPLIT, 16, 16, 64, 128);

            if (scroll_ram == null || tx_tilemap == null || bg_tilemap8x4 == null || bg_tilemap4x8 == null) {
                blktiger_vh_stop.handler();
                return 1;
            }

            tx_tilemap.transparent_pen = 3;
            bg_tilemap8x4.transparent_pen
                    = bg_tilemap4x8.transparent_pen = 15;

            bg_tilemap8x4.u32_transmask[0]
                    = bg_tilemap4x8.u32_transmask[0] = 0xffff;
            /* split type 0 is totally transparent in front half */
            bg_tilemap8x4.u32_transmask[1]
                    = bg_tilemap4x8.u32_transmask[1] = 0xfff0;
            /* split type 1 has pens 4-15 transparent in front half */
            bg_tilemap8x4.u32_transmask[2]
                    = bg_tilemap4x8.u32_transmask[2] = 0xff00;
            /* split type 1 has pens 8-15 transparent in front half */
            bg_tilemap8x4.u32_transmask[3]
                    = bg_tilemap4x8.u32_transmask[3] = 0xf000;
            /* split type 1 has pens 12-15 transparent in front half */

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
    public static WriteHandlerPtr blktiger_txvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (blktiger_txvideoram.read(offset) != data) {
                blktiger_txvideoram.write(offset, data);
                tilemap_mark_tile_dirty(tx_tilemap, offset & 0x3ff);
            }
        }
    };

    public static ReadHandlerPtr blktiger_bgvideoram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return scroll_ram.read(offset + blktiger_scroll_bank);
        }
    };

    public static WriteHandlerPtr blktiger_bgvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            offset += blktiger_scroll_bank;

            if (scroll_ram.read(offset) != data) {
                scroll_ram.write(offset, data);
                tilemap_mark_tile_dirty(bg_tilemap8x4, offset / 2);
                tilemap_mark_tile_dirty(bg_tilemap4x8, offset / 2);
            }
        }
    };

    public static WriteHandlerPtr blktiger_bgvideoram_bank_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            blktiger_scroll_bank = (data % BGRAM_BANKS) * BGRAM_BANK_SIZE;
        }
    };

    static UBytePtr scroll_1 = new UBytePtr(2);
    public static WriteHandlerPtr blktiger_scrolly_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int scrolly;
            scroll_1.write(offset, data);
            scrolly = scroll_1.read(0) | (scroll_1.read(1) << 8);
            tilemap_set_scrolly(bg_tilemap8x4, 0, scrolly);
            tilemap_set_scrolly(bg_tilemap4x8, 0, scrolly);
        }
    };
    static UBytePtr scroll_2 = new UBytePtr(2);
    public static WriteHandlerPtr blktiger_scrollx_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int scrollx;

            scroll_2.write(offset, data);
            scrollx = scroll_2.read(0) | (scroll_2.read(1) << 8);
            tilemap_set_scrollx(bg_tilemap8x4, 0, scrollx);
            tilemap_set_scrollx(bg_tilemap4x8, 0, scrollx);
        }
    };

    public static WriteHandlerPtr blktiger_video_control_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bits 0 and 1 are coin counters */
            coin_counter_w.handler(0, data & 1);
            coin_counter_w.handler(1, data & 2);

            /* bit 5 resets the sound CPU */
            cpu_set_reset_line(1, (data & 0x20) != 0 ? ASSERT_LINE : CLEAR_LINE);

            /* bit 6 flips screen */
            flip_screen_w.handler(0, data & 0x40);

            /* bit 7 enables characters? Just a guess */
            chon = ~data & 0x80;
        }
    };

    public static WriteHandlerPtr blktiger_video_enable_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* not sure which is which, but I think that bit 1 and 2 enable background and sprites */
 /* bit 1 enables bg ? */
            bgon = ~data & 0x02;

            /* bit 2 enables sprites ? */
            objon = ~data & 0x04;
        }
    };

    public static WriteHandlerPtr blktiger_screen_layout_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            screen_layout = data;
            tilemap_set_enable(bg_tilemap8x4, screen_layout);
            tilemap_set_enable(bg_tilemap4x8, NOT(screen_layout));
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

        /* Draw the sprites. */
        for (offs = spriteram_size[0] - 4; offs >= 0; offs -= 4) {
            int attr = buffered_spriteram.read(offs + 1);
            int sx = buffered_spriteram.read(offs + 3) - ((attr & 0x10) << 4);
            int sy = buffered_spriteram.read(offs + 2);
            int code = buffered_spriteram.read(offs) | ((attr & 0xe0) << 3);
            int color = attr & 0x07;
            int flipx = attr & 0x08;

            if (flip_screen() != 0) {
                sx = 240 - sx;
                sy = 240 - sy;
                flipx = NOT(flipx);
            }

            drawgfx(bitmap, Machine.gfx[2],
                    code,
                    color,
                    flipx, flip_screen(),
                    sx, sy,
                    Machine.visible_area, TRANSPARENCY_PEN, 15);
        }
    }

    static void mark_sprites_colors() {
        int offs;

        for (offs = spriteram_size[0] - 4; offs >= 0; offs -= 4) {
            int attr = buffered_spriteram.read(offs + 1);
            int sx = buffered_spriteram.read(offs + 3) - ((attr & 0x10) << 4);
            int sy = buffered_spriteram.read(offs + 2);

            /* only count visible sprites */
            if (sx + 15 >= Machine.visible_area.min_x
                    && sx <= Machine.visible_area.max_x
                    && sy + 15 >= Machine.visible_area.min_y
                    && sy <= Machine.visible_area.max_y) {
                int i;

                int color = attr & 0x07;
                int code = buffered_spriteram.read(offs) | ((attr & 0xe0) << 3);

                for (i = 0; i < 15; i++) {
                    if ((Machine.gfx[2].pen_usage[code] & (1 << i)) != 0) {
                        palette_used_colors.write(512 + 16 * color + i, PALETTE_COLOR_USED);
                    }
                }
            }
        }
    }

    public static VhUpdatePtr blktiger_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            tilemap_update(ALL_TILEMAPS);

            palette_init_used_colors();
            mark_sprites_colors();
            if (palette_recalc() != null) {
                tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
            }

            tilemap_render(ALL_TILEMAPS);

            fillbitmap(bitmap, palette_transparent_pen, Machine.visible_area);

            if (bgon != 0) {
                tilemap_draw(bitmap, screen_layout != 0 ? bg_tilemap8x4 : bg_tilemap4x8, TILEMAP_BACK);
            }

            if (objon != 0) {
                draw_sprites(bitmap);
            }

            if (bgon != 0) {
                tilemap_draw(bitmap, screen_layout != 0 ? bg_tilemap8x4 : bg_tilemap4x8, TILEMAP_FRONT);
            }

            if (chon != 0) {
                tilemap_draw(bitmap, tx_tilemap, 0);
            }
        }
    };

    public static VhEofCallbackPtr blktiger_eof_callback = new VhEofCallbackPtr() {
        public void handler() {
            buffer_spriteram_w.handler(0, 0);
        }
    };
}
