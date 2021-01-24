/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import gr.codebb.arcadeflex.common.libc.cstring.*;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UShortPtr;
import gr.codebb.arcadeflex.common.SubArrays.UShortArray;
import static gr.codebb.arcadeflex.common.libc.cstring.memcpy;
import static gr.codebb.arcadeflex.common.libc.expressions.NOT;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.cpu_gethorzbeampos;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.cpu_getscanline;
import gr.codebb.arcadeflex.old.sound.mixer;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.ORIENTATION_FLIP_X;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.ORIENTATION_FLIP_Y;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.ORIENTATION_SWAP_XY;

public class exerion {

    static final int BACKGROUND_X_START = 32;
    static final int BACKGROUND_X_START_FLIP = 72;

    static final int VISIBLE_X_MIN = (12 * 8);
    static final int VISIBLE_X_MAX = (52 * 8);
    static final int VISIBLE_Y_MIN = (2 * 8);
    static final int VISIBLE_Y_MAX = (30 * 8);

    public static int/*UINT8*/ exerion_cocktail_flip;

    static int/*UINT8*/ u8_char_palette, u8_sprite_palette;
    static int/*UINT8*/ u8_char_bank;

    static UBytePtr background_latches;
    static UShortPtr[] background_gfx = new UShortPtr[4];
    static char[] /*UINT8*/ u8_current_latches = new char[16];
    static int last_scanline_update;

    static UBytePtr background_mixer;

    /**
     * *************************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     * The palette PROM is connected to the RGB output this way:
     *
     * bit 7 -- 220 ohm resistor -- BLUE -- 470 ohm resistor -- BLUE -- 220 ohm
     * resistor -- GREEN -- 470 ohm resistor -- GREEN -- 1 kohm resistor --
     * GREEN -- 220 ohm resistor -- RED -- 470 ohm resistor -- RED bit 0 -- 1
     * kohm resistor -- RED
     *
     **************************************************************************
     */
    public static VhConvertColorPromPtr exerion_vh_convert_color_prom = new VhConvertColorPromPtr() {
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

            /* color_prom now points to the beginning of the char lookup table */
 /* fg chars */
            for (i = 0; i < 256; i++) {
                colortable[i + 0x000] = (char) (16 + (color_prom.read((i & 0xc0) | ((i & 3) << 4) | ((i >> 2) & 15)) & 15));
            }
            color_prom.inc(256);

            /* color_prom now points to the beginning of the sprite lookup table */
 /* sprites */
            for (i = 0; i < 256; i++) {
                colortable[i + 0x100] = (char) (16 + (color_prom.read((i & 0xc0) | ((i & 3) << 4) | ((i >> 2) & 15)) & 15));
            }
            color_prom.inc(256);

            /* bg chars (this is not the full story... there are four layers mixed */
 /* using another PROM */
            for (i = 0; i < 256; i++) {
                colortable[i + 0x200] = (char) (color_prom.readinc() & 15);
            }
        }
    };

    /**
     * ***********************************
     *
     * Video system startup
     *
     ************************************
     */
    public static VhStartPtr exerion_vh_start = new VhStartPtr() {
        public int handler() {
            UShortPtr dst;
            UBytePtr src;
            int i, x, y;

            /* get pointers to the mixing and lookup PROMs */
            background_mixer = new UBytePtr(memory_region(REGION_PROMS), 0x320);

            /* allocate memory to track the background latches */
            background_latches = new UBytePtr(Machine.drv.screen_height * 16);

            /* allocate memory for the decoded background graphics */
            background_gfx[0] = new UShortPtr(2 * 256 * 256 * 4);
            background_gfx[1] = new UShortPtr(background_gfx[0], 256 * 256);
            background_gfx[2] = new UShortPtr(background_gfx[1], 256 * 256);
            background_gfx[3] = new UShortPtr(background_gfx[2], 256 * 256);
            if (background_gfx[0] == null) {
                background_latches = null;
                return 1;
            }

            /*---------------------------------
		 * Decode the background graphics
		 *
		 * We decode the 4 background layers separately, but shuffle the bits so that
		 * we can OR all four layers together. Each layer has 2 bits per pixel. Each
		 * layer is decoded into the following bit patterns:
		 *
		 *	000a 0000 00AA
		 *  00b0 0000 BB00
		 *  0c00 00CC 0000
		 *  d000 DD00 0000
		 *
		 * Where AA,BB,CC,DD are the 2bpp data for the pixel,and a,b,c,d are the OR
		 * of these two bits together.
             */
            for (i = 0; i < 4; i++) {
                src = new UBytePtr(memory_region(REGION_GFX3), i * 0x2000);
                dst = new UShortPtr(background_gfx[i]);

                for (y = 0; y < 256; y++) {
                    for (x = 0; x < 128; x += 4) {
                        int/*UINT8*/ data = src.readinc();
                        int/*UINT16*/ val;

                        val = ((data >> 3) & 2) | ((data >> 0) & 1);
                        if (val != 0) {
                            val |= 0x100 >> i;
                        }
                        dst.writeinc((char) (val << (2 * i)));

                        val = ((data >> 4) & 2) | ((data >> 1) & 1);
                        if (val != 0) {
                            val |= 0x100 >> i;
                        }
                        dst.writeinc((char) (val << (2 * i)));

                        val = ((data >> 5) & 2) | ((data >> 2) & 1);
                        if (val != 0) {
                            val |= 0x100 >> i;
                        }
                        dst.writeinc((char) (val << (2 * i)));

                        val = ((data >> 6) & 2) | ((data >> 3) & 1);
                        if (val != 0) {
                            val |= 0x100 >> i;
                        }
                        dst.writeinc((char) (val << (2 * i)));
                    }
                    for (x = 0; x < 128; x++) {
                        dst.writeinc((char) 0);
                    }
                }
            }

            return generic_vh_start.handler();
        }
    };

    /**
     * ***********************************
     *
     * Video system shutdown
     *
     ************************************
     */
    public static VhStopPtr exerion_vh_stop = new VhStopPtr() {
        public void handler() {
            /* free the background graphics data */
            if (background_gfx[0] != null) {
                background_gfx[0] = null;
            }
            background_gfx[0] = null;
            background_gfx[1] = null;
            background_gfx[2] = null;
            background_gfx[3] = null;

            /* free the background latches data */
            if (background_latches != null) {
                background_latches = null;
            }

            generic_vh_stop.handler();
        }
    };

    /**
     * ***********************************
     *
     * Video register I/O
     *
     ************************************
     */
    public static WriteHandlerPtr exerion_videoreg_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* bit 0 = flip screen and joystick input multiplexor */
            exerion_cocktail_flip = data & 1;

            /* bits 1-2 char lookup table bank */
            u8_char_palette = ((data & 0x06) >> 1) & 0xFF;

            /* bits 3 char bank */
            u8_char_bank = ((data & 0x08) >> 3) & 0xFF;

            /* bits 4-5 unused */
 /* bits 6-7 sprite lookup table bank */
            u8_sprite_palette = ((data & 0xc0) >> 6) & 0xFF;
        }
    };

    public static WriteHandlerPtr exerion_video_latch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int ybeam = cpu_getscanline();

            if (ybeam >= Machine.drv.screen_height) {
                ybeam = Machine.drv.screen_height - 1;
            }

            /* copy data up to and including the current scanline */
            while (ybeam != last_scanline_update) {
                last_scanline_update = (last_scanline_update + 1) % Machine.drv.screen_height;
                memcpy(background_latches, last_scanline_update * 16, u8_current_latches, 16);
            }

            /* modify data on the current scanline */
            if (offset != -1) {
                u8_current_latches[offset] = (char) (data & 0xFF);
            }
        }
    };

    public static ReadHandlerPtr exerion_video_timing_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            /* bit 1 is VBLANK */
 /* bit 0 is the SNMI signal, which is low for H >= 0x1c0 and /VBLANK */

            int xbeam = cpu_gethorzbeampos();
            int ybeam = cpu_getscanline();
            int /*UINT8*/ result = 0;

            if (ybeam >= VISIBLE_Y_MAX) {
                result |= 2;
            }
            if (xbeam < 0x1c0 && ybeam < VISIBLE_Y_MAX) {
                result |= 1;
            }

            return result & 0xFF;
        }
    };

    /**
     * ***********************************
     *
     * Core refresh routine
     *
     ************************************
     */
    public static VhUpdatePtr exerion_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int sx, sy, offs, i;

            /* finish updating the scanlines */
            exerion_video_latch_w.handler(-1, 0);

            /* draw background */
            if (bitmap.depth == 8) {
                draw_background_8(bitmap);
            } else {
                throw new UnsupportedOperationException("unsupported");
                //draw_background_16(bitmap);
            }

            /* draw sprites */
            for (i = 0; i < spriteram_size[0]; i += 4) {
                int flags = spriteram.read(i + 0);
                int y = spriteram.read(i + 1) ^ 255;
                int code = spriteram.read(i + 2);
                int x = spriteram.read(i + 3) * 2 + 72;

                int xflip = flags & 0x80;
                int yflip = flags & 0x40;
                int doubled = flags & 0x10;
                int wide = flags & 0x08;
                int code2 = code;

                int color = ((flags >> 1) & 0x03) | ((code >> 5) & 0x04) | (code & 0x08) | (u8_sprite_palette * 16);
                GfxElement gfx = doubled != 0 ? Machine.gfx[2] : Machine.gfx[1];

                if (exerion_cocktail_flip != 0) {
                    x = 64 * 8 - gfx.width - x;
                    y = 32 * 8 - gfx.height - y;
                    if (wide != 0) {
                        y -= gfx.height;
                    }
                    xflip = NOT(xflip);
                    yflip = NOT(yflip);
                }

                if (wide != 0) {
                    if (yflip != 0) {
                        code |= 0x10;
                        code2 &= ~0x10;
                    } else {
                        code &= ~0x10;
                        code2 |= 0x10;
                    }

                    drawgfx(bitmap, gfx, code2, color, xflip, yflip, x, y + gfx.height,
                            Machine.visible_area, TRANSPARENCY_COLOR, 16);
                }

                drawgfx(bitmap, gfx, code, color, xflip, yflip, x, y,
                        Machine.visible_area, TRANSPARENCY_COLOR, 16);

                if (doubled != 0) {
                    i += 4;
                }
            }

            /* draw the visible text layer */
            for (sy = VISIBLE_Y_MIN / 8; sy < VISIBLE_Y_MAX / 8; sy++) {
                for (sx = VISIBLE_X_MIN / 8; sx < VISIBLE_X_MAX / 8; sx++) {
                    int x = exerion_cocktail_flip != 0 ? (63 * 8 - 8 * sx) : 8 * sx;
                    int y = exerion_cocktail_flip != 0 ? (31 * 8 - 8 * sy) : 8 * sy;

                    offs = sx + sy * 64;
                    drawgfx(bitmap, Machine.gfx[0],
                            videoram.read(offs) + 256 * u8_char_bank,
                            ((videoram.read(offs) & 0xf0) >> 4) + u8_char_palette * 16,
                            exerion_cocktail_flip, exerion_cocktail_flip, x, y,
                            Machine.visible_area, TRANSPARENCY_PEN, 0);
                }
            }
        }
    };

    /**
     * ***********************************
     *
     * Depth-specific refresh
     *
     ************************************
     */
    /*TODO*///	#define ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, x, y, xadv)	
/*TODO*///		if (orientation != 0)													
/*TODO*///		{																	
/*TODO*///			int dy = bitmap.line[1] - bitmap.line[0];						
/*TODO*///			int tx = x, ty = y, temp;										
/*TODO*///			if ((orientation & ORIENTATION_SWAP_XY) != 0)							
/*TODO*///			{																
/*TODO*///				temp = tx; tx = ty; ty = temp;								
/*TODO*///				xadv = dy / (bitmap.depth / 8);							
/*TODO*///			}																
/*TODO*///			if ((orientation & ORIENTATION_FLIP_X) != 0)							
/*TODO*///			{																
/*TODO*///				tx = bitmap.width - 1 - tx;								
/*TODO*///				if (!(orientation & ORIENTATION_SWAP_XY)) xadv = -xadv;		
/*TODO*///			}																
/*TODO*///			if ((orientation & ORIENTATION_FLIP_Y) != 0)							
/*TODO*///			{																
/*TODO*///				ty = bitmap.height - 1 - ty;								
/*TODO*///				if ((orientation & ORIENTATION_SWAP_XY)) xadv = -xadv;		
/*TODO*///			}																
/*TODO*///			/* can't lookup line because it may be negative! */				
/*TODO*///			dst = (TYPE *)(bitmap.line[0] + dy * ty) + tx;					
/*TODO*///		}
/*TODO*///	
/*TODO*///	#define INCLUDE_DRAW_CORE
/*TODO*///	
/*TODO*///	#define DRAW_FUNC draw_background_8
/*TODO*///	#define TYPE UINT8
/*TODO*///	#undef TYPE
/*TODO*///	#undef DRAW_FUNC
/*TODO*///	
/*TODO*///	#define DRAW_FUNC draw_background_16
/*TODO*///	#define TYPE UINT16
/*TODO*///	#undef TYPE
/*TODO*///	#undef DRAW_FUNC
/*TODO*///	
/*TODO*///	
/*TODO*///	#else
/*TODO*///	
    /**
     * ***********************************
     *
     * Background rendering
     *
     ************************************
     */
    static void draw_background_8(osd_bitmap bitmap) {
        UBytePtr latches = new UBytePtr(background_latches, VISIBLE_Y_MIN * 16);
        int orientation = Machine.orientation;
        int x, y;

        /* loop over all visible scanlines */
        for (y = VISIBLE_Y_MIN; y < VISIBLE_Y_MAX; y++, latches.inc(16)) {
            UShortPtr src0 = new UShortPtr(background_gfx[0], latches.read(1) * 256);
            UShortPtr src1 = new UShortPtr(background_gfx[1], latches.read(3) * 256);
            UShortPtr src2 = new UShortPtr(background_gfx[2], latches.read(5) * 256);
            UShortPtr src3 = new UShortPtr(background_gfx[3], latches.read(7) * 256);
            int xoffs0 = latches.read(0);
            int xoffs1 = latches.read(2);
            int xoffs2 = latches.read(4);
            int xoffs3 = latches.read(6);
            int start0 = latches.read(8) & 0x0f;
            int start1 = latches.read(9) & 0x0f;
            int start2 = latches.read(10) & 0x0f;
            int start3 = latches.read(11) & 0x0f;
            int stop0 = latches.read(8) >> 4;
            int stop1 = latches.read(9) >> 4;
            int stop2 = latches.read(10) >> 4;
            int stop3 = latches.read(11) >> 4;
            UShortArray pens = new UShortArray(Machine.remapped_colortable, 0x200 + (latches.read(12) >> 4) * 16);
            UBytePtr mixer = new UBytePtr(background_mixer, (latches.read(12) << 4) & 0xf0);
            UBytePtr dst = new UBytePtr(bitmap.line[y], VISIBLE_X_MIN);
            int xadv = 1;

            /* adjust in case we're oddly oriented */
            if (orientation != 0) {
                int dy = bitmap.line[1].offset - bitmap.line[0].offset;
                int tx = VISIBLE_X_MIN, ty = y, temp;
                if ((orientation & ORIENTATION_SWAP_XY) != 0) {
                    temp = tx;
                    tx = ty;
                    ty = temp;
                    xadv = dy / (bitmap.depth / 8);
                }
                if ((orientation & ORIENTATION_FLIP_X) != 0) {
                    tx = bitmap.width - 1 - tx;
                    if ((orientation & ORIENTATION_SWAP_XY) == 0) {
                        xadv = -xadv;
                    }
                }
                if ((orientation & ORIENTATION_FLIP_Y) != 0) {
                    ty = bitmap.height - 1 - ty;
                    if (((orientation & ORIENTATION_SWAP_XY)) != 0) {
                        xadv = -xadv;
                    }
                }
                /* can't lookup line because it may be negative! */
                dst = new UBytePtr(bitmap.line[0], (dy * ty) + tx);
            }

            /* the cocktail flip flag controls whether we could up or down in X */
            if (exerion_cocktail_flip == 0) {
                /* skip processing anything that's not visible */
                for (x = BACKGROUND_X_START; x < VISIBLE_X_MIN; x++) {
                    if ((++xoffs0 & 0x1f) == 0) {
                        start0++;
                        stop0++;
                    }
                    if ((++xoffs1 & 0x1f) == 0) {
                        start1++;
                        stop1++;
                    }
                    if ((++xoffs2 & 0x1f) == 0) {
                        start2++;
                        stop2++;
                    }
                    if ((++xoffs3 & 0x1f) == 0) {
                        start3++;
                        stop3++;
                    }
                }

                /* draw the rest of the scanline fully */
                for (x = VISIBLE_X_MIN; x < VISIBLE_X_MAX; x++, dst.inc(xadv)) {
                    int/*UINT16*/ combined = 0;
                    int/*UINT8*/ lookupval, colorindex;

                    /* the output enable is controlled by the carries on the start/stop counters */
 /* they are only active when the start has carried but the stop hasn't */
                    if (((start0 ^ stop0) & 0x10) != 0) {
                        combined |= src0.read(xoffs0 & 0xff);
                    }
                    if (((start1 ^ stop1) & 0x10) != 0) {
                        combined |= src1.read(xoffs1 & 0xff);
                    }
                    if (((start2 ^ stop2) & 0x10) != 0) {
                        combined |= src2.read(xoffs2 & 0xff);
                    }
                    if (((start3 ^ stop3) & 0x10) != 0) {
                        combined |= src3.read(xoffs3 & 0xff);
                    }

                    /* bits 8-11 of the combined value contains the lookup for the mixer PROM */
                    lookupval = mixer.read(combined >> 8) & 3;

                    /* the color index comes from the looked up value combined with the pixel data */
                    colorindex = (lookupval << 2) | ((combined >> (2 * lookupval)) & 3);
                    dst.write(pens.read(colorindex));

                    /* the start/stop counters are clocked when the low 5 bits of the X counter overflow */
                    if ((++xoffs0 & 0x1f) == 0) {
                        start0++;
                        stop0++;
                    }
                    if ((++xoffs1 & 0x1f) == 0) {
                        start1++;
                        stop1++;
                    }
                    if ((++xoffs2 & 0x1f) == 0) {
                        start2++;
                        stop2++;
                    }
                    if ((++xoffs3 & 0x1f) == 0) {
                        start3++;
                        stop3++;
                    }
                }
            } else {
                /* skip processing anything that's not visible */
                for (x = BACKGROUND_X_START; x < VISIBLE_X_MIN; x++) {
                    if ((xoffs0-- & 0x1f) == 0) {
                        start0++;
                        stop0++;
                    }
                    if ((xoffs1-- & 0x1f) == 0) {
                        start1++;
                        stop1++;
                    }
                    if ((xoffs2-- & 0x1f) == 0) {
                        start2++;
                        stop2++;
                    }
                    if ((xoffs3-- & 0x1f) == 0) {
                        start3++;
                        stop3++;
                    }
                }

                /* draw the rest of the scanline fully */
                for (x = VISIBLE_X_MIN; x < VISIBLE_X_MAX; x++, dst.inc(xadv)) {
                    int/*UINT16*/ combined = 0;
                    int/*UINT8*/ lookupval, colorindex;

                    /* the output enable is controlled by the carries on the start/stop counters */
 /* they are only active when the start has carried but the stop hasn't */
                    if (((start0 ^ stop0) & 0x10) != 0) {
                        combined |= src0.read(xoffs0 & 0xff);
                    }
                    if (((start1 ^ stop1) & 0x10) != 0) {
                        combined |= src1.read(xoffs1 & 0xff);
                    }
                    if (((start2 ^ stop2) & 0x10) != 0) {
                        combined |= src2.read(xoffs2 & 0xff);
                    }
                    if (((start3 ^ stop3) & 0x10) != 0) {
                        combined |= src3.read(xoffs3 & 0xff);
                    }

                    /* bits 8-11 of the combined value contains the lookup for the mixer PROM */
                    lookupval = mixer.read(combined >> 8) & 3;

                    /* the color index comes from the looked up value combined with the pixel data */
                    colorindex = (lookupval << 2) | ((combined >> (2 * lookupval)) & 3);
                    dst.write(pens.read(colorindex));

                    /* the start/stop counters are clocked when the low 5 bits of the X counter overflow */
                    if ((xoffs0-- & 0x1f) == 0) {
                        start0++;
                        stop0++;
                    }
                    if ((xoffs1-- & 0x1f) == 0) {
                        start1++;
                        stop1++;
                    }
                    if ((xoffs2-- & 0x1f) == 0) {
                        start2++;
                        stop2++;
                    }
                    if ((xoffs3-- & 0x1f) == 0) {
                        start3++;
                        stop3++;
                    }
                }
            }
        }
    }
    /*TODO*///	void DRAW_FUNC(struct osd_bitmap *bitmap)
/*TODO*///	{
/*TODO*///		UINT8 *latches = &background_latches[VISIBLE_Y_MIN * 16];
/*TODO*///		int orientation = Machine.orientation;
/*TODO*///		int x, y;
/*TODO*///	
/*TODO*///		/* loop over all visible scanlines */
/*TODO*///		for (y = VISIBLE_Y_MIN; y < VISIBLE_Y_MAX; y++, latches += 16)
/*TODO*///		{
/*TODO*///			UINT16 *src0 = &background_gfx[0][latches[1] * 256];
/*TODO*///			UINT16 *src1 = &background_gfx[1][latches[3] * 256];
/*TODO*///			UINT16 *src2 = &background_gfx[2][latches[5] * 256];
/*TODO*///			UINT16 *src3 = &background_gfx[3][latches[7] * 256];
/*TODO*///			int xoffs0 = latches[0];
/*TODO*///			int xoffs1 = latches[2];
/*TODO*///			int xoffs2 = latches[4];
/*TODO*///			int xoffs3 = latches[6];
/*TODO*///			int start0 = latches[8] & 0x0f;
/*TODO*///			int start1 = latches[9] & 0x0f;
/*TODO*///			int start2 = latches[10] & 0x0f;
/*TODO*///			int start3 = latches[11] & 0x0f;
/*TODO*///			int stop0 = latches[8] >> 4;
/*TODO*///			int stop1 = latches[9] >> 4;
/*TODO*///			int stop2 = latches[10] >> 4;
/*TODO*///			int stop3 = latches[11] >> 4;
/*TODO*///			UINT16 *pens = &Machine.remapped_colortable[0x200 + (latches[12] >> 4) * 16];
/*TODO*///			UINT8 *mixer = &background_mixer[(latches[12] << 4) & 0xf0];
/*TODO*///			TYPE *dst = &((TYPE *)bitmap.line[y])[VISIBLE_X_MIN];
/*TODO*///			int xadv = 1;
/*TODO*///	
/*TODO*///			/* adjust in case we're oddly oriented */
/*TODO*///			ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, VISIBLE_X_MIN, y, xadv);
/*TODO*///	
/*TODO*///			/* the cocktail flip flag controls whether we could up or down in X */
/*TODO*///			if (!exerion_cocktail_flip)
/*TODO*///			{
/*TODO*///				/* skip processing anything that's not visible */
/*TODO*///				for (x = BACKGROUND_X_START; x < VISIBLE_X_MIN; x++)
/*TODO*///				{
/*TODO*///					if (!(++xoffs0 & 0x1f)) start0++, stop0++;
/*TODO*///					if (!(++xoffs1 & 0x1f)) start1++, stop1++;
/*TODO*///					if (!(++xoffs2 & 0x1f)) start2++, stop2++;
/*TODO*///					if (!(++xoffs3 & 0x1f)) start3++, stop3++;
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* draw the rest of the scanline fully */
/*TODO*///				for (x = VISIBLE_X_MIN; x < VISIBLE_X_MAX; x++, dst += xadv)
/*TODO*///				{
/*TODO*///					UINT16 combined = 0;
/*TODO*///					UINT8 lookupval, colorindex;
/*TODO*///	
/*TODO*///					/* the output enable is controlled by the carries on the start/stop counters */
/*TODO*///					/* they are only active when the start has carried but the stop hasn't */
/*TODO*///					if ((start0 ^ stop0) & 0x10) combined |= src0[xoffs0 & 0xff];
/*TODO*///					if ((start1 ^ stop1) & 0x10) combined |= src1[xoffs1 & 0xff];
/*TODO*///					if ((start2 ^ stop2) & 0x10) combined |= src2[xoffs2 & 0xff];
/*TODO*///					if ((start3 ^ stop3) & 0x10) combined |= src3[xoffs3 & 0xff];
/*TODO*///	
/*TODO*///					/* bits 8-11 of the combined value contains the lookup for the mixer PROM */
/*TODO*///					lookupval = mixer[combined >> 8] & 3;
/*TODO*///	
/*TODO*///					/* the color index comes from the looked up value combined with the pixel data */
/*TODO*///					colorindex = (lookupval << 2) | ((combined >> (2 * lookupval)) & 3);
/*TODO*///					*dst = pens[colorindex];
/*TODO*///	
/*TODO*///					/* the start/stop counters are clocked when the low 5 bits of the X counter overflow */
/*TODO*///					if (!(++xoffs0 & 0x1f)) start0++, stop0++;
/*TODO*///					if (!(++xoffs1 & 0x1f)) start1++, stop1++;
/*TODO*///					if (!(++xoffs2 & 0x1f)) start2++, stop2++;
/*TODO*///					if (!(++xoffs3 & 0x1f)) start3++, stop3++;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				/* skip processing anything that's not visible */
/*TODO*///				for (x = BACKGROUND_X_START; x < VISIBLE_X_MIN; x++)
/*TODO*///				{
/*TODO*///					if (!(xoffs0-- & 0x1f)) start0++, stop0++;
/*TODO*///					if (!(xoffs1-- & 0x1f)) start1++, stop1++;
/*TODO*///					if (!(xoffs2-- & 0x1f)) start2++, stop2++;
/*TODO*///					if (!(xoffs3-- & 0x1f)) start3++, stop3++;
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* draw the rest of the scanline fully */
/*TODO*///				for (x = VISIBLE_X_MIN; x < VISIBLE_X_MAX; x++, dst += xadv)
/*TODO*///				{
/*TODO*///					UINT16 combined = 0;
/*TODO*///					UINT8 lookupval, colorindex;
/*TODO*///	
/*TODO*///					/* the output enable is controlled by the carries on the start/stop counters */
/*TODO*///					/* they are only active when the start has carried but the stop hasn't */
/*TODO*///					if ((start0 ^ stop0) & 0x10) combined |= src0[xoffs0 & 0xff];
/*TODO*///					if ((start1 ^ stop1) & 0x10) combined |= src1[xoffs1 & 0xff];
/*TODO*///					if ((start2 ^ stop2) & 0x10) combined |= src2[xoffs2 & 0xff];
/*TODO*///					if ((start3 ^ stop3) & 0x10) combined |= src3[xoffs3 & 0xff];
/*TODO*///	
/*TODO*///					/* bits 8-11 of the combined value contains the lookup for the mixer PROM */
/*TODO*///					lookupval = mixer[combined >> 8] & 3;
/*TODO*///	
/*TODO*///					/* the color index comes from the looked up value combined with the pixel data */
/*TODO*///					colorindex = (lookupval << 2) | ((combined >> (2 * lookupval)) & 3);
/*TODO*///					*dst = pens[colorindex];
/*TODO*///	
/*TODO*///					/* the start/stop counters are clocked when the low 5 bits of the X counter overflow */
/*TODO*///					if (!(xoffs0-- & 0x1f)) start0++, stop0++;
/*TODO*///					if (!(xoffs1-- & 0x1f)) start1++, stop1++;
/*TODO*///					if (!(xoffs2-- & 0x1f)) start2++, stop2++;
/*TODO*///					if (!(xoffs3-- & 0x1f)) start3++, stop3++;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	#endif
}
