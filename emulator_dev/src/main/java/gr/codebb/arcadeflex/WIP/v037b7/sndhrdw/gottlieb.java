/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6502.m6502H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;  
import static gr.codebb.arcadeflex.common.libc.cstring.strcmp;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.samples.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.samplesH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.v056.mame.timer.*;
import static gr.codebb.arcadeflex.v056.mame.timerH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.ay8910.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.ay8910H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._2151intf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._2151intfH.*;

public class gottlieb
{
	
        static int score_sample=7;
	static int random_offset=0;
		
	public static WriteHandlerPtr gottlieb_sh_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		
		data &= 0x3f;
	
		if ((data&0x0f) != 0xf) /* interrupt trigered by four low bits (not all 1's) */
		{
			if (Machine.samples != null)
			{
				if (strcmp(Machine.gamedrv.name,"reactor")==0)	/* reactor */
				{
					switch (data ^ 0x3f)
					{
						case 53:
						case 54:
						case 55:
						case 56:
						case 57:
						case 58:
						case 59:
							sample_start(0,(data^0x3f)-53,0);
							break;
						case 31:
							sample_start(0,7,0);
							score_sample=7;
							break;
						case 39:
							score_sample++;
							if (score_sample<20) sample_start(0,score_sample,0);
							break;
					}
				}
				else	/* qbert */
				{
					switch (data ^ 0x3f)
					{
						case 17:
						case 18:
						case 19:
						case 20:
						case 21:
							sample_start(0,((data^0x3f)-17)*8+random_offset,0);
							random_offset= (random_offset+1)&7;
							break;
						case 22:
							sample_start(0,40,0);
							break;
						case 23:
							sample_start(0,41,0);
							break;
						case 28:
							sample_start(0,42,0);
							break;
						case 36:
							sample_start(0,43,0);
							break;
					}
				}
			}
	
			soundlatch_w.handler(offset,data);
	
			switch (cpu_gettotalcpu())
			{
			case 2:
				/* Revision 1 sound board */
				cpu_cause_interrupt(1,M6502_INT_IRQ);
				break;
			case 3:
			case 4:
				/* Revision 2 & 3 sound board */
				cpu_cause_interrupt(cpu_gettotalcpu()-1,M6502_INT_IRQ);
				cpu_cause_interrupt(cpu_gettotalcpu()-2,M6502_INT_IRQ);
				break;
			}
		}
	} };
	
	
	public static void gottlieb_knocker()
	{
		if (Machine.samples != null)
		{
			if (strcmp(Machine.gamedrv.name,"reactor")==0)	/* reactor */
			{
			}
			else	/* qbert */
				sample_start(0,44,0);
		}
	}
	
	/* callback for the timer */
	public static timer_callback gottlieb_nmi_generate = new timer_callback() {
            @Override
            public void handler(int param) {
                cpu_cause_interrupt(1,M6502_INT_NMI);
            }
        };
	
	static String PhonemeTable[] =
	{
	 "EH3","EH2","EH1","PA0","DT" ,"A1" ,"A2" ,"ZH",
	 "AH2","I3" ,"I2" ,"I1" ,"M"  ,"N"  ,"B"  ,"V",
	 "CH" ,"SH" ,"Z"  ,"AW1","NG" ,"AH1","OO1","OO",
	 "L"  ,"K"  ,"J"  ,"H"  ,"G"  ,"F"  ,"D"  ,"S",
	 "A"  ,"AY" ,"Y1" ,"UH3","AH" ,"P"  ,"O"  ,"I",
	 "U"  ,"Y"  ,"T"  ,"R"  ,"E"  ,"W"  ,"AE" ,"AE1",
	 "AW2","UH2","UH1","UH" ,"O2" ,"O1" ,"IU" ,"U1",
	 "THV","TH" ,"ER" ,"EH" ,"E1" ,"AW" ,"PA1","STOP",
	 null
	};
	
	static int[] queue=new int[100];
        static int pos;
        
	public static WriteHandlerPtr gottlieb_speech_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	
		data ^= 255;
	
                logerror("Votrax: intonation %d, phoneme %02x %s\n",data >> 6,data & 0x3f,PhonemeTable[data & 0x3f]);
	
		queue[pos++] = data & 0x3f;
	
		if ((data & 0x3f) == 0x3f)
		{
/*TODO*///	#if 0
/*TODO*///			if (pos > 1)
/*TODO*///			{
/*TODO*///				int i;
/*TODO*///				char buf[200];
/*TODO*///	
/*TODO*///				buf[0] = 0;
/*TODO*///				for (i = 0;i < pos-1;i++)
/*TODO*///				{
/*TODO*///					if (queue[i] == 0x03 || queue[i] == 0x3e) strcat(buf," ");
/*TODO*///					else strcat(buf,PhonemeTable[queue[i]]);
/*TODO*///				}
/*TODO*///	
/*TODO*///				usrintf_showmessage(buf);
/*TODO*///			}
/*TODO*///	#endif
	
			pos = 0;
		}
	
		/* generate a NMI after a while to make the CPU continue to send data */
		timer_set(TIME_IN_USEC(50),0,gottlieb_nmi_generate);
	} };
	
	public static WriteHandlerPtr gottlieb_speech_clock_DAC_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{} };
	
	
	
	    /* partial decoding takes place to minimize chip count in a 6502+6532
	       system, so both page 0 (direct page) and 1 (stack) access the same
	       128-bytes ram,
	       either with the first 128 bytes of the page or the last 128 bytes */
	
	public static UBytePtr riot_ram=new UBytePtr();
	
	public static ReadHandlerPtr riot_ram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    return riot_ram.read(offset&0x7f);
	} };
	
	public static WriteHandlerPtr riot_ram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		riot_ram.write(offset&0x7f, data);
	} };
	
	public static int[] riot_regs=new int[32];
	    /* lazy handling of the 6532's I/O, and no handling of timers at all */
	
	public static ReadHandlerPtr gottlieb_riot_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    switch (offset&0x1f) {
		case 0: /* port A */
			return soundlatch_r.handler(offset) ^ 0xff;	/* invert command */
		case 2: /* port B */
			return 0x40;    /* say that PB6 is 1 (test SW1 not pressed) */
		case 5: /* interrupt register */
			return 0x40;    /* say that edge detected on PA7 */
		default:
			return riot_regs[offset&0x1f];
	    }
	} };
	
	public static WriteHandlerPtr gottlieb_riot_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	    riot_regs[offset&0x1f]=data;
	} };
	
	
	
	
	static int psg_latch;
	static timer_entry nmi_timer;
	static int nmi_rate;
	static int ym2151_port;
	
	public static void gottlieb_sound_init()
	{
		nmi_timer = null;
	}
	
	public static ReadHandlerPtr stooges_sound_input_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		/* bits 0-3 are probably unused (future expansion) */
	
		/* bits 4 & 5 are two dip switches. Unused? */
	
		/* bit 6 is the test switch. When 0, the CPU plays a pulsing tone. */
	
		/* bit 7 comes from the speech chip DATA REQUEST pin */
	
		return 0xc0;
	} };
	
	public static WriteHandlerPtr stooges_8910_latch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		psg_latch = data;
	} };
	
	/* callback for the timer */
	public static timer_callback nmi_callback = new timer_callback() { public void handler(int param) 
	{
		cpu_cause_interrupt(cpu_gettotalcpu()-1, M6502_INT_NMI);
	} };
	
	public static WriteHandlerPtr common_sound_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* Bit 0 enables and starts NMI timer */
	
		if (nmi_timer != null)
		{
			timer_remove(nmi_timer);
			nmi_timer = null;
		}
	
		if ((data & 0x01) != 0)
		{
			/* base clock is 250kHz divided by 256 */
			double interval = TIME_IN_HZ(250000.0/256/(256-nmi_rate));
			nmi_timer = timer_pulse(interval, 0, nmi_callback);
		}
	
		/* Bit 1 controls a LED on the sound board. I'm not emulating it */
	} };
        
        static int last;
	
	public static WriteHandlerPtr stooges_sound_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		
		common_sound_control_w.handler(offset, data);
	
		/* bit 2 goes to 8913 BDIR pin  */
		if ((last & 0x04) == 0x04 && (data & 0x04) == 0x00)
		{
			/* bit 3 selects which of the two 8913 to enable */
			if ((data & 0x08) != 0)
			{
				/* bit 4 goes to the 8913 BC1 pin */
				if ((data & 0x10) != 0)
					AY8910_control_port_0_w.handler(0,psg_latch);
				else
					AY8910_write_port_0_w.handler(0,psg_latch);
			}
			else
			{
				/* bit 4 goes to the 8913 BC1 pin */
				if ((data & 0x10) != 0)
					AY8910_control_port_1_w.handler(0,psg_latch);
				else
					AY8910_write_port_1_w.handler(0,psg_latch);
			}
		}
	
		/* bit 5 goes to the speech chip DIRECT DATA TEST pin */
	
		/* bit 6 = speech chip DATA PRESENT pin; high then low to make the chip read data */
		if ((last & 0x40) == 0x40 && (data & 0x40) == 0x00)
		{
		}
	
		/* bit 7 goes to the speech chip RESET pin */
	
		last = data & 0x44;
	} };
	
	public static WriteHandlerPtr exterm_sound_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		common_sound_control_w.handler(offset, data);
	
		/* Bit 7 selects YM2151 register or data port */
		ym2151_port = data & 0x80;
	} };
	
	public static WriteHandlerPtr gottlieb_nmi_rate_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		nmi_rate = data;
	} };
	
	public static timer_callback cause_dac_nmi_callback = new timer_callback() { public void handler(int param) 
	{
		cpu_cause_interrupt(cpu_gettotalcpu()-2, M6502_INT_NMI);
	} };
	
	public static WriteHandlerPtr gottlieb_cause_dac_nmi_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* make all the CPUs synchronize, and only AFTER that cause the NMI */
		timer_set(TIME_NOW,0,cause_dac_nmi_callback);
	} };
	
	public static ReadHandlerPtr gottlieb_cause_dac_nmi_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    gottlieb_cause_dac_nmi_w.handler(offset, 0);
		return 0;
	} };
	
	public static WriteHandlerPtr exterm_ym2151_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (ym2151_port != 0)
		{
			YM2151_data_port_0_w.handler(offset, data);
		}
		else
		{
			YM2151_register_port_0_w.handler(offset, data);
		}
	} };
}
