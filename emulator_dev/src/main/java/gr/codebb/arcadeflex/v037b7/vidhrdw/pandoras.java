/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.v037b7.drivers.pandoras.pandoras_sharedram;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class pandoras {

    static int flipscreen;
    static struct_tilemap layer0;

    /**
     * *********************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     * Pandora's Palace has one 32x8 palette PROM and two 256x4 lookup table
     * PROMs (one for characters, one for sprites). The palette PROM is
     * connected to the RGB output this way:
     *
     * bit 7 -- 220 ohm resistor -- BLUE -- 470 ohm resistor -- BLUE -- 220 ohm
     * resistor -- GREEN -- 470 ohm resistor -- GREEN -- 1 kohm resistor --
     * GREEN -- 220 ohm resistor -- RED -- 470 ohm resistor -- RED bit 0 -- 1
     * kohm resistor -- RED
     *
     **************************************************************************
     */
    static int TOTAL_COLORS(int gfxn) {
        return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
    }
    public static VhConvertColorPromPtr pandoras_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            int p_inc = 0;
            for (i = 0; i < Machine.drv.total_colors; i++) {
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

            /* color_prom now points to the beginning of the lookup table */
 /* sprites */
            for (i = 0; i < TOTAL_COLORS(1); i++) {
                colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + i] = (char) ((color_prom.readinc()) & 0x0f);
            }

            /* characters */
            for (i = 0; i < TOTAL_COLORS(0); i++) {
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char) (((color_prom.readinc()) & 0x0f) + 0x10);
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
    public static GetTileInfoPtr get_tile_info0 = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            char attr = colorram.read(tile_index);
            SET_TILE_INFO(0, videoram.read(tile_index) + ((attr & 0x10) << 4), attr & 0x0f);
            tile_info.u32_flags = TILE_FLIPYX((attr & 0xc0) >> 6);
            tile_info.u32_priority = (attr & 0x20) >> 5;
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStartPtr pandoras_vh_start = new VhStartPtr() {
        public int handler() {
            layer0 = tilemap_create(get_tile_info0, tilemap_scan_rows, TILEMAP_OPAQUE, 8, 8, 32, 32);

            if (layer0 == null) {
                return 1;
            }

            return 0;
        }
    };

    /**
     * *************************************************************************
     *
     * Memory Handlers
     *
     **************************************************************************
     */
    public static ReadHandlerPtr pandoras_vram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return videoram.read(offset);
        }
    };

    public static ReadHandlerPtr pandoras_cram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return colorram.read(offset);
        }
    };

    public static WriteHandlerPtr pandoras_vram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (videoram.read(offset) != data) {
                tilemap_mark_tile_dirty(layer0, offset);
                videoram.write(offset, data);
            }
        }
    };

    public static WriteHandlerPtr pandoras_cram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (colorram.read(offset) != data) {
                tilemap_mark_tile_dirty(layer0, offset);
                colorram.write(offset, data);
            }
        }
    };

    public static WriteHandlerPtr pandoras_scrolly_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            tilemap_set_scrolly(layer0, 0, data);
        }
    };

    public static WriteHandlerPtr pandoras_flipscreen_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            flipscreen = data;
            tilemap_set_flip(ALL_TILEMAPS, flipscreen != 0 ? (TILEMAP_FLIPY | TILEMAP_FLIPX) : 0);
        }
    };

    /**
     * *************************************************************************
     *
     * Screen Refresh
     *
     **************************************************************************
     */
    static void draw_sprites(osd_bitmap bitmap, UBytePtr sr) {
        int offs;

        for (offs = 0; offs < 0x100; offs += 4) {
            int sx = sr.read(offs + 1);
            int sy = 240 - sr.read(offs);
            int nflipx = sr.read(offs + 3) & 0x40;
            int nflipy = sr.read(offs + 3) & 0x80;

            drawgfx(bitmap, Machine.gfx[1],
                    sr.read(offs + 2),
                    sr.read(offs + 3) & 0x0f,
                    NOT(nflipx), NOT(nflipy),
                    sx, sy,
                    Machine.visible_area, TRANSPARENCY_COLOR, 0);
        }
    }

    public static VhUpdatePtr pandoras_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            tilemap_update(layer0);
            tilemap_render(layer0);

            tilemap_draw(bitmap, layer0, 1);
            draw_sprites(bitmap, new UBytePtr(pandoras_sharedram, 0x800));
            tilemap_draw(bitmap, layer0, 0);
        }
    };
}
