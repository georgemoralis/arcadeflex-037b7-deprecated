/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_recalc;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.fillbitmap;

public class bublbobl {

    public static UBytePtr bublbobl_objectram = new UBytePtr();
    public static int[] bublbobl_objectram_size = new int[1];
    public static int bublbobl_video_enable;

    public static VhConvertColorPromPtr bublbobl_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;

            /* no color PROMs here, only RAM, but the gfx data is inverted so we */
 /* cannot use the default lookup table */
            for (i = 0; i < Machine.drv.color_table_len; i++) {
                colortable[i] = (char) (i ^ 0x0f);
            }
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
    public static VhUpdatePtr bublbobl_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;
            int sx, sy, xc, yc;
            int gfx_num, gfx_attr, gfx_offs;
            UBytePtr prom_line;

            palette_recalc();
            /* no need to check the return code since we redraw everything each frame */

 /* Bubble Bobble doesn't have a real video RAM. All graphics (characters */
 /* and sprites) are stored in the same memory region, and information on */
 /* the background character columns is stored in the area dd00-dd3f */
 /* This clears & redraws the entire screen each pass */
            fillbitmap(bitmap, Machine.pens[255], Machine.visible_area);

            if (bublbobl_video_enable == 0) {
                return;
            }

            sx = 0;

            for (offs = 0; offs < bublbobl_objectram_size[0]; offs += 4) {
                /* skip empty sprites */
 /* this is dword aligned so the UINT32 * cast shouldn't give problems */
 /* on any architecture */
                if (bublbobl_objectram.READ_DWORD(offs) == 0) {
                    continue;
                }

                gfx_num = bublbobl_objectram.read(offs + 1);
                gfx_attr = bublbobl_objectram.read(offs + 3);
                prom_line = new UBytePtr(memory_region(REGION_PROMS), 0x80 + ((gfx_num & 0xe0) >> 1));

                gfx_offs = ((gfx_num & 0x1f) * 0x80);
                if ((gfx_num & 0xa0) == 0xa0) {
                    gfx_offs |= 0x1000;
                }

                sy = -bublbobl_objectram.read(offs + 0);

                for (yc = 0; yc < 32; yc++) {
                    if ((prom_line.read(yc / 2) & 0x08) != 0) {
                        continue;	/* NEXT */
                    }

                    if ((prom_line.read(yc / 2) & 0x04) == 0) /* next column */ {
                        sx = bublbobl_objectram.read(offs + 2);
                        if ((gfx_attr & 0x40) != 0) {
                            sx -= 256;
                        }
                    }

                    for (xc = 0; xc < 2; xc++) {
                        int goffs, code, color, flipx, flipy, x, y;

                        goffs = gfx_offs + xc * 0x40 + (yc & 7) * 0x02
                                + (prom_line.read(yc / 2) & 0x03) * 0x10;
                        code = videoram.read(goffs) + 256 * (videoram.read(goffs + 1) & 0x03) + 1024 * (gfx_attr & 0x0f);
                        color = (videoram.read(goffs + 1) & 0x3c) >> 2;
                        flipx = videoram.read(goffs + 1) & 0x40;
                        flipy = videoram.read(goffs + 1) & 0x80;
                        x = sx + xc * 8;
                        y = (sy + yc * 8) & 0xff;

                        if (flip_screen() != 0) {
                            x = 248 - x;
                            y = 248 - y;
                            flipx = NOT(flipx);
                            flipy = NOT(flipy);
                        }

                        drawgfx(bitmap, Machine.gfx[0],
                                code,
                                color,
                                flipx, flipy,
                                x, y,
                                Machine.visible_area, TRANSPARENCY_PEN, 0);
                    }
                }

                sx += 16;
            }
        }
    };
}
