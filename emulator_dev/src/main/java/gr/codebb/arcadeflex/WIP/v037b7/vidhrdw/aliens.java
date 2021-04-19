/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.PALETTE_COLOR_VISIBLE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.konamiic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.old.mame.drawgfx.fillbitmap;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhStartPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhStopPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhUpdatePtr;

public class aliens {

    static int[] layer_colorbase = new int[3];
    static int sprite_colorbase;

    /**
     * *************************************************************************
     *
     * Callbacks for the K052109
     *
     **************************************************************************
     */
    public static K052109_callbackProcPtr tile_callback = new K052109_callbackProcPtr() {
        public void handler(int layer, int bank, int[] code, int[] color) {
            code[0] |= ((color[0] & 0x3f) << 8) | (bank << 14);
            color[0] = layer_colorbase[layer] + ((color[0] & 0xc0) >> 6);
        }
    };

    /**
     * *************************************************************************
     *
     * Callbacks for the K051960
     *
     **************************************************************************
     */
    public static K051960_callbackProcPtr sprite_callback = new K051960_callbackProcPtr() {
        public void handler(int[] code, int[] color, int[] priority_mask, int[] shadow) {
            /* The PROM allows for mixed priorities, where sprites would have */
 /* priority over text but not on one or both of the other two planes. */
            switch (color[0] & 0x70) {
                case 0x10:
                    priority_mask[0] = 0x00;
                    break;
                /* over ABF */
                case 0x00:
                    priority_mask[0] = 0xf0;
                    break;
                /* over AB, not F */
                case 0x40:
                    priority_mask[0] = 0xf0 | 0xcc;
                    break;
                /* over A, not BF */
                case 0x20:
                case 0x60:
                    priority_mask[0] = 0xf0 | 0xcc | 0xaa;
                    break;
                /* over -, not ABF */
                case 0x50:
                    priority_mask[0] = 0xcc;
                    break;
                /* over AF, not B */
                case 0x30:
                case 0x70:
                    priority_mask[0] = 0xcc | 0xaa;
                    break;
                /* over F, not AB */
            }
            code[0] |= (color[0] & 0x80) << 6;
            color[0] = sprite_colorbase + (color[0] & 0x0f);
            shadow[0] = 0;
            /* shadows are not used by this game */
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStartPtr aliens_vh_start = new VhStartPtr() {
        public int handler() {
            paletteram = new UBytePtr(0x400);
            if (paletteram == null) {
                return 1;
            }

            layer_colorbase[0] = 0;
            layer_colorbase[1] = 4;
            layer_colorbase[2] = 8;
            sprite_colorbase = 16;
            if (K052109_vh_start(REGION_GFX1, 0, 1, 2, 3/*NORMAL_PLANE_ORDER*/, tile_callback) != 0) {
                paletteram = null;
                return 1;
            }
            if (K051960_vh_start(REGION_GFX2, 0, 1, 2, 3/*NORMAL_PLANE_ORDER*/, sprite_callback) != 0) {
                paletteram = null;
                K052109_vh_stop();
                return 1;
            }

            return 0;
        }
    };

    public static VhStopPtr aliens_vh_stop = new VhStopPtr() {
        public void handler() {
            paletteram = null;
            K052109_vh_stop();
            K051960_vh_stop();
        }
    };

    /**
     * *************************************************************************
     *
     * Display refresh
     *
     **************************************************************************
     */
    public static VhUpdatePtr aliens_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            K052109_tilemap_update();

            palette_init_used_colors();
            K051960_mark_sprites_colors();
            palette_used_colors.or(layer_colorbase[1] * 16, PALETTE_COLOR_VISIBLE);
            if (palette_recalc() != null) {
                tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
            }

            tilemap_render(ALL_TILEMAPS);

            fillbitmap(priority_bitmap, 0, null);
            fillbitmap(bitmap, Machine.pens[layer_colorbase[1] * 16], Machine.visible_area);
            K052109_tilemap_draw(bitmap, 1, 1 << 16);
            K052109_tilemap_draw(bitmap, 2, 2 << 16);
            K052109_tilemap_draw(bitmap, 0, 4 << 16);

            K051960_sprites_draw(bitmap, -1, -1);
        }
    };
}
