/*
 * ported to v0.37b7
 */
package gr.codebb.arcadeflex.v037b7.drivers;

import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.sndhrdw.pleiads.*;
import static gr.codebb.arcadeflex.v058.sound.tms36xxH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.v037b7.vidhrdw.naughtyb.*;

public class naughtyb {

    static MemoryReadAddress readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x3fff, MRA_ROM),
                new MemoryReadAddress(0x4000, 0x8fff, MRA_RAM),
                new MemoryReadAddress(0xb000, 0xb7ff, input_port_0_r), /* IN0 */
                new MemoryReadAddress(0xb800, 0xbfff, input_port_1_r), /* DSW */
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x3fff, MWA_ROM),
                new MemoryWriteAddress(0x4000, 0x7fff, MWA_RAM),
                new MemoryWriteAddress(0x8000, 0x87ff, videoram_w, videoram, videoram_size),
                new MemoryWriteAddress(0x8800, 0x8fff, naughtyb_videoram2_w, naughtyb_videoram2),
                new MemoryWriteAddress(0x9000, 0x97ff, naughtyb_videoreg_w),
                new MemoryWriteAddress(0x9800, 0x9fff, MWA_RAM, naughtyb_scrollreg),
                new MemoryWriteAddress(0xa000, 0xa7ff, pleiads_sound_control_a_w),
                new MemoryWriteAddress(0xa800, 0xafff, pleiads_sound_control_b_w),
                new MemoryWriteAddress(-1) /* end of table */};

    static MemoryWriteAddress popflame_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x3fff, MWA_ROM),
                new MemoryWriteAddress(0x4000, 0x7fff, MWA_RAM),
                new MemoryWriteAddress(0x8000, 0x87ff, videoram_w, videoram, videoram_size),
                new MemoryWriteAddress(0x8800, 0x8fff, naughtyb_videoram2_w, naughtyb_videoram2),
                new MemoryWriteAddress(0x9000, 0x97ff, popflame_videoreg_w),
                new MemoryWriteAddress(0x9800, 0x9fff, MWA_RAM, naughtyb_scrollreg),
                new MemoryWriteAddress(0xa000, 0xa7ff, pleiads_sound_control_a_w),
                new MemoryWriteAddress(0xa800, 0xafff, pleiads_sound_control_b_w),
                new MemoryWriteAddress(-1) /* end of table */};

    /**
     * *************************************************************************
     *
     * Naughty Boy doesn't have VBlank interrupts. Interrupts are still used by
     * the game: but they are related to coin slots.
     *
     **************************************************************************
     */
    public static InterruptPtr naughtyb_interrupt = new InterruptPtr() {
        public int handler() {
            if ((readinputport(2) & 1) != 0) {
                return nmi_interrupt.handler();
            } else {
                return ignore_interrupt.handler();
            }
        }
    };

    static InputPortPtr input_ports_naughtyb = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* IN0 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_START1);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_START2);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_BUTTON1);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY);

            PORT_START();
            /* DSW0 & VBLANK */
            PORT_DIPNAME(0x03, 0x01, DEF_STR("Lives"));
            PORT_DIPSETTING(0x00, "2");
            PORT_DIPSETTING(0x01, "3");
            PORT_DIPSETTING(0x02, "4");
            PORT_DIPSETTING(0x03, "5");
            PORT_DIPNAME(0x0c, 0x04, DEF_STR("Bonus_Life"));
            PORT_DIPSETTING(0x00, "10000");
            PORT_DIPSETTING(0x04, "30000");
            PORT_DIPSETTING(0x08, "50000");
            PORT_DIPSETTING(0x0c, "70000");
            PORT_DIPNAME(0x30, 0x10, DEF_STR("Coinage"));
            PORT_DIPSETTING(0x00, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0x10, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x20, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0x30, DEF_STR("1C_3C"));
            PORT_DIPNAME(0x40, 0x00, DEF_STR("Difficulty"));
            PORT_DIPSETTING(0x00, "Easy");
            PORT_DIPSETTING(0x40, "Hard");
            /* This is a bit of a mystery. Bit 0x80 is read as the vblank, but
		   it apparently also controls cocktail/table mode. */
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_VBLANK);

            PORT_START();
            /* FAKE */
 /* The coin slots are not memory mapped. */
 /* This fake input port is used by the interrupt */
 /* handler to be notified of coin insertions. We use IMPULSE to */
 /* trigger exactly one interrupt, without having to check when the */
 /* user releases the key. */
            PORT_BIT_IMPULSE(0x01, IP_ACTIVE_HIGH, IPT_COIN1, 1);
            INPUT_PORTS_END();
        }
    };

    static GfxLayout charlayout = new GfxLayout(
            8, 8, /* 8*8 characters */
            512, /* 512 characters */
            2, /* 2 bits per pixel */
            new int[]{512 * 8 * 8, 0}, /* the two bitplanes are separated */
            new int[]{7, 6, 5, 4, 3, 2, 1, 0}, /* pretty straightforward layout */
            new int[]{0 * 8, 1 * 8, 2 * 8, 3 * 8, 4 * 8, 5 * 8, 6 * 8, 7 * 8},
            8 * 8 /* every char takes 8 consecutive bytes */
    );

    static GfxDecodeInfo gfxdecodeinfo[]
            = {
                new GfxDecodeInfo(REGION_GFX1, 0, charlayout, 0, 32),
                new GfxDecodeInfo(REGION_GFX2, 0, charlayout, 32 * 4, 32),
                new GfxDecodeInfo(-1) /* end of array */};

    static CustomSound_interface naughtyb_custom_interface = new CustomSound_interface(
            naughtyb_sh_start,
            pleiads_sh_stop,
            pleiads_sh_update
    );

    static CustomSound_interface popflame_custom_interface = new CustomSound_interface(
            popflame_sh_start,
            pleiads_sh_stop,
            pleiads_sh_update
    );

    static TMS36XXinterface tms3615_interface = new TMS36XXinterface(
            1,
            new int[]{60}, /* mixing level */
            new int[]{TMS3615}, /* TMS36xx subtype */
            new int[]{350}, /* base clock (one octave below A) */
            /*
		 * Decay times of the voices; NOTE: it's unknown if
		 * the the TMS3615 mixes more than one voice internally.
		 * A wav taken from Pop Flamer sounds like there
		 * are at least no 'odd' harmonics (5 1/3' and 2 2/3')
             */
            new double[][]{{0.15, 0.20, 0, 0, 0, 0}}
    );

    static MachineDriver machine_driver_naughtyb = new MachineDriver(
            /* basic machine hardware */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_Z80,
                        1500000, /* 3 MHz ? */
                        readmem, writemem, null, null,
                        naughtyb_interrupt, 1
                )
            },
            60, DEFAULT_REAL_60HZ_VBLANK_DURATION, /* frames per second, vblank duration */
            1, /* single CPU, no need for interleaving */
            null,
            /* video hardware */
            36 * 8, 28 * 8, new rectangle(0 * 8, 36 * 8 - 1, 0 * 8, 28 * 8 - 1),
            gfxdecodeinfo,
            256, 32 * 4 + 32 * 4,
            naughtyb_vh_convert_color_prom,
            VIDEO_TYPE_RASTER,
            null,
            naughtyb_vh_start,
            naughtyb_vh_stop,
            naughtyb_vh_screenrefresh,
            /* sound hardware */
            /* uses the TMS3615NS for sound */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_TMS36XX,
                        tms3615_interface
                ),
                new MachineSound(
                        SOUND_CUSTOM,
                        naughtyb_custom_interface
                )
            }
    );

    /* Exactly the same but for the writemem handler */
    static MachineDriver machine_driver_popflame = new MachineDriver(
            /* basic machine hardware */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_Z80,
                        1500000, /* 3 MHz ? */
                        readmem, popflame_writemem, null, null,
                        naughtyb_interrupt, 1
                )
            },
            60, DEFAULT_REAL_60HZ_VBLANK_DURATION, /* frames per second, vblank duration */
            1, /* single CPU, no need for interleaving */
            null,
            /* video hardware */
            36 * 8, 28 * 8, new rectangle(0 * 8, 36 * 8 - 1, 0 * 8, 28 * 8 - 1),
            gfxdecodeinfo,
            256, 32 * 4 + 32 * 4,
            naughtyb_vh_convert_color_prom,
            VIDEO_TYPE_RASTER,
            null,
            naughtyb_vh_start,
            naughtyb_vh_stop,
            naughtyb_vh_screenrefresh,
            /* sound hardware */
            /* uses the TMS3615NS for sound */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_TMS36XX,
                        tms3615_interface
                ),
                new MachineSound(
                        SOUND_CUSTOM,
                        popflame_custom_interface
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
    static RomLoadPtr rom_naughtyb = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("1.30", 0x0000, 0x0800, 0xf6e1178e);
            ROM_LOAD("2.29", 0x0800, 0x0800, 0xb803eb8c);
            ROM_LOAD("3.28", 0x1000, 0x0800, 0x004d0ba7);
            ROM_LOAD("4.27", 0x1800, 0x0800, 0x3c7bcac6);
            ROM_LOAD("5.26", 0x2000, 0x0800, 0xea80f39b);
            ROM_LOAD("6.25", 0x2800, 0x0800, 0x66d9f942);
            ROM_LOAD("7.24", 0x3000, 0x0800, 0x00caf9be);
            ROM_LOAD("8.23", 0x3800, 0x0800, 0x17c3b6fb);

            ROM_REGION(0x2000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("15.44", 0x0000, 0x0800, 0xd692f9c7);
            ROM_LOAD("16.43", 0x0800, 0x0800, 0xd3ba8b27);
            ROM_LOAD("13.46", 0x1000, 0x0800, 0xc1669cd5);
            ROM_LOAD("14.45", 0x1800, 0x0800, 0xeef2c8e5);

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("11.48", 0x0000, 0x0800, 0x75ec9710);
            ROM_LOAD("12.47", 0x0800, 0x0800, 0xef0706c3);
            ROM_LOAD("9.50", 0x1000, 0x0800, 0x8c8db764);
            ROM_LOAD("10.49", 0x1800, 0x0800, 0xc97c97b9);

            ROM_REGION(0x0200, REGION_PROMS);
            ROM_LOAD("6301-1.63", 0x0000, 0x0100, 0x98ad89a1);/* palette low bits */
            ROM_LOAD("6301-1.64", 0x0100, 0x0100, 0x909107d4);/* palette high bits */
            ROM_END();
        }
    };

    static RomLoadPtr rom_naughtya = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("91", 0x0000, 0x0800, 0x42b14bc7);
            ROM_LOAD("92", 0x0800, 0x0800, 0xa24674b4);
            ROM_LOAD("3.28", 0x1000, 0x0800, 0x004d0ba7);
            ROM_LOAD("4.27", 0x1800, 0x0800, 0x3c7bcac6);
            ROM_LOAD("95", 0x2000, 0x0800, 0xe282f1b8);
            ROM_LOAD("96", 0x2800, 0x0800, 0x61178ff2);
            ROM_LOAD("97", 0x3000, 0x0800, 0x3cafde88);
            ROM_LOAD("8.23", 0x3800, 0x0800, 0x17c3b6fb);

            ROM_REGION(0x2000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("15.44", 0x0000, 0x0800, 0xd692f9c7);
            ROM_LOAD("16.43", 0x0800, 0x0800, 0xd3ba8b27);
            ROM_LOAD("13.46", 0x1000, 0x0800, 0xc1669cd5);
            ROM_LOAD("14.45", 0x1800, 0x0800, 0xeef2c8e5);

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("11.48", 0x0000, 0x0800, 0x75ec9710);
            ROM_LOAD("12.47", 0x0800, 0x0800, 0xef0706c3);
            ROM_LOAD("9.50", 0x1000, 0x0800, 0x8c8db764);
            ROM_LOAD("10.49", 0x1800, 0x0800, 0xc97c97b9);

            ROM_REGION(0x0200, REGION_PROMS);
            ROM_LOAD("6301-1.63", 0x0000, 0x0100, 0x98ad89a1);/* palette low bits */
            ROM_LOAD("6301-1.64", 0x0100, 0x0100, 0x909107d4);/* palette high bits */
            ROM_END();
        }
    };

    static RomLoadPtr rom_naughtyc = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("nb1ic30", 0x0000, 0x0800, 0x3f482fa3);
            ROM_LOAD("nb2ic29", 0x0800, 0x0800, 0x7ddea141);
            ROM_LOAD("nb3ic28", 0x1000, 0x0800, 0x8c72a069);
            ROM_LOAD("nb4ic27", 0x1800, 0x0800, 0x30feae51);
            ROM_LOAD("nb5ic26", 0x2000, 0x0800, 0x05242fd0);
            ROM_LOAD("nb6ic25", 0x2800, 0x0800, 0x7a12ffea);
            ROM_LOAD("nb7ic24", 0x3000, 0x0800, 0x9cc287df);
            ROM_LOAD("nb8ic23", 0x3800, 0x0800, 0x4d84ff2c);

            ROM_REGION(0x2000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("15.44", 0x0000, 0x0800, 0xd692f9c7);
            ROM_LOAD("16.43", 0x0800, 0x0800, 0xd3ba8b27);
            ROM_LOAD("13.46", 0x1000, 0x0800, 0xc1669cd5);
            ROM_LOAD("14.45", 0x1800, 0x0800, 0xeef2c8e5);

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("nb11ic48", 0x0000, 0x0800, 0x23271a13);
            ROM_LOAD("12.47", 0x0800, 0x0800, 0xef0706c3);
            ROM_LOAD("nb9ic50", 0x1000, 0x0800, 0xd6949c27);
            ROM_LOAD("10.49", 0x1800, 0x0800, 0xc97c97b9);

            ROM_REGION(0x0200, REGION_PROMS);
            ROM_LOAD("6301-1.63", 0x0000, 0x0100, 0x98ad89a1);/* palette low bits */
            ROM_LOAD("6301-1.64", 0x0100, 0x0100, 0x909107d4);/* palette high bits */
            ROM_END();
        }
    };

    static RomLoadPtr rom_popflame = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);/* 64k for code */
            ROM_LOAD("ic86.pop", 0x0000, 0x1000, 0x5e32bbdf);
            ROM_LOAD("ic80.pop", 0x1000, 0x1000, 0xb77abf3d);
            ROM_LOAD("ic94.pop", 0x2000, 0x1000, 0x945a3c0f);
            ROM_LOAD("ic100.pop", 0x3000, 0x1000, 0xf9f2343b);

            ROM_REGION(0x2000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("ic13.pop", 0x0000, 0x1000, 0x2367131e);
            ROM_LOAD("ic3.pop", 0x1000, 0x1000, 0xdeed0a8b);

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("ic29.pop", 0x0000, 0x1000, 0x7b54f60f);
            ROM_LOAD("ic38.pop", 0x1000, 0x1000, 0xdd2d9601);

            ROM_REGION(0x0200, REGION_PROMS);
            ROM_LOAD("ic53", 0x0000, 0x0100, 0x6e66057f);/* palette low bits */
            ROM_LOAD("ic54", 0x0100, 0x0100, 0x236bc771);/* palette high bits */
            ROM_END();
        }
    };

    static RomLoadPtr rom_popflama = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);/* 64k for code */
            ROM_LOAD("popflama.30", 0x0000, 0x1000, 0xa9bb0e8a);
            ROM_LOAD("popflama.28", 0x1000, 0x1000, 0xdebe6d03);
            ROM_LOAD("popflama.26", 0x2000, 0x1000, 0x09df0d4d);
            ROM_LOAD("popflama.24", 0x3000, 0x1000, 0xf399d553);

            ROM_REGION(0x2000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("ic13.pop", 0x0000, 0x1000, 0x2367131e);
            ROM_LOAD("ic3.pop", 0x1000, 0x1000, 0xdeed0a8b);

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("ic29.pop", 0x0000, 0x1000, 0x7b54f60f);
            ROM_LOAD("ic38.pop", 0x1000, 0x1000, 0xdd2d9601);

            ROM_REGION(0x0200, REGION_PROMS);
            ROM_LOAD("ic53", 0x0000, 0x0100, 0x6e66057f);/* palette low bits */
            ROM_LOAD("ic54", 0x0100, 0x0100, 0x236bc771);/* palette high bits */
            ROM_END();
        }
    };

    static RomLoadPtr rom_popflamb = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            /* 64k for code */
            ROM_LOAD("ic86.bin", 0x0000, 0x1000, 0x06397a4b);
            ROM_LOAD("ic80.pop", 0x1000, 0x1000, 0xb77abf3d);
            ROM_LOAD("ic94.bin", 0x2000, 0x1000, 0xae5248ae);
            ROM_LOAD("ic100.pop", 0x3000, 0x1000, 0xf9f2343b);

            ROM_REGION(0x2000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("ic13.pop", 0x0000, 0x1000, 0x2367131e);
            ROM_LOAD("ic3.pop", 0x1000, 0x1000, 0xdeed0a8b);

            ROM_REGION(0x2000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("ic29.pop", 0x0000, 0x1000, 0x7b54f60f);
            ROM_LOAD("ic38.pop", 0x1000, 0x1000, 0xdd2d9601);

            ROM_REGION(0x0200, REGION_PROMS);
            ROM_LOAD("ic53", 0x0000, 0x0100, 0x6e66057f);/* palette low bits */
            ROM_LOAD("ic54", 0x0100, 0x0100, 0x236bc771);/* palette high bits */
            ROM_END();
        }
    };

    public static GameDriver driver_naughtyb = new GameDriver("1982", "naughtyb", "naughtyb.java", rom_naughtyb, null, machine_driver_naughtyb, input_ports_naughtyb, null, ROT90, "Jaleco", "Naughty Boy", GAME_NO_COCKTAIL);
    public static GameDriver driver_naughtya = new GameDriver("1982", "naughtya", "naughtyb.java", rom_naughtya, driver_naughtyb, machine_driver_naughtyb, input_ports_naughtyb, null, ROT90, "bootleg", "Naughty Boy (bootleg)", GAME_NO_COCKTAIL);
    public static GameDriver driver_naughtyc = new GameDriver("1982", "naughtyc", "naughtyb.java", rom_naughtyc, driver_naughtyb, machine_driver_naughtyb, input_ports_naughtyb, null, ROT90, "Jaleco (Cinematronics license)", "Naughty Boy (Cinematronics)", GAME_NO_COCKTAIL);
    public static GameDriver driver_popflame = new GameDriver("1982", "popflame", "naughtyb.java", rom_popflame, null, machine_driver_popflame, input_ports_naughtyb, null, ROT90, "Jaleco", "Pop Flamer (set 1)", GAME_NO_COCKTAIL);
    public static GameDriver driver_popflama = new GameDriver("1982", "popflama", "naughtyb.java", rom_popflama, driver_popflame, machine_driver_popflame, input_ports_naughtyb, null, ROT90, "Jaleco", "Pop Flamer (set 2)", GAME_NO_COCKTAIL);
    public static GameDriver driver_popflamb = new GameDriver("1982", "popflamb", "naughtyb.java", rom_popflamb, driver_popflame, machine_driver_popflame, input_ports_naughtyb, null, ROT90, "Jaleco (Stern License)", "Pop Flamer (set 3)", GAME_NO_COCKTAIL);
}
