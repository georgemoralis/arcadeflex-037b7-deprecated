/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.drivers;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.ay8910.*;
import gr.codebb.arcadeflex.WIP.v037b7.sound.ay8910H.AY8910interface;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.old.mame.inputH.KEYCODE_F1;
import static gr.codebb.arcadeflex.old.sound.mixer.mixer_sound_enable_global_w;
import static gr.codebb.arcadeflex.v037b7.cpu.z80.z80H.Z80_NMI_INT;
import gr.codebb.arcadeflex.v056.mame.timer.timer_callback;
import static gr.codebb.arcadeflex.v056.mame.timer.timer_set;
import static gr.codebb.arcadeflex.v056.mame.timerH.TIME_NOW;
import static gr.codebb.arcadeflex.v037b7.machine.buggychl.*;
import static gr.codebb.arcadeflex.v037b7.vidhrdw.buggychl.*;

public class buggychl {

    public static WriteHandlerPtr bankswitch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            cpu_setbank(1, new UBytePtr(memory_region(REGION_CPU1), 0x10000 + (data & 7) * 0x2000));
        }
    };

    static int sound_nmi_enable, pending_nmi;

    public static timer_callback nmi_callback = new timer_callback() {
        public void handler(int param) {
            if (sound_nmi_enable != 0) {
                cpu_cause_interrupt(1, Z80_NMI_INT);
            } else {
                pending_nmi = 1;
            }
        }
    };

    public static WriteHandlerPtr sound_command_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            soundlatch_w.handler(0, data);
            timer_set(TIME_NOW, data, nmi_callback);
        }
    };

    public static WriteHandlerPtr nmi_disable_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            sound_nmi_enable = 0;
        }
    };

    public static WriteHandlerPtr nmi_enable_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            sound_nmi_enable = 1;
            if (pending_nmi != 0) {
                cpu_cause_interrupt(1, Z80_NMI_INT);
                pending_nmi = 0;
            }
        }
    };

    public static WriteHandlerPtr sound_enable_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            mixer_sound_enable_global_w(data & 1);
        }
    };

    static MemoryReadAddress readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x3fff, MRA_ROM), /* A22-04 (23) */
                new MemoryReadAddress(0x4000, 0x7fff, MRA_ROM), /* A22-05 (22) */
                new MemoryReadAddress(0x8000, 0x87ff, MRA_RAM), /* 6116 SRAM (36) */
                new MemoryReadAddress(0x8800, 0x8fff, MRA_RAM), /* 6116 SRAM (35) */
                new MemoryReadAddress(0xa000, 0xbfff, MRA_BANK1),
                new MemoryReadAddress(0xc800, 0xcfff, videoram_r),
                new MemoryReadAddress(0xd400, 0xd400, buggychl_mcu_r),
                new MemoryReadAddress(0xd401, 0xd401, buggychl_mcu_status_r),
                new MemoryReadAddress(0xd600, 0xd600, input_port_0_r), /* dsw */
                new MemoryReadAddress(0xd601, 0xd601, input_port_1_r), /* dsw */
                new MemoryReadAddress(0xd602, 0xd602, input_port_2_r), /* dsw */
                new MemoryReadAddress(0xd603, 0xd603, input_port_3_r), /* player inputs */
                new MemoryReadAddress(0xd608, 0xd608, input_port_4_r), /* wheel */
                new MemoryReadAddress(0xd609, 0xd609, input_port_5_r), /* coin + accelerator */
                //	new MemoryReadAddress( 0xd60a, 0xd60a, other inputs, not used?
                //	{ 0xd60b, 0xd60b, other inputs, not used?
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x3fff, MWA_ROM), /* A22-04 (23) */
                new MemoryWriteAddress(0x4000, 0x7fff, MWA_ROM), /* A22-05 (22) */
                new MemoryWriteAddress(0x8000, 0x87ff, MWA_RAM), /* 6116 SRAM (36) */
                new MemoryWriteAddress(0x8800, 0x8fff, MWA_RAM), /* 6116 SRAM (35) */
                new MemoryWriteAddress(0x9000, 0x9fff, buggychl_sprite_lookup_w),
                new MemoryWriteAddress(0xa000, 0xbfff, buggychl_chargen_w, buggychl_character_ram),
                new MemoryWriteAddress(0xc800, 0xcfff, videoram_w, videoram, videoram_size),
                //	new MemoryWriteAddress( 0xd000, 0xd000, horizon
                new MemoryWriteAddress(0xd100, 0xd100, buggychl_ctrl_w),
                new MemoryWriteAddress(0xd200, 0xd200, bankswitch_w),
                new MemoryWriteAddress(0xd300, 0xd300, watchdog_reset_w),
                //	{ 0xd301, 0xd301,
                //	{ 0xd302, 0xd302, reset mcu
                new MemoryWriteAddress(0xd303, 0xd303, buggychl_sprite_lookup_bank_w),
                //	{ 0xd304, 0xd307, sccon 1-4
                new MemoryWriteAddress(0xd400, 0xd400, buggychl_mcu_w),
                new MemoryWriteAddress(0xd500, 0xd57f, MWA_RAM, spriteram, spriteram_size),
                new MemoryWriteAddress(0xd610, 0xd610, sound_command_w),
                //	{ 0xd613, 0xd613, reset sound cpu  sound chips
                new MemoryWriteAddress(0xd618, 0xd618, MWA_NOP), /* accelerator clear */
                new MemoryWriteAddress(0xd700, 0xd7ff, paletteram_xxxxRRRRGGGGBBBB_swap_w, paletteram),
                new MemoryWriteAddress(0xd840, 0xd85f, MWA_RAM, buggychl_scrollv),
                new MemoryWriteAddress(0xdb00, 0xdbff, MWA_RAM, buggychl_scrollh),
                new MemoryWriteAddress(0xdc04, 0xdc04, MWA_RAM), /* should be fg scroll */
                new MemoryWriteAddress(0xdc06, 0xdc06, buggychl_bg_scrollx_w),
                new MemoryWriteAddress(-1) /* end of table */};

    static MemoryReadAddress sound_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x3fff, MRA_ROM),
                new MemoryReadAddress(0x4000, 0x47ff, MRA_RAM),
                new MemoryReadAddress(0x5000, 0x5000, soundlatch_r),
                //	new MemoryReadAddress( 0x5001, 0x5001, MRA_RAM ),	/* is command pending? */
                new MemoryReadAddress(0xe000, 0xefff, MRA_ROM), /* space for diagnostics ROM */
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress sound_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x3fff, MWA_ROM),
                new MemoryWriteAddress(0x4000, 0x47ff, MWA_RAM),
                new MemoryWriteAddress(0x4800, 0x4800, AY8910_control_port_0_w),
                new MemoryWriteAddress(0x4801, 0x4801, AY8910_write_port_0_w),
                new MemoryWriteAddress(0x4802, 0x4802, AY8910_control_port_1_w),
                new MemoryWriteAddress(0x4803, 0x4803, AY8910_write_port_1_w),
                new MemoryWriteAddress(0x4810, 0x481d, MWA_RAM), /* MSM5232 registers */
                new MemoryWriteAddress(0x4820, 0x4820, MWA_RAM), /* VOL/BAL   for the 7630 on the MSM5232 output */
                new MemoryWriteAddress(0x4830, 0x4830, MWA_RAM), /* TRBL/BASS for the 7630 on the MSM5232 output  */
                //	new MemoryWriteAddress( 0x5000, 0x5000, MWA_RAM ),	/* to main cpu */
                new MemoryWriteAddress(0x5001, 0x5001, nmi_enable_w),
                new MemoryWriteAddress(0x5002, 0x5002, nmi_disable_w),
                new MemoryWriteAddress(0x5003, 0x5003, sound_enable_w),
                new MemoryWriteAddress(0xe000, 0xefff, MWA_ROM),
                new MemoryWriteAddress(-1) /* end of table */};

    static MemoryReadAddress mcu_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x0000, buggychl_68705_portA_r),
                new MemoryReadAddress(0x0001, 0x0001, buggychl_68705_portB_r),
                new MemoryReadAddress(0x0002, 0x0002, buggychl_68705_portC_r),
                new MemoryReadAddress(0x0010, 0x007f, MRA_RAM),
                new MemoryReadAddress(0x0080, 0x07ff, MRA_ROM),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress mcu_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x0000, buggychl_68705_portA_w),
                new MemoryWriteAddress(0x0001, 0x0001, buggychl_68705_portB_w),
                new MemoryWriteAddress(0x0002, 0x0002, buggychl_68705_portC_w),
                new MemoryWriteAddress(0x0004, 0x0004, buggychl_68705_ddrA_w),
                new MemoryWriteAddress(0x0005, 0x0005, buggychl_68705_ddrB_w),
                new MemoryWriteAddress(0x0006, 0x0006, buggychl_68705_ddrC_w),
                new MemoryWriteAddress(0x0010, 0x007f, MWA_RAM),
                new MemoryWriteAddress(0x0080, 0x07ff, MWA_ROM),
                new MemoryWriteAddress(-1) /* end of table */};

    /**
     * ***************************************************************************
     */
    static InputPortPtr input_ports_buggychl = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_DIPNAME(0x01, 0x01, DEF_STR("Unknown"));
            PORT_DIPSETTING(0x01, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x02, 0x02, DEF_STR("Unknown"));
            PORT_DIPSETTING(0x02, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x04, 0x04, DEF_STR("Free_Play"));
            PORT_DIPSETTING(0x04, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x08, 0x08, DEF_STR("Unknown"));
            PORT_DIPSETTING(0x08, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x10, 0x10, DEF_STR("Unknown"));
            PORT_DIPSETTING(0x10, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_SERVICE(0x20, IP_ACTIVE_LOW);
            PORT_DIPNAME(0x40, 0x40, DEF_STR("Flip_Screen"));
            PORT_DIPSETTING(0x40, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x80, 0x80, DEF_STR("Unknown"));
            PORT_DIPSETTING(0x80, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));

            PORT_START();
            /* IN1 */
            PORT_DIPNAME(0x0f, 0x00, DEF_STR("Coin_A"));
            PORT_DIPSETTING(0x0f, DEF_STR("9C_1C"));
            PORT_DIPSETTING(0x0e, DEF_STR("8C_1C"));
            PORT_DIPSETTING(0x0d, DEF_STR("7C_1C"));
            PORT_DIPSETTING(0x0c, DEF_STR("6C_1C"));
            PORT_DIPSETTING(0x0b, DEF_STR("5C_1C"));
            PORT_DIPSETTING(0x0a, DEF_STR("4C_1C"));
            PORT_DIPSETTING(0x09, DEF_STR("3C_1C"));
            PORT_DIPSETTING(0x08, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0x00, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x01, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0x02, DEF_STR("1C_3C"));
            PORT_DIPSETTING(0x03, DEF_STR("1C_4C"));
            PORT_DIPSETTING(0x04, DEF_STR("1C_5C"));
            PORT_DIPSETTING(0x05, DEF_STR("1C_6C"));
            PORT_DIPSETTING(0x06, DEF_STR("1C_7C"));
            PORT_DIPSETTING(0x07, DEF_STR("1C_8C"));
            PORT_DIPNAME(0xf0, 0x00, DEF_STR("Coin_B"));
            PORT_DIPSETTING(0xf0, DEF_STR("9C_1C"));
            PORT_DIPSETTING(0xe0, DEF_STR("8C_1C"));
            PORT_DIPSETTING(0xd0, DEF_STR("7C_1C"));
            PORT_DIPSETTING(0xc0, DEF_STR("6C_1C"));
            PORT_DIPSETTING(0xb0, DEF_STR("5C_1C"));
            PORT_DIPSETTING(0xa0, DEF_STR("4C_1C"));
            PORT_DIPSETTING(0x90, DEF_STR("3C_1C"));
            PORT_DIPSETTING(0x80, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0x00, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x10, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0x20, DEF_STR("1C_3C"));
            PORT_DIPSETTING(0x30, DEF_STR("1C_4C"));
            PORT_DIPSETTING(0x40, DEF_STR("1C_5C"));
            PORT_DIPSETTING(0x50, DEF_STR("1C_6C"));
            PORT_DIPSETTING(0x60, DEF_STR("1C_7C"));
            PORT_DIPSETTING(0x70, DEF_STR("1C_8C"));

            PORT_START();
            /* IN2 */
            PORT_DIPNAME(0x01, 0x01, DEF_STR("Unknown"));
            PORT_DIPSETTING(0x01, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x02, 0x02, DEF_STR("Unknown"));
            PORT_DIPSETTING(0x02, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x04, 0x04, DEF_STR("Unknown"));
            PORT_DIPSETTING(0x04, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x08, 0x08, DEF_STR("Unknown"));
            PORT_DIPSETTING(0x08, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x10, 0x10, "Coinage Display");
            PORT_DIPSETTING(0x10, "Coins/Credits");
            PORT_DIPSETTING(0x00, "Insert Coin");
            PORT_DIPNAME(0x20, 0x20, "Year Display");
            PORT_DIPSETTING(0x00, DEF_STR("No"));
            PORT_DIPSETTING(0x20, DEF_STR("Yes"));
            PORT_BITX(0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE);
            PORT_DIPSETTING(0x40, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x80, 0x80, DEF_STR("Coinage"));
            PORT_DIPSETTING(0x80, "A and B");
            PORT_DIPSETTING(0x00, "A only");

            PORT_START();
            /* IN3 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_START1);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_BUTTON2);/* shift */
            PORT_BITX(0x10, IP_ACTIVE_HIGH, IPT_SERVICE, "Test Button", KEYCODE_F1, IP_JOY_NONE);
            PORT_BIT(0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN);
            PORT_BIT(0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN);

            PORT_START();
            /* IN4 - wheel */
            PORT_ANALOG(0xff, 0x00, IPT_DIAL | IPF_REVERSE, 30, 15, 0, 0);

            PORT_START();
            /* IN5 */
            PORT_BIT(0x01, IP_ACTIVE_HIGH, IPT_COIN1);
            PORT_BIT(0x02, IP_ACTIVE_HIGH, IPT_COIN2);
            PORT_BIT(0x04, IP_ACTIVE_HIGH, IPT_SERVICE1);
            PORT_BIT(0x08, IP_ACTIVE_HIGH, IPT_TILT);
            PORT_BIT(0xf0, IP_ACTIVE_HIGH, IPT_BUTTON1);/* accelerator */
            INPUT_PORTS_END();
        }
    };

    static GfxLayout charlayout = new GfxLayout(
            8, 8,
            256,
            4,
            new int[]{3 * 0x800 * 8, 2 * 0x800 * 8, 0x800 * 8, 0},
            new int[]{7, 6, 5, 4, 3, 2, 1, 0},
            new int[]{0 * 8, 1 * 8, 2 * 8, 3 * 8, 4 * 8, 5 * 8, 6 * 8, 7 * 8},
            8 * 8
    );

    static GfxLayout spritelayout = new GfxLayout(
            16, 1,
            RGN_FRAC(1, 8),
            4,
            new int[]{RGN_FRAC(3, 4), RGN_FRAC(2, 4), RGN_FRAC(1, 4), RGN_FRAC(0, 4)},
            new int[]{((RGN_FRAC(1, 8) + 7)), ((RGN_FRAC(1, 8) + 7)) + 1 * (-1), ((RGN_FRAC(1, 8) + 7)) + 2 * (-1), ((RGN_FRAC(1, 8) + 7)) + 3 * (-1),
                ((RGN_FRAC(1, 8) + 7)) + 4 * (-1), ((RGN_FRAC(1, 8) + 7)) + 4 * (-1) + 1 * (-1), ((RGN_FRAC(1, 8) + 7)) + 4 * (-1) + 2 * (-1), ((RGN_FRAC(1, 8) + 7)) + 4 * (-1) + 3 * (-1), (7), (7) + 1 * (-1), (7) + 2 * (-1), (7) + 3 * (-1),
                (7) + 4 * (-1), (7) + 4 * (-1) + 1 * (-1), (7) + 4 * (-1) + 2 * (-1), (7) + 4 * (-1) + 3 * (-1)},
            new int[]{0},
            8
    );

    static GfxDecodeInfo gfxdecodeinfo[]
            = {
                new GfxDecodeInfo(0, 0, charlayout, 0, 8), /* decoded at runtime */
                /* sprites are drawn pixel by pixel by draw_sprites() */
                new GfxDecodeInfo(REGION_GFX1, 0, spritelayout, 0, 8),
                new GfxDecodeInfo(-1) /* end of array */};

    public static WriteHandlerPtr portA_0_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* VOL/BAL   for the 7630 on this 8910 output */
        }
    };
    public static WriteHandlerPtr portB_0_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* TRBL/BASS for the 7630 on this 8910 output */
        }
    };
    public static WriteHandlerPtr portA_1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* VOL/BAL   for the 7630 on this 8910 output */
        }
    };
    public static WriteHandlerPtr portB_1_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* TRBL/BASS for the 7630 on this 8910 output */
        }
    };

    static AY8910interface ay8910_interface = new AY8910interface(
            2, /* 2 chips */
            8000000 / 4, /* 2 MHz */
            new int[]{30, 30},
            new ReadHandlerPtr[]{null, null},
            new ReadHandlerPtr[]{null, null},
            new WriteHandlerPtr[]{portA_0_w, portA_1_w},
            new WriteHandlerPtr[]{portB_0_w, portB_1_w}
    );

    static MachineDriver machine_driver_buggychl = new MachineDriver(
            /* basic machine hardware */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_Z80,
                        4000000, /* 4 MHz??? */
                        readmem, writemem, null, null,
                        interrupt, 1
                ),
                new MachineCPU(
                        CPU_Z80 | CPU_AUDIO_CPU,
                        4000000, /* 4 MHz??? */
                        sound_readmem, sound_writemem, null, null,
                        null, 0,
                        interrupt, 60 /* irq is timed, tied to the cpu clock and not to vblank */
                /* nmi is caused by the main cpu */
                ),
                new MachineCPU(
                        CPU_M68705,
                        8000000 / 2, /* 4 MHz */
                        mcu_readmem, mcu_writemem, null, null,
                        ignore_interrupt, 0 /* irq's generated by Z80 */
                )
            },
            60, DEFAULT_REAL_60HZ_VBLANK_DURATION, /* frames per second, vblank duration */
            1, /* interleaving */
            null,
            /* video hardware */
            32 * 8, 32 * 8, new rectangle(0 * 8, 32 * 8 - 1, 2 * 8, 30 * 8 - 1),
            gfxdecodeinfo,
            128 + 128, 128,
            buggychl_init_palette,
            VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
            null,
            buggychl_vh_start,
            buggychl_vh_stop,
            buggychl_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_AY8910,
                        ay8910_interface
                )
            /* there's a MSM5232 too */
            }
    );

    /**
     * *************************************************************************
     *
     * Game driver(s)
     *
     **************************************************************************
     */
    static RomLoadPtr rom_buggychl = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x1c000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("a22-04-2.23", 0x00000, 0x4000, 0x16445a6a);
            ROM_LOAD("a22-05-2.22", 0x04000, 0x4000, 0xd57430b2);
            ROM_LOAD("a22-01.3", 0x10000, 0x4000, 0xaf3b7554);/* banked */
            ROM_LOAD("a22-02.2", 0x14000, 0x4000, 0xb8a645fb);/* banked */
            ROM_LOAD("a22-03.1", 0x18000, 0x4000, 0x5f45d469);/* banked */

            ROM_REGION(0x10000, REGION_CPU2);
            /* sound Z80 */
            ROM_LOAD("a22-24.28", 0x00000, 0x4000, 0x1e7f841f);

            ROM_REGION(0x0800, REGION_CPU3);/* 8k for the microcontroller */
            ROM_LOAD("a22-19.31", 0x00000, 0x0800, 0x06a71df0);

            ROM_REGION(0x20000, REGION_GFX1 | REGIONFLAG_DISPOSE);/* sprites */
            ROM_LOAD("a22-06.111", 0x00000, 0x4000, 0x1df91b17);
            ROM_LOAD("a22-07.110", 0x04000, 0x4000, 0x2f0ab9b7);
            ROM_LOAD("a22-08.109", 0x08000, 0x4000, 0x49cb2134);
            ROM_LOAD("a22-09.108", 0x0c000, 0x4000, 0xe682e200);
            ROM_LOAD("a22-10.107", 0x10000, 0x4000, 0x653b7e25);
            ROM_LOAD("a22-11.106", 0x14000, 0x4000, 0x8057b55c);
            ROM_LOAD("a22-12.105", 0x18000, 0x4000, 0x8b365b24);
            ROM_LOAD("a22-13.104", 0x1c000, 0x4000, 0x2c6d68fe);

            ROM_REGION(0x4000, REGION_GFX2);/* sprite zoom tables */
            ROM_LOAD("a22-14.59", 0x0000, 0x2000, 0xa450b3ef);/* vertical */
            ROM_LOAD("a22-15.115", 0x2000, 0x1000, 0x337a0c14);/* horizontal */
            ROM_LOAD("a22-16.116", 0x3000, 0x1000, 0x337a0c14);/* horizontal */
            ROM_END();
        }
    };

    static RomLoadPtr rom_buggycht = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x1c000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("bu04.bin", 0x00000, 0x4000, 0xf90ab854);
            ROM_LOAD("bu05.bin", 0x04000, 0x4000, 0x543d0949);
            ROM_LOAD("a22-01.3", 0x10000, 0x4000, 0xaf3b7554);/* banked */
            ROM_LOAD("a22-02.2", 0x14000, 0x4000, 0xb8a645fb);/* banked */
            ROM_LOAD("a22-03.1", 0x18000, 0x4000, 0x5f45d469);/* banked */

            ROM_REGION(0x10000, REGION_CPU2);
            /* sound Z80 */
            ROM_LOAD("a22-24.28", 0x00000, 0x4000, 0x1e7f841f);

            ROM_REGION(0x0800, REGION_CPU3);/* 8k for the microcontroller */
            ROM_LOAD("a22-19.31", 0x00000, 0x0800, 0x06a71df0);

            ROM_REGION(0x20000, REGION_GFX1 | REGIONFLAG_DISPOSE);/* sprites */
            ROM_LOAD("a22-06.111", 0x00000, 0x4000, 0x1df91b17);
            ROM_LOAD("a22-07.110", 0x04000, 0x4000, 0x2f0ab9b7);
            ROM_LOAD("a22-08.109", 0x08000, 0x4000, 0x49cb2134);
            ROM_LOAD("a22-09.108", 0x0c000, 0x4000, 0xe682e200);
            ROM_LOAD("a22-10.107", 0x10000, 0x4000, 0x653b7e25);
            ROM_LOAD("a22-11.106", 0x14000, 0x4000, 0x8057b55c);
            ROM_LOAD("a22-12.105", 0x18000, 0x4000, 0x8b365b24);
            ROM_LOAD("a22-13.104", 0x1c000, 0x4000, 0x2c6d68fe);

            ROM_REGION(0x4000, REGION_GFX2);/* sprite zoom tables */
            ROM_LOAD("a22-14.59", 0x0000, 0x2000, 0xa450b3ef);/* vertical */
            ROM_LOAD("a22-15.115", 0x2000, 0x1000, 0x337a0c14);/* horizontal */
            ROM_LOAD("a22-16.116", 0x3000, 0x1000, 0x337a0c14);/* horizontal */
            ROM_END();
        }
    };

    public static GameDriver driver_buggychl = new GameDriver("1984", "buggychl", "buggychl.java", rom_buggychl, null, machine_driver_buggychl, input_ports_buggychl, null, ROT270, "Taito Corporation", "Buggy Challenge", GAME_IMPERFECT_SOUND);
    public static GameDriver driver_buggycht = new GameDriver("1984", "buggycht", "buggychl.java", rom_buggycht, driver_buggychl, machine_driver_buggychl, input_ports_buggychl, null, ROT270, "Taito Corporation (Tecfri license)", "Buggy Challenge (Tecfri)", GAME_IMPERFECT_SOUND);
}
