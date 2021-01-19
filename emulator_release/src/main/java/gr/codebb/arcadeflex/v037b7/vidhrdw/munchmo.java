/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_GFX2;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.copyscrollbitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class munchmo {

    public static UBytePtr mnchmobl_vreg = new UBytePtr();
    public static UBytePtr mnchmobl_status_vram = new UBytePtr();
    public static UBytePtr mnchmobl_sprite_xpos = new UBytePtr();
    public static UBytePtr mnchmobl_sprite_attr = new UBytePtr();
    public static UBytePtr mnchmobl_sprite_tile = new UBytePtr();

    static int mnchmobl_palette_bank;
    static int flipscreen;

    public static VhConvertColorPromPtr mnchmobl_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            int p_ptr = 0;
            for (i = 0; i < Machine.drv.total_colors; i++) {
                int bit0, bit1, bit2;

                /* red component */
                bit0 = (color_prom.read() >> 0) & 0x01;
                bit1 = (color_prom.read() >> 1) & 0x01;
                bit2 = (color_prom.read() >> 2) & 0x01;
                palette[p_ptr++] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                /* green component */
                bit0 = (color_prom.read() >> 3) & 0x01;
                bit1 = (color_prom.read() >> 4) & 0x01;
                bit2 = (color_prom.read() >> 5) & 0x01;
                palette[p_ptr++] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                /* blue component */
                bit0 = (color_prom.read() >> 6) & 0x01;
                bit1 = (color_prom.read() >> 7) & 0x01;
                palette[p_ptr++] = (char) (0x4f * bit0 + 0xa8 * bit1);

                color_prom.inc();
            }
        }
    };

    public static WriteHandlerPtr mnchmobl_palette_bank_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (mnchmobl_palette_bank != (data & 0x3)) {
                memset(dirtybuffer, 1, 0x100);
                mnchmobl_palette_bank = data & 0x3;
            }
        }
    };

    public static WriteHandlerPtr mnchmobl_flipscreen_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (flipscreen != data) {
                memset(dirtybuffer, 1, 0x100);
                flipscreen = data;
            }
        }
    };

    public static ReadHandlerPtr mnchmobl_sprite_xpos_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return mnchmobl_sprite_xpos.read(offset);
        }
    };
    public static WriteHandlerPtr mnchmobl_sprite_xpos_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            mnchmobl_sprite_xpos.write(offset, data);
        }
    };

    public static ReadHandlerPtr mnchmobl_sprite_attr_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return mnchmobl_sprite_attr.read(offset);
        }
    };
    public static WriteHandlerPtr mnchmobl_sprite_attr_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            mnchmobl_sprite_attr.write(offset, data);
        }
    };

    public static ReadHandlerPtr mnchmobl_sprite_tile_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return mnchmobl_sprite_tile.read(offset);
        }
    };
    public static WriteHandlerPtr mnchmobl_sprite_tile_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            mnchmobl_sprite_tile.write(offset, data);
        }
    };

    public static VhStopPtr mnchmobl_vh_stop = new VhStopPtr() {
        public void handler() {
            if (tmpbitmap != null) {
                bitmap_free(tmpbitmap);
            }
            dirtybuffer = null;
        }
    };

    public static VhStartPtr mnchmobl_vh_start = new VhStartPtr() {
        public int handler() {
            dirtybuffer = new char[0x100];
            tmpbitmap = bitmap_alloc(512, 512);
            if (dirtybuffer != null && tmpbitmap != null) {
                memset(dirtybuffer, 1, 0x100);
                return 0;
            }
            mnchmobl_vh_stop.handler();
            return 1;
        }
    };

    public static ReadHandlerPtr mnchmobl_videoram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return videoram.read(offset);
        }
    };

    public static WriteHandlerPtr mnchmobl_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            offset = offset & 0xff;
            /* mirror the two banks? */
            if (videoram.read(offset) != data) {
                videoram.write(offset, data);
                dirtybuffer[offset] = 1;
            }
        }
    };

    static void draw_status(osd_bitmap bitmap) {
        rectangle clip = Machine.visible_area;
        GfxElement gfx = Machine.gfx[0];
        int row;

        for (row = 0; row < 4; row++) {
            int sy, sx = (row & 1) * 8;
            UBytePtr source = new UBytePtr(mnchmobl_status_vram, (row & 1) * 32);
            if (row <= 1) {
                source.inc(2 * 32);
                sx += 256 + 32 + 16;
            }
            for (sy = 0; sy < 256; sy += 8) {
                drawgfx(bitmap, gfx,
                        source.readinc(),
                        0, /* color */
                        0, 0, /* no flip */
                        sx, sy,
                        clip,
                        TRANSPARENCY_NONE, 0);
            }
        }
    }

    static void draw_background(osd_bitmap bitmap) {
        /*
		ROM B1.2C contains 256 tilemaps defining 4x4 configurations of
		the tiles in ROM B2.2B
         */
        UBytePtr tile_data = memory_region(REGION_GFX2);
        GfxElement gfx = Machine.gfx[1];
        int offs;

        for (offs = 0; offs < 0x100; offs++) {
            if (dirtybuffer[offs] != 0) {
                int sy = (offs % 16) * 32;
                int sx = (offs / 16) * 32;
                int tile_number = videoram.read(offs);
                int row, col;
                dirtybuffer[offs] = 0;
                for (row = 0; row < 4; row++) {
                    for (col = 0; col < 4; col++) {
                        drawgfx(tmpbitmap, gfx,
                                tile_data.read(col + tile_number * 4 + row * 0x400),
                                mnchmobl_palette_bank,
                                0, 0, /* flip */
                                sx + col * 8, sy + row * 8,
                                null, TRANSPARENCY_NONE, 0);
                    }
                }
            }
        }

        {
            int scrollx = -(mnchmobl_vreg.read(6) * 2 + (mnchmobl_vreg.read(7) >> 7)) - 64 - 128 - 16;
            int scrolly = 0;

            copyscrollbitmap(bitmap, tmpbitmap,
                    1, new int[]{scrollx}, 1, new int[]{scrolly},
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
        }
    }

    static void draw_sprites(osd_bitmap bitmap) {
        rectangle clip = Machine.visible_area;
        int scroll = mnchmobl_vreg.read(6);
        int flags = mnchmobl_vreg.read(7);
        /*   XB?????? */
        int xadjust = - 128 - 16 - ((flags & 0x80) != 0 ? 1 : 0);
        int bank = (flags & 0x40) != 0 ? 1 : 0;
        GfxElement gfx = Machine.gfx[2 + bank];
        int color_base = mnchmobl_palette_bank * 4 + 3;
        int i;
        for (i = 0; i < 0x200; i++) {
            int tile_number = mnchmobl_sprite_tile.read(i);
            /*   ETTTTTTT */
            int attributes = mnchmobl_sprite_attr.read(i);
            /*   XYYYYYCC */
            int sx = mnchmobl_sprite_xpos.read(i);
            /*   XXXXXXX? */
            int sy = (i / 0x40) * 0x20;
            /* Y YY------ */
            sy += (attributes >> 2) & 0x1f;
            if (tile_number != 0xff && (attributes & 0x80) != 0) {
                sx = (sx >> 1) | (tile_number & 0x80);
                sx = 2 * ((-32 - scroll - sx) & 0xff) + xadjust;
                drawgfx(bitmap, gfx,
                        0x7f - (tile_number & 0x7f),
                        color_base - (attributes & 0x03),
                        0, 0, /* no flip */
                        sx, sy,
                        clip, TRANSPARENCY_PEN, 7);
            }
        }
    }

    public static VhUpdatePtr mnchmobl_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            draw_background(bitmap);
            draw_sprites(bitmap);
            draw_status(bitmap);
        }
    };
}
