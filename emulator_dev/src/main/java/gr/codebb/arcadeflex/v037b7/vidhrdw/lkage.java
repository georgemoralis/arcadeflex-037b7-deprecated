/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.spriteram;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.videoram;

public class lkage {

    public static UBytePtr lkage_scroll = new UBytePtr();
    public static UBytePtr lkage_vreg = new UBytePtr();
    static /*unsigned*/ char bg_tile_bank, fg_tile_bank;

    static struct_tilemap bg_tilemap;
    static struct_tilemap fg_tilemap;
    static struct_tilemap tx_tilemap;

    public static WriteHandlerPtr lkage_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (videoram.read(offset) != data) {
                videoram.write(offset, data);

                switch (offset / 0x400) {
                    case 0:
                        tilemap_mark_tile_dirty(tx_tilemap, offset & 0x3ff);
                        break;

                    case 1:
                        tilemap_mark_tile_dirty(fg_tilemap, offset & 0x3ff);
                        break;

                    case 2:
                        tilemap_mark_tile_dirty(bg_tilemap, offset & 0x3ff);
                        break;
                }
            }
        }
    };

    public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            SET_TILE_INFO(0, videoram.read(tile_index + 0x800) + 256 * (bg_tile_bank != 0 ? 5 : 1), 0);
        }
    };

    public static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            SET_TILE_INFO(0, videoram.read(tile_index + 0x400) + 256 * (fg_tile_bank != 0 ? 1 : 0), 1);
        }
    };

    public static GetTileInfoPtr get_tx_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            SET_TILE_INFO(0, videoram.read(tile_index), 2);
        }
    };

    public static VhStartPtr lkage_vh_start = new VhStartPtr() {
        public int handler() {
            bg_tile_bank = fg_tile_bank = 0;

            bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 8, 8, 32, 32);
            fg_tilemap = tilemap_create(get_fg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);
            tx_tilemap = tilemap_create(get_tx_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);

            fg_tilemap.transparent_pen = 0;
            tx_tilemap.transparent_pen = 0;

            tilemap_set_scrolldx(tx_tilemap, -9, 15);
            tilemap_set_scrolldx(fg_tilemap, -15, 13);
            tilemap_set_scrolldx(bg_tilemap, -13, 19);

            return 0;
        }
    };

    static void draw_sprites(osd_bitmap bitmap, int priority) {
        rectangle clip = new rectangle(Machine.visible_area);
        int finish = spriteram.offset;
        UBytePtr source = new UBytePtr(spriteram, 0x60 - 4);
        GfxElement gfx = Machine.gfx[1];

        while (source.offset >= finish) {
            int attributes = source.read(2);
            /*
				bit 0: horizontal flip
				bit 1: vertical flip
				bit 2: bank select
				bit 3: sprite size
				bit 4..6: color
				bit 7: priority
             */

            if ((attributes >> 7) == priority) {
                int y;
                int color = (attributes >> 4) & 7;
                int flipx = attributes & 0x01;
                int flipy = attributes & 0x02;
                int height = (attributes & 0x08) != 0 ? 2 : 1;
                int sx = source.read(0);
                int sy = 256 - 16 * height - source.read(1);
                int sprite_number = source.read(3) + ((attributes & 0x04) << 6);

                if (flip_screen_x[0] != 0) {
                    sx = 240 - sx - 6;
                    flipx = NOT(flipx);
                } else {
                    sx -= 23;
                }
                sx = ((sx + 8) & 0xff) - 8;
                if (flip_screen_y[0] != 0) {
                    sy = 256 - 16 * height - sy;
                    flipy = NOT(flipy);
                }
                sy -= 1;

                if (height == 2 && flipy == 0) {
                    sprite_number ^= 1;
                }

                for (y = 0; y < height; y++) {
                    drawgfx(bitmap, gfx,
                            sprite_number ^ y,
                            color,
                            flipx, flipy,
                            sx, sy + 16 * y,
                            clip,
                            TRANSPARENCY_PEN, 0);
                }
            }
            source.dec(4);
        }
    }

    static void lkage_set_palette_row(int virtual_row, int logical_row, int len) {
        UBytePtr source = new UBytePtr(paletteram, logical_row * 32);
        int indx = virtual_row * 16;
        while (len-- != 0) {
            char greenblue = source.readinc();
            char red = source.readinc();
            palette_change_color(indx++,
                    (red & 0xf) * 0x11,
                    (greenblue >> 4) * 0x11,
                    (greenblue & 0xf) * 0x11
            );
        }
    }

    public static VhUpdatePtr lkage_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            flip_screen_x_w.handler(0, ~lkage_vreg.read(2) & 0x01);
            flip_screen_y_w.handler(0, ~lkage_vreg.read(2) & 0x02);

            if (bg_tile_bank != (lkage_vreg.read(1) & 0x08)) {
                bg_tile_bank = (char) (lkage_vreg.read(1) & 0x08);
                tilemap_mark_all_tiles_dirty(bg_tilemap);
            }

            if (fg_tile_bank != (lkage_vreg.read(0) & 0x04)) {
                fg_tile_bank = (char) (lkage_vreg.read(0) & 0x04);
                tilemap_mark_all_tiles_dirty(fg_tilemap);
            }

            {
                lkage_set_palette_row(0x0, 0x00, 16 * 8);
                /* sprite colors */
                lkage_set_palette_row(0x8, 0x30 + (lkage_vreg.read(1) >> 4), 16);
                /* bg colors */
                lkage_set_palette_row(0x9, 0x20 + (lkage_vreg.read(1) >> 4), 16);
                /* fg colors */
                lkage_set_palette_row(0xa, 0x11, 16);
                /* text colors */
            }

            tilemap_set_scrollx(tx_tilemap, 0, lkage_scroll.read(0));
            tilemap_set_scrolly(tx_tilemap, 0, lkage_scroll.read(1));
            tilemap_set_scrollx(fg_tilemap, 0, lkage_scroll.read(2));
            tilemap_set_scrolly(fg_tilemap, 0, lkage_scroll.read(3));
            tilemap_set_scrollx(bg_tilemap, 0, lkage_scroll.read(4));
            tilemap_set_scrolly(bg_tilemap, 0, lkage_scroll.read(5));

            tilemap_update(ALL_TILEMAPS);
            if (palette_recalc() != null) {
                tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
            }
            tilemap_render(ALL_TILEMAPS);

            if ((lkage_vreg.read(2) & 0xf0) == 0xf0) {
                tilemap_draw(bitmap, bg_tilemap, 0);
                draw_sprites(bitmap, 1);
                tilemap_draw(bitmap, fg_tilemap, 0);
                draw_sprites(bitmap, 0);
                tilemap_draw(bitmap, tx_tilemap, 0);
            } else {
                tilemap_draw(bitmap, tx_tilemap, TILEMAP_IGNORE_TRANSPARENCY);
            }
        }
    };
}
