/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.drivers;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.mame.driverH.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.sound.namco.*;
import static gr.codebb.arcadeflex.sound.namcoH.*;
import static gr.codebb.arcadeflex.sound.samplesH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.machine.xevious.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.sndintrfH.*;
import static gr.codebb.arcadeflex.v037b7.vidhrdw.xevious.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;

public class xevious {

    static MemoryReadAddress readmem_cpu1[]
            = {
                new MemoryReadAddress(0x0000, 0x3fff, MRA_ROM),
                new MemoryReadAddress(0x6800, 0x6807, xevious_dsw_r),
                new MemoryReadAddress(0x7000, 0x700f, xevious_customio_data_r),
                new MemoryReadAddress(0x7100, 0x7100, xevious_customio_r),
                new MemoryReadAddress(0x7800, 0xcfff, xevious_sharedram_r),
                new MemoryReadAddress(0xf000, 0xffff, xevious_bb_r),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryReadAddress readmem_cpu2[]
            = {
                new MemoryReadAddress(0x0000, 0x1fff, MRA_ROM),
                new MemoryReadAddress(0x6800, 0x6807, xevious_dsw_r),
                new MemoryReadAddress(0x7800, 0xcfff, xevious_sharedram_r),
                new MemoryReadAddress(0xf000, 0xffff, xevious_bb_r),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryReadAddress readmem_cpu3[]
            = {
                new MemoryReadAddress(0x0000, 0x0fff, MRA_ROM),
                new MemoryReadAddress(0x7800, 0xcfff, xevious_sharedram_r),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress writemem_cpu1[]
            = {
                new MemoryWriteAddress(0x0000, 0x3fff, MWA_ROM),
                new MemoryWriteAddress(0x6820, 0x6820, xevious_interrupt_enable_1_w),
                new MemoryWriteAddress(0x6821, 0x6821, xevious_interrupt_enable_2_w),
                new MemoryWriteAddress(0x6822, 0x6822, xevious_interrupt_enable_3_w),
                new MemoryWriteAddress(0x6823, 0x6823, xevious_halt_w), /* reset controll */
                new MemoryWriteAddress(0x6830, 0x683f, MWA_NOP), /* watch dock reset */
                new MemoryWriteAddress(0x7000, 0x700f, xevious_customio_data_w),
                new MemoryWriteAddress(0x7100, 0x7100, xevious_customio_w),
                new MemoryWriteAddress(0x7800, 0xafff, xevious_sharedram_w, xevious_sharedram),
                new MemoryWriteAddress(0xb000, 0xb7ff, xevious_fg_colorram_w, xevious_fg_colorram),
                new MemoryWriteAddress(0xb800, 0xbfff, xevious_bg_colorram_w, xevious_bg_colorram),
                new MemoryWriteAddress(0xc000, 0xc7ff, xevious_fg_videoram_w, xevious_fg_videoram),
                new MemoryWriteAddress(0xc800, 0xcfff, xevious_bg_videoram_w, xevious_bg_videoram),
                new MemoryWriteAddress(0xd000, 0xd07f, xevious_vh_latch_w), /* ?? */
                new MemoryWriteAddress(0xf000, 0xffff, xevious_bs_w),
                new MemoryWriteAddress(0x8780, 0x87ff, MWA_RAM, spriteram_2), /* here only */
                new MemoryWriteAddress(0x9780, 0x97ff, MWA_RAM, spriteram_3), /* to initialize */
                new MemoryWriteAddress(0xa780, 0xa7ff, MWA_RAM, spriteram, spriteram_size), /* the pointers */
                new MemoryWriteAddress(-1) /* end of table */};

    static MemoryWriteAddress writemem_cpu2[]
            = {
                new MemoryWriteAddress(0x0000, 0x1fff, MWA_ROM),
                new MemoryWriteAddress(0x6830, 0x683f, MWA_NOP), /* watch dog reset */
                new MemoryWriteAddress(0x7800, 0xafff, xevious_sharedram_w),
                new MemoryWriteAddress(0xb000, 0xb7ff, xevious_fg_colorram_w),
                new MemoryWriteAddress(0xb800, 0xbfff, xevious_bg_colorram_w),
                new MemoryWriteAddress(0xc000, 0xc7ff, xevious_fg_videoram_w),
                new MemoryWriteAddress(0xc800, 0xcfff, xevious_bg_videoram_w),
                new MemoryWriteAddress(0xd000, 0xd07f, xevious_vh_latch_w), /* ?? */
                new MemoryWriteAddress(0xf000, 0xffff, xevious_bs_w),
                new MemoryWriteAddress(-1) /* end of table */};

    static MemoryWriteAddress writemem_cpu3[]
            = {
                new MemoryWriteAddress(0x0000, 0x0fff, MWA_ROM),
                new MemoryWriteAddress(0x6800, 0x681f, pengo_sound_w, namco_soundregs),
                new MemoryWriteAddress(0x6822, 0x6822, xevious_interrupt_enable_3_w),
                new MemoryWriteAddress(0x7800, 0xcfff, xevious_sharedram_w),
                new MemoryWriteAddress(-1) /* end of table */};

    static InputPortPtr input_ports_xevious = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* DSW0 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_BUTTON2);
            PORT_DIPNAME(0x02, 0x02, "Flags Award Bonus Life");
            PORT_DIPSETTING(0x00, DEF_STR("No"));
            PORT_DIPSETTING(0x02, DEF_STR("Yes"));
            PORT_DIPNAME(0x0c, 0x0c, "Right Coin");
            PORT_DIPSETTING(0x0c, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x08, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0x04, DEF_STR("1C_3C"));
            PORT_DIPSETTING(0x00, DEF_STR("1C_6C"));
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL);
            PORT_DIPNAME(0x60, 0x60, DEF_STR("Difficulty"));
            PORT_DIPSETTING(0x40, "Easy");
            PORT_DIPSETTING(0x60, "Normal");
            PORT_DIPSETTING(0x20, "Hard");
            PORT_DIPSETTING(0x00, "Hardest");
            PORT_DIPNAME(0x80, 0x80, "Freeze?");
            PORT_DIPSETTING(0x80, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));

            PORT_START();
            /* DSW1 */
            PORT_DIPNAME(0x03, 0x03, "Left Coin");
            PORT_DIPSETTING(0x01, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0x03, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x00, DEF_STR("2C_3C"));
            PORT_DIPSETTING(0x02, DEF_STR("1C_2C"));
            /* TODO: bonus scores are different for 5 lives */
            PORT_DIPNAME(0x1c, 0x1c, DEF_STR("Bonus_Life"));
            PORT_DIPSETTING(0x18, "10K 40K 40K");
            PORT_DIPSETTING(0x14, "10K 50K 50K");
            PORT_DIPSETTING(0x10, "20K 50K 50K");
            PORT_DIPSETTING(0x0c, "20K 70K 70K");
            PORT_DIPSETTING(0x08, "20K 80K 80K");
            PORT_DIPSETTING(0x1c, "20K 60K 60K");
            PORT_DIPSETTING(0x04, "20K 60K");
            PORT_DIPSETTING(0x00, "None");
            PORT_DIPNAME(0x60, 0x60, DEF_STR("Lives"));
            PORT_DIPSETTING(0x40, "1");
            PORT_DIPSETTING(0x20, "2");
            PORT_DIPSETTING(0x60, "3");
            PORT_DIPSETTING(0x00, "5");
            PORT_DIPNAME(0x80, 0x80, DEF_STR("Cabinet"));
            PORT_DIPSETTING(0x80, DEF_STR("Upright"));
            PORT_DIPSETTING(0x00, DEF_STR("Cocktail"));

            PORT_START();
            /* FAKE */
 /* The player inputs are not memory mapped, they are handled by an I/O chip. */
 /* These fake input ports are read by galaga_customio_data_r() */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY);
            PORT_BIT_IMPULSE(0x10, IP_ACTIVE_LOW, IPT_BUTTON1, 1);
            PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1, null, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS);
            PORT_BIT(0xc0, IP_ACTIVE_LOW, IPT_UNUSED);

            PORT_START();
            /* FAKE */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT_IMPULSE(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, 1);
            PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, null, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS);
            PORT_BIT(0xc0, IP_ACTIVE_LOW, IPT_UNUSED);

            PORT_START();
            /* FAKE */
            PORT_BIT(0x03, IP_ACTIVE_LOW, IPT_UNUSED);
            PORT_BIT_IMPULSE(0x04, IP_ACTIVE_LOW, IPT_START1, 1);
            PORT_BIT_IMPULSE(0x08, IP_ACTIVE_LOW, IPT_START2, 1);
            PORT_BIT_IMPULSE(0x10, IP_ACTIVE_LOW, IPT_COIN1, 1);
            PORT_BIT_IMPULSE(0x20, IP_ACTIVE_LOW, IPT_COIN2, 1);
            PORT_BIT_IMPULSE(0x40, IP_ACTIVE_LOW, IPT_COIN3, 1);
            PORT_SERVICE(0x80, IP_ACTIVE_LOW);
            INPUT_PORTS_END();
        }
    };

    /* same as xevious, the only difference is DSW0 bit 7 */
    static InputPortPtr input_ports_xeviousa = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* DSW0 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_BUTTON2);
            PORT_DIPNAME(0x02, 0x02, "Flags Award Bonus Life");
            PORT_DIPSETTING(0x00, DEF_STR("No"));
            PORT_DIPSETTING(0x02, DEF_STR("Yes"));
            PORT_DIPNAME(0x0c, 0x0c, "Right Coin");
            PORT_DIPSETTING(0x0c, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x08, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0x04, DEF_STR("1C_3C"));
            PORT_DIPSETTING(0x00, DEF_STR("1C_6C"));
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL);
            PORT_DIPNAME(0x60, 0x60, DEF_STR("Difficulty"));
            PORT_DIPSETTING(0x40, "Easy");
            PORT_DIPSETTING(0x60, "Normal");
            PORT_DIPSETTING(0x20, "Hard");
            PORT_DIPSETTING(0x00, "Hardest");
            /* when switch is on Namco, high score names are 10 letters long */
            PORT_DIPNAME(0x80, 0x80, "Copyright");
            PORT_DIPSETTING(0x00, "Namco");
            PORT_DIPSETTING(0x80, "Atari/Namco");

            PORT_START();
            /* DSW1 */
            PORT_DIPNAME(0x03, 0x03, "Left Coin");
            PORT_DIPSETTING(0x01, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0x03, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x00, DEF_STR("2C_3C"));
            PORT_DIPSETTING(0x02, DEF_STR("1C_2C"));
            /* TODO: bonus scores are different for 5 lives */
            PORT_DIPNAME(0x1c, 0x1c, DEF_STR("Bonus_Life"));
            PORT_DIPSETTING(0x18, "10K 40K 40K");
            PORT_DIPSETTING(0x14, "10K 50K 50K");
            PORT_DIPSETTING(0x10, "20K 50K 50K");
            PORT_DIPSETTING(0x0c, "20K 70K 70K");
            PORT_DIPSETTING(0x08, "20K 80K 80K");
            PORT_DIPSETTING(0x1c, "20K 60K 60K");
            PORT_DIPSETTING(0x04, "20K 60K");
            PORT_DIPSETTING(0x00, "None");
            PORT_DIPNAME(0x60, 0x60, DEF_STR("Lives"));
            PORT_DIPSETTING(0x40, "1");
            PORT_DIPSETTING(0x20, "2");
            PORT_DIPSETTING(0x60, "3");
            PORT_DIPSETTING(0x00, "5");
            PORT_DIPNAME(0x80, 0x80, DEF_STR("Cabinet"));
            PORT_DIPSETTING(0x80, DEF_STR("Upright"));
            PORT_DIPSETTING(0x00, DEF_STR("Cocktail"));

            PORT_START();
            /* FAKE */
 /* The player inputs are not memory mapped, they are handled by an I/O chip. */
 /* These fake input ports are read by galaga_customio_data_r() */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY);
            PORT_BIT_IMPULSE(0x10, IP_ACTIVE_LOW, IPT_BUTTON1, 1);
            PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1, null, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS);
            PORT_BIT(0xc0, IP_ACTIVE_LOW, IPT_UNUSED);

            PORT_START();
            /* FAKE */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT_IMPULSE(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, 1);
            PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, null, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS);
            PORT_BIT(0xc0, IP_ACTIVE_LOW, IPT_UNUSED);

            PORT_START();
            /* FAKE */
            PORT_BIT(0x03, IP_ACTIVE_LOW, IPT_UNUSED);
            PORT_BIT_IMPULSE(0x04, IP_ACTIVE_LOW, IPT_START1, 1);
            PORT_BIT_IMPULSE(0x08, IP_ACTIVE_LOW, IPT_START2, 1);
            PORT_BIT_IMPULSE(0x10, IP_ACTIVE_LOW, IPT_COIN1, 1);
            PORT_BIT_IMPULSE(0x20, IP_ACTIVE_LOW, IPT_COIN2, 1);
            PORT_BIT_IMPULSE(0x40, IP_ACTIVE_LOW, IPT_COIN3, 1);
            PORT_SERVICE(0x80, IP_ACTIVE_LOW);
            INPUT_PORTS_END();
        }
    };

    /* same as xevious, the only difference is DSW0 bit 7. Note that the bit is */
 /* inverted wrt xevious. */
    static InputPortPtr input_ports_sxevious = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* DSW0 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_BUTTON2);
            PORT_DIPNAME(0x02, 0x02, "Flags Award Bonus Life");
            PORT_DIPSETTING(0x00, DEF_STR("No"));
            PORT_DIPSETTING(0x02, DEF_STR("Yes"));
            PORT_DIPNAME(0x0c, 0x0c, "Right Coin");
            PORT_DIPSETTING(0x0c, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x08, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0x04, DEF_STR("1C_3C"));
            PORT_DIPSETTING(0x00, DEF_STR("1C_6C"));
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL);
            PORT_DIPNAME(0x60, 0x60, DEF_STR("Difficulty"));
            PORT_DIPSETTING(0x40, "Easy");
            PORT_DIPSETTING(0x60, "Normal");
            PORT_DIPSETTING(0x20, "Hard");
            PORT_DIPSETTING(0x00, "Hardest");
            PORT_DIPNAME(0x80, 0x00, "Freeze?");
            PORT_DIPSETTING(0x00, DEF_STR("Off"));
            PORT_DIPSETTING(0x80, DEF_STR("On"));

            PORT_START();
            /* DSW1 */
            PORT_DIPNAME(0x03, 0x03, "Left Coin");
            PORT_DIPSETTING(0x01, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0x03, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x00, DEF_STR("2C_3C"));
            PORT_DIPSETTING(0x02, DEF_STR("1C_2C"));
            /* TODO: bonus scores are different for 5 lives */
            PORT_DIPNAME(0x1c, 0x1c, DEF_STR("Bonus_Life"));
            PORT_DIPSETTING(0x18, "10K 40K 40K");
            PORT_DIPSETTING(0x14, "10K 50K 50K");
            PORT_DIPSETTING(0x10, "20K 50K 50K");
            PORT_DIPSETTING(0x0c, "20K 70K 70K");
            PORT_DIPSETTING(0x08, "20K 80K 80K");
            PORT_DIPSETTING(0x1c, "20K 60K 60K");
            PORT_DIPSETTING(0x04, "20K 60K");
            PORT_DIPSETTING(0x00, "None");
            PORT_DIPNAME(0x60, 0x60, DEF_STR("Lives"));
            PORT_DIPSETTING(0x40, "1");
            PORT_DIPSETTING(0x20, "2");
            PORT_DIPSETTING(0x60, "3");
            PORT_DIPSETTING(0x00, "5");
            PORT_DIPNAME(0x80, 0x80, DEF_STR("Cabinet"));
            PORT_DIPSETTING(0x80, DEF_STR("Upright"));
            PORT_DIPSETTING(0x00, DEF_STR("Cocktail"));

            PORT_START();
            /* FAKE */
 /* The player inputs are not memory mapped, they are handled by an I/O chip. */
 /* These fake input ports are read by galaga_customio_data_r() */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY);
            PORT_BIT_IMPULSE(0x10, IP_ACTIVE_LOW, IPT_BUTTON1, 1);
            PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1, null, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS);
            PORT_BIT(0xc0, IP_ACTIVE_LOW, IPT_UNUSED);

            PORT_START();
            /* FAKE */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL);
            PORT_BIT_IMPULSE(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, 1);
            PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, null, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS);
            PORT_BIT(0xc0, IP_ACTIVE_LOW, IPT_UNUSED);

            PORT_START();
            /* FAKE */
            PORT_BIT(0x03, IP_ACTIVE_LOW, IPT_UNUSED);
            PORT_BIT_IMPULSE(0x04, IP_ACTIVE_LOW, IPT_START1, 1);
            PORT_BIT_IMPULSE(0x08, IP_ACTIVE_LOW, IPT_START2, 1);
            PORT_BIT_IMPULSE(0x10, IP_ACTIVE_LOW, IPT_COIN1, 1);
            PORT_BIT_IMPULSE(0x20, IP_ACTIVE_LOW, IPT_COIN2, 1);
            PORT_BIT_IMPULSE(0x40, IP_ACTIVE_LOW, IPT_COIN3, 1);
            PORT_SERVICE(0x80, IP_ACTIVE_LOW);
            INPUT_PORTS_END();
        }
    };

    /* foreground characters */
    static GfxLayout charlayout = new GfxLayout(
            8, 8, /* 8*8 characters */
            512, /* 512 characters */
            1, /* 1 bit per pixel */
            new int[]{0},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7},
            new int[]{0 * 8, 1 * 8, 2 * 8, 3 * 8, 4 * 8, 5 * 8, 6 * 8, 7 * 8},
            8 * 8 /* every char takes 8 consecutive bytes */
    );
    /* background tiles */
    static GfxLayout bgcharlayout = new GfxLayout(
            8, 8, /* 8*8 characters */
            512, /* 512 characters */
            2, /* 2 bits per pixel */
            new int[]{0, 512 * 8 * 8},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7},
            new int[]{0 * 8, 1 * 8, 2 * 8, 3 * 8, 4 * 8, 5 * 8, 6 * 8, 7 * 8},
            8 * 8 /* every char takes 8 consecutive bytes */
    );
    /* sprite set #1 */
    static GfxLayout spritelayout1 = new GfxLayout(
            16, 16, /* 16*16 sprites */
            128, /* 128 sprites */
            3, /* 3 bits per pixel */
            new int[]{128 * 64 * 8 + 4, 0, 4},
            new int[]{0, 1, 2, 3, 8 * 8 + 0, 8 * 8 + 1, 8 * 8 + 2, 8 * 8 + 3,
                16 * 8 + 0, 16 * 8 + 1, 16 * 8 + 2, 16 * 8 + 3, 24 * 8 + 0, 24 * 8 + 1, 24 * 8 + 2, 24 * 8 + 3},
            new int[]{0 * 8, 1 * 8, 2 * 8, 3 * 8, 4 * 8, 5 * 8, 6 * 8, 7 * 8,
                32 * 8, 33 * 8, 34 * 8, 35 * 8, 36 * 8, 37 * 8, 38 * 8, 39 * 8},
            64 * 8 /* every sprite takes 128 consecutive bytes */
    );
    /* sprite set #2 */
    static GfxLayout spritelayout2 = new GfxLayout(
            16, 16, /* 16*16 sprites */
            128, /* 128 sprites */
            3, /* 3 bits per pixel */
            new int[]{0, 128 * 64 * 8, 128 * 64 * 8 + 4},
            new int[]{0, 1, 2, 3, 8 * 8 + 0, 8 * 8 + 1, 8 * 8 + 2, 8 * 8 + 3,
                16 * 8 + 0, 16 * 8 + 1, 16 * 8 + 2, 16 * 8 + 3, 24 * 8 + 0, 24 * 8 + 1, 24 * 8 + 2, 24 * 8 + 3},
            new int[]{0 * 8, 1 * 8, 2 * 8, 3 * 8, 4 * 8, 5 * 8, 6 * 8, 7 * 8,
                32 * 8, 33 * 8, 34 * 8, 35 * 8, 36 * 8, 37 * 8, 38 * 8, 39 * 8},
            64 * 8 /* every sprite takes 128 consecutive bytes */
    );
    /* sprite set #3 */
    static GfxLayout spritelayout3 = new GfxLayout(
            16, 16, /* 16*16 sprites */
            64, /* 64 sprites */
            3, /* 3 bits per pixel (one is always 0) */
            new int[]{64 * 64 * 8, 0, 4},
            new int[]{0, 1, 2, 3, 8 * 8 + 0, 8 * 8 + 1, 8 * 8 + 2, 8 * 8 + 3,
                16 * 8 + 0, 16 * 8 + 1, 16 * 8 + 2, 16 * 8 + 3, 24 * 8 + 0, 24 * 8 + 1, 24 * 8 + 2, 24 * 8 + 3},
            new int[]{0 * 8, 1 * 8, 2 * 8, 3 * 8, 4 * 8, 5 * 8, 6 * 8, 7 * 8,
                32 * 8, 33 * 8, 34 * 8, 35 * 8, 36 * 8, 37 * 8, 38 * 8, 39 * 8},
            64 * 8 /* every sprite takes 64 consecutive bytes */
    );

    static GfxDecodeInfo gfxdecodeinfo[]
            = {
                new GfxDecodeInfo(REGION_GFX1, 0x0000, charlayout, 128 * 4 + 64 * 8, 64),
                new GfxDecodeInfo(REGION_GFX2, 0x0000, bgcharlayout, 0, 128),
                new GfxDecodeInfo(REGION_GFX3, 0x0000, spritelayout1, 128 * 4, 64),
                new GfxDecodeInfo(REGION_GFX3, 0x2000, spritelayout2, 128 * 4, 64),
                new GfxDecodeInfo(REGION_GFX3, 0x6000, spritelayout3, 128 * 4, 64),
                new GfxDecodeInfo(-1) /* end of array */};

    static namco_interface namco_interface = new namco_interface(
            3072000 / 32, /* sample rate */
            3, /* number of voices */
            100, /* playback volume */
            REGION_SOUND1 /* memory region */
    );

    static String xevious_sample_names[]
            = {
                "*xevious",
                "explo1.wav", /* ground target explosion */
                "explo2.wav", /* Solvalou explosion */
                null /* end of array */};

    static Samplesinterface samples_interface = new Samplesinterface(
            1, /* one channel */
            80, /* volume */
            xevious_sample_names
    );

    static MachineDriver machine_driver_xevious = new MachineDriver(
            /* basic machine hardware */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_Z80,
                        3125000, /* 3.125 MHz (?) */
                        readmem_cpu1, writemem_cpu1, null, null,
                        xevious_interrupt_1, 1
                ),
                new MachineCPU(
                        CPU_Z80,
                        3125000, /* 3.125 MHz */
                        readmem_cpu2, writemem_cpu2, null, null,
                        xevious_interrupt_2, 1
                ),
                new MachineCPU(
                        CPU_Z80,
                        3125000, /* 3.125 MHz */
                        readmem_cpu3, writemem_cpu3, null, null,
                        xevious_interrupt_3, 2
                )
            },
            60, DEFAULT_60HZ_VBLANK_DURATION, /* frames per second, vblank duration */
            100, /* 100 CPU slices per frame - an high value to ensure proper */
            /* synchronization of the CPUs */
            xevious_init_machine,
            /* video hardware */
            36 * 8, 28 * 8, new rectangle(0 * 8, 36 * 8 - 1, 0 * 8, 28 * 8 - 1),
            gfxdecodeinfo,
            128 + 1, 128 * 4 + 64 * 8 + 64 * 2,
            xevious_vh_convert_color_prom,
            VIDEO_TYPE_RASTER,
            null,
            xevious_vh_start,
            null,
            xevious_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_NAMCO,
                        namco_interface
                ),
                new MachineSound(
                        SOUND_SAMPLES,
                        samples_interface
                )
            }
    );

    /**
     * *************************************************************************
     *
     * Game driver(s)
     *
     **************************************************************************
     */
    static RomLoadPtr rom_xevious = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);/* 64k for the first CPU */
            ROM_LOAD("xvi_1.3p", 0x0000, 0x1000, 0x09964dda);
            ROM_LOAD("xvi_2.3m", 0x1000, 0x1000, 0x60ecce84);
            ROM_LOAD("xvi_3.2m", 0x2000, 0x1000, 0x79754b7d);
            ROM_LOAD("xvi_4.2l", 0x3000, 0x1000, 0xc7d4bbf0);

            ROM_REGION(0x10000, REGION_CPU2);/* 64k for the second CPU */
            ROM_LOAD("xvi_5.3f", 0x0000, 0x1000, 0xc85b703f);
            ROM_LOAD("xvi_6.3j", 0x1000, 0x1000, 0xe18cdaad);

            ROM_REGION(0x10000, REGION_CPU3);/* 64k for the audio CPU */
            ROM_LOAD("xvi_7.2c", 0x0000, 0x1000, 0xdd35cf1c);

            ROM_REGION(0x1000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_12.3b", 0x0000, 0x1000, 0x088c8b26);/* foreground characters */

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_13.3c", 0x0000, 0x1000, 0xde60ba25);/* bg pattern B0 */
            ROM_LOAD("xvi_14.3d", 0x1000, 0x1000, 0x535cdbbc);/* bg pattern B1 */

            ROM_REGION(0x8000, REGION_GFX3 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_15.4m", 0x0000, 0x2000, 0xdc2c0ecb);/* sprite set #1, planes 0/1 */
            ROM_LOAD("xvi_18.4r", 0x2000, 0x2000, 0x02417d19);/* sprite set #1, plane 2, set #2, plane 0 */
            ROM_LOAD("xvi_17.4p", 0x4000, 0x2000, 0xdfb587ce);/* sprite set #2, planes 1/2 */
            ROM_LOAD("xvi_16.4n", 0x6000, 0x1000, 0x605ca889);/* sprite set #3, planes 0/1 */
 /* 0xa000-0xafff empty space to decode sprite set #3 as 3 bits per pixel */

            ROM_REGION(0x4000, REGION_GFX4);/* background tilemaps */
            ROM_LOAD("xvi_9.2a", 0x0000, 0x1000, 0x57ed9879);
            ROM_LOAD("xvi_10.2b", 0x1000, 0x2000, 0xae3ba9e5);
            ROM_LOAD("xvi_11.2c", 0x3000, 0x1000, 0x31e244dd);

            ROM_REGION(0x0b00, REGION_PROMS);
            ROM_LOAD("xvi_8bpr.6a", 0x0000, 0x0100, 0x5cc2727f);/* palette red component */
            ROM_LOAD("xvi_9bpr.6d", 0x0100, 0x0100, 0x5c8796cc);/* palette green component */
            ROM_LOAD("xvi10bpr.6e", 0x0200, 0x0100, 0x3cb60975);/* palette blue component */
            ROM_LOAD("xvi_7bpr.4h", 0x0300, 0x0200, 0x22d98032);/* bg tiles lookup table low bits */
            ROM_LOAD("xvi_6bpr.4f", 0x0500, 0x0200, 0x3a7599f0);/* bg tiles lookup table high bits */
            ROM_LOAD("xvi_4bpr.3l", 0x0700, 0x0200, 0xfd8b9d91);/* sprite lookup table low bits */
            ROM_LOAD("xvi_5bpr.3m", 0x0900, 0x0200, 0xbf906d82);/* sprite lookup table high bits */

            ROM_REGION(0x0200, REGION_SOUND1);/* sound PROMs */
            ROM_LOAD("xvi_2bpr.7n", 0x0000, 0x0100, 0x550f06bc);
            ROM_LOAD("xvi_1bpr.5n", 0x0100, 0x0100, 0x77245b66);/* timing - not used */
            ROM_END();
        }
    };

    static RomLoadPtr rom_xeviousa = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);/* 64k for the first CPU */
            ROM_LOAD("xea-1m-a.bin", 0x0000, 0x2000, 0x8c2b50ec);
            ROM_LOAD("xea-1l-a.bin", 0x2000, 0x2000, 0x0821642b);

            ROM_REGION(0x10000, REGION_CPU2);/* 64k for the second CPU */
            ROM_LOAD("xea-4c-a.bin", 0x0000, 0x2000, 0x14d8fa03);

            ROM_REGION(0x10000, REGION_CPU3);/* 64k for the audio CPU */
            ROM_LOAD("xvi_7.2c", 0x0000, 0x1000, 0xdd35cf1c);

            ROM_REGION(0x1000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_12.3b", 0x0000, 0x1000, 0x088c8b26);/* foreground characters */

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_13.3c", 0x0000, 0x1000, 0xde60ba25);/* bg pattern B0 */
            ROM_LOAD("xvi_14.3d", 0x1000, 0x1000, 0x535cdbbc);/* bg pattern B1 */

            ROM_REGION(0x8000, REGION_GFX3 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_15.4m", 0x0000, 0x2000, 0xdc2c0ecb);/* sprite set #1, planes 0/1 */
            ROM_LOAD("xvi_18.4r", 0x2000, 0x2000, 0x02417d19);/* sprite set #1, plane 2, set #2, plane 0 */
            ROM_LOAD("xvi_17.4p", 0x4000, 0x2000, 0xdfb587ce);/* sprite set #2, planes 1/2 */
            ROM_LOAD("xvi_16.4n", 0x6000, 0x1000, 0x605ca889);/* sprite set #3, planes 0/1 */
 /* 0xa000-0xafff empty space to decode sprite set #3 as 3 bits per pixel */

            ROM_REGION(0x4000, REGION_GFX4);/* background tilemaps */
            ROM_LOAD("xvi_9.2a", 0x0000, 0x1000, 0x57ed9879);
            ROM_LOAD("xvi_10.2b", 0x1000, 0x2000, 0xae3ba9e5);
            ROM_LOAD("xvi_11.2c", 0x3000, 0x1000, 0x31e244dd);

            ROM_REGION(0x0b00, REGION_PROMS);
            ROM_LOAD("xvi_8bpr.6a", 0x0000, 0x0100, 0x5cc2727f);/* palette red component */
            ROM_LOAD("xvi_9bpr.6d", 0x0100, 0x0100, 0x5c8796cc);/* palette green component */
            ROM_LOAD("xvi10bpr.6e", 0x0200, 0x0100, 0x3cb60975);/* palette blue component */
            ROM_LOAD("xvi_7bpr.4h", 0x0300, 0x0200, 0x22d98032);/* bg tiles lookup table low bits */
            ROM_LOAD("xvi_6bpr.4f", 0x0500, 0x0200, 0x3a7599f0);/* bg tiles lookup table high bits */
            ROM_LOAD("xvi_4bpr.3l", 0x0700, 0x0200, 0xfd8b9d91);/* sprite lookup table low bits */
            ROM_LOAD("xvi_5bpr.3m", 0x0900, 0x0200, 0xbf906d82);/* sprite lookup table high bits */

            ROM_REGION(0x0200, REGION_SOUND1);/* sound PROMs */
            ROM_LOAD("xvi_2bpr.7n", 0x0000, 0x0100, 0x550f06bc);
            ROM_LOAD("xvi_1bpr.5n", 0x0100, 0x0100, 0x77245b66);/* timing - not used */
            ROM_END();
        }
    };

    static RomLoadPtr rom_xevios = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);/* 64k for the first CPU */
            ROM_LOAD("4.7h", 0x0000, 0x1000, 0x1f8ca4c0);
            ROM_LOAD("5.6h", 0x1000, 0x1000, 0x2e47ce8f);
            ROM_LOAD("xvi_3.2m", 0x2000, 0x1000, 0x79754b7d);
            ROM_LOAD("7.4h", 0x3000, 0x1000, 0x7033f2e3);

            ROM_REGION(0x10000, REGION_CPU2);/* 64k for the second CPU */
            ROM_LOAD("xvi_5.3f", 0x0000, 0x1000, 0xc85b703f);
            ROM_LOAD("xvi_6.3j", 0x1000, 0x1000, 0xe18cdaad);

            ROM_REGION(0x10000, REGION_CPU3);/* 64k for the audio CPU */
            ROM_LOAD("xvi_7.2c", 0x0000, 0x1000, 0xdd35cf1c);

            ROM_REGION(0x1000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_12.3b", 0x0000, 0x1000, 0x088c8b26);/* foreground characters */

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_13.3c", 0x0000, 0x1000, 0xde60ba25);/* bg pattern B0 */
            ROM_LOAD("xvi_14.3d", 0x1000, 0x1000, 0x535cdbbc);/* bg pattern B1 */

            ROM_REGION(0x8000, REGION_GFX3 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_15.4m", 0x0000, 0x2000, 0xdc2c0ecb);/* sprite set #1, planes 0/1 */
            ROM_LOAD("16.8d", 0x2000, 0x2000, 0x44262c04);/* sprite set #1, plane 2, set #2, plane 0 */
            ROM_LOAD("xvi_17.4p", 0x4000, 0x2000, 0xdfb587ce);/* sprite set #2, planes 1/2 */
            ROM_LOAD("xvi_16.4n", 0x6000, 0x1000, 0x605ca889);/* sprite set #3, planes 0/1 */
 /* 0xa000-0xafff empty space to decode sprite set #3 as 3 bits per pixel */

            ROM_REGION(0x4000, REGION_GFX4);/* background tilemaps */
            ROM_LOAD("10.1d", 0x0000, 0x1000, 0x10baeebb);
            ROM_LOAD("xvi_10.2b", 0x1000, 0x2000, 0xae3ba9e5);
            ROM_LOAD("12.3d", 0x3000, 0x1000, 0x51a4e83b);

            ROM_REGION(0x0b00, REGION_PROMS);
            ROM_LOAD("xvi_8bpr.6a", 0x0000, 0x0100, 0x5cc2727f);/* palette red component */
            ROM_LOAD("xvi_9bpr.6d", 0x0100, 0x0100, 0x5c8796cc);/* palette green component */
            ROM_LOAD("xvi10bpr.6e", 0x0200, 0x0100, 0x3cb60975);/* palette blue component */
            ROM_LOAD("xvi_7bpr.4h", 0x0300, 0x0200, 0x22d98032);/* bg tiles lookup table low bits */
            ROM_LOAD("xvi_6bpr.4f", 0x0500, 0x0200, 0x3a7599f0);/* bg tiles lookup table high bits */
            ROM_LOAD("xvi_4bpr.3l", 0x0700, 0x0200, 0xfd8b9d91);/* sprite lookup table low bits */
            ROM_LOAD("xvi_5bpr.3m", 0x0900, 0x0200, 0xbf906d82);/* sprite lookup table high bits */

            ROM_REGION(0x0200, REGION_SOUND1);/* sound PROMs */
            ROM_LOAD("xvi_2bpr.7n", 0x0000, 0x0100, 0x550f06bc);
            ROM_LOAD("xvi_1bpr.5n", 0x0100, 0x0100, 0x77245b66);/* timing - not used */

            ROM_REGION(0x3000, REGION_USER1);
            /* extra ROMs (function unknown, could be emulation of the custom I/O */
 /* chip with a Z80): */
            ROM_LOAD("1.16j", 0x0000, 0x1000, 0x2618f0ce);
            ROM_LOAD("2.17b", 0x1000, 0x2000, 0xde359fac);
            ROM_END();
        }
    };

    static RomLoadPtr rom_sxevious = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);/* 64k for the first CPU */
            ROM_LOAD("cpu_3p.rom", 0x0000, 0x1000, 0x1c8d27d5);
            ROM_LOAD("cpu_3m.rom", 0x1000, 0x1000, 0xfd04e615);
            ROM_LOAD("cpu_2m.rom", 0x2000, 0x1000, 0x294d5404);
            ROM_LOAD("cpu_2l.rom", 0x3000, 0x1000, 0x6a44bf92);

            ROM_REGION(0x10000, REGION_CPU2);/* 64k for the second CPU */
            ROM_LOAD("cpu_3f.rom", 0x0000, 0x1000, 0xd4bd3d81);
            ROM_LOAD("cpu_3j.rom", 0x1000, 0x1000, 0xaf06be5f);

            ROM_REGION(0x10000, REGION_CPU3);/* 64k for the audio CPU */
            ROM_LOAD("xvi_7.2c", 0x0000, 0x1000, 0xdd35cf1c);

            ROM_REGION(0x1000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_12.3b", 0x0000, 0x1000, 0x088c8b26);/* foreground characters */

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_13.3c", 0x0000, 0x1000, 0xde60ba25);/* bg pattern B0 */
            ROM_LOAD("xvi_14.3d", 0x1000, 0x1000, 0x535cdbbc);/* bg pattern B1 */

            ROM_REGION(0x8000, REGION_GFX3 | REGIONFLAG_DISPOSE);
            ROM_LOAD("xvi_15.4m", 0x0000, 0x2000, 0xdc2c0ecb);/* sprite set #1, planes 0/1 */
            ROM_LOAD("xvi_18.4r", 0x2000, 0x2000, 0x02417d19);/* sprite set #1, plane 2, set #2, plane 0 */
            ROM_LOAD("xvi_17.4p", 0x4000, 0x2000, 0xdfb587ce);/* sprite set #2, planes 1/2 */
            ROM_LOAD("xvi_16.4n", 0x6000, 0x1000, 0x605ca889);/* sprite set #3, planes 0/1 */
 /* 0xa000-0xafff empty space to decode sprite set #3 as 3 bits per pixel */

            ROM_REGION(0x4000, REGION_GFX4);/* background tilemaps */
            ROM_LOAD("xvi_9.2a", 0x0000, 0x1000, 0x57ed9879);
            ROM_LOAD("xvi_10.2b", 0x1000, 0x2000, 0xae3ba9e5);
            ROM_LOAD("xvi_11.2c", 0x3000, 0x1000, 0x31e244dd);

            ROM_REGION(0x0b00, REGION_PROMS);
            ROM_LOAD("xvi_8bpr.6a", 0x0000, 0x0100, 0x5cc2727f);/* palette red component */
            ROM_LOAD("xvi_9bpr.6d", 0x0100, 0x0100, 0x5c8796cc);/* palette green component */
            ROM_LOAD("xvi10bpr.6e", 0x0200, 0x0100, 0x3cb60975);/* palette blue component */
            ROM_LOAD("xvi_7bpr.4h", 0x0300, 0x0200, 0x22d98032);/* bg tiles lookup table low bits */
            ROM_LOAD("xvi_6bpr.4f", 0x0500, 0x0200, 0x3a7599f0);/* bg tiles lookup table high bits */
            ROM_LOAD("xvi_4bpr.3l", 0x0700, 0x0200, 0xfd8b9d91);/* sprite lookup table low bits */
            ROM_LOAD("xvi_5bpr.3m", 0x0900, 0x0200, 0xbf906d82);/* sprite lookup table high bits */

            ROM_REGION(0x0200, REGION_SOUND1);/* sound PROMs */
            ROM_LOAD("xvi_2bpr.7n", 0x0000, 0x0100, 0x550f06bc);
            ROM_LOAD("xvi_1bpr.5n", 0x0100, 0x0100, 0x77245b66);/* timing - not used */
            ROM_END();
        }
    };

    public static InitDriverPtr init_xevios = new InitDriverPtr() {
        public void handler() {
            int A, i;

            /* convert one of the sprite ROMs to the format used by Xevious */
            for (A = 0x2000; A < 0x4000; A++) {
                int[] bit = new int[8];
                UBytePtr RAM = memory_region(REGION_GFX3);

                /* 76543210 . 13570246 bit rotation */
                for (i = 0; i < 8; i++) {
                    bit[i] = (RAM.read(A) >> i) & 1;
                }

                RAM.write(A,
                        (bit[6] << 0)
                        + (bit[4] << 1)
                        + (bit[2] << 2)
                        + (bit[0] << 3)
                        + (bit[7] << 4)
                        + (bit[5] << 5)
                        + (bit[3] << 6)
                        + (bit[1] << 7));
            }

            /* convert one of tile map ROMs to the format used by Xevious */
            for (A = 0x0000; A < 0x1000; A++) {
                int[] bit = new int[8];
                UBytePtr RAM = memory_region(REGION_GFX4);

                /* 76543210 . 37512640 bit rotation */
                for (i = 0; i < 8; i++) {
                    bit[i] = (RAM.read(A) >> i) & 1;
                }

                RAM.write(A,
                        (bit[0] << 0)
                        + (bit[4] << 1)
                        + (bit[6] << 2)
                        + (bit[2] << 3)
                        + (bit[1] << 4)
                        + (bit[5] << 5)
                        + (bit[7] << 6)
                        + (bit[3] << 7));
            }
        }
    };

    public static GameDriver driver_xevious = new GameDriver("1982", "xevious", "xevious.java", rom_xevious, null, machine_driver_xevious, input_ports_xevious, null, ROT90, "Namco", "Xevious (Namco)");
    public static GameDriver driver_xeviousa = new GameDriver("1982", "xeviousa", "xevious.java", rom_xeviousa, driver_xevious, machine_driver_xevious, input_ports_xeviousa, null, ROT90, "Namco (Atari license)", "Xevious (Atari)");
    public static GameDriver driver_xevios = new GameDriver("1983", "xevios", "xevious.java", rom_xevios, driver_xevious, machine_driver_xevious, input_ports_xevious, init_xevios, ROT90, "bootleg", "Xevios");
    public static GameDriver driver_sxevious = new GameDriver("1984", "sxevious", "xevious.java", rom_sxevious, driver_xevious, machine_driver_xevious, input_ports_sxevious, null, ROT90, "Namco", "Super Xevious");
}
