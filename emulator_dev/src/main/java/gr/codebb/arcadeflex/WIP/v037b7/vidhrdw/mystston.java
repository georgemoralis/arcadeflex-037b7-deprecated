/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.flip_screen_w;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.flip_screen;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.cpu_get_pc;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.TRANSPARENCY_PEN;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_recalc;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.tile_info;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.tilemap_create;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.tilemap_draw;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.tilemap_mark_all_pixels_dirty;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.tilemap_mark_all_tiles_dirty;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.tilemap_mark_tile_dirty;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.tilemap_render;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.tilemap_set_scrolly;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.tilemap_update;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.ALL_TILEMAPS;
import gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.GetMemoryOffsetPtr;
import gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.GetTileInfoPtr;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.SET_TILE_INFO;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.TILEMAP_OPAQUE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.TILEMAP_TRANSPARENT;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.TILE_FLIPY;
import gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.struct_tilemap;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.spriteram;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.spriteram_size;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;
import static gr.codebb.arcadeflex.old.mame.common.coin_counter_w;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhConvertColorPromPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhStartPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhUpdatePtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;



public class mystston {

    public static UBytePtr mystston_fgvideoram = new UBytePtr();
    public static UBytePtr mystston_bgvideoram = new UBytePtr();
    public static UBytePtr mystston_scroll = new UBytePtr();

    static int textcolor;
    static struct_tilemap fg_tilemap, bg_tilemap;

    /**
     * *************************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     * Mysterious Stones has both palette RAM and a PROM. The PROM is used for
     * text.
     *
     **************************************************************************
     */
    public static VhConvertColorPromPtr mystston_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;

            int p_inc = 0;
            p_inc += 3 * 24;
            /* first 24 colors are RAM */

            for (i = 0; i < 32; i++) {
                int bit0, bit1, bit2;

                /* red component */
                bit0 = (color_prom.read() >> 0) & 0x01;
                bit1 = (color_prom.read() >> 1) & 0x01;
                bit2 = (color_prom.read() >> 2) & 0x01;
                palette[p_inc++] = ((char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2));
                /* green component */
                bit0 = (color_prom.read() >> 3) & 0x01;
                bit1 = (color_prom.read() >> 4) & 0x01;
                bit2 = (color_prom.read() >> 5) & 0x01;
                palette[p_inc++] = ((char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2));
                /* blue component */
                bit0 = 0;
                bit1 = (color_prom.read() >> 6) & 0x01;
                bit2 = (color_prom.read() >> 7) & 0x01;
                palette[p_inc++] = ((char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2));

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
    public static GetMemoryOffsetPtr get_memory_offset = new GetMemoryOffsetPtr() {
        public int handler(int u32_col, int u32_row, int u32_num_cols, int u32_num_rows) {

            return (u32_num_cols - 1 - u32_col) * u32_num_rows + u32_row;
        }
    };

    public static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() {
        @Override
        public void handler(int tile_index) {
            int code;

            code = mystston_fgvideoram.read(tile_index) + ((mystston_fgvideoram.read(tile_index + 0x400) & 0x07) << 8);
            SET_TILE_INFO(0, code, textcolor);
        }
    };
    public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
        @Override
        public void handler(int tile_index) {
            int code;

            code = mystston_bgvideoram.read(tile_index) + ((mystston_bgvideoram.read(tile_index + 0x200) & 0x01) << 8);
            SET_TILE_INFO(1, code, 0);
            /* the right (lower) side of the screen is flipped */
            tile_info.u32_flags = (tile_index & 0x10) != 0 ? TILE_FLIPY : 0;
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStartPtr mystston_vh_start = new VhStartPtr() {
        public int handler() {
            fg_tilemap = tilemap_create(get_fg_tile_info, get_memory_offset, TILEMAP_TRANSPARENT, 8, 8, 32, 32);
            bg_tilemap = tilemap_create(get_bg_tile_info, get_memory_offset, TILEMAP_OPAQUE, 16, 16, 16, 32);

            if (fg_tilemap == null || bg_tilemap == null) {
                return 1;
            }

            fg_tilemap.transparent_pen = 0;

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
    public static WriteHandlerPtr mystston_fgvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            mystston_fgvideoram.write(offset, data);
            tilemap_mark_tile_dirty(fg_tilemap, offset & 0x3ff);
        }
    };

    public static WriteHandlerPtr mystston_bgvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            mystston_bgvideoram.write(offset, data);
            tilemap_mark_tile_dirty(bg_tilemap, offset & 0x1ff);
        }
    };

    public static WriteHandlerPtr mystston_scroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            tilemap_set_scrolly(bg_tilemap, 0, data);
        }
    };

    public static WriteHandlerPtr mystston_2000_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int new_textcolor;

            /* bits 0 and 1 are text color */
            new_textcolor = ((data & 0x01) << 1) | ((data & 0x02) >> 1);
            if (textcolor != new_textcolor) {
                tilemap_mark_all_tiles_dirty(fg_tilemap);
                textcolor = new_textcolor;
            }

            /* bits 4 and 5 are coin counters */
            coin_counter_w.handler(0, data & 0x10);
            coin_counter_w.handler(1, data & 0x20);

            /* bit 7 is screen flip */
            flip_screen_w.handler(0, data & 0x80);

            /* other bits unused? */
            logerror("PC %04x: 2000 = %02x\n", cpu_get_pc(), data);
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
            if ((spriteram.read(offs) & 0x01) != 0) {
                int sx, sy, flipx, flipy;

                sx = 240 - spriteram.read(offs + 3);
                sy = (240 - spriteram.read(offs + 2)) & 0xff;
                flipx = spriteram.read(offs) & 0x04;
                flipy = spriteram.read(offs) & 0x02;
                if (flip_screen() != 0) {
                    sx = 240 - sx;
                    sy = 240 - sy;
                    flipx = NOT(flipx);
                    flipy = NOT(flipy);
                }

                drawgfx(bitmap, Machine.gfx[2],
                        spriteram.read(offs + 1) + ((spriteram.read(offs) & 0x10) << 4),
                        (spriteram.read(offs) & 0x08) >> 3,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }
        }
    }

    public static VhUpdatePtr mystston_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            tilemap_update(ALL_TILEMAPS);

            if (palette_recalc() != null) {
                tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
            }

            tilemap_render(ALL_TILEMAPS);

            tilemap_draw(bitmap, bg_tilemap, 0);
            draw_sprites(bitmap);
            tilemap_draw(bitmap, fg_tilemap, 0);
        }
    };
}
