/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.drivers;

import static gr.codebb.arcadeflex.WIP.v037b7.machine._6821pia.*;
import static gr.codebb.arcadeflex.WIP.v037b7.machine._6821piaH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.GfxDecodeInfo;
import gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.GfxLayout;
import gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.rectangle;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import gr.codebb.arcadeflex.WIP.v037b7.sound.dacH.DACinterface;
import gr.codebb.arcadeflex.WIP.v037b7.sound.hc55516H.hc55516_interface;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.common.libc.cstring.memcpy;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.old.arcadeflex.fileio.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.mame.inputH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import gr.codebb.arcadeflex.v037b7.mame.sndintrfH.MachineSound;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.machine.williams.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.williams.*;

public class williams {

    /**
     * ** local stuff ***
     */

    static char cmos_base;
    static char cmos_length;

    /**
     * ** configuration macros ***
     */
    public static void CONFIGURE_CMOS(char a, char l) {
        cmos_base = a;
        cmos_length = l;
    }

    public static void CONFIGURE_BLITTER(char x, char r, char c) {
        williams_blitter_xor = x;
        williams_blitter_remap = r;
        williams_blitter_clip = c;
    }

    public static void CONFIGURE_TILEMAP(char m, char[] p, char f, byte s, char b) {
        williams2_tilemap_mask = m;
        williams2_row_to_palette = p;
        williams2_M7_flip = (f) != 0 ? (char) 0x80 : (char) 0x00;
        williams2_videoshift = s;
        williams2_special_bg_color = b;
    }

    public static void CONFIGURE_PIAS(pia6821_interface a, pia6821_interface b, pia6821_interface c) {
        pia_unconfig();
        pia_config(0, PIA_STANDARD_ORDERING | PIA_8BIT, a);
        pia_config(1, PIA_STANDARD_ORDERING | PIA_8BIT, b);
        pia_config(2, PIA_STANDARD_ORDERING | PIA_8BIT, c);
    }

    public static nvramPtr nvram_handler = new nvramPtr() {
        public void handler(Object file, int read_or_write) {
            UBytePtr ram = memory_region(REGION_CPU1);

            if (read_or_write != 0) {
                osd_fwrite(file, ram, cmos_base, cmos_length);
            } else {
                if (file != null) {
                    osd_fread(file, ram, cmos_base, cmos_length);
                } else {
                    memset(ram, cmos_base, 0, cmos_length);
                }
            }
        }
    };

    /**
     * ***********************************
     *
     * Defender memory handlers
     *
     ************************************
     */
    static MemoryReadAddress defender_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x97ff, MRA_BANK1),
                new MemoryReadAddress(0x9800, 0xbfff, MRA_RAM),
                new MemoryReadAddress(0xc000, 0xcfff, MRA_BANK2),
                new MemoryReadAddress(0xd000, 0xffff, MRA_ROM),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress defender_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x97ff, williams_videoram_w, videoram, videoram_size),
                new MemoryWriteAddress(0x9800, 0xbfff, MWA_RAM),
                new MemoryWriteAddress(0xc000, 0xcfff, MWA_BANK2, defender_bank_base),
                new MemoryWriteAddress(0xc000, 0xc00f, MWA_RAM, paletteram),
                new MemoryWriteAddress(0xd000, 0xdfff, defender_bank_select_w),
                new MemoryWriteAddress(0xe000, 0xffff, MWA_ROM),
                new MemoryWriteAddress(-1) /* end of table */};

    /**
     * ***********************************
     *
     * General Williams memory handlers
     *
     ************************************
     */
    static MemoryReadAddress williams_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x97ff, MRA_BANK1),
                new MemoryReadAddress(0x9800, 0xbfff, MRA_RAM),
                new MemoryReadAddress(0xc804, 0xc807, pia_0_r),
                new MemoryReadAddress(0xc80c, 0xc80f, pia_1_r),
                new MemoryReadAddress(0xcb00, 0xcb00, williams_video_counter_r),
                new MemoryReadAddress(0xcc00, 0xcfff, MRA_RAM),
                new MemoryReadAddress(0xd000, 0xffff, MRA_ROM),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress williams_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x97ff, williams_videoram_w, williams_bank_base, videoram_size),
                new MemoryWriteAddress(0x9800, 0xbfff, MWA_RAM),
                new MemoryWriteAddress(0xc000, 0xc00f, paletteram_BBGGGRRR_w, paletteram),
                new MemoryWriteAddress(0xc804, 0xc807, pia_0_w),
                new MemoryWriteAddress(0xc80c, 0xc80f, pia_1_w),
                new MemoryWriteAddress(0xc900, 0xc900, williams_vram_select_w),
                new MemoryWriteAddress(0xca00, 0xca07, williams_blitter_w, williams_blitterram),
                new MemoryWriteAddress(0xcbff, 0xcbff, watchdog_reset_w),
                new MemoryWriteAddress(0xcc00, 0xcfff, MWA_RAM),
                new MemoryWriteAddress(0xd000, 0xffff, MWA_ROM),
                new MemoryWriteAddress(-1) /* end of table */};

    /**
     * ***********************************
     *
     * Blaster memory handlers
     *
     ************************************
     */
    static MemoryReadAddress blaster_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x3fff, MRA_BANK1),
                new MemoryReadAddress(0x4000, 0x96ff, MRA_BANK2),
                new MemoryReadAddress(0x9700, 0xbfff, MRA_RAM),
                new MemoryReadAddress(0xc804, 0xc807, pia_0_r),
                new MemoryReadAddress(0xc80c, 0xc80f, pia_1_r),
                new MemoryReadAddress(0xcb00, 0xcb00, williams_video_counter_r),
                new MemoryReadAddress(0xcc00, 0xcfff, MRA_RAM),
                new MemoryReadAddress(0xd000, 0xffff, MRA_ROM),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress blaster_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x96ff, williams_videoram_w, williams_bank_base, videoram_size),
                new MemoryWriteAddress(0x9700, 0xbaff, MWA_RAM),
                new MemoryWriteAddress(0xbb00, 0xbbff, MWA_RAM, blaster_color_zero_table),
                new MemoryWriteAddress(0xbc00, 0xbcff, MWA_RAM, blaster_color_zero_flags),
                new MemoryWriteAddress(0xbd00, 0xbfff, MWA_RAM),
                new MemoryWriteAddress(0xc000, 0xc00f, paletteram_BBGGGRRR_w, paletteram),
                new MemoryWriteAddress(0xc804, 0xc807, pia_0_w),
                new MemoryWriteAddress(0xc80c, 0xc80f, pia_1_w),
                new MemoryWriteAddress(0xc900, 0xc900, blaster_vram_select_w),
                new MemoryWriteAddress(0xc940, 0xc940, blaster_remap_select_w),
                new MemoryWriteAddress(0xc980, 0xc980, blaster_bank_select_w),
                new MemoryWriteAddress(0xc9C0, 0xc9c0, blaster_video_bits_w, blaster_video_bits),
                new MemoryWriteAddress(0xca00, 0xca07, williams_blitter_w, williams_blitterram),
                new MemoryWriteAddress(0xcbff, 0xcbff, watchdog_reset_w),
                new MemoryWriteAddress(0xcc00, 0xcfff, MWA_RAM),
                new MemoryWriteAddress(0xd000, 0xffff, MWA_ROM),
                new MemoryWriteAddress(-1) /* end of table */};

    /**
     * ***********************************
     *
     * Later Williams memory handlers
     *
     ************************************
     */
    static MemoryReadAddress williams2_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x7fff, MRA_BANK1),
                new MemoryReadAddress(0x8000, 0x87ff, MRA_BANK2),
                new MemoryReadAddress(0x8800, 0x8fff, MRA_BANK3),
                new MemoryReadAddress(0x9000, 0xbfff, MRA_RAM),
                new MemoryReadAddress(0xc000, 0xc7ff, MRA_RAM),
                new MemoryReadAddress(0xc980, 0xc983, pia_1_r),
                new MemoryReadAddress(0xc984, 0xc987, pia_0_r),
                new MemoryReadAddress(0xcbe0, 0xcbe0, williams_video_counter_r),
                new MemoryReadAddress(0xcc00, 0xcfff, MRA_RAM),
                new MemoryReadAddress(0xd000, 0xffff, MRA_ROM),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress williams2_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x8fff, williams2_videoram_w, videoram, videoram_size),
                new MemoryWriteAddress(0x9000, 0xbfff, MWA_RAM),
                new MemoryWriteAddress(0xc000, 0xc7ff, MWA_RAM),
                new MemoryWriteAddress(0xc800, 0xc800, williams2_bank_select_w),
                new MemoryWriteAddress(0xc880, 0xc887, williams_blitter_w, williams_blitterram),
                new MemoryWriteAddress(0xc900, 0xc900, watchdog_reset_w),
                new MemoryWriteAddress(0xc980, 0xc983, pia_1_w),
                new MemoryWriteAddress(0xc984, 0xc987, pia_0_w),
                new MemoryWriteAddress(0xc98c, 0xc98c, williams2_7segment_w),
                new MemoryWriteAddress(0xcb00, 0xcb00, williams2_fg_select_w),
                new MemoryWriteAddress(0xcb20, 0xcb20, williams2_bg_select_w),
                new MemoryWriteAddress(0xcb40, 0xcb40, MWA_RAM, williams2_xscroll_low),
                new MemoryWriteAddress(0xcb60, 0xcb60, MWA_RAM, williams2_xscroll_high),
                new MemoryWriteAddress(0xcb80, 0xcb80, MWA_RAM),
                new MemoryWriteAddress(0xcba0, 0xcba0, MWA_RAM, williams2_blit_inhibit),
                new MemoryWriteAddress(0xcc00, 0xcfff, MWA_RAM),
                new MemoryWriteAddress(0xd000, 0xffff, MWA_ROM),
                new MemoryWriteAddress(-1) /* end of table */};

    /**
     * ***********************************
     *
     * Sound board memory handlers
     *
     ************************************
     */
    static MemoryReadAddress sound_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x007f, MRA_RAM),
                new MemoryReadAddress(0x0400, 0x0403, pia_2_r),
                new MemoryReadAddress(0x8400, 0x8403, pia_2_r), /* used by Colony 7, perhaps others? */
                new MemoryReadAddress(0xb000, 0xffff, MRA_ROM), /* most games start at $F000; Sinistar starts at $B000 */
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress sound_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x007f, MWA_RAM),
                new MemoryWriteAddress(0x0400, 0x0403, pia_2_w),
                new MemoryWriteAddress(0x8400, 0x8403, pia_2_w), /* used by Colony 7, perhaps others? */
                new MemoryWriteAddress(0xb000, 0xffff, MWA_ROM), /* most games start at $F000; Sinistar starts at $B000 */
                new MemoryWriteAddress(-1) /* end of table */};

    /**
     * ***********************************
     *
     * Later sound board memory handlers
     *
     ************************************
     */
    static MemoryReadAddress williams2_sound_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x00ff, MRA_RAM),
                new MemoryReadAddress(0x2000, 0x2003, pia_2_r),
                new MemoryReadAddress(0xe000, 0xffff, MRA_ROM),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress williams2_sound_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x00ff, MWA_RAM),
                new MemoryWriteAddress(0x2000, 0x2003, pia_2_w),
                new MemoryWriteAddress(0xe000, 0xffff, MWA_ROM),
                new MemoryWriteAddress(-1) /* end of table */};

    /**
     * ***********************************
     *
     * Port definitions
     *
     ************************************
     */
    static InputPortPtr input_ports_defender = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, IPT_BUTTON1, "Fire", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, IPT_BUTTON2, "Thrust", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BITX(0x04, IP_ACTIVE_HIGH, IPT_BUTTON3, "Smart Bomb", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, IPT_BUTTON4, "Hyperspace", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BITX(0x40, IP_ACTIVE_HIGH, IPT_BUTTON6, "Reverse", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY);

            PORT_START();
            /* IN1 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY);
            PORT_BIT(0xfe, IP_ACTIVE_HIGH, IPT_UNKNOWN);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);

            PORT_START();
            /* IN3 - fake port for better joystick control */
 /* This fake port is handled via defender_input_port_1 */
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_CHEAT);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_CHEAT);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_colony7 = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_BUTTON1);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_BUTTON2);

            PORT_START();
            /* IN1 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_BUTTON3);
            PORT_BIT(0xfe, IP_ACTIVE_HIGH, IPT_UNKNOWN);

            PORT_START();
            /* IN2 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_stargate = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, IPT_BUTTON1, "Fire", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, IPT_BUTTON2, "Thrust", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BITX(0x04, IP_ACTIVE_HIGH, IPT_BUTTON3, "Smart Bomb", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, IPT_BUTTON6, "Hyperspace", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BITX(0x40, IP_ACTIVE_HIGH, IPT_BUTTON4, "Reverse", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY);

            PORT_START();
            /* IN1 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, IPT_BUTTON5, "Inviso", IP_KEY_DEFAULT, IP_JOY_DEFAULT);
            PORT_BIT(0xfc, IP_ACTIVE_HIGH, IPT_UNKNOWN);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);

            PORT_START();
            /* IN3 - fake port for better joystick control */
 /* This fake port is handled via stargate_input_port_0 */
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_CHEAT);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_CHEAT);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_joust = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_UNUSED);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0xc0, IP_ACTIVE_HIGH, IPT_UNUSED);

            PORT_START();
            /* IN1 */
            PORT_BIT(0xff, IP_ACTIVE_HIGH, IPT_UNUSED);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);

            PORT_START();
            /* IN3 (muxed with IN0) */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER1);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER1);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER1);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_UNUSED);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0xc0, IP_ACTIVE_HIGH, IPT_UNUSED);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_robotron = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_UP);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_DOWN);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_LEFT);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_RIGHT);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_UP);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_DOWN);

            PORT_START();
            /* IN1 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_LEFT);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_RIGHT);
            PORT_BIT(0xfc, IP_ACTIVE_HIGH, IPT_UNKNOWN);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_bubbles = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START1);

            PORT_START();
            /* IN1 */
            PORT_BIT(0xff, IP_ACTIVE_HIGH, IPT_UNKNOWN);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_splat = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_UP | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_DOWN | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_LEFT | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_RIGHT | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_UP | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_DOWN | IPF_8WAY | IPF_PLAYER2);

            PORT_START();
            /* IN2 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_LEFT | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0xfc, IP_ACTIVE_HIGH, IPT_UNKNOWN);

            PORT_START();
            /* IN4 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);

            PORT_START();
            /* IN3 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_UP | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_DOWN | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_LEFT | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_RIGHT | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_UP | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_DOWN | IPF_8WAY | IPF_PLAYER1);

            PORT_START();
            /* IN4 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_LEFT | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0xfc, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_sinistar = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
 /* pseudo analog joystick, see below */

            PORT_START();
            /* IN1 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_BUTTON1);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_BUTTON2);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START2);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);

            PORT_START();
            /* fake, converted by sinistar_input_port_0() */
            PORT_ANALOG(0xff, 0x38, IPT_AD_STICK_X, 100, 10, 0x00, 0x6f);

            PORT_START();
            /* fake, converted by sinistar_input_port_0() */
            PORT_ANALOG(0xff, 0x38, IPT_AD_STICK_Y | IPF_REVERSE, 100, 10, 0x00, 0x6f);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_lottofun = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN);/* Used by ticket dispenser */

            PORT_START();
            /* IN1 */
            PORT_BIT(0xff, IP_ACTIVE_HIGH, IPT_UNKNOWN);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, IPF_TOGGLE, "Memory Protect", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN);// COIN1.5? :)
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN);// Sound board handshake
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_blaster = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
 /* pseudo analog joystick, see below */

            PORT_START();
            /* IN1 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_BUTTON2);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_BUTTON1);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_BUTTON3);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0xc0, IP_ACTIVE_HIGH, IPT_UNKNOWN);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BITX(0x08, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);

            PORT_START();
            /* fake, converted by sinistar_input_port_0() */
            PORT_ANALOG(0xff, 0x38, IPT_AD_STICK_X, 100, 10, 0x00, 0x6f);

            PORT_START();
            /* fake, converted by sinistar_input_port_0() */
            PORT_ANALOG(0xff, 0x38, IPT_AD_STICK_Y | IPF_REVERSE, 100, 10, 0x00, 0x6f);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_mysticm = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN1 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_START2);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_UNUSED);/* Key */
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_BUTTON1);

            PORT_START();
            PORT_BIT(0xff, IP_ACTIVE_HIGH, IPT_UNUSED);

            PORT_START();
            /* IN0 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BITX(0x04, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_UNUSED);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_tshoot = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 (muxed with IN3)*/
            PORT_ANALOG(0x3F, 0x20, IPT_AD_STICK_Y, 25, 10, 0, 0x3F);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_BUTTON1);

            PORT_START();
            /* IN1 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_BUTTON2);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_BUTTON3);
            PORT_BIT(0x3C, IP_ACTIVE_HIGH, IPT_UNUSED);/* 0011-1100 output */
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_START2);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BITX(0x04, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_UNUSED);

            PORT_START();
            /* IN3 (muxed with IN0) */
            PORT_ANALOG(0x3F, 0x20, IPT_AD_STICK_X, 25, 10, 0, 0x3F);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_BUTTON1);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_inferno = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 (muxed with IN3) */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPF_PLAYER1 | IPT_JOYSTICKLEFT_UP);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPF_PLAYER1 | IPT_JOYSTICKLEFT_LEFT);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPF_PLAYER1 | IPT_JOYSTICKLEFT_RIGHT);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPF_PLAYER1 | IPT_JOYSTICKLEFT_DOWN);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPF_PLAYER1 | IPT_JOYSTICKRIGHT_UP);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPF_PLAYER1 | IPT_JOYSTICKRIGHT_LEFT);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPF_PLAYER1 | IPT_JOYSTICKRIGHT_RIGHT);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPF_PLAYER1 | IPT_JOYSTICKRIGHT_DOWN);

            PORT_START();
            /* IN1 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPF_PLAYER1 | IPT_BUTTON1);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPF_PLAYER2 | IPT_BUTTON1);
            PORT_BIT(0x3C, IP_ACTIVE_HIGH, IPT_UNUSED);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_START2);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BITX(0x04, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_UNUSED);

            PORT_START();
            /* IN3 (muxed with IN0) */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPF_PLAYER2 | IPT_JOYSTICKLEFT_UP);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPF_PLAYER2 | IPT_JOYSTICKLEFT_LEFT);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPF_PLAYER2 | IPT_JOYSTICKLEFT_RIGHT);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPF_PLAYER2 | IPT_JOYSTICKLEFT_DOWN);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPF_PLAYER2 | IPT_JOYSTICKRIGHT_UP);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPF_PLAYER2 | IPT_JOYSTICKRIGHT_LEFT);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPF_PLAYER2 | IPT_JOYSTICKRIGHT_RIGHT);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPF_PLAYER2 | IPT_JOYSTICKRIGHT_DOWN);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_joust2 = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER1);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER1);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_UNUSED);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1);
            PORT_BIT(0xc0, IP_ACTIVE_LOW, IPT_UNUSED);

            PORT_START();
            /* IN1 */
            PORT_BIT(0xFF, IP_ACTIVE_HIGH, IPT_UNUSED);

            PORT_START();
            /* IN2 */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, 0, "Auto Up", KEYCODE_F1, IP_JOY_NONE);
            PORT_BITX(0x02, IP_ACTIVE_HIGH, 0, "Advance", KEYCODE_F2, IP_JOY_NONE);
            PORT_BITX(0x04, IP_ACTIVE_HIGH, 0, "High Score Reset", KEYCODE_7, IP_JOY_NONE);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x10, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_COIN3);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_TILT);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_UNUSED);

            PORT_START();
            /* IN3 (muxed with IN0) */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_UNUSED);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1);
            PORT_BIT(0xc0, IP_ACTIVE_LOW, IPT_UNUSED);
            INPUT_PORTS_END();
        }
    };

    /**
     * ***********************************
     *
     * Graphics definitions
     *
     ************************************
     */
    static GfxLayout williams2_layout = new GfxLayout(
            24, 16,
            256,
            4,
            new int[]{0, 1, 2, 3},
            new int[]{0 + 0 * 8, 4 + 0 * 8, 0 + 0 * 8 + 0x4000 * 8, 4 + 0 * 8 + 0x4000 * 8, 0 + 0 * 8 + 0x8000 * 8, 4 + 0 * 8 + 0x8000 * 8,
                0 + 1 * 8, 4 + 1 * 8, 0 + 1 * 8 + 0x4000 * 8, 4 + 1 * 8 + 0x4000 * 8, 0 + 1 * 8 + 0x8000 * 8, 4 + 1 * 8 + 0x8000 * 8,
                0 + 2 * 8, 4 + 2 * 8, 0 + 2 * 8 + 0x4000 * 8, 4 + 2 * 8 + 0x4000 * 8, 0 + 2 * 8 + 0x8000 * 8, 4 + 2 * 8 + 0x8000 * 8,
                0 + 3 * 8, 4 + 3 * 8, 0 + 3 * 8 + 0x4000 * 8, 4 + 3 * 8 + 0x4000 * 8, 0 + 3 * 8 + 0x8000 * 8, 4 + 3 * 8 + 0x8000 * 8
            },
            new int[]{0 * 8, 4 * 8, 8 * 8, 12 * 8, 16 * 8, 20 * 8, 24 * 8, 28 * 8, 32 * 8, 36 * 8, 40 * 8, 44 * 8, 48 * 8, 52 * 8, 56 * 8, 60 * 8},
            4 * 16 * 8
    );

    static GfxDecodeInfo williams2_gfxdecodeinfo[]
            = {
                new GfxDecodeInfo(REGION_GFX1, 0, williams2_layout, 16, 8),
                new GfxDecodeInfo(-1) /* end of array */};

    /**
     * ***********************************
     *
     * Sound definitions
     *
     ************************************
     */
    static DACinterface dac_interface = new DACinterface(
            1,
            new int[]{50}
    );

    static hc55516_interface sinistar_cvsd_interface = new hc55516_interface(
            1, /* 1 chip */
            new int[]{80}
    );

    /**
     * ***********************************
     *
     * Machine driver
     *
     ************************************
     */
    static MachineDriver machine_driver_defender = new MachineDriver(
            /* basic machine hardware  */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_M6809,
                        1000000,
                        defender_readmem, defender_writemem, null, null,
                        ignore_interrupt, 1
                ),
                new MachineCPU(
                        CPU_M6808 | CPU_AUDIO_CPU,
                        3579000 / 4,
                        sound_readmem, sound_writemem, null, null,
                        ignore_interrupt, 1
                )
            },
            60, DEFAULT_60HZ_VBLANK_DURATION,
            1,
            defender_init_machine,
            /* video hardware */
            304, 256,
            new rectangle(6, 298 - 1, 7, 247 - 1),
            null,
            16, 16,
            null,
            VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_SUPPORTS_DIRTY,
            null,
            williams_vh_start,
            williams_vh_stop,
            williams_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_DAC,
                        dac_interface
                )
            },
            nvram_handler
    );

    static MachineDriver machine_driver_williams = new MachineDriver(
            /* basic machine hardware  */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_M6809,
                        1000000,
                        williams_readmem, williams_writemem, null, null,
                        ignore_interrupt, 1
                ),
                new MachineCPU(
                        CPU_M6808 | CPU_AUDIO_CPU,
                        3579000 / 4,
                        sound_readmem, sound_writemem, null, null,
                        ignore_interrupt, 1
                )
            },
            60, DEFAULT_60HZ_VBLANK_DURATION,
            1,
            williams_init_machine,
            /* video hardware */
            304, 256,
            new rectangle(6, 298 - 1, 7, 247 - 1),
            null,
            16, 16,
            null,
            VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_SUPPORTS_DIRTY,
            null,
            williams_vh_start,
            williams_vh_stop,
            williams_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_DAC,
                        dac_interface
                )
            },
            nvram_handler
    );

    static MachineDriver machine_driver_sinistar = new MachineDriver(
            /* basic machine hardware  */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_M6809,
                        1000000,
                        williams_readmem, williams_writemem, null, null,
                        ignore_interrupt, 1
                ),
                new MachineCPU(
                        CPU_M6808 | CPU_AUDIO_CPU,
                        3579000 / 4,
                        sound_readmem, sound_writemem, null, null,
                        ignore_interrupt, 1
                )
            },
            60, DEFAULT_60HZ_VBLANK_DURATION,
            1,
            williams_init_machine,
            /* video hardware */
            304, 256,
            new rectangle(6, 298 - 1, 7, 247 - 1),
            null,
            16, 16,
            null,
            VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_SUPPORTS_DIRTY,
            null,
            williams_vh_start,
            williams_vh_stop,
            williams_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_DAC,
                        dac_interface
                ),
                new MachineSound(
                        SOUND_HC55516,
                        sinistar_cvsd_interface
                )
            },
            nvram_handler
    );

    static MachineDriver machine_driver_blaster = new MachineDriver(
            /* basic machine hardware  */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_M6809,
                        1000000,
                        blaster_readmem, blaster_writemem, null, null,
                        ignore_interrupt, 1
                ),
                new MachineCPU(
                        CPU_M6808 | CPU_AUDIO_CPU,
                        3579000 / 4,
                        sound_readmem, sound_writemem, null, null,
                        ignore_interrupt, 1
                )
            },
            60, DEFAULT_60HZ_VBLANK_DURATION,
            1,
            williams_init_machine,
            /* video hardware */
            304, 256,
            new rectangle(6, 298 - 1, 7, 247 - 1),
            null,
            16 + 256, 16 + 256,
            null,
            VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
            null,
            blaster_vh_start,
            williams_vh_stop,
            blaster_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_DAC,
                        dac_interface
                )
            },
            nvram_handler
    );

    static MachineDriver machine_driver_williams2 = new MachineDriver(
            /* basic machine hardware  */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_M6809,
                        1000000,
                        williams2_readmem, williams2_writemem, null, null,
                        ignore_interrupt, 1
                ),
                new MachineCPU(
                        CPU_M6808 | CPU_AUDIO_CPU,
                        3579000 / 4,
                        williams2_sound_readmem, williams2_sound_writemem, null, null,
                        ignore_interrupt, 1
                )
            },
            60, DEFAULT_60HZ_VBLANK_DURATION,
            1,
            williams2_init_machine,
            /* video hardware */
            288, 256,
            new rectangle(4, 288 - 1, 8, 248 - 1),
            williams2_gfxdecodeinfo,
            16 + 8 * 16, 16 + 8 * 16,
            null,
            VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
            null,
            williams2_vh_start,
            williams2_vh_stop,
            williams_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_DAC,
                        dac_interface
                )
            },
            nvram_handler
    );

    static MachineDriver machine_driver_joust2 = new MachineDriver(
            /* basic machine hardware  */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_M6809,
                        1000000,
                        williams2_readmem, williams2_writemem, null, null,
                        ignore_interrupt, 1
                ),
                new MachineCPU(
                        CPU_M6808 | CPU_AUDIO_CPU,
                        3579000 / 4,
                        williams2_sound_readmem, williams2_sound_writemem, null, null,
                        ignore_interrupt, 1
                )//,
/*TODO*///                SOUND_CPU_WILLIAMS_CVSD
            },
            60, DEFAULT_60HZ_VBLANK_DURATION,
            1,
            joust2_init_machine,
            /* video hardware */
            288, 256,
            new rectangle(4, 288 - 1, 8, 248 - 1),
            williams2_gfxdecodeinfo,
            16 + 8 * 16, 16 + 8 * 16,
            null,
            VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
            null,
            williams2_vh_start,
            williams2_vh_stop,
            williams_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,null,
/*TODO*///            new MachineSound[]{
/*TODO*///                SOUND_WILLIAMS_CVSD
/*TODO*///            },
            nvram_handler
    );

    /**
     * ***********************************
     *
     * Driver initialization
     *
     ************************************
     */
    public static InitDriverPtr init_defender = new InitDriverPtr() {
        public void handler() {
            /*static const UINT32*/
            int bank[] = {0x0c000, 0x10000, 0x11000, 0x12000, 0x0c000, 0x0c000, 0x0c000, 0x13000};
            defender_bank_list = bank;

            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xc400, (char) 0x100);

            /* PIA configuration */
            CONFIGURE_PIAS(defender_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);

        }
    };

    public static InitDriverPtr init_defndjeu = new InitDriverPtr() {
        public void handler() {
            UBytePtr rom = memory_region(REGION_CPU1);
            int x;

            for (x = 0xd000; x < 0x15000; x++) {
                char src = rom.read(x);
                rom.write(x, (src & 0x7e) | (src >> 7) | (src << 7));
            }
        }
    };

    public static InitDriverPtr init_mayday = new InitDriverPtr() {
        public void handler() {
            int bank[] = {0x0c000, 0x10000, 0x11000, 0x12000, 0x0c000, 0x0c000, 0x0c000, 0x13000};
            defender_bank_list = bank;

            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xc400, (char) 0x100);

            /* PIA configuration */
            CONFIGURE_PIAS(williams_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);

            /* install a handler to catch protection checks */
            mayday_protection = install_mem_read_handler(0, 0xa190, 0xa191, mayday_protection_r);

        }
    };

    public static InitDriverPtr init_colony7 = new InitDriverPtr() {
        public void handler() {
            int bank[] = {0x0c000, 0x10000, 0x11000, 0x12000, 0x0c000, 0x0c000, 0x0c000, 0x0c000};
            defender_bank_list = bank;

            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xc400, (char) 0x100);

            /* PIA configuration */
            CONFIGURE_PIAS(williams_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);

        }
    };

    public static InitDriverPtr init_stargate = new InitDriverPtr() {
        public void handler() {
            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* PIA configuration */
            CONFIGURE_PIAS(stargate_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);

        }
    };

    public static InitDriverPtr init_joust = new InitDriverPtr() {
        public void handler() {
            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 4, (char) 0, (char) 0);

            /* PIA configuration */
            CONFIGURE_PIAS(williams_muxed_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);

        }
    };

    public static InitDriverPtr init_robotron = new InitDriverPtr() {
        public void handler() {
            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 4, (char) 0, (char) 0);

            /* PIA configuration */
            CONFIGURE_PIAS(williams_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);
        }
    };

    public static InitDriverPtr init_bubbles = new InitDriverPtr() {
        public void handler() {
            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 4, (char) 0, (char) 0);

            /* PIA configuration */
            CONFIGURE_PIAS(williams_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);

        }
    };

    public static InitDriverPtr init_splat = new InitDriverPtr() {
        public void handler() {
            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 0, (char) 0, (char) 0);

            /* PIA configuration */
            CONFIGURE_PIAS(williams_dual_muxed_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);

        }
    };

    public static InitDriverPtr init_sinistar = new InitDriverPtr() {
        public void handler() {
            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 4, (char) 0, (char) 1);

            /* PIA configuration */
            CONFIGURE_PIAS(williams_49way_pia_0_intf, williams_pia_1_intf, sinistar_snd_pia_intf);

            /* install RAM instead of ROM in the Dxxx slot */
            install_mem_read_handler(0, 0xd000, 0xdfff, MRA_RAM);
            install_mem_write_handler(0, 0xd000, 0xdfff, MWA_RAM);

        }
    };

    public static InitDriverPtr init_lottofun = new InitDriverPtr() {
        public void handler() {
            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 4, (char) 0, (char) 0);

            /* PIA configuration */
            CONFIGURE_PIAS(lottofun_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);
        }
    };

    public static InitDriverPtr init_blaster = new InitDriverPtr() {
        public void handler() {
            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 0, (char) 1, (char) 0);

            /* PIA configuration */
            CONFIGURE_PIAS(williams_49way_pia_0_intf, williams_pia_1_intf, williams_snd_pia_intf);
        }
    };

    public static InitDriverPtr init_tshoot = new InitDriverPtr() {
        public void handler() {
            char tilemap_colors[] = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7};

            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 0, (char) 0, (char) 0);
            CONFIGURE_TILEMAP((char) 0x7f, tilemap_colors, (char) 1, (byte) 0, (char) 0);

            /* PIA configuration */
            CONFIGURE_PIAS(tshoot_pia_0_intf, williams2_pia_1_intf, tshoot_snd_pia_intf);

        }
    };

    public static InitDriverPtr init_joust2 = new InitDriverPtr() {
        public void handler() {
            char tilemap_colors[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 0, (char) 0, (char) 0);
            CONFIGURE_TILEMAP((char) 0xff, tilemap_colors, (char) 0, (byte) -2, (char) 0);

            /* PIA configuration */
            CONFIGURE_PIAS(williams2_muxed_pia_0_intf, joust2_pia_1_intf, williams2_snd_pia_intf);

            /* expand the sound ROMs */
            memcpy(memory_region(REGION_CPU3), 0x18000, memory_region(REGION_CPU3), 0x10000, 0x08000);
            memcpy(memory_region(REGION_CPU3), 0x20000, memory_region(REGION_CPU3), 0x10000, 0x10000);
            memcpy(memory_region(REGION_CPU3), 0x38000, memory_region(REGION_CPU3), 0x30000, 0x08000);
            memcpy(memory_region(REGION_CPU3), 0x40000, memory_region(REGION_CPU3), 0x30000, 0x10000);
            memcpy(memory_region(REGION_CPU3), 0x58000, memory_region(REGION_CPU3), 0x50000, 0x08000);
            memcpy(memory_region(REGION_CPU3), 0x60000, memory_region(REGION_CPU3), 0x50000, 0x10000);

        }
    };

    public static InitDriverPtr init_mysticm = new InitDriverPtr() {
        public void handler() {
            char tilemap_colors[] = {1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 0, (char) 0, (char) 0);
            CONFIGURE_TILEMAP((char) 0x7f, tilemap_colors, (char) 1, (byte) 0, (char) 1);

            /* PIA configuration */
            CONFIGURE_PIAS(mysticm_pia_0_intf, williams2_pia_1_intf, williams2_snd_pia_intf);

            /* install RAM instead of ROM in the Dxxx slot */
            install_mem_read_handler(0, 0xd000, 0xdfff, MRA_RAM);
            install_mem_write_handler(0, 0xd000, 0xdfff, MWA_RAM);

        }
    };

    public static InitDriverPtr init_inferno = new InitDriverPtr() {
        public void handler() {
            char tilemap_colors[] = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7};

            /* CMOS configuration */
            CONFIGURE_CMOS((char) 0xcc00, (char) 0x400);

            /* video configuration */
            CONFIGURE_BLITTER((char) 0, (char) 0, (char) 0);
            CONFIGURE_TILEMAP((char) 0x7f, tilemap_colors, (char) 1, (byte) 0, (char) 0);

            /* PIA configuration */
            CONFIGURE_PIAS(williams2_muxed_pia_0_intf, williams2_pia_1_intf, williams2_snd_pia_intf);

            /* install RAM instead of ROM in the Dxxx slot */
            install_mem_read_handler(0, 0xd000, 0xdfff, MRA_RAM);
            install_mem_write_handler(0, 0xd000, 0xdfff, MWA_RAM);

        }
    };

    /**
     * ***********************************
     *
     * ROM definitions
     *
     ************************************
     */
    static RomLoadPtr rom_defender = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x14000, REGION_CPU1);
            ROM_LOAD("defend.1", 0x0d000, 0x0800, 0xc3e52d7e);
            ROM_LOAD("defend.4", 0x0d800, 0x0800, 0x9a72348b);
            ROM_LOAD("defend.2", 0x0e000, 0x1000, 0x89b75984);
            ROM_LOAD("defend.3", 0x0f000, 0x1000, 0x94f51e9b);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("defend.9", 0x10000, 0x0800, 0x6870e8a5);
            ROM_LOAD("defend.12", 0x10800, 0x0800, 0xf1f88938);
            ROM_LOAD("defend.8", 0x11000, 0x0800, 0xb649e306);
            ROM_LOAD("defend.11", 0x11800, 0x0800, 0x9deaf6d9);
            ROM_LOAD("defend.7", 0x12000, 0x0800, 0x339e092e);
            ROM_LOAD("defend.10", 0x12800, 0x0800, 0xa543b167);
            ROM_RELOAD(0x13800, 0x0800);
            ROM_LOAD("defend.6", 0x13000, 0x0800, 0x65f4efd1);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("defend.snd", 0xf800, 0x0800, 0xfefd5b48);
            ROM_END();
        }
    };

    static RomLoadPtr rom_defendg = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x14000, REGION_CPU1);
            ROM_LOAD("defeng01.bin", 0x0d000, 0x0800, 0x6111d74d);
            ROM_LOAD("defeng04.bin", 0x0d800, 0x0800, 0x3cfc04ce);
            ROM_LOAD("defeng02.bin", 0x0e000, 0x1000, 0xd184ab6b);
            ROM_LOAD("defeng03.bin", 0x0f000, 0x1000, 0x788b76d7);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("defeng09.bin", 0x10000, 0x0800, 0xf57caa62);
            ROM_LOAD("defeng12.bin", 0x10800, 0x0800, 0x33db686f);
            ROM_LOAD("defeng08.bin", 0x11000, 0x0800, 0x9a9eb3d2);
            ROM_LOAD("defeng11.bin", 0x11800, 0x0800, 0x5ca4e860);
            ROM_LOAD("defeng07.bin", 0x12000, 0x0800, 0x545c3326);
            ROM_LOAD("defeng10.bin", 0x12800, 0x0800, 0x941cf34e);
            ROM_RELOAD(0x13800, 0x0800);
            ROM_LOAD("defeng06.bin", 0x13000, 0x0800, 0x3af34c05);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("defend.snd", 0xf800, 0x0800, 0xfefd5b48);
            ROM_END();
        }
    };

    static RomLoadPtr rom_defendw = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x14000, REGION_CPU1);
            ROM_LOAD("wb01.bin", 0x0d000, 0x1000, 0x0ee1019d);
            ROM_LOAD("defeng02.bin", 0x0e000, 0x1000, 0xd184ab6b);
            ROM_LOAD("wb03.bin", 0x0f000, 0x1000, 0xa732d649);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("defeng09.bin", 0x10000, 0x0800, 0xf57caa62);
            ROM_LOAD("defeng12.bin", 0x10800, 0x0800, 0x33db686f);
            ROM_LOAD("defeng08.bin", 0x11000, 0x0800, 0x9a9eb3d2);
            ROM_LOAD("defeng11.bin", 0x11800, 0x0800, 0x5ca4e860);
            ROM_LOAD("defeng07.bin", 0x12000, 0x0800, 0x545c3326);
            ROM_LOAD("defeng10.bin", 0x12800, 0x0800, 0x941cf34e);
            ROM_RELOAD(0x13800, 0x0800);
            ROM_LOAD("defeng06.bin", 0x13000, 0x0800, 0x3af34c05);

            ROM_REGION(0x10000, REGION_CPU2);
            ROM_LOAD("defend.snd", 0xf800, 0x0800, 0xfefd5b48);
            ROM_END();
        }
    };

    static RomLoadPtr rom_defndjeu = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x15000, REGION_CPU1);
            ROM_LOAD("15", 0x0d000, 0x1000, 0x706a24bd);
            ROM_LOAD("16", 0x0e000, 0x1000, 0x03201532);
            ROM_LOAD("17", 0x0f000, 0x1000, 0x25287eca);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("18", 0x10000, 0x1000, 0xe99d5679);
            ROM_LOAD("19", 0x11000, 0x1000, 0x769f5984);
            ROM_LOAD("20", 0x12000, 0x1000, 0x12fa0788);
            ROM_LOAD("21", 0x13000, 0x1000, 0xbddb71a3);
            ROM_RELOAD(0x14000, 0x1000);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("s", 0xf800, 0x0800, 0xcb79ae42);
            ROM_END();
        }
    };

    static RomLoadPtr rom_defcmnd = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x15000, REGION_CPU1);
            ROM_LOAD("defcmnda.1", 0x0d000, 0x1000, 0x68effc1d);
            ROM_LOAD("defcmnda.2", 0x0e000, 0x1000, 0x1126adc9);
            ROM_LOAD("defcmnda.3", 0x0f000, 0x1000, 0x7340209d);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("defcmnda.10", 0x10000, 0x0800, 0x3dddae75);
            ROM_LOAD("defcmnda.7", 0x10800, 0x0800, 0x3f1e7cf8);
            ROM_LOAD("defcmnda.9", 0x11000, 0x0800, 0x8882e1ff);
            ROM_LOAD("defcmnda.6", 0x11800, 0x0800, 0xd068f0c5);
            ROM_LOAD("defcmnda.8", 0x12000, 0x0800, 0xfef4cb77);
            ROM_LOAD("defcmnda.5", 0x12800, 0x0800, 0x49b50b40);
            ROM_LOAD("defcmnda.4", 0x13000, 0x0800, 0x43d42a1b);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("defcmnda.snd", 0xf800, 0x0800, 0xf122d9c9);
            ROM_END();
        }
    };

    static RomLoadPtr rom_defence = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x15000, REGION_CPU1);
            ROM_LOAD("1", 0x0d000, 0x1000, 0xebc93622);
            ROM_LOAD("2", 0x0e000, 0x1000, 0x2a4f4f44);
            ROM_LOAD("3", 0x0f000, 0x1000, 0xa4112f91);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("0", 0x10000, 0x0800, 0x7a1e5998);
            ROM_LOAD("7", 0x10800, 0x0800, 0x4c2616a3);
            ROM_LOAD("9", 0x11000, 0x0800, 0x7b146003);
            ROM_LOAD("6", 0x11800, 0x0800, 0x6d748030);
            ROM_LOAD("8", 0x12000, 0x0800, 0x52d5438b);
            ROM_LOAD("5", 0x12800, 0x0800, 0x4a270340);
            ROM_LOAD("4", 0x13000, 0x0800, 0xe13f457c);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("defcmnda.snd", 0xf800, 0x0800, 0xf122d9c9);
            ROM_END();
        }
    };

    static RomLoadPtr rom_mayday = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x15000, REGION_CPU1);
            ROM_LOAD("ic03-3.bin", 0x0d000, 0x1000, 0xa1ff6e62);
            ROM_LOAD("ic02-2.bin", 0x0e000, 0x1000, 0x62183aea);
            ROM_LOAD("ic01-1.bin", 0x0f000, 0x1000, 0x5dcb113f);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("ic04-4.bin", 0x10000, 0x1000, 0xea6a4ec8);
            ROM_LOAD("ic05-5.bin", 0x11000, 0x1000, 0x0d797a3e);
            ROM_LOAD("ic06-6.bin", 0x12000, 0x1000, 0xee8bfcd6);
            ROM_LOAD("ic07-7d.bin", 0x13000, 0x1000, 0xd9c065e7);
            ROM_RELOAD(0x14000, 0x1000);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("ic28-8.bin", 0xf800, 0x0800, 0xfefd5b48);
            /* Sound ROM is same in both versions. Can be merged !!! */
            ROM_END();
        }
    };

    static RomLoadPtr rom_maydaya = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x15000, REGION_CPU1);
            ROM_LOAD("mayday.c", 0x0d000, 0x1000, 0x872a2f2d);
            ROM_LOAD("mayday.b", 0x0e000, 0x1000, 0xc4ab5e22);
            ROM_LOAD("mayday.a", 0x0f000, 0x1000, 0x329a1318);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("mayday.d", 0x10000, 0x1000, 0xc2ae4716);
            ROM_LOAD("mayday.e", 0x11000, 0x1000, 0x41225666);
            ROM_LOAD("mayday.f", 0x12000, 0x1000, 0xc39be3c0);
            ROM_LOAD("mayday.g", 0x13000, 0x1000, 0x2bd0f106);
            ROM_RELOAD(0x14000, 0x1000);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("ic28-8.bin", 0xf800, 0x0800, 0xfefd5b48);
            ROM_END();
        }
    };

    static RomLoadPtr rom_colony7 = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x14000, REGION_CPU1);
            ROM_LOAD("cs03.bin", 0x0d000, 0x1000, 0x7ee75ae5);
            ROM_LOAD("cs02.bin", 0x0e000, 0x1000, 0xc60b08cb);
            ROM_LOAD("cs01.bin", 0x0f000, 0x1000, 0x1bc97436);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("cs06.bin", 0x10000, 0x0800, 0x318b95af);
            ROM_LOAD("cs04.bin", 0x10800, 0x0800, 0xd740faee);
            ROM_LOAD("cs07.bin", 0x11000, 0x0800, 0x0b23638b);
            ROM_LOAD("cs05.bin", 0x11800, 0x0800, 0x59e406a8);
            ROM_LOAD("cs08.bin", 0x12000, 0x0800, 0x3bfde87a);
            ROM_RELOAD(0x12800, 0x0800);

            ROM_REGION(0x10000, REGION_CPU2);
            ROM_LOAD("cs11.bin", 0xf800, 0x0800, 0x6032293c);/* Sound ROM */
            ROM_END();
        }
    };

    static RomLoadPtr rom_colony7a = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x14000, REGION_CPU1);
            ROM_LOAD("cs03a.bin", 0x0d000, 0x1000, 0xe0b0d23b);
            ROM_LOAD("cs02a.bin", 0x0e000, 0x1000, 0x370c6f41);
            ROM_LOAD("cs01a.bin", 0x0f000, 0x1000, 0xba299946);
            /* bank 0 is the place for CMOS ram */
            ROM_LOAD("cs06.bin", 0x10000, 0x0800, 0x318b95af);
            ROM_LOAD("cs04.bin", 0x10800, 0x0800, 0xd740faee);
            ROM_LOAD("cs07.bin", 0x11000, 0x0800, 0x0b23638b);
            ROM_LOAD("cs05.bin", 0x11800, 0x0800, 0x59e406a8);
            ROM_LOAD("cs08.bin", 0x12000, 0x0800, 0x3bfde87a);
            ROM_RELOAD(0x12800, 0x0800);

            ROM_REGION(0x10000, REGION_CPU2);
            ROM_LOAD("cs11.bin", 0xf800, 0x0800, 0x6032293c);/* Sound ROM */
            ROM_END();
        }
    };

    static RomLoadPtr rom_stargate = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("01", 0x0000, 0x1000, 0x88824d18);
            ROM_LOAD("02", 0x1000, 0x1000, 0xafc614c5);
            ROM_LOAD("03", 0x2000, 0x1000, 0x15077a9d);
            ROM_LOAD("04", 0x3000, 0x1000, 0xa8b4bf0f);
            ROM_LOAD("05", 0x4000, 0x1000, 0x2d306074);
            ROM_LOAD("06", 0x5000, 0x1000, 0x53598dde);
            ROM_LOAD("07", 0x6000, 0x1000, 0x23606060);
            ROM_LOAD("08", 0x7000, 0x1000, 0x4ec490c7);
            ROM_LOAD("09", 0x8000, 0x1000, 0x88187b64);
            ROM_LOAD("10", 0xd000, 0x1000, 0x60b07ff7);
            ROM_LOAD("11", 0xe000, 0x1000, 0x7d2c5daf);
            ROM_LOAD("12", 0xf000, 0x1000, 0xa0396670);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("sg.snd", 0xf800, 0x0800, 0x2fcf6c4d);
            ROM_END();
        }
    };

    static RomLoadPtr rom_joust = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("joust.wg1", 0x0000, 0x1000, 0xfe41b2af);
            ROM_LOAD("joust.wg2", 0x1000, 0x1000, 0x501c143c);
            ROM_LOAD("joust.wg3", 0x2000, 0x1000, 0x43f7161d);
            ROM_LOAD("joust.wg4", 0x3000, 0x1000, 0xdb5571b6);
            ROM_LOAD("joust.wg5", 0x4000, 0x1000, 0xc686bb6b);
            ROM_LOAD("joust.wg6", 0x5000, 0x1000, 0xfac5f2cf);
            ROM_LOAD("joust.wg7", 0x6000, 0x1000, 0x81418240);
            ROM_LOAD("joust.wg8", 0x7000, 0x1000, 0xba5359ba);
            ROM_LOAD("joust.wg9", 0x8000, 0x1000, 0x39643147);
            ROM_LOAD("joust.wga", 0xd000, 0x1000, 0x3f1c4f89);
            ROM_LOAD("joust.wgb", 0xe000, 0x1000, 0xea48b359);
            ROM_LOAD("joust.wgc", 0xf000, 0x1000, 0xc710717b);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("joust.snd", 0xf000, 0x1000, 0xf1835bdd);
            ROM_END();
        }
    };

    static RomLoadPtr rom_joustwr = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("joust.wg1", 0x0000, 0x1000, 0xfe41b2af);
            ROM_LOAD("joust.wg2", 0x1000, 0x1000, 0x501c143c);
            ROM_LOAD("joust.wg3", 0x2000, 0x1000, 0x43f7161d);
            ROM_LOAD("joust.wg4", 0x3000, 0x1000, 0xdb5571b6);
            ROM_LOAD("joust.wg5", 0x4000, 0x1000, 0xc686bb6b);
            ROM_LOAD("joust.wg6", 0x5000, 0x1000, 0xfac5f2cf);
            ROM_LOAD("joust.wr7", 0x6000, 0x1000, 0xe6f439c4);
            ROM_LOAD("joust.wg8", 0x7000, 0x1000, 0xba5359ba);
            ROM_LOAD("joust.wg9", 0x8000, 0x1000, 0x39643147);
            ROM_LOAD("joust.wra", 0xd000, 0x1000, 0x2039014a);
            ROM_LOAD("joust.wgb", 0xe000, 0x1000, 0xea48b359);
            ROM_LOAD("joust.wgc", 0xf000, 0x1000, 0xc710717b);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("joust.snd", 0xf000, 0x1000, 0xf1835bdd);
            ROM_END();
        }
    };

    static RomLoadPtr rom_joustr = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("joust.wg1", 0x0000, 0x1000, 0xfe41b2af);
            ROM_LOAD("joust.wg2", 0x1000, 0x1000, 0x501c143c);
            ROM_LOAD("joust.wg3", 0x2000, 0x1000, 0x43f7161d);
            ROM_LOAD("joust.sr4", 0x3000, 0x1000, 0xab347170);
            ROM_LOAD("joust.wg5", 0x4000, 0x1000, 0xc686bb6b);
            ROM_LOAD("joust.sr6", 0x5000, 0x1000, 0x3d9a6fac);
            ROM_LOAD("joust.sr7", 0x6000, 0x1000, 0x0a70b3d1);
            ROM_LOAD("joust.sr8", 0x7000, 0x1000, 0xa7f01504);
            ROM_LOAD("joust.sr9", 0x8000, 0x1000, 0x978687ad);
            ROM_LOAD("joust.sra", 0xd000, 0x1000, 0xc0c6e52a);
            ROM_LOAD("joust.srb", 0xe000, 0x1000, 0xab11bcf9);
            ROM_LOAD("joust.src", 0xf000, 0x1000, 0xea14574b);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("joust.snd", 0xf000, 0x1000, 0xf1835bdd);
            ROM_END();
        }
    };

    static RomLoadPtr rom_robotron = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("robotron.sb1", 0x0000, 0x1000, 0x66c7d3ef);
            ROM_LOAD("robotron.sb2", 0x1000, 0x1000, 0x5bc6c614);
            ROM_LOAD("robotron.sb3", 0x2000, 0x1000, 0xe99a82be);
            ROM_LOAD("robotron.sb4", 0x3000, 0x1000, 0xafb1c561);
            ROM_LOAD("robotron.sb5", 0x4000, 0x1000, 0x62691e77);
            ROM_LOAD("robotron.sb6", 0x5000, 0x1000, 0xbd2c853d);
            ROM_LOAD("robotron.sb7", 0x6000, 0x1000, 0x49ac400c);
            ROM_LOAD("robotron.sb8", 0x7000, 0x1000, 0x3a96e88c);
            ROM_LOAD("robotron.sb9", 0x8000, 0x1000, 0xb124367b);
            ROM_LOAD("robotron.sba", 0xd000, 0x1000, 0x13797024);
            ROM_LOAD("robotron.sbb", 0xe000, 0x1000, 0x7e3c1b87);
            ROM_LOAD("robotron.sbc", 0xf000, 0x1000, 0x645d543e);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("robotron.snd", 0xf000, 0x1000, 0xc56c1d28);
            ROM_END();
        }
    };

    static RomLoadPtr rom_robotryo = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("robotron.sb1", 0x0000, 0x1000, 0x66c7d3ef);
            ROM_LOAD("robotron.sb2", 0x1000, 0x1000, 0x5bc6c614);
            ROM_LOAD("robotron.yo3", 0x2000, 0x1000, 0x67a369bc);
            ROM_LOAD("robotron.yo4", 0x3000, 0x1000, 0xb0de677a);
            ROM_LOAD("robotron.yo5", 0x4000, 0x1000, 0x24726007);
            ROM_LOAD("robotron.yo6", 0x5000, 0x1000, 0x028181a6);
            ROM_LOAD("robotron.yo7", 0x6000, 0x1000, 0x4dfcceae);
            ROM_LOAD("robotron.sb8", 0x7000, 0x1000, 0x3a96e88c);
            ROM_LOAD("robotron.sb9", 0x8000, 0x1000, 0xb124367b);
            ROM_LOAD("robotron.yoa", 0xd000, 0x1000, 0x4a9d5f52);
            ROM_LOAD("robotron.yob", 0xe000, 0x1000, 0x2afc5e7f);
            ROM_LOAD("robotron.yoc", 0xf000, 0x1000, 0x45da9202);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("robotron.snd", 0xf000, 0x1000, 0xc56c1d28);
            ROM_END();
        }
    };

    static RomLoadPtr rom_bubbles = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("bubbles.1b", 0x0000, 0x1000, 0x8234f55c);
            ROM_LOAD("bubbles.2b", 0x1000, 0x1000, 0x4a188d6a);
            ROM_LOAD("bubbles.3b", 0x2000, 0x1000, 0x7728f07f);
            ROM_LOAD("bubbles.4b", 0x3000, 0x1000, 0x040be7f9);
            ROM_LOAD("bubbles.5b", 0x4000, 0x1000, 0x0b5f29e0);
            ROM_LOAD("bubbles.6b", 0x5000, 0x1000, 0x4dd0450d);
            ROM_LOAD("bubbles.7b", 0x6000, 0x1000, 0xe0a26ec0);
            ROM_LOAD("bubbles.8b", 0x7000, 0x1000, 0x4fd23d8d);
            ROM_LOAD("bubbles.9b", 0x8000, 0x1000, 0xb48559fb);
            ROM_LOAD("bubbles.10b", 0xd000, 0x1000, 0x26e7869b);
            ROM_LOAD("bubbles.11b", 0xe000, 0x1000, 0x5a5b572f);
            ROM_LOAD("bubbles.12b", 0xf000, 0x1000, 0xce22d2e2);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("bubbles.snd", 0xf000, 0x1000, 0x689ce2aa);
            ROM_END();
        }
    };

    static RomLoadPtr rom_bubblesr = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("bubblesr.1b", 0x0000, 0x1000, 0xdda4e782);
            ROM_LOAD("bubblesr.2b", 0x1000, 0x1000, 0x3c8fa7f5);
            ROM_LOAD("bubblesr.3b", 0x2000, 0x1000, 0xf869bb9c);
            ROM_LOAD("bubblesr.4b", 0x3000, 0x1000, 0x0c65eaab);
            ROM_LOAD("bubblesr.5b", 0x4000, 0x1000, 0x7ece4e13);
            ROM_LOAD("bubbles.6b", 0x5000, 0x1000, 0x4dd0450d);
            ROM_LOAD("bubbles.7b", 0x6000, 0x1000, 0xe0a26ec0);
            ROM_LOAD("bubblesr.8b", 0x7000, 0x1000, 0x598b9bd6);
            ROM_LOAD("bubbles.9b", 0x8000, 0x1000, 0xb48559fb);
            ROM_LOAD("bubblesr.10b", 0xd000, 0x1000, 0x8b396db0);
            ROM_LOAD("bubblesr.11b", 0xe000, 0x1000, 0x096af43e);
            ROM_LOAD("bubblesr.12b", 0xf000, 0x1000, 0x5c1244ef);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("bubbles.snd", 0xf000, 0x1000, 0x689ce2aa);
            ROM_END();
        }
    };

    static RomLoadPtr rom_splat = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("splat.01", 0x0000, 0x1000, 0x1cf26e48);
            ROM_LOAD("splat.02", 0x1000, 0x1000, 0xac0d4276);
            ROM_LOAD("splat.03", 0x2000, 0x1000, 0x74873e59);
            ROM_LOAD("splat.04", 0x3000, 0x1000, 0x70a7064e);
            ROM_LOAD("splat.05", 0x4000, 0x1000, 0xc6895221);
            ROM_LOAD("splat.06", 0x5000, 0x1000, 0xea4ab7fd);
            ROM_LOAD("splat.07", 0x6000, 0x1000, 0x82fd8713);
            ROM_LOAD("splat.08", 0x7000, 0x1000, 0x7dded1b4);
            ROM_LOAD("splat.09", 0x8000, 0x1000, 0x71cbfe5a);
            ROM_LOAD("splat.10", 0xd000, 0x1000, 0xd1a1f632);
            ROM_LOAD("splat.11", 0xe000, 0x1000, 0xca8cde95);
            ROM_LOAD("splat.12", 0xf000, 0x1000, 0x5bee3e60);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("splat.snd", 0xf000, 0x1000, 0xa878d5f3);
            ROM_END();
        }
    };

    static RomLoadPtr rom_sinistar = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("sinistar.01", 0x0000, 0x1000, 0xf6f3a22c);
            ROM_LOAD("sinistar.02", 0x1000, 0x1000, 0xcab3185c);
            ROM_LOAD("sinistar.03", 0x2000, 0x1000, 0x1ce1b3cc);
            ROM_LOAD("sinistar.04", 0x3000, 0x1000, 0x6da632ba);
            ROM_LOAD("sinistar.05", 0x4000, 0x1000, 0xb662e8fc);
            ROM_LOAD("sinistar.06", 0x5000, 0x1000, 0x2306183d);
            ROM_LOAD("sinistar.07", 0x6000, 0x1000, 0xe5dd918e);
            ROM_LOAD("sinistar.08", 0x7000, 0x1000, 0x4785a787);
            ROM_LOAD("sinistar.09", 0x8000, 0x1000, 0x50cb63ad);
            ROM_LOAD("sinistar.10", 0xe000, 0x1000, 0x3d670417);
            ROM_LOAD("sinistar.11", 0xf000, 0x1000, 0x3162bc50);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("speech.ic7", 0xb000, 0x1000, 0xe1019568);
            ROM_LOAD("speech.ic5", 0xc000, 0x1000, 0xcf3b5ffd);
            ROM_LOAD("speech.ic6", 0xd000, 0x1000, 0xff8d2645);
            ROM_LOAD("speech.ic4", 0xe000, 0x1000, 0x4b56a626);
            ROM_LOAD("sinistar.snd", 0xf000, 0x1000, 0xb82f4ddb);
            ROM_END();
        }
    };

    static RomLoadPtr rom_sinista1 = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);/* 64k for code */
            ROM_LOAD("sinrev1.01", 0x0000, 0x1000, 0x3810d7b8);
            ROM_LOAD("sinistar.02", 0x1000, 0x1000, 0xcab3185c);
            ROM_LOAD("sinrev1.03", 0x2000, 0x1000, 0x7c984ca9);
            ROM_LOAD("sinrev1.04", 0x3000, 0x1000, 0xcc6c4f24);
            ROM_LOAD("sinrev1.05", 0x4000, 0x1000, 0x12285bfe);
            ROM_LOAD("sinrev1.06", 0x5000, 0x1000, 0x7a675f35);
            ROM_LOAD("sinrev1.07", 0x6000, 0x1000, 0xb0463243);
            ROM_LOAD("sinrev1.08", 0x7000, 0x1000, 0x909040d4);
            ROM_LOAD("sinrev1.09", 0x8000, 0x1000, 0xcc949810);
            ROM_LOAD("sinrev1.10", 0xe000, 0x1000, 0xea87a53f);
            ROM_LOAD("sinrev1.11", 0xf000, 0x1000, 0x88d36e80);

            ROM_REGION(0x10000, REGION_CPU2);/* 64k for the sound CPU */
            ROM_LOAD("speech.ic7", 0xb000, 0x1000, 0xe1019568);
            ROM_LOAD("speech.ic5", 0xc000, 0x1000, 0xcf3b5ffd);
            ROM_LOAD("speech.ic6", 0xd000, 0x1000, 0xff8d2645);
            ROM_LOAD("speech.ic4", 0xe000, 0x1000, 0x4b56a626);
            ROM_LOAD("sinistar.snd", 0xf000, 0x1000, 0xb82f4ddb);
            ROM_END();
        }
    };

    static RomLoadPtr rom_sinista2 = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("sinistar.01", 0x0000, 0x1000, 0xf6f3a22c);
            ROM_LOAD("sinistar.02", 0x1000, 0x1000, 0xcab3185c);
            ROM_LOAD("sinistar.03", 0x2000, 0x1000, 0x1ce1b3cc);
            ROM_LOAD("sinistar.04", 0x3000, 0x1000, 0x6da632ba);
            ROM_LOAD("sinistar.05", 0x4000, 0x1000, 0xb662e8fc);
            ROM_LOAD("sinistar.06", 0x5000, 0x1000, 0x2306183d);
            ROM_LOAD("sinistar.07", 0x6000, 0x1000, 0xe5dd918e);
            ROM_LOAD("sinrev2.08", 0x7000, 0x1000, 0xd7ecee45);
            ROM_LOAD("sinistar.09", 0x8000, 0x1000, 0x50cb63ad);
            ROM_LOAD("sinistar.10", 0xe000, 0x1000, 0x3d670417);
            ROM_LOAD("sinrev2.11", 0xf000, 0x1000, 0x792c8b00);

            ROM_REGION(0x10000, REGION_CPU2);/* 64k for the sound CPU */
            ROM_LOAD("speech.ic7", 0xb000, 0x1000, 0xe1019568);
            ROM_LOAD("speech.ic5", 0xc000, 0x1000, 0xcf3b5ffd);
            ROM_LOAD("speech.ic6", 0xd000, 0x1000, 0xff8d2645);
            ROM_LOAD("speech.ic4", 0xe000, 0x1000, 0x4b56a626);
            ROM_LOAD("sinistar.snd", 0xf000, 0x1000, 0xb82f4ddb);
            ROM_END();
        }
    };

    static RomLoadPtr rom_lottofun = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("vl4e.dat", 0x0000, 0x1000, 0x5e9af236);
            ROM_LOAD("vl4c.dat", 0x1000, 0x1000, 0x4b134ae2);
            ROM_LOAD("vl4a.dat", 0x2000, 0x1000, 0xb2f1f95a);
            ROM_LOAD("vl5e.dat", 0x3000, 0x1000, 0xc8681c55);
            ROM_LOAD("vl5c.dat", 0x4000, 0x1000, 0xeb9351e0);
            ROM_LOAD("vl5a.dat", 0x5000, 0x1000, 0x534f2fa1);
            ROM_LOAD("vl6e.dat", 0x6000, 0x1000, 0xbefac592);
            ROM_LOAD("vl6c.dat", 0x7000, 0x1000, 0xa73d7f13);
            ROM_LOAD("vl6a.dat", 0x8000, 0x1000, 0x5730a43d);
            ROM_LOAD("vl7a.dat", 0xd000, 0x1000, 0xfb2aec2c);
            ROM_LOAD("vl7c.dat", 0xe000, 0x1000, 0x9a496519);
            ROM_LOAD("vl7e.dat", 0xf000, 0x1000, 0x032cab4b);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("vl2532.snd", 0xf000, 0x1000, 0x214b8a04);
            ROM_END();
        }
    };

    static RomLoadPtr rom_blaster = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x3c000, REGION_CPU1);
            ROM_LOAD("blaster.11", 0x04000, 0x2000, 0x6371e62f);
            ROM_LOAD("blaster.12", 0x06000, 0x2000, 0x9804faac);
            ROM_LOAD("blaster.17", 0x08000, 0x1000, 0xbf96182f);
            ROM_LOAD("blaster.16", 0x0d000, 0x1000, 0x54a40b21);
            ROM_LOAD("blaster.13", 0x0e000, 0x2000, 0xf4dae4c8);

            ROM_LOAD("blaster.15", 0x00000, 0x4000, 0x1ad146a4);
            ROM_LOAD("blaster.8", 0x10000, 0x4000, 0xf110bbb0);
            ROM_LOAD("blaster.9", 0x14000, 0x4000, 0x5c5b0f8a);
            ROM_LOAD("blaster.10", 0x18000, 0x4000, 0xd47eb67f);
            ROM_LOAD("blaster.6", 0x1c000, 0x4000, 0x47fc007e);
            ROM_LOAD("blaster.5", 0x20000, 0x4000, 0x15c1b94d);
            ROM_LOAD("blaster.14", 0x24000, 0x4000, 0xaea6b846);
            ROM_LOAD("blaster.7", 0x28000, 0x4000, 0x7a101181);
            ROM_LOAD("blaster.1", 0x2c000, 0x4000, 0x8d0ea9e7);
            ROM_LOAD("blaster.2", 0x30000, 0x4000, 0x03c4012c);
            ROM_LOAD("blaster.4", 0x34000, 0x4000, 0xfc9d39fb);
            ROM_LOAD("blaster.3", 0x38000, 0x4000, 0x253690fb);

            ROM_REGION(0x10000, REGION_CPU2);
            /* 64k for the sound CPU */
            ROM_LOAD("blaster.18", 0xf000, 0x1000, 0xc33a3145);

            ROM_REGION(0x0800, REGION_PROMS);
            /* color PROM data */
            ROM_LOAD("blaster.col", 0x0000, 0x0800, 0xbac50bc4);
            ROM_END();
        }
    };

    static RomLoadPtr rom_tshoot = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x48000, REGION_CPU1);
            ROM_LOAD("rom18.cpu", 0x0D000, 0x1000, 0xeffc33f1);/* IC55 */
            ROM_LOAD("rom2.cpu", 0x0E000, 0x1000, 0xfd982687);/* IC9	*/
            ROM_LOAD("rom3.cpu", 0x0F000, 0x1000, 0x9617054d);/* IC10 */

            ROM_LOAD("rom11.cpu", 0x10000, 0x2000, 0x60d5fab8);/* IC18 */
            ROM_LOAD("rom9.cpu", 0x12000, 0x2000, 0xa4dd4a0e);/* IC16 */
            ROM_LOAD("rom7.cpu", 0x14000, 0x2000, 0xf25505e6);/* IC14 */
            ROM_LOAD("rom5.cpu", 0x16000, 0x2000, 0x94a7c0ed);/* IC12 */

            ROM_LOAD("rom17.cpu", 0x20000, 0x2000, 0xb02d1ccd);/* IC26 */
            ROM_LOAD("rom15.cpu", 0x22000, 0x2000, 0x11709935);/* IC24 */

            ROM_LOAD("rom10.cpu", 0x30000, 0x2000, 0x0f32bad8);/* IC17 */
            ROM_LOAD("rom8.cpu", 0x32000, 0x2000, 0xe9b6cbf7);/* IC15 */
            ROM_LOAD("rom6.cpu", 0x34000, 0x2000, 0xa49f617f);/* IC13 */
            ROM_LOAD("rom4.cpu", 0x36000, 0x2000, 0xb026dc00);/* IC11 */

            ROM_LOAD("rom16.cpu", 0x40000, 0x2000, 0x69ce38f8);/* IC25 */
            ROM_LOAD("rom14.cpu", 0x42000, 0x2000, 0x769a4ae5);/* IC23 */
            ROM_LOAD("rom13.cpu", 0x44000, 0x2000, 0xec016c9b);/* IC21 */
            ROM_LOAD("rom12.cpu", 0x46000, 0x2000, 0x98ae7afa);/* IC19 */

 /* sound CPU */
            ROM_REGION(0x10000, REGION_CPU2);
            ROM_LOAD("rom1.cpu", 0xE000, 0x2000, 0x011a94a7);
            /* IC8	*/

            ROM_REGION(0xc000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("rom20.cpu", 0x00000, 0x2000, 0xc6e1d253);/* IC57 */
            ROM_LOAD("rom21.cpu", 0x04000, 0x2000, 0x9874e90f);/* IC58 */
            ROM_LOAD("rom19.cpu", 0x08000, 0x2000, 0xb9ce4d2a);/* IC41 */
            ROM_END();
        }
    };

    static RomLoadPtr rom_joust2 = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x48000, REGION_CPU1);
            ROM_LOAD("ic55_r1.cpu", 0x0D000, 0x1000, 0x08b0d5bd);/* IC55 ROM02 */
            ROM_LOAD("ic09_r2.cpu", 0x0E000, 0x1000, 0x951175ce);/* IC09 ROM03 */
            ROM_LOAD("ic10_r2.cpu", 0x0F000, 0x1000, 0xba6e0f6c);/* IC10 ROM04 */

            ROM_LOAD("ic18_r1.cpu", 0x10000, 0x2000, 0x9dc986f9);/* IC18 ROM11 */
            ROM_LOAD("ic16_r2.cpu", 0x12000, 0x2000, 0x56e2b550);/* IC16 ROM09 */
            ROM_LOAD("ic14_r2.cpu", 0x14000, 0x2000, 0xf3bce576);/* IC14 ROM07 */
            ROM_LOAD("ic12_r2.cpu", 0x16000, 0x2000, 0x5f8b4919);/* IC12 ROM05 */

            ROM_LOAD("ic26_r1.cpu", 0x20000, 0x2000, 0x4ef5e805);/* IC26 ROM19 */
            ROM_LOAD("ic24_r1.cpu", 0x22000, 0x2000, 0x4861f063);/* IC24 ROM17 */
            ROM_LOAD("ic22_r1.cpu", 0x24000, 0x2000, 0x421aafa8);/* IC22 ROM15 */
            ROM_LOAD("ic20_r1.cpu", 0x26000, 0x2000, 0x3432ff55);/* IC20 ROM13 */

            ROM_LOAD("ic17_r1.cpu", 0x30000, 0x2000, 0x3e01b597);/* IC17 ROM10 */
            ROM_LOAD("ic15_r1.cpu", 0x32000, 0x2000, 0xff26fb29);/* IC15 ROM08 */
            ROM_LOAD("ic13_r2.cpu", 0x34000, 0x2000, 0x5f107db5);/* IC13 ROM06 */

            ROM_LOAD("ic25_r1.cpu", 0x40000, 0x2000, 0x47580af5);/* IC25 ROM18 */
            ROM_LOAD("ic23_r1.cpu", 0x42000, 0x2000, 0x869b5942);/* IC23 ROM16 */
            ROM_LOAD("ic21_r1.cpu", 0x44000, 0x2000, 0x0bbd867c);/* IC21 ROM14 */
            ROM_LOAD("ic19_r1.cpu", 0x46000, 0x2000, 0xb9221ed1);/* IC19 ROM12 */

 /* sound CPU */
            ROM_REGION(0x10000, REGION_CPU2);
            ROM_LOAD("ic08_r1.cpu", 0x0E000, 0x2000, 0x84517c3c);/* IC08 ROM08 */

 /* sound board */
            ROM_REGION(0x70000, REGION_CPU3);
            ROM_LOAD("u04_r1.snd", 0x10000, 0x8000, 0x3af6b47d);/* IC04 ROM23 */
            ROM_LOAD("u19_r1.snd", 0x30000, 0x8000, 0xe7f9ed2e);/* IC19 ROM24 */
            ROM_LOAD("u20_r1.snd", 0x50000, 0x8000, 0xc85b29f7);/* IC20 ROM25 */

            ROM_REGION(0xc000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("ic57_r1.vid", 0x00000, 0x4000, 0x572c6b01);/* IC57 ROM20 */
            ROM_LOAD("ic58_r1.vid", 0x04000, 0x4000, 0xaa94bf05);/* IC58 ROM21 */
            ROM_LOAD("ic41_r1.vid", 0x08000, 0x4000, 0xc41e3daa);/* IC41 ROM22 */
            ROM_END();
        }
    };

    static RomLoadPtr rom_mysticm = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x48000, REGION_CPU1);
            ROM_LOAD("mm02_2.a09", 0x0E000, 0x1000, 0x3a776ea8);/* IC9	*/
            ROM_LOAD("mm03_2.a10", 0x0F000, 0x1000, 0x6e247c75);/* IC10 */

            ROM_LOAD("mm11_1.a18", 0x10000, 0x2000, 0xf537968e);/* IC18 */
            ROM_LOAD("mm09_1.a16", 0x12000, 0x2000, 0x3bd12f6c);/* IC16 */
            ROM_LOAD("mm07_1.a14", 0x14000, 0x2000, 0xea2a2a68);/* IC14 */
            ROM_LOAD("mm05_1.a12", 0x16000, 0x2000, 0xb514eef3);/* IC12 */

            ROM_LOAD("mm18_1.a26", 0x20000, 0x2000, 0x9b391a81);/* IC26 */
            ROM_LOAD("mm16_1.a24", 0x22000, 0x2000, 0x399e175d);/* IC24 */
            ROM_LOAD("mm14_1.a22", 0x24000, 0x2000, 0x191153b1);/* IC22 */

            ROM_LOAD("mm10_1.a17", 0x30000, 0x2000, 0xd6a37509);/* IC17 */
            ROM_LOAD("mm08_1.a15", 0x32000, 0x2000, 0x6f1a64f2);/* IC15 */
            ROM_LOAD("mm06_1.a13", 0x34000, 0x2000, 0x2e6795d4);/* IC13 */
            ROM_LOAD("mm04_1.a11", 0x36000, 0x2000, 0xc222fb64);/* IC11 */

            ROM_LOAD("mm17_1.a25", 0x40000, 0x2000, 0xd36f0a96);/* IC25 */
            ROM_LOAD("mm15_1.a23", 0x42000, 0x2000, 0xcd5d99da);/* IC23 */
            ROM_LOAD("mm13_1.a21", 0x44000, 0x2000, 0xef4b79db);/* IC21 */
            ROM_LOAD("mm12_1.a19", 0x46000, 0x2000, 0xa1f04bf0);/* IC19 */

 /* sound CPU */
            ROM_REGION(0x10000, REGION_CPU2);
            ROM_LOAD("mm01_1.a08", 0x0E000, 0x2000, 0x65339512);/* IC8	*/

            ROM_REGION(0xc000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("mm20_1.b57", 0x00000, 0x2000, 0x5c0f4f46);/* IC57 */
            ROM_LOAD("mm21_1.b58", 0x04000, 0x2000, 0xcb90b3c5);/* IC58 */
            ROM_LOAD("mm19_1.b41", 0x08000, 0x2000, 0xe274df86);/* IC41 */
            ROM_END();
        }
    };

    static RomLoadPtr rom_inferno = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x48000, REGION_CPU1);
            ROM_LOAD("ic9.inf", 0x0E000, 0x1000, 0x1a013185);
            /* IC9	*/
            ROM_LOAD("ic10.inf", 0x0F000, 0x1000, 0xdbf64a36);
            /* IC10 */

            ROM_LOAD("ic18.inf", 0x10000, 0x2000, 0x95bcf7b1);
            /* IC18 */
            ROM_LOAD("ic16.inf", 0x12000, 0x2000, 0x8bc4f935);
            /* IC16 */
            ROM_LOAD("ic14.inf", 0x14000, 0x2000, 0xa70508a7);
            /* IC14 */
            ROM_LOAD("ic12.inf", 0x16000, 0x2000, 0x7ffb87f9);
            /* IC12 */

            ROM_LOAD("ic17.inf", 0x30000, 0x2000, 0xb4684139);
            /* IC17 */
            ROM_LOAD("ic15.inf", 0x32000, 0x2000, 0x128a6ad6);
            /* IC15 */
            ROM_LOAD("ic13.inf", 0x34000, 0x2000, 0x83a9e4d6);
            /* IC13 */
            ROM_LOAD("ic11.inf", 0x36000, 0x2000, 0xc2e9c909);
            /* IC11 */

            ROM_LOAD("ic25.inf", 0x40000, 0x2000, 0x103a5951);
            /* IC25 */
            ROM_LOAD("ic23.inf", 0x42000, 0x2000, 0xc04749a0);
            /* IC23 */
            ROM_LOAD("ic21.inf", 0x44000, 0x2000, 0xc405f853);
            /* IC21 */
            ROM_LOAD("ic19.inf", 0x46000, 0x2000, 0xade7645a);
            /* IC19 */

 /* sound CPU */
            ROM_REGION(0x10000, REGION_CPU2);
            ROM_LOAD("ic8.inf", 0x0E000, 0x2000, 0x4e3123b8);
            /* IC8	*/

            ROM_REGION(0xc000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("ic57.inf", 0x00000, 0x2000, 0x65a4ef79);
            /* IC57 */
            ROM_LOAD("ic58.inf", 0x04000, 0x2000, 0x4bb1c2a0);
            /* IC58 */
            ROM_LOAD("ic41.inf", 0x08000, 0x2000, 0xf3f7238f);
            /* IC41 */
            ROM_END();
        }
    };

    /**
     * ***********************************
     *
     * Game drivers
     *
     ************************************
     */
    public static GameDriver driver_defender = new GameDriver("1980", "defender", "williams.java", rom_defender, null, machine_driver_defender, input_ports_defender, init_defender, ROT0, "Williams", "Defender (Red label)");
    public static GameDriver driver_defendg = new GameDriver("1980", "defendg", "williams.java", rom_defendg, driver_defender, machine_driver_defender, input_ports_defender, init_defender, ROT0, "Williams", "Defender (Green label)");
    public static GameDriver driver_defendw = new GameDriver("1980", "defendw", "williams.java", rom_defendw, driver_defender, machine_driver_defender, input_ports_defender, init_defender, ROT0, "Williams", "Defender (White label)");
    public static GameDriver driver_defndjeu = new GameDriver("1980", "defndjeu", "williams.java", rom_defndjeu, driver_defender, machine_driver_defender, input_ports_defender, init_defndjeu, ROT0, "Jeutel", "Defender ? (bootleg)", GAME_NOT_WORKING);
    public static GameDriver driver_defcmnd = new GameDriver("1980", "defcmnd", "williams.java", rom_defcmnd, driver_defender, machine_driver_defender, input_ports_defender, init_defender, ROT0, "bootleg", "Defense Command (set 1)");
    public static GameDriver driver_defence = new GameDriver("1981", "defence", "williams.java", rom_defence, driver_defender, machine_driver_defender, input_ports_defender, init_defender, ROT0, "Outer Limits", "Defence Command");

    public static GameDriver driver_mayday = new GameDriver("1980", "mayday", "williams.java", rom_mayday, null, machine_driver_defender, input_ports_defender, init_mayday, ROT0, "<unknown>", "Mayday (set 1)");
    public static GameDriver driver_maydaya = new GameDriver("1980", "maydaya", "williams.java", rom_maydaya, driver_mayday, machine_driver_defender, input_ports_defender, init_mayday, ROT0, "<unknown>", "Mayday (set 2)");

    public static GameDriver driver_colony7 = new GameDriver("1981", "colony7", "williams.java", rom_colony7, null, machine_driver_defender, input_ports_colony7, init_colony7, ROT270, "Taito", "Colony 7 (set 1)");
    public static GameDriver driver_colony7a = new GameDriver("1981", "colony7a", "williams.java", rom_colony7a, driver_colony7, machine_driver_defender, input_ports_colony7, init_colony7, ROT270, "Taito", "Colony 7 (set 2)");

    public static GameDriver driver_stargate = new GameDriver("1981", "stargate", "williams.java", rom_stargate, null, machine_driver_williams, input_ports_stargate, init_stargate, ROT0, "Williams", "Stargate");

    public static GameDriver driver_robotron = new GameDriver("1982", "robotron", "williams.java", rom_robotron, null, machine_driver_williams, input_ports_robotron, init_robotron, ROT0, "Williams", "Robotron (Solid Blue label)");
    public static GameDriver driver_robotryo = new GameDriver("1982", "robotryo", "williams.java", rom_robotryo, driver_robotron, machine_driver_williams, input_ports_robotron, init_robotron, ROT0, "Williams", "Robotron (Yellow/Orange label)");

    public static GameDriver driver_joust = new GameDriver("1982", "joust", "williams.java", rom_joust, null, machine_driver_williams, input_ports_joust, init_joust, ROT0, "Williams", "Joust (White/Green label)");
    public static GameDriver driver_joustr = new GameDriver("1982", "joustr", "williams.java", rom_joustr, driver_joust, machine_driver_williams, input_ports_joust, init_joust, ROT0, "Williams", "Joust (Solid Red label)");
    public static GameDriver driver_joustwr = new GameDriver("1982", "joustwr", "williams.java", rom_joustwr, driver_joust, machine_driver_williams, input_ports_joust, init_joust, ROT0, "Williams", "Joust (White/Red label)");

    public static GameDriver driver_bubbles = new GameDriver("1982", "bubbles", "williams.java", rom_bubbles, null, machine_driver_williams, input_ports_bubbles, init_bubbles, ROT0, "Williams", "Bubbles");
    public static GameDriver driver_bubblesr = new GameDriver("1982", "bubblesr", "williams.java", rom_bubblesr, driver_bubbles, machine_driver_williams, input_ports_bubbles, init_bubbles, ROT0, "Williams", "Bubbles (Solid Red label)");

    public static GameDriver driver_splat = new GameDriver("1982", "splat", "williams.java", rom_splat, null, machine_driver_williams, input_ports_splat, init_splat, ROT0, "Williams", "Splat!");

    public static GameDriver driver_sinistar = new GameDriver("1982", "sinistar", "williams.java", rom_sinistar, null, machine_driver_sinistar, input_ports_sinistar, init_sinistar, ROT270, "Williams", "Sinistar (revision 3)");
    public static GameDriver driver_sinista1 = new GameDriver("1982", "sinista1", "williams.java", rom_sinista1, driver_sinistar, machine_driver_sinistar, input_ports_sinistar, init_sinistar, ROT270, "Williams", "Sinistar (prototype version)");
    public static GameDriver driver_sinista2 = new GameDriver("1982", "sinista2", "williams.java", rom_sinista2, driver_sinistar, machine_driver_sinistar, input_ports_sinistar, init_sinistar, ROT270, "Williams", "Sinistar (revision 2)");

    public static GameDriver driver_blaster = new GameDriver("1983", "blaster", "williams.java", rom_blaster, null, machine_driver_blaster, input_ports_blaster, init_blaster, ROT0, "Williams", "Blaster");

    public static GameDriver driver_mysticm = new GameDriver("1983", "mysticm", "williams.java", rom_mysticm, null, machine_driver_williams2, input_ports_mysticm, init_mysticm, ROT0, "Williams", "Mystic Marathon");
    public static GameDriver driver_tshoot = new GameDriver("1984", "tshoot", "williams.java", rom_tshoot, null, machine_driver_williams2, input_ports_tshoot, init_tshoot, ROT0, "Williams", "Turkey Shoot");
    public static GameDriver driver_inferno = new GameDriver("1984", "inferno", "williams.java", rom_inferno, null, machine_driver_williams2, input_ports_inferno, init_inferno, ROT0, "Williams", "Inferno", GAME_IMPERFECT_SOUND);
    public static GameDriver driver_joust2 = new GameDriver("1986", "joust2", "williams.java", rom_joust2, null, machine_driver_joust2, input_ports_joust2, init_joust2, ROT270, "Williams", "Joust 2 - Survival of the Fittest (set 1)");

    public static GameDriver driver_lottofun = new GameDriver("1987", "lottofun", "williams.java", rom_lottofun, null, machine_driver_williams, input_ports_lottofun, init_lottofun, ROT0, "H.A.R. Management", "Lotto Fun");
}
