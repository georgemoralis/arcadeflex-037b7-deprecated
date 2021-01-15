/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.old2.mame.common.bitmap_alloc;
import static gr.codebb.arcadeflex.old2.mame.common.bitmap_free;
import static gr.codebb.arcadeflex.old2.mame.mame.Machine;
import static gr.codebb.arcadeflex.sndhrdw.pleiads.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;

public class naughtyb {

    /* from sndhrdw/pleiads.c */
    public static UBytePtr naughtyb_videoram2 = new UBytePtr();

    static int videoreg;

    /* use these to draw charset B */
    public static UBytePtr naughtyb_scrollreg = new UBytePtr();

    /* use this to select palette */
    static /*unsigned*/ char u8_palreg;

    /* used in Naughty Boy to select video bank */
    static int bankreg;

    static rectangle scrollvisiblearea = new rectangle(
            2 * 8, 34 * 8 - 1,
            0 * 8, 28 * 8 - 1
    );

    static rectangle leftvisiblearea = new rectangle(
            0 * 8, 2 * 8 - 1,
            0 * 8, 28 * 8 - 1
    );

    static rectangle rightvisiblearea = new rectangle(
            34 * 8, 36 * 8 - 1,
            0 * 8, 28 * 8 - 1
    );

    /**
     * *************************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     * Naughty Boy has two 256x4 palette PROMs, one containing the high bits and
     * the other the low bits (2x2x2 color space). The palette PROMs are
     * connected to the RGB output this way:
     *
     * bit 3 -- -- 270 ohm resistor -- GREEN -- 270 ohm resistor -- BLUE bit 0
     * -- 270 ohm resistor -- RED
     *
     * bit 3 -- -- GREEN -- BLUE bit 0 -- RED
     *
     * plus 270 ohm pullup and pulldown resistors on all lines
     *
     **************************************************************************
     */
    public static VhConvertColorPromPtr naughtyb_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            //#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
            //#define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])

            int p_ptr = 0;
            for (i = 0; i < Machine.drv.total_colors; i++) {
                int bit0, bit1;

                bit0 = (color_prom.read(0) >> 0) & 0x01;
                bit1 = (color_prom.read(Machine.drv.total_colors) >> 0) & 0x01;
                palette[p_ptr++] = (char) (0x55 * bit0 + 0xaa * bit1);
                bit0 = (color_prom.read(0) >> 2) & 0x01;
                bit1 = (color_prom.read(Machine.drv.total_colors) >> 2) & 0x01;
                palette[p_ptr++] = (char) (0x55 * bit0 + 0xaa * bit1);
                bit0 = (color_prom.read(0) >> 1) & 0x01;
                bit1 = (color_prom.read(Machine.drv.total_colors) >> 1) & 0x01;
                palette[p_ptr++] = (char) (0x55 * bit0 + 0xaa * bit1);

                color_prom.inc();
            }

            /* first bank of characters use colors 0-31, 64-95, 128-159 and 192-223 */
            for (i = 0; i < 8; i++) {
                int j;

                for (j = 0; j < 4; j++) {
                    colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (4 * i + j * 4 * 8)] = (char) (i + j * 64);
                    colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (4 * i + j * 4 * 8 + 1)] = (char) (8 + i + j * 64);
                    colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (4 * i + j * 4 * 8 + 2)] = (char) (2 * 8 + i + j * 64);
                    colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + (4 * i + j * 4 * 8 + 3)] = (char) (3 * 8 + i + j * 64);
                }
            }

            /* second bank of characters use colors 32-63, 96-127, 160-191 and 224-255 */
            for (i = 0; i < 8; i++) {
                int j;

                for (j = 0; j < 4; j++) {
                    colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + (4 * i + j * 4 * 8)] = (char) (i + 32 + j * 64);
                    colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + (4 * i + j * 4 * 8 + 1)] = (char) (8 + i + 32 + j * 64);
                    colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + (4 * i + j * 4 * 8 + 2)] = (char) (2 * 8 + i + 32 + j * 64);
                    colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + (4 * i + j * 4 * 8 + 3)] = (char) (3 * 8 + i + 32 + j * 64);
                }
            }
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VhStartPtr naughtyb_vh_start = new VhStartPtr() {
        public int handler() {
            videoreg = 0;
            u8_palreg = 0;
            bankreg = 0;

            /* Naughty Boy has a virtual screen twice as large as the visible screen */
            if ((dirtybuffer = new char[videoram_size[0]]) == null) {
                return 1;
            }
            memset(dirtybuffer, 1, videoram_size[0]);

            if ((tmpbitmap = bitmap_alloc(68 * 8, 28 * 8)) == null) {
                dirtybuffer = null;
                return 1;
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
    public static VhStopPtr naughtyb_vh_stop = new VhStopPtr() {
        public void handler() {
            bitmap_free(tmpbitmap);
            dirtybuffer = null;
        }
    };

    public static WriteHandlerPtr naughtyb_videoram2_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (naughtyb_videoram2.read(offset) != data) {
                dirtybuffer[offset] = 1;

                naughtyb_videoram2.write(offset, data);
            }
        }
    };

    public static WriteHandlerPtr naughtyb_videoreg_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bits 4+5 control the sound circuit */
            pleiads_sound_control_c_w.handler(offset, data);

            if ((videoreg & 0x0f) != (data & 0x0f)) {
                videoreg = data;

                u8_palreg = (char) ((data >> 1) & 0x03);
                /* pallette sel is bit 1 & 2 */
                bankreg = (data >> 2) & 0x01;
                /* banksel is just bit 2 */

                memset(dirtybuffer, 1, videoram_size[0]);
            }
        }
    };

    public static WriteHandlerPtr popflame_videoreg_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bits 4+5 control the sound circuit */
            pleiads_sound_control_c_w.handler(offset, data);

            if ((videoreg & 0x0f) != (data & 0x0f)) {
                videoreg = data;

                u8_palreg = (char) ((data >> 1) & 0x03);
                /* pallette sel is bit 1 & 2 */
                bankreg = (data >> 3) & 0x01;
                /* banksel is just bit 3 */

                memset(dirtybuffer, 1, videoram_size[0]);
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
     * The Naughty Boy screen is split into two sections by the hardware
     *
     * NonScrolled = 28x4 - (rows 0,1,34,35, as shown below) this area is split
     * between the top and bottom of the screen, and the address mapping is
     * really funky.
     *
     * Scrolled = 28x64, with a 28x32 viewport, as shown below Each column in
     * the virtual screen is 64 (40h) characters high. Thus, column 27 is stored
     * in VRAm at address 0-3fh, column 26 is stored at 40-7f, and so on. This
     * illustration shows the horizonal scroll register set to zero, so the
     * topmost 32 rows of the virtual screen are shown.
     *
     * The following screen-to-memory mapping. This is shown from player's
     * viewpoint, which with the CRT rotated 90 degrees CCW. This example shows
     * the horizonal scroll register set to zero.
     *
     *
     * COLUMN 0 1 2 - 25 26 27 ------------------------------- 0| 76E 76A 762 -
     * 70A 706 702 | | | Nonscrolled display 1| 76F 76B 762 - 70B 707 703 |
     * |-------------------------------| ----- 2| 6C0 680 640 - 80 40 00 | | | R
     * 3| 6C1 681 641 - 81 41 01 | O | | 28 x 32 viewport W || | | | into 28x64
     * virtual, | | scrollable screen 32| 6DE 69E 65E 9E 5E 1E | | | 33| 6DF 69F
     * 65F - 9F 5F 1F | |-------------------------------| ----- 34| 76C 768 764
     * 708 704 700 | | | Nonscrolled display 35| 76D 769 765 709 705 701 |
     * -------------------------------
     *
     *
     **************************************************************************
     */
    public static VhUpdatePtr naughtyb_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            /* for every character in the Video RAM, check if it has been modified */
 /* since last time and update it accordingly. */
            for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
                if (dirtybuffer[offs] != 0) {
                    int sx, sy;

                    dirtybuffer[offs] = 0;

                    if (offs < 0x700) {
                        sx = offs % 64;
                        sy = offs / 64;
                    } else {
                        sx = 64 + (offs - 0x700) % 4;
                        sy = (offs - 0x700) / 4;
                    }

                    drawgfx(tmpbitmap, Machine.gfx[0],
                            naughtyb_videoram2.read(offs) + 256 * bankreg,
                            (naughtyb_videoram2.read(offs) >> 5) + 8 * u8_palreg,
                            0, 0,
                            8 * sx, 8 * sy,
                            null, TRANSPARENCY_NONE, 0);

                    drawgfx(tmpbitmap, Machine.gfx[1],
                            videoram.read(offs) + 256 * bankreg,
                            (videoram.read(offs) >> 5) + 8 * u8_palreg,
                            0, 0,
                            8 * sx, 8 * sy,
                            null, TRANSPARENCY_PEN, 0);
                }
            }

            /* copy the temporary bitmap to the screen */
            {
                int scrollx;

                copybitmap(bitmap, tmpbitmap, 0, 0, -66 * 8, 0, leftvisiblearea, TRANSPARENCY_NONE, 0);
                copybitmap(bitmap, tmpbitmap, 0, 0, -30 * 8, 0, rightvisiblearea, TRANSPARENCY_NONE, 0);

                scrollx = -naughtyb_scrollreg.read() + 16;
                copyscrollbitmap(bitmap, tmpbitmap, 1, new int[]{scrollx}, 0, null, scrollvisiblearea, TRANSPARENCY_NONE, 0);
            }
        }
    };
}
