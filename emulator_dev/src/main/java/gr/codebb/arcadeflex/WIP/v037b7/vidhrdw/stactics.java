/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.machine.stactics.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;

public class stactics {

    /* These are needed by machine/stactics.c  */
    public static int stactics_vblank_count;
    public static int stactics_shot_standby;
    public static int stactics_shot_arrive;

    /* These are needed by driver/stactics.c   */
    public static UBytePtr stactics_scroll_ram = new UBytePtr();
    public static UBytePtr stactics_videoram_b = new UBytePtr();
    public static UBytePtr stactics_chardata_b = new UBytePtr();
    public static UBytePtr stactics_videoram_d = new UBytePtr();
    public static UBytePtr stactics_chardata_d = new UBytePtr();
    public static UBytePtr stactics_videoram_e = new UBytePtr();
    public static UBytePtr stactics_chardata_e = new UBytePtr();
    public static UBytePtr stactics_videoram_f = new UBytePtr();
    public static UBytePtr stactics_chardata_f = new UBytePtr();
    public static UBytePtr stactics_display_buffer = new UBytePtr();

    public static char[] dirty_videoram_b;
    public static char[] dirty_chardata_b;
    public static char[] dirty_videoram_d;
    public static char[] dirty_chardata_d;
    public static char[] dirty_videoram_e;
    public static char[] dirty_chardata_e;
    public static char[] dirty_videoram_f;
    public static char[] dirty_chardata_f;

    public static int d_offset;
    public static int e_offset;
    public static int f_offset;

    static int palette_select;

    static osd_bitmap tmpbitmap2;
    static osd_bitmap bitmap_B;
    static osd_bitmap bitmap_D;
    static osd_bitmap bitmap_E;
    static osd_bitmap bitmap_F;

    static UBytePtr beamdata;
    static int states_per_frame;

    public static int DIRTY_CHARDATA_SIZE = 0x100;
    public static int BEAMDATA_SIZE = 0x800;

    /* The first 16 came from the 7448 BCD to 7-segment decoder data sheet */
 /* The rest are made up */
    static char stactics_special_chars[] = {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /* Space */
        0x80, 0x80, 0x80, 0xf0, 0x80, 0x80, 0xf0, 0x00, /* extras... */
        0xf0, 0x80, 0x80, 0xf0, 0x00, 0x00, 0xf0, 0x00, /* extras... */
        0x90, 0x90, 0x90, 0xf0, 0x00, 0x00, 0x00, 0x00, /* extras... */
        0x00, 0x00, 0x00, 0xf0, 0x10, 0x10, 0xf0, 0x00, /* extras... */
        0x00, 0x00, 0x00, 0xf0, 0x80, 0x80, 0xf0, 0x00, /* extras... */
        0xf0, 0x90, 0x90, 0xf0, 0x10, 0x10, 0xf0, 0x00, /* 9 */
        0xf0, 0x90, 0x90, 0xf0, 0x90, 0x90, 0xf0, 0x00, /* 8 */
        0xf0, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x00, /* 7 */
        0xf0, 0x80, 0x80, 0xf0, 0x90, 0x90, 0xf0, 0x00, /* 6 */
        0xf0, 0x80, 0x80, 0xf0, 0x10, 0x10, 0xf0, 0x00, /* 5 */
        0x90, 0x90, 0x90, 0xf0, 0x10, 0x10, 0x10, 0x00, /* 4 */
        0xf0, 0x10, 0x10, 0xf0, 0x10, 0x10, 0xf0, 0x00, /* 3 */
        0xf0, 0x10, 0x10, 0xf0, 0x80, 0x80, 0xf0, 0x00, /* 2 */
        0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x00, /* 1 */
        0xf0, 0x90, 0x90, 0x90, 0x90, 0x90, 0xf0, 0x00, /* 0 */
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /* Space */
        0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /* 1 pip */
        0x60, 0x90, 0x80, 0x60, 0x10, 0x90, 0x60, 0x00, /* S for Score */
        0x80, 0x00, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, /* 2 pips */
        0x80, 0x00, 0x80, 0x00, 0x80, 0x00, 0x00, 0x00, /* 3 pips */
        0x60, 0x90, 0x80, 0x80, 0x80, 0x90, 0x60, 0x00, /* C for Credits */
        0xe0, 0x90, 0x90, 0xe0, 0x90, 0x90, 0xe0, 0x00, /* B for Barriers */
        0xe0, 0x90, 0x90, 0xe0, 0xc0, 0xa0, 0x90, 0x00, /* R for Rounds */
        0x80, 0x00, 0x80, 0x00, 0x80, 0x00, 0x80, 0x00, /* 4 pips */
        0x00, 0x60, 0x60, 0x00, 0x60, 0x60, 0x00, 0x00, /* Colon */
        0x40, 0xe0, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, /* Sight */
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /* Space (Unused) */
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /* Space (Unused) */
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /* Space (Unused) */
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, /* Space (Unused) */
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 /* Space */};

    static int firebeam_state;
    static int old_firebeam_state;

    public static VhConvertColorPromPtr stactics_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i, j;

            /* Now make the palette */
            int p_ptr = 0;
            for (i = 0; i < 16; i++) {
                int bit0, bit1, bit2, bit3;

                bit0 = i & 1;
                bit1 = (i >> 1) & 1;
                bit2 = (i >> 2) & 1;
                bit3 = (i >> 3) & 1;

                /* red component */
                palette[p_ptr++] = (char) (0xff * bit0);

                /* green component */
                palette[p_ptr++] = (char) (0xff * bit1 - 0xcc * bit3);

                /* blue component */
                palette[p_ptr++] = (char) (0xff * bit2);
            }

            /* The color prom in Space Tactics is used for both   */
 /* color codes, and priority layering of the 4 layers */
 /* Since we are taking care of the layering by our    */
 /* drawing order, we don't need all of the color prom */
 /* entries */
 /* For each of 4 color schemes */
            int c_ptr = 0;
            for (i = 0; i < 4; i++) {
                /* For page B - Alphanumerics and alien shots */
                for (j = 0; j < 16; j++) {
                    colortable[c_ptr++] = (0);
                    colortable[c_ptr++] = color_prom.read(i * 0x100 + 0x01 * 0x10 + j);
                }
                /* For page F - Close Aliens (these are all the same color) */
                for (j = 0; j < 16; j++) {
                    colortable[c_ptr++] = 0;
                    colortable[c_ptr++] = color_prom.read(i * 0x100 + 0x02 * 0x10);
                }
                /* For page E - Medium Aliens (these are all the same color) */
                for (j = 0; j < 16; j++) {
                    colortable[c_ptr++] = 0;
                    colortable[c_ptr++] = color_prom.read(i * 0x100 + 0x04 * 0x10 + j);
                }
                /* For page D - Far Aliens (these are all the same color) */
                for (j = 0; j < 16; j++) {
                    colortable[c_ptr++] = 0;
                    colortable[c_ptr++] = color_prom.read(i * 0x100 + 0x08 * 0x10 + j);
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
    public static VhStartPtr stactics_vh_start = new VhStartPtr() {
        public int handler() {
            int i, j;
            UBytePtr firebeam_data;
            char[] firechar = new char[256 * 8 * 9];

            if ((tmpbitmap = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                return 1;
            }
            if ((tmpbitmap2 = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                return 1;
            }
            if ((bitmap_B = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                return 1;
            }
            if ((bitmap_D = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                return 1;
            }
            if ((bitmap_E = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                return 1;
            }
            if ((bitmap_F = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                return 1;
            }

            /* Allocate dirty buffers */
            if ((dirty_videoram_b = new char[videoram_size[0]]) == null) {
                return 1;
            }
            if ((dirty_videoram_d = new char[videoram_size[0]]) == null) {
                return 1;
            }
            if ((dirty_videoram_e = new char[videoram_size[0]]) == null) {
                return 1;
            }
            if ((dirty_videoram_f = new char[videoram_size[0]]) == null) {
                return 1;
            }
            if ((dirty_chardata_b = new char[DIRTY_CHARDATA_SIZE]) == null) {
                return 1;
            }
            if ((dirty_chardata_d = new char[DIRTY_CHARDATA_SIZE]) == null) {
                return 1;
            }
            if ((dirty_chardata_e = new char[DIRTY_CHARDATA_SIZE]) == null) {
                return 1;
            }
            if ((dirty_chardata_f = new char[DIRTY_CHARDATA_SIZE]) == null) {
                return 1;
            }

            memset(dirty_videoram_b, 1, videoram_size[0]);
            memset(dirty_videoram_d, 1, videoram_size[0]);
            memset(dirty_videoram_e, 1, videoram_size[0]);
            memset(dirty_videoram_f, 1, videoram_size[0]);
            memset(dirty_chardata_b, 1, DIRTY_CHARDATA_SIZE);
            memset(dirty_chardata_d, 1, DIRTY_CHARDATA_SIZE);
            memset(dirty_chardata_e, 1, DIRTY_CHARDATA_SIZE);
            memset(dirty_chardata_f, 1, DIRTY_CHARDATA_SIZE);

            d_offset = 0;
            e_offset = 0;
            f_offset = 0;

            palette_select = 0;
            stactics_vblank_count = 0;
            stactics_shot_standby = 1;
            stactics_shot_arrive = 0;
            firebeam_state = 0;
            old_firebeam_state = 0;

            /* Create a fake character set for LED fire beam */
            memset(firechar, 0, firechar.length);
            for (i = 0; i < 256; i++) {
                for (j = 0; j < 8; j++) {
                    if (((i >> j) & 0x01) != 0) {
                        firechar[i * 9 + (7 - j)] |= (0x01 << (7 - j));
                        firechar[i * 9 + (7 - j) + 1] |= (0x01 << (7 - j));
                    }
                }
            }

            for (i = 0; i < 256; i++) {
                decodechar(Machine.gfx[4],
                        i,
                        new UBytePtr(firechar),
                        Machine.drv.gfxdecodeinfo[4].gfxlayout);
            }

            /* Decode the Fire Beam ROM for later      */
 /* (I am basically just juggling the bytes */
 /* and storing it again to make it easier) */
            if ((beamdata = new UBytePtr(BEAMDATA_SIZE)) == null) {
                return 1;
            }

            firebeam_data = memory_region(REGION_GFX1);

            for (i = 0; i < 256; i++) {
                beamdata.write(i * 8, firebeam_data.read(i));
                beamdata.write(i * 8 + 1, firebeam_data.read(i + 1024));
                beamdata.write(i * 8 + 2, firebeam_data.read(i + 256));
                beamdata.write(i * 8 + 3, firebeam_data.read(i + 1024 + 256));
                beamdata.write(i * 8 + 4, firebeam_data.read(i + 512));
                beamdata.write(i * 8 + 5, firebeam_data.read(i + 1024 + 512));
                beamdata.write(i * 8 + 6, firebeam_data.read(i + 512 + 256));
                beamdata.write(i * 8 + 7, firebeam_data.read(i + 1024 + 512 + 256));
            }

            /* Build some characters for simulating the LED displays */
            for (i = 0; i < 32; i++) {
                decodechar(Machine.gfx[5],
                        i,
                        new UBytePtr(stactics_special_chars),
                        Machine.drv.gfxdecodeinfo[5].gfxlayout);
            }

            stactics_vblank_count = 0;
            stactics_vert_pos = 0;
            stactics_horiz_pos = 0;
            stactics_motor_on.write(0);

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
    public static VhStopPtr stactics_vh_stop = new VhStopPtr() {
        public void handler() {
            dirty_videoram_b = null;
            dirty_videoram_d = null;
            dirty_videoram_e = null;
            dirty_videoram_f = null;
            dirty_chardata_b = null;
            dirty_chardata_d = null;
            dirty_chardata_e = null;
            dirty_chardata_f = null;

            beamdata = null;

            bitmap_free(tmpbitmap);
            bitmap_free(tmpbitmap2);
            bitmap_free(bitmap_B);
            bitmap_free(bitmap_D);
            bitmap_free(bitmap_E);
            bitmap_free(bitmap_F);
        }
    };

    public static WriteHandlerPtr stactics_palette_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int old_palette_select = palette_select;

            switch (offset) {
                case 0:
                    palette_select = (palette_select & 0x02) | (data & 0x01);
                    break;
                case 1:
                    palette_select = (palette_select & 0x01) | ((data & 0x01) << 1);
                    break;
                default:
                    return;
            }

            if (old_palette_select != palette_select) {
                memset(dirty_videoram_b, 1, videoram_size[0]);
                memset(dirty_videoram_d, 1, videoram_size[0]);
                memset(dirty_videoram_e, 1, videoram_size[0]);
                memset(dirty_videoram_f, 1, videoram_size[0]);
            }
            return;
        }
    };

    public static WriteHandlerPtr stactics_scroll_ram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int temp;

            if (stactics_scroll_ram.read(offset) != data) {
                stactics_scroll_ram.write(offset, data);
                temp = (offset & 0x700) >> 8;
                switch (temp) {
                    case 4: // Page D
                    {
                        if ((data & 0x01) != 0) {
                            d_offset = offset & 0xff;
                        }
                        break;
                    }
                    case 5: // Page E
                    {
                        if ((data & 0x01) != 0) {
                            e_offset = offset & 0xff;
                        }
                        break;
                    }
                    case 6: // Page F
                    {
                        if ((data & 0x01) != 0) {
                            f_offset = offset & 0xff;
                        }
                        break;
                    }
                }
            }
        }
    };

    public static WriteHandlerPtr stactics_speed_latch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* This writes to a shift register which is clocked by   */
 /* a 555 oscillator.  This value determines the speed of */
 /* the LED fire beams as follows:                        */

 /*   555_freq / bits_in_SR * edges_in_SR / states_in_PR67 / frame_rate */
 /*      = num_led_states_per_frame  */
 /*   36439 / 8 * x / 32 / 60 ~= 19/8*x */
 /* Here, we will count the number of rising edges in the shift register */
            int i;
            int num_rising_edges = 0;

            for (i = 0; i < 8; i++) {
                if ((((data >> i) & 0x01) == 1) && (((data >> ((i + 1) % 8)) & 0x01) == 0)) {
                    num_rising_edges++;
                }
            }

            states_per_frame = num_rising_edges * 19 / 8;
        }
    };

    public static WriteHandlerPtr stactics_shot_trigger_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            stactics_shot_standby = 0;
        }
    };

    public static WriteHandlerPtr stactics_shot_flag_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            stactics_shot_arrive = 0;
        }
    };

    public static WriteHandlerPtr stactics_videoram_b_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (stactics_videoram_b.read(offset) != data) {
                stactics_videoram_b.write(offset, data);
                dirty_videoram_b[offset] = 1;
            }
        }
    };

    public static WriteHandlerPtr stactics_chardata_b_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (stactics_chardata_b.read(offset) != data) {
                stactics_chardata_b.write(offset, data);
                dirty_chardata_b[offset >> 3] = 1;
            }
        }
    };

    public static WriteHandlerPtr stactics_videoram_d_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (stactics_videoram_d.read(offset) != data) {
                stactics_videoram_d.write(offset, data);
                dirty_videoram_d[offset] = 1;
            }
        }
    };

    public static WriteHandlerPtr stactics_chardata_d_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (stactics_chardata_d.read(offset) != data) {
                stactics_chardata_d.write(offset, data);
                dirty_chardata_d[offset >> 3] = 1;
            }
        }
    };

    public static WriteHandlerPtr stactics_videoram_e_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (stactics_videoram_e.read(offset) != data) {
                stactics_videoram_e.write(offset, data);
                dirty_videoram_e[offset] = 1;
            }
        }
    };

    public static WriteHandlerPtr stactics_chardata_e_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (stactics_chardata_e.read(offset) != data) {
                stactics_chardata_e.write(offset, data);
                dirty_chardata_e[offset >> 3] = 1;
            }
        }
    };

    public static WriteHandlerPtr stactics_videoram_f_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (stactics_videoram_f.read(offset) != data) {
                stactics_videoram_f.write(offset, data);
                dirty_videoram_f[offset] = 1;
            }
        }
    };

    public static WriteHandlerPtr stactics_chardata_f_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (stactics_chardata_f.read(offset) != data) {
                stactics_chardata_f.write(offset, data);
                dirty_chardata_f[offset >> 3] = 1;
            }
        }
    };

    /* Actual area for visible monitor stuff is only 30*8 lines */
 /* The rest is used for the score, etc. */
    static rectangle visible_screen_area = new rectangle(0 * 8, 32 * 8, 0 * 8, 30 * 8);

    /**
     * *************************************************************************
     *
     * Draw the game screen in the given osd_bitmap. Do NOT call
     * osd_update_display() from this function, it will be called by the main
     * emulation engine.
     *
     **************************************************************************
     */
    public static VhUpdatePtr stactics_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs, sx, sy, i;
            int char_number;
            int color_code;
            int pixel_x, pixel_y;

            int palette_offset = palette_select * 64;

            for (offs = 0x400 - 1; offs >= 0; offs--) {
                sx = offs % 32;
                sy = offs / 32;

                color_code = palette_offset + (stactics_videoram_b.read(offs) >> 4);

                /* Draw aliens in Page D */
                char_number = stactics_videoram_d.read(offs);

                if (dirty_chardata_d[char_number] == 1) {
                    decodechar(Machine.gfx[3],
                            char_number,
                            stactics_chardata_d,
                            Machine.drv.gfxdecodeinfo[3].gfxlayout);
                    dirty_chardata_d[char_number] = 2;
                    dirty_videoram_d[offs] = 1;
                } else if (dirty_chardata_d[char_number] == 2) {
                    dirty_videoram_d[offs] = 1;
                }

                if (dirty_videoram_d[offs] != 0) {
                    drawgfx(bitmap_D, Machine.gfx[3],
                            char_number,
                            color_code,
                            0, 0,
                            sx * 8, sy * 8,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                    dirty_videoram_d[offs] = 0;
                }

                /* Draw aliens in Page E */
                char_number = stactics_videoram_e.read(offs);

                if (dirty_chardata_e[char_number] == 1) {
                    decodechar(Machine.gfx[2],
                            char_number,
                            stactics_chardata_e,
                            Machine.drv.gfxdecodeinfo[2].gfxlayout);
                    dirty_chardata_e[char_number] = 2;
                    dirty_videoram_e[offs] = 1;
                } else if (dirty_chardata_e[char_number] == 2) {
                    dirty_videoram_e[offs] = 1;
                }

                if (dirty_videoram_e[offs] != 0) {
                    drawgfx(bitmap_E, Machine.gfx[2],
                            char_number,
                            color_code,
                            0, 0,
                            sx * 8, sy * 8,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                    dirty_videoram_e[offs] = 0;
                }

                /* Draw aliens in Page F */
                char_number = stactics_videoram_f.read(offs);

                if (dirty_chardata_f[char_number] == 1) {
                    decodechar(Machine.gfx[1],
                            char_number,
                            stactics_chardata_f,
                            Machine.drv.gfxdecodeinfo[1].gfxlayout);
                    dirty_chardata_f[char_number] = 2;
                    dirty_videoram_f[offs] = 1;
                } else if (dirty_chardata_f[char_number] == 2) {
                    dirty_videoram_f[offs] = 1;
                }

                if (dirty_videoram_f[offs] != 0) {
                    drawgfx(bitmap_F, Machine.gfx[1],
                            char_number,
                            color_code,
                            0, 0,
                            sx * 8, sy * 8,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                    dirty_videoram_f[offs] = 0;
                }

                /* Draw the page B stuff */
                char_number = stactics_videoram_b.read(offs);

                if (dirty_chardata_b[char_number] == 1) {
                    decodechar(Machine.gfx[0],
                            char_number,
                            stactics_chardata_b,
                            Machine.drv.gfxdecodeinfo[0].gfxlayout);
                    dirty_chardata_b[char_number] = 2;
                    dirty_videoram_b[offs] = 1;
                } else if (dirty_chardata_b[char_number] == 2) {
                    dirty_videoram_b[offs] = 1;
                }

                if (dirty_videoram_b[offs] != 0) {
                    drawgfx(bitmap_B, Machine.gfx[0],
                            char_number,
                            color_code,
                            0, 0,
                            sx * 8, sy * 8,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                    dirty_videoram_b[offs] = 0;
                }

            }

            /* Now, composite the four layers together */
            copyscrollbitmap(tmpbitmap2, bitmap_D, 0, null, 1, new int[]{d_offset},
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
            copyscrollbitmap(tmpbitmap2, bitmap_E, 0, null, 1, new int[]{e_offset},
                    Machine.visible_area, TRANSPARENCY_COLOR, 0);
            copyscrollbitmap(tmpbitmap2, bitmap_F, 0, null, 1, new int[]{f_offset},
                    Machine.visible_area, TRANSPARENCY_COLOR, 0);
            copybitmap(tmpbitmap2, bitmap_B, 0, 0, 0, 0,
                    Machine.visible_area, TRANSPARENCY_COLOR, 0);

            /* Now flip X & simulate the monitor motion */
            fillbitmap(bitmap, Machine.pens[0], Machine.visible_area);
            copybitmap(bitmap, tmpbitmap2, 1, 0, stactics_horiz_pos, stactics_vert_pos,
                    visible_screen_area, TRANSPARENCY_NONE, 0);

            /* Finally, draw stuff that is on the console or on top of the monitor (LED's) */
            /**
             * *** Draw Score Display ****
             */
            pixel_x = 16;
            pixel_y = 248;

            /* Draw an S */
            drawgfx(bitmap, Machine.gfx[5],
                    18,
                    0,
                    0, 0,
                    pixel_x, pixel_y,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
            pixel_x += 6;
            /* Draw a colon */
            drawgfx(bitmap, Machine.gfx[5],
                    25,
                    0,
                    0, 0,
                    pixel_x, pixel_y,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
            pixel_x += 6;
            /* Draw the digits */
            for (i = 1; i < 7; i++) {
                drawgfx(bitmap, Machine.gfx[5],
                        stactics_display_buffer.read(i) & 0x0f,
                        16,
                        0, 0,
                        pixel_x, pixel_y,
                        Machine.visible_area, TRANSPARENCY_NONE, 0);
                pixel_x += 6;
            }

            /**
             * *** Draw Credits Indicator ****
             */
            pixel_x = 64 + 16;

            /* Draw a C */
            drawgfx(bitmap, Machine.gfx[5],
                    21,
                    0,
                    0, 0,
                    pixel_x, pixel_y,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
            pixel_x += 6;
            /* Draw a colon */
            drawgfx(bitmap, Machine.gfx[5],
                    25,
                    0,
                    0, 0,
                    pixel_x, pixel_y,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
            pixel_x += 6;
            /* Draw the pips */
            for (i = 7; i < 9; i++) {
                drawgfx(bitmap, Machine.gfx[5],
                        16 + (~stactics_display_buffer.read(i) & 0x0f),
                        16,
                        0, 0,
                        pixel_x, pixel_y,
                        Machine.visible_area, TRANSPARENCY_NONE, 0);
                pixel_x += 2;
            }

            /**
             * *** Draw Rounds Indicator ****
             */
            pixel_x = 128 + 16;

            /* Draw an R */
            drawgfx(bitmap, Machine.gfx[5],
                    22,
                    0,
                    0, 0,
                    pixel_x, pixel_y,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
            pixel_x += 6;
            /* Draw a colon */
            drawgfx(bitmap, Machine.gfx[5],
                    25,
                    0,
                    0, 0,
                    pixel_x, pixel_y,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
            pixel_x += 6;
            /* Draw the pips */
            for (i = 9; i < 12; i++) {
                drawgfx(bitmap, Machine.gfx[5],
                        16 + (~stactics_display_buffer.read(i) & 0x0f),
                        16,
                        0, 0,
                        pixel_x, pixel_y,
                        Machine.visible_area, TRANSPARENCY_NONE, 0);
                pixel_x += 2;
            }

            /**
             * *** Draw Barriers Indicator ****
             */
            pixel_x = 192 + 16;
            /* Draw a B */
            drawgfx(bitmap, Machine.gfx[5],
                    23,
                    0,
                    0, 0,
                    pixel_x, pixel_y,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
            pixel_x += 6;
            /* Draw a colon */
            drawgfx(bitmap, Machine.gfx[5],
                    25,
                    0,
                    0, 0,
                    pixel_x, pixel_y,
                    Machine.visible_area, TRANSPARENCY_NONE, 0);
            pixel_x += 6;
            /* Draw the pips */
            for (i = 12; i < 16; i++) {
                drawgfx(bitmap, Machine.gfx[5],
                        16 + (~stactics_display_buffer.read(i) & 0x0f),
                        16,
                        0, 0,
                        pixel_x, pixel_y,
                        Machine.visible_area, TRANSPARENCY_NONE, 0);
                pixel_x += 2;
            }

            /* An LED fire beam! */
 /* (There were 120 green LEDS mounted in the cabinet in the game, */
 /*  and one red one, for the sight)                               */
 /* First, update the firebeam state */
            old_firebeam_state = firebeam_state;
            if (stactics_shot_standby == 0) {
                firebeam_state = (firebeam_state + states_per_frame) % 512;
            }

            /* These are thresholds for the two shots from the LED fire ROM */
 /* (Note: There are two more for sound triggers, */
 /*        whenever that gets implemented)        */
            if ((old_firebeam_state < 0x8b) & (firebeam_state >= 0x8b)) {
                stactics_shot_arrive = 1;
            }

            if ((old_firebeam_state < 0xca) & (firebeam_state >= 0xca)) {
                stactics_shot_arrive = 1;
            }

            if (firebeam_state > 255) {
                firebeam_state = 0;
                stactics_shot_standby = 1;
            }

            /* Now, draw the beam */
            pixel_x = 15;
            pixel_y = 166;

            for (i = 0; i < 8; i++) {
                if ((i % 2) == 1) {
                    /* Draw 7 LEDS on each side */
                    drawgfx(bitmap, Machine.gfx[4],
                            beamdata.read(firebeam_state * 8 + i) & 0x7f,
                            16 * 2, /* Make it green */
                            0, 0,
                            pixel_x, pixel_y,
                            Machine.visible_area, TRANSPARENCY_COLOR, 0);
                    drawgfx(bitmap, Machine.gfx[4],
                            beamdata.read(firebeam_state * 8 + i) & 0x7f,
                            16 * 2, /* Make it green */
                            1, 0,
                            255 - pixel_x, pixel_y,
                            Machine.visible_area, TRANSPARENCY_COLOR, 0);
                    pixel_x += 14;
                    pixel_y -= 7;
                } else {
                    /* Draw 8 LEDS on each side */
                    drawgfx(bitmap, Machine.gfx[4],
                            beamdata.read(firebeam_state * 8 + i),
                            16 * 2, /* Make it green */
                            0, 0,
                            pixel_x, pixel_y,
                            Machine.visible_area, TRANSPARENCY_COLOR, 0);
                    drawgfx(bitmap, Machine.gfx[4],
                            beamdata.read(firebeam_state * 8 + i),
                            16 * 2, /* Make it green */
                            1, 0,
                            255 - pixel_x, pixel_y,
                            Machine.visible_area, TRANSPARENCY_COLOR, 0);
                    pixel_x += 16;
                    pixel_y -= 8;
                }

            }

            /* Red Sight LED */
            pixel_x = 134;
            pixel_y = 112;

            if ((stactics_motor_on.read() & 0x01) != 0) {
                drawgfx(bitmap, Machine.gfx[5],
                        26,
                        16, /* red */
                        0, 0,
                        pixel_x, pixel_y,
                        Machine.visible_area, TRANSPARENCY_COLOR, 0);
            }

            /* Update vblank counter */
            stactics_vblank_count++;

            /* reset dirty flags */
            for (i = 0; i < 0xff; i++) {
                dirty_chardata_b[i] &= 0x01;
                dirty_chardata_d[i] &= 0x01;
                dirty_chardata_e[i] &= 0x01;
                dirty_chardata_f[i] &= 0x01;
            }

        }
    };
}
