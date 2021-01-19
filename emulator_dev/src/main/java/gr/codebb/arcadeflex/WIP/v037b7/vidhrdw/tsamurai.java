/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class tsamurai {

    public static UBytePtr tsamurai_videoram = new UBytePtr();
    static int bgcolor;
    static int textbank;

    static struct_tilemap background, foreground;

    /*
	** color prom decoding
     */
    public static VhConvertColorPromPtr tsamurai_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            int p_inc = 0;
            for (i = 0; i < Machine.drv.total_colors; i++) {
                int bit0, bit1, bit2, bit3;

                /* red component */
                bit0 = (color_prom.read(0) >> 0) & 0x01;
                bit1 = (color_prom.read(0) >> 1) & 0x01;
                bit2 = (color_prom.read(0) >> 2) & 0x01;
                bit3 = (color_prom.read(0) >> 3) & 0x01;
                palette[p_inc++] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                /* green component */
                bit0 = (color_prom.read(Machine.drv.total_colors) >> 0) & 0x01;
                bit1 = (color_prom.read(Machine.drv.total_colors) >> 1) & 0x01;
                bit2 = (color_prom.read(Machine.drv.total_colors) >> 2) & 0x01;
                bit3 = (color_prom.read(Machine.drv.total_colors) >> 3) & 0x01;
                palette[p_inc++] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                /* blue component */
                bit0 = (color_prom.read(2 * Machine.drv.total_colors) >> 0) & 0x01;
                bit1 = (color_prom.read(2 * Machine.drv.total_colors) >> 1) & 0x01;
                bit2 = (color_prom.read(2 * Machine.drv.total_colors) >> 2) & 0x01;
                bit3 = (color_prom.read(2 * Machine.drv.total_colors) >> 3) & 0x01;
                palette[p_inc++] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);

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
    public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attributes = tsamurai_videoram.read(2 * tile_index + 1);
            int color = (attributes & 0x1f);
            SET_TILE_INFO(0, tsamurai_videoram.read(2 * tile_index) + 4 * (attributes & 0xc0), color);
        }
    };

    public static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            int tile_number = videoram.read(tile_index);
            if ((textbank & 1) != 0) {
                tile_number += 256;
            }
            SET_TILE_INFO(1, tile_number, colorram.read(((tile_index & 0x1f) * 2) + 1) & 0x1f);
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStartPtr tsamurai_vh_start = new VhStartPtr() {
        public int handler() {
            background = tilemap_create(get_bg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);
            foreground = tilemap_create(get_fg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);

            if (background == null || foreground == null) {
                return 1;
            }

            background.transparent_pen = 0;
            foreground.transparent_pen = 0;
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
    public static WriteHandlerPtr tsamurai_scrolly_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            tilemap_set_scrolly(background, 0, data);
        }
    };

    public static WriteHandlerPtr tsamurai_scrollx_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            tilemap_set_scrollx(background, 0, data);
        }
    };

    public static WriteHandlerPtr tsamurai_bgcolor_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            bgcolor = data;
        }
    };

    public static WriteHandlerPtr tsamurai_textbank_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (textbank != data) {
                textbank = data;
                tilemap_mark_all_tiles_dirty(foreground);
            }
        }
    };

    public static WriteHandlerPtr tsamurai_bg_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (tsamurai_videoram.read(offset) != data) {
                tsamurai_videoram.write(offset, data);
                offset = offset / 2;
                tilemap_mark_tile_dirty(background, offset);
            }
        }
    };
    public static WriteHandlerPtr tsamurai_fg_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (videoram.read(offset) != data) {
                videoram.write(offset, data);
                tilemap_mark_tile_dirty(foreground, offset);
            }
        }
    };
    public static WriteHandlerPtr tsamurai_fg_colorram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (colorram.read(offset) != data) {
                colorram.write(offset, data);
                if ((offset & 1) != 0) {
                    int col = offset / 2;
                    int row;
                    for (row = 0; row < 32; row++) {
                        tilemap_mark_tile_dirty(foreground, 32 * row + col);
                    }
                }
            }
        }
    };

    /**
     * *************************************************************************
     *
     * Display refresh
     *
     **************************************************************************
     */
    static int flicker;

    static void draw_sprites(osd_bitmap bitmap) {
        GfxElement gfx = Machine.gfx[2];
        rectangle clip = new rectangle(Machine.visible_area);
        UBytePtr source = new UBytePtr(spriteram, 32 * 4 - 4);
        int finish = spriteram.offset;
        /* ? */

        flicker = 1 - flicker;

        while (source.offset >= finish) {
            int attributes = source.read(2);
            /* bit 0x10 is usually, but not always set */

            int sx = source.read(3) - 16;
            int sy = 240 - source.read(0);
            int sprite_number = source.read(1);
            int color = attributes & 0x1f;
            //color = 0x2d - color; nunchakun fix?
            if (sy < -16) {
                sy += 256;
            }

            if (flip_screen() != 0) {
                drawgfx(bitmap, gfx,
                        sprite_number & 0x7f,
                        color,
                        1, (sprite_number & 0x80) != 0 ? 0 : 1,
                        256 - 32 - sx, 256 - 32 - sy,
                        clip, TRANSPARENCY_PEN, 0);
            } else {
                drawgfx(bitmap, gfx,
                        sprite_number & 0x7f,
                        color,
                        0, sprite_number & 0x80,
                        sx, sy,
                        clip, TRANSPARENCY_PEN, 0);
            }

            source.dec(4);
        }
    }

    public static VhUpdatePtr tsamurai_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            tilemap_update(ALL_TILEMAPS);
            tilemap_render(ALL_TILEMAPS);

            /*
			This following isn't particularly efficient.  We'd be better off to
			dynamically change every 8th palette to the background color, so we
			could draw the background as an opaque tilemap.
	
			Note that the background color register isn't well understood
			(screenshots would be helpful)
             */
            fillbitmap(bitmap, Machine.pens[bgcolor], null);
            tilemap_draw(bitmap, background, 0);

            draw_sprites(bitmap);

            tilemap_draw(bitmap, foreground, 0);
        }
    };
}
