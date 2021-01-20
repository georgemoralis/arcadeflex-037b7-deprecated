/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.arcadeflex.video.osd_mark_dirty;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.sound.mixer.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.ay8910.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_GFX4;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_GFX5;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.SubArrays.UShortArray;
import gr.codebb.arcadeflex.old.arcadeflex.libc_old.IntPtr;

public class dday {

    public static UBytePtr dday_videoram2 = new UBytePtr();
    public static UBytePtr dday_videoram3 = new UBytePtr();
    static int control = 0;

    static UBytePtr searchlight_image;
    static int searchlight_flipx;
    static int searchlight_enable = 0;

    public static final int BL0 = 0;
    public static final int BL1 = 1;
    public static final int BL2 = 2;
    public static final int BL3 = 3;

    static void drawgfx_shadow(osd_bitmap dest, GfxElement gfx,/*unsigned*/ int code,/*unsigned*/ int color, int sx, int sy, rectangle clip, int transparency, UBytePtr shadow_mask, UBytePtr layer_mask, int layer) {
        int ox, oy, ex, ey, y, start;
        UBytePtr sd;
        UBytePtr bm, bme;
        int/*UINT8*/ u8_col;
        IntPtr sd4;
        int col4;
        int f, shadow = 0, l;

        code %= gfx.total_elements;
        color %= gfx.total_colors;

        /* check bounds */
        ox = sx;
        oy = sy;
        ex = sx + gfx.width - 1;
        if (sx < 0) {
            sx = 0;
        }
        if (clip != null && sx < clip.min_x) {
            sx = clip.min_x;
        }
        if (ex >= dest.width) {
            ex = dest.width - 1;
        }
        if (clip != null && ex > clip.max_x) {
            ex = clip.max_x;
        }
        if (sx > ex) {
            return;
        }
        ey = sy + gfx.height - 1;
        if (sy < 0) {
            sy = 0;
        }
        if (clip != null && sy < clip.min_y) {
            sy = clip.min_y;
        }
        if (ey >= dest.height) {
            ey = dest.height - 1;
        }
        if (clip != null && ey > clip.max_y) {
            ey = clip.max_y;
        }
        if (sy > ey) {
            return;
        }

        osd_mark_dirty(sx, sy, ex, ey, 0);
        /* ASG 971011 */

        start = code * gfx.height + (sy - oy);

        if (gfx.colortable != null) /* remap colors */ {
            UShortArray paldata = new UShortArray(gfx.colortable, gfx.color_granularity * color);

            switch (transparency) {
                case TRANSPARENCY_NONE:
                    if (layer_mask != null) {
                        for (y = sy; y <= ey; y++) {
                            bm = new UBytePtr(dest.line[y]);
                            bme = new UBytePtr(bm, ex);
                            sd = new UBytePtr(gfx.gfxdata, start * gfx.line_modulo + (sx - ox));
                            for (bm.offset += sx; bm.offset <= bme.offset - 7; bm.offset += 8) {
                                shadow = (shadow_mask.readinc());
                                l = (layer_mask.readinc());

                                if (((l & 0x01) >> 0) == layer) {
                                    bm.write(0, paldata.read(sd.read(0) + ((shadow & 0x01) << 8)));
                                }
                                if (((l & 0x02) >> 1) == layer) {
                                    bm.write(1, paldata.read(sd.read(1) + ((shadow & 0x02) << 7)));
                                }
                                if (((l & 0x04) >> 2) == layer) {
                                    bm.write(2, paldata.read(sd.read(2) + ((shadow & 0x04) << 6)));
                                }
                                if (((l & 0x08) >> 3) == layer) {
                                    bm.write(3, paldata.read(sd.read(3) + ((shadow & 0x08) << 5)));
                                }
                                if (((l & 0x10) >> 4) == layer) {
                                    bm.write(4, paldata.read(sd.read(4) + ((shadow & 0x10) << 4)));
                                }
                                if (((l & 0x20) >> 5) == layer) {
                                    bm.write(5, paldata.read(sd.read(5) + ((shadow & 0x20) << 3)));
                                }
                                if (((l & 0x40) >> 6) == layer) {
                                    bm.write(6, paldata.read(sd.read(6) + ((shadow & 0x40) << 2)));
                                }
                                if (((l & 0x80) >> 7) == layer) {
                                    bm.write(7, paldata.read(sd.read(7) + ((shadow & 0x80) << 1)));
                                }
                                sd.inc(8);
                            }
                            start += 1;
                        }
                    } else {
                        for (y = sy; y <= ey; y++) {
                            bm = new UBytePtr(dest.line[y]);
                            bme = new UBytePtr(bm, ex);
                            sd = new UBytePtr(gfx.gfxdata, start * gfx.line_modulo + (sx - ox));
                            for (bm.offset += sx; bm.offset <= bme.offset - 7; bm.offset += 8) {
                                shadow = (shadow_mask.readinc());

                                bm.write(0, paldata.read(sd.read(0) + ((shadow & 0x01) << 8)));
                                bm.write(1, paldata.read(sd.read(1) + ((shadow & 0x02) << 7)));
                                bm.write(2, paldata.read(sd.read(2) + ((shadow & 0x04) << 6)));
                                bm.write(3, paldata.read(sd.read(3) + ((shadow & 0x08) << 5)));
                                bm.write(4, paldata.read(sd.read(4) + ((shadow & 0x10) << 4)));
                                bm.write(5, paldata.read(sd.read(5) + ((shadow & 0x20) << 3)));
                                bm.write(6, paldata.read(sd.read(6) + ((shadow & 0x40) << 2)));
                                bm.write(7, paldata.read(sd.read(7) + ((shadow & 0x80) << 1)));
                                sd.inc(8);
                            }
                            start += 1;
                        }
                    }
                    break;

                case TRANSPARENCY_PEN:

                    for (y = sy; y <= ey; y++) {
                        bm = new UBytePtr(dest.line[y]);
                        bme = new UBytePtr(bm, ex);
                        sd4 = new IntPtr(gfx.gfxdata, start * gfx.line_modulo + (sx - ox));
                        f = 0;
                        for (bm.offset += sx; bm.offset <= bme.offset - 3; bm.offset += 4, f ^= 1) {
                            if (f != 0) {
                                shadow >>= 4;
                            } else {
                                shadow = (shadow_mask.readinc());
                            }
                            col4 = sd4.read();//read_dword(sd4);
                            if (col4 != 0) {
                                u8_col = col4 & 0xFF;
                                if (u8_col != 0) {
                                    bm.write(BL0, paldata.read(u8_col + ((shadow & 0x01) << 8)));
                                }
                                u8_col = (col4 >> 8) & 0xFF;
                                if (u8_col != 0) {
                                    bm.write(BL1, paldata.read(u8_col + ((shadow & 0x02) << 7)));
                                }
                                u8_col = (col4 >> 16) & 0xFF;
                                if (u8_col != 0) {
                                    bm.write(BL2, paldata.read(u8_col + ((shadow & 0x04) << 6)));
                                }
                                u8_col = (col4 >> 24) & 0xFF;
                                if (u8_col != 0) {
                                    bm.write(BL3, paldata.read(u8_col + ((shadow & 0x08) << 5)));
                                }
                            }
                            sd4.inc();
                        }
                        start += 1;
                    }
                    break;
            }
        }
    }

    /**
     * *************************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     **************************************************************************
     */
    public static VhConvertColorPromPtr dday_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i, total;

            total = Machine.drv.total_colors / 2;
            int p_ptr = 0;
            for (i = 0; i < total; i++) {
                int bit0, bit1, bit2, bit3, r, g, b;

                /* red component */
                bit0 = (color_prom.read(0) >> 0) & 0x01;
                bit1 = (color_prom.read(0) >> 1) & 0x01;
                bit2 = (color_prom.read(0) >> 2) & 0x01;
                bit3 = (color_prom.read(0) >> 3) & 0x01;
                r = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
                /* green component */
                bit0 = (color_prom.read(total) >> 0) & 0x01;
                bit1 = (color_prom.read(total) >> 1) & 0x01;
                bit2 = (color_prom.read(total) >> 2) & 0x01;
                bit3 = (color_prom.read(total) >> 3) & 0x01;
                g = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
                /* blue component */
                bit0 = (color_prom.read(2 * total) >> 0) & 0x01;
                bit1 = (color_prom.read(2 * total) >> 1) & 0x01;
                bit2 = (color_prom.read(2 * total) >> 2) & 0x01;
                bit3 = (color_prom.read(2 * total) >> 3) & 0x01;
                b = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

                palette[p_ptr + 0] = (char) (r & 0xFF);
                palette[p_ptr + 1] = (char) (g & 0xFF);
                palette[p_ptr + 2] = (char) (b & 0xFF);

                /* darker version for searchlight */
                palette[p_ptr + (3 * 256)] = (char) ((r >>> 3) & 0xFF);
                palette[p_ptr + (3 * 256 + 1)] = (char) ((g >>> 3) & 0xFF);
                palette[p_ptr + (3 * 256 + 2)] = (char) ((b >>> 3) & 0xFF);

                p_ptr += 3;//palette += 3;

                color_prom.inc();
            }

            /* HACK!!! This table is handgenerated, but it matches the screenshot.
		   I have no clue how it really works */
            colortable[0 * 4 + 0] = 0;
            colortable[0 * 4 + 1] = 1;
            colortable[0 * 4 + 2] = 21;
            colortable[0 * 4 + 3] = 2;

            colortable[1 * 4 + 0] = 4;
            colortable[1 * 4 + 1] = 5;
            colortable[1 * 4 + 2] = 3;
            colortable[1 * 4 + 3] = 7;

            colortable[2 * 4 + 0] = 8;
            colortable[2 * 4 + 1] = 21;
            colortable[2 * 4 + 2] = 10;
            colortable[2 * 4 + 3] = 3;

            colortable[3 * 4 + 0] = 8;
            colortable[3 * 4 + 1] = 21;
            colortable[3 * 4 + 2] = 10;
            colortable[3 * 4 + 3] = 3;

            colortable[4 * 4 + 0] = 16;
            colortable[4 * 4 + 1] = 17;
            colortable[4 * 4 + 2] = 18;
            colortable[4 * 4 + 3] = 7;

            colortable[5 * 4 + 0] = 29;
            colortable[5 * 4 + 1] = 21;
            colortable[5 * 4 + 2] = 22;
            colortable[5 * 4 + 3] = 27;

            colortable[6 * 4 + 0] = 29;
            colortable[6 * 4 + 1] = 21;
            colortable[6 * 4 + 2] = 26;
            colortable[6 * 4 + 3] = 27;

            colortable[7 * 4 + 0] = 29;
            colortable[7 * 4 + 1] = 2;
            colortable[7 * 4 + 2] = 4;
            colortable[7 * 4 + 3] = 27;

            for (i = 0; i < 8 * 4; i++) {
                colortable[i + 256] = (char) (colortable[i] + 256);
            }
        }
    };

    public static void dday_decode() {
        int i;
        UBytePtr mask = memory_region(REGION_GFX4);
        char/*UINT8*/ data;

        /* create x-flipped search light mask */
        for (i = 0x1000; i < 0x1800; i++) {
            data = mask.read(i);

            mask.write(i + 0x800, ((data >> 7) & 0x01) | ((data >> 5) & 0x02)
                    | ((data >> 3) & 0x04) | ((data >> 1) & 0x08)
                    | ((data << 1) & 0x10) | ((data << 3) & 0x20)
                    | ((data << 5) & 0x40) | ((data << 7) & 0x80));
        }
    }

    public static WriteHandlerPtr dday_colorram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            colorram.write(offset & 0x3e0, data);
        }
    };

    public static ReadHandlerPtr dday_colorram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return colorram.read(offset & 0x3e0);
        }
    };

    public static WriteHandlerPtr dday_searchlight_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            searchlight_image = new UBytePtr(memory_region(REGION_GFX4), 0x200 * (data & 0x07));
            searchlight_flipx = (data >> 3) & 0x01;
        }
    };

    public static WriteHandlerPtr dday_control_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            //logerror("Control = %02X\n", data);

            /* Bit 0 is coin counter 1 */
            coin_counter_w.handler(0, data & 0x01);

            /* Bit 1 is coin counter 2 */
            coin_counter_w.handler(1, data & 0x02);

            /* Bit 4 is sound enable */
            if ((data & 0x10) == 0 && (control & 0x10) != 0) {
                AY8910_reset(0);
                AY8910_reset(1);
            }

            mixer_sound_enable_global_w(data & 0x10);

            /* Bit 6 is search light enable */
            searchlight_enable = data & 0x40;

            control = data;
        }
    };

    /**
     * *************************************************************************
     *
     * Draw the game screen in the given osd_bitmap. Do NOT call
     * osd_update_display() from this function, it will be called by the main
     * emulation engine.
     *
     **************************************************************************
     */
    public static VhUpdatePtr dday_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
                int code, code_background, sx, sy, flipx;
                UBytePtr searchlight_bitmap;

                sy = (offs / 32);
                sx = (offs % 32);

                flipx = 0;
                code = 0;

                /* draw the search light, if enabled */
                if (searchlight_enable != 0) {
                    flipx = (sx >> 4) & 0x01;
                    code = searchlight_image.read((sy << 4) | (flipx != 0 ? sx ^ 0x1f : sx));

                    if (searchlight_flipx != flipx) {
                        if ((code & 0x80) != 0) {
                            /* No mirroring, draw dark spot */
                            code = 1;
                        }
                    }

                    code &= 0x3f;
                }

                searchlight_bitmap = new UBytePtr(memory_region(REGION_GFX4), (flipx != 0 ? 0x1800 : 0x1000) | (code << 3));

                sx *= 8;
                sy *= 8;

                code_background = videoram.read(offs);

                flipx = colorram.read(sy << 2) & 0x01;
                code = dday_videoram3.read(flipx != 0 ? offs ^ 0x1f : offs);

                /* is the vehicle layer character non-blank? */
                if (code != 0) {
                    UBytePtr layer_bitmap;

                    layer_bitmap = new UBytePtr(memory_region(REGION_GFX5), code_background << 3);

                    /* draw part of background appearing behind the vehicles
				   skipping characters totally in the foreground */
                    if (layer_bitmap.read(0) != 0 || layer_bitmap.read(1) != 0 || layer_bitmap.read(2) != 0 || layer_bitmap.read(3) != 0
                            || layer_bitmap.read(4) != 0 || layer_bitmap.read(5) != 0 || layer_bitmap.read(6) != 0 || layer_bitmap.read(7) != 0) {
                        drawgfx_shadow(bitmap, Machine.gfx[0],
                                code_background,
                                code_background >> 5,
                                sx, sy,
                                Machine.visible_area, TRANSPARENCY_NONE,
                                searchlight_bitmap,
                                layer_bitmap, 1);
                    }

                    /* draw vehicles */
                    drawgfx_shadow(bitmap, Machine.gfx[flipx != 0 ? 3 : 2],
                            code,
                            code >> 5,
                            sx, sy,
                            Machine.visible_area, TRANSPARENCY_PEN,
                            searchlight_bitmap,
                            null, 0);

                    /* draw part of background appearing in front of the vehicles
				   skipping characters totally in the background */
                    if (~layer_bitmap.read(0) != 0 || ~layer_bitmap.read(1) != 0 || ~layer_bitmap.read(2) != 0 || ~layer_bitmap.read(3) != 0
                            || ~layer_bitmap.read(4) != 0 || ~layer_bitmap.read(5) != 0 || ~layer_bitmap.read(6) != 0 || ~layer_bitmap.read(7) != 0) {
                        drawgfx_shadow(bitmap, Machine.gfx[0],
                                code_background,
                                code_background >> 5,
                                sx, sy,
                                Machine.visible_area, TRANSPARENCY_NONE,
                                searchlight_bitmap,
                                layer_bitmap, 0);
                    }
                } else {
                    /* draw background, we don't have to worry about the layering */
                    drawgfx_shadow(bitmap, Machine.gfx[0],
                            code_background,
                            code_background >> 5,
                            sx, sy,
                            Machine.visible_area, TRANSPARENCY_NONE,
                            searchlight_bitmap,
                            null, 0);
                }

                /* draw text layer */
                code = dday_videoram2.read(offs);

                if (code != 0) {
                    drawgfx_shadow(bitmap, Machine.gfx[1],
                            code,
                            code >> 5,
                            sx, sy,
                            Machine.visible_area, TRANSPARENCY_PEN,
                            searchlight_bitmap,
                            null, 0);
                }
            }
        }
    };
}
