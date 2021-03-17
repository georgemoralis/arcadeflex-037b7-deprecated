/****************************************************************************
 *																			*
 *	Functions to emulate the TMS34061 video controller						*
 *																			*
 *  Created by Zsolt Vasvari on 5/26/1998.									*
 *																			*
 *  This is far from complete. See the TMS34061 User's Guide available on	*
 *  www.spies.com/arcade													*
 *																			*
 ****************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.tms34061H.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v056.mame.timer.*;
import static gr.codebb.arcadeflex.v056.mame.timerH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;

public class tms34061
{	
	public static final int  REG_HORENDSYNC   = 0;
	public static final int  REG_HORENDBLNK   = 1;
	public static final int  REG_HORSTARTBLNK = 2;
	public static final int  REG_HORTOTAL     = 3;
	public static final int  REG_VERENDSYNC   = 4;
	public static final int  REG_VERENDBLNK   = 5;
	public static final int  REG_VERSTARTBLNK = 6;
	public static final int  REG_VERTOTAL     = 7;
	public static final int  REG_DISPUPDATE   = 8;
	public static final int  REG_DISPSTART    = 9;
	public static final int  REG_VERINT       = 10;
	public static final int  REG_CONTROL1     = 11;
	public static final int  REG_CONTROL2     = 12;
	public static final int  REG_STATUS       = 13;
	public static final int  REG_XYOFFSET     = 14;
	public static final int  REG_XYADDRESS    = 15;
	public static final int  REG_DISPADDRESS  = 16;
	public static final int  REG_VERCOUNTER   = 17;
	
	static int[] regs=new int[REG_VERCOUNTER+1];
	
	public static TMS34061interface intf;
	
	static timer_entry timer;
	
	
	public static int TMS34061_start(TMS34061interface _interface)
	{
		intf = _interface;
	
		// Initialize registers to their default values from the manual
		regs[REG_HORENDSYNC]   = 0x10;
		regs[REG_HORENDBLNK]   = 0x20;
		regs[REG_HORSTARTBLNK] = 0x1f0;
		regs[REG_HORTOTAL]     = 0x200;
		regs[REG_VERENDSYNC]   = 0x04;
		regs[REG_VERENDBLNK]   = 0x10;
		regs[REG_VERSTARTBLNK] = 0xf0;
		regs[REG_VERTOTAL]     = 0x100;
		regs[REG_DISPUPDATE]   = 0x00;
		regs[REG_DISPSTART]    = 0x00;
		regs[REG_VERINT]       = 0x00;
		regs[REG_CONTROL1]     = 0x7000;
		regs[REG_CONTROL2]     = 0x600;
		regs[REG_STATUS]       = 0x00;
		regs[REG_XYOFFSET]     = 0x10;
		regs[REG_XYADDRESS]    = 0x00;
		regs[REG_DISPADDRESS]  = 0x00;
		regs[REG_VERCOUNTER]   = 0x00;
	
		// Start vertical interrupt timer.
		timer = timer_pulse(TIME_IN_HZ (Machine.drv.frames_per_second),
		                    intf.cpu, TMS34061_intcallback);
		return timer==null?1:0;
	}
	
	
	public static void TMS34061_stop()
	{
		timer_remove(timer);
	}
	
	
	public static WriteHandlerPtr TMS34061_register_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int status = 0;		// Unsupported
	
		// Calculate which register and which half we're accessing
		int reg = offset >> 2;
	
		// Set register
		if ((offset & 0x02) != 0)
		{
			// Hi word
			regs[reg] = ((regs[reg] & 0xff) | (data << 8));
		}
		else
		{
			// Lo word
			regs[reg] = ((regs[reg] & 0xff00) | data);
		}
	
		switch (reg)
		{
		case REG_VERINT:
			// Set vertical interrupt timer
			timer_reset(timer, cpu_getscanlinetime(regs[reg]));
	
			// Fall through
	
		case REG_CONTROL1:
		case REG_CONTROL2:
		case REG_XYADDRESS:
			status = 1;		// Ok
			break;
		}
	
		if (status==0)
			logerror("Unsupported TMS34061 write. Reg #%02X=%04X - PC: %04X\n",
					reg, regs[reg], cpu_get_pc());
	} };
	
	static timer_callback TMS34061_intcallback = new timer_callback() {
            @Override
            public void handler(int param) {
                // Reset timer for next frame
	//	timer_reset(timer, cpu_getscanlinetime(regs[REG_VERINT]));
	
		// Get out if vertical interrupts are disabled
	    if ((regs[REG_CONTROL1] & 0x400)==0) return;
	
		regs[REG_STATUS] |= 0x0001;
	
		cpu_cause_interrupt (param, intf.vertical_interrupt.handler());
            }
        };
        
	
	public static ReadHandlerPtr TMS34061_register_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int ret;
	
		int status = 0;		// Unsupported
	
		// Calculate which register and which half we're accessing
		int reg = offset >> 2;
	
		// Get register
		if ((offset & 0x02) != 0)
		{
			// Hi word
			ret = (regs[reg] >> 8);
		}
		else
		{
			// Lo word
			ret = (regs[reg] & 0xff);
		}
	
		switch (reg)
		{
		case REG_STATUS:
			// Need to clear status register now
			regs[reg] = 0;
	
			// Fall through
	
		case REG_CONTROL1:
		case REG_CONTROL2:
		case REG_XYADDRESS:
			status = 1;		// Ok
			break;
		}
	
		if (status==0)
			logerror("Unsupported TMS34061 read.  Reg #%02X      - PC: %04X\n",
					reg, cpu_get_pc());
	
		return ret;
	} };
	
	
	public static void adjust_xyaddress(int offset, int x, int y)
	{
		// This an implementation of table on Page 4-15 of the User's Guide
		switch (offset & 0x06)
		{
		case 0x00:      break;
		case 0x02: x++; break;
		case 0x04: x--; break;
		case 0x06: x=0; break;
		}
	
		switch (offset & 0x18)
		{
		case 0x00:      break;
		case 0x08: y++; break;
		case 0x10: y--; break;
		case 0x18: y=0; break;
		}
	
		// Currently only implements when the X-Y addresses are 8 bits each
		// Case #7 on Page 4-18 in User's Guide
	
		regs[REG_XYADDRESS] = ((y & 0xff) << 8) | (x & 0xff);
	}
	
	public static WriteHandlerPtr TMS34061_xypixel_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		// Currently only implements when the X-Y addresses are 8 bits each
		// Case #7 on Page 4-18 in User's Guide
	
		int x = regs[REG_XYADDRESS] & 0xff;
		int y = regs[REG_XYADDRESS] >> 8;
	
	    intf.setpixel.handler(x, y, data);
	
		if (offset != 0) adjust_xyaddress(offset, x, y);
	} };
	
	
	public static ReadHandlerPtr TMS34061_xypixel_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		// Currently only implements when the X-Y addresses are 8 bits each
		// Case #7 on Page 4-18 in User's Guide
	
		int x = regs[REG_XYADDRESS] & 0xff;
		int y = regs[REG_XYADDRESS] >> 8;
	
	    int ret = intf.getpixel.handler(x, y);
	
		if (offset != 0) adjust_xyaddress(offset, x, y);
	
		return ret;
	} };
	
	
	public static WriteHandlerPtr TMS34061_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int col = intf.getcoladdress.handler(offset);
		int row = intf.getrowaddress.handler(offset);
	
		/* Get function code and call appropriate handler */
		int func = intf.getfunction.handler(offset);
		switch (func)
		{
		case 0:
		case 2:  /* Register access */
			TMS34061_register_w.handler(col, data);
			break;
	
		case 1:  /* XY access. Note: col is really the address adjustment function.
				    The real col and row comes from the XY address registers */
			TMS34061_xypixel_w.handler(col, data);
			break;
	
		case 3:  /* Direct access */
			intf.setpixel.handler(col, row, data);
			break;
	
		default:
			logerror("Unsupported TMS34061 function %d - PC: %04X\n",
					func, cpu_get_pc());
		}
	} };
	
	public static ReadHandlerPtr TMS34061_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int ret = 0;
	
		int col = intf.getcoladdress.handler(offset);
		int row = intf.getrowaddress.handler(offset);
	
		/* Get function code and call appropriate handler */
		int func = intf.getfunction.handler(offset);
		switch (func)
		{
		case 0:
		case 2:  /* Register access */
			ret = TMS34061_register_r.handler(col);
			break;
	
		case 1:  /* XY access. Note: col is really the address adjustment code.
				    The real col and row comes from the XY address registers */
			ret = TMS34061_xypixel_r.handler(col);
			break;
	
		case 3:  /* Direct access */
			ret = intf.getpixel.handler(col, row);
			break;
	
		default:
			logerror("Unsupported TMS34061 function %d - PC: %04X\n",
					func, cpu_get_pc());
		}
	
		return ret;
	} };
	
	
	public static int TMS34061_display_blanked()
	{
		return ((regs[REG_CONTROL2] & 0x2000)!=0?0:1);
	}
	
}
