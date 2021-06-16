/***************************************************************************

88 Games (c) 1988 Konami

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.drivers;

import gr.codebb.arcadeflex.WIP.v037b7.cpu.konami.konami.konami_cpu_setlines_callbackPtr;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.old.mame.usrintrf.usrintf_showmessage;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.konami.konamiH.KONAMI_INT_IRQ;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_CPU1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_CPU2;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_GFX1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_GFX2;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_PROMS;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_SOUND1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.ROM_CONTINUE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.ROM_END;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.ROM_LOAD;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.ROM_REGION;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.cpu_cause_interrupt;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.cpu_get_pc;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.ignore_interrupt;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.watchdog_reset_w;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.ASSERT_LINE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.CLEAR_LINE;
import gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.rectangle;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.input_port_0_r;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.input_port_1_r;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.input_port_2_r;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.input_port_3_r;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.input_port_4_r;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.input_port_5_r;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.DEF_STR;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.INPUT_PORTS_END;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPF_8WAY;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPF_PLAYER1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPF_PLAYER2;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_BUTTON1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_BUTTON2;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_COIN1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_COIN2;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_COIN3;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_JOYSTICK_DOWN;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_JOYSTICK_LEFT;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_JOYSTICK_RIGHT;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_JOYSTICK_UP;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_START1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_START2;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_UNKNOWN;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IPT_UNUSED;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.IP_ACTIVE_LOW;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.PORT_BIT;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.PORT_DIPNAME;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.PORT_DIPSETTING;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.PORT_SERVICE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.PORT_START;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MRA_BANK1;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MRA_RAM;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MRA_ROM;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MWA_RAM;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MWA_ROM;
import gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MemoryReadAddress;
import gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MemoryWriteAddress;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.cpu_setbank;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.soundlatch_r;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.soundlatch_w;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._2151intf.YM2151_data_port_0_w;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._2151intf.YM2151_register_port_0_w;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._2151intf.YM2151_status_port_0_r;
import gr.codebb.arcadeflex.WIP.v037b7.sound._2151intfH.YM2151interface;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._2151intfH.YM3012_VOL;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw._88games.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.konamiic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.common.libc.cstring.memcpy;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.old.mame.common.coin_counter_w;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.sound.mixerH.MIXER_PAN_CENTER;
import static gr.codebb.arcadeflex.old.sound.mixerH.MIXER_PAN_LEFT;
import static gr.codebb.arcadeflex.old.sound.mixerH.MIXER_PAN_RIGHT;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.InitDriverPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.InitMachinePtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.InputPortPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.InterruptPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ReadHandlerPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.RomLoadPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteYmHandlerPtr;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.CPU_AUDIO_CPU;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.CPU_KONAMI;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.CPU_Z80;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.DEFAULT_60HZ_VBLANK_DURATION;
import gr.codebb.arcadeflex.v037b7.mame.driverH.GameDriver;
import gr.codebb.arcadeflex.v037b7.mame.driverH.MachineCPU;
import gr.codebb.arcadeflex.v037b7.mame.driverH.MachineDriver;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.ROT90;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.VIDEO_MODIFIES_PALETTE;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.VIDEO_TYPE_RASTER;
import gr.codebb.arcadeflex.v037b7.mame.sndintrfH.MachineSound;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.SOUND_K007232;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.SOUND_YM2151;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.konami.konami.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.k007232.K007232_bankswitch;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.k007232.K007232_read_port_0_r;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.k007232.K007232_set_volume;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.k007232.K007232_write_port_0_w;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.k007232H.K007232_VOL;
import gr.codebb.arcadeflex.WIP.v037b7.sound.k007232H.K007232_interface;
import gr.codebb.arcadeflex.WIP.v037b7.sound.k007232H.portwritehandlerPtr;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.upd7759.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.upd7759H.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.old.arcadeflex.fileio.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.ROT0;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;

public class _88games
{
	
	
	static UBytePtr ram = new UBytePtr();
	static int videobank;
	
	
	static UBytePtr nvram = new UBytePtr();
	static int[] nvram_size = new int[1];
	
	public static nvramPtr nvram_handler  = new nvramPtr() { public void handler(Object file, int read_or_write) 
	{
		if (read_or_write != 0)
			osd_fwrite(file,nvram,nvram_size[0]);
		else
		{
			if (file != null)
				osd_fread(file,nvram,nvram_size[0]);
			else
				memset(nvram,0,nvram_size[0]);
		}
	} };
	
	
	public static InterruptPtr k88games_interrupt = new InterruptPtr() { public int handler() 
	{
		if (K052109_is_IRQ_enabled() != 0) return interrupt.handler();
		else return ignore_interrupt.handler();
	} };
	
	static int zoomreadroms;
	
	public static ReadHandlerPtr bankedram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (videobank != 0) return ram.read(offset);
		else
		{
			if (zoomreadroms != 0)
				return K051316_rom_0_r.handler(offset);
			else
				return K051316_0_r.handler(offset);
		}
	} };
	
	public static WriteHandlerPtr bankedram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (videobank != 0) ram.write(offset, data);
		else K051316_0_w.handler(offset,data);
	} };
	
	public static WriteHandlerPtr k88games_5f84_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* bits 0/1 coin counters */
		coin_counter_w.handler(0,data & 0x01);
		coin_counter_w.handler(1,data & 0x02);
	
		/* bit 2 enables ROM reading from the 051316 */
		/* also 5fce == 2 read roms, == 3 read ram */
		zoomreadroms = data & 0x04;
	
		if ((data & 0xf8) != 0)
		{
/*TODO*///			char buf[40];
/*TODO*///			sprintf(buf,"5f84 = %02x",data);
/*TODO*///			usrintf_showmessage(buf);
		}
	} };
	
	public static WriteHandlerPtr k88games_sh_irqtrigger_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_cause_interrupt(1,0xff);
	} };
	
        static int cheat = 0;
        
	/* handle fake button for speed cheat */
	public static ReadHandlerPtr cheat_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int res;
		
		int bits[] = { 0xee, 0xff, 0xbb, 0xaa };
	
		res = readinputport(1);
	
		if ((readinputport(0) & 0x08) == 0)
		{
			res |= 0x55;
			res &= bits[cheat];
			cheat = (++cheat)%4;
		}
		return res;
	} };
	
	static int speech_chip;
	static int invalid_code;
	static int total_samples[] = { 0x39, 0x15 };
	
	public static WriteHandlerPtr speech_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int reset = ( ( data >> 1 ) & 1 );
		int start = ( ~data ) & 1;
	
		speech_chip = ( data & 4 )!=0 ? 1 : 0;
	
		UPD7759_reset_w.handler( speech_chip, reset );
	
		if (invalid_code==0)
			UPD7759_start_w.handler( speech_chip, start );
	} };
	
	public static WriteHandlerPtr speech_msg_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		UPD7759_message_w.handler( speech_chip, data );
		invalid_code = (data == total_samples[speech_chip])?1:0;
	} };
	
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_RAM ),	/* banked ROM + palette RAM */
		new MemoryReadAddress( 0x2000, 0x37ff, MRA_RAM ),
		new MemoryReadAddress( 0x3800, 0x3fff, bankedram_r ),
		new MemoryReadAddress( 0x5f94, 0x5f94, input_port_0_r ),
	//	new MemoryReadAddress( 0x5f95, 0x5f95, input_port_1_r ),
		new MemoryReadAddress( 0x5f95, 0x5f95, cheat_r ),	/* P1 IO and handle fake button for cheating */
		new MemoryReadAddress( 0x5f96, 0x5f96, input_port_2_r ),
		new MemoryReadAddress( 0x5f97, 0x5f97, input_port_3_r ),
		new MemoryReadAddress( 0x5f9b, 0x5f9b, input_port_4_r ),
		new MemoryReadAddress( 0x4000, 0x7fff, K052109_051960_r ),
		new MemoryReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x0fff, MWA_RAM ),	/* banked ROM */
		new MemoryWriteAddress( 0x1000, 0x1fff, paletteram_xBBBBBGGGGGRRRRR_swap_w, paletteram ),	/* banked ROM + palette RAM */
		new MemoryWriteAddress( 0x2000, 0x2fff, MWA_RAM ),
		new MemoryWriteAddress( 0x3000, 0x37ff, MWA_RAM, nvram, nvram_size ),
		new MemoryWriteAddress( 0x3800, 0x3fff, bankedram_w, ram ),
		new MemoryWriteAddress( 0x5f84, 0x5f84, k88games_5f84_w ),
		new MemoryWriteAddress( 0x5f88, 0x5f88, watchdog_reset_w ),
		new MemoryWriteAddress( 0x5f8c, 0x5f8c, soundlatch_w ),
		new MemoryWriteAddress( 0x5f90, 0x5f90, k88games_sh_irqtrigger_w ),
		new MemoryWriteAddress( 0x5fc0, 0x5fcf, K051316_ctrl_0_w ),
		new MemoryWriteAddress( 0x4000, 0x7fff, K052109_051960_w ),
		new MemoryWriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	static MemoryReadAddress sound_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new MemoryReadAddress( 0xa000, 0xa000, soundlatch_r ),
		new MemoryReadAddress( 0xc001, 0xc001, YM2151_status_port_0_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress sound_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new MemoryWriteAddress( 0x9000, 0x9000, speech_msg_w ),
		new MemoryWriteAddress( 0xc000, 0xc000, YM2151_register_port_0_w ),
		new MemoryWriteAddress( 0xc001, 0xc001, YM2151_data_port_0_w ),
		new MemoryWriteAddress( 0xe000, 0xe000, speech_control_w ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	
	/***************************************************************************
	
		Input Ports
	
	***************************************************************************/
	
	static InputPortPtr input_ports_88games = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
	//	PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		/* Fake button to press buttons 1 and 3 impossibly fast. Handle via konami_IN1_r */
		PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_CHEAT | IPF_PLAYER1, "Run Like Hell Cheat", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "World Records" );
		PORT_DIPSETTING(    0x20, "Don't Erase" );
		PORT_DIPSETTING(    0x00, "Erase on Reset" );
		PORT_SERVICE( 0x40, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER4 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START4 );
	
		PORT_START(); 
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0xd0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0xb0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "1C_7C") );
	//	PORT_DIPSETTING(    0x00, "Disabled" );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x60, 0x60, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x60, "Easy" );
		PORT_DIPSETTING(    0x40, "Normal" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static YM2151interface ym2151_interface = new YM2151interface
	(
		1,			/* 1 chip */
		3579545,	/* 3.579545 MHz */
		new int[] { YM3012_VOL(75,MIXER_PAN_LEFT,75,MIXER_PAN_RIGHT) },
		new WriteYmHandlerPtr[] { null }
	);
	
	static UPD7759_interface upd7759_interface = new UPD7759_interface
	(
		2,							/* number of chips */
		UPD7759_STANDARD_CLOCK,
		new int[] { 30, 30 },					/* volume */
		new int[] { REGION_SOUND1, REGION_SOUND2 },	/* memory region */
		UPD7759_STANDALONE_MODE,	/* chip mode */
		new irqcallbackPtr[] {null}
	);
	
	public static InitMachinePtr k88games_init_machine = new InitMachinePtr() { public void handler() 
	{
		konami_cpu_setlines_callback = k88games_banking;
		paletteram = new UBytePtr(memory_region(REGION_CPU1), 0x20000);
	} };
	
	static MachineDriver machine_driver_88games = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_KONAMI,
				3000000, /* ? */
				readmem,writemem,null,null,
				k88games_interrupt,1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3579545,
				sound_readmem, sound_writemem,null,null,
				ignore_interrupt,0	/* interrupts are triggered by the main CPU */
			)
		},
		60, DEFAULT_60HZ_VBLANK_DURATION,	/* frames per second, vblank duration */
		1,	/* 1 CPU slice per frame - interleaving is forced when a sound command is written */
		k88games_init_machine,
	
		/* video hardware */
		64*8, 32*8, new rectangle( 13*8, (64-13)*8-1, 2*8, 30*8-1 ),
		null,	/* gfx decoded by konamiic.c */
		2048, 2048,
		null,
	
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
		null,
		k88games_vh_start,
		k88games_vh_stop,
		k88games_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_YM2151,
				ym2151_interface
			),
			new MachineSound(
				SOUND_UPD7759,
				upd7759_interface
			)
		},
	
		nvram_handler
	);
	
	
	
	/***************************************************************************
	
	  Game ROMs
	
	***************************************************************************/
	
	static RomLoadPtr rom_88games = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x21000, REGION_CPU1 );/* code + banked roms + space for banked ram */
	    ROM_LOAD( "861m01.k18", 0x08000, 0x08000, 0x4a4e2959 );
		ROM_LOAD( "861m02.k16", 0x10000, 0x10000, 0xe19f15f6 );
	
		ROM_REGION( 0x10000, REGION_CPU2 );/* Z80 code */
		ROM_LOAD( "861d01.d9", 0x00000, 0x08000, 0x0ff1dec0 );
	
		ROM_REGION( 0x080000, REGION_GFX1 );/* graphics ( dont dispose as the program can read them ) */
		ROM_LOAD_GFX_EVEN( "861a08.a", 0x000000, 0x10000, 0x77a00dd6 );	/* characters */
		ROM_LOAD_GFX_ODD ( "861a08.c", 0x000000, 0x10000, 0xb422edfc );
		ROM_LOAD_GFX_EVEN( "861a08.b", 0x020000, 0x10000, 0x28a8304f );
		ROM_LOAD_GFX_ODD ( "861a08.d", 0x020000, 0x10000, 0xe01a3802 );
		ROM_LOAD_GFX_EVEN( "861a09.a", 0x040000, 0x10000, 0xdf8917b6 );
		ROM_LOAD_GFX_ODD ( "861a09.c", 0x040000, 0x10000, 0xf577b88f );
		ROM_LOAD_GFX_EVEN( "861a09.b", 0x060000, 0x10000, 0x4917158d );
		ROM_LOAD_GFX_ODD ( "861a09.d", 0x060000, 0x10000, 0x2bb3282c );
	
		ROM_REGION( 0x100000, REGION_GFX2 );/* graphics ( dont dispose as the program can read them ) */
		ROM_LOAD_GFX_EVEN( "861a05.a", 0x000000, 0x10000, 0xcedc19d0 );	/* sprites */
		ROM_LOAD_GFX_ODD ( "861a05.e", 0x000000, 0x10000, 0x725af3fc );
		ROM_LOAD_GFX_EVEN( "861a05.b", 0x020000, 0x10000, 0xdb2a8808 );
		ROM_LOAD_GFX_ODD ( "861a05.f", 0x020000, 0x10000, 0x32d830ca );
		ROM_LOAD_GFX_EVEN( "861a05.c", 0x040000, 0x10000, 0xcf03c449 );
		ROM_LOAD_GFX_ODD ( "861a05.g", 0x040000, 0x10000, 0xfd51c4ea );
		ROM_LOAD_GFX_EVEN( "861a05.d", 0x060000, 0x10000, 0x97d78c77 );
		ROM_LOAD_GFX_ODD ( "861a05.h", 0x060000, 0x10000, 0x60d0c8a5 );
		ROM_LOAD_GFX_EVEN( "861a06.a", 0x080000, 0x10000, 0x85e2e30e );
		ROM_LOAD_GFX_ODD ( "861a06.e", 0x080000, 0x10000, 0x6f96651c );
		ROM_LOAD_GFX_EVEN( "861a06.b", 0x0a0000, 0x10000, 0xce17eaf0 );
		ROM_LOAD_GFX_ODD ( "861a06.f", 0x0a0000, 0x10000, 0x88310bf3 );
		ROM_LOAD_GFX_EVEN( "861a06.c", 0x0c0000, 0x10000, 0xa568b34e );
		ROM_LOAD_GFX_ODD ( "861a06.g", 0x0c0000, 0x10000, 0x4a55beb3 );
		ROM_LOAD_GFX_EVEN( "861a06.d", 0x0e0000, 0x10000, 0xbc70ab39 );
		ROM_LOAD_GFX_ODD ( "861a06.h", 0x0e0000, 0x10000, 0xd906b79b );
	
		ROM_REGION( 0x040000, REGION_GFX3 );/* graphics ( dont dispose as the program can read them ) */
		ROM_LOAD( "861a04.a", 0x000000, 0x10000, 0x092a8b15 );/* zoom/rotate */
		ROM_LOAD( "861a04.b", 0x010000, 0x10000, 0x75744b56 );
		ROM_LOAD( "861a04.c", 0x020000, 0x10000, 0xa00021c5 );
		ROM_LOAD( "861a04.d", 0x030000, 0x10000, 0xd208304c );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "861.g3",   0x0000, 0x0100, 0x429785db );/* priority encoder (not used) */
	
		ROM_REGION( 0x20000, REGION_SOUND1 );/* samples for UPD7759 #0 */
		ROM_LOAD( "861a07.a", 0x000000, 0x10000, 0x5d035d69 );
		ROM_LOAD( "861a07.b", 0x010000, 0x10000, 0x6337dd91 );
	
		ROM_REGION( 0x20000, REGION_SOUND2 );/* samples for UPD7759 #1 */
		ROM_LOAD( "861a07.c", 0x000000, 0x10000, 0x5067a38b );
		ROM_LOAD( "861a07.d", 0x010000, 0x10000, 0x86731451 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_konami88 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x21000, REGION_CPU1 );/* code + banked roms + space for banked ram */
		ROM_LOAD( "861.e03", 0x08000, 0x08000, 0x55979bd9 );
		ROM_LOAD( "861.e02", 0x10000, 0x10000, 0x5b7e98a6 );
	
		ROM_REGION( 0x10000, REGION_CPU2 );/* Z80 code */
		ROM_LOAD( "861d01.d9", 0x00000, 0x08000, 0x0ff1dec0 );
	
		ROM_REGION(  0x080000, REGION_GFX1 );/* graphics ( dont dispose as the program can read them ) */
		ROM_LOAD_GFX_EVEN( "861a08.a", 0x000000, 0x10000, 0x77a00dd6 );	/* characters */
		ROM_LOAD_GFX_ODD ( "861a08.c", 0x000000, 0x10000, 0xb422edfc );
		ROM_LOAD_GFX_EVEN( "861a08.b", 0x020000, 0x10000, 0x28a8304f );
		ROM_LOAD_GFX_ODD ( "861a08.d", 0x020000, 0x10000, 0xe01a3802 );
		ROM_LOAD_GFX_EVEN( "861a09.a", 0x040000, 0x10000, 0xdf8917b6 );
		ROM_LOAD_GFX_ODD ( "861a09.c", 0x040000, 0x10000, 0xf577b88f );
		ROM_LOAD_GFX_EVEN( "861a09.b", 0x060000, 0x10000, 0x4917158d );
		ROM_LOAD_GFX_ODD ( "861a09.d", 0x060000, 0x10000, 0x2bb3282c );
	
		ROM_REGION( 0x100000, REGION_GFX2 );/* graphics ( dont dispose as the program can read them ) */
		ROM_LOAD_GFX_EVEN( "861a05.a", 0x000000, 0x10000, 0xcedc19d0 );	/* sprites */
		ROM_LOAD_GFX_ODD ( "861a05.e", 0x000000, 0x10000, 0x725af3fc );
		ROM_LOAD_GFX_EVEN( "861a05.b", 0x020000, 0x10000, 0xdb2a8808 );
		ROM_LOAD_GFX_ODD ( "861a05.f", 0x020000, 0x10000, 0x32d830ca );
		ROM_LOAD_GFX_EVEN( "861a05.c", 0x040000, 0x10000, 0xcf03c449 );
		ROM_LOAD_GFX_ODD ( "861a05.g", 0x040000, 0x10000, 0xfd51c4ea );
		ROM_LOAD_GFX_EVEN( "861a05.d", 0x060000, 0x10000, 0x97d78c77 );
		ROM_LOAD_GFX_ODD ( "861a05.h", 0x060000, 0x10000, 0x60d0c8a5 );
		ROM_LOAD_GFX_EVEN( "861a06.a", 0x080000, 0x10000, 0x85e2e30e );
		ROM_LOAD_GFX_ODD ( "861a06.e", 0x080000, 0x10000, 0x6f96651c );
		ROM_LOAD_GFX_EVEN( "861a06.b", 0x0a0000, 0x10000, 0xce17eaf0 );
		ROM_LOAD_GFX_ODD ( "861a06.f", 0x0a0000, 0x10000, 0x88310bf3 );
		ROM_LOAD_GFX_EVEN( "861a06.c", 0x0c0000, 0x10000, 0xa568b34e );
		ROM_LOAD_GFX_ODD ( "861a06.g", 0x0c0000, 0x10000, 0x4a55beb3 );
		ROM_LOAD_GFX_EVEN( "861a06.d", 0x0e0000, 0x10000, 0xbc70ab39 );
		ROM_LOAD_GFX_ODD ( "861a06.h", 0x0e0000, 0x10000, 0xd906b79b );
	
		ROM_REGION( 0x040000, REGION_GFX3 );/* graphics ( dont dispose as the program can read them ) */
		ROM_LOAD( "861a04.a", 0x000000, 0x10000, 0x092a8b15 );/* zoom/rotate */
		ROM_LOAD( "861a04.b", 0x010000, 0x10000, 0x75744b56 );
		ROM_LOAD( "861a04.c", 0x020000, 0x10000, 0xa00021c5 );
		ROM_LOAD( "861a04.d", 0x030000, 0x10000, 0xd208304c );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "861.g3",   0x0000, 0x0100, 0x429785db );/* priority encoder (not used) */
	
		ROM_REGION( 0x20000, REGION_SOUND1 );/* samples for UPD7759 #0 */
		ROM_LOAD( "861a07.a", 0x000000, 0x10000, 0x5d035d69 );
		ROM_LOAD( "861a07.b", 0x010000, 0x10000, 0x6337dd91 );
	
		ROM_REGION( 0x20000, REGION_SOUND2 );/* samples for UPD7759 #1 */
		ROM_LOAD( "861a07.c", 0x000000, 0x10000, 0x5067a38b );
		ROM_LOAD( "861a07.d", 0x010000, 0x10000, 0x86731451 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_hypsptsp = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x21000, REGION_CPU1 );/* code + banked roms + space for banked ram */
		ROM_LOAD( "861f03.k18", 0x08000, 0x08000, 0x8c61aebd );
		ROM_LOAD( "861f02.k16", 0x10000, 0x10000, 0xd2460c28 );
	
		ROM_REGION( 0x10000, REGION_CPU2 );/* Z80 code */
		ROM_LOAD( "861d01.d9", 0x00000, 0x08000, 0x0ff1dec0 );
	
		ROM_REGION(  0x080000, REGION_GFX1 );/* graphics ( dont dispose as the program can read them ) */
		ROM_LOAD_GFX_EVEN( "861a08.a", 0x000000, 0x10000, 0x77a00dd6 );	/* characters */
		ROM_LOAD_GFX_ODD ( "861a08.c", 0x000000, 0x10000, 0xb422edfc );
		ROM_LOAD_GFX_EVEN( "861a08.b", 0x020000, 0x10000, 0x28a8304f );
		ROM_LOAD_GFX_ODD ( "861a08.d", 0x020000, 0x10000, 0xe01a3802 );
		ROM_LOAD_GFX_EVEN( "861a09.a", 0x040000, 0x10000, 0xdf8917b6 );
		ROM_LOAD_GFX_ODD ( "861a09.c", 0x040000, 0x10000, 0xf577b88f );
		ROM_LOAD_GFX_EVEN( "861a09.b", 0x060000, 0x10000, 0x4917158d );
		ROM_LOAD_GFX_ODD ( "861a09.d", 0x060000, 0x10000, 0x2bb3282c );
	
		ROM_REGION( 0x100000, REGION_GFX2 );/* graphics ( dont dispose as the program can read them ) */
		ROM_LOAD_GFX_EVEN( "861a05.a", 0x000000, 0x10000, 0xcedc19d0 );	/* sprites */
		ROM_LOAD_GFX_ODD ( "861a05.e", 0x000000, 0x10000, 0x725af3fc );
		ROM_LOAD_GFX_EVEN( "861a05.b", 0x020000, 0x10000, 0xdb2a8808 );
		ROM_LOAD_GFX_ODD ( "861a05.f", 0x020000, 0x10000, 0x32d830ca );
		ROM_LOAD_GFX_EVEN( "861a05.c", 0x040000, 0x10000, 0xcf03c449 );
		ROM_LOAD_GFX_ODD ( "861a05.g", 0x040000, 0x10000, 0xfd51c4ea );
		ROM_LOAD_GFX_EVEN( "861a05.d", 0x060000, 0x10000, 0x97d78c77 );
		ROM_LOAD_GFX_ODD ( "861a05.h", 0x060000, 0x10000, 0x60d0c8a5 );
		ROM_LOAD_GFX_EVEN( "861a06.a", 0x080000, 0x10000, 0x85e2e30e );
		ROM_LOAD_GFX_ODD ( "861a06.e", 0x080000, 0x10000, 0x6f96651c );
		ROM_LOAD_GFX_EVEN( "861a06.b", 0x0a0000, 0x10000, 0xce17eaf0 );
		ROM_LOAD_GFX_ODD ( "861a06.f", 0x0a0000, 0x10000, 0x88310bf3 );
		ROM_LOAD_GFX_EVEN( "861a06.c", 0x0c0000, 0x10000, 0xa568b34e );
		ROM_LOAD_GFX_ODD ( "861a06.g", 0x0c0000, 0x10000, 0x4a55beb3 );
		ROM_LOAD_GFX_EVEN( "861a06.d", 0x0e0000, 0x10000, 0xbc70ab39 );
		ROM_LOAD_GFX_ODD ( "861a06.h", 0x0e0000, 0x10000, 0xd906b79b );
	
		ROM_REGION( 0x040000, REGION_GFX3 );/* graphics ( dont dispose as the program can read them ) */
		ROM_LOAD( "861a04.a", 0x000000, 0x10000, 0x092a8b15 );/* zoom/rotate */
		ROM_LOAD( "861a04.b", 0x010000, 0x10000, 0x75744b56 );
		ROM_LOAD( "861a04.c", 0x020000, 0x10000, 0xa00021c5 );
		ROM_LOAD( "861a04.d", 0x030000, 0x10000, 0xd208304c );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "861.g3",   0x0000, 0x0100, 0x429785db );/* priority encoder (not used) */
	
		ROM_REGION( 0x20000, REGION_SOUND1 );/* samples for UPD7759 #0 */
		ROM_LOAD( "861a07.a", 0x000000, 0x10000, 0x5d035d69 );
		ROM_LOAD( "861a07.b", 0x010000, 0x10000, 0x6337dd91 );
	
		ROM_REGION( 0x20000, REGION_SOUND2 );/* samples for UPD7759 #1 */
		ROM_LOAD( "861a07.c", 0x000000, 0x10000, 0x5067a38b );
		ROM_LOAD( "861a07.d", 0x010000, 0x10000, 0x86731451 );
	ROM_END(); }}; 
	
	
	
	public static konami_cpu_setlines_callbackPtr k88games_banking = new konami_cpu_setlines_callbackPtr() { public void handler(int lines) 
	{
		UBytePtr RAM = memory_region(REGION_CPU1);
		int offs;
	
	logerror("%04x: bank select %02x\n",cpu_get_pc(),lines);
	
		/* bits 0-2 select ROM bank for 0000-1fff */
		/* bit 3: when 1, palette RAM at 1000-1fff */
		/* bit 4: when 0, 051316 RAM at 3800-3fff; when 1, work RAM at 2000-3fff (NVRAM 3370-37ff) */
		offs = 0x10000 + (lines & 0x07) * 0x2000;
		memcpy(RAM,new UBytePtr(RAM, offs),0x1000);
		if ((lines & 0x08) != 0)
		{
			if (paletteram != new UBytePtr(RAM, 0x1000))
			{
				memcpy(new UBytePtr(RAM, 0x1000),paletteram,0x1000);
				paletteram = new UBytePtr(RAM, 0x1000);
			}
		}
		else
		{
			if (paletteram != new UBytePtr(RAM, 0x20000))
			{
				memcpy(new UBytePtr(RAM, 0x20000),paletteram,0x1000);
				paletteram = new UBytePtr(RAM, 0x20000);
			}
			memcpy(new UBytePtr(RAM, 0x1000),new UBytePtr(RAM, offs+0x1000),0x1000);
		}
		videobank = lines & 0x10;
	
		/* bit 5 = enable char ROM reading through the video RAM */
		K052109_set_RMRD_line((lines & 0x20)!=0 ? ASSERT_LINE : CLEAR_LINE);
	
		/* bit 6 is unknown, 1 most of the time */
	
		/* bit 7 controls layer priority */
		k88games_priority = lines & 0x80;
	} };
	
	
	
	public static InitDriverPtr init_88games = new InitDriverPtr() { public void handler() 
	{
		konami_rom_deinterleave_2(REGION_GFX1);
		konami_rom_deinterleave_2(REGION_GFX2);
	} };
	
	
	
	public static GameDriver driver_88games	   = new GameDriver("1988"	,"88games"	,"_88games.java"	,rom_88games,null	,machine_driver_88games	,input_ports_88games	,init_88games	,ROT0	,	"Konami", "'88 Games" );
	public static GameDriver driver_konami88	   = new GameDriver("1988"	,"konami88"	,"_88games.java"	,rom_konami88,driver_88games	,machine_driver_88games	,input_ports_88games	,init_88games	,ROT0	,	"Konami", "Konami '88" );
	public static GameDriver driver_hypsptsp	   = new GameDriver("1988"	,"hypsptsp"	,"_88games.java"	,rom_hypsptsp,driver_88games	,machine_driver_88games	,input_ports_88games	,init_88games	,ROT0	,	"Konami", "Hyper Sports Special (Japan)" );
}
