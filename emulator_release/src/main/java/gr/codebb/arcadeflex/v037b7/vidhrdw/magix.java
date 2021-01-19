/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.old.arcadeflex.video.osd_clearbitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class magix {

    /* Variables that driver has access to: */
    public static UBytePtr magix_videoram_0;
    public static UBytePtr magix_videoram_1;

    /* Variables only used here: */
    static struct_tilemap tilemap_0, tilemap_1;
    static int magix_videobank;

    /**
     * *************************************************************************
     *
     * Memory Handlers
     *
     **************************************************************************
     */
    public static WriteHandlerPtr magix_videobank_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            magix_videobank = data;
        }
    };

    public static ReadHandlerPtr magix_videoram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int bank;

            /*	Bit 1 of the bankswitching register contols the c000-c7ff
			area (Palette). Bit 0 controls the c800-dfff area (Tiles) */
            if (offset < 0x0800) {
                bank = magix_videobank & 2;
            } else {
                bank = magix_videobank & 1;
            }

            if (bank != 0) {
                return magix_videoram_0.read(offset);
            } else {
                return magix_videoram_1.read(offset);
            }
        }
    };

    public static WriteHandlerPtr magix_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (offset < 0x0800) // c000-c7ff	Banked Palette RAM
            {
                int bank = magix_videobank & 2;
                UBytePtr RAM;
                int r, g, b;

                if (bank != 0) {
                    RAM = magix_videoram_0;
                } else {
                    RAM = magix_videoram_1;
                }

                RAM.write(offset, data);
                data = RAM.read(offset & ~1) | (RAM.read(offset | 1) << 8);

                /* BBBBBGGGGGRRRRRx */
                r = (data >> 0) & 0x1f;
                g = (data >> 5) & 0x1f;
                b = (data >> 10) & 0x1f;

                palette_change_color(offset / 2 + (bank != 0 ? 0x400 : 0), (r << 3) | (r >> 2), (g << 3) | (g >> 2), (b << 3) | (b >> 2));
            } else {
                int tile;
                int bank = magix_videobank & 1;

                if (offset < 0x1000) {
                    tile = (offset - 0x0800);		// c800-cfff: Banked Color RAM
                } else {
                    tile = (offset - 0x1000) / 2;	// d000-dfff: Banked Tiles RAM
                }
                if (bank != 0) {
                    magix_videoram_0.write(offset, data);
                    tilemap_mark_tile_dirty(tilemap_0, tile);
                } else {
                    magix_videoram_1.write(offset, data);
                    tilemap_mark_tile_dirty(tilemap_1, tile);
                }
            }
        }
    };

    public static WriteHandlerPtr magix_flipscreen_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            tilemap_set_flip(ALL_TILEMAPS, (data & 1) != 0 ? (TILEMAP_FLIPX | TILEMAP_FLIPY) : 0);
        }
    };

    /**
     * *************************************************************************
     *
     * Callbacks for the TileMap code
     *
     **************************************************************************
     */
    /**
     * *************************************************************************
     *
     * [ Tiles Format ]
     *
     * Offset:
     *
     * Videoram + 0000.w	Code Colorram + 0000.b	Color
     *
     *
     **************************************************************************
     */
    /* Background */
    public static final int DIM_NX_0 = (0x40);
    public static final int DIM_NY_0 = (0x20);

    public static GetTileInfoPtr get_tile_info_0 = new GetTileInfoPtr() {
        @Override
        public void handler(int tile_index) {
            int code = magix_videoram_0.read(0x1000 + tile_index * 2 + 0) + magix_videoram_0.read(0x1000 + tile_index * 2 + 1) * 256;
            int color = magix_videoram_0.read(0x0800 + tile_index) & 0x07;
            SET_TILE_INFO(0, code, color);
        }
    };

    /* Text Plane */
    public static final int DIM_NX_1 = (0x40);
    public static final int DIM_NY_1 = (0x20);

    public static GetTileInfoPtr get_tile_info_1 = new GetTileInfoPtr() {
        @Override
        public void handler(int tile_index) {
            int code = magix_videoram_1.read(0x1000 + tile_index * 2 + 0) + magix_videoram_1.read(0x1000 + tile_index * 2 + 1) * 256;
            int color = magix_videoram_1.read(0x0800 + tile_index) & 0x3f;
            SET_TILE_INFO(1, code, color);
        }
    };

    /**
     * *************************************************************************
     *
     *
     * Vh_Start
     *
     *
     **************************************************************************
     */
    public static VhStartPtr magix_vh_start = new VhStartPtr() {
        public int handler() {
            tilemap_0 = tilemap_create(get_tile_info_0,
                    tilemap_scan_rows,
                    TILEMAP_OPAQUE,
                    8, 8,
                    DIM_NX_0, DIM_NY_0);

            tilemap_1 = tilemap_create(get_tile_info_1,
                    tilemap_scan_rows,
                    TILEMAP_TRANSPARENT,
                    8, 8,
                    DIM_NX_1, DIM_NY_1);

            if (tilemap_0 != null && tilemap_1 != null) {
                tilemap_1.transparent_pen = 0;
                return 0;
            } else {
                return 1;
            }
        }
    };

    /**
     * *************************************************************************
     *
     *
     * Screen Drawing
     *
     *
     **************************************************************************
     */
    public static VhUpdatePtr magix_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int layers_ctrl = -1;

            tilemap_update(ALL_TILEMAPS);

            palette_init_used_colors();

            /* No Sprites ... */
            if (palette_recalc() != null) {
                tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
            }

            tilemap_render(ALL_TILEMAPS);

            if ((layers_ctrl & 1) != 0) {
                tilemap_draw(bitmap, tilemap_0, 0);
            } else {
                osd_clearbitmap(bitmap);
            }

            if ((layers_ctrl & 2) != 0) {
                tilemap_draw(bitmap, tilemap_1, 0);
            }
        }
    };
}
