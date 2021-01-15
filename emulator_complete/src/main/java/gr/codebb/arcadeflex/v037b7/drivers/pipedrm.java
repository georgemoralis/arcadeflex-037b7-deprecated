/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.drivers;

import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.mame.palette.*;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.sound.mixerH.*;
import static gr.codebb.arcadeflex.sound._2151intfH.*;
import static gr.codebb.arcadeflex.sound._2608intf.*;
import static gr.codebb.arcadeflex.sound._2608intfH.*;
import static gr.codebb.arcadeflex.sound._2610intf.*;
import static gr.codebb.arcadeflex.sound._2610intfH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.sndintrfH.*;
import static gr.codebb.arcadeflex.v056.mame.timer.*;
import static gr.codebb.arcadeflex.v056.mame.timerH.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.v03b7.vidhrdw.pipedrm.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;

public class pipedrm {

    public static /*UINT8*/ int u8_pipedrm_video_control;

    static /*UINT8*/ int u8_pending_command;
    static /*UINT8*/ int u8_sound_command;

    /**
     * ***********************************
     *
     * Initialization & bankswitching
     *
     ************************************
     */
    public static InitMachinePtr init_machine = new InitMachinePtr() {
        public void handler() {
            UBytePtr ram;

            ram = memory_region(REGION_CPU1);
            cpu_setbank(1, new UBytePtr(ram, 0x10000));

            ram = memory_region(REGION_CPU2);
            cpu_setbank(2, new UBytePtr(ram, 0x10000));
        }
    };

    public static WriteHandlerPtr pipedrm_bankswitch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /*
			Bit layout:
	
			D7 = unknown
			D6 = flip screen
			D5 = background 2 X scroll MSB
			D4 = background 1 X scroll MSB
			D3 = background videoram select
			D2-D0 = program ROM bank select
             */
            UBytePtr ram = memory_region(REGION_CPU1);

            /* set the memory bank on the Z80 using the low 3 bits */
            cpu_setbank(1, new UBytePtr(ram, 0x10000 + (data & 0x7) * 0x2000));

            /* save this globally because the remaining bits affect the video */
            u8_pipedrm_video_control = data & 0xFF;
        }
    };

    public static WriteHandlerPtr sound_bankswitch_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            UBytePtr ram = memory_region(REGION_CPU2);
            cpu_setbank(2, new UBytePtr(ram, 0x10000 + (data & 0x01) * 0x8000));
        }
    };

    /**
     * ***********************************
     *
     * Sound CPU I/O
     *
     ************************************
     */
    public static timer_callback delayed_command_w = new timer_callback() {
        public void handler(int data) {
            u8_sound_command = data & 0xff;
            u8_pending_command = 1;

            /* Hatris polls commands *and* listens to the NMI; this causes it to miss */
 /* sound commands. It's possible the NMI isn't really hooked up on the YM2608 */
 /* sound board. */
            if ((data & 0x100) != 0) {
                cpu_set_nmi_line(1, ASSERT_LINE);
            }
        }
    };

    public static WriteHandlerPtr sound_command_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            timer_set(TIME_NOW, data | 0x100, delayed_command_w);
        }
    };

    public static WriteHandlerPtr sound_command_nonmi_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            timer_set(TIME_NOW, data, delayed_command_w);
        }
    };

    public static WriteHandlerPtr pending_command_clear_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            u8_pending_command = 0;
            cpu_set_nmi_line(1, CLEAR_LINE);
        }
    };

    public static ReadHandlerPtr pending_command_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return u8_pending_command;
        }
    };

    public static ReadHandlerPtr sound_command_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return u8_sound_command;
        }
    };

    /**
     * ***********************************
     *
     * Main CPU memory handlers
     *
     ************************************
     */
    static MemoryReadAddress readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x7fff, MRA_ROM),
                new MemoryReadAddress(0x8000, 0x9fff, MRA_RAM),
                new MemoryReadAddress(0xa000, 0xbfff, MRA_BANK1),
                new MemoryReadAddress(0xc000, 0xcbff, paletteram_r),
                new MemoryReadAddress(0xcc00, 0xcfff, MRA_RAM),
                new MemoryReadAddress(0xd000, 0xffff, pipedrm_videoram_r),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x7fff, MWA_ROM),
                new MemoryWriteAddress(0x8000, 0x9fff, MWA_RAM),
                new MemoryWriteAddress(0xa000, 0xbfff, MWA_ROM),
                new MemoryWriteAddress(0xc000, 0xcbff, paletteram_xRRRRRGGGGGBBBBB_w, paletteram),
                new MemoryWriteAddress(0xcc00, 0xcfff, MWA_RAM, spriteram, spriteram_size),
                new MemoryWriteAddress(0xd000, 0xffff, pipedrm_videoram_w, videoram, videoram_size),
                new MemoryWriteAddress(-1) /* end of table */};

    static MemoryReadAddress hatris_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x7fff, MRA_ROM),
                new MemoryReadAddress(0x8000, 0x9fff, MRA_RAM),
                new MemoryReadAddress(0xa000, 0xbfff, MRA_BANK1),
                new MemoryReadAddress(0xc000, 0xcfff, paletteram_r),
                new MemoryReadAddress(0xd000, 0xffff, hatris_videoram_r),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress hatris_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x7fff, MWA_ROM),
                new MemoryWriteAddress(0x8000, 0x9fff, MWA_RAM),
                new MemoryWriteAddress(0xa000, 0xbfff, MWA_ROM),
                new MemoryWriteAddress(0xc000, 0xcfff, paletteram_xRRRRRGGGGGBBBBB_w, paletteram),
                new MemoryWriteAddress(0xd000, 0xffff, hatris_videoram_w, videoram, videoram_size),
                new MemoryWriteAddress(-1) /* end of table */};

    static IOReadPort readport[]
            = {
                new IOReadPort(0x20, 0x20, input_port_0_r),
                new IOReadPort(0x21, 0x21, input_port_1_r),
                new IOReadPort(0x22, 0x22, input_port_2_r),
                new IOReadPort(0x23, 0x23, input_port_3_r),
                new IOReadPort(0x24, 0x24, input_port_4_r),
                new IOReadPort(0x25, 0x25, pending_command_r),
                new IOReadPort(-1) /* end of table */};

    static IOWritePort writeport[]
            = {
                new IOWritePort(0x20, 0x20, sound_command_w),
                new IOWritePort(0x21, 0x21, pipedrm_bankswitch_w),
                new IOWritePort(0x22, 0x25, pipedrm_scroll_regs_w),
                new IOWritePort(-1) /* end of table */};

    /**
     * ***********************************
     *
     * Sound CPU memory handlers
     *
     ************************************
     */
    static MemoryReadAddress sound_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x77ff, MRA_ROM),
                new MemoryReadAddress(0x7800, 0x7fff, MRA_RAM),
                new MemoryReadAddress(0x8000, 0xffff, MRA_BANK2),
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress sound_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x77ff, MWA_ROM),
                new MemoryWriteAddress(0x7800, 0x7fff, MWA_RAM),
                new MemoryWriteAddress(0x8000, 0xffff, MWA_ROM),
                new MemoryWriteAddress(-1) /* end of table */};

    static IOReadPort sound_readport[]
            = {
                new IOReadPort(0x16, 0x16, sound_command_r),
                new IOReadPort(0x18, 0x18, YM2610_status_port_0_A_r),
                new IOReadPort(0x1a, 0x1a, YM2610_status_port_0_B_r),
                new IOReadPort(-1) /* end of table */};

    static IOWritePort sound_writeport[]
            = {
                new IOWritePort(0x04, 0x04, sound_bankswitch_w),
                new IOWritePort(0x17, 0x17, pending_command_clear_w),
                new IOWritePort(0x18, 0x18, YM2610_control_port_0_A_w),
                new IOWritePort(0x19, 0x19, YM2610_data_port_0_A_w),
                new IOWritePort(0x1a, 0x1a, YM2610_control_port_0_B_w),
                new IOWritePort(0x1b, 0x1b, YM2610_data_port_0_B_w),
                new IOWritePort(-1) /* end of table */};

    static IOReadPort hatris_sound_readport[]
            = {
                new IOReadPort(0x04, 0x04, sound_command_r),
                new IOReadPort(0x05, 0x05, pending_command_r),
                new IOReadPort(0x08, 0x08, YM2608_status_port_0_A_r),
                new IOReadPort(0x0a, 0x0a, YM2608_status_port_0_B_r),
                new IOReadPort(-1) /* end of table */};

    static IOWritePort hatris_sound_writeport[]
            = {
                new IOWritePort(0x02, 0x02, YM2608_control_port_0_B_w),
                new IOWritePort(0x03, 0x03, YM2608_data_port_0_B_w),
                new IOWritePort(0x05, 0x05, pending_command_clear_w),
                new IOWritePort(0x08, 0x08, YM2608_control_port_0_A_w),
                new IOWritePort(0x09, 0x09, YM2608_data_port_0_A_w),
                new IOWritePort(0x0a, 0x0a, YM2608_control_port_0_B_w),
                new IOWritePort(0x0b, 0x0b, YM2608_data_port_0_B_w),
                new IOWritePort(-1) /* end of table */};

    /**
     * ***********************************
     *
     * Port definitions
     *
     ************************************
     */
    static InputPortPtr input_ports_pipedrm = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* $20 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_UNKNOWN);

            PORT_START();
            /* $21 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_UNKNOWN);

            PORT_START();
            /* $22 */
            PORT_DIPNAME(0x0f, 0x0f, DEF_STR("Coin_A"));
            PORT_DIPSETTING(0x06, DEF_STR("5C_1C"));
            PORT_DIPSETTING(0x07, DEF_STR("4C_1C"));
            PORT_DIPSETTING(0x08, DEF_STR("3C_1C"));
            PORT_DIPSETTING(0x09, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0x04, "6 Coins/4 Credits");
            PORT_DIPSETTING(0x03, DEF_STR("4C_3C"));
            PORT_DIPSETTING(0x0f, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x02, "5 Coins/6 Credits");
            PORT_DIPSETTING(0x01, DEF_STR("4C_5C"));
            PORT_DIPSETTING(0x00, DEF_STR("2C_3C"));
            //	PORT_DIPSETTING(    0x05, DEF_STR( "2C_3C") );
            PORT_DIPSETTING(0x0e, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0x0d, DEF_STR("1C_3C"));
            PORT_DIPSETTING(0x0c, DEF_STR("1C_4C"));
            PORT_DIPSETTING(0x0b, DEF_STR("1C_5C"));
            PORT_DIPSETTING(0x0a, DEF_STR("1C_6C"));
            PORT_DIPNAME(0xf0, 0xf0, DEF_STR("Coin_B"));
            PORT_DIPSETTING(0x60, DEF_STR("5C_1C"));
            PORT_DIPSETTING(0x70, DEF_STR("4C_1C"));
            PORT_DIPSETTING(0x80, DEF_STR("3C_1C"));
            PORT_DIPSETTING(0x90, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0x40, "6 Coins/4 Credits");
            PORT_DIPSETTING(0x30, DEF_STR("4C_3C"));
            PORT_DIPSETTING(0xf0, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x20, "5 Coins/6 Credits");
            PORT_DIPSETTING(0x10, DEF_STR("4C_5C"));
            PORT_DIPSETTING(0x00, DEF_STR("2C_3C"));
            //	PORT_DIPSETTING(    0x50, DEF_STR( "2C_3C") );
            PORT_DIPSETTING(0xe0, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0xd0, DEF_STR("1C_3C"));
            PORT_DIPSETTING(0xc0, DEF_STR("1C_4C"));
            PORT_DIPSETTING(0xb0, DEF_STR("1C_5C"));
            PORT_DIPSETTING(0xa0, DEF_STR("1C_6C"));

            PORT_START();
            /* $23 */
            PORT_DIPNAME(0x03, 0x03, DEF_STR("Difficulty"));
            PORT_DIPSETTING(0x02, "Easy");
            PORT_DIPSETTING(0x03, "Normal");
            PORT_DIPSETTING(0x01, "Hard");
            PORT_DIPSETTING(0x00, "Super");
            PORT_DIPNAME(0x0c, 0x04, DEF_STR("Lives"));
            PORT_DIPSETTING(0x0c, "1");
            PORT_DIPSETTING(0x08, "2");
            PORT_DIPSETTING(0x04, "3");
            PORT_DIPSETTING(0x00, "4");
            PORT_DIPNAME(0x10, 0x10, DEF_STR("Demo_Sounds"));
            PORT_DIPSETTING(0x00, DEF_STR("Off"));
            PORT_DIPSETTING(0x10, DEF_STR("On"));
            PORT_DIPNAME(0x20, 0x20, DEF_STR("Flip_Screen"));
            PORT_DIPSETTING(0x20, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_DIPNAME(0x40, 0x40, "Training Mode");
            PORT_DIPSETTING(0x00, DEF_STR("Off"));
            PORT_DIPSETTING(0x40, DEF_STR("On"));
            PORT_SERVICE(0x80, IP_ACTIVE_LOW);

            PORT_START();
            /* $24 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_COIN1);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_COIN2);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_START1);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_START2);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_UNKNOWN);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_SERVICE1);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_UNKNOWN);
            INPUT_PORTS_END();
        }
    };

    static InputPortPtr input_ports_hatris = new InputPortPtr() {
        public void handler() {
            PORT_START();
            /* $20 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_UNKNOWN);

            PORT_START();
            /* $21 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_UNKNOWN);

            PORT_START();
            /* $22 */
            PORT_DIPNAME(0x0f, 0x00, DEF_STR("Coin_A"));
            PORT_DIPSETTING(0x09, DEF_STR("5C_1C"));
            PORT_DIPSETTING(0x08, DEF_STR("4C_1C"));
            PORT_DIPSETTING(0x07, DEF_STR("3C_1C"));
            PORT_DIPSETTING(0x06, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0x0b, "6 Coins/4 Credits");
            PORT_DIPSETTING(0x0c, DEF_STR("4C_3C"));
            PORT_DIPSETTING(0x00, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0x0d, "5 Coins/6 Credits");
            PORT_DIPSETTING(0x0e, DEF_STR("4C_5C"));
            PORT_DIPSETTING(0x0f, DEF_STR("2C_3C"));
            //	PORT_DIPSETTING(    0x0a, DEF_STR( "2C_3C") );
            PORT_DIPSETTING(0x01, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0x02, DEF_STR("1C_3C"));
            PORT_DIPSETTING(0x03, DEF_STR("1C_4C"));
            PORT_DIPSETTING(0x04, DEF_STR("1C_5C"));
            PORT_DIPSETTING(0x05, DEF_STR("1C_6C"));
            PORT_DIPNAME(0xf0, 0x00, DEF_STR("Coin_B"));
            PORT_DIPSETTING(0x90, DEF_STR("5C_1C"));
            PORT_DIPSETTING(0x80, DEF_STR("4C_1C"));
            PORT_DIPSETTING(0x70, DEF_STR("3C_1C"));
            PORT_DIPSETTING(0x60, DEF_STR("2C_1C"));
            PORT_DIPSETTING(0xb0, "6 Coins/4 Credits");
            PORT_DIPSETTING(0xc0, DEF_STR("4C_3C"));
            PORT_DIPSETTING(0x00, DEF_STR("1C_1C"));
            PORT_DIPSETTING(0xd0, "5 Coins/6 Credits");
            PORT_DIPSETTING(0xe0, DEF_STR("4C_5C"));
            PORT_DIPSETTING(0xf0, DEF_STR("2C_3C"));
            //	PORT_DIPSETTING(    0xa0, DEF_STR( "2C_3C") );
            PORT_DIPSETTING(0x10, DEF_STR("1C_2C"));
            PORT_DIPSETTING(0x20, DEF_STR("1C_3C"));
            PORT_DIPSETTING(0x30, DEF_STR("1C_4C"));
            PORT_DIPSETTING(0x40, DEF_STR("1C_5C"));
            PORT_DIPSETTING(0x50, DEF_STR("1C_6C"));

            PORT_START();
            /* $23 */
            PORT_DIPNAME(0x03, 0x00, "Difficulty 1");
            PORT_DIPSETTING(0x01, "Easy");
            PORT_DIPSETTING(0x00, "Normal");
            PORT_DIPSETTING(0x02, "Hard");
            PORT_DIPSETTING(0x03, "Super");
            PORT_DIPNAME(0x0c, 0x00, "Difficulty 2");
            PORT_DIPSETTING(0x04, "Easy");
            PORT_DIPSETTING(0x00, "Normal");
            PORT_DIPSETTING(0x08, "Hard");
            PORT_DIPSETTING(0x0c, "Super");
            PORT_SERVICE(0x10, IP_ACTIVE_HIGH);
            PORT_DIPNAME(0x20, 0x00, DEF_STR("Flip_Screen"));
            PORT_DIPSETTING(0x00, DEF_STR("Off"));
            PORT_DIPSETTING(0x20, DEF_STR("On"));
            PORT_DIPNAME(0x40, 0x00, DEF_STR("Demo_Sounds"));
            PORT_DIPSETTING(0x40, DEF_STR("Off"));
            PORT_DIPSETTING(0x00, DEF_STR("On"));
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_UNUSED);

            PORT_START();
            /* $24 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_COIN1);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_COIN2);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_START1);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_START2);
            PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_UNUSED);
            PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_UNUSED);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_SERVICE1);
            PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_UNUSED);
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
    static GfxLayout bglayout = new GfxLayout(
            8, 4,
            RGN_FRAC(1, 1),
            4,
            new int[]{0, 1, 2, 3},
            new int[]{4, 0, 12, 8, 20, 16, 28, 24},
            new int[]{0 * 32, 1 * 32, 2 * 32, 3 * 32},
            8 * 16
    );

    static GfxLayout splayout = new GfxLayout(
            16, 16,
            RGN_FRAC(1, 1),
            4,
            new int[]{0, 1, 2, 3},
            new int[]{12, 8, 28, 24, 4, 0, 20, 16, 44, 40, 60, 56, 36, 32, 52, 48},
            new int[]{0 * 64, 1 * 64, 2 * 64, 3 * 64, 4 * 64, 5 * 64, 6 * 64, 7 * 64,
                8 * 64, 9 * 64, 10 * 64, 11 * 64, 12 * 64, 13 * 64, 14 * 64, 15 * 64},
            8 * 128
    );

    static GfxDecodeInfo gfxdecodeinfo[]
            = {
                new GfxDecodeInfo(REGION_GFX1, 0, bglayout, 0, 64),
                new GfxDecodeInfo(REGION_GFX2, 0, bglayout, 0, 64),
                new GfxDecodeInfo(REGION_GFX3, 0, splayout, 1024, 32),
                new GfxDecodeInfo(-1) /* end of array */};

    static GfxDecodeInfo gfxdecodeinfo_hatris[]
            = {
                new GfxDecodeInfo(REGION_GFX1, 0, bglayout, 0, 128),
                new GfxDecodeInfo(REGION_GFX2, 0, bglayout, 0, 128),
                new GfxDecodeInfo(-1) /* end of array */};

    /**
     * ***********************************
     *
     * Sound definitions
     *
     ************************************
     */
    public static WriteYmHandlerPtr irqhandler = new WriteYmHandlerPtr() {
        public void handler(int irq) {
            cpu_set_irq_line(1, 0, irq != 0 ? ASSERT_LINE : CLEAR_LINE);
        }
    };

    static YM2608interface ym2608_interface = new YM2608interface(
            1,
            8000000, /* 8 MHz */
            new int[]{50},
            new ReadHandlerPtr[]{null},
            new ReadHandlerPtr[]{null},
            new WriteHandlerPtr[]{null},
            new WriteHandlerPtr[]{null},
            new WriteYmHandlerPtr[]{irqhandler},
            new int[]{REGION_SOUND1},
            new int[]{YM3012_VOL(50, MIXER_PAN_LEFT, 50, MIXER_PAN_RIGHT)}
    );

    static YM2610interface ym2610_interface = new YM2610interface(
            1,
            8000000, /* 8 MHz */
            new int[]{50},
            new ReadHandlerPtr[]{null},
            new ReadHandlerPtr[]{null},
            new WriteHandlerPtr[]{null},
            new WriteHandlerPtr[]{null},
            new WriteYmHandlerPtr[]{irqhandler},
            new int[]{REGION_SOUND1},
            new int[]{REGION_SOUND2},
            new int[]{YM3012_VOL(50, MIXER_PAN_LEFT, 50, MIXER_PAN_RIGHT)}
    );

    /**
     * ***********************************
     *
     * Machine driver
     *
     ************************************
     */
    static MachineDriver machine_driver_pipedrm = new MachineDriver(
            /* basic machine hardware */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_Z80,
                        12000000 / 2,
                        readmem, writemem, readport, writeport,
                        interrupt, 1
                ),
                new MachineCPU(
                        CPU_Z80 | CPU_AUDIO_CPU,
                        14318000 / 4,
                        sound_readmem, sound_writemem, sound_readport, sound_writeport,
                        ignore_interrupt, 0
                )
            },
            60, DEFAULT_REAL_60HZ_VBLANK_DURATION,
            1,
            init_machine,
            /* video hardware */
            44 * 8, 30 * 8, new rectangle(0 * 8, 44 * 8 - 1, 0 * 8, 30 * 8 - 1),
            gfxdecodeinfo,
            1536, 1536,
            null,
            VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
            null,
            pipedrm_vh_start,
            pipedrm_vh_stop,
            pipedrm_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(SOUND_YM2610, ym2610_interface)
            }
    );

    static MachineDriver machine_driver_hatris = new MachineDriver(
            /* basic machine hardware */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_Z80,
                        12000000 / 2,
                        hatris_readmem, hatris_writemem, readport, writeport,
                        interrupt, 1
                ),
                new MachineCPU(
                        CPU_Z80 | CPU_AUDIO_CPU,
                        14318000 / 4,
                        sound_readmem, sound_writemem, hatris_sound_readport, hatris_sound_writeport,
                        ignore_interrupt, 0
                )
            },
            60, DEFAULT_REAL_60HZ_VBLANK_DURATION,
            1,
            init_machine,
            /* video hardware */
            44 * 8, 30 * 8, new rectangle(0 * 8, 44 * 8 - 1, 0 * 8, 30 * 8 - 1),
            gfxdecodeinfo_hatris,
            2048, 2048,
            null,
            VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
            null,
            pipedrm_vh_start,
            pipedrm_vh_stop,
            hatris_vh_screenrefresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(SOUND_YM2608, ym2608_interface)
            }
    );

    /**
     * ***********************************
     *
     * ROM definitions
     *
     ************************************
     */
    static RomLoadPtr rom_pipedrm = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x20000, REGION_CPU1);
            ROM_LOAD("1", 0x00000, 0x08000, 0xdbfac46b);
            ROM_LOAD("2", 0x10000, 0x10000, 0xb7adb99a);

            ROM_REGION(0x20000, REGION_CPU2);
            ROM_LOAD("4", 0x00000, 0x08000, 0x497fad4c);
            ROM_LOAD("3", 0x10000, 0x10000, 0x4800322a);

            ROM_REGION(0x100000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("s73", 0x000000, 0x80000, 0x63f4e10c);
            ROM_LOAD("s72", 0x080000, 0x80000, 0x4e669e97);

            ROM_REGION(0x100000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("s71", 0x000000, 0x80000, 0x431485ee);
            /* s72 will be copied here */

            ROM_REGION(0x080000, REGION_GFX3 | REGIONFLAG_DISPOSE);
            ROM_LOAD_GFX_EVEN("a30", 0x00000, 0x40000, 0x50bc5e98);
            ROM_LOAD_GFX_ODD("a29", 0x00000, 0x40000, 0xa240a448);

            ROM_REGION(0x80000, REGION_SOUND1);
            ROM_LOAD("g72", 0x00000, 0x80000, 0xdc3d14be);

            ROM_REGION(0x80000, REGION_SOUND2);
            ROM_LOAD("g71", 0x00000, 0x80000, 0x488e2fd1);
            ROM_END();
        }
    };

    static RomLoadPtr rom_hatris = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1);
            ROM_LOAD("2-ic79.bin", 0x00000, 0x08000, 0xbbcaddbf);

            ROM_REGION(0x10000, REGION_CPU2);
            ROM_LOAD("1-ic81.bin", 0x00000, 0x08000, 0xdb25e166);

            ROM_REGION(0x80000, REGION_GFX1 | REGIONFLAG_DISPOSE);
            ROM_LOAD("b0-ic56.bin", 0x00000, 0x20000, 0x34f337a4);
            ROM_LOAD("b1-ic73.bin", 0x40000, 0x08000, 0x6351d0ba);

            ROM_REGION(0x40000, REGION_GFX2 | REGIONFLAG_DISPOSE);
            ROM_LOAD("a0-ic55.bin", 0x00000, 0x20000, 0x7b7bc619);
            ROM_LOAD("a1-ic60.bin", 0x20000, 0x20000, 0xf74d4168);

            ROM_REGION(0x20000, REGION_SOUND1);
            ROM_LOAD("pc-ic53.bin", 0x00000, 0x20000, 0x07147712);
            ROM_END();
        }
    };

    /**
     * ***********************************
     *
     * Driver initialization
     *
     ************************************
     */
    public static InitDriverPtr init_pipedrm = new InitDriverPtr() {
        public void handler() {
            /* copy the shared ROM from GFX1 to GFX2 */
            memcpy(memory_region(REGION_GFX2), 0x80000, memory_region(REGION_GFX1), 0x80000, 0x80000);
        }
    };

    public static InitDriverPtr init_hatris = new InitDriverPtr() {
        public void handler() {
            /* clear out unused ROM regions */
            memset(memory_region(REGION_GFX1), 0x20000, 0, 0x20000);
            memset(memory_region(REGION_GFX1), 0x48000, 0, 0x38000);

            install_port_write_handler(0, 0x20, 0x20, sound_command_nonmi_w);
        }
    };

    /**
     * ***********************************
     *
     * Game drivers
     *
     ************************************
     */
    public static GameDriver driver_pipedrm = new GameDriver("1990", "pipedrm", "pipedrm.java", rom_pipedrm, null, machine_driver_pipedrm, input_ports_pipedrm, init_pipedrm, ROT0, "Video System Co.", "Pipe Dream (Japan)");
    public static GameDriver driver_hatris = new GameDriver("1990", "hatris", "pipedrm.java", rom_hatris, null, machine_driver_hatris, input_ports_hatris, init_hatris, ROT0, "Video System Co.", "Hatris (Japan)");
}
