/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.video.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;

public class arabian {

    static osd_bitmap tmpbitmap2;
    static /*unsigned*/ char[] u8_inverse_palette = new char[256];
    /* JB 970727 */

    public static VhConvertColorPromPtr arabian_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            int p_inc = 0;
            /* this should be very close */
            for (i = 0; i < Machine.drv.total_colors / 2; i++) {
                int on;

                on = (i & 0x08) != 0 ? 0x80 : 0xff;

                palette[p_inc++] = ((char) ((i & 0x04) != 0 ? 0xff : 0));
                palette[p_inc++] = ((char) ((i & 0x02) != 0 ? on : 0));
                palette[p_inc++] = ((char) ((i & 0x01) != 0 ? on : 0));
            }

            /* this is handmade to match the screen shot */
            palette[p_inc++] = ((char) (0x00));
            palette[p_inc++] = ((char) (0x00));	  // 0000
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0x00));
            palette[p_inc++] = ((char) (0xff));  // 0001
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0x00));
            palette[p_inc++] = ((char) (0xff));  // 0010
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0x00));
            palette[p_inc++] = ((char) (0xff));  // 0011
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0x00));  // 0100
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0xff));  // 0101
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0xff));  // 0110
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0xff));  // 0111
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0x00));
            palette[p_inc++] = ((char) (0x00));  // 1000
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0xff));  // 1001
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0x80));  // 1010
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0x00));
            palette[p_inc++] = ((char) (0xff));  // 1011
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0x00));  // 1100
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0xff));  // 1101
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0x80));  // 1110
            palette[p_inc++] = ((char) (0x00));

            palette[p_inc++] = ((char) (0xff));
            palette[p_inc++] = ((char) (0xff));  // 1111
            palette[p_inc++] = ((char) (0x00));
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
    public static VhStartPtr arabian_vh_start = new VhStartPtr() {
        public int handler() {
            int p1, p2, p3, p4, v1, v2, offs;
            int i;
            /* JB 970727 */

            if ((tmpbitmap = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                return 1;
            }

            if ((tmpbitmap2 = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                bitmap_free(tmpbitmap2);
                return 1;
            }

            /* JB 970727 */
            for (i = 0; i < Machine.drv.total_colors; i++) {
                u8_inverse_palette[Machine.pens[i]] = (char) i;
            }

            /*transform graphics data into more usable format*/
 /*which is coded like this:
	
	  byte adr+0x4000  byte adr
	  DCBA DCBA        DCBA DCBA
	
	D-bits of pixel 4
	C-bits of pixel 3
	B-bits of pixel 2
	A-bits of pixel 1
	
	after conversion :
	
	  byte adr+0x4000  byte adr
	  DDDD CCCC        BBBB AAAA
             */
            for (offs = 0; offs < 0x4000; offs++) {
                v1 = memory_region(REGION_GFX1).read(offs);
                v2 = memory_region(REGION_GFX1).read(offs + 0x4000);

                p1 = (v1 & 0x01) | ((v1 & 0x10) >> 3) | ((v2 & 0x01) << 2) | ((v2 & 0x10) >> 1);
                v1 = v1 >> 1;
                v2 = v2 >> 1;
                p2 = (v1 & 0x01) | ((v1 & 0x10) >> 3) | ((v2 & 0x01) << 2) | ((v2 & 0x10) >> 1);
                v1 = v1 >> 1;
                v2 = v2 >> 1;
                p3 = (v1 & 0x01) | ((v1 & 0x10) >> 3) | ((v2 & 0x01) << 2) | ((v2 & 0x10) >> 1);
                v1 = v1 >> 1;
                v2 = v2 >> 1;
                p4 = (v1 & 0x01) | ((v1 & 0x10) >> 3) | ((v2 & 0x01) << 2) | ((v2 & 0x10) >> 1);

                memory_region(REGION_GFX1).write(offs, p1 | (p2 << 4));
                memory_region(REGION_GFX1).write(offs + 0x4000, p3 | (p4 << 4));

            }
            return 0;
        }
    };

    /**
     * *************************************************************************
     *
     * Stop the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStopPtr arabian_vh_stop = new VhStopPtr() {
        public void handler() {
            bitmap_free(tmpbitmap2);
            bitmap_free(tmpbitmap);
        }
    };

    public static void blit_byte(/*UINT8*/int u8_x, /*UINT8*/ int u8_y, int val, int val2, /*UINT8*/ int u8_plane) {
        int p1, p2, p3, p4;

        byte dx = 1, dy = 0;

        p4 = val & 0x0f;
        p3 = (val >> 4) & 0x0f;
        p2 = val2 & 0x0f;
        p1 = (val2 >> 4) & 0x0f;

        if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
            int t;

            t = u8_x;
            u8_x = u8_y;
            u8_y = t;
            t = dx;
            dx = dy;
            dy = (byte) t;
        }
        if ((Machine.orientation & ORIENTATION_FLIP_X) != 0) {
            u8_x = u8_x ^ 0xff;
            dx = (byte) -dx;
        }
        if ((Machine.orientation & ORIENTATION_FLIP_Y) != 0) {
            u8_y = u8_y ^ 0xff;
            dy = (byte) -dy;
        }

        if ((u8_plane & 0x01) != 0) {
            if (p4 != 8) {
                tmpbitmap.line[u8_y].write(u8_x, Machine.pens[p4]);
            }
            if (p3 != 8) {
                tmpbitmap.line[u8_y + dy].write(u8_x + dx, Machine.pens[p3]);
            }
            if (p2 != 8) {
                tmpbitmap.line[u8_y + 2 * dy].write(u8_x + 2 * dx, Machine.pens[p2]);
            }
            if (p1 != 8) {
                tmpbitmap.line[u8_y + 3 * dy].write(u8_x + 3 * dx, Machine.pens[p1]);
            }
        }

        if ((u8_plane & 0x04) != 0) {
            if (p4 != 8) {
                tmpbitmap2.line[u8_y].write(u8_x, Machine.pens[16 + p4]);
            }
            if (p3 != 8) {
                tmpbitmap2.line[u8_y + dy].write(u8_x + dx, Machine.pens[16 + p3]);
            }
            if (p2 != 8) {
                tmpbitmap2.line[u8_y + 2 * dy].write(u8_x + 2 * dx, Machine.pens[16 + p2]);
            }
            if (p1 != 8) {
                tmpbitmap2.line[u8_y + 3 * dy].write(u8_x + 3 * dx, Machine.pens[16 + p1]);
            }
        }

        if (dx >= 0 && dy >= 0) {
            osd_mark_dirty(u8_x, u8_y, u8_x + 3 * dx, u8_y + 3 * dy, 0);
        } else if (dx >= 0) {
            osd_mark_dirty(u8_x, u8_y + 3 * dy, u8_x + 3 * dx, u8_y, 0);
        } else if (dy >= 0) {
            osd_mark_dirty(u8_x + 3 * dx, u8_y, u8_x, u8_y + 3 * dy, 0);
        } else {
            osd_mark_dirty(u8_x + 3 * dx, u8_y + 3 * dy, u8_x, u8_y, 0);
        }
    }

    public static void arabian_blit_area(/*UINT8*/int u8_plane, char src, /*UINT8*/ int u8_x, /*UINT8*/ int u8_y, /*UINT8*/ int u8_sx, /*UINT8*/ int u8_sy) {
        int i, j;

        for (i = 0; i <= u8_sx; i++, u8_x += 4) {
            for (j = 0; j <= u8_sy; j++) {
                blit_byte(u8_x, u8_y + j, memory_region(REGION_GFX1).read(src), memory_region(REGION_GFX1).read(src + 0x4000), u8_plane);
                src++;
            }
        }
    }

    public static WriteHandlerPtr arabian_blitter_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            spriteram.write(offset, data);

            if ((offset & 0x07) == 6) {
                /*UINT8*/
                int u8_plane, u8_x, u8_y, u8_sx, u8_sy;
                char src;

                u8_plane = spriteram.read(offset - 6) & 0xFF;
                src = (char) (spriteram.read(offset - 5) | (spriteram.read(offset - 4) << 8));
                u8_x = (spriteram.read(offset - 2) << 2) & 0xFF;
                u8_y = spriteram.read(offset - 3) & 0xFF;
                u8_sx = spriteram.read(offset - 0) & 0xFF;
                u8_sy = spriteram.read(offset - 1) & 0xFF;

                arabian_blit_area(u8_plane, src, u8_x, u8_y, u8_sx, u8_sy);
            }
        }
    };

    public static WriteHandlerPtr arabian_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int plane1, plane2, plane3, plane4;
            UBytePtr bm;

            /*UINT8*/
            int u8_x, u8_y;
            byte dx = 1, dy = 0;

            plane1 = spriteram.read(0) & 0x01;
            plane2 = spriteram.read(0) & 0x02;
            plane3 = spriteram.read(0) & 0x04;
            plane4 = spriteram.read(0) & 0x08;

            u8_x = ((offset >> 8) << 2) & 0xFF;
            u8_y = offset & 0xff;

            if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
                int t;

                t = u8_x;
                u8_x = u8_y;
                u8_y = t;
                t = dx;
                dx = dy;
                dy = (byte) t;
            }
            if ((Machine.orientation & ORIENTATION_FLIP_X) != 0) {
                u8_x = u8_x ^ 0xff;
                dx = (byte) -dx;
            }
            if ((Machine.orientation & ORIENTATION_FLIP_Y) != 0) {
                u8_y = u8_y ^ 0xff;
                dy = (byte) -dy;
            }

            /* JB 970727 */
            tmpbitmap.line[u8_y].write(u8_x, u8_inverse_palette[tmpbitmap.line[u8_y].read(u8_x)]);
            tmpbitmap.line[u8_y + dy].write(u8_x + dx, u8_inverse_palette[tmpbitmap.line[u8_y + dy].read(u8_x + dx)]);
            tmpbitmap.line[u8_y + 2 * dy].write(u8_x + 2 * dx, u8_inverse_palette[tmpbitmap.line[u8_y + 2 * dy].read(u8_x + 2 * dx)]);
            tmpbitmap.line[u8_y + 3 * dy].write(u8_x + 3 * dx, u8_inverse_palette[tmpbitmap.line[u8_y + 3 * dy].read(u8_x + 3 * dx)]);
            tmpbitmap2.line[u8_y].write(u8_x, u8_inverse_palette[tmpbitmap2.line[u8_y].read(u8_x)]);
            tmpbitmap2.line[u8_y + dy].write(u8_x + dx, u8_inverse_palette[tmpbitmap2.line[u8_y + dy].read(u8_x + dx)]);
            tmpbitmap2.line[u8_y + 2 * dy].write(u8_x + 2 * dx, u8_inverse_palette[tmpbitmap2.line[u8_y + 2 * dy].read(u8_x + 2 * dx)]);
            tmpbitmap2.line[u8_y + 3 * dy].write(u8_x + 3 * dx, u8_inverse_palette[tmpbitmap2.line[u8_y + 3 * dy].read(u8_x + 3 * dx)]);

            if (plane1 != 0) {
                bm = new UBytePtr(tmpbitmap.line[u8_y], u8_x);
                bm.write(bm.read() & 0xf3);
                if ((data & 0x10) != 0) {
                    bm.write(bm.read() | 8);
                }
                if ((data & 0x01) != 0) {
                    bm.write(bm.read() | 4);
                }

                bm = new UBytePtr(tmpbitmap.line[u8_y + dy], u8_x + dx);
                bm.write(bm.read() & 0xf3);
                if ((data & 0x20) != 0) {
                    bm.write(bm.read() | 8);
                }
                if ((data & 0x02) != 0) {
                    bm.write(bm.read() | 4);
                }

                bm = new UBytePtr(tmpbitmap.line[u8_y + 2 * dy], u8_x + 2 * dx);
                bm.write(bm.read() & 0xf3);
                if ((data & 0x40) != 0) {
                    bm.write(bm.read() | 8);
                }
                if ((data & 0x04) != 0) {
                    bm.write(bm.read() | 4);
                }

                bm = new UBytePtr(tmpbitmap.line[u8_y + 3 * dy], u8_x + 3 * dx);
                bm.write(bm.read() & 0xf3);
                if ((data & 0x80) != 0) {
                    bm.write(bm.read() | 8);
                }
                if ((data & 0x08) != 0) {
                    bm.write(bm.read() | 4);
                }
            }

            if (plane2 != 0) {
                bm = new UBytePtr(tmpbitmap.line[u8_y], u8_x);
                bm.write(bm.read() & 0xfc);
                if ((data & 0x10) != 0) {
                    bm.write(bm.read() | 2);
                }
                if ((data & 0x01) != 0) {
                    bm.write(bm.read() | 1);
                }

                bm = new UBytePtr(tmpbitmap.line[u8_y + dy], u8_x + dx);
                bm.write(bm.read() & 0xfc);
                if ((data & 0x20) != 0) {
                    bm.write(bm.read() | 2);
                }
                if ((data & 0x02) != 0) {
                    bm.write(bm.read() | 1);
                }

                bm = new UBytePtr(tmpbitmap.line[u8_y + 2 * dy], u8_x + 2 * dx);
                bm.write(bm.read() & 0xfc);
                if ((data & 0x40) != 0) {
                    bm.write(bm.read() | 2);
                }
                if ((data & 0x04) != 0) {
                    bm.write(bm.read() | 1);
                }

                bm = new UBytePtr(tmpbitmap.line[u8_y + 3 * dy], u8_x + 3 * dx);
                bm.write(bm.read() & 0xfc);
                if ((data & 0x80) != 0) {
                    bm.write(bm.read() | 2);
                }
                if ((data & 0x08) != 0) {
                    bm.write(bm.read() | 1);
                }
            }

            if (plane3 != 0) {
                bm = new UBytePtr(tmpbitmap2.line[u8_y], u8_x);
                bm.write(bm.read() & 0xf3);
                if ((data & 0x10) != 0) {
                    bm.write(bm.read() | (16 + 8));
                }
                if ((data & 0x01) != 0) {
                    bm.write(bm.read() | (16 + 4));
                }

                bm = new UBytePtr(tmpbitmap2.line[u8_y + dy], u8_x + dx);
                bm.write(bm.read() & 0xf3);
                if ((data & 0x20) != 0) {
                    bm.write(bm.read() | (16 + 8));
                }
                if ((data & 0x02) != 0) {
                    bm.write(bm.read() | (16 + 4));
                }

                bm = new UBytePtr(tmpbitmap2.line[u8_y + 2 * dy], u8_x + 2 * dx);
                bm.write(bm.read() & 0xf3);
                if ((data & 0x40) != 0) {
                    bm.write(bm.read() | (16 + 8));
                }
                if ((data & 0x04) != 0) {
                    bm.write(bm.read() | (16 + 4));
                }

                bm = new UBytePtr(tmpbitmap2.line[u8_y + 3 * dy], u8_x + 3 * dx);
                bm.write(bm.read() & 0xf3);
                if ((data & 0x80) != 0) {
                    bm.write(bm.read() | (16 + 8));
                }
                if ((data & 0x08) != 0) {
                    bm.write(bm.read() | (16 + 4));
                }
            }

            if (plane4 != 0) {
                bm = new UBytePtr(tmpbitmap2.line[u8_y], u8_x);
                bm.write(bm.read() & 0xfc);
                if ((data & 0x10) != 0) {
                    bm.write(bm.read() | (16 + 2));
                }
                if ((data & 0x01) != 0) {
                    bm.write(bm.read() | (16 + 1));
                }

                bm = new UBytePtr(tmpbitmap2.line[u8_y + dy], u8_x + dx);
                bm.write(bm.read() & 0xfc);
                if ((data & 0x20) != 0) {
                    bm.write(bm.read() | (16 + 2));
                }
                if ((data & 0x02) != 0) {
                    bm.write(bm.read() | (16 + 1));
                }

                bm = new UBytePtr(tmpbitmap2.line[u8_y + 2 * dy], u8_x + 2 * dx);
                bm.write(bm.read() & 0xfc);
                if ((data & 0x40) != 0) {
                    bm.write(bm.read() | (16 + 2));
                }
                if ((data & 0x04) != 0) {
                    bm.write(bm.read() | (16 + 1));
                }

                bm = new UBytePtr(tmpbitmap2.line[u8_y + 3 * dy], u8_x + 3 * dx);
                bm.write(bm.read() & 0xfc);
                if ((data & 0x80) != 0) {
                    bm.write(bm.read() | (16 + 2));
                }
                if ((data & 0x08) != 0) {
                    bm.write(bm.read() | (16 + 1));
                }
            }

            /* JB 970727 */
            tmpbitmap.line[u8_y].write(u8_x, Machine.pens[tmpbitmap.line[u8_y].read(u8_x)]);
            tmpbitmap.line[u8_y + dy].write(u8_x + dx, Machine.pens[tmpbitmap.line[u8_y + dy].read(u8_x + dx)]);
            tmpbitmap.line[u8_y + 2 * dy].write(u8_x + 2 * dx, Machine.pens[tmpbitmap.line[u8_y + 2 * dy].read(u8_x + 2 * dx)]);
            tmpbitmap.line[u8_y + 3 * dy].write(u8_x + 3 * dx, Machine.pens[tmpbitmap.line[u8_y + 3 * dy].read(u8_x + 3 * dx)]);
            tmpbitmap2.line[u8_y].write(u8_x, Machine.pens[tmpbitmap2.line[u8_y].read(u8_x)]);
            tmpbitmap2.line[u8_y + dy].write(u8_x + dx, Machine.pens[tmpbitmap2.line[u8_y + dy].read(u8_x + dx)]);
            tmpbitmap2.line[u8_y + 2 * dy].write(u8_x + 2 * dx, Machine.pens[tmpbitmap2.line[u8_y + 2 * dy].read(u8_x + 2 * dx)]);
            tmpbitmap2.line[u8_y + 3 * dy].write(u8_x + 3 * dx, Machine.pens[tmpbitmap2.line[u8_y + 3 * dy].read(u8_x + 3 * dx)]);

            if (dx >= 0 && dy >= 0) {
                osd_mark_dirty(u8_x, u8_y, u8_x + 3 * dx, u8_y + 3 * dy, 0);
            } else if (dx >= 0) {
                osd_mark_dirty(u8_x, u8_y + 3 * dy, u8_x + 3 * dx, u8_y, 0);
            } else if (dy >= 0) {
                osd_mark_dirty(u8_x + 3 * dx, u8_y, u8_x, u8_y + 3 * dy, 0);
            } else {
                osd_mark_dirty(u8_x + 3 * dx, u8_y + 3 * dy, u8_x, u8_y, 0);
            }
        }
    };

    public static VhUpdatePtr arabian_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            copybitmap(bitmap, tmpbitmap2, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_NONE, 0);
            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_COLOR, 0);
        }
    };
}
