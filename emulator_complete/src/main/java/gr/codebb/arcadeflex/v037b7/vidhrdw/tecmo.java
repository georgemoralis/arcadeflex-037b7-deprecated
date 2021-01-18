/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;

public class tecmo {

    public static UBytePtr tecmo_txvideoram = new UBytePtr();
    public static UBytePtr tecmo_fgvideoram = new UBytePtr();
    public static UBytePtr tecmo_bgvideoram = new UBytePtr();

    public static int tecmo_video_type = 0;
    /*
	   video_type is used to distinguish Rygar, Silkworm and Gemini Wing.
	   This is needed because there is a difference in the tile and sprite indexing.
     */

    static struct_tilemap tx_tilemap, fg_tilemap, bg_tilemap;

    /**
     * *************************************************************************
     *
     * Callbacks for the TileMap code
     *
     **************************************************************************
     */
    public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attr = tecmo_bgvideoram.read(tile_index + 0x200);
            SET_TILE_INFO(3, tecmo_bgvideoram.read(tile_index) + ((attr & 0x07) << 8), attr >> 4);
        }
    };

    public static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attr = tecmo_fgvideoram.read(tile_index + 0x200);
            SET_TILE_INFO(2, tecmo_fgvideoram.read(tile_index) + ((attr & 0x07) << 8), attr >> 4);
        }
    };

    public static GetTileInfoPtr gemini_get_bg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attr = tecmo_bgvideoram.read(tile_index + 0x200);
            SET_TILE_INFO(3, tecmo_bgvideoram.read(tile_index) + ((attr & 0x70) << 4), attr & 0x0f);
        }
    };

    public static GetTileInfoPtr gemini_get_fg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attr = tecmo_fgvideoram.read(tile_index + 0x200);
            SET_TILE_INFO(2, tecmo_fgvideoram.read(tile_index) + ((attr & 0x70) << 4), attr & 0x0f);
        }
    };

    public static GetTileInfoPtr get_tx_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attr = tecmo_txvideoram.read(tile_index + 0x400);
            SET_TILE_INFO(0, tecmo_txvideoram.read(tile_index) + ((attr & 0x03) << 8), attr >> 4);
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStartPtr tecmo_vh_start = new VhStartPtr() {
        public int handler() {
            if (tecmo_video_type == 2) /* gemini */ {
                bg_tilemap = tilemap_create(gemini_get_bg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 16, 16, 32, 16);
                fg_tilemap = tilemap_create(gemini_get_fg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 16, 16, 32, 16);
            } else /* rygar, silkworm */ {
                bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 16, 16, 32, 16);
                fg_tilemap = tilemap_create(get_fg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 16, 16, 32, 16);
            }
            tx_tilemap = tilemap_create(get_tx_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);

            if (bg_tilemap == null || fg_tilemap == null || tx_tilemap == null) {
                return 1;
            }

            bg_tilemap.transparent_pen = 0;
            fg_tilemap.transparent_pen = 0;
            tx_tilemap.transparent_pen = 0;
            /* 0x100 is the background color */
            palette_transparent_color = 0x100;

            tilemap_set_scrolldx(bg_tilemap, -48, 256 + 48);
            tilemap_set_scrolldx(fg_tilemap, -48, 256 + 48);

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
    public static WriteHandlerPtr tecmo_txvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (tecmo_txvideoram.read(offset) != data) {
                tecmo_txvideoram.write(offset, data);
                tilemap_mark_tile_dirty(tx_tilemap, offset & 0x3ff);
            }
        }
    };

    public static WriteHandlerPtr tecmo_fgvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (tecmo_fgvideoram.read(offset) != data) {
                tecmo_fgvideoram.write(offset, data);
                tilemap_mark_tile_dirty(fg_tilemap, offset & 0x1ff);
            }
        }
    };

    public static WriteHandlerPtr tecmo_bgvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (tecmo_bgvideoram.read(offset) != data) {
                tecmo_bgvideoram.write(offset, data);
                tilemap_mark_tile_dirty(bg_tilemap, offset & 0x1ff);
            }
        }
    };
    static char[]/*UINT8*/ scroll_1 = new char[3];
    public static WriteHandlerPtr tecmo_fgscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            scroll_1[offset] = (char) (data & 0xFF);

            tilemap_set_scrollx(fg_tilemap, 0, scroll_1[0] + 256 * scroll_1[1]);
            tilemap_set_scrolly(fg_tilemap, 0, scroll_1[2]);
        }
    };
    static char[]/*UINT8*/ scroll_2 = new char[3];
    public static WriteHandlerPtr tecmo_bgscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            scroll_2[offset] = (char) (data & 0xFF);

            tilemap_set_scrollx(bg_tilemap, 0, scroll_2[0] + 256 * scroll_2[1]);
            tilemap_set_scrolly(bg_tilemap, 0, scroll_2[2]);
        }
    };

    public static WriteHandlerPtr tecmo_flipscreen_w = new WriteHandlerPtr() {
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
        int layout[][]
                = {
                    {0, 1, 4, 5, 16, 17, 20, 21},
                    {2, 3, 6, 7, 18, 19, 22, 23},
                    {8, 9, 12, 13, 24, 25, 28, 29},
                    {10, 11, 14, 15, 26, 27, 30, 31},
                    {32, 33, 36, 37, 48, 49, 52, 53},
                    {34, 35, 38, 39, 50, 51, 54, 55},
                    {40, 41, 44, 45, 56, 57, 60, 61},
                    {42, 43, 46, 47, 58, 59, 62, 63}
                };

        for (offs = spriteram_size[0] - 8; offs >= 0; offs -= 8) {
            int flags = spriteram.read(offs + 3);
            int priority = flags >> 6;
            int bank = spriteram.read(offs + 0);
            if ((bank & 4) != 0) {
                /* visible */
                int which = spriteram.read(offs + 1);
                int code, xpos, ypos, flipx, flipy, priority_mask, x, y;
                int size = spriteram.read(offs + 2) & 3;

                if (tecmo_video_type != 0) /* gemini, silkworm */ {
                    code = which + ((bank & 0xf8) << 5);
                } else /* rygar */ {
                    code = which + ((bank & 0xf0) << 4);
                }

                code &= ~((1 << (size * 2)) - 1);
                size = 1 << size;

                xpos = spriteram.read(offs + 5) - ((flags & 0x10) << 4);
                ypos = spriteram.read(offs + 4) - ((flags & 0x20) << 3);
                flipx = bank & 1;
                flipy = bank & 2;

                if (flip_screen() != 0) {
                    xpos = 256 - (8 * size) - xpos;
                    ypos = 256 - (8 * size) - ypos;
                    flipx = NOT(flipx);
                    flipy = NOT(flipy);
                }

                /* bg: 1; fg:2; text: 4 */
                switch (priority) {
                    default:
                    case 0x0:
                        priority_mask = 0;
                        break;
                    case 0x1:
                        priority_mask = 0xf0;
                        break;
                    /* obscured by text layer */
                    case 0x2:
                        priority_mask = 0xf0 | 0xcc;
                        break;/* obscured by foreground */
                    case 0x3:
                        priority_mask = 0xf0 | 0xcc | 0xaa;
                        break;
                    /* obscured by bg and fg */
                }

                for (y = 0; y < size; y++) {
                    for (x = 0; x < size; x++) {
                        int sx = xpos + 8 * (flipx != 0 ? (size - 1 - x) : x);
                        int sy = ypos + 8 * (flipy != 0 ? (size - 1 - y) : y);
                        pdrawgfx(bitmap, Machine.gfx[1],
                                code + layout[y][x],
                                flags & 0xf,
                                flipx, flipy,
                                sx, sy,
                                Machine.visible_area, TRANSPARENCY_PEN, 0,
                                priority_mask);
                    }
                }
            }
        }
    }

    static void mark_sprite_colors() {
        int i;
        char[] palette_map = new char[16];
        int pal_base;

        memset(palette_map, 0, sizeof(palette_map));

        for (i = 0; i < spriteram_size[0]; i += 8) {
            int color;

            color = spriteram.read(i + 3) & 0x0f;
            palette_map[color] |= 0xffff;
        }

        /* now build the final table */
        pal_base = Machine.drv.gfxdecodeinfo[1].color_codes_start;
        for (i = 0; i < 16; i++) {
            int usage = palette_map[i], j;
            if (usage != 0) {
                for (j = 1; j < 16; j++) {
                    if ((usage & (1 << j)) != 0) {
                        palette_used_colors.or(pal_base + i * 16 + j, PALETTE_COLOR_VISIBLE);
                    }
                }
            }
        }
    }

    public static VhUpdatePtr tecmo_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            tilemap_update(ALL_TILEMAPS);

            palette_init_used_colors();
            mark_sprite_colors();
            palette_used_colors.write(0x100, PALETTE_COLOR_USED);

            if (palette_recalc() != null) {
                tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
            }

            tilemap_render(ALL_TILEMAPS);

            fillbitmap(priority_bitmap, 0, null);
            fillbitmap(bitmap, Machine.pens[0x100], Machine.visible_area);
            tilemap_draw(bitmap, bg_tilemap, 1 << 16);
            tilemap_draw(bitmap, fg_tilemap, 2 << 16);
            tilemap_draw(bitmap, tx_tilemap, 4 << 16);

            draw_sprites(bitmap);
        }
    };
}
