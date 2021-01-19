/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.CLEAR_LINE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.video.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.old.mame.usrintrf.*;
import static gr.codebb.arcadeflex.old2.arcadeflex.osd_cpuH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.cpu.z80.z80.*;
import static gr.codebb.arcadeflex.v037b7.cpu.z80.z80H.Z80_AF;
import static gr.codebb.arcadeflex.v037b7.cpu.z80.z80H.Z80_BC;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_CPU1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.input_port_0_r;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.input_port_1_r;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.*;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class astrocde {

    public static final int SCREEN_WIDTH = 320;
    public static final int MAX_LINES = 204;

    public static final int MAX_INT_PER_FRAME = 256;

    public static UBytePtr wow_videoram = new UBytePtr();
    static int magic_expand_color, magic_control, magic_expand_count, magic_shift_leftover;
    static int collision;

    /* This value affects the star layout, the value is correct since
	   it is mentioned in the docs and perfectly matches screen shots.
     */
    public static final int CLOCKS_PER_LINE = 455;

    /* This value affects the star blinking and the sparkle patterns.
	   It is just a guess, aiming to a supposed 60Hz refresh rate, and has
	   not been verified.
     */
    public static final int CLOCKS_PER_FRAME = (CLOCKS_PER_LINE * 262);

    public static final int RNG_PERIOD = 131071;
    /* 2^17-1 */
    static int[] rng;
    static int[] star;

    static int[][] colors = new int[MAX_INT_PER_FRAME][8];
    static int[] colorsplit = new int[MAX_INT_PER_FRAME];
    static int BackgroundData, VerticalBlank;

    static int[][] sparkle = new int[MAX_INT_PER_FRAME][4];
    /* sparkle[line][0] is star enable */

    public static VhConvertColorPromPtr astrocde_init_palette = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            /* This routine builds a palette using a transformation from */
 /* the YUV (Y, B-Y, R-Y) to the RGB color space */

            int i, j;

            float Y, RY, BY;
            /* Y, R-Y, and B-Y signals as generated by the game */
 /* Y = Luminance . (0 to 1) */
 /* R-Y = Color along R-Y axis . C*(-1 to +1) */
 /* B-Y = Color along B-Y axis . C*(-1 to +1) */
            float R, G, B;

            float brightest = 1.0f;
            /* Approx. Luminance values for the extremes . (0 to 1) */
            float dimmest = 0.0f;
            float C = 0.75f;
            /* Approx. Chroma intensity */

 /* The astrocade has a 256 color palette                 */
 /* 32 colors, with 8 luminance levels for each color     */
 /* The 32 colors circle around the YUV color space,      */
 /* with the exception of the first 8 which are grayscale */

 /* Note: to simulate a B&W monitor, set C=0 and all      */
 /*       colors will appear as the first 8 grayscales    */
            int p_inc = 0;
            for (i = 0; i < 32; i++) {
                RY = C * (float) Math.sin((double) (i * 2.0f * 3.14159f / 32.0f));
                if (i == 0) {
                    BY = 0;
                } else {
                    BY = C * (float) Math.cos((double) (i * 2.0 * 3.14159 / 32.0));
                }

                for (j = 0; j < 8; j++) {
                    Y = (j / 7.0f) * (brightest - dimmest) + dimmest;

                    /* Transform to RGB */
                    R = (RY + Y) * 255;
                    G = (Y - 0.299f * (RY + Y) - 0.114f * (BY + Y)) / 0.587f * 255;
                    B = (BY + Y) * 255;

                    /* Clipping, in case of saturation */
                    if (R < 0) {
                        R = 0;
                    }
                    if (R > 255) {
                        R = 255;
                    }
                    if (G < 0) {
                        G = 0;
                    }
                    if (G > 255) {
                        G = 255;
                    }
                    if (B < 0) {
                        B = 0;
                    }
                    if (B > 255) {
                        B = 255;
                    }

                    /* Round, and set the value */
                    palette[p_inc] = ((char) (Math.floor(R + .5)));
                    p_inc++;
                    palette[p_inc] = ((char) (Math.floor(G + .5)));
                    p_inc++;
                    palette[p_inc] = ((char) (Math.floor(B + .5)));
                    p_inc++;
                }
            }
        }
    };

    /**
     * **************************************************************************
     * Scanline Interrupt System
     * **************************************************************************
     */
    static int NextScanInt = 0;
    /* Normal */
    static int CurrentScan = 0;
    static int InterruptFlag = 0;

    static int GorfDelay;
    /* Gorf */
    static int Countdown = 0;

    public static WriteHandlerPtr astrocde_interrupt_enable_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            InterruptFlag = data;

            if ((data & 0x01) != 0) /* Disable Interrupts? */ {
                interrupt_enable_w.handler(0, 0);
            } else {
                interrupt_enable_w.handler(0, 1);
            }

            /* Gorf Special interrupt */
            if ((data & 0x10) != 0) {
                GorfDelay = (CurrentScan + 7) & 0xFF;

                /* Gorf Special *MUST* occur before next scanline interrupt */
                if ((NextScanInt > CurrentScan) && (NextScanInt < GorfDelay)) {
                    GorfDelay = NextScanInt - 1;
                }
            }
        }
    };

    public static WriteHandlerPtr astrocde_interrupt_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* A write to 0F triggers an interrupt at that scanline */
            NextScanInt = data;
        }
    };

    public static InterruptPtr wow_interrupt = new InterruptPtr() {
        public int handler() {
            int res, i, next;

            if (osd_skip_this_frame() == 0) {
                wow_update_line(Machine.scrbitmap, CurrentScan);
            }

            next = (CurrentScan + 1) % MAX_INT_PER_FRAME;
            for (i = 0; i < 8; i++) {
                colors[next][i] = colors[CurrentScan][i];
            }
            for (i = 0; i < 4; i++) {
                sparkle[next][i] = sparkle[CurrentScan][i];
            }
            colorsplit[next] = colorsplit[CurrentScan];

            CurrentScan = next;

            /* Scanline interrupt enabled ? */
            res = ignore_interrupt.handler();

            if ((InterruptFlag & 0x08) != 0 && (CurrentScan == NextScanInt)) {
                res = interrupt.handler();
            }

            return res;
        }
    };

    /**
     * **************************************************************************
     * Gorf - Interrupt routine and Timer hack
     * **************************************************************************
     */
    public static InterruptPtr gorf_interrupt = new InterruptPtr() {
        public int handler() {
            int res;

            res = wow_interrupt.handler();

            /* Gorf Special Bits */
            if (Countdown > 0) {
                Countdown--;
            }

            if ((InterruptFlag & 0x10) != 0 && (CurrentScan == GorfDelay)) {
                res = interrupt.handler() & 0xF0;
            }

            /*	cpu_clear_pending_interrupts(0); */
            //	Z80_Clear_Pending_Interrupts();					/* Temporary Fix */
            cpu_set_irq_line(0, 0, CLEAR_LINE);

            return res;
        }
    };

    /* ======================================================================= */
    public static ReadHandlerPtr wow_video_retrace_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return CurrentScan;
        }
    };

    public static ReadHandlerPtr wow_intercept_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int res;

            res = collision;
            collision = 0;

            return res;
        }
    };

    /* Switches color registers at this zone - 40 zones (NOT USED) */
    public static WriteHandlerPtr astrocde_colour_split_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            colorsplit[CurrentScan] = 2 * (data & 0x3f);

            BackgroundData = ((data & 0xc0) >> 6) * 0x55;
        }
    };

    /* This selects commercial (high res, arcade) or
	                  consumer (low res, astrocade) mode */
    public static WriteHandlerPtr astrocde_mode_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            //	astrocade_mode = data & 0x01;
        }
    };

    public static WriteHandlerPtr astrocde_vertical_blank_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (VerticalBlank != data) {
                VerticalBlank = data;
            }
        }
    };

    public static WriteHandlerPtr astrocde_colour_register_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            colors[CurrentScan][offset] = data;

        }
    };
    static int color_reg_num = 7;
    public static WriteHandlerPtr astrocde_colour_block_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            astrocde_colour_register_w.handler(color_reg_num, data);

            color_reg_num = (color_reg_num - 1) & 7;
        }
    };

    public static WriteHandlerPtr wow_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if ((offset < 0x4000) && (wow_videoram.read(offset) != data)) {
                wow_videoram.write(offset, data);
            }
        }
    };

    public static WriteHandlerPtr astrocde_magic_expand_color_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            magic_expand_color = data;
        }
    };

    public static WriteHandlerPtr astrocde_magic_control_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            magic_control = data;

            magic_expand_count = 0;
            /* reset flip-flop for expand mode on write to this register */
            magic_shift_leftover = 0;
            /* reset shift buffer on write to this register */

            if ((magic_control & 0x04) != 0) {
                usrintf_showmessage("unsupported MAGIC ROTATE mode");
            }
        }
    };

    static void copywithflip(int offset, int data) {
        int shift, data1;

        if ((magic_control & 0x40) != 0) /* copy backwards */ {
            int bits, stib, k;

            bits = data;
            stib = 0;
            for (k = 0; k < 4; k++) {
                stib >>= 2;
                stib |= (bits & 0xc0);
                bits <<= 2;
            }

            data = stib;
        }

        shift = magic_control & 3;
        data1 = 0;
        if ((magic_control & 0x40) != 0) /* copy backwards */ {
            while (shift > 0) {
                data1 <<= 2;
                data1 |= (data & 0xc0) >> 6;
                data <<= 2;
                shift--;
            }
        } else {
            while (shift > 0) {
                data1 >>= 2;
                data1 |= (data & 0x03) << 6;
                data >>= 2;
                shift--;
            }
        }
        data |= magic_shift_leftover;
        magic_shift_leftover = data1;

        if ((magic_control & 0x30) != 0) {
            /* TODO: the collision detection should be made */
 /* independently for each of the four pixels    */

            if (data != 0 && wow_videoram.read(offset) != 0) {
                collision |= 0xff;
            } else {
                collision &= 0x0f;
            }
        }

        if ((magic_control & 0x20) != 0) {
            data ^= wow_videoram.read(offset);
            /* draw in XOR mode */
        } else if ((magic_control & 0x10) != 0) {
            data |= wow_videoram.read(offset);
            /* draw in OR mode */
        }
        wow_videoram_w.handler(offset, data);
    }

    public static WriteHandlerPtr wow_magicram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if ((magic_control & 0x08) != 0) /* expand mode */ {
                int bits, bibits, k;

                bits = data;
                if (magic_expand_count != 0) {
                    bits <<= 4;
                }
                bibits = 0;
                for (k = 0; k < 4; k++) {
                    bibits <<= 2;
                    if ((bits & 0x80) != 0) {
                        bibits |= (magic_expand_color >> 2) & 0x03;
                    } else {
                        bibits |= magic_expand_color & 0x03;
                    }
                    bits <<= 1;
                }

                copywithflip(offset, bibits);

                magic_expand_count ^= 1;
            } else {
                copywithflip(offset, data);
            }
        }
    };
    static int src;
    static int mode;
    /*  bit 0 = direction
								bit 1 = expand mode
								bit 2 = constant
								bit 3 = flush
								bit 4 = flip
								bit 5 = flop */
    static int skip;
    /* bytes to skip after row copy */
    static int dest;
    static int length;
    /* row length */
    static int loops;
    /* rows to copy - 1 */

    public static WriteHandlerPtr astrocde_pattern_board_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            UBytePtr RAM = memory_region(REGION_CPU1);

            switch (offset) {
                case 0:
                    src = data;
                    break;
                case 1:
                    src = src + data * 256;
                    break;
                case 2:
                    mode = data & 0x3f;
                    /* register is 6 bit wide */
                    break;
                case 3:
                    skip = data;
                    break;
                case 4:
                    dest = skip + data * 256;
                    /* register 3 is shared between skip and dest */
                    break;
                case 5:
                    length = data;
                    break;
                case 6:
                    loops = data;
                    break;
            }

            if (offset == 6) /* trigger blit */ {
                int i, j;

                /* Kludge: have to steal some cycles from the Z80 otherwise text
			   scrolling in Gorf is too fast. */
                z80_ICount[0] -= 4 * (length + 1) * (loops + 1);

                for (i = 0; i <= loops; i++) {
                    for (j = 0; j <= length; j++) {
                        if ((mode & 0x08) == 0 || j < length) {
                            if ((mode & 0x01) != 0) /* Direction */ {
                                RAM.write(src, RAM.read(dest));
                            } else if (dest >= 0) {
                                cpu_writemem16(dest, RAM.read(src));
                                /* ASG 971005 */
                            }
                        } /* close out writes in case of shift... I think this is wrong */ else if (j == length) {
                            if (dest >= 0) {
                                cpu_writemem16(dest, 0);
                            }
                        }

                        if ((j & 1) != 0 || (mode & 0x02) == 0) /* Expand Mode - don't increment source on odd loops */ {
                            if ((mode & 0x04) != 0) {
                                src++;
                                /* Constant mode - don't increment at all! */
                            }
                        }

                        if ((mode & 0x20) != 0) {
                            dest++;
                            /* copy forwards */
                        } else {
                            dest--;
                            /* backwards */
                        }
                    }

                    if ((j & 1) != 0 && (mode & 0x02) != 0) /* always increment source at end of line */ {
                        if ((mode & 0x04) != 0) {
                            src++;
                            /* Constant mode - don't increment at all! */
                        }
                    }

                    if ((mode & 0x08) != 0 && (mode & 0x04) != 0) /* Correct src if in flush mode */ {
                        src--;
                        /* and NOT in Constant mode */
                    }

                    if ((mode & 0x20) != 0) {
                        dest--;
                        /* copy forwards */
                    } else {
                        dest++;
                        /* backwards */
                    }

                    dest += (int) ((byte) skip);
                    /* extend the sign of the skip register */

                }
            }
        }
    };

    public static InitDriverPtr init_star_field = new InitDriverPtr() {
        public void handler() {
            int generator;
            int count, x, y;

            generator = 0;

            /* this 17-bit shifter with XOR feedback has a period of 2^17-1 iterations */
            for (count = 0; count < RNG_PERIOD; count++) {
                int bit1, bit2;

                generator <<= 1;
                bit1 = (~generator >> 17) & 1;
                bit2 = (generator >> 5) & 1;

                if ((bit1 ^ bit2) != 0) {
                    generator |= 1;
                }

                rng[count] = generator & 0x1ffff;
            }

            /* calculate stars positions */
            count = 0;
            for (y = 0; y < MAX_LINES; y++) {
                for (x = -16; x < CLOCKS_PER_LINE - 16; x++) /* perfect values determined with screen shots */ {
                    if (x >= Machine.visible_area.min_x
                            && x <= Machine.visible_area.max_x
                            && y >= Machine.visible_area.min_y
                            && y <= Machine.visible_area.max_y) {
                        if ((rng[count] & 0x1fe00) == 0x0fe00) {
                            star[x + SCREEN_WIDTH * y] = 1;
                        } else {
                            star[x + SCREEN_WIDTH * y] = 0;
                        }
                    }

                    count++;
                }
            }

            /* now convert the rng values to Y adjustments that will be used at runtime */
            for (count = 0; count < RNG_PERIOD; count++) {
                int r;

                r = rng[count];
                rng[count] = (((r >> 12) & 1) << 3)
                        + (((r >> 8) & 1) << 2)
                        + (((r >> 4) & 1) << 1)
                        + (((r >> 0) & 1) << 0);
            }
        }
    };

    /* GORF Special Registers
	 *
	 * These are data writes, done by IN commands
	 *
	 * The data is placed on the upper bits 8-11 bits of the address bus (B)
	 * and is used to drive 2 8 bit addressable latches to control :-
	 *
	 * IO 15
	 *   0 coin counter
	 *   1 coin counter
	 *   2 Star enable (never written to)
	 *   3 Sparkle 1
	 *   4 Sparkle 2
	 *   5 Sparkle 3
	 *   6 Second Amp On/Off ?
	 *   7 Drv7
	 *
	 * IO 16
	 *   0
	 *   1
	 *   2
	 *   3
	 *   4
	 *   5
	 *   6
	 *   7 Space Cadet Light ?
	 *
     */
    public static ReadHandlerPtr gorf_io_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int data;

            data = (cpu_get_reg(Z80_BC) >> 8) & 0x0f;

            offset = (offset << 3) + (data >> 1);
            data = ~data & 0x01;

            switch (offset) {
                case 0:
                    coin_counter_w.handler(0, data);
                    break;
                case 1:
                    coin_counter_w.handler(1, data);
                    break;
                case 2:
                    sparkle[CurrentScan][0] = data;
                    break;
                case 3:
                    sparkle[CurrentScan][1] = data;
                    break;
                case 4:
                    sparkle[CurrentScan][2] = data;
                    break;
                case 5:
                    sparkle[CurrentScan][3] = data;
                    break;
            }

            return 0;
        }
    };

    /* Wizard of Wor Special Registers
	 *
	 * These are data writes, done by IN commands
	 *
	 * The data is placed on the upper bits 8-11 bits of the address bus (A)
	 * and is used to drive 1 8 bit addressable latches to control :-
	 *
	 * IO 15
	 *   0 coin counter
	 *   1 coin counter
	 *   2 Star enable (never written to)
	 *   3 Sparkle 1
	 *   4 Sparkle 2
	 *   5 Sparkle 3
	 *   6 n.c.
	 *   7 coin counter
	 *
     */
    public static ReadHandlerPtr wow_io_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int data;

            data = (cpu_get_reg(Z80_AF) >> 8) & 0x0f;

            offset = (offset << 3) + (data >> 1);
            data = ~data & 0x01;

            switch (offset) {
                case 0:
                    coin_counter_w.handler(0, data);
                    break;
                case 1:
                    coin_counter_w.handler(1, data);
                    break;
                case 2:
                    sparkle[CurrentScan][0] = data;
                    break;
                case 3:
                    sparkle[CurrentScan][1] = data;
                    break;
                case 4:
                    sparkle[CurrentScan][2] = data;
                    break;
                case 5:
                    sparkle[CurrentScan][3] = data;
                    break;
                case 7:
                    coin_counter_w.handler(2, data);
                    break;
            }
            return 0;
        }
    };

    /**
     * *************************************************************************
     */
    public static VhStopPtr astrocde_vh_stop = new VhStopPtr() {
        public void handler() {
            rng = null;
            star = null;
        }
    };

    public static VhStartPtr astrocde_vh_start = new VhStartPtr() {
        public int handler() {
            rng = new int[RNG_PERIOD * 4];
            star = new int[SCREEN_WIDTH * MAX_LINES * 4];

            //memset(sparkle,0,sizeof(sparkle));
            CurrentScan = 0;

            return 0;
        }
    };

    public static VhStartPtr astrocde_stars_vh_start = new VhStartPtr() {
        public int handler() {
            int res;

            res = astrocde_vh_start.handler();

            sparkle[0][0] = 1;
            /* wow doesn't initialize this */
            init_star_field.handler();

            return res;
        }
    };

    /**
     * *************************************************************************
     */
    public static void wow_update_line(osd_bitmap bitmap, int line) {
        /* Copy one line to bitmap, using current color register settings */

        int memloc;
        int i, x;
        int data, color;
        int rngoffs;

        if (line >= MAX_LINES) {
            return;
        }

        rngoffs = MOD_U32_U64_U32(MUL_U64_U32_U32(
                cpu_getcurrentframe() % RNG_PERIOD, CLOCKS_PER_FRAME), RNG_PERIOD);

        memloc = line * 80;

        for (i = 0; i < 80; i++, memloc++) {
            if (line < VerticalBlank) {
                data = wow_videoram.read(memloc);
            } else {
                data = BackgroundData;
            }

            for (x = i * 4 + 3; x >= i * 4; x--) {
                int pen, scol;

                color = data & 0x03;
                if (i < colorsplit[line]) {
                    color += 4;
                }

                if ((data & 0x03) == 0) {
                    if (sparkle[line][0] != 0) {
                        if (star[x + SCREEN_WIDTH * line] != 0) {
                            scol = rng[(rngoffs + x + CLOCKS_PER_LINE * line) % RNG_PERIOD];
                            pen = (colors[line][color] & ~7) + scol / 2;
                        } else {
                            pen = 0;
                        }
                    } else {
                        pen = colors[line][color];
                    }
                } else {
                    if (sparkle[line][data & 0x03] != 0) {
                        scol = rng[(rngoffs + x + CLOCKS_PER_LINE * line) % RNG_PERIOD];
                        pen = (colors[line][color] & ~7) + scol / 2;
                    } else {
                        pen = colors[line][color];
                    }
                }

                plot_pixel.handler(bitmap, x, line, Machine.pens[pen]);

                data >>= 2;
            }
        }
    }

    public static VhUpdatePtr astrocde_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            if (full_refresh != 0) {
                int i;

                for (i = 0; i < MAX_LINES; i++) {
                    wow_update_line(bitmap, i);
                }
            }
        }
    };

    public static VhUpdatePtr seawolf2_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int x, y, centre;
            UBytePtr RAM = memory_region(REGION_CPU1);

            astrocde_vh_screenrefresh.handler(bitmap, full_refresh);

            /* Draw a sight */
            if (RAM.read(0xc1fb) != 0) /* Number of Players */ {
                /* Yellow sight for Player 1 */

                centre = 317 - ((input_port_0_r.handler(0) & 0x3f) - 18) * 10;

                if (centre < 2) {
                    centre = 2;
                }
                if (centre > 317) {
                    centre = 317;
                }

                for (y = 35 - 10; y < 35 + 11; y++) {
                    plot_pixel.handler(bitmap, centre, y, Machine.pens[0x77]);
                }

                for (x = centre - 20; x < centre + 21; x++) {
                    if ((x > 0) && (x < 319)) {
                        plot_pixel.handler(bitmap, x, 35, Machine.pens[0x77]);
                    }
                }

                /* Red sight for Player 2 */
                if (RAM.read(0xc1fb) == 2) {
                    centre = 316 - ((input_port_1_r.handler(0) & 0x3f) - 18) * 10;

                    if (centre < 1) {
                        centre = 1;
                    }
                    if (centre > 316) {
                        centre = 316;
                    }

                    for (y = 33 - 10; y < 33 + 11; y++) {
                        plot_pixel.handler(bitmap, centre, y, Machine.pens[0x58]);
                    }

                    for (x = centre - 20; x < centre + 21; x++) {
                        if ((x > 0) && (x < 319)) {
                            plot_pixel.handler(bitmap, x, 33, Machine.pens[0x58]);
                        }
                    }
                }
            }
        }
    };
}
